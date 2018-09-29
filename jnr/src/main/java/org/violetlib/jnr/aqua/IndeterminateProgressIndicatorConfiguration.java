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

import org.violetlib.jnr.aqua.AquaUIPainter.Orientation;
import org.violetlib.jnr.aqua.AquaUIPainter.ProgressWidget;
import org.violetlib.jnr.aqua.AquaUIPainter.Size;
import org.violetlib.jnr.aqua.AquaUIPainter.State;

/**
	A configuration for an indeterminate progress indicator.
*/

public class IndeterminateProgressIndicatorConfiguration
	extends ProgressIndicatorLayoutConfiguration
	implements Configuration
{
	private final @Nonnull
	State state;
	private final int animationFrame;

	public IndeterminateProgressIndicatorConfiguration(@Nonnull ProgressWidget pw,
																										 @Nonnull Size sz,
																										 @Nonnull State state,
																										 @Nonnull Orientation o,
																										 int animationFrame)
	{
		super(pw, sz, o);

		this.state = state;
		this.animationFrame = animationFrame;
	}

	public @Nonnull
	State getState()
	{
		return state;
	}

	public int getAnimationFrame()
	{
		return animationFrame;
	}

	@Override
	public boolean equals(@Nullable Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		IndeterminateProgressIndicatorConfiguration that = (IndeterminateProgressIndicatorConfiguration) o;
		return animationFrame == that.animationFrame && state == that.state;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(super.hashCode(), state, animationFrame);
	}

	@Override
	public @Nonnull
	String toString()
	{
		String fs = animationFrame > 0 ? " " + animationFrame : "";
		return super.toString() + " " + state + fs;
	}
}
