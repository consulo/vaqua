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

import org.violetlib.jnr.aqua.AquaUIPainter.Size;
import org.violetlib.jnr.aqua.AquaUIPainter.State;

/**
	A configuration for spinner arrows.
*/

public class SpinnerArrowsConfiguration
	extends SpinnerArrowsLayoutConfiguration
	implements Configuration
{
	private final @Nonnull
	State state;
	private final boolean isFocused;
	private final boolean isPressedTop;

	public SpinnerArrowsConfiguration(@Nonnull Size size,
																		@Nonnull State state,
																		boolean isFocused,
																		boolean isPressedTop)
	{
		super(size);
		this.state = state;
		this.isFocused = isFocused;
		this.isPressedTop = isPressedTop;
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

	public boolean isPressedTop()
	{
		return isPressedTop;
	}

	@Override
	public boolean equals(@Nullable Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		SpinnerArrowsConfiguration that = (SpinnerArrowsConfiguration) o;
		return state == that.state && isFocused == that.isFocused && isPressedTop == that.isPressedTop;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(super.hashCode(), state, isFocused, isPressedTop);
	}

	@Override
	public @Nonnull
	String toString()
	{
		String fs = isFocused ? " focused" : "";
		String ts = state == State.PRESSED ? (isPressedTop ? "-Top" : "-Bottom") : "";
		return super.toString() + " " + state + ts + fs;
	}
}
