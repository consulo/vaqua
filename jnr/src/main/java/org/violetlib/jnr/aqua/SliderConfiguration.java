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

import org.violetlib.jnr.aqua.AquaUIPainter.Size;
import org.violetlib.jnr.aqua.AquaUIPainter.SliderWidget;
import org.violetlib.jnr.aqua.AquaUIPainter.State;
import org.violetlib.jnr.aqua.AquaUIPainter.TickMarkPosition;
import org.violetlib.jnr.impl.JNRUtils;

/**
	A configuration for a slider.
*/

public class SliderConfiguration
	extends SliderLayoutConfiguration
	implements Configuration
{
	private final @Nonnull
	State state;
	private final boolean isFocused;
	private final double value;

	public SliderConfiguration(@Nonnull SliderWidget sw,
														 @Nonnull Size sz,
														 @Nonnull State state,
														 boolean isFocused,
														 double value,
														 int numberOfTickMarks,
														 @Nonnull TickMarkPosition position
														 )
	{
		super(sw, sz, numberOfTickMarks, position);

		this.state = state;
		this.isFocused = isFocused;
		this.value = value;
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

	public double getValue()
	{
		return value;
	}

	@Override
	public boolean equals(@Nullable Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		SliderConfiguration that = (SliderConfiguration) o;
		return state == that.state && isFocused == that.isFocused && value == that.value;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(super.hashCode(), state, isFocused, value);
	}

	@Override
	public @Nonnull
	String toString()
	{
		String fs = isFocused ? " focused" : "";
		return super.toString() + " " + state + fs + " " + JNRUtils.format2(value);
	}
}
