/*
 * Copyright (c) 2015-2018 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

package org.violetlib.jnr.aqua.impl;

import java.awt.Color;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.violetlib.jnr.aqua.AquaUIPainter.PopupButtonWidget;
import org.violetlib.jnr.aqua.AquaUIPainter.State;
import org.violetlib.jnr.aqua.PopupButtonConfiguration;
import org.violetlib.vappearances.VAppearance;

/**

*/

public abstract class PopUpArrowPainterBase
{
	private @Nonnull
	Color ACTIVE_COLOR = new Color(7, 7, 7, 150);
	private @Nonnull
	Color DISABLED_COLOR = new Color(0, 0, 0, 64);

	protected final @Nonnull
	PopupButtonConfiguration gg;
	protected final @Nonnull
	Color color;

	public PopUpArrowPainterBase(@Nonnull PopupButtonConfiguration gg, @Nullable VAppearance appearance)
	{
		this.gg = gg;

		State st = gg.getState();
		boolean isDark = appearance != null && appearance.isDark();
		boolean isDisabled = st == State.DISABLED || st == State.DISABLED_INACTIVE;
		PopupButtonWidget w = gg.getPopupButtonWidget();

		if (!isDark
					&& (st == State.ROLLOVER || st == State.PRESSED)
					&& (w == PopupButtonWidget.BUTTON_POP_UP_RECESSED || w == PopupButtonWidget.BUTTON_POP_DOWN_RECESSED)) {
			color = Color.WHITE;
		} else if (appearance != null) {
			color = appearance.getColors().get(isDisabled ? "disabledControlText" : "controlText");
		} else {
			color = isDisabled ? DISABLED_COLOR : ACTIVE_COLOR;
		}
	}
}
