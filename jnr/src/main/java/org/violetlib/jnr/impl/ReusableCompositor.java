/*
 * Copyright (c) 2015-2018 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

package org.violetlib.jnr.impl;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.util.Arrays;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
	A compositor that renders into an INT_ARGB_PRE raster from various sources. Multiple renderings can be composed into
	the same raster. The raster can be drawn to a graphics context or returned as an image.

	<p>
	The compositor has an integer scale factor, used to support high resolution displays. The scale factor is available to
	the sources so that they can produce a rendering at the appropriate scale; it is not used for drawing the raster or
	returning the raster as an image.
	</p>

	<p>
	The compositor can be reused without reallocating the raster. Reusing a compositor is permitted except when the raster
	image is in use. The raster size is configured when the compositor is first used or reused.
	</p>

	<p>
	This class is not thread safe.
	</p>
*/

public class ReusableCompositor
{
	// TBD: would it be faster to turn everything into an Image and use graphics operations?

	private @Nullable
	int[] data;	// the actual raster buffer, reallocated as needed to contain at least the required number of pixels.
		// May be null if the raster has zero size.

	private @Nullable
	BufferedImage b;	// an image using the raster buffer, created on demand and released when the raster buffer is replaced.
		// May be null if the raster has zero size.

	private boolean isConfigured;	// true if the raster dimensions have been changed but the raster has not been updated
	private boolean isEmpty;			// true if the raster is known to have no content (allows compose to be faster)

	private int rasterWidth;			// the current raster width (may be inconsistent with the raster buffer until it has been configured)
	private int rasterHeight;			// the current raster height (may be inconsistent with the raster buffer until it has been configured)
	private int scaleFactor;			// the scale factor

	/**
		This interface permits additional image sources to be supported. They must be implemented using already supported
		sources.
	*/

	public interface PixelSource
	{
		/**
			Render pixels into the specified compositor.

			@param compositor The compositor.
		*/

		void composeTo(@Nonnull ReusableCompositor compositor);
	}

	public interface PixelOperator
	{
		int combine(int destintationPixel, int sourcePixel);
	}

	/**
		Create a reusable compositor. The raster width and height are zero.
	*/

	public ReusableCompositor()
	{
	}

	/**
		Create a reusable compositor using the specified buffer.
	*/

	public ReusableCompositor(@Nonnull int[] data, int rw, int rh, int scaleFactor)
	{
		if (rw < 0 || rh < 0) {
			throw new IllegalArgumentException("Invalid negative raster width and/or height");
		}

		if (scaleFactor < 1 || scaleFactor > 8) {
			throw new IllegalArgumentException("Invalid or unsupported scale factor");
		}

		this.data = data;
		this.rasterWidth = rw;
		this.rasterHeight = rh;
		this.scaleFactor = scaleFactor;
		this.isConfigured = true;
		this.isEmpty = true;
	}

	/**
		Return the INT_ARGB_PRE color model.
	*/

	public static @Nonnull
	ColorModel getColorModel()
	{
		return BasicImageSupport.getColorModel();
	}

	/**
		Return the width of the raster, in pixels.
	*/

	public int getRasterWidth()
	{
		return rasterWidth;
	}

	/**
		Return the height of the raster, in pixels.
	*/

	public int getRasterHeight()
	{
		return rasterHeight;
	}

	/**
		Return the scale factor, which relates raster pixels to device independent pixels.
	*/

	public int getScaleFactor()
	{
		return scaleFactor;
	}

	/**
		Return the width of the raster, in device independent pixels (according to the scale factor).
	*/

	public float getWidth()
	{
		return ((float) rasterWidth) / scaleFactor;
	}

	/**
		Return the height of the raster, in device independent pixels (according to the scale factor).
	*/

	public float getHeight()
	{
		return ((float) rasterHeight) / scaleFactor;
	}


	/**
		Create a compositor that is configured to the same raster size and scale factor as this one.
	*/

	public @Nonnull
	ReusableCompositor createSimilar()
	{
		ReusableCompositor c = new ReusableCompositor();
		c.reset(rasterWidth, rasterHeight, scaleFactor);
		return c;
	}

	/**
		Create a compositor containing a horizontally flipped copy of this one.
	*/

	public @Nonnull
	ReusableCompositor createHorizontallyFlippedCopy()
	{
		ReusableCompositor output = createSimilar();
		output.copyHorizontallyFlippedFrom(this);
		return output;
	}

	/**
		Resize and clear the raster in preparation for rendering. The actual clearing or reallocation of the raster may be
		deferred until the raster is needed.

		@param rasterWidth The new raster width.
		@param rasterHeight The new raster height.
		@param scaleFactor The scale factor that relates raster pixels to device independent pixels.
	*/

	public void reset(int rasterWidth, int rasterHeight, int scaleFactor)
	{
		if (rasterWidth < 0 || rasterHeight < 0) {
			throw new IllegalArgumentException("Invalid negative raster width and/or height");
		}

		if (scaleFactor < 1 || scaleFactor > 8) {
			throw new IllegalArgumentException("Invalid or unsupported scale factor");
		}

		this.rasterWidth = rasterWidth;
		this.rasterHeight = rasterHeight;
		this.scaleFactor = scaleFactor;
		isEmpty = true;
	}

	/**
		Ensure that the raster buffer has been configured to the proper size and cleared if needed. This method supports
		lazy configuration of the raster buffer.
	*/

	protected void ensureConfigured()
	{
		if (!isConfigured) {
			isConfigured = true;
			int requiredSize = rasterWidth * rasterHeight;
			if (requiredSize > 0) {
				if (data == null || data.length < requiredSize) {
					data = new int[requiredSize];
					b = null;
				} else {
					Arrays.fill(data, 0);
				}
			}
		}
	}

	/**
		Render into the raster after resizing it and clearing it. This is a convenience method.

		@param r The renderer.
		@param rasterWidth The new raster width.
		@param rasterHeight The new raster height.
		@param scaleFactor The scale factor that relates raster pixels to device independent pixels.
	*/

	public void render(@Nonnull BasicRenderer r, int rasterWidth, int rasterHeight, int scaleFactor)
	{
		reset(rasterWidth, rasterHeight, scaleFactor);
		ensureConfigured();
		if (data != null) {
			float rw = ((float) rasterWidth) / scaleFactor;
			float rh = ((float) rasterHeight) / scaleFactor;
			r.render(data, rasterWidth, rasterHeight, rw, rh);
			isEmpty = false;
		}
	}

	/**
		Render into the raster, composing with existing contents.

		@param o The source of the pixels to compose with the existing contents. This object may be any of the standard
			sources ({@link BasicRenderer}, {@link PainterExtension}, or another {@link ReusableCompositor}), or an object
			that supports the {@link PixelSource} interface.
	*/

	public void compose(@Nonnull Object o)
	{
		if (o instanceof BasicRenderer) {
			BasicRenderer br = (BasicRenderer) o;
			composeRenderer(br);
		} else if (o instanceof PainterExtension) {
			PainterExtension px = (PainterExtension) o;
			composePainter(px, 0, 0);
		} else if (o instanceof ReusableCompositor) {
			ReusableCompositor rc = (ReusableCompositor) o;
			composeFrom(rc, 0, 0, rasterWidth, rasterHeight);
		} else if (o instanceof PixelSource) {
			PixelSource sr = (PixelSource) o;
			sr.composeTo(this);
		} else {
			throw new UnsupportedOperationException("Unsupported pixel source");
		}
	}

	/**
		Render into the raster, composing with existing contents.

		@param r The renderer that provides the pixels.
	*/

	public void composeRenderer(@Nonnull BasicRenderer r)
	{
		ensureConfigured();
		if (data != null) {
			if (isEmpty) {
				float rw = ((float) rasterWidth) / scaleFactor;
				float rh = ((float) rasterHeight) / scaleFactor;
				r.render(data, rasterWidth, rasterHeight, rw, rh);
				isEmpty = false;
			} else {
				composeRenderer(r, 0, 0, rasterWidth, rasterHeight);
			}
		}
	}

	/**
		Render into a region of the raster, composing with existing contents.

		@param r The renderer.
		@param dx The X origin of the raster region.
		@param dy The Y origin of the raster region.
		@param dw The width of the raster region.
		@param dh The height of the raster region.
	*/

	public void composeRenderer(@Nonnull BasicRenderer r, int dx, int dy, int dw, int dh)
	{
		if (dw > 0 && dh > 0) {
			ReusableCompositor temp = new ReusableCompositor();
			temp.render(r, dw, dh, scaleFactor);
			composeFrom(temp, dx, dy, dw, dh);
		}
	}

	/**
		Render a painter extension into a region of the raster, composing with existing contents.

		@param px The painter.
		@param dx The X origin of the raster region.
		@param dy The Y origin of the raster region.
		@param dw The width of the raster region.
		@param dh The height of the raster region.
	*/

	public void composePainter(@Nonnull PainterExtension px, int dx, int dy, int dw, int dh)
	{
		if (dw > 0 && dh > 0) {
			ReusableCompositor temp = new ReusableCompositor();
			temp.reset(dw, dh, scaleFactor);
			temp.composePainter(px, 0, 0);
			composeFrom(temp, dx, dy, dw, dh);
		}
	}

	/**
		Copy pixels from a compositor, flipping horizontally.

		@param source The compositor that is the source of the pixels.
	*/

	private void copyHorizontallyFlippedFrom(@Nonnull ReusableCompositor source)
	{
		ensureConfigured();

		if (data != null) {
			int[] sourceData = source.data;
			if (sourceData != null) {
				isEmpty = true;
				if (!source.isEmpty) {
					int sourceSpan = source.getRasterWidth();
					for (int row = 0; row < rasterHeight; row++) {
						for (int col = 0; col < rasterWidth; col++) {
							int sourceCol = rasterWidth - col - 1;
							int pixel = sourceData[row * sourceSpan + sourceCol];
							int alpha = (pixel >> 24) & 0xFF;
							if (alpha != 0) {
								isEmpty = false;
								data[row * rasterWidth + col] = pixel;
							}
						}
					}
				}
			}
		}
	}

	/**
		Render from a compositor into a region of the raster, composing with existing contents.

		@param source The compositor that is the source of the pixels.
		@param dx The X origin of the raster region.
		@param dy The Y origin of the raster region.
		@param dw The width of the raster region.
		@param dh The height of the raster region.
	*/

	public void composeFrom(@Nonnull ReusableCompositor source, int dx, int dy, int dw, int dh)
	{
		ensureConfigured();

		if (data != null) {
			int[] sourceData = source.data;
			if (sourceData != null) {
				isEmpty = false;
				int sourceSpan = source.getRasterWidth();
				for (int rowOffset = 0; rowOffset < dh; rowOffset++) {
					int row = dy + rowOffset;
					if (row >= 0 && row < rasterHeight) {
						for (int colOffset = 0; colOffset < dw; colOffset++) {
							int col = dx + colOffset;
							if (col >= 0 && col < rasterWidth) {
								int pixel = sourceData[rowOffset * sourceSpan + colOffset];
								int alpha = (pixel >> 24) & 0xFF;
								if (alpha != 0) {
									if (alpha != 0xFF) {
										pixel = JNRUtils.combine(data[row * rasterWidth + col], pixel);
									}
									data[row * rasterWidth + col] = pixel;
								}
							}
						}
					}
				}
			}
		}
	}

	/**
		Render from a designated region of a compositor into a designated region of the raster, composing with existing
		contents.

		@param source The compositor that is the source of the pixels.
		@param sx The X origin of the source region.
		@param sy The Y origin of the source region.
		@param dx The X origin of the raster region.
		@param dy The Y origin of the raster region.
		@param dw The width of the region.
		@param dh The height of the region.
	*/

	public void composeFrom(@Nonnull ReusableCompositor source, int sx, int sy, int dx, int dy, int dw, int dh)
	{
		ensureConfigured();

		if (data != null) {
			int[] sourceData = source.data;
			if (sourceData != null) {
				isEmpty = false;
				int sourceWidth = source.getRasterWidth();
				int sourceHeight = source.getRasterHeight();
				for (int rowOffset = 0; rowOffset < dh; rowOffset++) {
					int sourceRow = sy + rowOffset;
					int row = dy + rowOffset;
					if (row >= 0 && row < rasterHeight && sourceRow >= 0 && sourceRow < sourceHeight) {
						for (int colOffset = 0; colOffset < dw; colOffset++) {
							int sourceColumn = sx + colOffset;
							int col = dx + colOffset;
							if (col >= 0 && col < rasterWidth && sourceColumn >= 0 && sourceColumn < sourceWidth) {
								int pixel = sourceData[sourceRow * sourceWidth + sourceColumn];
								int alpha = (pixel >> 24) & 0xFF;
								if (alpha != 0) {
									if (alpha != 0xFF) {
										pixel = JNRUtils.combine(data[row * rasterWidth + col], pixel);
									}
									data[row * rasterWidth + col] = pixel;
								}
							}
						}
					}
				}
			}
		}
	}

	/**
		Render a painter extension into the raster, composing with existing contents.
	*/

	public void composePainter(@Nonnull PainterExtension px, float x, float y)
	{
		BufferedImage im = getImage();	// this method configures the raster buffer and the buffered image

		if (im != null) {
			isEmpty = false;
			Graphics2D g = im.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.scale(scaleFactor, scaleFactor);
			g.translate(x, y);
			float rw = ((float) rasterWidth) / scaleFactor;
			float rh = ((float) rasterHeight) / scaleFactor;
			px.paint(g, rw, rh);
		}
	}

	/**
		A very special case. Allows direct manipulation of the pixels, not just composing.
	*/

	public void renderFrom(@Nonnull BasicRenderer r)
	{
		ensureConfigured();

		if (data != null) {
			isEmpty = false;
			float w = ((float) rasterWidth) / scaleFactor;
			float h = ((float) rasterHeight) / scaleFactor;
			r.render(data, rasterWidth, rasterHeight, w, h);
		}
	}

	/**
		Blend pixels from a source compostior into the raster.

		@param source The compositor that is the source of the pixels.
		@param op The blending operator.
	*/

	public void blendFrom(@Nonnull ReusableCompositor source, @Nonnull PixelOperator op)
	{
		blendFrom(source, op, 0, 0, rasterWidth, rasterHeight);
	}

	/**
		Blend pixels from a source compositor into a region of the raster.

		@param source The compositor that is the source of the pixels.
		@param op The blending operator.
		@param dx The X origin of the raster region.
		@param dy The Y origin of the raster region.
		@param dw The width of the raster region.
		@param dh The height of the raster region.
	*/

	public void blendFrom(@Nonnull ReusableCompositor source, @Nonnull PixelOperator op, int dx, int dy, int dw, int dh)
	{
		ensureConfigured();

		if (data != null) {
			int[] sourceData = source.data;
			if (sourceData != null) {
				int sourceSpan = source.getRasterWidth();
				for (int rowOffset = 0; rowOffset < dh; rowOffset++) {
					int row = dy + rowOffset;
					if (row >= 0 && row < rasterHeight) {
						for (int colOffset = 0; colOffset < dw; colOffset++) {
							int col = dx + colOffset;
							if (col >= 0 && col < rasterWidth) {
								int destinationIndex = row * rasterWidth + col;
								int sourcePixel = sourceData[rowOffset * sourceSpan + colOffset];
								int destinationPixel = data[destinationIndex];
								int pixel = op.combine(destinationPixel, sourcePixel);
								int alpha = (pixel >> 24) & 0xFF;
								if (alpha != 0) {
									data[destinationIndex] = pixel;
									isEmpty = false;
								}
							}
						}
					}
				}
			}
		}
	}

	/**
		Erase pixels in the existing contents.
	*/

	public void erase(int dx, int dy, int dw, int dh)
	{
		if (dw > 0 && dh > 0) {
			ensureConfigured();

			if (data != null) {
				for (int rowOffset = 0; rowOffset < dh; rowOffset++) {
					int row = dy + rowOffset;
					if (row >= 0 && row < rasterHeight) {
						for (int colOffset = 0; colOffset < dw; colOffset++) {
							int col = dx + colOffset;
							if (col >= 0 && col < rasterWidth) {
								data[row * rasterWidth + col] = 0;
							}
						}
					}
				}
			}
		}
	}

	/**
		Return the raster as an image. The image shares the raster buffer with this compositor. The compositor should not
		be reused until the image is no longer in use.

		@return the image, or null if the raster has zero size.
	*/

	public @Nullable
	BufferedImage getImage()
	{
		ensureConfigured();

		if (b == null && data != null) {
			b = BasicImageSupport.createImage(data, rasterWidth, rasterHeight);
		}

		return b;
	}

	/**
		Draw the raster to the specified graphics context.
		@param g The graphics context.
	*/

	public void paint(@Nonnull Graphics2D g)
	{
		BufferedImage im = getImage();

		if (im != null) {
			g.drawImage(im, null, null);
		}
	}
}
