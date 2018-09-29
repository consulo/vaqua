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

public interface BasicContextualColors {

    @Nonnull
	Color getBackground(@Nonnull AppearanceContext context);

    @Nonnull
	Color getForeground(@Nonnull AppearanceContext context);
}
