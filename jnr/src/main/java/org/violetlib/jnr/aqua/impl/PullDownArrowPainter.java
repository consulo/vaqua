/*
 * Copyright (c) 2015 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

package org.violetlib.jnr.aqua.impl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import javax.annotation.Nonnull;

import org.violetlib.jnr.aqua.AquaUIPainter;
import org.violetlib.jnr.aqua.PopupButtonConfiguration;
import org.violetlib.jnr.impl.PainterExtension;

/**
	Simulates the rendering of a Yosemite pull down menu button arrow.
*/

public class PullDownArrowPainter
	extends PopUpArrowPainterBase
	implements PainterExtension
{
	public PullDownArrowPainter(@Nonnull PopupButtonConfiguration g)
	{
		super(g);
	}

	@Override
	public void paint(@Nonnull Graphics2D g, float width, float height)
	{
		AquaUIPainter.Size sz = gg.getSize();
		double h = 2.5;
		double w = 5;

		double x1 = (width - w) / 2;
		double y1 = (height - h) / 2;
		double x2 = x1 + w/2;
		double y2 = y1 + h;
		double x3 = x1 + w;

		Color c = getColor();

		Path2D p = new Path2D.Double();
		p.moveTo(x1, y1);
		p.lineTo(x2, y2);
		p.lineTo(x3, y1);
		g.setColor(c);
		g.setStroke(new BasicStroke(1.5f));
		g.draw(p);
	}
}
