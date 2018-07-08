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

import org.violetlib.jnr.aqua.AquaUIPainter.SegmentedButtonWidget;
import org.violetlib.jnr.aqua.AquaUIPainter.Size;
import org.violetlib.jnr.aqua.AquaUIPainter.Position;

/**
	A layout configuration for a segmented button.
*/

public class SegmentedButtonLayoutConfiguration
	extends LayoutConfiguration
{
	private final @Nonnull
	SegmentedButtonWidget bw;
	private final @Nonnull
	Size size;
	private final @Nonnull
	Position position;

	public SegmentedButtonLayoutConfiguration(@Nonnull SegmentedButtonWidget bw,
																						@Nonnull Size size,
																						@Nonnull Position position)
	{
		this.bw = bw;
		this.size = size;
		this.position = position;
	}

	public @Nonnull
	SegmentedButtonWidget getWidget()
	{
		return bw;
	}

	public @Nonnull
	Size getSize()
	{
		return size;
	}

	public @Nonnull
	Position getPosition()
	{
		return position;
	}

	@Override
	public boolean equals(@Nullable Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SegmentedButtonLayoutConfiguration that = (SegmentedButtonLayoutConfiguration) o;
		return bw == that.bw && size == that.size && position == that.position;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(bw, size, position);
	}

	@Override
	public @Nonnull
	String toString()
	{
		return bw + " " + size + " " + position;
	}
}
