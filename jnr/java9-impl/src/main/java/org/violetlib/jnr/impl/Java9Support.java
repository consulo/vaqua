/*
 * Copyright (c) 2016 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

package org.violetlib.jnr.impl;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.annotation.Nonnull;

/**
	Java platform specific support for Java 9 and later.
*/

public class Java9Support implements JavaSupport.JavaSupportImpl
{
	@Override
	public int getScaleFactor(@Nonnull Graphics g)
	{
		// This works in Java 9. Before that, it returned 1.
		Graphics2D gg = (Graphics2D) g;
		GraphicsConfiguration gc = gg.getDeviceConfiguration();
		AffineTransform t = gc.getDefaultTransform();
		double sx = t.getScaleX();
		double sy = t.getScaleY();
		return (int) Math.max(sx, sy);
	}

	@Override
	public Image createMultiResolutionImage(int baseImageWidth, int baseImageHeight, @Nonnull BufferedImage im)
	{
		return new JNR9MultiResolutionImage(baseImageWidth, baseImageHeight, im);
	}
}
