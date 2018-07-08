/*
 * Copyright (c) 2015-2016 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

package org.violetlib.jnr.aqua.impl;

import org.violetlib.jnr.aqua.*;
import org.violetlib.jnr.impl.BasicRendererDescription;
import org.violetlib.jnr.impl.JNRPlatformUtils;
import org.violetlib.jnr.impl.MultiResolutionRendererDescription;
import org.violetlib.jnr.impl.RendererDescription;

import static org.violetlib.jnr.impl.JNRUtils.*;

import javax.annotation.*;

/**
	Renderer descriptions for NSView based rendering on OS X 10.10 (Yosemite).
*/

public class ViewRendererDescriptions
	implements RendererDescriptions
{
	@Override
	public @Nonnull
	RendererDescription getButtonRendererDescription(@Nonnull ButtonConfiguration g)
	{
		AquaUIPainter.ButtonWidget bw = toCanonicalButtonStyle(g.getButtonWidget());

		if (bw == AquaUIPainter.ButtonWidget.BUTTON_TOOLBAR_ITEM) {
			ToolBarItemWellConfiguration tg = new ToolBarItemWellConfiguration(g.getState(), true);
			return getToolBarItemWellRendererDescription(tg);
		}

		AquaUIPainter.Size sz = g.getSize();

		int platformVersion = JNRPlatformUtils.getPlatformVersion();
		if (bw == AquaUIPainter.ButtonWidget.BUTTON_ROUND_TOOLBAR && platformVersion < 101100) {
			bw = AquaUIPainter.ButtonWidget.BUTTON_ROUND;
		}

		if (bw == AquaUIPainter.ButtonWidget.BUTTON_PUSH) {
			switch (sz) {
				case LARGE:
				case REGULAR:
					return new BasicRendererDescription(-6, 0, 12, 2);
				case SMALL:
					return new BasicRendererDescription(-5, -1, 10, 3);
				case MINI:
					return new BasicRendererDescription(-1, 0, 2, 0);
				default:
					throw new UnsupportedOperationException();
			}

		} else if (bw == AquaUIPainter.ButtonWidget.BUTTON_BEVEL) {
			return new BasicRendererDescription(0, 0, 0, 0);

		} else if (bw == AquaUIPainter.ButtonWidget.BUTTON_BEVEL_ROUND) {
			return new BasicRendererDescription(-2, -2, 4, 4);

		} else if (bw == AquaUIPainter.ButtonWidget.BUTTON_CHECK_BOX) {
			switch (sz) {
				case LARGE:
				case REGULAR:
					return new BasicRendererDescription(0, 0, 0, 0);
				case SMALL:
					return new BasicRendererDescription(0, 0, 0, 0);
				case MINI:
					return new BasicRendererDescription(0, -1, 0, 1);
				default:
					throw new UnsupportedOperationException();
			}

		} else if (bw == AquaUIPainter.ButtonWidget.BUTTON_RADIO) {
			switch (sz) {
				case LARGE:
				case REGULAR:
					return new BasicRendererDescription(0, 0, 0, 0);
				case SMALL:
					return new BasicRendererDescription(0, 0, 0, 1);
				case MINI:
					return new BasicRendererDescription(0, 0, 0, 1);
				default:
					throw new UnsupportedOperationException();
			}

		} else if (bw == AquaUIPainter.ButtonWidget.BUTTON_DISCLOSURE) {
			switch (sz) {
				case LARGE:
				case REGULAR:
					return new BasicRendererDescription(0, 0, 0, 0);
				case SMALL:
					return new BasicRendererDescription(0, 0, 0, 0);
				case MINI:
					return new BasicRendererDescription(0, 0, 2, 0);
				default:
					throw new UnsupportedOperationException();
			}

		} else if (bw == AquaUIPainter.ButtonWidget.BUTTON_HELP) {
			switch (sz) {
				case LARGE:
				case REGULAR:
					return new BasicRendererDescription(0, 0, 0, platformVersion < 101200 ? 3 : 0);
				case SMALL:
					return new BasicRendererDescription(0, 0, 0, 0);
				case MINI:
					return platformVersion < 101200 ? new BasicRendererDescription(0, -0.5f, 1, 0) : new BasicRendererDescription(-0.49f, 0, 1, 0);
				default:
					throw new UnsupportedOperationException();
			}

		} else if (bw == AquaUIPainter.ButtonWidget.BUTTON_GRADIENT) {
			return new BasicRendererDescription(0, -1, 0, 2);

		} else if (bw == AquaUIPainter.ButtonWidget.BUTTON_RECESSED) {
			return new BasicRendererDescription(0, 0, 0, 1);

		} else if (bw == AquaUIPainter.ButtonWidget.BUTTON_INLINE) {
			return new BasicRendererDescription(0, 0, 0, 0);

		} else if (bw == AquaUIPainter.ButtonWidget.BUTTON_ROUNDED_RECT) {
			return new BasicRendererDescription(0, 0, 0, 1);

		} else if (bw == AquaUIPainter.ButtonWidget.BUTTON_TEXTURED) {
			return new BasicRendererDescription(0, 0, 0, 0);

		} else if (bw == AquaUIPainter.ButtonWidget.BUTTON_TEXTURED_TOOLBAR) {
			return new BasicRendererDescription(0, 0, 0, 0);

		} else if (bw == AquaUIPainter.ButtonWidget.BUTTON_ROUND) {
			switch (sz) {
				case LARGE:
				case REGULAR:
					return new BasicRendererDescription(0, -4, 0, 10);	// tall raster needed to show a regular size button
				case SMALL:
					return new BasicRendererDescription(0, -2, 0, 8);		// tall raster needed to show a small size button
				case MINI:
					RendererDescription rd1 = new BasicRendererDescription(0, -1, 0, 3);
					RendererDescription rd2 = new BasicRendererDescription(0, -0.5f, 0, 3);
					return new MultiResolutionRendererDescription(rd1, rd2);
				default:
					throw new UnsupportedOperationException();
			}

		} else if (bw == AquaUIPainter.ButtonWidget.BUTTON_DISCLOSURE_TRIANGLE) {
			return new BasicRendererDescription(0, 0, 0, 0);

		} else if (bw == AquaUIPainter.ButtonWidget.BUTTON_PUSH_INSET2) {
			return new BasicRendererDescription(0, 0, 0, 0);

		} else if (bw == AquaUIPainter.ButtonWidget.BUTTON_ROUND_INSET) {
			return new BasicRendererDescription(0, 0, 0, 0);

		} else if (bw == AquaUIPainter.ButtonWidget.BUTTON_ROUND_TEXTURED) {
			return new BasicRendererDescription(0, 0, 0, 0);

		} else if (bw == AquaUIPainter.ButtonWidget.BUTTON_ROUND_TOOLBAR) {
			return new BasicRendererDescription(0, 0, 0, 0);

		} else if (bw == AquaUIPainter.ButtonWidget.BUTTON_COLOR_WELL) {
			return new BasicRendererDescription(0, 0, 0, 0);

		} else {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public @Nonnull
	RendererDescription getSegmentedButtonRendererDescription(@Nonnull SegmentedButtonConfiguration g)
	{
		// The native view renderer renders an entire segmented control but arranges that only one button is rendered into
		// our buffer. It does not make sense to change the raster width, because the raster width is the only way that the
		// native renderer knows how wide the button should be. If any horizontal adjustment is needed, it should be made
		// by the native renderer.

		AquaUIPainter.SegmentedButtonWidget bw = g.getWidget();
		AquaUIPainter.Size sz = g.getSize();
		int platformVersion = JNRPlatformUtils.getPlatformVersion();

		switch (bw) {
			case BUTTON_TAB:
			case BUTTON_SEGMENTED:
			case BUTTON_SEGMENTED_SEPARATED:
				switch (sz) {
					case LARGE:
					case REGULAR:
						return createVertical(0, 1);
					case SMALL:
						return createVertical(0, 2);
					case MINI:
						return createVertical(-0.51f, 5);
					default:
						throw new UnsupportedOperationException();
				}

			case BUTTON_SEGMENTED_INSET:
				switch (sz) {
					case LARGE:
					case REGULAR:
						return createVertical(-1, 4);
					case SMALL:
						return createVertical(-1, 5);
					case MINI:
						return createVertical(-1, 6);
					default:
						throw new UnsupportedOperationException();
				}

			case BUTTON_SEGMENTED_SCURVE:
			case BUTTON_SEGMENTED_TEXTURED:
			case BUTTON_SEGMENTED_TEXTURED_TOOLBAR:
			case BUTTON_SEGMENTED_TOOLBAR:
			case BUTTON_SEGMENTED_TEXTURED_SEPARATED:
			case BUTTON_SEGMENTED_TEXTURED_SEPARATED_TOOLBAR:
				boolean raise = bw == AquaUIPainter.SegmentedButtonWidget.BUTTON_SEGMENTED_TEXTURED_TOOLBAR || bw == AquaUIPainter.SegmentedButtonWidget.BUTTON_SEGMENTED_TEXTURED_SEPARATED_TOOLBAR;
				switch (sz) {
					case LARGE:
					case REGULAR:
						return platformVersion >= 101100 ? createVertical(-1.49f, 3) : createVertical(-1, 2);
					case SMALL:
						return platformVersion >= 101100 ? createVertical(raise ? -1.49f : -0.49f, 4) : createVertical(-1, 4);
					case MINI:
						return new MultiResolutionRendererDescription(
							createVertical(platformVersion >= 101100 ? 0 : -1, 5),
							createVertical(0, platformVersion >= 101100 ? 5 : 4.5f));
					default:
						throw new UnsupportedOperationException();
				}

			case BUTTON_SEGMENTED_SMALL_SQUARE:
				switch (sz) {
					case LARGE:
					case REGULAR:
						return createVertical(0, 2);
					case SMALL:
						return createVertical(0, 3);
					case MINI:
						return createVertical(0, 4);
					default:
						throw new UnsupportedOperationException();
				}

			default:
				throw new UnsupportedOperationException();
		}
	}

	protected @Nonnull
	RendererDescription createVertical(float yOffset, float heightAdjustment)
	{
		return new BasicRendererDescription(0, yOffset, 0, heightAdjustment);
	}

	protected @Nonnull
	RendererDescription createVertical(float yOffset1, float yOffset2, float heightAdjustment)
	{
		RendererDescription rd1 = new BasicRendererDescription(0, yOffset1, 0, heightAdjustment);
		RendererDescription rd2 = new BasicRendererDescription(0, yOffset2, 0, heightAdjustment);
		return new MultiResolutionRendererDescription(rd1, rd2);
	}

	@Override
	public @Nonnull
	RendererDescription getComboBoxRendererDescription(@Nonnull ComboBoxConfiguration g)
	{
		AquaUIPainter.ComboBoxWidget bw = g.getWidget();
		AquaUIPainter.Size sz = g.getSize();

		if (bw == AquaUIPainter.ComboBoxWidget.BUTTON_COMBO_BOX_CELL) {
			switch (sz) {
				case LARGE:
				case REGULAR:
					return new BasicRendererDescription(0, -3, 3, 3);
				case SMALL:
					return new BasicRendererDescription(0, -1, 3, 1);
				case MINI:
					return new BasicRendererDescription(0, 0, 2, 1);
				default:
					throw new UnsupportedOperationException();
			}
		} else if (bw == AquaUIPainter.ComboBoxWidget.BUTTON_COMBO_BOX_TEXTURED || bw == AquaUIPainter.ComboBoxWidget.BUTTON_COMBO_BOX_TEXTURED_TOOLBAR){
			switch (sz) {
				case LARGE:
				case REGULAR:
					return new BasicRendererDescription(0, 0, 1, 0);
				case SMALL:
					return new BasicRendererDescription(0, 0, 2, 2);
				case MINI:
					return new BasicRendererDescription(0, 0, 0, 2);
				default:
					throw new UnsupportedOperationException();
			}
		} else {
			switch (sz) {
				case LARGE:
				case REGULAR:
					return new BasicRendererDescription(-0.5f, 0, 3, 1);
				case SMALL:
					return new BasicRendererDescription(-0.5f, 0, 3, 2);
				case MINI:
					return new BasicRendererDescription(-0.5f, 0, 2, 2);
				default:
					throw new UnsupportedOperationException();
			}
		}
	}

	@Override
	public @Nullable
	RendererDescription getBasicPopupButtonRendererDescription(@Nonnull PopupButtonConfiguration g)
	{
		return getPopupButtonRendererDescription(g);
	}

	@Override
	public @Nonnull
	RendererDescription getPopupButtonRendererDescription(@Nonnull PopupButtonConfiguration g)
	{
		AquaUIPainter.PopupButtonWidget bw = g.getPopupButtonWidget();
		AquaUIPainter.Size sz = g.getSize();

		boolean isSquare = bw == AquaUIPainter.PopupButtonWidget.BUTTON_POP_UP_SQUARE || bw == AquaUIPainter.PopupButtonWidget.BUTTON_POP_DOWN_SQUARE;
		boolean isArrowsOnly = bw == AquaUIPainter.PopupButtonWidget.BUTTON_POP_UP_CELL || bw == AquaUIPainter.PopupButtonWidget.BUTTON_POP_DOWN_CELL;

		if ((isSquare || isArrowsOnly) && sz == AquaUIPainter.Size.MINI) {
			sz = AquaUIPainter.Size.SMALL;
		}

		switch (bw) {
			case BUTTON_POP_UP:
				switch (sz) {
					case LARGE:
					case REGULAR:
						return new BasicRendererDescription(-2, 0, 5, 0);
					case SMALL:
						return new BasicRendererDescription(-3, 0, 6, 0);
					case MINI:
						return new BasicRendererDescription(-1, 0, 3, 0);
					default:
						throw new UnsupportedOperationException();
				}
			case BUTTON_POP_UP_CELL:
				// extra height not needed for Core UI renderer
				switch (sz) {
					case LARGE:
					case REGULAR:
						return new BasicRendererDescription(0, -3, 0, 3);
					case SMALL:
						return new BasicRendererDescription(0, -1, 0, 1);
					case MINI:
						return new BasicRendererDescription(-1, -1, 3, 1);
					default:
						throw new UnsupportedOperationException();
				}
			case BUTTON_POP_UP_SQUARE:
				switch (sz) {
					case LARGE:
					case REGULAR:
						return new BasicRendererDescription(0, 0, 0, 0);
					case SMALL:
						return new BasicRendererDescription(0, 0, 0, 0);
					case MINI:
						return new BasicRendererDescription(-1, -1, 3, 1);
					default:
						throw new UnsupportedOperationException();
				}
			case BUTTON_POP_DOWN:
				switch (sz) {
					case LARGE:
					case REGULAR:
						return new BasicRendererDescription(-3, 0, 6, 1);
					case SMALL:
						return new BasicRendererDescription(-3, 0, 6, 1);
					case MINI:
						return new BasicRendererDescription(0, 0, 1, 0);
					default:
						throw new UnsupportedOperationException();
				}
			case BUTTON_POP_DOWN_CELL:
				switch (sz) {
					case LARGE:
					case REGULAR:
						return new BasicRendererDescription(0, 0, 0, 0);
					case SMALL:
						return new BasicRendererDescription(0, 0, 0, 0);
					case MINI:
						return new BasicRendererDescription(0, 0, 1, 0);
					default:
						throw new UnsupportedOperationException();
				}
			case BUTTON_POP_DOWN_SQUARE:
				switch (sz) {
					case LARGE:
					case REGULAR:
						return new BasicRendererDescription(0, 0, 0, 0);
					case SMALL:
						return new BasicRendererDescription(0, 0, 0, 0);
					case MINI:
						return new BasicRendererDescription(0, -1, 1, 1);
					default:
						throw new UnsupportedOperationException();
				}

			case BUTTON_POP_UP_BEVEL:
			case BUTTON_POP_DOWN_BEVEL:
				return new BasicRendererDescription(-2, -2, 4, 4);

			case BUTTON_POP_UP_ROUND_RECT:
			case BUTTON_POP_DOWN_ROUND_RECT:
				return new BasicRendererDescription(0, 0, 0, 1);

			case BUTTON_POP_UP_RECESSED:
			case BUTTON_POP_DOWN_RECESSED:
				return new BasicRendererDescription(0, 0, 0, 1);

			case BUTTON_POP_DOWN_TEXTURED:
			case BUTTON_POP_UP_TEXTURED:
			case BUTTON_POP_DOWN_TEXTURED_TOOLBAR:
			case BUTTON_POP_UP_TEXTURED_TOOLBAR:
				return new BasicRendererDescription(0, 0, 0, 0);

			case BUTTON_POP_UP_GRADIENT:
			case BUTTON_POP_DOWN_GRADIENT:
				return new BasicRendererDescription(0, -1, 0, 2);
			default:
				throw new UnsupportedOperationException();
		}
	}

	@Override
	public @Nonnull
	RendererDescription getToolBarItemWellRendererDescription(@Nonnull ToolBarItemWellConfiguration g)
	{
		return new BasicRendererDescription(0, 0, 0, 0);
	}

	@Override
	public @Nonnull
	RendererDescription getTitleBarRendererDescription(@Nonnull TitleBarConfiguration g)
	{
		return new BasicRendererDescription(0, 0, 0, 0);
	}

	@Override
	public @Nonnull
	RendererDescription getSliderRendererDescription(@Nonnull SliderConfiguration g)
	{
		return new BasicRendererDescription(0, 0, 0, 0);
	}

	@Override
	public @Nonnull
	RendererDescription getSliderTrackRendererDescription(@Nonnull SliderConfiguration g)
	{
		return new BasicRendererDescription(0, 0, 0, 0);
	}

	@Override
	public @Nonnull
	RendererDescription getSliderThumbRendererDescription(@Nonnull SliderConfiguration g)
	{
		AquaUIPainter.Size sz = g.getSize();

		if (g.isHorizontal() || g.isVertical()) {
			if (!g.hasTickMarks()) {
				float yOffset1 = -1;
				float yOffset2 = g.isHorizontal() ? size2D(sz, -0.5f, 0, 0) : size2D(sz, -0.5f, -0.5f, -0.5f);
				RendererDescription rd1 = new BasicRendererDescription(0, yOffset1, 0, 0);
				RendererDescription rd2 = new BasicRendererDescription(0, yOffset2, 0, 0);
				return new MultiResolutionRendererDescription(rd1, rd2);
			}
			if (g.isHorizontal()) {
				// The goal is to visually center the pointer horizontally in the layout width
				float xOffset1 = 0;
				float xOffset2 = size2D(sz, 0, 0, 0);
				if (g.getTickMarkPosition() == AquaUIPainter.TickMarkPosition.ABOVE) {
					float yOffset1 = 0;
					float yOffset2 = size2D(sz, 0, 0, 0);
					float ha = size2D(sz, 1, 0, 0);
					RendererDescription rd1 = new BasicRendererDescription(xOffset1, yOffset1, 0, ha);
					RendererDescription rd2 = new BasicRendererDescription(xOffset2, yOffset2, 0, ha);
					return new MultiResolutionRendererDescription(rd1, rd2);
				} else {
					float yOffset1 = size2D(sz, -1, 0, 0);
					float yOffset2 = size2D(sz, 0, 0, 0);
					float ha = 1;
					RendererDescription rd1 = new BasicRendererDescription(xOffset1, yOffset1, 0, ha);
					RendererDescription rd2 = new BasicRendererDescription(xOffset2, yOffset2, 0, ha);
					return new MultiResolutionRendererDescription(rd1, rd2);
				}
			} else {
				// Vertical sliders
				// The goal is to visually center the pointer vertically in the layout height, ignoring the shadow
				float yOffset1 = 0;
				float yOffset2 = size2D(sz, 0, 0.5f, 0.5f);
				if (g.getTickMarkPosition() == AquaUIPainter.TickMarkPosition.LEFT) {
					float wa = size2D(sz, 1, 2, 2);
					float ha = size(sz, 1, 0, 0);
					RendererDescription rd1 = new BasicRendererDescription(0, yOffset1, wa, ha);
					RendererDescription rd2 = new BasicRendererDescription(0, yOffset2, wa, ha);
					return new MultiResolutionRendererDescription(rd1, rd2);
				} else {
					float ha = size(sz, 1, 0, 0);
					RendererDescription rd1 = new BasicRendererDescription(0, yOffset1, 0, ha);
					RendererDescription rd2 = new BasicRendererDescription(0, yOffset2, 0, ha);
					return new MultiResolutionRendererDescription(rd1, rd2);
				}
			}
		} else {
			return new BasicRendererDescription(0, 0, 0, 0);
		}
	}

	@Override
	public @Nonnull
	RendererDescription getSpinnerArrowsRendererDescription(@Nonnull SpinnerArrowsConfiguration g)
	{
		return new BasicRendererDescription(0, 0, 0, 0);
	}

	@Override
	public @Nonnull
	RendererDescription getSplitPaneDividerRendererDescription(@Nonnull SplitPaneDividerConfiguration g)
	{
		AquaUIPainter.Orientation o = g.getOrientation();

		switch (g.getWidget())
		{
			case THIN_DIVIDER:
				// At 2x, the native view painter requires a "divider width" of at least 2 points.
				// At 1x, a larger size works better for both horizontal and vertical dividers.
				// We should only be given a "divider width" of one point, as that is the fixed logical divider width.
				return o == AquaUIPainter.Orientation.HORIZONTAL ? new BasicRendererDescription(0, 0, 0, 9) : new BasicRendererDescription(-1, 0, 2, 0);

			case THICK_DIVIDER:
				// At 2x, the native view painter requires a "divider width" of at least 10 points.
				// At 1x, a larger width works better for vertical dividers.
				// We should only be given a "divider width" of 9 points, as that is the fixed logical divider width.
				return o == AquaUIPainter.Orientation.HORIZONTAL ? new BasicRendererDescription(0, 0, 0, 1) :
					new MultiResolutionRendererDescription(new BasicRendererDescription(-4, 0, 6, 0), new BasicRendererDescription(-3, 0, 6, 0));

			case PANE_SPLITTER:
				// At 2x, the native view painter requires a "divider width" of at least 11 points.
				// At 1x, a larger width works better for vertical dividers.
				// We should only be given a "divider width" of 10 points, as that is the fixed logical divider width.
				return o == AquaUIPainter.Orientation.HORIZONTAL ? new BasicRendererDescription(0, 0, 0, 1) : new BasicRendererDescription(-5, 0, 10, 0);

			default:
				return null;
		}
	}

	@Override
	public @Nonnull
	RendererDescription getGroupBoxRendererDescription(@Nonnull GroupBoxConfiguration g)
	{
		return new BasicRendererDescription(-3, -2, 6, 6);
	}

	@Override
	public @Nonnull
	RendererDescription getListBoxRendererDescription(@Nonnull ListBoxConfiguration g)
	{
		return new BasicRendererDescription(0, 0, 0, 0);
	}

	@Override
	public @Nonnull
	RendererDescription getTextFieldRendererDescription(@Nonnull TextFieldConfiguration g)
	{
		return new BasicRendererDescription(0, 0, 0, 0);
	}

	@Override
	public @Nonnull
	RendererDescription getScrollBarRendererDescription(@Nonnull ScrollBarConfiguration g)
	{
		return new BasicRendererDescription(0, 0, 0, 0);
	}

	@Override
	public @Nonnull
	RendererDescription getScrollColumnSizerRendererDescription(@Nonnull ScrollColumnSizerConfiguration g)
	{
		return new BasicRendererDescription(0, 0, 0, 0);	// obsolete
	}

	@Override
	public @Nonnull
	RendererDescription getProgressIndicatorRendererDescription(@Nonnull ProgressIndicatorConfiguration g)
	{
		AquaUIPainter.ProgressWidget pw = g.getWidget();
		AquaUIPainter.Orientation o = g.getOrientation();
		AquaUIPainter.Size sz = g.getSize();

		if (pw == AquaUIPainter.ProgressWidget.BAR) {
			if (o == AquaUIPainter.Orientation.HORIZONTAL) {
				return new BasicRendererDescription(-1, 0, 2, 1);
			} else {
				return new BasicRendererDescription(0, -1, 1, 2);
			}
		} else {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public @Nonnull
	RendererDescription getIndeterminateProgressIndicatorRendererDescription(@Nonnull IndeterminateProgressIndicatorConfiguration g)
	{
		AquaUIPainter.ProgressWidget pw = g.getWidget();
		AquaUIPainter.Orientation o = g.getOrientation();
		AquaUIPainter.Size sz = g.getSize();

		if (pw == AquaUIPainter.ProgressWidget.INDETERMINATE_BAR) {
			if (o == AquaUIPainter.Orientation.HORIZONTAL) {
				return new BasicRendererDescription(-1, 0, 2, 1);
			} else {
				return new BasicRendererDescription(0, -1, 1, 2);
			}
		} else if (pw == AquaUIPainter.ProgressWidget.SPINNER){
			return new BasicRendererDescription(0, 0, 0, 0);
		} else {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public @Nonnull
	RendererDescription getTableColumnHeaderRendererDescription(@Nonnull TableColumnHeaderConfiguration g)
	{
		return new BasicRendererDescription(0, 0, 0, 0);
	}

	@Override
	public @Nonnull
	RendererDescription getGradientRendererDescription(@Nonnull GradientConfiguration g)
	{
		return new BasicRendererDescription(0, 0, 0, 0);	// obsolete
	}

	/**
		Map a button widget to a canonical equivalent. This mapping addresses the fact that certain styles have become
		obsolete and are best supported by using a similar style.
	*/

	protected @Nonnull
	AquaUIPainter.ButtonWidget toCanonicalButtonStyle(AquaUIPainter.ButtonWidget bw)
	{
//		switch (bw) {
//			case BUTTON_ROUND_INSET:
//				return AquaUIPainter.ButtonWidget.BUTTON_ROUND;
//			case BUTTON_ROUND_TEXTURED:
//				return AquaUIPainter.ButtonWidget.BUTTON_ROUND;
//		}
		return bw;
	}

	protected int pos(@Nonnull AquaUIPainter.Position pos, int first, int last, int only)
	{
		switch (pos)
		{
			case FIRST:
				return first;
			case LAST:
				return last;
			case ONLY:
				return only;
			default:
				return 0;
		}
	}

	protected int pos(@Nonnull AquaUIPainter.Position pos, int first, int middle, int last, int only)
	{
		switch (pos)
		{
			case FIRST:
				return first;
			case MIDDLE:
				return middle;
			case LAST:
				return last;
			case ONLY:
				return only;
			default:
				return 0;
		}
	}

	protected float pos(@Nonnull AquaUIPainter.Position pos, float first, float last, float only)
	{
		switch (pos)
		{
			case FIRST:
				return first;
			case LAST:
				return last;
			case ONLY:
				return only;
			default:
				return 0;
		}
	}

	protected float pos(@Nonnull AquaUIPainter.Position pos, float first, float middle, float last, float only)
	{
		switch (pos)
		{
			case FIRST:
				return first;
			case MIDDLE:
				return middle;
			case LAST:
				return last;
			case ONLY:
				return only;
			default:
				return 0;
		}
	}
}
