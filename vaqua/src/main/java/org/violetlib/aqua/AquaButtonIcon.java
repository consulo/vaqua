/*
 * Copyright (c) 2016-2018 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

package org.violetlib.aqua;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.UIResource;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An icon for a specific button whose rendering may depend upon the widget, state, button state, and appearance. The
 * rendering is based on the button's default icon.
 */

public class AquaButtonIcon implements Icon, UIResource {

    public interface ImageOperatorSupplier {
        @Nullable Object getCurrentImageProcessingOperator(@NotNull AbstractButton b, boolean isTemplate);
    }

    protected final @NotNull AbstractButton b;
    protected final boolean isTemplate;
    protected final @NotNull ImageOperatorSupplier operatorSupplier;

    /**
     * Create a context-sensitive button icon.
     * @param b The button.
     * @param isTemplate True if the source image is a template image and it should be treated as such.
     * @param operatorSupplier Determines the image processing operator to apply to the source image when the icon is
     *                         painted.
     */
    public AquaButtonIcon(@NotNull AbstractButton b,
                          boolean isTemplate,
                          @NotNull ImageOperatorSupplier operatorSupplier) {
        this.b = b;
        this.isTemplate = isTemplate;
        this.operatorSupplier = operatorSupplier;
    }

    @Override
    public int getIconWidth() {
        Icon icon = b.getIcon();
        return icon != null ? icon.getIconWidth() : 0;
    }

    @Override
    public int getIconHeight() {
        Icon icon = b.getIcon();
        return icon != null ? icon.getIconHeight() : 0;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Icon icon = b.getIcon();
        if (icon != null) {
            Object operator = operatorSupplier.getCurrentImageProcessingOperator(b, isTemplate);
            Image im = AquaImageFactory.getProcessedImage(icon, operator);
            if (im != null) {
                boolean isComplete = g.drawImage(im, x, y, c);
                if (!isComplete) {
                    new ImageIcon(im);
                    if (!g.drawImage(im, x, y, c)) {
                        System.err.println("Button icon not drawn!");
                    }
                }
            }
        }
    }
}
