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

import org.violetlib.jnr.aqua.AquaUIPainter.ScrollBarWidget;
import org.violetlib.jnr.aqua.AquaUIPainter.Size;
import org.violetlib.jnr.aqua.AquaUIPainter.Orientation;

/**
	A layout configuration for a scroll bar.
*/

public class ScrollBarLayoutConfiguration
	extends LayoutConfiguration
{
	private final @Nonnull
	ScrollBarWidget bw;
	private final @Nonnull
	Size size;
	private final @Nonnull
	Orientation o;

	public ScrollBarLayoutConfiguration(@Nonnull ScrollBarWidget bw, @Nonnull Size size, @Nonnull Orientation o)
	{
		this.bw = bw;
		this.size = size;
		this.o = o;
	}

	protected ScrollBarLayoutConfiguration(@Nonnull ScrollBarLayoutConfiguration g)
	{
		this.bw = g.getWidget();
		this.size = g.getSize();
		this.o = g.getOrientation();
	}

	public @Nonnull
	ScrollBarWidget getWidget()
	{
		return bw;
	}

	public @Nonnull
	Size getSize()
	{
		return size;
	}

	public @Nonnull
	Orientation getOrientation()
	{
		return o;
	}

	@Override
	public boolean equals(@Nullable Object o1)
	{
		if (this == o1) return true;
		if (o1 == null || getClass() != o1.getClass()) return false;
		ScrollBarLayoutConfiguration that = (ScrollBarLayoutConfiguration) o1;
		return bw == that.bw && size == that.size && o == that.o;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(bw, size, o);
	}

	@Override
	public @Nonnull
	String toString()
	{
		return bw + " " + size + " " + o;
	}
}
