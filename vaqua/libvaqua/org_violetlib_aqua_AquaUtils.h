/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class org_violetlib_aqua_AquaUtils */

#ifndef _Included_org_violetlib_aqua_AquaUtils
#define _Included_org_violetlib_aqua_AquaUtils
#ifdef __cplusplus
extern "C" {
#endif
#undef org_violetlib_aqua_AquaUtils_MENU_BLINK_DELAY
#define org_violetlib_aqua_AquaUtils_MENU_BLINK_DELAY 50L
#undef org_violetlib_aqua_AquaUtils_ERASE_IF_TEXTURED
#define org_violetlib_aqua_AquaUtils_ERASE_IF_TEXTURED 1L
#undef org_violetlib_aqua_AquaUtils_ERASE_IF_VIBRANT
#define org_violetlib_aqua_AquaUtils_ERASE_IF_VIBRANT 2L
#undef org_violetlib_aqua_AquaUtils_ERASE_ALWLAYS
#define org_violetlib_aqua_AquaUtils_ERASE_ALWLAYS 4L
#undef org_violetlib_aqua_AquaUtils_TITLE_BAR_NONE
#define org_violetlib_aqua_AquaUtils_TITLE_BAR_NONE 0L
#undef org_violetlib_aqua_AquaUtils_TITLE_BAR_ORDINARY
#define org_violetlib_aqua_AquaUtils_TITLE_BAR_ORDINARY 1L
#undef org_violetlib_aqua_AquaUtils_TITLE_BAR_TRANSPARENT
#define org_violetlib_aqua_AquaUtils_TITLE_BAR_TRANSPARENT 2L
#undef org_violetlib_aqua_AquaUtils_TITLE_BAR_HIDDEN
#define org_violetlib_aqua_AquaUtils_TITLE_BAR_HIDDEN 3L
#undef org_violetlib_aqua_AquaUtils_TITLE_BAR_OVERLAY
#define org_violetlib_aqua_AquaUtils_TITLE_BAR_OVERLAY 4L
/*
 * Class:     org_violetlib_aqua_AquaUtils
 * Method:    nativeIsFullScreenWindow
 * Signature: (Ljava/awt/Window;)Z
 */
JNIEXPORT jboolean JNICALL Java_org_violetlib_aqua_AquaUtils_nativeIsFullScreenWindow
  (JNIEnv *, jclass, jobject);

/*
 * Class:     org_violetlib_aqua_AquaUtils
 * Method:    nativeSetTitleBarStyle
 * Signature: (Ljava/awt/Window;I)I
 */
JNIEXPORT jint JNICALL Java_org_violetlib_aqua_AquaUtils_nativeSetTitleBarStyle
  (JNIEnv *, jclass, jobject, jint);

/*
 * Class:     org_violetlib_aqua_AquaUtils
 * Method:    nativeAddToolbarToWindow
 * Signature: (Ljava/awt/Window;)I
 */
JNIEXPORT jint JNICALL Java_org_violetlib_aqua_AquaUtils_nativeAddToolbarToWindow
  (JNIEnv *, jclass, jobject);

/*
 * Class:     org_violetlib_aqua_AquaUtils
 * Method:    nativeSetWindowCornerRadius
 * Signature: (Ljava/awt/Window;F)I
 */
JNIEXPORT jint JNICALL Java_org_violetlib_aqua_AquaUtils_nativeSetWindowCornerRadius
  (JNIEnv *, jclass, jobject, jfloat);

/*
 * Class:     org_violetlib_aqua_AquaUtils
 * Method:    nativeSetAWTViewVisibility
 * Signature: (Ljava/awt/Window;Z)V
 */
JNIEXPORT void JNICALL Java_org_violetlib_aqua_AquaUtils_nativeSetAWTViewVisibility
  (JNIEnv *, jclass, jobject, jboolean);

/*
 * Class:     org_violetlib_aqua_AquaUtils
 * Method:    nativeSyncAWTView
 * Signature: (Ljava/awt/Window;)V
 */
JNIEXPORT void JNICALL Java_org_violetlib_aqua_AquaUtils_nativeSyncAWTView
  (JNIEnv *, jclass, jobject);

/*
 * Class:     org_violetlib_aqua_AquaUtils
 * Method:    debugWindow
 * Signature: (Ljava/awt/Window;)V
 */
JNIEXPORT void JNICALL Java_org_violetlib_aqua_AquaUtils_debugWindow
  (JNIEnv *, jclass, jobject);

/*
 * Class:     org_violetlib_aqua_AquaUtils
 * Method:    syslog
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_org_violetlib_aqua_AquaUtils_syslog
  (JNIEnv *, jclass, jstring);

#ifdef __cplusplus
}
#endif
#endif