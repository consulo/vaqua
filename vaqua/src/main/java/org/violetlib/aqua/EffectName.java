/*
 * Copyright (c) 2018 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

package org.violetlib.aqua;

import javax.annotation.Nonnull;

/**

 */

public class EffectName {
    private final @Nonnull
	String name;

    public static final @Nonnull
	EffectName EFFECT_NONE = new EffectName("none");
    public static final @Nonnull
	EffectName EFFECT_PRESSED = new EffectName("pressed");
    public static final @Nonnull
	EffectName EFFECT_DEEP_PRESSED = new EffectName("deepPressed");
    public static final @Nonnull
	EffectName EFFECT_DISABLED = new EffectName("disabled");
    public static final @Nonnull
	EffectName EFFECT_ROLLOVER = new EffectName("rollover");

    private EffectName(@Nonnull String name) {
        this.name = name;
    }

    public @Nonnull
	String getName() {
        return name;
    }

    @Override
    public @Nonnull
	String toString() {
        return name;
    }
}
