/*
 * Changes copyright (c) 2016-2018 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

package org.violetlib.aqua;

import org.violetlib.aqua.impl.JavaSupportImpl;
import relocate.javax.swing.plaf.basic.BasicGraphicsUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.util.function.Function;

/**
 * Support for Java 9 and later
 */

public class Java9Support implements JavaSupportImpl {
    @Override
    public boolean isAvaliable() {
        try {
            return Class.forName("java.lang.Module") != null && Class.forName("java.awt.image.MultiResolutionImage") != null;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public int getScaleFactor(Graphics g) {
        // This works in Java 9. Before that, it returned 1.
        Graphics2D gg = (Graphics2D) g;
        GraphicsConfiguration gc = gg.getDeviceConfiguration();
        AffineTransform t = gc.getDefaultTransform();
        double sx = t.getScaleX();
        double sy = t.getScaleY();
        return (int) Math.max(sx, sy);
    }

    @Override
    public boolean hasOpaqueBeenExplicitlySet(JComponent c) {
        boolean result = AquaUtils.nativeHasOpaqueBeenExplicitlySet(c);
        if (result) {
            //AquaUtils.syslog("HasOpaqueBeenExplicitlySet: " + c);
        }
        return result;
    }

    @Override
    public Image getDockIconImage() {
        Taskbar t = Taskbar.getTaskbar();
        return t.getIconImage();
    }

    @Override
    public void drawString(JComponent c, Graphics2D g, String string, float x, float y) {
        BasicGraphicsUtils.drawString(c, g, string, x, y);
    }

    @Override
    public void drawStringUnderlineCharAt(JComponent c, Graphics2D g, String string, int underlinedIndex, float x, float y) {
        BasicGraphicsUtils.drawStringUnderlineCharAt(c, g, string, underlinedIndex, x, y);
    }

    @Override
    public String getClippedString(JComponent c, FontMetrics fm, String string, int availTextWidth) {
        return BasicGraphicsUtils.getClippedString(c, fm, string, availTextWidth);
    }

    @Override
    public float getStringWidth(JComponent c, FontMetrics fm, String string) {
        return BasicGraphicsUtils.getStringWidth(c, fm, string);
    }

    @Override
    public void installAATextInfo(UIDefaults table) {
        AquaUtils.nativeInstallAATextInfo(table);
    }

    @Override
    public AquaMultiResolutionImage createMultiResolutionImage(BufferedImage im) {
        return new Aqua9MultiResolutionImage(im);
    }

    @Override
    public AquaMultiResolutionImage createMultiResolutionImage(BufferedImage im1, BufferedImage im2) {
        return new Aqua9MultiResolutionImage2(im1, im2);
    }

    @Override
    public Image applyFilter(Image image, ImageFilter filter) {
        return Aqua9MultiResolutionImage.apply(image, filter);
    }

    @Override
    public Image applyMapper(Image source, Function<Image, Image> mapper) {
        return Aqua9MultiResolutionImage.apply(source, mapper);
    }

    @Override
    public Image applyMapper(Image source, AquaMultiResolutionImage.Mapper mapper) {
        return Aqua9MultiResolutionImage.apply(source, mapper);
    }

    @Override
    public BufferedImage createImage(int width, int height, int[] data) {
        ColorModel colorModel = new
                DirectColorModel(
                ColorSpace.getInstance(ColorSpace.CS_sRGB),
                32,
                0x00ff0000,// Red
                0x0000ff00,// Green
                0x000000ff,// Blue
                0xff000000,// Alpha
                true,       // Alpha Premultiplied
                DataBuffer.TYPE_INT
        );
        WritableRaster raster = colorModel.createCompatibleWritableRaster(width, height);
        raster.setDataElements(0, 0, width, height, data);
        BufferedImage b = new BufferedImage(colorModel, raster, true, null);
        return b;
    }

    public void preload(Image image, int availableInfo) {

        // TBD: preloading is supported by a private method of ToolkitImage
        // Currently would need to use native code to call it
    }

    @Override
    public void lockRenderQueue() {
        // TBD
    }

    @Override
    public void unlockRenderQueue() {
        // TBD
    }

    @Override
    public AquaPopupFactory createPopupFactory() {
        return new Aqua9PopupFactory();
    }
}
