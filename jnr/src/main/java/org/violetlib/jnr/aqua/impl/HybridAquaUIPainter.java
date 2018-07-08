/*
 * Copyright (c) 2015-2018 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

package org.violetlib.jnr.aqua.impl;

import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import javax.annotation.*;

import org.violetlib.jnr.Painter;
import org.violetlib.jnr.aqua.*;
import org.violetlib.jnr.impl.JNRPlatformUtils;

/**
	A hybrid painter that uses the best available implementation for each given configuration.
*/

public class HybridAquaUIPainter
	implements AquaUIPainter
{
	protected final @Nonnull
	AquaUIPainter viewPainter;
	protected final @Nonnull
	AquaUIPainter coreUIPainter;
	protected final @Nullable
	AquaUIPainter jrsPainter;

	private final @Nonnull
	AquaUILayoutInfo layout;
	private int w;
	private int h;

	public HybridAquaUIPainter(@Nonnull AquaUIPainter viewPainter,
														 @Nonnull AquaUIPainter coreUIPainter,
														 @Nullable AquaUIPainter jrsPainter)
	{
		this.viewPainter = viewPainter;
		this.coreUIPainter = coreUIPainter;
		this.jrsPainter = jrsPainter;

		layout = viewPainter.getLayoutInfo();	// all implementations share the same layout
	}

	@Override
	public @Nonnull
	HybridAquaUIPainter copy()
	{
		return new HybridAquaUIPainter(viewPainter, coreUIPainter, jrsPainter);
	}

	@Override
	public void configure(int w, int h)
	{
		this.w = w;
		this.h = h;
	}

	@Override
	public @Nonnull
	Painter getPainter(@Nonnull Configuration g)
		throws UnsupportedOperationException
	{
		AquaUIPainter p = select(g);
		p.configure(w, h);
		return p.getPainter(g);
	}

	protected @Nonnull
	AquaUIPainter select(@Nonnull Configuration g)
	{
		// Prefer the JSR painter if defined because it is faster, except where it is not accurate.
		// Otherwise the core UI painter except where it falls down and the view painter is better.

		if (g instanceof ButtonConfiguration) {
			ButtonConfiguration bg = (ButtonConfiguration) g;
			ButtonWidget bw = bg.getButtonWidget();
			if (bw == ButtonWidget.BUTTON_INLINE) {
				return viewPainter;
			} else {
				return coreUIPainter;
			}
		} else if (g instanceof SegmentedButtonConfiguration) {
			return coreUIPainter;
		} else if (g instanceof GradientConfiguration) {
			return coreUIPainter;
		} else if (g instanceof ComboBoxConfiguration) {
			ComboBoxConfiguration bg = (ComboBoxConfiguration) g;
			ComboBoxWidget w = bg.getWidget();
			State st = bg.getState();
			Size sz = bg.getSize();

			// On 10.11 and earlier, all renderers paint proper cell style arrows, except JDK is unable to paint the mini
			// size. On 10.12 and later, the cell style arrows have changed to a "V" shape, but JDK still uses the triangle
			// version.

			if (w == ComboBoxWidget.BUTTON_COMBO_BOX_CELL) {
				int platformVersion = JNRPlatformUtils.getPlatformVersion();
				if (platformVersion >= 101200 || sz == Size.MINI) {
					return coreUIPainter;
				}
			} else if (st == State.DISABLED
				|| st == State.DISABLED_INACTIVE
				|| bg.getLayoutDirection() == UILayoutDirection.RIGHT_TO_LEFT
				|| w == ComboBoxWidget.BUTTON_COMBO_BOX_TEXTURED
				|| w == ComboBoxWidget.BUTTON_COMBO_BOX_TEXTURED_TOOLBAR
				) {
				return coreUIPainter;
			}
		} else if (g instanceof PopupButtonConfiguration) {
			PopupButtonConfiguration bg = (PopupButtonConfiguration) g;
			if (bg.getLayoutDirection() == UILayoutDirection.RIGHT_TO_LEFT) {
				return coreUIPainter;
			}
			PopupButtonWidget widget = bg.getPopupButtonWidget();
			if (widget == PopupButtonWidget.BUTTON_POP_UP_TEXTURED
				|| widget == PopupButtonWidget.BUTTON_POP_DOWN_TEXTURED
				|| widget == PopupButtonWidget.BUTTON_POP_UP_TEXTURED_TOOLBAR
				|| widget == PopupButtonWidget.BUTTON_POP_DOWN_TEXTURED_TOOLBAR) {
				return coreUIPainter;
			}
		} else if (g instanceof ProgressIndicatorConfiguration) {
			ProgressIndicatorConfiguration bg = (ProgressIndicatorConfiguration) g;
			if (bg.getWidget() == ProgressWidget.BAR && bg.getOrientation() == Orientation.HORIZONTAL) {
				return coreUIPainter;
			}
		} else if (g instanceof IndeterminateProgressIndicatorConfiguration) {
			IndeterminateProgressIndicatorConfiguration bg = (IndeterminateProgressIndicatorConfiguration) g;
			return coreUIPainter;
		} else if (g instanceof TextFieldConfiguration) {
			TextFieldConfiguration bg = (TextFieldConfiguration) g;
			TextFieldWidget w = bg.getWidget();
			if (w != TextFieldWidget.TEXT_FIELD && w != TextFieldWidget.TEXT_FIELD_ROUND) {
				return coreUIPainter;
			}
		} else if (g instanceof SliderConfiguration) {
			SliderConfiguration bg = (SliderConfiguration) g;
			if (!bg.hasTickMarks()) {
				return coreUIPainter;
			}
		} else if (g instanceof TitleBarConfiguration) {
			return coreUIPainter;
		} else if (g instanceof ScrollBarConfiguration) {
			return coreUIPainter;
		}

		return jrsPainter != null ? jrsPainter : coreUIPainter;
	}

	@Override
	public @Nonnull
	AquaUILayoutInfo getLayoutInfo()
	{
		return layout;
	}

	@Override
	public @Nullable
	Shape getOutline(@Nonnull LayoutConfiguration g)
	{
		viewPainter.configure(w, h);
		return viewPainter.getOutline(g);
	}

	@Override
	public @Nonnull
	Rectangle2D getComboBoxEditorBounds(@Nonnull ComboBoxLayoutConfiguration g)
	{
		viewPainter.configure(w, h);
		return viewPainter.getComboBoxEditorBounds(g);
	}

	@Override
	public @Nonnull
	Rectangle2D getComboBoxIndicatorBounds(@Nonnull ComboBoxLayoutConfiguration g)
	{
		viewPainter.configure(w, h);
		return viewPainter.getComboBoxIndicatorBounds(g);
	}

	@Override
	public @Nonnull
	Rectangle2D getPopupButtonContentBounds(@Nonnull PopupButtonLayoutConfiguration g)
	{
		viewPainter.configure(w, h);
		return viewPainter.getPopupButtonContentBounds(g);
	}

	@Override
	public @Nonnull
	Rectangle2D getSliderThumbBounds(@Nonnull SliderLayoutConfiguration g, double thumbPosition)
	{
		viewPainter.configure(w, h);
		return viewPainter.getSliderThumbBounds(g, thumbPosition);
	}

	@Override
	public double getSliderThumbPosition(@Nonnull SliderLayoutConfiguration g, int x, int y)
	{
		viewPainter.configure(w, h);
		return viewPainter.getSliderThumbPosition(g, x, y);
	}

	@Override
	public float getScrollBarThumbPosition(@Nonnull ScrollBarThumbLayoutConfiguration g, boolean useExtent)
	{
		viewPainter.configure(w, h);
		return viewPainter.getScrollBarThumbPosition(g, useExtent);
	}

	@Override
	public int getScrollBarThumbHit(@Nonnull ScrollBarThumbConfiguration g)
	{
		viewPainter.configure(w, h);
		return viewPainter.getScrollBarThumbHit(g);
	}

	@Override
	public @Nonnull
	Rectangle2D getSliderLabelBounds(@Nonnull SliderLayoutConfiguration g,
																									 double thumbPosition,
																									 @Nonnull Dimension size)
	{
		viewPainter.configure(w, h);
		return viewPainter.getSliderLabelBounds(g, thumbPosition, size);
	}

	@Override
	public @Nonnull
	String toString()
	{
		String s = "Hybrid " + viewPainter + "+" + coreUIPainter;
		if (jrsPainter != null) {
			s = s + "+" + jrsPainter;
		}
		return s;
	}
}
