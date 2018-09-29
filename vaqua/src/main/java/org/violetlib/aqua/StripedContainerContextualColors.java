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

public class StripedContainerContextualColors implements ContainerContextualColors {

    protected @Nonnull
	ContextualColor containerBackground;
    protected @Nonnull
	ContextualColor evenRowBackground;
    protected @Nonnull
	ContextualColor oddRowBackground;
    protected @Nonnull
	ContextualColor grid;
    protected boolean isRowSelected;
    protected @Nonnull
	ContextualColor background;
    protected @Nonnull
	ContextualColor foreground;

    /**
     * Create colors for a striped container.
     */
    public StripedContainerContextualColors(@Nonnull ContextualColor containerBackground,
                                            @Nonnull ContextualColor evenRowBackground,
                                            @Nonnull ContextualColor oddRowBackground,
                                            @Nonnull ContextualColor foreground,
                                            @Nonnull ContextualColor grid) {
        this.containerBackground = containerBackground;
        this.evenRowBackground = evenRowBackground;
        this.oddRowBackground = oddRowBackground;
        this.grid = grid;
        this.background = containerBackground;
        this.foreground = foreground;
    }

    public void configureForContainer() {
        isRowSelected = false;
        this.background = containerBackground;
    }

    public void configureForRow(int rowIndex, boolean isRowSelected) {
        this.isRowSelected = isRowSelected;
        this.background = rowIndex % 2 == 0 ? evenRowBackground : oddRowBackground;
    }

    public void configureForRow(boolean isRowSelected) {
        this.isRowSelected = isRowSelected;
        this.background = containerBackground;
    }

    public boolean isStriped() {
        return true;
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
