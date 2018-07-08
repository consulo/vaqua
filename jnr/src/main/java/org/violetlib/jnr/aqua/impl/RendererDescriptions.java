/*
 * Copyright (c) 2015 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

package org.violetlib.jnr.aqua.impl;

import javax.annotation.*;

import org.violetlib.jnr.aqua.*;
import org.violetlib.jnr.impl.RendererDescription;

/**
	Provides renderer descriptions. Often shared by multiple painters that use the same underlying renderer.
*/

public interface RendererDescriptions
{
	@Nonnull
	RendererDescription getButtonRendererDescription(@Nonnull ButtonConfiguration g);

	@Nonnull
	RendererDescription getSegmentedButtonRendererDescription(@Nonnull SegmentedButtonConfiguration g);

	@Nonnull
	RendererDescription getComboBoxRendererDescription(@Nonnull ComboBoxConfiguration g);

	@Nonnull
	RendererDescription getPopupButtonRendererDescription(@Nonnull PopupButtonConfiguration g);

	@Nullable
	RendererDescription getBasicPopupButtonRendererDescription(@Nonnull PopupButtonConfiguration g);

	@Nonnull
	RendererDescription getToolBarItemWellRendererDescription(@Nonnull ToolBarItemWellConfiguration g);

	@Nonnull
	RendererDescription getTitleBarRendererDescription(@Nonnull TitleBarConfiguration g);

	@Nonnull
	RendererDescription getSliderRendererDescription(@Nonnull SliderConfiguration g);

	@Nonnull
	RendererDescription getSliderTrackRendererDescription(@Nonnull SliderConfiguration g);

	@Nonnull
	RendererDescription getSliderThumbRendererDescription(@Nonnull SliderConfiguration g);

	@Nonnull
	RendererDescription getSpinnerArrowsRendererDescription(@Nonnull SpinnerArrowsConfiguration g);

	@Nonnull
	RendererDescription getSplitPaneDividerRendererDescription(@Nonnull SplitPaneDividerConfiguration g);

	@Nonnull
	RendererDescription getGroupBoxRendererDescription(@Nonnull GroupBoxConfiguration g);

	@Nonnull
	RendererDescription getListBoxRendererDescription(@Nonnull ListBoxConfiguration g);

	@Nonnull
	RendererDescription getTextFieldRendererDescription(@Nonnull TextFieldConfiguration g);

	@Nonnull
	RendererDescription getScrollBarRendererDescription(@Nonnull ScrollBarConfiguration g);

	@Nonnull
	RendererDescription getScrollColumnSizerRendererDescription(@Nonnull ScrollColumnSizerConfiguration g);

	@Nonnull
	RendererDescription getProgressIndicatorRendererDescription(@Nonnull ProgressIndicatorConfiguration g);

	@Nonnull
	RendererDescription getIndeterminateProgressIndicatorRendererDescription(@Nonnull IndeterminateProgressIndicatorConfiguration g);

	@Nonnull
	RendererDescription getTableColumnHeaderRendererDescription(@Nonnull TableColumnHeaderConfiguration g);

	@Nonnull
	RendererDescription getGradientRendererDescription(@Nonnull GradientConfiguration g);
}
