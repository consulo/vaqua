/*
 * Copyright (c) 2015-2016 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

package org.violetlib.jnr.aqua.impl;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import javax.annotation.*;

import org.violetlib.jnr.Insetter;
import org.violetlib.jnr.LayoutInfo;
import org.violetlib.jnr.aqua.AquaUILayoutInfo;
import org.violetlib.jnr.aqua.AquaUIPainter;
import org.violetlib.jnr.aqua.ComboBoxLayoutConfiguration;
import org.violetlib.jnr.aqua.PopupButtonLayoutConfiguration;
import org.violetlib.jnr.aqua.ScrollBarThumbConfiguration;
import org.violetlib.jnr.aqua.ScrollBarThumbLayoutConfiguration;
import org.violetlib.jnr.aqua.SliderLayoutConfiguration;
import org.violetlib.jnr.impl.JNRPlatformUtils;

/**
	An abstract base class containing common code that supports layout but not rendering.
*/

public abstract class AquaUIPainterAbstractBase
	implements AquaUIPainter
{
	/*
	  Because all paint requests are issued from the UI event thread, we can use a separate method to set the generic
	  parameters.
	*/

	protected int w;
	protected int h;
	protected boolean forceVertical;	// for evaluation mode only
	protected boolean isAlignmentEnabled = true;

	// Layout related state
	protected @Nullable
	LayoutInfo layoutInfo;

	// The following variables define the current rendering bounds. The bounds may be different only if the widget
	// specifies a fixed width or height that is smaller than the client specified bounds. The client specified bounds
	// always have a zero origin.
	protected float xOffset;				// the X offset introduced to center a fixed width widget in a larger space
	protected float yOffset;				// the Y offset introduced to center a fixed height widget in a larger space
	protected float pWidth;					// the width of the painted area, limited by the widget fixed width (if any)
	protected float pHeight;				// the height of the painted area, limited by the widget fixed height (if any)

	protected static final @Nonnull
	AquaUILayoutInfo uiLayout;
	protected static final @Nonnull
	UIOutliner uiOutliner;

	static {
		int platformVersion = JNRPlatformUtils.getPlatformVersion();
		uiLayout = findLayoutInfo(platformVersion);
		uiOutliner = new YosemiteOutliner((YosemiteLayoutInfo) uiLayout);
	}

	private static @Nonnull
	AquaUILayoutInfo findLayoutInfo(int platformVersion)
	{
		if (platformVersion >= 101200) {
			return new SierraLayoutInfo();
		} else if (platformVersion >= 101100) {
			return new ElCapitanLayoutInfo();
		} else {
			return new YosemiteLayoutInfo();
		}
	}

	public void setAlignmentEnabled(boolean b)
	{
		isAlignmentEnabled = b;
	}

	@Override
	public void configure(int w, int h)
	{
		this.w = w;
		this.h = h;

		forceVertical = false;
	}

	@Override
	public @Nonnull
	AquaUILayoutInfo getLayoutInfo()
	{
		return uiLayout;
	}

	@Override
	public final @Nonnull
	Rectangle2D getComboBoxEditorBounds(@Nonnull ComboBoxLayoutConfiguration g)
	{
		LayoutInfo layoutInfo = uiLayout.getLayoutInfo(g);
		Rectangle2D bounds = getCenteredBounds(layoutInfo);
		Insetter s = uiLayout.getComboBoxEditorInsets(g);
		return s.applyToBounds2D(bounds);
	}

	@Override
	public final @Nonnull
	Rectangle2D getComboBoxIndicatorBounds(@Nonnull ComboBoxLayoutConfiguration g)
	{
		LayoutInfo layoutInfo = uiLayout.getLayoutInfo(g);
		Rectangle2D bounds = getCenteredBounds(layoutInfo);
		Insetter s = uiLayout.getComboBoxIndicatorInsets(g);
		return s.applyToBounds2D(bounds);
	}

	@Override
	public final @Nonnull
	Rectangle2D getPopupButtonContentBounds(@Nonnull PopupButtonLayoutConfiguration g)
	{
		LayoutInfo layoutInfo = uiLayout.getLayoutInfo(g);
		Rectangle2D bounds = getCenteredBounds(layoutInfo);
		Insetter s = uiLayout.getPopupButtonContentInsets(g);
		return s.applyToBounds2D(bounds);
	}

	@Override
	public float getScrollBarThumbPosition(@Nonnull ScrollBarThumbLayoutConfiguration g, boolean useExtent)
	{
		LayoutInfo layoutInfo = uiLayout.getLayoutInfo(g);
		Rectangle2D bounds = getCenteredBounds(layoutInfo);
		return uiLayout.getScrollBarThumbPosition(bounds, g, useExtent);
	}

	@Override
	public int getScrollBarThumbHit(@Nonnull ScrollBarThumbConfiguration g)
	{
		LayoutInfo layoutInfo = uiLayout.getLayoutInfo(g);
		Rectangle2D bounds = getCenteredBounds(layoutInfo);
		return uiLayout.getScrollBarThumbHit(bounds, g);
	}

	@Override
	public final @Nonnull
	Rectangle2D getSliderThumbBounds(@Nonnull SliderLayoutConfiguration g, double thumbPosition)
	{
		LayoutInfo layoutInfo = uiLayout.getLayoutInfo(g);
		Rectangle2D bounds = getCenteredBounds(layoutInfo);
		return uiLayout.getSliderThumbBounds(bounds, g, thumbPosition);
	}

	@Override
	public final @Nonnull
	Rectangle2D getSliderLabelBounds(@Nonnull SliderLayoutConfiguration g,
																												 double thumbPosition,
																												 @Nonnull Dimension size)
	{
		if (thumbPosition < 0 || thumbPosition > 1) {
			return new Rectangle(0, 0, 0, 0);
		}

		LayoutInfo layoutInfo = uiLayout.getLayoutInfo(g);
		Rectangle2D bounds = getCenteredBounds(layoutInfo);
		return uiLayout.getSliderLabelBounds(bounds, g, thumbPosition, size);
	}

	/**
		Return the location along the major axis of the center of the thumb for a given thumb position. This method is
		appropriate only for linear sliders.

		@param bounds The slider bounds.
		@param g The slider layout configuration.
		@param thumbPosition The thumb position.
		@return the X coordinate of the thumb center, if the slider is horizontal, or the Y coordinate of the thumb center,
			if the slider is vertical.
	*/

	public final double getSliderThumbCenter(@Nonnull Rectangle2D bounds,
																					 @Nonnull SliderLayoutConfiguration g,
																					 double thumbPosition)
	{
		return uiLayout.getSliderThumbCenter(bounds, g, thumbPosition);
	}

	@Override
	public final double getSliderThumbPosition(@Nonnull SliderLayoutConfiguration g, int x, int y)
	{
		LayoutInfo layoutInfo = uiLayout.getLayoutInfo(g);
		Rectangle2D bounds = getCenteredBounds(layoutInfo);
		return uiLayout.getSliderThumbPosition(bounds, g, x, y);
	}

	/**
		Update the rendering configuration based on the widget layout.

		@param info The layout info for the widget being rendered. Null if not available.
	*/

	protected void configureLayout(@Nullable LayoutInfo info)
	{
		xOffset = 0;
		yOffset = 0;
		pWidth = w;
		pHeight = h;
		layoutInfo = info;

		if (layoutInfo != null) {
			// If the widget rendering cannot expand to fill the requested space, we center the rendering in that space.

			float fixedWidth = layoutInfo.getFixedVisualWidth();
			if (fixedWidth > 0) {
				if (w > fixedWidth && isAlignmentEnabled) {
					xOffset = (w - fixedWidth) / 2;
				}
				pWidth = fixedWidth;
			}

			float fixedHeight = layoutInfo.getFixedVisualHeight();
			if (fixedHeight > 0) {
				if (h > fixedHeight && isAlignmentEnabled) {
					yOffset = (h - fixedHeight) / 2;
				}
				pHeight = fixedHeight;
			}
		}
	}

	/**
	 	Return the configured bounds adjusted for the alignment that is required when the layout bounds exceeds one or more
	 	fixed sizes.
	*/

	protected @Nonnull
	Rectangle2D getCenteredBounds(@Nullable LayoutInfo layoutInfo)
	{
		float x = 0;
		float y = 0;
		float width = w;
		float height = h;

		if (layoutInfo != null) {
			float fixedWidth = layoutInfo.getFixedVisualWidth();
			if (fixedWidth > 0) {
				float extra = width - fixedWidth;
				if (extra > 0) {
					x += extra / 2;
					width -= extra;
				}
			}

			float fixedHeight = layoutInfo.getFixedVisualHeight();
			if (fixedHeight > 0) {
				float extra = height - fixedHeight;
				if (extra > 0) {
					y += extra / 2;
					height -= extra;
				}
			}
		}

		return new Rectangle2D.Float(x, y, width, height);
	}
}
