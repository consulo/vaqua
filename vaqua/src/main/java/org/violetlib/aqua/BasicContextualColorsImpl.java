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
 * Identify contextually determined foreground and background colors.
 */

public class BasicContextualColorsImpl implements BasicContextualColors {

    protected @Nonnull
	ContextualColor background;
    protected @Nonnull
	ContextualColor foreground;

    public BasicContextualColorsImpl(@Nonnull ContextualColor background,
                                     @Nonnull ContextualColor foreground) {
        this.background = background;
        this.foreground = foreground;
    }

    @Override
    public @Nonnull
	Color getBackground(@Nonnull AppearanceContext context) {
        AquaColors.setupDebugging(this);
        Color color = background.get(context);
        AquaColors.clearDebugging();
        return color;
    }

    @Override
    public @Nonnull
	Color getForeground(@Nonnull AppearanceContext context) {
        AquaColors.setupDebugging(this);
        Color color = foreground.get(context);
        AquaColors.clearDebugging();
        return color;
    }
}
