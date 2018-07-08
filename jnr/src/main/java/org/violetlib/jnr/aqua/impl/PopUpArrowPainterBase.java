/*
 * Copyright (c) 2015-2016 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

package org.violetlib.jnr.aqua.impl;

import java.awt.Color;

import javax.annotation.Nonnull;

import org.violetlib.jnr.aqua.AquaUIPainter.PopupButtonWidget;
import org.violetlib.jnr.aqua.AquaUIPainter.State;
import org.violetlib.jnr.aqua.PopupButtonConfiguration;

/**

*/

public abstract class PopUpArrowPainterBase
{
	protected @Nonnull
	Color ACTIVE_COLOR = new Color(7, 7, 7, 150);
	protected @Nonnull
	Color DISABLED_COLOR = new Color(0, 0, 0, 64);

	protected final @Nonnull
	PopupButtonConfiguration gg;

	public PopUpArrowPainterBase(@Nonnull PopupButtonConfiguration gg)
	{
		this.gg = gg;
	}

	protected @Nonnull
	Color getColor()
	{
		State st = gg.getState();
		PopupButtonWidget w = gg.getPopupButtonWidget();

		if ((st == State.ROLLOVER || st == State.PRESSED)
			&& (w == PopupButtonWidget.BUTTON_POP_UP_RECESSED || w == PopupButtonWidget.BUTTON_POP_DOWN_RECESSED)) {
			return Color.WHITE;
		}

		return st == State.DISABLED || st == State.DISABLED_INACTIVE ? DISABLED_COLOR : ACTIVE_COLOR;
	}
}
