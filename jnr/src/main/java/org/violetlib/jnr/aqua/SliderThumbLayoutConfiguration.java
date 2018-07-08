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

import org.violetlib.jnr.impl.JNRUtils;

/**
	A layout configuration for a slider thumb.
*/

public class SliderThumbLayoutConfiguration
	extends SliderLayoutConfiguration
{
	private final double thumbPosition;

	public SliderThumbLayoutConfiguration(@Nonnull SliderLayoutConfiguration g, double thumbPosition)
	{
		super(g);

		this.thumbPosition = thumbPosition;
	}

	public @Nonnull
	SliderLayoutConfiguration getSliderLayoutConfiguration()
	{
		return this;
	}

	public double getThumbPosition()
	{
		return thumbPosition;
	}

	@Override
	public boolean equals(@Nullable Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		SliderThumbLayoutConfiguration that = (SliderThumbLayoutConfiguration) o;
		return Objects.equals(thumbPosition, that.thumbPosition);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(super.hashCode(), thumbPosition);
	}

	@Override
	public @Nonnull
	String toString()
	{
		return super.toString() + " " + JNRUtils.format2(thumbPosition);
	}
}
