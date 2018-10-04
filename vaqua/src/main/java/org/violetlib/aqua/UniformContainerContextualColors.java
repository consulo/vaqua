/*
 * Copyright (c) 2018 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

package org.violetlib.aqua;

import java.awt.*;

import javax.annotation.Nonnull;

/**

 */

public class UniformContainerContextualColors implements ContainerContextualColors {

    protected @Nonnull
	ContextualColor background;
    protected @Nonnull
	ContextualColor foreground;
    protected @Nonnull
	ContextualColor grid;
    protected boolean isRowSelected;

    /**
     * Create colors for a uniform (not striped) container.
     */

    public UniformContainerContextualColors(@Nonnull ContextualColor background,
                                            @Nonnull ContextualColor foreground,
                                            @Nonnull ContextualColor grid) {
        this.background = background;
        this.foreground = foreground;
        this.grid = grid;
    }

    public void configureForContainer() {
        isRowSelected = false;
    }

    public void configureForRow(int rowIndex, boolean isRowSelected) {
        this.isRowSelected = isRowSelected;
    }

    public void configureForRow(boolean isRowSelected) {
        this.isRowSelected = isRowSelected;
    }

    public boolean isStriped() {
        return false;
    }

    @Override
    public @Nonnull
	Color getBackground(@Nonnull AppearanceContext context) {
        context = context.withSelected(isRowSelected || context.isSelected());
        return background.get(context);
    }

    @Override
    public @Nonnull
	Color getForeground(@Nonnull AppearanceContext context) {
        context = context.withSelected(isRowSelected || context.isSelected());
        return foreground.get(context);
    }

    public @Nonnull
	Color getGrid(@Nonnull AppearanceContext context) {
        context = context.withSelected(isRowSelected || context.isSelected());
        return grid.get(context);
    }
}
