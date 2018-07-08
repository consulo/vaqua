/*
 * Copyright (c) 2015 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

package org.violetlib.jnr.aqua.impl;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import org.violetlib.jnr.impl.CombinedInsetter;
import org.violetlib.jnr.impl.FloatingInsetter1;
import org.violetlib.jnr.Insetter;
import org.violetlib.jnr.impl.Insetter1;
import org.violetlib.jnr.aqua.TitleBarLayoutConfiguration;
import org.violetlib.jnr.impl.Insetters;

import static org.violetlib.jnr.aqua.AquaUIPainter.*;
import static org.violetlib.jnr.aqua.AquaUIPainter.TitleBarButtonWidget.*;

import javax.annotation.*;

/**
	Layout information for a title bar.
*/

public class TitleBarLayoutInfo
{
	private static final int BUTTON_AREA_RIGHT_PAD = 11;
	private static final int DOCUMENT_HEIGHT = 22;
	private static final int UTILITY_HEIGHT = 16;
	private static final int DOCUMENT_DIAMETER = 12;
	private static final int UTILITY_DIAMETER = 11;

	private final @Nonnull
	WindowInfo[] windowInfo;

	public TitleBarLayoutInfo(@Nonnull Rectangle[] documentButtonBounds, @Nonnull Rectangle[] utilityButtonBounds)
	{
		windowInfo = new WindowInfo[2];
		windowInfo[0] = new WindowInfo(DOCUMENT_HEIGHT, documentButtonBounds, DOCUMENT_DIAMETER);
		windowInfo[1] = new WindowInfo(UTILITY_HEIGHT, utilityButtonBounds, UTILITY_DIAMETER);
	}

	public @Nonnull
	Insetter getButtonInsets(@Nonnull TitleBarLayoutConfiguration g, @Nonnull TitleBarButtonWidget bw)
	{
		ButtonInfo buttonInfo = getButtonInfo(g.getWidget(), bw);
		return buttonInfo.insets;
	}

	public @Nonnull
	Shape getButtonShape(@Nonnull Rectangle2D bounds,
																			 @Nonnull TitleBarLayoutConfiguration g,
																			 @Nonnull TitleBarButtonWidget bw)
	{
		/*
			Because the title bar buttons are aligned top left, the title bar width and height do not affect the locations of
			the buttons. The origin is always zero.
		*/

		ButtonInfo buttonInfo = getButtonInfo(g.getWidget(), bw);
		return buttonInfo.shape;
	}

	public @Nonnull
	Insetter getLabelInsets(@Nonnull TitleBarLayoutConfiguration g)
	{
		WindowInfo w = getWindowInfo(g.getWidget());
		return w.labelInsets;
	}

	public @Nullable
	TitleBarButtonWidget identifyButton(@Nonnull Rectangle2D bounds,
																											 @Nonnull TitleBarLayoutConfiguration g, int x, int y)
	{
		WindowInfo w = getWindowInfo(g.getWidget());
		for (int i = 0; i < w.buttonInfo.length; i++) {
			ButtonInfo buttonInfo = w.buttonInfo[i];
			Rectangle bb = buttonInfo.bounds;
			if (bb.contains(x, y)) {
				return fromButtonIndex(i);
			}
		}
		return null;
	}

	protected @Nonnull
	WindowInfo getWindowInfo(@Nonnull TitleBarWidget w)
	{
		int index = toWindowTypeIndex(w);
		return windowInfo[index];
	}

	protected @Nonnull
	ButtonInfo getButtonInfo(@Nonnull TitleBarWidget w, @Nonnull TitleBarButtonWidget bw)
	{
		WindowInfo windowInfo = getWindowInfo(w);
		int index = toButtonIndex(bw);
		return windowInfo.buttonInfo[index];
	}

	public static int toWindowTypeIndex(@Nonnull TitleBarWidget w)
	{
		switch (w) {
			case DOCUMENT_WINDOW:
				return 0;
			case UTILITY_WINDOW:
				return 1;
			default:
				throw new UnsupportedOperationException();
		}
	}

	public static int toButtonIndex(@Nonnull TitleBarButtonWidget bw)
	{
		switch (bw) {
			case CLOSE_BOX:
				return 0;
			case MINIMIZE_BOX:
				return 1;
			case RESIZE_BOX:
				return 2;
			default:
				throw new UnsupportedOperationException();
		}
	}

	public static @Nonnull
	TitleBarButtonWidget fromButtonIndex(int index)
	{
		switch (index) {
			case 0:
				return CLOSE_BOX;
			case 1:
				return MINIMIZE_BOX;
			case 2:
				return RESIZE_BOX;
			default:
				throw new UnsupportedOperationException();
		}
	}

	private static class WindowInfo
	{
		final float height;
		final float buttonAreaWidth;
		final @Nonnull
		ButtonInfo[] buttonInfo;
		final @Nonnull
		Insetter labelInsets;

		public WindowInfo(float height, @Nonnull Rectangle[] buttonBounds, float buttonDiameter)
		{
			int buttonCount = buttonBounds.length;
			Rectangle lastBounds = buttonBounds[buttonCount-1];

			this.height = height;
			this.buttonAreaWidth = lastBounds.x + lastBounds.width + BUTTON_AREA_RIGHT_PAD;
			this.buttonInfo = new ButtonInfo[buttonCount];
			for (int i = 0; i < buttonCount; i++) {
				buttonInfo[i] = new ButtonInfo(buttonBounds[i], buttonDiameter);
			}
			labelInsets = Insetters.createFixed(1, buttonAreaWidth, 1, 1);
		}
	}

	private static class ButtonInfo
	{
		final @Nonnull
		Rectangle bounds;
		final @Nonnull
		Shape shape;
		final @Nonnull
		Insetter insets;

		public ButtonInfo(@Nonnull Rectangle bounds, float diameter)
		{
			this.bounds = bounds;
			this.shape = createButtonShape(bounds, diameter);
			Insetter1 horizontal = FloatingInsetter1.createLeftTopAligned(bounds.width, bounds.x);
			Insetter1 vertical = FloatingInsetter1.createLeftTopAligned(bounds.height, bounds.y);
			insets = new CombinedInsetter(horizontal, vertical);
		}
	}

	private static @Nonnull
	Shape createButtonShape(@Nonnull Rectangle bounds, float diameter)
	{
		float radius = diameter / 2;
		float x = (float) (bounds.x + bounds.width / 2.0 - radius);
		float y = (float) (bounds.y + bounds.height / 2.0 - radius);
		return new Ellipse2D.Float(x, y, diameter, diameter);
	}
}
