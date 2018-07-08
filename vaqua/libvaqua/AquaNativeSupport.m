/*
 * @(#)AquaNativeSupport.m
 *
 * Copyright (c) 2004-2007 Werner Randelshofer, Switzerland.
 * Copyright (c) 2014-2018 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this software, except in
 * accordance with the license agreement you entered into with
 * Werner Randelshofer. For details see accompanying license terms.
 */

/**
 * Native code support for the VAqua look and feel.
 *
 * @version $Id$
 */

static int VERSION = 3;

#include <stdio.h>
#include "jni.h"
#include "org_violetlib_aqua_fc_OSXFile.h"
#include "org_violetlib_aqua_OSXSystemProperties.h"
#include "org_violetlib_aqua_AquaNativeSupport.h"
#include "org_violetlib_aqua_AquaIcon.h"
#include "org_violetlib_aqua_AquaImageFactory.h"
#include "org_violetlib_aqua_AquaNativeColorChooser.h"
#include "org_violetlib_aqua_AquaUtils.h"
#include "org_violetlib_aqua_AquaSheetSupport.h"
#include "org_violetlib_aqua_AquaVibrantSupport.h"

#import <Cocoa/Cocoa.h>
#import <CoreServices/CoreServices.h>
#import <CoreFoundation/CoreFoundation.h>
#import <CoreGraphics/CoreGraphics.h>
#import <JavaNativeFoundation.h>
#import <Availability.h>

#import "AquaSidebarBackground.h"
#import "AquaWrappedAWTView.h"

@interface AWTWindow
@end

// Not sure if this works, but try to ensure that patched classes are loaded before the patch.

@interface CMenuBar
@end

@interface CMenuItem
@end

static JavaVM *vm;
static jint javaVersion;
static jobject synchronizeCallback;

NSString *createIndentation(int indent)
{
    return [@"                                   " substringToIndex: indent];
}

NSString *createLayerDescription(CALayer *layer)
{
    if (layer) {
        NSString *description = [layer debugDescription];
        NSRect frame = layer.frame;
        CGColorRef bcc = layer.backgroundColor;
        NSString *cd = bcc ? [NSString stringWithFormat: @" %@", (NSColor*) bcc] : @"";
        return [NSString stringWithFormat: @" %@%@ %f %f %f %f", description, cd,
            frame.origin.x, frame.origin.y, frame.size.width, frame.size.height];
    } else {
        return @"";
    }
}

NSString *createViewDescription(NSView *v)
{
    if (v) {
        NSString *description = [v debugDescription];
        if ([v isKindOfClass: [NSVisualEffectView class]]) {
            NSVisualEffectView *vv = (NSVisualEffectView*) v;
            description = [NSString stringWithFormat: @"%@ state=%ld", description, (long) vv.state];
        }
        return description;
    } else {
        return @"";
    }
}

void viewDebug(NSView *v, NSString *title, int indent)
{
    NSString *titleString = title ? [NSString stringWithFormat: @"%@: ", title] : @"";
    NSString *layerDescription = createLayerDescription(v.layer);
    NSString *od = v.opaque ? @" Opaque" : @"";
    NSString *viewDescription = createViewDescription(v);

    NSLog(@"%@%@%@%@ %f %f %f %f %@",
        createIndentation(indent),
        titleString, viewDescription, od, v.frame.origin.x, v.frame.origin.y, v.bounds.size.width, v.bounds.size.height,
        layerDescription);

//    if (v.layer) {
//        if (v.layer.superlayer) {
//            NSLog(@"%@superlayer: %@",
//                createIndentation(indent+2), createLayerDescription(v.layer.superlayer));
//        }
//        if (v.layer.sublayers) {
//            for (CALayer *sl in v.layer.sublayers) {
//                NSLog(@"%@sublayer: %@",
//                    createIndentation(indent+2), createLayerDescription(sl));
//            }
//        }
//    }

    for (NSView *sv in v.subviews) {
        viewDebug(sv, @"", indent+2);
    }
}

NSView *getTopView(NSWindow *w)
{
    NSView *view = w.contentView;
    while (view != nil) {
        NSView *parent = view.superview;
        if (parent == nil) {
            return view;
        }
        view = parent;
    }
    return nil;
}

void windowDebug(NSWindow *w)
{
    NSString *od = w.opaque ? @" Opaque" : @"";
    NSRect frame = w.frame;
    NSLog(@"Window: %@%@ %f %f %f %f", [w description], od, frame.origin.x, frame.origin.y, frame.size.width, frame.size.height);

    NSAppearance *appearance = w.appearance;
    if (appearance) {
        NSLog(@"  Appearance: %@", [appearance name]);
    }
    appearance = w.effectiveAppearance;
    if (appearance) {
        NSLog(@"  Effective appearance: %@", [appearance name]);
    }

    NSView *v = getTopView(w);
    if (v != nil) {
        viewDebug(v, @"", 2);
    }
}

void setupLayers(NSView *v)
{
    NSView *vv = v;
    while (vv) {
        vv.wantsLayer = YES;
        vv = vv.superview;
    }
}

// Ensure that our wrapper view is installed as the parent of the AWT view.

AquaWrappedAWTView *ensureWrapper(NSWindow *w)
{
    NSView *contentView = w.contentView;
    if ([contentView isKindOfClass: [AquaWrappedAWTView class]]) {
        return (AquaWrappedAWTView *) contentView;
    }

    float cornerRadius = -1;
    {
        CALayer *layer = [contentView layer];
        if (layer != nil) {
            cornerRadius = [layer cornerRadius];
        }
    }

    NSView *view = [contentView retain];
    AquaWrappedAWTView *wrapper = [[AquaWrappedAWTView alloc] initWithFrame: view.frame];
    wrapper.wantsLayer = YES;

    w.contentView = wrapper;

    if (cornerRadius > 0) {
        CALayer *layer = [wrapper layer];
        if (layer != nil) {
            [layer setCornerRadius: cornerRadius];
        }
    }

    [wrapper installAWTView: view];
    [contentView release];
    return wrapper;
}

AquaWrappedAWTView *getWrapper(NSWindow *w)
{
    NSView *contentView = w.contentView;
    if ([contentView isKindOfClass: [AquaWrappedAWTView class]]) {
        return (AquaWrappedAWTView *) contentView;
    }
    return nil;
}

NSView *getAWTView(NSWindow *w)
{
    NSView *contentView = w.contentView;
    if ([contentView isKindOfClass: [AquaWrappedAWTView class]]) {
        AquaWrappedAWTView *wrapper = (AquaWrappedAWTView *) contentView;
        return [wrapper awtView];
    }
    return contentView;
}

@interface MyDefaultResponder : NSObject
- (void)defaultsChanged:(NSNotification *)notification;
@end
@implementation MyDefaultResponder
- (void)defaultsChanged:(NSNotification *)notification {
    //NSLog(@"Notification received: %@", [notification name]);

    NSUserDefaults* defaults = [NSUserDefaults standardUserDefaults];
    [defaults synchronize];

    JNIEnv *env;
    jboolean attached = NO;
    int status = (*vm)->GetEnv(vm, (void **) &env, JNI_VERSION_1_6);
    if (status == JNI_EDETACHED) {
        status = (*vm)->AttachCurrentThread(vm, (void **) &env, 0);
        if (status == JNI_OK) {
            attached = YES;
        } else {
            NSLog(@"Unable to attach thread %d", status);
        }
    }

    if (status == JNI_OK) {
        if (synchronizeCallback != NULL) {
            jclass cl = (*env)->GetObjectClass(env, synchronizeCallback);
            jmethodID m = (*env)->GetMethodID(env, cl, "run", "()V");
            if (m != NULL) {
                (*env)->CallVoidMethod(env, synchronizeCallback, m);
            } else {
                NSLog(@"Unable to invoke callback -- run method not found");
            }
        }
    } else {
        NSLog(@"Unable to invoke notification callback %d", status);
    }

    if (attached) {
        (*vm)->DetachCurrentThread(vm);
    }
}
@end

/*
 * Class:     org_violetlib_aqua_OSXSystemProperties
 * Method:    nativeGetFullKeyboardAccessEnabled
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_org_violetlib_aqua_OSXSystemProperties_nativeGetFullKeyboardAccessEnabled
    (JNIEnv *env, jclass cl)
{
    jboolean result = NO;

    JNF_COCOA_ENTER(env);

    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    NSInteger value = [userDefaults integerForKey: @"AppleKeyboardUIMode"];
    result = (value & 02) != 0;

    JNF_COCOA_EXIT(env);

    return result;
}

/*
 * Class:     org_violetlib_aqua_OSXSystemProperties
 * Method:    nativeGetShowAllFiles
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_org_violetlib_aqua_OSXSystemProperties_nativeGetShowAllFiles
    (JNIEnv *env, jclass cl)
{
    jboolean result = NO;

    JNF_COCOA_ENTER(env);

    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    [userDefaults addSuiteNamed: @"com.apple.finder" ];
    result = [userDefaults boolForKey:@"AppleShowAllFiles"];

    //NSLog(@"Show all files: %d", result);

    JNF_COCOA_EXIT(env);

    return result;
}

/*
 * Class:     org_violetlib_aqua_OSXSystemProperties
 * Method:    nativeGetScrollToClick
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_org_violetlib_aqua_OSXSystemProperties_nativeGetScrollToClick
    (JNIEnv *env, jclass cl)
{
    jboolean result = NO;

    JNF_COCOA_ENTER(env);

    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    result = [userDefaults boolForKey:@"AppleScrollerPagingBehavior"];

    JNF_COCOA_EXIT(env);

    return result;
}

/*
 * Class:     org_violetlib_aqua_OSXSystemProperties
 * Method:    nativeGetUseOverlayScrollBars
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_org_violetlib_aqua_OSXSystemProperties_nativeGetUseOverlayScrollBars
    (JNIEnv *env, jclass cl)
{
    jboolean result = NO;

    JNF_COCOA_ENTER(env);

    NSScrollerStyle style = [NSScroller preferredScrollerStyle];
    result = style == NSScrollerStyleOverlay;
    //NSLog(@"Use overlay scroll bars: %ld %d", (long) style, result);

    JNF_COCOA_EXIT(env);

    return result;
}

/*
 * Class:     org_violetlib_aqua_OSXSystemProperties
 * Method:    nativeGetReduceTransparency
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_org_violetlib_aqua_OSXSystemProperties_nativeGetReduceTransparency
    (JNIEnv *env, jclass cl)
{
    jboolean result = NO;

    JNF_COCOA_ENTER(env);

    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    result = [userDefaults boolForKey:@"reduceTransparency"];

    JNF_COCOA_EXIT(env);

    return result;
}

/*
 * Class:     org_violetlib_aqua_OSXSystemProperties
 * Method:    enableCallback
 * Signature: (Ljava/lang/Runnable;)V
 */
JNIEXPORT void JNICALL Java_org_violetlib_aqua_OSXSystemProperties_enableCallback
    (JNIEnv *env, jclass cl, jobject jrunnable)
{
    JNF_COCOA_ENTER(env);

    synchronizeCallback = JNFNewGlobalRef(env, jrunnable);

    jint status = (*env)->GetJavaVM(env, &vm);
    if (status == 0) {

        NSString * const KeyboardUIModeDidChangeNotification = @"com.apple.KeyboardUIModeDidChange";
        NSString * const ReduceTransparencyStatusDidChangeNotification = @"AXInterfaceReduceTransparencyStatusDidChange";

        MyDefaultResponder *r = [[MyDefaultResponder alloc] init];
        [r retain];
        NSNotificationCenter *center = [NSNotificationCenter defaultCenter];
        NSDistributedNotificationCenter *dcenter = [NSDistributedNotificationCenter defaultCenter];
        [center addObserver:r
                    selector:@selector(defaultsChanged:)
                        name:NSPreferredScrollerStyleDidChangeNotification
                      object:nil];
        [dcenter addObserver:r
                    selector:@selector(defaultsChanged:)
                        name:KeyboardUIModeDidChangeNotification  // use nil to see all notifications
                      object:nil];
        [dcenter addObserver:r
                    selector:@selector(defaultsChanged:)
                        name:ReduceTransparencyStatusDidChangeNotification
                      object:nil];
        //NSLog(@"Observer registered");
    }

    JNF_COCOA_EXIT(env);
}

/*
 * Class:     org_violetlib_aqua_fc_OSXFile
 * Method:    getFileType
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_org_violetlib_aqua_fc_OSXFile_nativeGetFileType
    (JNIEnv *env, jclass instance, jstring pathJ)
{
    // Assert arguments
    if (pathJ == NULL) return false;

    jint result = -1;

    // Allocate a memory pool
    NSAutoreleasePool* pool = [NSAutoreleasePool new];

    // Convert Java String to NS String
    const jchar *pathC = (*env)->GetStringChars(env, pathJ, NULL);
    NSString *pathNS = [NSString stringWithCharacters:(UniChar *)pathC length:(*env)->GetStringLength(env, pathJ)];
    (*env)->ReleaseStringChars(env, pathJ, pathC);

    // Do the API calls
    NSFileManager *fileManagerNS = [NSFileManager defaultManager];
    NSDictionary* d = [fileManagerNS attributesOfItemAtPath:pathNS error:nil];
    if (d != nil) {
        NSString* fileType = [d fileType];
        if (fileType != nil) {
            if ([fileType isEqualToString:NSFileTypeRegular]) {
                result = 0;
            } else if ([fileType isEqualToString:NSFileTypeDirectory]) {
                result = 1;
            } else if ([fileType isEqualToString:NSFileTypeSymbolicLink]) {
                result = 2;
            }
        }
    }

    // Release memory pool
    [pool release];

    // Return the result
    return result;
}

/*
 * Class:     org_violetlib_aqua_fc_OSXFile
 * Method:    resolveAlias
 * Signature: (Ljava/lang/String;Z)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_violetlib_aqua_fc_OSXFile_nativeResolveAlias__Ljava_lang_String_2Z
    (JNIEnv *env, jclass instance, jstring aliasPathJ, jboolean noUI)
{
    // Assert arguments
    if (aliasPathJ == NULL) return false;

    jstring result = NULL;

    // Allocate a memory pool
    NSAutoreleasePool* pool = [NSAutoreleasePool new];

    // Convert Java String to NS String
    const jchar *pathC = (*env)->GetStringChars(env, aliasPathJ, NULL);
    NSString *pathNS = [NSString stringWithCharacters:(UniChar *)pathC length:(*env)->GetStringLength(env, aliasPathJ)];
    (*env)->ReleaseStringChars(env, aliasPathJ, pathC);

    // Do the API calls
    NSString *resultNS = [pathNS stringByResolvingSymlinksInPath];
    if (resultNS != nil) {
        // Convert NSString to jstring
        result = (*env)->NewStringUTF(env, [resultNS UTF8String]);
    }

    // Release memory pool
    [pool release];

    // Return the result
    return result;
}

/*
 * Class:     org_violetlib_aqua_fc_OSXFile
 * Method:    jniResolveAlias
 * Signature: ([BZ)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_violetlib_aqua_fc_OSXFile_nativeResolveAlias___3BZ
    (JNIEnv *env, jclass instance, jbyteArray serializedAlias, jboolean noUI)
{
    // Assert arguments
    if (serializedAlias == NULL) return false;

    CFDataRef dataRef;
    CFDataRef bookmarkDataRef;
    UInt8* serializedAliasBytes; // bytes of serializedAlias
    int length; // length of serializedAlias
    UInt8 resolvedPathC[2048];
    jstring result = NULL;

    length = (*env)->GetArrayLength(env, serializedAlias);
    serializedAliasBytes = (UInt8 *) (*env)->GetByteArrayElements(env, serializedAlias, NULL);
    if (serializedAliasBytes != NULL) {
        dataRef = CFDataCreate(NULL, serializedAliasBytes, length);
        if (dataRef != NULL) {
            bookmarkDataRef = CFURLCreateBookmarkDataFromAliasRecord(NULL, dataRef);
            if (bookmarkDataRef != NULL) {
                CFURLBookmarkResolutionOptions opt = (noUI) ? kCFBookmarkResolutionWithoutUIMask : 0;
                Boolean isStale;
                CFErrorRef error;
                CFURLRef u = CFURLCreateByResolvingBookmarkData(NULL, bookmarkDataRef, opt, NULL, NULL, &isStale, &error);
                if (u != NULL) {
                    Boolean success = CFURLGetFileSystemRepresentation(u, true, resolvedPathC, 2048);
                    if (success) {
                        result = (*env)->NewStringUTF(env, (const char *) resolvedPathC);
                    }
                    CFRelease(u);
                }
                CFRelease(bookmarkDataRef);
            }
            CFRelease(dataRef);
        }
        (*env)->ReleaseByteArrayElements(env, serializedAlias, (jbyte *) serializedAliasBytes, JNI_ABORT);
    }
    return result;
}

/*
 * Class:     org_violetlib_aqua_fc_OSXFile
 * Method:    getLabel
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_org_violetlib_aqua_fc_OSXFile_nativeGetLabel
    (JNIEnv *env, jclass instance, jstring pathJ)
{

    // Assert arguments
    if (pathJ == NULL) return -1;

    // Allocate a memory pool
    NSAutoreleasePool* pool = [NSAutoreleasePool new];

    jint result = -1;

    // Convert Java String to NS String
    const jchar *pathC = (*env)->GetStringChars(env, pathJ, NULL);
    NSString *pathNS = [NSString stringWithCharacters:(UniChar *)pathC length:(*env)->GetStringLength(env, pathJ)];
    (*env)->ReleaseStringChars(env, pathJ, pathC);

    // Do the API calls
    NSURL *u = [NSURL fileURLWithPath:pathNS];
    if (u != nil) {
        CFErrorRef error;
        CFNumberRef fileLabel;
        Boolean success = CFURLCopyResourcePropertyForKey((CFURLRef) u, kCFURLLabelNumberKey, &fileLabel, &error);
        if (success) {
            CFNumberGetValue(fileLabel, kCFNumberSInt32Type, &result);
        }
    }

    // Release memory pool
    [pool release];

    // Return the result
    return result;
}

/*
 * Class:     org_violetlib_aqua_fc_OSXFile
 * Method:    getKindString
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_violetlib_aqua_fc_OSXFile_nativeGetKindString
    (JNIEnv *env, jclass instance, jstring pathJ)
{
    // Assert arguments
    if (pathJ == NULL) return NULL;

    // Allocate a memory pool
    NSAutoreleasePool* pool = [NSAutoreleasePool new];

    jstring result = NULL;

    // Convert Java String to NS String
    const jchar *pathC = (*env)->GetStringChars(env, pathJ, NULL);
    NSString *pathNS = [NSString stringWithCharacters:(UniChar *)pathC length:(*env)->GetStringLength(env, pathJ)];
    (*env)->ReleaseStringChars(env, pathJ, pathC);

    // Do the API calls
    NSURL *u = [NSURL fileURLWithPath:pathNS];
    if (u != nil) {
        CFErrorRef error;
        CFStringRef kind;
        Boolean success = CFURLCopyResourcePropertyForKey((CFURLRef) u, kCFURLLocalizedTypeDescriptionKey, &kind, &error);
        if (success) {
            CFRange range;
            range.location = 0;
            // Note that CFStringGetLength returns the number of UTF-16 characters,
            // which is not necessarily the number of printed/composed characters
            range.length = CFStringGetLength(kind);
            UniChar charBuf[range.length];
            CFStringGetCharacters(kind, range, charBuf);
            result = (*env)->NewString(env, (jchar *)charBuf, (jsize)range.length);
        }
    }

    // Release memory pool
    [pool release];

    // Return the result
    return result;
}

// Render an image into a Java int array
// w and h is the desired image size
// scaleFactor is the scaleFactor of the display for which this rendering is intended
// return NULL if scaleFactor is greater than 1 and the image has only one representation

static jintArray renderImageIntoBufferForDisplay(JNIEnv *env, NSImage *image, jint w, jint h, jfloat scaleFactor)
{
    if (scaleFactor > 1 && [[image representations] count] < 2) {
        return NULL;
    }

    int rw = (int) (w * scaleFactor);
    int rh = (int) (h * scaleFactor);

    jboolean isCopy = JNI_FALSE;
    jintArray jdata = (*env)->NewIntArray(env, rw * rh);
    if (jdata != NULL) {
        void *data = (*env)->GetPrimitiveArrayCritical(env, jdata, &isCopy);
        if (data != nil) {
            CGColorSpaceRef colorspace = CGColorSpaceCreateDeviceRGB();
            CGContextRef cg = CGBitmapContextCreate(data, rw, rh, 8, rw * 4, colorspace, kCGImageAlphaPremultipliedFirst | kCGBitmapByteOrder32Host);
            CGColorSpaceRelease(colorspace);

            // The following method is deprecated in OS 10.10
            // NSGraphicsContext *ng = [NSGraphicsContext graphicsContextWithGraphicsPort:cg flipped:NO];

            NSGraphicsContext *ng = [NSGraphicsContext graphicsContextWithCGContext:cg flipped:NO];

            CGContextRelease(cg);

            //NSLog(@"Rendering image into %dx%d %fx: %@", w, h, scaleFactor, image);

            NSGraphicsContext *old = [[NSGraphicsContext currentContext] retain];
            [NSGraphicsContext setCurrentContext:ng];

            NSAffineTransform *tr = [NSAffineTransform transform];
            [tr scaleBy: scaleFactor];
            NSDictionary *hints = [NSDictionary dictionaryWithObject:tr forKey:NSImageHintCTM];
            NSRect frame = NSMakeRect(0, 0, w, h);
            NSImageRep *rep = [image bestRepresentationForRect:frame context:nil hints:hints];
            NSRect toRect = NSMakeRect(0, 0, rw, rh);

            //NSLog(@"Rendering image into %dx%d %fx using rep: %@", w, h, scaleFactor, rep);

            [rep drawInRect:toRect];

            [NSGraphicsContext setCurrentContext:old];
            [old release];
            (*env)->ReleasePrimitiveArrayCritical(env, jdata, data, 0);
            return jdata;
        }
    }

    return NULL;
}

// Render an image into a Java array
// rw and rh are the actual size of the raster

static jboolean renderImageIntoBuffers(JNIEnv *env, NSImage *image, jobjectArray joutput, jint w, jint h)
{
    //NSLog(@"Render image into buffers: %@", image);

    jboolean result = NO;

    jintArray buffer1 = renderImageIntoBufferForDisplay(env, image, w, h, 1);
    jintArray buffer2 = renderImageIntoBufferForDisplay(env, image, w, h, 2);

    if (buffer1) {
        (*env)->SetObjectArrayElement(env, joutput, 0, buffer1);
        (*env)->SetObjectArrayElement(env, joutput, 1, buffer2);
        result = YES;
    }

    return result;
}

typedef long (*QuickLookRequest)(CFAllocatorRef, CFURLRef, CGSize, CFDictionaryRef);

static NSImage *getFileImage(NSString *path, jboolean isQuickLook, jboolean isIconMode, jint w, jint h)
{
    //NSLog(@"getFileImage %d %@", isQuickLook, path);

    NSImage *result = nil;
    if (isQuickLook) {
        NSURL *fileURL = [NSURL fileURLWithPath:path];
        if (fileURL != nil) {
            // Load the QuickLook bundle
            NSURL *bundleURL = [NSURL fileURLWithPath:@"/System/Library/Frameworks/QuickLook.framework"];
            CFBundleRef cfBundle = CFBundleCreate(kCFAllocatorDefault, (CFURLRef)bundleURL);
            // If we didn't succeed, the framework does not exist.
            if (cfBundle) {
                NSDictionary *dict = [NSDictionary dictionaryWithObject:[NSNumber numberWithBool:isIconMode]
                                                                 forKey:@"IconMode"];
                // Get the thumbnail function pointer
                QuickLookRequest functionRef = CFBundleGetFunctionPointerForName(cfBundle,
                                                                                 CFSTR("QLThumbnailImageCreate"));
                if (functionRef) {
                    CGSize size = CGSizeMake(w, h);
                    CGImageRef ref = (CGImageRef) functionRef(kCFAllocatorDefault,
                                                 (CFURLRef)fileURL,
                                                 size,
                                                 (CFDictionaryRef)dict);

                    if (ref) {
                        result = [[[NSImage alloc] initWithCGImage:ref size:size] autorelease];
                        CFRelease(ref);
                    } else {
                        //NSLog(@"No quick look image found");
                    }
                }
            }
        }
    } else {
        result = [[NSWorkspace sharedWorkspace] iconForFile:path];
    }

    //NSLog(@"getFileImage result %@", result);

    return result;
}

/*
 * Class:     org_violetlib_aqua_fc_OSXFile
 * Method:    nativeRenderFileImage
 * Signature: (Ljava/lang/String;ZZ[[III)Z
 */
JNIEXPORT jboolean JNICALL Java_org_violetlib_aqua_fc_OSXFile_nativeRenderFileImage
    (JNIEnv *env, jclass cl, jstring jpath, jboolean isQuickLook, jboolean isIconMode, jobjectArray output, jint w, jint h)
{
    jboolean result = NO;

    JNF_COCOA_ENTER(env);

    NSString *path = JNFNormalizedNSStringForPath(env, jpath);

        NSImage *image = getFileImage(path, isQuickLook, isIconMode, w, h);
        if (image != nil) {
                result = renderImageIntoBuffers(env, image, output, w, h);
        }

    JNF_COCOA_EXIT(env);

    return result;
}

/*
 * Class:     org_violetlib_aqua_AquaImageFactory
 * Method:    nativeRenderImageFile
 * Signature: (Ljava/lang/String;[[III)Z
 */
JNIEXPORT jboolean JNICALL Java_org_violetlib_aqua_AquaImageFactory_nativeRenderImageFile
    (JNIEnv *env, jclass cl, jstring jpath, jobjectArray buffers, jint w, jint h)
{
    jboolean result = NO;

    JNF_COCOA_ENTER(env);

    NSString *path = JNFNormalizedNSStringForPath(env, jpath);
    NSImage *image = [[NSImage alloc] initWithContentsOfFile:path];

    if (image != nil) {
        result = renderImageIntoBuffers(env, image, buffers, w, h);
    }

    JNF_COCOA_EXIT(env);

    return result;
}

/*
 * Class:     org_violetlib_aqua_AquaImageFactory
 * Method:    getNativeImage
 * Signature: (Ljava/lang/String;II)Ljava/awt/Image;
 */
JNIEXPORT jobject JNICALL Java_org_violetlib_aqua_AquaImageFactory_getNativeImage
    (JNIEnv *env, jclass cl, jstring jname, jint w, jint h)
{
    jobject result = NULL;

    static JNF_CLASS_CACHE(jc_CImage, "sun/lwawt/macosx/CImage");
    static JNF_CLASS_CACHE(jc_Creator, "sun/lwawt/macosx/CImage$Creator");
    static JNF_STATIC_MEMBER_CACHE(jm_getCreator, jc_CImage, "getCreator", "()Lsun/lwawt/macosx/CImage$Creator;");
    static JNF_MEMBER_CACHE(jm_createImage, jc_Creator, "createImageFromName", "(Ljava/lang/String;II)Ljava/awt/Image;");

    JNF_COCOA_ENTER(env);

    jobject creator = JNFCallStaticObjectMethod(env, jm_getCreator);
    if (creator != NULL) {
        result = JNFCallObjectMethod(env, creator, jm_createImage, jname, w, h);
    }

    JNF_COCOA_EXIT(env);

    return result;
}

/*
 * Class:     org_violetlib_aqua_AquaIcon
 * Method:    nativeRenderIcon
 * Signature: (I[[II)Z
 */
JNIEXPORT jboolean JNICALL Java_org_violetlib_aqua_AquaIcon_nativeRenderIcon
    (JNIEnv *env, jclass cl, jint osType, jobjectArray buffers, jint size)
{
    jboolean result = NO;

    JNF_COCOA_ENTER(env);

    NSImage *image = [[NSWorkspace sharedWorkspace] iconForFileType: NSFileTypeForHFSTypeCode(osType)];

    if (image != nil) {
        result = renderImageIntoBuffers(env, image, buffers, size, size);
    }

    JNF_COCOA_EXIT(env);

    return result;
}

// Many deprecated functions but no replacement as of OS X 10.11
#pragma GCC diagnostic push
#pragma GCC diagnostic ignored "-Wdeprecated-declarations"
#pragma GCC warning "Many deprecated functions used here"

/*
 * Class:     org_violetlib_aqua_fc_OSXFile
 * Method:    getBasicItemInfoFlags
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_org_violetlib_aqua_fc_OSXFile_nativeGetBasicItemInfoFlags
    (JNIEnv *env, jclass javaClass, jstring pathJ)
{
    // Assert arguments
    if (pathJ == NULL) return -1;

    // Allocate a memory pool
    NSAutoreleasePool* pool = [NSAutoreleasePool new];

    jint result = 0;

    // Convert Java String to NS String
    const jchar *pathC = (*env)->GetStringChars(env, pathJ, NULL);
    NSString *pathNS = [NSString stringWithCharacters:(UniChar *)pathC length:(*env)->GetStringLength(env, pathJ)];
    (*env)->ReleaseStringChars(env, pathJ, pathC);

    // Do the API calls
    NSURL *u = [NSURL fileURLWithPath:pathNS];
    if (u != nil) {
        OSStatus err;
        LSItemInfoRecord itemInfoRecord;
        err = LSCopyItemInfoForURL((CFURLRef) u, kLSRequestBasicFlagsOnly, &itemInfoRecord);
        if (err == 0) {
            result = itemInfoRecord.flags;
        }
    }

    // Release memory pool
    [pool release];

    // Return the result
    return result;
}

JNIEXPORT jstring JNICALL Java_org_violetlib_aqua_fc_OSXFile_nativeGetDisplayName
    (JNIEnv *env, jclass javaClass, jstring pathJ)
{

    // Assert arguments
    if (pathJ == NULL) return NULL;

    // Allocate a memory pool
    NSAutoreleasePool* pool = [NSAutoreleasePool new];

    // Convert Java String to NS String
    const jchar *pathC = (*env)->GetStringChars(env, pathJ, NULL);
    NSString *pathNS = [NSString stringWithCharacters:(UniChar *)pathC
        length:(*env)->GetStringLength(env, pathJ)];
    (*env)->ReleaseStringChars(env, pathJ, pathC);

    // Do the API calls
    NSFileManager *fileManagerNS = [NSFileManager defaultManager];
    NSString *displayNameNS = [fileManagerNS displayNameAtPath: pathNS];

    // Convert NSString to jstring
    jstring displayNameJ = (*env)->NewStringUTF(env, [displayNameNS UTF8String]);

    // Release memory pool
    [pool release];

    // Return the result
    return displayNameJ;
}

/*
 * Class:     org_violetlib_aqua_fc_OSXFile
 * Method:    nativeGetLastUsedDate
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jlong JNICALL Java_org_violetlib_aqua_fc_OSXFile_nativeGetLastUsedDate
    (JNIEnv *env, jclass javaClass, jstring pathJ)
{

    // Assert arguments
    if (pathJ == NULL) return 0;

    // Allocate a memory pool
    NSAutoreleasePool* pool = [NSAutoreleasePool new];

    // Convert Java String to NS String
    const jchar *pathC = (*env)->GetStringChars(env, pathJ, NULL);
    NSString *pathNS = [NSString stringWithCharacters:(UniChar *)pathC
        length:(*env)->GetStringLength(env, pathJ)];
    (*env)->ReleaseStringChars(env, pathJ, pathC);

    jlong result = 0;

    // Do the API calls
    NSURL *u = [NSURL fileURLWithPath:pathNS];
    if (u != nil) {
        MDItemRef item = MDItemCreateWithURL(NULL, CFBridgingRetain(u));
        if (item != NULL) {
            CFDateRef date = (CFDateRef) MDItemCopyAttribute(item, kMDItemLastUsedDate);
            if (date != NULL) {
                CFAbsoluteTime /* double */ at = CFDateGetAbsoluteTime(date);    /* seconds since Jan 1 2001 */
                long long jtime = (long long) at;
                jtime += (60 * 60 * 24) * (31 * 365 + 8);
                jtime *= 1000;
                result = (jlong) jtime;
            }
        }
    }

    // Release memory pool
    [pool release];

    // Return the result
    return result;
}

/*
 * Class:     org_violetlib_aqua_fc_OSXFile
 * Method:    nativeExecuteSavedSearch
 * Signature: (Ljava/lang/String)[Ljava/lang/String
 */
JNIEXPORT jobjectArray JNICALL Java_org_violetlib_aqua_fc_OSXFile_nativeExecuteSavedSearch
    (JNIEnv *env, jclass javaClass, jstring pathJ)
{
    // Assert arguments
    if (pathJ == NULL) return NULL;

    // Prepare result
    jobjectArray result = NULL;

    // Allocate a memory pool
    NSAutoreleasePool* pool = [NSAutoreleasePool new];

    // Convert Java String to NS String
    const jchar *pathC = (*env)->GetStringChars(env, pathJ, NULL);
    NSString *pathNS = [NSString stringWithCharacters:(UniChar *)pathC
                                               length:(*env)->GetStringLength(env, pathJ)];
    (*env)->ReleaseStringChars(env, pathJ, pathC);

    // Read the saved search file and execute the query synchronously
    NSData *data = [NSData dataWithContentsOfFile:pathNS];
    if (data != nil) {
        NSPropertyListReadOptions readOptions = NSPropertyListImmutable;
        NSError *error;
        NSDictionary *plist = (NSDictionary *)[NSPropertyListSerialization propertyListWithData:data options:readOptions format:NULL error:&error];
        if (plist != nil) {
            NSString *queryString = (NSString *) [plist objectForKey:@"RawQuery"];
            NSDictionary *searchCriteria = (NSDictionary *) [plist objectForKey:@"SearchCriteria"];
            if (queryString != nil && searchCriteria != nil) {
                NSArray *scopeDirectories = (NSArray *) [searchCriteria objectForKey:@"FXScopeArrayOfPaths"];
                if (scopeDirectories != nil) {
                    MDQueryRef query = MDQueryCreate(NULL, CFBridgingRetain(queryString), NULL, NULL);
                    if (query != NULL) {
                        OptionBits scopeOptions = 0;
                        MDQuerySetSearchScope(query, CFBridgingRetain(scopeDirectories), scopeOptions);
                        CFOptionFlags optionFlags = kMDQuerySynchronous;
                        Boolean b = MDQueryExecute(query, optionFlags);
                        if (b) {
                            CFIndex count = MDQueryGetResultCount(query);
                            jclass stringClass = (*env)->FindClass(env, "java/lang/String");
                            result = (*env)->NewObjectArray(env, count, stringClass, NULL);
                            for (CFIndex i = 0; i < count; i++) {
                                MDItemRef item = (MDItemRef) MDQueryGetResultAtIndex(query, i);
                                CFStringRef path = (CFStringRef) MDItemCopyAttribute(item, kMDItemPath);
                                NSString *pathNS = (NSString *) path;
                                jstring pathJ = (*env)->NewStringUTF(env, [pathNS UTF8String]);
                                (*env)->SetObjectArrayElement(env, result, i, pathJ);
                            }
                        }
                    }
                }
            }
        }
    }

    // Release memory pool
    [pool release];

    return result;
}

/*
 * Class:     org_violetlib_aqua_fc_OSXFile
 * Method:    nativeGetSidebarFiles
 * Signature: (I)[Ljava/lang/String;
 */
JNIEXPORT jobjectArray JNICALL Java_org_violetlib_aqua_fc_OSXFile_nativeGetSidebarFiles
    (JNIEnv *env, jclass cl, jint which, jint iconSize, jint lastSeed)
{
    CFStringRef listID = which > 0 ? kLSSharedFileListFavoriteVolumes : kLSSharedFileListFavoriteItems;

    LSSharedFileListRef list = LSSharedFileListCreate(NULL, listID, NULL);
    if (!list) {
        NSLog(@"Failed to create shared file list for %@", listID);
        return NULL;
    }

    UInt32 seed = LSSharedFileListGetSeedValue(list);
    if (seed == lastSeed) {
        CFRelease(list);
        return NULL;
    }

    CFArrayRef items = LSSharedFileListCopySnapshot(list, &seed);
    size_t count = CFArrayGetCount(items);

    jclass objectClass = (*env)->FindClass(env, "java/lang/Object");
    jclass integerClass = (*env)->FindClass(env, "java/lang/Integer");
    jmethodID newIntegerMethodID = (*env)->GetMethodID(env, integerClass, "<init>", "(I)V");

    jobjectArray result = (*env)->NewObjectArray(env, 1 + count * 6, objectClass, NULL);
    size_t j = 0;
    (*env)->SetObjectArrayElement(env, result, j++, (*env)->NewObject(env, integerClass, newIntegerMethodID, seed));

    if (which >= 2) {    // testing
        //NSLog(@"%ld elements for %@", count, list);
    }

    if (count > 0) {
        for (size_t i = 0; i < count; i++) {
            LSSharedFileListItemRef item = (LSSharedFileListItemRef) CFArrayGetValueAtIndex(items, i);
            if (!item) {
                continue;
            }

            // Collect six elements: display name, UID, hidden flag, resolved path, 1x rendering, 2x rendering

            jstring displayNameJ = NULL;

            CFStringRef displayName = LSSharedFileListItemCopyDisplayName(item);
            NSString *displayNameNS = (NSString *) displayName;
            if (displayNameNS) {
                displayNameJ = (*env)->NewStringUTF(env, [displayNameNS UTF8String]);
                CFRelease(displayName);
            }

            UInt32 itemId = LSSharedFileListItemGetID(item);
            jobject itemIdJ = (*env)->NewObject(env, integerClass, newIntegerMethodID, itemId);

            CFTypeRef hiddenProperty = LSSharedFileListItemCopyProperty(item, kLSSharedFileListItemHidden);
            jint hiddenFlag = hiddenProperty && hiddenProperty == kCFBooleanTrue;
            if (hiddenProperty) {
                CFRelease(hiddenProperty);
            }
            jobject flagsJ = (*env)->NewObject(env, integerClass, newIntegerMethodID, hiddenFlag);

            jstring pathJ = NULL;
            CFURLRef outURL = LSSharedFileListItemCopyResolvedURL(item, kLSSharedFileListNoUserInteraction|kLSSharedFileListDoNotMountVolumes, NULL);
            if (outURL) {
                CFStringRef itemPath = CFURLCopyFileSystemPath(outURL, kCFURLPOSIXPathStyle);
                if (itemPath) {
                    NSString *pathNS = (NSString *) itemPath;
                    pathJ = (*env)->NewStringUTF(env, [pathNS UTF8String]);
                    CFRelease(itemPath);
                }
                CFRelease(outURL);
            }

            jobject icon1J = NULL;
            jobject icon2J = NULL;

            if (iconSize > 0) {
                IconRef icon = LSSharedFileListItemCopyIconRef(item);
                if (icon) {
                    NSImage *iconImage = [[NSImage alloc] initWithIconRef:icon];
                    icon1J = renderImageIntoBufferForDisplay(env, iconImage, iconSize, iconSize, 1);
                    icon2J = renderImageIntoBufferForDisplay(env, iconImage, iconSize, iconSize, 2);
                    [iconImage release];
                    CFRelease(icon);
                }
            }

            (*env)->SetObjectArrayElement(env, result, j++, displayNameJ);
            (*env)->SetObjectArrayElement(env, result, j++, itemIdJ);
            (*env)->SetObjectArrayElement(env, result, j++, flagsJ);
            (*env)->SetObjectArrayElement(env, result, j++, pathJ);
            (*env)->SetObjectArrayElement(env, result, j++, icon1J);
            (*env)->SetObjectArrayElement(env, result, j++, icon2J);
        }
    }

    CFRelease(items);
    CFRelease(list);
    return result;
}

#pragma GCC diagnostic pop

static NSColorPanel *colorPanel;
static jobject colorPanelCallback;
static jboolean colorPanelBeingConfigured;

@interface MyColorPanelDelegate : NSObject <NSWindowDelegate> {}
- (void) colorChanged: (id) sender;
@end

@implementation MyColorPanelDelegate

- (void) windowWillClose:(NSNotification *) ns
{
    JNIEnv *env;
    jboolean attached = NO;
    int status = (*vm)->GetEnv(vm, (void **) &env, JNI_VERSION_1_6);
    if (status == JNI_EDETACHED) {
        status = (*vm)->AttachCurrentThread(vm, (void **) &env, 0);
        if (status == JNI_OK) {
            attached = YES;
        } else {
            NSLog(@"Unable to attach thread %d", status);
        }
    }

    if (status == JNI_OK) {
        // Using dynamic lookup because we do not know which class loader was used
        jclass cl = (*env)->GetObjectClass(env, colorPanelCallback);
        jmethodID m = (*env)->GetMethodID(env, cl, "disconnected", "()V");
        if (m != NULL) {
            (*env)->CallVoidMethod(env, colorPanelCallback, m);
        } else {
            NSLog(@"Unable to invoke callback -- disconnected method not found");
        }
    } else {
        NSLog(@"Unable to invoke callback %d", status);
    }

    if (attached) {
        (*vm)->DetachCurrentThread(vm);
    }
}

- (void) colorChanged: (id) sender
{
        if (colorPanelBeingConfigured) {
            return;
        }

    NSColor *color = [colorPanel color];

    JNIEnv *env;
    jboolean attached = NO;
    int status = (*vm)->GetEnv(vm, (void **) &env, JNI_VERSION_1_6);
    if (status == JNI_EDETACHED) {
        status = (*vm)->AttachCurrentThread(vm, (void **) &env, 0);
        if (status == JNI_OK) {
            attached = YES;
        } else {
            NSLog(@"Unable to attach thread %d", status);
        }
    }

    if (status == JNI_OK) {
        static JNF_CLASS_CACHE(jc_Color, "java/awt/Color");
        static JNF_MEMBER_CACHE(jm_createColor, jc_Color, "<init>", "(FFFF)V");
        CGFloat r, g, b, a;
        [color getRed:&r green:&g blue:&b alpha:&a];
        jobject jColor = JNFNewObject(env, jm_createColor, r, g, b, a);
        // Using dynamic lookup because we do not know which class loader was used
        jclass cl = (*env)->GetObjectClass(env, colorPanelCallback);
        jmethodID m = (*env)->GetMethodID(env, cl, "applyColor", "(Ljava/awt/Color;)V");
        if (m != NULL) {
            (*env)->CallVoidMethod(env, colorPanelCallback, m, jColor);
        } else {
            NSLog(@"Unable to invoke callback -- applyColor method not found");
        }
    } else {
        NSLog(@"Unable to invoke callback %d", status);
    }

    if (attached) {
        (*vm)->DetachCurrentThread(vm);
    }
}

@end

static jboolean setupColorPanel()
{
    MyColorPanelDelegate *delegate = [[MyColorPanelDelegate alloc] init];
    colorPanel = [NSColorPanel sharedColorPanel];
    [colorPanel setDelegate: delegate];
    [colorPanel setAction: @selector(colorChanged:)];
    [colorPanel setTarget: delegate];
    [colorPanel setContinuous: YES];
    [colorPanel makeKeyAndOrderFront: nil];
    [colorPanel setReleasedWhenClosed: NO];
    return YES;
}

/*
 * Class:     org_violetlib_aqua_AquaNativeColorChooser
 * Method:    display
 * Signature: (Lorg/violetlib/aqua/AquaSharedColorChooser/Owner;)Z
 */
JNIEXPORT jboolean JNICALL Java_org_violetlib_aqua_AquaNativeColorChooser_create
    (JNIEnv *env, jclass cl, jobject ownerCallback)
{
    colorPanelCallback = JNFNewGlobalRef(env, ownerCallback);

    __block jboolean result = NO;

    JNF_COCOA_ENTER(env);

    if (!vm) {
        (*env)->GetJavaVM(env, &vm);
    }

    void (^block)() = ^(){
        result = setupColorPanel();
    };

    if ([NSThread isMainThread]) {
        block();
    } else {
        [JNFRunLoop performOnMainThreadWaiting:YES withBlock:block];
    }

    JNF_COCOA_EXIT(env);

    return result;
}

/*
 * Class:     org_violetlib_aqua_AquaNativeColorChooser
 * Method:    show
 * Signature: (FFFFZ)V
 */
JNIEXPORT void JNICALL Java_org_violetlib_aqua_AquaNativeColorChooser_show
    (JNIEnv *env, jclass cl, jfloat red, jfloat green, jfloat blue, jfloat alpha, jboolean wantAlpha)
{
        if (colorPanel) {
        JNF_COCOA_ENTER(env);

        void (^block)() = ^(){
                colorPanelBeingConfigured = YES;
                        NSColor *color = [NSColor colorWithSRGBRed:(CGFloat)red
                                                 green:(CGFloat)green
                                                  blue:(CGFloat)blue
                                                 alpha:(CGFloat)alpha];
            colorPanel.showsAlpha = wantAlpha;
            colorPanel.color = color;
            [colorPanel makeKeyAndOrderFront: nil];
            colorPanelBeingConfigured = NO;
        };

        if ([NSThread isMainThread]) {
            block();
        } else {
            [JNFRunLoop performOnMainThreadWaiting:YES withBlock:block];
        }

        JNF_COCOA_EXIT(env);
    }
}

/*
 * Class:     org_violetlib_aqua_AquaNativeColorChooser
 * Method:    hide
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_violetlib_aqua_AquaNativeColorChooser_hide
    (JNIEnv *env, jclass cl)
{
        if (colorPanel) {
        JNF_COCOA_ENTER(env);

        void (^block)() = ^(){
            [colorPanel close];
        };

        if ([NSThread isMainThread]) {
            block();
        } else {
            [JNFRunLoop performOnMainThreadWaiting:YES withBlock:block];
        }

        JNF_COCOA_EXIT(env);
    }
}

static jobject getWindowPeer(JNIEnv *env, jobject w)
{
    static JNF_CLASS_CACHE(jc_Window, "java/awt/Window");
    static JNF_MEMBER_CACHE(jf_peer, jc_Window, "peer", "Ljava/awt/peer/ComponentPeer;");
    return JNFGetObjectField(env, w, jf_peer);
}

static jobject getPlatformWindow(JNIEnv *env, jobject windowPeer)
{
    static JNF_CLASS_CACHE(jc_LWWindowPeer, "sun/lwawt/LWWindowPeer");
    static JNF_MEMBER_CACHE(jm_getPlatformWindow, jc_LWWindowPeer, "getPlatformWindow", "()Lsun/lwawt/PlatformWindow;");
    return windowPeer != NULL ? JNFCallObjectMethod(env, windowPeer, jm_getPlatformWindow) : NULL;
}

/*
 * Class:     org_violetlib_aqua_AquaUtils
 * Method:    nativeGetNativeWindow
 * Signature: (Ljava/awt/Window;[Ljava/lang/Object;)J
 */
JNIEXPORT jlong JNICALL Java_org_violetlib_aqua_AquaUtils_nativeGetNativeWindow
  (JNIEnv *env, jclass cl, jobject w, jobjectArray data)
 {
    static JNF_CLASS_CACHE(jc_CFRetainedResource, "sun/lwawt/macosx/CFRetainedResource");
    static JNF_MEMBER_CACHE(jf_ptr, jc_CFRetainedResource, "ptr", "J");
    static JNF_MEMBER_CACHE(jf_readLock, jc_CFRetainedResource, "readLock", "Ljava/util/concurrent/locks/Lock;");

    jlong result = 0;
    jobject readLock = NULL;

    JNF_COCOA_ENTER(env);

    jobject peer = getWindowPeer(env, w);
    if (peer != NULL) {
        jobject platformWindow = getPlatformWindow(env, peer);
        if (platformWindow != NULL) {
            result = JNFGetLongField(env, platformWindow, jf_ptr);
            if (result != 0) {
                jclass c = (*env)->GetObjectClass(env, platformWindow);
                jfieldID f_readLock = (*env)->GetFieldID(env, c, "readLock", "Ljava/util/concurrent/locks/Lock;");
                if (f_readLock != NULL) {
                    readLock = JNFGetObjectField(env, platformWindow, jf_readLock);
                }
                (*env)->ExceptionClear(env);
            } else {
                NSLog(@"nativeGetNativeWindow: No pointer");
            }
        } else {
            NSLog(@"nativeGetNativeWindow: No platform window");
        }
    } else {
        NSLog(@"nativeGetNativeWindow: No window peer");
    }

    (*env)->SetObjectArrayElement(env, data, 0, readLock);

    JNF_COCOA_EXIT(env);

    return result;
 }

/*
 * Class:     org_violetlib_aqua_AquaUtils
 * Method:    nativeSetTitledWindowStyle
 * Signature: (Ljava/awt/Window;ZILjava/awt/Insets;)V
 */
JNIEXPORT void JNICALL Java_org_violetlib_aqua_AquaUtils_nativeSetTitledWindowStyle
  (JNIEnv *env, jclass cl, jobject w, jboolean isDecorated, jint height, jobject insets)
{
    static JNF_CLASS_CACHE(jc_CPlatformWindow, "sun/lwawt/macosx/CPlatformWindow");
    static JNF_MEMBER_CACHE(jm_setStyleBits, jc_CPlatformWindow, "setStyleBits", "(IZ)V");
    static JNF_CLASS_CACHE(jc_Window, "java/awt/Window");
    static JNF_MEMBER_CACHE(jf_height, jc_Window, "height", "I");
    static JNF_CLASS_CACHE(jc_LWWindowPeer, "sun/lwawt/LWWindowPeer");
    static JNF_MEMBER_CACHE(jm_updateInsets, jc_LWWindowPeer, "updateInsets", "(Ljava/awt/Insets;)Z");

    JNF_COCOA_ENTER(env);

    jobject peer = getWindowPeer(env, w);
    jobject platformWindow = getPlatformWindow(env, peer);
    if (platformWindow == NULL) {
        return;
    }
    int DECORATED = 1 << 1;
    JNFCallVoidMethod(env, platformWindow, jm_setStyleBits, DECORATED, isDecorated);

    // Java eventually will be informed of the new window size and insets, but we need to update now so
    // that the initial painting of the root pane will be positioned correctly.

    JNFSetIntField(env, w, jf_height, height);
    JNFCallBooleanMethod(env, peer, jm_updateInsets, insets);

    JNF_COCOA_EXIT(env);
}

/*
 * Class:     org_violetlib_aqua_AquaUtils
 * Method:    nativeSetWindowTextured
 * Signature: (Ljava/awt/Window;Z)V
 */
JNIEXPORT void JNICALL Java_org_violetlib_aqua_AquaUtils_nativeSetWindowTextured
  (JNIEnv *env, jclass cl, jobject w, jboolean isTextured)
{
    static JNF_CLASS_CACHE(jc_LWWindowPeer, "sun/lwawt/LWWindowPeer");
    static JNF_MEMBER_CACHE(jm_setTextured, jc_LWWindowPeer, "setTextured", "(Z)V");

    JNF_COCOA_ENTER(env);

    jobject peer = getWindowPeer(env, w);
    if (peer != nil) {
        JNFCallVoidMethod(env, peer, jm_setTextured, isTextured);
    }

    JNF_COCOA_EXIT(env);
}

/*
 * Class:     org_violetlib_aqua_AquaUtils
 * Method:    nativeSetWindowBackground
 * Signature: (Ljava/awt/Window;Ljava/awt/Color;)V
 */
JNIEXPORT void JNICALL Java_org_violetlib_aqua_AquaUtils_nativeSetWindowBackground
  (JNIEnv *env, jclass cl, jobject w, jobject color)
{
    static JNF_CLASS_CACHE(jc_LWWindowPeer, "sun/lwawt/LWWindowPeer");
    static JNF_MEMBER_CACHE(jm_setBackground, jc_LWWindowPeer, "setBackground", "(Ljava/awt/Color;)V");
    static JNF_MEMBER_CACHE(jm_setOpaque, jc_LWWindowPeer, "setOpaque", "(Z)V");
    static JNF_CLASS_CACHE(jc_Color, "java/awt/Color");
    static JNF_MEMBER_CACHE(jm_getAlpha, jc_Color, "getAlpha", "()I");

    JNF_COCOA_ENTER(env);

    jobject peer = getWindowPeer(env, w);
    if (peer != nil) {
        JNFCallVoidMethod(env, peer, jm_setBackground, color);
        int alpha = JNFCallIntMethod(env, color, jm_getAlpha);
        JNFCallVoidMethod(env, peer, jm_setOpaque, alpha == 255);
    }

    JNF_COCOA_EXIT(env);
}

/*
 * Class:     org_violetlib_aqua_AquaUtils
 * Method:    nativeIsFullScreenWindow
 * Signature: (J)Z
 */
JNIEXPORT jboolean JNICALL Java_org_violetlib_aqua_AquaUtils_nativeIsFullScreenWindow
  (JNIEnv *env, jclass cl, jlong wptr)
{
    jboolean result = 0;

    JNF_COCOA_ENTER(env);

    NSWindow *w = (NSWindow *) wptr;
    NSUInteger mask = [w styleMask];
    if (mask & NSWindowStyleMaskFullScreen) {
        result = 1;
    }

    JNF_COCOA_EXIT(env);

    return result;
}

static const jint TITLEBAR_NONE = 0;
static const jint TITLEBAR_ORDINARY = 1;
static const jint TITLEBAR_TRANSPARENT = 2;
static const jint TITLEBAR_HIDDEN = 3;
static const jint TITLEBAR_OVERLAY = 4;

/*
 * Class:     org_violetlib_aqua_AquaUtils
 * Method:    nativeSetTitleBarStyle
 * Signature: (JI)I
 */
JNIEXPORT jint JNICALL Java_org_violetlib_aqua_AquaUtils_nativeSetTitleBarStyle
    (JNIEnv *env, jclass cl, jlong wptr, jint style)
{
    // This method uses API introduced in Yosemite

    jint result = -1;

    JNF_COCOA_ENTER(env);

    NSWindow *w = (NSWindow *) wptr;
    if ([w respondsToSelector: @selector(setTitlebarAppearsTransparent:)]) {
        [JNFRunLoop performOnMainThreadWaiting:YES withBlock:^(){

            NSProcessInfo *pi = [NSProcessInfo processInfo];
            NSOperatingSystemVersion osv = [pi operatingSystemVersion];
            BOOL isElCapitan = osv.majorVersion >= 10 && osv.minorVersion >= 11;

            // Because this method is used by component UIs, it updates the same set of properties regardless of the
            // style. It never does a partial update.

            // We need to make the window Movable if we want the user to be able to drag the window from the window
            // title, which we do when the title bar is transparent.

            // On Yosemite, if the window is not Movable, mouse events over the title bar never get to the Java window.
            // If we want some control over title bar mouse events (which we do when the title bar is hidden), we must
            // make the window Movable.

            NSUInteger originalStyleMask = w.styleMask;
            NSUInteger styleMask = originalStyleMask;
            BOOL isTextured = (styleMask & NSWindowStyleMaskTexturedBackground) != 0;
            BOOL isMovable = true;
            BOOL isMovableByBackground = isTextured;
            BOOL isTransparent = NO;
            BOOL isHidden = NO;
            BOOL isFixNeeded = NO;

            switch (style) {
                case TITLEBAR_NONE:
                    styleMask &= ~(NSWindowStyleMaskTitled | NSWindowStyleMaskFullSizeContentView);
                    break;
                case TITLEBAR_ORDINARY:
                default:
                    styleMask |= NSWindowStyleMaskTitled;
                    styleMask &= ~NSWindowStyleMaskFullSizeContentView;
                    break;
                case TITLEBAR_TRANSPARENT:
                    styleMask |= (NSWindowStyleMaskTitled | NSWindowStyleMaskFullSizeContentView);
                    isTransparent = YES;
                    isMovableByBackground = NO;
                    isFixNeeded = YES;
                    break;
                case TITLEBAR_HIDDEN:
                    styleMask |= (NSWindowStyleMaskTitled | NSWindowStyleMaskFullSizeContentView);
                    isTransparent = YES;
                    isHidden = YES;
                    isMovable = !isElCapitan;
                    isMovableByBackground = NO;
                    isFixNeeded = YES;
                    break;
                case TITLEBAR_OVERLAY:
                    styleMask |= (NSWindowStyleMaskTitled | NSWindowStyleMaskFullSizeContentView);
                    isFixNeeded = YES;
                    break;
                }

            [[w standardWindowButton:NSWindowCloseButton] setHidden:isHidden];
            [[w standardWindowButton:NSWindowMiniaturizeButton] setHidden:isHidden];
            [[w standardWindowButton:NSWindowZoomButton] setHidden:isHidden];

            [w setTitlebarAppearsTransparent: isTransparent];
            [w setStyleMask: styleMask];

            [w setMovableByWindowBackground:isMovableByBackground];
            [w setMovable:isMovable];

            if (isFixNeeded) {
                // Workaround for a mysterious problem observed in some circumstances but not others.
                // The corner radius is not set, so painting happens outside the rounded corners.
                NSView *topView = getTopView(w);
                if (topView != nil) {
                    CALayer *layer = [topView layer];
                    if (layer != nil) {
                        CGFloat radius = [layer cornerRadius];
                        if (radius == 0) {
                            //NSLog(@"Fixing corner radius of %@", layer);
                            [layer setCornerRadius: 6];
                        }
                    } else {
                        NSLog(@"Unable to fix corner radius: no layer");
                    }
                } else {
                    NSLog(@"Unable to fix corner radius: did not find top view");
                }
            }

            if (((originalStyleMask ^ styleMask) & NSWindowStyleMaskFullSizeContentView) != 0) {
                // The full size content view option has changed.
                // We need to get Java to recompute the window insets.
                // This should do it...
                if ([w respondsToSelector: @selector(windowDidResize:)]) {
#pragma GCC diagnostic push
#pragma GCC diagnostic ignored "-Wnonnull"
                    [((id)w) windowDidResize:nil];
#pragma GCC diagnostic pop
                }
            }
        }];
        result = 0;
    }

    JNF_COCOA_EXIT(env);

    return result;
}

/*
 * Class:     org_violetlib_aqua_AquaUtils
 * Method:    nativeAddToolbarToWindow
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_violetlib_aqua_AquaUtils_nativeAddToolbarToWindow
    (JNIEnv *env, jclass cl, jlong wptr)
{
    jint result = -1;

    JNF_COCOA_ENTER(env);

    NSWindow *w = (NSWindow *) wptr;
    [JNFRunLoop performOnMainThreadWaiting:YES withBlock:^(){
        NSToolbar *tb = [[NSToolbar alloc] initWithIdentifier: @"Foo"];
        [tb setShowsBaselineSeparator: NO];
        [w setToolbar: tb];
    }];
    result = 0;

    JNF_COCOA_EXIT(env);

    return result;
}

/*
 * Class:     org_violetlib_aqua_AquaSheetSupport
 * Method:    nativeDisplayAsSheet
 * Signature: (JJ)I
 */
JNIEXPORT jint JNICALL Java_org_violetlib_aqua_AquaSheetSupport_nativeDisplayAsSheet
    (JNIEnv *env, jclass cl, jlong wptr, jlong owner_wptr)
{
    static JNF_CLASS_CACHE(jc_Window, "java/awt/Window");
    static JNF_MEMBER_CACHE(jm_getOwner, jc_Window, "getOwner", "()Ljava/awt/Window;");

    jint result = -1;

    JNF_COCOA_ENTER(env);

    NSWindow *w = (NSWindow *) wptr;
    NSWindow *no = (NSWindow *) owner_wptr;

    [JNFRunLoop performOnMainThreadWaiting:NO withBlock:^(){
        // setting NSTitledWindowMask seems necessary for a reliable vibrant background in a native sheet
        // but we install our own NSVisualEffectView, so it is not necessary here
        // NSUInteger styleMask = [w styleMask];
        // [w setStyleMask: styleMask | NSTitledWindowMask ];
        // debug
        [no beginSheet:w completionHandler:nil];
    }];
    result = 0;

    JNF_COCOA_EXIT(env);

    return result;
}

/*
 * Class:     org_violetlib_aqua_AquaUtils
 * Method:    nativeSetWindowVisibleField
 * Signature: (Ljava/awt/Window;Z)V
 */
JNIEXPORT void JNICALL Java_org_violetlib_aqua_AquaUtils_nativeSetWindowVisibleField
    (JNIEnv *env, jclass cl, jobject window, jboolean isVisible)
{
    static JNF_CLASS_CACHE(jc_Window, "java/awt/Window");
    static JNF_MEMBER_CACHE(jf_visible, jc_Window, "visible", "Z");

    JNF_COCOA_ENTER(env);

    JNFSetBooleanField(env, window, jf_visible, isVisible);

    JNF_COCOA_EXIT(env);
}

/*
 * Class:     org_violetlib_aqua_AquaUtils
 * Method:    nativeSetWindowCornerRadius
 * Signature: (JF)I
 */
JNIEXPORT jint JNICALL Java_org_violetlib_aqua_AquaUtils_nativeSetWindowCornerRadius
    (JNIEnv *env, jclass cl, jlong wptr, jfloat radius)
{
    __block jint result = -1;

    JNF_COCOA_ENTER(env);

    NSWindow *w = (NSWindow *) wptr;
    [JNFRunLoop performOnMainThreadWaiting:YES withBlock:^(){
        NSView *topView = getTopView(w);
        if (topView != nil) {
            topView.wantsLayer = YES;
            CALayer *layer = [topView layer];
            if (layer != nil) {
                if (![w hasShadow]) {
                    NSLog(@"Window %@ has no shadow", w);
                }
                [layer setCornerRadius: radius];
                [w invalidateShadow];
                result = 0;
            } else {
                NSLog(@"Unable to set corner radius: no layer");
            }
        } else {
            NSLog(@"Unable to set corner radius: did not find top view");
        }
    }];

    JNF_COCOA_EXIT(env);

    return result;
}

static const int LIGHT_STYLE = 0;
static const int DARK_STYLE = 1;
static const int SIDEBAR_STYLE = 2;
static const int TITLE_BAR_STYLE = 3;
static const int MENU_STYLE = 4;
static const int POPOVER_STYLE = 5;
static const int MEDIUM_LIGHT_STYLE = 6;
static const int ULTRA_DARK_STYLE = 7;
static const int SHEET_STYLE = 8;

static NSAppearance *getVibrantAppearance(jint style)
{
    NSString *name;

    switch (style) {

    case DARK_STYLE:
    case ULTRA_DARK_STYLE:
        name = NSAppearanceNameVibrantDark;
        break;

    default:
        name = NSAppearanceNameVibrantLight;
        break;
    }

    return [NSAppearance appearanceNamed: name];
}

static NSVisualEffectMaterial getVibrantMaterial(jint style)
{
    BOOL isYosemite = floor(NSAppKitVersionNumber) <= NSAppKitVersionNumber10_10_Max;

    switch (style) {

    default:
    case LIGHT_STYLE:
    case SHEET_STYLE:
        return NSVisualEffectMaterialLight;

    case DARK_STYLE:
        return NSVisualEffectMaterialDark;

    case SIDEBAR_STYLE:
        return isYosemite ? NSVisualEffectMaterialLight : NSVisualEffectMaterialSidebar;

    case TITLE_BAR_STYLE:
        return NSVisualEffectMaterialTitlebar;

    case MENU_STYLE:
        return NSVisualEffectMaterialMenu;

    case POPOVER_STYLE:
        return isYosemite ? NSVisualEffectMaterialLight : NSVisualEffectMaterialPopover;

    case MEDIUM_LIGHT_STYLE:
        return isYosemite ? NSVisualEffectMaterialLight : NSVisualEffectMaterialMediumLight;

    case ULTRA_DARK_STYLE:
        return isYosemite ? NSVisualEffectMaterialDark : NSVisualEffectMaterialUltraDark;
    }
}

/*
 * Class:     org_violetlib_aqua_AquaVibrantSupport
 * Method:    setupVisualEffectWindow
 * Signature: (JIZ)I
 */
JNIEXPORT jint JNICALL Java_org_violetlib_aqua_AquaVibrantSupport_setupVisualEffectWindow
    (JNIEnv *env, jclass cl, jlong wptr, jint style, jboolean forceActive)
{
    jint result = -1;

    JNF_COCOA_ENTER(env);

    NSWindow *w = (NSWindow *) wptr;

    if (style == SHEET_STYLE) {
        forceActive = YES;
    }

    [JNFRunLoop performOnMainThreadWaiting:YES withBlock:^(){
        // Insert a visual effect view as a sibling of the AWT view if there is not already one present.
        AquaWrappedAWTView *wrapper = ensureWrapper(w);
        NSVisualEffectView *fxView = [wrapper addFullWindowVisualEffectView];
        fxView.appearance = getVibrantAppearance(style);
        fxView.material = getVibrantMaterial(style);
        fxView.state = forceActive ? NSVisualEffectStateActive : NSVisualEffectStateFollowsWindowActiveState;
        [fxView setNeedsDisplay: YES];
        setupLayers(fxView);
    }];
    result = 0;

    JNF_COCOA_EXIT(env);

    return result;
}

/*
 * Class:     org_violetlib_aqua_AquaVibrantSupport
 * Method:    removeVisualEffectWindow
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_violetlib_aqua_AquaVibrantSupport_removeVisualEffectWindow
    (JNIEnv *env, jclass cl, jlong wptr)
{
    jint result = -1;

    JNF_COCOA_ENTER(env);

    NSWindow *w = (NSWindow *) wptr;
    [JNFRunLoop performOnMainThreadWaiting:YES withBlock:^(){
        AquaWrappedAWTView *wrapper = getWrapper(w);
        if (wrapper != nil) {
            [wrapper removeFullWindowVisualEffectView];
        }
    }];
    result = 0;

    JNF_COCOA_EXIT(env);

    return result;
}

/*
 * Class:     org_violetlib_aqua_AquaVibrantSupport
 * Method:    createVisualEffectView
 * Signature: (JIZ)J
 */
JNIEXPORT jlong JNICALL Java_org_violetlib_aqua_AquaVibrantSupport_nativeCreateVisualEffectView
    (JNIEnv *env, jclass cl, jlong wptr, jint style, jboolean supportSelections)
{
    __block jlong result = 0;

    JNF_COCOA_ENTER(env);

    NSWindow *w = (NSWindow *) wptr;
    [JNFRunLoop performOnMainThreadWaiting:YES withBlock:^(){
        // Insert a view as a sibling of the AWT view.
        AquaWrappedAWTView *wrapper = ensureWrapper(w);
        NSView *view;
        if (supportSelections && style == SIDEBAR_STYLE) {
            view = [[AquaSidebarBackground alloc] initWithFrame: NSMakeRect(0, 0, 0, 0)];
        } else {
            NSVisualEffectView *fxView = [[NSVisualEffectView alloc] initWithFrame: NSMakeRect(0, 0, 0, 0)];
            fxView.appearance = getVibrantAppearance(style);
            fxView.material = getVibrantMaterial(style);
            fxView.blendingMode = NSVisualEffectBlendingModeBehindWindow;
            view = fxView;
        }
        [wrapper addSiblingView: view];
        setupLayers(view);
        result = (jlong) view;
    }];

    JNF_COCOA_EXIT(env);

    return result;
}

/*
 * Class:     org_violetlib_aqua_AquaVibrantSupport
 * Method:    setViewFrame
 * Signature: (JIIIII)I
 */
JNIEXPORT jint JNICALL Java_org_violetlib_aqua_AquaVibrantSupport_setViewFrame
    (JNIEnv *env, jclass cl, jlong ptr, jint x, jint y, jint w, jint h, jint yflipped)
{
    __block jint result = -1;

    JNF_COCOA_ENTER(env);

    NSView *view = (NSView *) ptr;
    NSWindow *window = [view window];

    if (window != nil) {
        [JNFRunLoop performOnMainThreadWaiting:YES withBlock:^(){

//            NSLog(@"Setting visual effect view frame: %d %d %d %d %d", x, y, w, h, yflipped);
//            NSRect f = window.frame;
//            NSLog(@"  Window size: %f %f", f.size.width, f.size.height);

            [view setFrame: NSMakeRect(x, yflipped, w, h)];
            view.needsDisplay = YES;
            result = 0;
        }];
    } else {
        NSLog(@"AquaVibrantSupport_setViewFrame failed: no native window");
    }

    JNF_COCOA_EXIT(env);

    return result;
}

/*
 * Class:     org_violetlib_aqua_AquaVibrantSupport
 * Method:    nativeUpdateSelectionBackgrounds
 * Signature: (J[I)I
 */
JNIEXPORT jint JNICALL Java_org_violetlib_aqua_AquaVibrantSupport_nativeUpdateSelectionBackgrounds
    (JNIEnv *env, jclass cl, jlong ptr, jintArray jdata)
{
    __block jint result = -1;

    JNF_COCOA_ENTER(env);

    NSView *view = (NSView *) ptr;

    //windowDebug(view.window);   // debug

    if ([view isKindOfClass: [AquaSidebarBackground class]]) {
        AquaSidebarBackground *sbb = (AquaSidebarBackground*) view;
        if (jdata != NULL) {
            int *data = (*env)->GetIntArrayElements(env, jdata, NULL);
            if (data != NULL) {
                [JNFRunLoop performOnMainThreadWaiting:YES withBlock:^(){
                    [sbb updateSelectionViews: data];
                    result = 0;
                }];
                (*env)->ReleaseIntArrayElements(env, jdata, data, JNI_ABORT);
            }
        } else {
            [JNFRunLoop performOnMainThreadWaiting:YES withBlock:^(){
                [sbb updateSelectionViews: NULL];
                result = 0;
            }];
        }
    }

    JNF_COCOA_EXIT(env);

    return result;
}

/*
 * Class:     org_violetlib_aqua_AquaVibrantSupport
 * Method:    disposeVisualEffectView
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_violetlib_aqua_AquaVibrantSupport_disposeVisualEffectView
    (JNIEnv *env, jclass cl, jlong ptr)
{
    __block jint result = -1;

    JNF_COCOA_ENTER(env);

    NSView *view = (NSView *) ptr;
    NSWindow *window = [view window];

    if (window != nil) {
        [JNFRunLoop performOnMainThreadWaiting:YES withBlock:^(){
            AquaWrappedAWTView *wrapper = getWrapper(window);
            if (wrapper != nil && wrapper == [view superview]) {
                [view removeFromSuperview];
                result = 0;
            }
        }];
    } else {
        NSLog(@"AquaVibrantSupport_disposeVisualEffectView failed: no native window");
    }

    JNF_COCOA_EXIT(env);

    return result;
}

/*
 * Class:     org_violetlib_aqua_AquaUtils
 * Method:    nativeSetAWTViewVisibility
 * Signature: (JZ)I
 */
JNIEXPORT jint JNICALL Java_org_violetlib_aqua_AquaUtils_nativeSetAWTViewVisibility
    (JNIEnv *env, jclass cl, jlong wptr, jboolean isVisible)
{
    JNF_COCOA_ENTER(env);

    NSWindow *w = (NSWindow *) wptr;
    [JNFRunLoop performOnMainThreadWaiting:YES withBlock:^(){
        NSView *v = getAWTView(w);
        v.hidden = !isVisible;
    }];

    JNF_COCOA_EXIT(env);

    return 0;
}

/*
 * Class:     org_violetlib_aqua_AquaUtils
 * Method:    nativeSyncAWTView
 * Signature: (J)V
 */
JNIEXPORT jint JNICALL Java_org_violetlib_aqua_AquaUtils_nativeSyncAWTView
    (JNIEnv *env, jclass cl, jlong wptr)
{
    JNF_COCOA_ENTER(env);

    NSWindow *w = (NSWindow *) wptr;
    [JNFRunLoop performOnMainThreadWaiting:YES withBlock:^(){
        NSView *v = getAWTView(w);
        //NSLog(@"Forcing update of AWTView layer");
        [v.layer displayIfNeeded];
        //NSLog(@"Completed forced update of AWTView layer");
    }];

    JNF_COCOA_EXIT(env);
    return 0;
}

/*
 * Class:     org_violetlib_aqua_AquaUtils
 * Method:    nativeGetLeftSideBearing
 * Signature: (Ljavax/swing/JComponent;Ljava/awt/FontMetrics;C)I
 */
JNIEXPORT jint JNICALL Java_org_violetlib_aqua_AquaUtils_nativeGetLeftSideBearing
  (JNIEnv *env, jclass cl, jobject comp, jobject fm, jchar firstChar)
{
    static JNF_CLASS_CACHE(jc_SwingUtilities2, "sun/swing/SwingUtilities2");
    static JNF_STATIC_MEMBER_CACHE(jm_getLeftSideBearing, jc_SwingUtilities2, "getLeftSideBearing", "(Ljavax/swing/JComponent;Ljava/awt/FontMetrics;C)I");

    jint result = 0;

    JNF_COCOA_ENTER(env);

    result = JNFCallStaticIntMethod(env, jm_getLeftSideBearing, comp, fm, firstChar);

    JNF_COCOA_EXIT(env);

    return result;
}

/*
 * Class:     org_violetlib_aqua_AquaUtils
 * Method:    nativeInstallAATextInfo
 * Signature: (Ljavax/swing/UIDefaults;)V
 */
JNIEXPORT void JNICALL Java_org_violetlib_aqua_AquaUtils_nativeInstallAATextInfo
  (JNIEnv *env, jclass cl, jobject table)
{
    // This implementation is valid in Java 9 (not in Java 8).
    // SwingUtilities2.putAATextInfo(true, table);

    static JNF_CLASS_CACHE(jc_SwingUtilities2, "sun/swing/SwingUtilities2");
    static JNF_STATIC_MEMBER_CACHE(jm_putAATextInfo, jc_SwingUtilities2, "putAATextInfo", "(ZLjava/util/Map;)V");

    JNF_COCOA_ENTER(env);

    JNFCallStaticVoidMethod(env, jm_putAATextInfo, JNI_TRUE, table);

    JNF_COCOA_EXIT(env);
}

/*
 * Class:     org_violetlib_aqua_AquaUtils
 * Method:    disablePopupCache
 * Signature: (Ljavax/swing/Popup;)V
 */
JNIEXPORT void JNICALL Java_org_violetlib_aqua_AquaUtils_disablePopupCache
    (JNIEnv *env, jclass cl, jobject popup)
{
    static JNF_CLASS_CACHE(jc_HeavyWeightPopup, "javax/swing/PopupFactory$HeavyWeightPopup");
    static JNF_MEMBER_CACHE(jm_setCacheEnabled, jc_HeavyWeightPopup, "setCacheEnabled", "(Z)V");

    JNFCallVoidMethod(env, popup, jm_setCacheEnabled, JNI_FALSE);
}

/*
 * Class:     org_violetlib_aqua_AquaUtils
 * Method:    getScreenMenuBarProperty
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_org_violetlib_aqua_AquaUtils_getScreenMenuBarProperty
  (JNIEnv *env, jclass cl)
{
    static JNF_CLASS_CACHE(jc_AquaMenuBarUI, "com/apple/laf/AquaMenuBarUI");
    static JNF_STATIC_MEMBER_CACHE(jm_getScreenMenuBarProperty, jc_AquaMenuBarUI, "getScreenMenuBarProperty", "()Z");

    static JNF_CLASS_CACHE(jc_LWCToolkit, "sun/lwawt/macosx/LWCToolkit");
    static JNF_STATIC_MEMBER_CACHE(jm_isSystemMenuBarSupported, jc_LWCToolkit, "isSystemMenuBarSupported", "()Z");

    jboolean result = 0;

    @try {
        result = JNFCallStaticBooleanMethod(env, jm_getScreenMenuBarProperty);
    }
    @catch (NSException *exception) {
        @try {
            result = JNFCallStaticBooleanMethod(env, jm_isSystemMenuBarSupported);
        }
        @catch (NSException *exception2) {
        }
    }

    return result;
}

/*
 * Class:     org_violetlib_aqua_AquaUtils
 * Method:    setScreenMenuBar
 * Signature: (Ljavax/swing/JFrame;Ljavax/swing/plaf/MenuBarUI;)V
 */
JNIEXPORT void JNICALL Java_org_violetlib_aqua_AquaUtils_setScreenMenuBar
  (JNIEnv *env, jclass cl, jobject frame, jobject menuBarUI)
{
    static JNF_CLASS_CACHE(jc_AquaMenuBarUI, "com/apple/laf/AquaMenuBarUI");
    static JNF_MEMBER_CACHE(jm_setScreenMenuBar, jc_AquaMenuBarUI, "setScreenMenuBar", "(Ljavax/swing/JFrame;)Z");

    JNFCallBooleanMethod(env, menuBarUI, jm_setScreenMenuBar, frame);
}

/*
 * Class:     org_violetlib_aqua_AquaUtils
 * Method:    clearScreenMenuBar
 * Signature: (Ljavax/swing/JFrame;Ljavax/swing/plaf/MenuBarUI;)V
 */
JNIEXPORT void JNICALL Java_org_violetlib_aqua_AquaUtils_clearScreenMenuBar
  (JNIEnv *env, jclass cl, jobject frame, jobject menuBarUI)
{
    static JNF_CLASS_CACHE(jc_AquaMenuBarUI, "com/apple/laf/AquaMenuBarUI");
    static JNF_MEMBER_CACHE(jm_clearScreenMenuBar, jc_AquaMenuBarUI, "clearScreenMenuBar", "(Ljavax/swing/JFrame;)V");

    JNFCallVoidMethod(env, menuBarUI, jm_clearScreenMenuBar, frame);
}

/*
 * Class:     org_violetlib_aqua_AquaUtils
 * Method:    nativeHasOpaqueBeenExplicitlySet
 * Signature: (Ljavax/swing/JComponent;)Z
 */
JNIEXPORT jboolean JNICALL Java_org_violetlib_aqua_AquaUtils_nativeHasOpaqueBeenExplicitlySet
  (JNIEnv *env, jclass cl, jobject c)
{
    static JNF_CLASS_CACHE(jc_JComponent, "javax/swing/JComponent");
    static JNF_MEMBER_CACHE(jm_getFlag, jc_JComponent, "getFlag", "(I)Z");

    return JNFCallBooleanMethod(env, c, jm_getFlag, 24);    // 24 is JComponent.OPAQUE_SET
}

/*
 * Class:     org_violetlib_aqua_AquaUtils
 * Method:    nativeDebugWindow
 * Signature: (J)V
 */
JNIEXPORT int JNICALL Java_org_violetlib_aqua_AquaUtils_nativeDebugWindow
    (JNIEnv *env, jclass cl, jlong wptr)
{
    JNF_COCOA_ENTER(env);

    NSWindow *w = (NSWindow *) wptr;
    [JNFRunLoop performOnMainThreadWaiting:YES withBlock:^(){
        windowDebug(w);
    }];

    JNF_COCOA_EXIT(env);
    return 0;
}

/*
 * Class:     org_violetlib_aqua_AquaUtils
 * Method:    syslog
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_org_violetlib_aqua_AquaUtils_syslog
    (JNIEnv *env, jclass cl, jstring msg)
{
    jsize slen = (*env) -> GetStringLength(env, msg);
    const jchar *schars = (*env) -> GetStringChars(env, msg, NULL);
    CFStringRef s = CFStringCreateWithCharacters(NULL, schars, slen);
    NSLog(@"%@", s);
    CFRelease(s);
    (*env) -> ReleaseStringChars(env, msg, schars);
}

/*
 * Class:     org_violetlib_aqua_AquaNativeSupport
 * Method:    setup
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_org_violetlib_aqua_AquaNativeSupport_setup
    (JNIEnv *env, jclass cl, jint jv)
{
    javaVersion = jv;
}

/*
 * Class:     org_violetlib_aqua_AquaNativeSupport
 * Method:    nativeGetNativeCodeVersion
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_violetlib_aqua_AquaNativeSupport_nativeGetNativeCodeVersion
    (JNIEnv *env, jclass javaClass)
{
    return VERSION;
}
