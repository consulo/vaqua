/*
 * Copyright (c) 2015-2018 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

package org.violetlib.jnr.aqua.jrs;

import javax.annotation.*;

import org.violetlib.jnr.aqua.ComboBoxConfiguration;
import org.violetlib.jnr.aqua.PopupButtonConfiguration;
import org.violetlib.jnr.aqua.ScrollBarConfiguration;
import org.violetlib.jnr.aqua.SliderConfiguration;
import org.violetlib.jnr.aqua.SplitPaneDividerConfiguration;
import org.violetlib.jnr.aqua.TableColumnHeaderConfiguration;
import org.violetlib.jnr.aqua.impl.CircularSliderPainterExtension;
import org.violetlib.jnr.aqua.impl.ComboBoxButtonCellPainterExtension;
import org.violetlib.jnr.aqua.impl.LinearSliderPainterExtension;
import org.violetlib.jnr.aqua.impl.OverlayScrollBarPainterExtension;
import org.violetlib.jnr.aqua.impl.PopUpArrowPainter;
import org.violetlib.jnr.aqua.impl.PullDownArrowPainter;
import org.violetlib.jnr.aqua.impl.TableColumnHeaderCellPainterExtension;
import org.violetlib.jnr.aqua.impl.ThickSplitPaneDividerPainterExtension;
import org.violetlib.jnr.aqua.impl.ThinSplitPaneDividerPainterExtension;
import org.violetlib.jnr.impl.PainterExtension;
import org.violetlib.jnr.impl.Renderer;

/**
	This class augments the JRS native painting code to work around its deficiencies.
*/

public class AugmentedJRSPainter
	extends JRSPainter
{
	@Override
	public @Nonnull
	AugmentedJRSPainter copy()
	{
		return new AugmentedJRSPainter();
	}

	@Override
	public @Nonnull
	Renderer getTableColumnHeaderRenderer(@Nonnull TableColumnHeaderConfiguration g)
	{
		// Do not use the native renderer. Use our simulation instead.

		PainterExtension px = new TableColumnHeaderCellPainterExtension(g);
		return Renderer.create(px);
	}

	@Override
	protected @Nonnull
	Renderer getSplitPaneDividerRenderer(@Nonnull SplitPaneDividerConfiguration g)
	{
		if (g.getWidget() == DividerWidget.THICK_DIVIDER) {
			PainterExtension px = new ThickSplitPaneDividerPainterExtension(g);
			return Renderer.create(px);
		} else if (g.getWidget() == DividerWidget.THIN_DIVIDER) {
			PainterExtension px = new ThinSplitPaneDividerPainterExtension(g);
			return Renderer.create(px);
		} else {
			return super.getSplitPaneDividerRenderer(g);
		}
	}

	@Override
	protected @Nonnull
	Renderer getComboBoxButtonRenderer(@Nonnull ComboBoxConfiguration g)
	{
		ComboBoxWidget bw = g.getWidget();
		if (bw == ComboBoxWidget.BUTTON_COMBO_BOX_CELL) {
			PainterExtension px = new ComboBoxButtonCellPainterExtension(g);
			return Renderer.create(px);
		} else {
			return super.getComboBoxButtonRenderer(g);
		}
	}

	@Override
	public @Nullable
	Renderer getPopupArrowRenderer(@Nonnull PopupButtonConfiguration g)
	{
		Renderer r = super.getPopupArrowRenderer(g);
		if (isArrowNeeded(g)) {
			if (g.isPopUp()) {
				return Renderer.create(new PopUpArrowPainter(g));
			} else {
				return Renderer.create(new PullDownArrowPainter(g));
			}
		}
		return r;
	}

	private boolean isArrowNeeded(@Nonnull PopupButtonConfiguration g)
	{
		PopupButtonWidget w = g.getPopupButtonWidget();

		if (g.isPopUp()) {
			return w == PopupButtonWidget.BUTTON_POP_UP_RECESSED;	// correct the color
		}

		switch (w) {
			// These button widgets paint their own arrows
			case BUTTON_POP_DOWN:
			case BUTTON_POP_UP:
			case BUTTON_POP_DOWN_ROUND_RECT:
			case BUTTON_POP_UP_ROUND_RECT:
				return false;
		}

		return true;
	}

	@Override
	protected @Nonnull
	Renderer getScrollBarRenderer(@Nonnull ScrollBarConfiguration g)
	{
		ScrollBarWidget sw = g.getWidget();

		if (sw == ScrollBarWidget.LEGACY) {
			return super.getScrollBarRenderer(g);
		}

		return Renderer.create(new OverlayScrollBarPainterExtension(uiLayout, g));
	}

	@Override
	protected @Nonnull
	Renderer getSliderRenderer(@Nonnull SliderConfiguration g)
	{
		Renderer r = super.getSliderRenderer(g);
		if (g.getWidget() == SliderWidget.SLIDER_CIRCULAR) {
			Renderer pr = Renderer.create(new CircularSliderPainterExtension(g));
			return Renderer.createCompositeRenderer(r, pr);
		}
		return r;
	}

	@Override
	protected @Nullable
	Renderer getSliderTickMarkRenderer(@Nonnull SliderConfiguration g)
	{
		if (g.getWidget() != SliderWidget.SLIDER_CIRCULAR && g.hasTickMarks()) {
			return Renderer.create(new LinearSliderPainterExtension(uiLayout, g));
		} else {
			return null;
		}
	}

	@Override
	public @Nonnull
	String toString()
	{
		return "Augmented " + super.toString();
	}
}
