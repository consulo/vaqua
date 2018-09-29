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

/**
	A layout configuration for a scroll bar thumb.
*/

public class ScrollBarThumbLayoutConfiguration
	extends ScrollBarLayoutConfiguration
{
	private final int value;
	private final float thumbExtent;

	public ScrollBarThumbLayoutConfiguration(@Nonnull ScrollBarConfiguration g, int value)
	{
		super(g);

		this.value = value;
		this.thumbExtent = g.getThumbExtent();
	}

	public ScrollBarThumbLayoutConfiguration(@Nonnull AquaUIPainter.ScrollBarWidget bw,
																					 @Nonnull AquaUIPainter.Size size,
																					 @Nonnull AquaUIPainter.Orientation o,
																					 float thumbExtent,
																					 int value)
	{
		super(bw, size, o);

		this.value = value;
		this.thumbExtent = thumbExtent;
	}

	public int getValue()
	{
		return value;
	}

	public float getThumbExtent()
	{
		return thumbExtent;
	}

	@Override
	public boolean equals(@Nullable Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		ScrollBarThumbLayoutConfiguration that = (ScrollBarThumbLayoutConfiguration) o;
		return value == that.value;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(super.hashCode(), value);
	}

	@Override
	public @Nonnull
	String toString()
	{
		return super.toString() + " " + value;
	}
}
