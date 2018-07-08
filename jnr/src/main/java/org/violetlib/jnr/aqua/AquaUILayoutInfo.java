/*
 * Copyright (c) 2015 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

package org.violetlib.jnr.aqua;

import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import org.violetlib.jnr.Insetter;
import org.violetlib.jnr.LayoutInfo;
import org.violetlib.jnr.aqua.impl.PopupArrowConfiguration;
import org.violetlib.jnr.aqua.impl.SliderThumbConfiguration;
import org.violetlib.jnr.impl.BasicLayoutInfo;
import org.violetlib.jnr.impl.CombinedInsetter;

import static org.violetlib.jnr.aqua.AquaUIPainter.TitleBarButtonWidget;

import javax.annotation.*;

/**
	Provides layout information for widgets based on the platform UI.
*/

public abstract class AquaUILayoutInfo
{
	/**
		Return the layout information for the specified widget configuration.
		@param g The configuration.
		@return the layout information for the specified configuration.
	*/

	public @Nonnull
	LayoutInfo getLayoutInfo(@Nonnull LayoutConfiguration g)
		throws UnsupportedOperationException
	{
		if (g instanceof ButtonLayoutConfiguration) {
			ButtonLayoutConfiguration gg = (ButtonLayoutConfiguration) g;
			return getButtonLayoutInfo(gg);
		}

		if (g instanceof ComboBoxLayoutConfiguration) {
			ComboBoxLayoutConfiguration gg = (ComboBoxLayoutConfiguration) g;
			return getComboBoxLayoutInfo(gg);
		}

		if (g instanceof PopupButtonLayoutConfiguration) {
			PopupButtonLayoutConfiguration gg = (PopupButtonLayoutConfiguration) g;
			return getPopUpButtonLayoutInfo(gg);
		}

		if (g instanceof TitleBarLayoutConfiguration) {
			TitleBarLayoutConfiguration gg = (TitleBarLayoutConfiguration) g;
			return getTitleBarLayoutInfo(gg);
		}

		if (g instanceof SliderLayoutConfiguration) {
			SliderLayoutConfiguration gg = (SliderLayoutConfiguration) g;
			return getSliderLayoutInfo(gg);
		}

		if (g instanceof SpinnerArrowsLayoutConfiguration) {
			SpinnerArrowsLayoutConfiguration gg = (SpinnerArrowsLayoutConfiguration) g;
			return getSpinnerArrowsLayoutInfo(gg);
		}

		if (g instanceof SplitPaneDividerLayoutConfiguration) {
			SplitPaneDividerLayoutConfiguration gg = (SplitPaneDividerLayoutConfiguration) g;
			return getSplitPaneDividerLayoutInfo(gg);
		}

		if (g instanceof SegmentedButtonLayoutConfiguration) {
			SegmentedButtonLayoutConfiguration gg = (SegmentedButtonLayoutConfiguration) g;
			return getSegmentedButtonLayoutInfo(gg);
		}

		if (g instanceof ToolBarItemWellLayoutConfiguration) {
			ToolBarItemWellLayoutConfiguration gg = (ToolBarItemWellLayoutConfiguration) g;
			return getToolBarItemWellLayoutInfo(gg);
		}

		if (g instanceof GroupBoxLayoutConfiguration) {
			GroupBoxLayoutConfiguration gg = (GroupBoxLayoutConfiguration) g;
			return getGroupBoxLayoutInfo(gg);
		}

		if (g instanceof ListBoxLayoutConfiguration) {
			ListBoxLayoutConfiguration gg = (ListBoxLayoutConfiguration) g;
			return getListBoxLayoutInfo(gg);
		}

		if (g instanceof TextFieldLayoutConfiguration) {
			TextFieldLayoutConfiguration gg = (TextFieldLayoutConfiguration) g;
			return getTextFieldLayoutInfo(gg);
		}

		if (g instanceof ScrollBarLayoutConfiguration) {
			ScrollBarLayoutConfiguration gg = (ScrollBarLayoutConfiguration) g;
			return getScrollBarLayoutInfo(gg);
		}

		if (g instanceof ScrollColumnSizerLayoutConfiguration) {
			ScrollColumnSizerLayoutConfiguration gg = (ScrollColumnSizerLayoutConfiguration) g;
			return getScrollColumnSizerLayoutInfo(gg);
		}

		if (g instanceof ProgressIndicatorLayoutConfiguration) {
			ProgressIndicatorLayoutConfiguration gg = (ProgressIndicatorLayoutConfiguration) g;
			return getProgressIndicatorLayoutInfo(gg);
		}

		if (g instanceof TableColumnHeaderLayoutConfiguration) {
			TableColumnHeaderLayoutConfiguration gg = (TableColumnHeaderLayoutConfiguration) g;
			return getTableColumnHeaderLayoutInfo(gg);
		}

		// for testing
		if (g instanceof SliderThumbConfiguration) {
			SliderThumbConfiguration gg = (SliderThumbConfiguration) g;
			return getSliderThumbLayoutInfo(gg.getSliderConfiguration());
		}

		// for testing
		if (g instanceof PopupArrowConfiguration) {
			PopupArrowConfiguration gg = (PopupArrowConfiguration) g;
			Insetter s = getPopUpArrowInsets(gg.getPopupButtonConfiguration());
			if (s instanceof CombinedInsetter) {
				CombinedInsetter cs = (CombinedInsetter) s;
				float fixedWidth = cs.getFixedRegionWidth();
				float fixedHeight = cs.getFixedRegionHeight();
				return BasicLayoutInfo.createFixed(fixedWidth, fixedHeight);
			}
		}

		return BasicLayoutInfo.getInstance();	// should not happen
	}

	/**
		Return the content insets for the specified widget configuration.
		@param g The configuration.
		@return the content insets, or null if the specified configuration does not support contents.
	*/

	public @Nullable
	Insetter getContentInsets(@Nonnull LayoutConfiguration g)
	{
		if (g instanceof ButtonLayoutConfiguration) {
			ButtonLayoutConfiguration gg = (ButtonLayoutConfiguration) g;
			return getButtonLabelInsets(gg);
		}

		if (g instanceof ComboBoxLayoutConfiguration) {
			ComboBoxLayoutConfiguration gg = (ComboBoxLayoutConfiguration) g;
			return getComboBoxEditorInsets(gg);
		}

		if (g instanceof PopupButtonLayoutConfiguration) {
			PopupButtonLayoutConfiguration gg = (PopupButtonLayoutConfiguration) g;
			return getPopupButtonContentInsets(gg);
		}

		if (g instanceof TitleBarLayoutConfiguration) {
			TitleBarLayoutConfiguration gg = (TitleBarLayoutConfiguration) g;
			return getTitleBarLabelInsets(gg);
		}

		if (g instanceof SegmentedButtonLayoutConfiguration) {
			SegmentedButtonLayoutConfiguration gg = (SegmentedButtonLayoutConfiguration) g;
			return getSegmentedButtonLabelInsets(gg);
		}

		if (g instanceof TextFieldLayoutConfiguration) {
			TextFieldLayoutConfiguration gg = (TextFieldLayoutConfiguration) g;
			return getTextFieldTextInsets(gg);
		}

		if (g instanceof TableColumnHeaderLayoutConfiguration) {
			TableColumnHeaderLayoutConfiguration gg = (TableColumnHeaderLayoutConfiguration) g;
			return getTableColumnHeaderLabelInsets(gg);
		}

		return null;
	}

	/**
		Indicate where a button label should be painted, based on the specified parameters.

		@param g This parameter specifies the layout configuration of the button.
		@return an insetter that can be used to determine the label area, or null if the button has no label area.
	*/

	public abstract @Nullable
	Insetter getButtonLabelInsets(@Nonnull ButtonLayoutConfiguration g);

	/**
		Indicate where a segmented button label should be painted, based on the specified parameters.

		@param g This parameter specifies the layout configuration of the segmented button.
		@return an insetter that can be used to determine the label area, or null if the button has no label area.
	*/

	public abstract @Nonnull
	Insetter getSegmentedButtonLabelInsets(@Nonnull SegmentedButtonLayoutConfiguration g);

	/**
		Return the (dynamic) insets of the indicator within the combo box.

		@param g This parameter specifies the layout configuration of the segmented button.
		@return the insets.
	*/

	public abstract @Nonnull
	Insetter getComboBoxIndicatorInsets(@Nonnull ComboBoxLayoutConfiguration g);

	/**
		Return the insets that define the editor area in a properly sized combo box.

		@param g This parameter specifies the layout configuration of the segmented button.
		@return the insets.
	*/

	public abstract @Nonnull
	Insetter getComboBoxEditorInsets(@Nonnull ComboBoxLayoutConfiguration g);

	/**
		Return the insets that define the arrow area in a properly sized pop up button. For internal use.

		@param g This parameter specifies the layout configuration of the pop up button.
		@return the insets.
	*/

	public abstract @Nonnull
	Insetter getPopUpArrowInsets(@Nonnull PopupButtonConfiguration g);

	/**
		Return the insets that define the content area in a properly sized pop up button.

		@param g This parameter specifies the layout configuration of the pop up button.
		@return the insets.
	*/

	public abstract @Nonnull
	Insetter getPopupButtonContentInsets(@Nonnull PopupButtonLayoutConfiguration g);

	/**
		Return the insets that define the content area in a text field.

		@param g This parameter specifies the layout configuration of the text field.
		@return the insets.
	*/

	public abstract @Nonnull
	Insetter getTextFieldTextInsets(@Nonnull TextFieldLayoutConfiguration g);

	/**
		Return the insets that define the active area corresponding to the search button in a search field.

		@param g This parameter specifies the layout configuration of the search field.
		@return the insets, or null if there is no search button in the specified configuration.
	*/

	public abstract @Nullable
	Insetter getSearchButtonInsets(@Nonnull TextFieldLayoutConfiguration g);

	/**
		Return the insets that define the active area corresponding to the cancel button in a search field.

		@param g This parameter specifies the layout configuration of the search field.
		@return the insets, or null if there is no cancel button in the specified configuration.
	*/

	public abstract @Nullable
	Insetter getCancelButtonInsets(@Nonnull TextFieldLayoutConfiguration g);

	/**
		Return the insets that define the search button rendering region in a search field.

		@param g This parameter specifies the layout configuration of the search field.
		@return the insets, or null if there is no search button in the specified configuration.
	*/

	public abstract @Nullable
	Insetter getSearchButtonPaintingInsets(@Nonnull TextFieldLayoutConfiguration g);

	/**
		Return the insets that define the cancel button rendering region in a search field.

		@param g This parameter specifies the layout configuration of the search field.
		@return the insets, or null if there is no cancel button in the specified configuration.
	*/

	public abstract @Nullable
	Insetter getCancelButtonPaintingInsets(@Nonnull TextFieldLayoutConfiguration g);

	/**
		Return the layout info for the search button in a search field.

		@param g This parameter specifies the layout configuration of the search field.
		@return the layout info, or null if there is no search button in the specified configuration.
	*/

	public abstract @Nullable
	LayoutInfo getSearchButtonLayoutInfo(@Nonnull TextFieldLayoutConfiguration g);

	/**
		Return the layout info for the cancel button in a search field.

		@param g This parameter specifies the layout configuration of the search field.
		@return the layout info, or null if there is no cancel button in the specified configuration.
	*/

	public abstract @Nullable
	LayoutInfo getCancelButtonLayoutInfo(@Nonnull TextFieldLayoutConfiguration g);

	/**
		Map a major axis coordinate of a scroll bar to a thumb position along the scroll bar track.

		@param bounds The bounds of the scroll bar.
		@param g This parameter describes the scroll bar and the coordinate.
		@param useExtent If true, the coordinate is interpreted as the location of the leading edge of the thumb, for the
			purpose of repositioning the thumb. If false, the coordinate is interpreted as a fraction of the full track, for
			the purpose of scroll-to-here.

		@return the thumb position as a fraction of the scroll bar track, if in the range 0 to 1 (inclusive), or a value
			less than 0 if the coordinate is outside the track in the area corresponding to low values, or a value greater
			than 1 if the coordinate is outside the track in the area corresponding to high values.

		The scroll bar track is the portion of the widget that the thumb can occupy.
	*/

	public abstract float getScrollBarThumbPosition(@Nonnull Rectangle2D bounds,
																									@Nonnull ScrollBarThumbLayoutConfiguration g,
																									boolean useExtent);

	/**
		Determine the visible bounds of a scroll bar thumb. This method takes into account any minimum scroll bar thumb
		length.

		@param bounds The bounds of the scroll bar.
		@param g This parameter describes the scroll bar.

		@return the visible bounds of the scroll bar thumb.
	*/

	public abstract @Nonnull
	Rectangle2D getScrollBarThumbBounds(@Nonnull Rectangle2D bounds,
																															 @Nonnull ScrollBarConfiguration g);

	/**
		Determine whether a major axis coordinate of a scroll bar corresponds to the visible thumb.
		@param bounds The bounds of the scroll bar.
		@param g This parameter describes the scroll bar and the coordinate.

		@return zero if the coordinate corresponds to the visible thumb, -1 if it is in the track at a lower position,
			1 if it is in the track at a higher position, a large negative number otherwise.

		The scroll bar track is the portion of the widget that the thumb can occupy.
	*/

	public abstract int getScrollBarThumbHit(@Nonnull Rectangle2D bounds,
																					 @Nonnull ScrollBarThumbConfiguration g);

	/**
		Return the layout info for the thumb of a slider.

		@param g This parameter specifies the layout configuration of the slider.
		@return the layout info.
	*/

	// this method supports evaluation
	public abstract @Nonnull
	LayoutInfo getSliderThumbLayoutInfo(@Nonnull SliderLayoutConfiguration g);

	/**
		Return the bounds of the thumb of a slider for the purposes of hit detection.

		@param bounds The bounds of the slider.
		@param g The slider layout configuration.
		@param thumbPosition The thumb position.
		@return the thumb insets.
	*/

	public abstract @Nonnull
	Rectangle2D getSliderThumbBounds(@Nonnull Rectangle2D bounds,
																														@Nonnull SliderLayoutConfiguration g,
																														double thumbPosition);

	/**
		Return the insets of the region of a linear slider where the track is painted.

		@param g The slider layout configuration.
		@return the track insets.
	*/

	public abstract @Nonnull
	Insetter getSliderTrackPaintingInsets(@Nonnull SliderLayoutConfiguration g);

	/**
		Return the insets of the thumb of a slider for the purposes of outlining or highlighting. The returned insets do not
		include shadow areas.

		@param g The slider layout configuration.
		@param thumbPosition The thumb position.
		@return the thumb insets.
	*/

	public abstract @Nonnull
	Insetter getSliderThumbInsets(@Nonnull SliderLayoutConfiguration g, double thumbPosition);

	/**
		Return the insets of the thumb of a slider for the purposes of painting the thumb. The returned insets include
		shadow areas.

		@param g The slider layout configuration.
		@param thumbPosition The thumb position.
		@return the thumb insets.
	*/

	// insets rather than bounds are needed for evaluation because we supply a variety of raster sizes
	public abstract @Nonnull
	Insetter getSliderThumbPaintingInsets(@Nonnull SliderLayoutConfiguration g, double thumbPosition);

	/**
		Return the suggested region for painting a label next to a slider.

		@param bounds The slider bounds.
		@param g The slider layout configuration.
		@param thumbPosition The thumb position associated with the label.
		@param labelSize The size of the label.
		@return the bounds of the suggested label region.
	*/

	public abstract @Nonnull
	Rectangle2D getSliderLabelBounds(@Nonnull Rectangle2D bounds,
																														@Nonnull SliderLayoutConfiguration g,
																														double thumbPosition,
																														@Nonnull Dimension labelSize);

	/**
		Return the location along the major axis of the center of the thumb for a given thumb position. This method is
		appropriate only for linear sliders.

		@param bounds The slider bounds.
		@param g The slider layout configuration.
		@param thumbPosition The thumb position.
		@return the X coordinate of the thumb center, if the slider is horizontal, or the Y coordinate of the thumb center,
			if the slider is vertical.
	*/

	public abstract double getSliderThumbCenter(@Nonnull Rectangle2D bounds,
																							@Nonnull SliderLayoutConfiguration g,
																							double thumbPosition);

	/**
		Map a location in a slider to the corresponding thumb position. This method is used for hit detection.

		@param bounds The slider bounds.
		@param g The slider layout configuration.
		@param x The X coordinate of the location.
		@param y The Y coordinate of the location.
		@return the thumb position corresponding to the specified location.
	*/

	public abstract double getSliderThumbPosition(@Nonnull Rectangle2D bounds,
																								@Nonnull SliderLayoutConfiguration g, int x, int y);

	/**
		Return the insets of the sort indicator of a table header cell for the purposes of painting the sort indicator.

		@param g The table header cell layout configuration.
		@return the sort indicator insets, or null if no sort indicator is displayed in the specified configuration.
	*/

	public abstract @Nullable
	Insetter getTableColumnHeaderSortArrowInsets(@Nonnull TableColumnHeaderLayoutConfiguration g);

	/**
		Return the insets of the label area of a table header cell for the purposes of painting the label. If the
		configuration does not imply the display of a sort indicator, then no space is reserved for a sort indicator.

		@param g The table header cell layout configuration.
		@return the label insets.
	*/

	public abstract @Nonnull
	Insetter getTableColumnHeaderLabelInsets(@Nonnull TableColumnHeaderLayoutConfiguration g);

	/**
		Return the insets of a title bar button.

		@param g The title bar layout configuration.
		@param bw The title bar button widget that indicates which button is specified.
		@return the button insets.
	*/

	public abstract @Nonnull
	Insetter getTitleBarButtonInsets(@Nonnull TitleBarLayoutConfiguration g,
																														@Nonnull TitleBarButtonWidget bw);

	/**
		Return the shape of a title bar button, for painting. This method is for internal use.

		@param bounds The title bar bounds.
		@param g The title bar layout configuration.
		@param bw The title bar button widget that indicates which button is specified.
		@return the button shape.
	*/

	public abstract @Nonnull
	Shape getTitleBarButtonShape(@Nonnull Rectangle2D bounds,
																												@Nonnull TitleBarLayoutConfiguration g,
																												@Nonnull TitleBarButtonWidget bw);

	/**
		Return the insets of the title bar label area. The label area is used for the window title and icon.

		@param g The title bar layout configuration.
		@return the label area insets.
	*/

	public abstract @Nullable
	Insetter getTitleBarLabelInsets(@Nonnull TitleBarLayoutConfiguration g);

	/**
		Map a location in a title bar to the corresponding button. This method is used for hit detection.

		@param bounds The title bar bounds.
		@param g The title bar layout configuration.
		@param x The X coordinate of the location.
		@param y The Y coordinate of the location.
		@return the widget that identifies the button corresponding to the specified location, or null if the location does
			not correspond to a button.
	*/

	public abstract @Nullable
	TitleBarButtonWidget identifyTitleBarButton(@Nonnull Rectangle2D bounds,
																																				@Nonnull TitleBarLayoutConfiguration g,
																																				int x, int y);

	protected abstract @Nonnull
	LayoutInfo getButtonLayoutInfo(@Nonnull ButtonLayoutConfiguration g);

	protected abstract @Nonnull
	LayoutInfo getSegmentedButtonLayoutInfo(@Nonnull SegmentedButtonLayoutConfiguration g);

	protected abstract @Nonnull
	LayoutInfo getComboBoxLayoutInfo(@Nonnull ComboBoxLayoutConfiguration g);

	protected abstract @Nonnull
	LayoutInfo getPopUpButtonLayoutInfo(@Nonnull PopupButtonLayoutConfiguration g);

	protected abstract @Nonnull
	LayoutInfo getToolBarItemWellLayoutInfo(@Nonnull ToolBarItemWellLayoutConfiguration g);

	protected abstract @Nonnull
	LayoutInfo getTitleBarLayoutInfo(@Nonnull TitleBarLayoutConfiguration g);

	protected abstract @Nonnull
	LayoutInfo getSliderLayoutInfo(@Nonnull SliderLayoutConfiguration g);

	protected abstract @Nonnull
	LayoutInfo getSpinnerArrowsLayoutInfo(@Nonnull SpinnerArrowsLayoutConfiguration g);

	protected abstract @Nonnull
	LayoutInfo getSplitPaneDividerLayoutInfo(@Nonnull SplitPaneDividerLayoutConfiguration g);

	protected abstract @Nonnull
	LayoutInfo getGroupBoxLayoutInfo(@Nonnull GroupBoxLayoutConfiguration g);

	protected abstract @Nonnull
	LayoutInfo getListBoxLayoutInfo(@Nonnull ListBoxLayoutConfiguration g);

	protected abstract @Nonnull
	LayoutInfo getTextFieldLayoutInfo(@Nonnull TextFieldLayoutConfiguration g);

	protected abstract @Nonnull
	LayoutInfo getScrollBarLayoutInfo(@Nonnull ScrollBarLayoutConfiguration g);

	protected abstract @Nonnull
	LayoutInfo getScrollColumnSizerLayoutInfo(@Nonnull ScrollColumnSizerLayoutConfiguration g);

	protected abstract @Nonnull
	LayoutInfo getProgressIndicatorLayoutInfo(@Nonnull ProgressIndicatorLayoutConfiguration g);

	protected abstract @Nonnull
	LayoutInfo getTableColumnHeaderLayoutInfo(@Nonnull TableColumnHeaderLayoutConfiguration g);
}
