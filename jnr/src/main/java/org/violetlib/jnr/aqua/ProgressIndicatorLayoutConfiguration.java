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

import org.violetlib.jnr.aqua.AquaUIPainter.ProgressWidget;
import org.violetlib.jnr.aqua.AquaUIPainter.Size;
import org.violetlib.jnr.aqua.AquaUIPainter.Orientation;

/**
	A layout configuration for a progress indicator.
*/

public class ProgressIndicatorLayoutConfiguration
	extends LayoutConfiguration
{
	private final @Nonnull
	ProgressWidget pw;
	private final @Nonnull
	Size size;
	private final @Nonnull
	Orientation o;

	public ProgressIndicatorLayoutConfiguration(@Nonnull ProgressWidget pw,
																							@Nonnull Size size,
																							@Nonnull Orientation o)
	{
		// progress bars have only one size
		this.size = pw == ProgressWidget.SPINNER ? size : Size.REGULAR;
		this.o = o;
		this.pw = pw;
	}

	protected ProgressIndicatorLayoutConfiguration(@Nonnull ProgressIndicatorLayoutConfiguration g)
	{
		this.size = g.getSize();
		this.o = g.getOrientation();
		this.pw = g.getWidget();
	}

	public @Nonnull
	ProgressWidget getWidget()
	{
		return pw;
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
		ProgressIndicatorLayoutConfiguration that = (ProgressIndicatorLayoutConfiguration) o1;
		return pw == that.pw && size == that.size && o == that.o;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(pw, size, o);
	}

	@Override
	public @Nonnull
	String toString()
	{
		return pw + " " + size + " " + o;
	}
}
