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

import org.violetlib.jnr.aqua.AquaUIPainter.ButtonWidget;
import org.violetlib.jnr.aqua.AquaUIPainter.Size;
import org.violetlib.jnr.aqua.AquaUIPainter.State;
import org.violetlib.jnr.aqua.AquaUIPainter.ButtonState;
import org.violetlib.jnr.aqua.AquaUIPainter.UILayoutDirection;

/**
	A configuration for a button that supports an animated transition from one button state to another.
*/

public class AnimatedButtonConfiguration
	extends ButtonConfiguration
{
	private final @Nonnull
	ButtonState previousButtonState;
	private final float transition;

	public AnimatedButtonConfiguration(@Nonnull ButtonWidget bw,
																		 @Nonnull Size size,
																		 @Nonnull State state,
																		 boolean isFocused,
																		 @Nonnull ButtonState buttonState,
																		 @Nonnull UILayoutDirection ld,
																		 @Nonnull ButtonState previousButtonState,
																		 float transition)
	{
		super(bw, size, state, isFocused, buttonState, ld);

		if (transition < 0 || transition > 1) {
			throw new IllegalArgumentException("Invalid transition");
		}

		this.transition = transition;
		this.previousButtonState = previousButtonState;
	}

	public @Nonnull
	ButtonState getPreviousButtonState()
	{
		return previousButtonState;
	}

	public float getTransition()
	{
		return transition;
	}

	@Override
	public boolean equals(@Nullable Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		AnimatedButtonConfiguration that = (AnimatedButtonConfiguration) o;
		return transition == that.transition && previousButtonState == that.previousButtonState;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(super.hashCode(), previousButtonState, transition);
	}
}
