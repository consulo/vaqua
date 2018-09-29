/*
 * Copyright (c) 2015 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

package org.violetlib.jnr.aqua.impl;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.violetlib.jnr.aqua.*;

/**
	Provides outlines for widgets that can be used to draw focus rings.
*/

public abstract class UIOutliner
{
	public @Nullable
	Shape getOutline(@Nonnull Rectangle2D bounds, @Nonnull LayoutConfiguration g)
		throws UnsupportedOperationException
	{
		if (g instanceof ButtonLayoutConfiguration) {
			ButtonLayoutConfiguration gg = (ButtonLayoutConfiguration) g;
			return getButtonOutline(bounds, gg);
		}

		if (g instanceof ComboBoxLayoutConfiguration) {
			ComboBoxLayoutConfiguration gg = (ComboBoxLayoutConfiguration) g;
			return getComboBoxOutline(bounds, gg);
		}

		if (g instanceof PopupButtonLayoutConfiguration) {
			PopupButtonLayoutConfiguration gg = (PopupButtonLayoutConfiguration) g;
			return getPopUpButtonOutline(bounds, gg);
		}

		if (g instanceof TitleBarLayoutConfiguration) {
			TitleBarLayoutConfiguration gg = (TitleBarLayoutConfiguration) g;
			return getTitleBarOutline(bounds, gg);
		}

		if (g instanceof SliderThumbLayoutConfiguration) {
			SliderThumbLayoutConfiguration gg = (SliderThumbLayoutConfiguration) g;
			return getSliderThumbOutline(bounds, gg);
		}

		if (g instanceof SliderLayoutConfiguration) {
			SliderLayoutConfiguration gg = (SliderLayoutConfiguration) g;
			return getSliderOutline(bounds, gg);
		}

		if (g instanceof SpinnerArrowsLayoutConfiguration) {
			SpinnerArrowsLayoutConfiguration gg = (SpinnerArrowsLayoutConfiguration) g;
			return getSpinnerArrowsOutline(bounds, gg);
		}

		if (g instanceof SplitPaneDividerLayoutConfiguration) {
			SplitPaneDividerLayoutConfiguration gg = (SplitPaneDividerLayoutConfiguration) g;
			return getSplitPaneDividerOutline(bounds, gg);
		}

		if (g instanceof SegmentedButtonLayoutConfiguration) {
			SegmentedButtonLayoutConfiguration gg = (SegmentedButtonLayoutConfiguration) g;
			return getSegmentedButtonOutline(bounds, gg);
		}

		if (g instanceof ToolBarItemWellLayoutConfiguration) {
			ToolBarItemWellLayoutConfiguration gg = (ToolBarItemWellLayoutConfiguration) g;
			return getToolBarItemWellOutline(bounds, gg);
		}

		if (g instanceof GroupBoxLayoutConfiguration) {
			GroupBoxLayoutConfiguration gg = (GroupBoxLayoutConfiguration) g;
			return getGroupBoxOutline(bounds, gg);
		}

		if (g instanceof ListBoxLayoutConfiguration) {
			ListBoxLayoutConfiguration gg = (ListBoxLayoutConfiguration) g;
			return getListBoxOutline(bounds, gg);
		}

		if (g instanceof TextFieldLayoutConfiguration) {
			TextFieldLayoutConfiguration gg = (TextFieldLayoutConfiguration) g;
			return getTextFieldOutline(bounds, gg);
		}

		if (g instanceof ScrollBarLayoutConfiguration) {
			ScrollBarLayoutConfiguration gg = (ScrollBarLayoutConfiguration) g;
			return getScrollBarOutline(bounds, gg);
		}

		if (g instanceof ScrollColumnSizerLayoutConfiguration) {
			ScrollColumnSizerLayoutConfiguration gg = (ScrollColumnSizerLayoutConfiguration) g;
			return getScrollColumnSizerOutline(bounds, gg);
		}

		if (g instanceof ProgressIndicatorLayoutConfiguration) {
			ProgressIndicatorLayoutConfiguration gg = (ProgressIndicatorLayoutConfiguration) g;
			return getProgressIndicatorOutline(bounds, gg);
		}

		if (g instanceof TableColumnHeaderLayoutConfiguration) {
			TableColumnHeaderLayoutConfiguration gg = (TableColumnHeaderLayoutConfiguration) g;
			return getTableColumnHeaderOutline(bounds, gg);
		}

		throw new UnsupportedOperationException();
	}

	protected abstract @Nullable
	Shape getSliderThumbOutline(@Nonnull Rectangle2D bounds, @Nonnull SliderThumbLayoutConfiguration g);

	protected abstract @Nullable
	Shape getButtonOutline(@Nonnull Rectangle2D bounds, @Nonnull ButtonLayoutConfiguration g);

	protected abstract @Nullable
	Shape getSegmentedButtonOutline(@Nonnull Rectangle2D bounds, @Nonnull SegmentedButtonLayoutConfiguration g);

	protected abstract @Nullable
	Shape getComboBoxOutline(@Nonnull Rectangle2D bounds, @Nonnull ComboBoxLayoutConfiguration g);

	protected abstract @Nullable
	Shape getPopUpButtonOutline(@Nonnull Rectangle2D bounds, @Nonnull PopupButtonLayoutConfiguration g);

	protected abstract @Nullable
	Shape getToolBarItemWellOutline(@Nonnull Rectangle2D bounds, @Nonnull ToolBarItemWellLayoutConfiguration g);

	protected abstract @Nullable
	Shape getTitleBarOutline(@Nonnull Rectangle2D bounds, @Nonnull TitleBarLayoutConfiguration g);

	protected abstract @Nullable
	Shape getSliderOutline(@Nonnull Rectangle2D bounds, @Nonnull SliderLayoutConfiguration g);

	protected abstract @Nullable
	Shape getSpinnerArrowsOutline(@Nonnull Rectangle2D bounds, @Nonnull SpinnerArrowsLayoutConfiguration g);

	protected abstract @Nullable
	Shape getSplitPaneDividerOutline(@Nonnull Rectangle2D bounds, @Nonnull SplitPaneDividerLayoutConfiguration g);

	protected abstract @Nullable
	Shape getGroupBoxOutline(@Nonnull Rectangle2D bounds, @Nonnull GroupBoxLayoutConfiguration g);

	protected abstract @Nullable
	Shape getListBoxOutline(@Nonnull Rectangle2D bounds, @Nonnull ListBoxLayoutConfiguration g);

	protected abstract @Nullable
	Shape getTextFieldOutline(@Nonnull Rectangle2D bounds, @Nonnull TextFieldLayoutConfiguration g);

	protected abstract @Nullable
	Shape getScrollBarOutline(@Nonnull Rectangle2D bounds, @Nonnull ScrollBarLayoutConfiguration g);

	protected abstract @Nullable
	Shape getScrollColumnSizerOutline(@Nonnull Rectangle2D bounds, @Nonnull ScrollColumnSizerLayoutConfiguration g);

	protected abstract @Nullable
	Shape getProgressIndicatorOutline(@Nonnull Rectangle2D bounds, @Nonnull ProgressIndicatorLayoutConfiguration g);

	protected abstract @Nullable
	Shape getTableColumnHeaderOutline(@Nonnull Rectangle2D bounds, @Nonnull TableColumnHeaderLayoutConfiguration g);

	public static int size(@Nonnull AquaUIPainter.Size sz, int regular, int small, int mini)
	{
		switch (sz) {
			case SMALL:
				return small;
			case MINI:
				return mini;
			default:
				return regular;
		}
	}

	public static float size2D(@Nonnull AquaUIPainter.Size sz, float regular, float small, float mini)
	{
		switch (sz) {
			case SMALL:
				return small;
			case MINI:
				return mini;
			default:
				return regular;
		}
	}
}
