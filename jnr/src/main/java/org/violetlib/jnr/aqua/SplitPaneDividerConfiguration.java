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

import org.violetlib.jnr.aqua.AquaUIPainter.DividerWidget;
import org.violetlib.jnr.aqua.AquaUIPainter.Orientation;
import org.violetlib.jnr.aqua.AquaUIPainter.State;

/**
	A configuration for a split pane divider.
*/

public class SplitPaneDividerConfiguration
	extends SplitPaneDividerLayoutConfiguration
	implements Configuration
{
	private final @Nonnull
	State state;

	public SplitPaneDividerConfiguration(@Nonnull DividerWidget dw,
																			 @Nonnull State state,
																			 @Nonnull Orientation o,
																			 int thickness)
	{
		super(dw, o, thickness);

		this.state = state;
	}

	public @Nonnull
	State getState()
	{
		return state;
	}

	@Override
	public boolean equals(@Nullable Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		SplitPaneDividerConfiguration that = (SplitPaneDividerConfiguration) o;
		return state == that.state;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(super.hashCode(), state);
	}

	@Override
	public @Nonnull
	String toString()
	{
		return super.toString() + " " + state;
	}
}
