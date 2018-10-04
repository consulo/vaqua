/*
 * Copyright (c) 2015 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

package org.violetlib.jnr.aqua;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.violetlib.jnr.aqua.AquaUIPainter.State;

/**
	A configuration for a scroll column sizer.
*/

public class ScrollColumnSizerConfiguration
	extends ScrollColumnSizerLayoutConfiguration
	implements Configuration
{
	private final @Nonnull
	State state;
	private final boolean isFocused;

	public ScrollColumnSizerConfiguration(@Nonnull State state, boolean isFocused)
	{
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
		ScrollColumnSizerConfiguration that = (ScrollColumnSizerConfiguration) o;
		return state == that.state && isFocused == that.isFocused;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(state, isFocused);
	}

	@Override
	public @Nonnull
	String toString()
	{
		String fs = isFocused ? " focused" : "";
		return super.toString() + " " + state + fs;
	}
}
