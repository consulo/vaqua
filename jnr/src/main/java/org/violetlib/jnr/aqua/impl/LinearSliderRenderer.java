/*
 * Copyright (c) 2015-2018 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

package org.violetlib.jnr.aqua.impl;

import java.awt.geom.Rectangle2D;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.violetlib.jnr.Insetter;
import org.violetlib.jnr.aqua.SliderConfiguration;
import org.violetlib.jnr.impl.JNRUtils;
import org.violetlib.jnr.impl.Renderer;
import org.violetlib.jnr.impl.ReusableCompositor;

/**
	A renderer for linear sliders using CoreUI based renderers for the track and thumb. It repositions the track and thumb
	to match what NSSlider paints (more or less).
*/

public class LinearSliderRenderer
	extends Renderer
{
	private static final @Nonnull
	ReusableCompositor.PixelOperator blender = new Blender();

	protected final @Nonnull
	SliderConfiguration g;
	protected final @Nonnull
	Renderer trackRenderer;
	protected final @Nonnull
	Insetter trackInsets;
	protected final @Nullable
	Renderer tickMarkRenderer;
	protected final @Nonnull
	Renderer thumbRenderer;
	protected final @Nonnull
	Insetter thumbInsets;
	protected final boolean isThumbTranslucent;

	public LinearSliderRenderer(@Nonnull SliderConfiguration g,
															@Nonnull Renderer trackRenderer,
															@Nonnull Insetter trackInsets,
															@Nullable Renderer tickMarkRenderer,
															@Nonnull Renderer thumbRenderer,
															@Nonnull Insetter thumbInsets,
															boolean isThumbTranslucent)
	{
		this.g = g;
		this.trackRenderer = trackRenderer;
		this.trackInsets = trackInsets;
		this.tickMarkRenderer = tickMarkRenderer;
		this.thumbRenderer = thumbRenderer;
		this.thumbInsets = thumbInsets;
		this.isThumbTranslucent = isThumbTranslucent;
	}

	@Override
	public void composeTo(@Nonnull ReusableCompositor compositor)
	{
		float w = compositor.getWidth();
		float h = compositor.getHeight();

		{
			Rectangle2D trackBounds = trackInsets.apply2D(w, h);
			Renderer r = Renderer.createOffsetRenderer(trackRenderer, trackBounds);
			r.composeTo(compositor);
			if (tickMarkRenderer != null) {
				tickMarkRenderer.composeTo(compositor);
			}
		}

		{
			Rectangle2D thumbBounds = thumbInsets.apply2D(w, h);
			double x = thumbBounds.getX();
			double y = thumbBounds.getY();
			Renderer r = Renderer.createOffsetRenderer(thumbRenderer, x, y, thumbBounds.getWidth(), thumbBounds.getHeight());

			// If a translucent thumb is directly painted, the track will show through.
			// Instead, the non-transparent thumb pixels must be copied into the raster.
			// A small adjustment is made at the boundary to avoid the appearance of a gap.

			if (isThumbTranslucent) {
				ReusableCompositor mask = compositor.createSimilar();
				r.composeTo(mask);
				compositor.blendFrom(mask, blender);
			} else {
				r.composeTo(compositor);
			}
		}
	}

	private static class Blender
		implements ReusableCompositor.PixelOperator
	{
		@Override
		public int combine(int destintationPixel, int sourcePixel)
		{
			int alpha = (sourcePixel >> 24) & 0xFF;
			if (alpha == 0) {
				return destintationPixel;
			}
			if (alpha > 20) {
				return sourcePixel;
			}
			return JNRUtils.combine(destintationPixel, sourcePixel);
		}
	}
}
