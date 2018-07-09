package org.violetlib.aqua.impl;

import org.violetlib.aqua.AquaMultiResolutionImage;
import org.violetlib.aqua.AquaPopupFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageFilter;
import java.util.function.Function;

public interface JavaSupportImpl {
    boolean isAvaliable();

    int getScaleFactor(Graphics g);

    boolean hasOpaqueBeenExplicitlySet(final JComponent c);

    Image getDockIconImage();

    void drawString(JComponent c, Graphics2D g, String string, float x, float y);

    void drawStringUnderlineCharAt(JComponent c, Graphics2D g, String string, int underlinedIndex, float x, float y);

    String getClippedString(JComponent c, FontMetrics fm, String string, int availTextWidth);

    float getStringWidth(JComponent c, FontMetrics fm, String string);

    void installAATextInfo(UIDefaults table);

    AquaMultiResolutionImage createMultiResolutionImage(BufferedImage im);

    AquaMultiResolutionImage createMultiResolutionImage(BufferedImage im1, BufferedImage im2);

    Image applyFilter(Image image, ImageFilter filter);

    Image applyMapper(Image source, Function<Image, Image> mapper);

    Image applyMapper(Image source, AquaMultiResolutionImage.Mapper mapper);

    BufferedImage createImage(int width, int height, int[] data);

    void preload(Image image, int availableInfo);

    void lockRenderQueue();

    void unlockRenderQueue();

    AquaPopupFactory createPopupFactory();
}
