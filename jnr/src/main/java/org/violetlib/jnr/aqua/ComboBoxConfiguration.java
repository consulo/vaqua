/*
 * Copyright (c) 2015 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

package org.violetlib.jnr.aqua;

import java.util.Objects;

import javax.annotation.*;

import org.violetlib.jnr.aqua.AquaUIPainter.ComboBoxWidget;
import org.violetlib.jnr.aqua.AquaUIPainter.Size;
import org.violetlib.jnr.aqua.AquaUIPainter.State;
import org.violetlib.jnr.aqua.AquaUIPainter.UILayoutDirection;

/**
	A configuration for an editable combo box.
*/

public class ComboBoxConfiguration
	extends ComboBoxLayoutConfiguration
	implements Configuration
{
	private final @Nonnull
	State state;
	private final boolean isFocused;

	public ComboBoxConfiguration(@Nonnull ComboBoxWidget widget,
															 @Nonnull Size size,
															 @Nonnull State state,
															 boolean isFocused,
															 @Nonnull UILayoutDirection ld)
	{
		super(widget, size, ld);

		this.isFocused = isFocused;
		this.state = state;
	}

	public ComboBoxConfiguration(@Nonnull ComboBoxLayoutConfiguration g, @Nonnull State state, boolean isFocused)
	{
		this(g.getWidget(), g.getSize(), state, isFocused, g.getLayoutDirection());
	}

	public @Nonnull
	State getState()
	{
		return state;
	}

	public boolean isFocused()
	{
		return isFocused;
	}

	@Override
	public boolean equals(@Nullable Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		ComboBoxConfiguration that = (ComboBoxConfiguration) o;
		return isFocused == that.isFocused && state == that.state;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(super.hashCode(), state, isFocused);
	}

	@Override
	public @Nonnull
	String toString()
	{
		String fs = isFocused ? " focused" : "";
		return super.toString() + " " + state + fs;
	}
}
