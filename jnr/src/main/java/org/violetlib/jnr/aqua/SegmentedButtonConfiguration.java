/*
 * Copyright (c) 2015-2016 Alan Snyder.
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
import org.violetlib.jnr.aqua.AquaUIPainter.State;
import org.violetlib.jnr.aqua.AquaUIPainter.Direction;

/**
	A configuration for a segmented button.

	<p>
	The painting model for segmented buttons follows the Java model. The buttons are abutted with no overlap. For each
	divider between two buttons, the client code decides which button owns the divider and sets the leftDividerState
	and rightDividerState parameters appropriately. The divider is painted in the space allocated to the button that
	owns the divider.
	</p>

	<p>
	Because Java normally does not know which buttons are part of a segmented control, the normal policy is for the button
	on the left to own the divider. The divider state is set to selected if that button is selected.
	</p>

	<p>
	Code that knows about the buttons in a segmented control can get slightly better results by having the selected button
	own the dividers on either side, buttons to the left of the selected button own the dividers to their left, and
	buttons to the right of the selected button own the dividers to their right.
	</p>

	<p>
	We currently assume that writing direction does not affect the appearance of the buttons and dividers, so that left
	always means left or top and right always means right or bottom.
	</p>
*/

public class SegmentedButtonConfiguration
	extends SegmentedButtonLayoutConfiguration
	implements Configuration
{
	private final @Nonnull
	State state;
	private final boolean isSelected;
	private final boolean isFocused;
	private final @Nonnull
	Direction d;	// the direction that the "top" of the button faces
	private final @Nonnull
	DividerState leftDividerState;
	private final @Nonnull
	DividerState rightDividerState;

	/**
		The display configuration of a divider between two segments in a segmented control.
	*/

	public enum DividerState
	{
		NONE,
		ORDINARY,
		SELECTED
	}

	public SegmentedButtonConfiguration(@Nonnull SegmentedButtonWidget bw,
																			@Nonnull Size size,
																			@Nonnull State state,
																			boolean isSelected,
																			boolean isFocused,
																			@Nonnull Direction d,
																			@Nonnull Position position,
																			@Nonnull DividerState leftDividerState,
																			@Nonnull DividerState rightDividerState)
	{
		super(bw, size, position);

		this.state = state;
		this.isSelected = isSelected;
		this.isFocused = isFocused;
		this.d = d;
		this.leftDividerState = leftDividerState;
		this.rightDividerState = rightDividerState;
	}

	public SegmentedButtonConfiguration(@Nonnull SegmentedButtonLayoutConfiguration g,
																			@Nonnull State state,
																			boolean isSelected,
																			boolean isFocused,
																			@Nonnull Direction d,
																			@Nonnull DividerState leftDividerState,
																			@Nonnull DividerState rightDividerState)
	{
		this(g.getWidget(), g.getSize(), state, isSelected, isFocused, d, g.getPosition(), leftDividerState, rightDividerState);
	}

	public @Nonnull
	SegmentedButtonConfiguration withWidget(@Nonnull SegmentedButtonWidget widget)
	{
		return new SegmentedButtonConfiguration(widget,
			getSize(), state, isSelected, isFocused, d, getPosition(), leftDividerState, rightDividerState);
	}

	public @Nonnull
	State getState()
	{
		return state;
	}

	public boolean isSelected()
	{
		return isSelected;
	}

	public boolean isFocused()
	{
		return isFocused;
	}

	public @Nonnull
	Direction getDirection()
	{
		return d;
	}

	public @Nonnull
	DividerState getLeftDividerState()
	{
		return leftDividerState;
	}

	public @Nonnull
	DividerState getRightDividerState()
	{
		return rightDividerState;
	}

	@Override
	public boolean equals(@Nullable Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		SegmentedButtonConfiguration that = (SegmentedButtonConfiguration) o;
		return state == that.state
			&& isSelected == that.isSelected
			&& isFocused == that.isFocused
			&& d == that.d
			&& leftDividerState == that.leftDividerState
			&& rightDividerState == that.rightDividerState;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(super.hashCode(), state, isSelected, isFocused, d, leftDividerState, rightDividerState);
	}

	@Override
	public @Nonnull
	String toString()
	{
		String fs = isFocused ? " focused" : "";
		String ss = isSelected ? "S" : "-";
		String ls = leftDividerState == DividerState.NONE ? "" : leftDividerState == DividerState.ORDINARY ? "<" : "[";
		String rs = rightDividerState == DividerState.NONE ? "" : rightDividerState == DividerState.ORDINARY ? ">" : "]";
		return super.toString() + " " + d + " " + state + fs + " " + ls + ss + rs;
	}
}
