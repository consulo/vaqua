/*
 * Copyright (c) 2015-2018 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

package org.violetlib.jnr.aqua;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.violetlib.jnr.aqua.AquaUIPainter.State;
import org.violetlib.jnr.aqua.AquaUIPainter.TitleBarWidget;

/**
	A configuration for a title bar.
*/

public class TitleBarConfiguration
	extends TitleBarLayoutConfiguration
	implements Configuration
{
	private final @Nonnull
	State titleBarState;
	private final @Nonnull
	State closeButtonState;
	private final @Nonnull
	State minimizeButtonState;
	private final @Nonnull
	State resizeButtonState;
	private final @Nonnull
	ResizeAction resizeAction;
	private final boolean isDirty;

	/**
		The possible actions corresponding to the resize button on a title bar.
	*/
	public enum ResizeAction
	{
		FULL_SCREEN_ENTER,
		FULL_SCREEN_EXIT,
		ZOOM_ENTER,
		ZOOM_EXIT
	}

	public TitleBarConfiguration(@Nonnull TitleBarWidget tw,
															 @Nonnull State titleBarState,
															 @Nonnull State closeButtonState,
															 @Nonnull State minimizeButtonState,
															 @Nonnull State resizeButtonState,
															 @Nonnull ResizeAction resizeAction,
															 boolean isDirty)
	{
		super(tw);

		/*
			The title bar has only two rendering states: active and inactive.
			The buttons have only four (possible) rendering states: active, inactive, pressed, and rollover.
		  The close button cannot be disabled; therefore, when the title bar is active, the close button cannot be inactive.
		  When the title bar is inactive, all button states are permitted. This allows the buttons to become active
		  when the mouse is over the button area.
		  Full screen mode is not supported for utility windows.
		*/

		boolean isActive = titleBarState != State.INACTIVE && titleBarState != State.DISABLED && titleBarState != State.DISABLED_INACTIVE;
		titleBarState = isActive ? State.ACTIVE : State.INACTIVE;
		if (isActive && closeButtonState == State.INACTIVE) {
			closeButtonState = State.ACTIVE;
		}
		if (tw != TitleBarWidget.DOCUMENT_WINDOW) {
			if (resizeAction == ResizeAction.FULL_SCREEN_ENTER) {
				resizeAction = ResizeAction.ZOOM_ENTER;
			} else if (resizeAction == ResizeAction.FULL_SCREEN_EXIT) {
				resizeAction = ResizeAction.ZOOM_EXIT;
			}
		}

		this.titleBarState = titleBarState;
		this.closeButtonState = closeButtonState;
		this.minimizeButtonState = minimizeButtonState;
		this.resizeButtonState = resizeButtonState;
		this.resizeAction = resizeAction;
		this.isDirty = isDirty;
	}

	public @Nonnull
	State getTitleBarState()
	{
		return titleBarState;
	}

	public @Nonnull
	State getCloseButtonState()
	{
		return closeButtonState;
	}

	public @Nonnull
	State getMinimizeButtonState()
	{
		return minimizeButtonState;
	}

	public @Nonnull
	State getResizeButtonState()
	{
		return resizeButtonState;
	}

	public @Nonnull
	ResizeAction getResizeAction()
	{
		return resizeAction;
	}

	public boolean isDirty()
	{
		return isDirty;
	}

	@Override
	public boolean equals(@Nullable Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		TitleBarConfiguration that = (TitleBarConfiguration) o;
		return titleBarState == that.titleBarState
						 && closeButtonState == that.closeButtonState
						 && minimizeButtonState == that.minimizeButtonState
						 && resizeButtonState == that.resizeButtonState
						 && resizeAction == that.resizeAction
						 && isDirty == that.isDirty;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(super.hashCode(), titleBarState, closeButtonState, minimizeButtonState, resizeButtonState, resizeAction, isDirty);
	}

	@Override
	public @Nonnull
	String toString()
	{
		String ds = isDirty ? " dirty" : "";
		return super.toString() + " " + titleBarState + ds + " close:" + closeButtonState + " minimize:" + minimizeButtonState
						 + " resize:" + resizeButtonState + " " + resizeAction;
	}
}
