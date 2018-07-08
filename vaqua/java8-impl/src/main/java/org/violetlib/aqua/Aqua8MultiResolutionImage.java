/*
 * Copyright (c) 2015-2016 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

package org.violetlib.aqua;

import java.awt.*;
import java.awt.image.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import sun.awt.image.MultiResolutionCachedImage;
import sun.awt.image.MultiResolutionImage;

/**
 * A multi-resolution image with a single representation. This class is designed for Java 8.
 */
public class Aqua8MultiResolutionImage extends AquaMultiResolutionImage implements MultiResolutionImage {
    public Aqua8MultiResolutionImage(BufferedImage im) {
        super(im);
    }

    public Aqua8MultiResolutionImage(int width, int height, BufferedImage im) {
        super(im, width, height);
    }

    @Override
    public Image getResolutionVariant(int width, int height) {
        return baseImage;
    }

    @Override
    public List<Image> getResolutionVariants() {
        java.util.List<Image> result = new ArrayList<>();
        result.add(baseImage);
        return result;
    }

    @Override
    public AquaMultiResolutionImage map(Mapper mapper) {
        return new Aqua8MultiResolutionImage(mapper.map(baseImage, 1));
    }

    @Override
    public AquaMultiResolutionImage map(Function<Image, Image> mapper) {
        return new Aqua8MultiResolutionImage(Images.toBufferedImage(mapper.apply(baseImage)));
    }

    /**
     * Create an image by applying a filter. Supports multi-resolution images.
     */
    public static Image apply(Image image, ImageFilter filter) {
        if (image instanceof MultiResolutionImage) {
            Function<Image,Image> f = (rv -> createFilteredImage(rv, filter));
            return apply(image, f);
        }

        return createFilteredImage(image, filter);
    }

    private static Image createFilteredImage(Image image, ImageFilter filter) {
        final ImageProducer prod = new FilteredImageSource(image.getSource(), filter);
        return waitForImage(Toolkit.getDefaultToolkit().createImage(prod));
    }

    // The following is a workaround for a JDK 8 problem - trying to draw a MultiResolutionImage that is not ready
    // throws an exception.

    private static Image waitForImage(Image image) {
        final boolean[] mutex = new boolean[] { false };
        ImageObserver observer = (Image img, int infoflags, int x, int y, int width, int height) -> {
            if ((width != -1 && height != -1 && (infoflags & ImageObserver.ALLBITS) != 0) || (infoflags & ImageObserver.ABORT) != 0) {
                synchronized (mutex) {
                    mutex[0] = true;
                    mutex.notify();
                }
                return false;
            } else {
                return true;
            }
        };
        synchronized (mutex) {
            while (!mutex[0] && image.getWidth(observer) == -1) {
                try {
                    mutex.wait();
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
        return image;
    }

    /**
     * Create an image by applying a generic mapper. Supports multi-resolution images.
     */
    public static Image apply(Image source, Function<Image,Image> mapper) {
        if (source instanceof MultiResolutionCachedImage) {
            MultiResolutionCachedImage s = (MultiResolutionCachedImage) source;
            return s.map(mapper::apply);
        }

        if (source instanceof AquaMultiResolutionImage) {
            AquaMultiResolutionImage s = (AquaMultiResolutionImage) source;
            return s.map(mapper);
        }

        if (source instanceof MultiResolutionImage) {
            MultiResolutionImage s = (MultiResolutionImage) source;
            return new Aqua8MappedMultiResolutionImage(s, mapper);
        }

        return mapper.apply(source);
    }

    /**
     * Create an image by applying a specialized mapper. Supports multi-resolution images.
     */
    public static Image apply(Image source, AquaMultiResolutionImage.Mapper mapper) {
        if (source instanceof MultiResolutionCachedImage) {
            MultiResolutionCachedImage s = (MultiResolutionCachedImage) source;
            int width = s.getWidth(null);
            return s.map(rv -> mapper.map(rv, rv.getWidth(null) / width));
        }

        if (source instanceof AquaMultiResolutionImage) {
            AquaMultiResolutionImage s = (AquaMultiResolutionImage) source;
            return s.map(mapper);
        }

        // The following allows optimized implementations.
        if (source instanceof MultiResolutionImage) {
            MultiResolutionImage im = (MultiResolutionImage) source;
            try {
                Method m = im.getClass().getMethod("map", Function.class);
                m.setAccessible(true);
                Object o = m.invoke(im, mapper);
                if (o instanceof Image) {
                    return (Image) o;
                }
            } catch (NoSuchMethodException ex) {
                // ignore
            } catch (Exception ex) {
                if (ex instanceof InvocationTargetException) {
                    Throwable th = ((InvocationTargetException) ex).getTargetException();
                    System.err.println("Unable to map image: " + th);
                } else {
                    System.err.println("Unable to map image: " + ex);
                }
            }
        }

        if (source instanceof MultiResolutionImage) {
            MultiResolutionImage s = (MultiResolutionImage) source;
            int width = source.getWidth(null);
            return new Aqua8MappedMultiResolutionImage(s, rv -> mapper.map(rv, rv.getWidth(null) / width));
        }

        return mapper.map(source, 1);
    }
}
