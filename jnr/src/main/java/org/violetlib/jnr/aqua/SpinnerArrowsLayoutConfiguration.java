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

/**
	A layout configuration for spinner arrows.
*/

public class SpinnerArrowsLayoutConfiguration
	extends LayoutConfiguration
{
	private final @Nonnull
	Size size;

	public SpinnerArrowsLayoutConfiguration(@Nonnull Size size)
	{
		this.size = size;
	}

	public @Nonnull
	Size getSize()
	{
		return size;
	}

	@Override
	public boolean equals(@Nullable Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SpinnerArrowsLayoutConfiguration that = (SpinnerArrowsLayoutConfiguration) o;
		return size == that.size;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(size);
	}

	@Override
	public @Nonnull
	String toString()
	{
		return "Spinner Arrows " + size;
	}
}
