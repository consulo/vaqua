/*
 * Copyright (c) 2015-2016 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

package org.violetlib.jnr.impl;

import javax.annotation.Nonnull;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.util.ArrayList;
import java.util.List;

/**

 */
public class JNR8MultiResolutionImage extends Image implements sun.awt.image.MultiResolutionImage {
    public static Image create(int baseImageWidth, int baseImageHeight, @Nonnull BufferedImage im) {
        return new JNR8MultiResolutionImage(baseImageWidth, baseImageHeight, im);
    }

    private final int baseImageWidth;
    private final int baseImageHeight;
    private final
    @Nonnull
    BufferedImage im;

    private JNR8MultiResolutionImage(int baseImageWidth, int baseImageHeight, @Nonnull BufferedImage im) {
        this.baseImageWidth = baseImageWidth;
        this.baseImageHeight = baseImageHeight;
        this.im = im;
    }

    @Override
    public Image getResolutionVariant(int width, int height) {
        return im;
    }

    @Override
    public List<Image> getResolutionVariants() {
        List<Image> result = new ArrayList<>();
        result.add(im);
        return result;
    }

    @Override
    public int getWidth(ImageObserver observer) {
        return baseImageWidth;
    }

    @Override
    public int getHeight(ImageObserver observer) {
        return baseImageHeight;
    }

    @Override
    public Object getProperty(String name, ImageObserver observer) {
        return im.getProperty(name, observer);
    }

    @Override
    public ImageProducer getSource() {
        return im.getSource();
    }

    @Override
    public Graphics getGraphics() {
        return im.getGraphics();
    }
}
