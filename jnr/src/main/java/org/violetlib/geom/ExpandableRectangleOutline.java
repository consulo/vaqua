/*
 * Copyright (c) 2015 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

package org.violetlib.geom;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import javax.annotation.Nonnull;

/**
	An expandable outline in the form of a rectangle.
*/

public final class ExpandableRectangleOutline
	extends ExpandableOutline
{
	private final double x;
	private final double y;
	private final double width;
	private final double height;

	public ExpandableRectangleOutline(double x, double y, double width, double height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public ExpandableRectangleOutline(@Nonnull Rectangle2D source)
	{
		this.x = source.getX();
		this.y = source.getY();
		this.width = source.getWidth();
		this.height = source.getHeight();
	}

	@Override
	public @Nonnull
	Shape getShape(float offset)
	{
		double nx = x - offset;
		double ny = y - offset;
		double nwidth = width + 2 * offset;
		double nheight = height + 2 * offset;
		return new Rectangle2D.Double(nx, ny, nwidth, nheight);
	}
}
