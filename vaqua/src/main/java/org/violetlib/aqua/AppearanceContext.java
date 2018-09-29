/*
 * Copyright (c) 2018 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

package org.violetlib.aqua;

import javax.annotation.Nonnull;

import org.violetlib.jnr.aqua.AquaUIPainter;

/**

 */

public class AppearanceContext {

    private final @Nonnull
	AquaAppearance appearance;
    private final @Nonnull
	AquaUIPainter.State state;
    private final boolean isSelected;
    private final boolean isIcon;

    /**
     * Create an appearance context
     * @param appearance The appearance to use.
     * @param state The component state. The {@code ACTIVE_DEFAULT} state is used for active components that own the
     *              keyboard focus.
     * @param isSelected If true, use a color appropriate for a selected item, if one is defined.
     * @param isIcon If true, use a color appropriate for an icon label, if one is defined.
     */

    public AppearanceContext(@Nonnull AquaAppearance appearance,
                             @Nonnull AquaUIPainter.State state,
                             boolean isSelected,
                             boolean isIcon) {
        this.appearance = appearance;
        this.state = state;
        this.isSelected = isSelected;
        this.isIcon = isIcon;
    }

    public @Nonnull
	AquaAppearance getAppearance() {
        return appearance;
    }

    public @Nonnull
	AquaUIPainter.State getState() {
        return state;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public boolean isIcon() {
        return isIcon;
    }

    public @Nonnull
	AppearanceContext withSelected(boolean b) {
        return b == isSelected ? this : new AppearanceContext(appearance, state, b, isIcon);
    }

    public @Nonnull
	AppearanceContext withState(@Nonnull AquaUIPainter.State state) {
        return state == this.state ? this : new AppearanceContext(appearance, state, isSelected, isIcon);
    }

    @Override
    public @Nonnull
	String toString() {
        String s = "[" + appearance.getName() + " " + state;
        if (isSelected) {
            s += " Selected";
        }
        if (isIcon) {
            s += " Icon";
        }
        return s + "]";
    }
}
