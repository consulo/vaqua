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

import org.violetlib.jnr.aqua.AquaUIPainter.TextFieldWidget;
import org.violetlib.jnr.aqua.AquaUIPainter.Size;
import org.violetlib.jnr.aqua.AquaUIPainter.State;
import org.violetlib.jnr.aqua.AquaUIPainter.UILayoutDirection;

/**
	A configuration for a text field.
*/

public class TextFieldConfiguration
	extends TextFieldLayoutConfiguration
	implements Configuration
{
	private final @Nonnull
	State state;
	private final boolean isFocused;

	public TextFieldConfiguration(@Nonnull TextFieldWidget tw,
																@Nonnull Size sz,
																@Nonnull State state,
																boolean isFocused,
																@Nonnull UILayoutDirection ld)
	{
		super(tw, sz, ld);

		this.state = state;
		this.isFocused = isFocused;
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
		TextFieldConfiguration that = (TextFieldConfiguration) o;
		return state == that.state && isFocused == that.isFocused;
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
