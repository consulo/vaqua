/*
 * Copyright (c) 2015 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

package org.violetlib.jnr.impl;

import java.awt.Graphics;

import javax.annotation.Nonnull;

import org.violetlib.jnr.Painter;

/**

*/

public class OffsetPainter
	implements Painter
{
	private final @Nonnull
	Painter p;
	private final float xOffset;
	private final float yOffset;

	public OffsetPainter(@Nonnull Painter p, float xOffset, float yOffset)
	{
		this.p = p;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}

	@Override
	public void paint(@Nonnull Graphics g, float x, float y)
	{
		p.paint(g, x + xOffset, y + yOffset);
	}
}
