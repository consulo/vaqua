/*
 * Copyright (c) 2016 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

package org.violetlib.jnr.impl;

import javax.annotation.Nonnull;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * Java platform specific support for Java 9 and later.
 */
public class Java9Support implements JavaSupportImpl {
    @Override
    public boolean isAvaliable() {
        try {
            Class.forName("java.lang.Module");
            Class.forName("java.awt.image.MultiResolutionImage");
            return true;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public int getScaleFactor(@Nonnull Graphics g) {
        // This works in Java 9. Before that, it returned 1.
        Graphics2D gg = (Graphics2D) g;
        GraphicsConfiguration gc = gg.getDeviceConfiguration();
        AffineTransform t = gc.getDefaultTransform();
        double sx = t.getScaleX();
        double sy = t.getScaleY();
        return (int) Math.max(sx, sy);
    }

    @Override
    public Image createMultiResolutionImage(int baseImageWidth, int baseImageHeight, @Nonnull BufferedImage im) {
        return JNR9MultiResolutionImage.create(baseImageWidth, baseImageHeight, im);
    }
}
