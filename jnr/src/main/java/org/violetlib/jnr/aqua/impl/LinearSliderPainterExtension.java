/*
 * Copyright (c) 2015 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

package org.violetlib.jnr.aqua.impl;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.jetbrains.annotations.*;

import org.violetlib.jnr.aqua.AquaUILayoutInfo;
import org.violetlib.jnr.aqua.AquaUIPainter;
import org.violetlib.jnr.aqua.SliderConfiguration;
import org.violetlib.jnr.impl.JNRUtils;
import org.violetlib.jnr.impl.PainterExtension;

/**
	Simulates the rendering of tick marks on linear sliders.
*/

public class LinearSliderPainterExtension
	implements PainterExtension
{
	protected final @NotNull AquaUILayoutInfo uiLayout;
	protected final @NotNull SliderConfiguration sg;

	protected Color TICK_COLOR = new Color(10, 10, 10, 110);

	public LinearSliderPainterExtension(@NotNull AquaUILayoutInfo uiLayout, @NotNull SliderConfiguration g)
	{
		this.uiLayout = uiLayout;
		this.sg = g;
	}

	@Override
	public void paint(@NotNull Graphics2D g, float width, float height)
	{
		AquaUIPainter.SliderWidget sw = sg.getWidget();

		if (sg.hasTickMarks()) {
			paintLinearTickMarks(g, width, height);
		}
	}

	protected void paintLinearTickMarks(@NotNull Graphics2D g, float width, float height)
	{
		AquaUIPainter.SliderWidget sw = sg.getWidget();
		AquaUIPainter.Size sz = sg.getSize();
		boolean isHorizontal = sw == AquaUIPainter.SliderWidget.SLIDER_HORIZONTAL || sw == AquaUIPainter.SliderWidget.SLIDER_HORIZONTAL_RIGHT_TO_LEFT;
		int tickCount = sg.getNumberOfTickMarks();
		AquaUIPainter.TickMarkPosition position = sg.getTickMarkPosition();
		Rectangle2D bounds = new Rectangle2D.Float(0, 0, width, height);

		double w = 1;
		double h = JNRUtils.size(sz, 4, 3, 3);
		double sep = 2;

		if (isHorizontal) {
			boolean isAbove = position == AquaUIPainter.TickMarkPosition.ABOVE;
			double y = isAbove ? sep : height - sep - h;
			double x0;
			double x1;
			if (tickCount == 1) {
				x0 = x1 = uiLayout.getSliderThumbCenter(bounds, sg, 0.5);
			} else {
				x0 = uiLayout.getSliderThumbCenter(bounds, sg, 0);
				x1 = uiLayout.getSliderThumbCenter(bounds, sg, 1);
				if (sw == AquaUIPainter.SliderWidget.SLIDER_HORIZONTAL_RIGHT_TO_LEFT) {
					double temp = x0;
					x0 = x1;
					x1 = temp;
				}
			}
			SliderHorizontalTickPainter tp = new SliderHorizontalTickPainter(TICK_COLOR, w, h, x0, x1, y, tickCount);
			tp.paint(g);
		} else {
			boolean isLeft = position == AquaUIPainter.TickMarkPosition.LEFT;
			double x = isLeft ? sep : width - sep - h;
			double y0;
			double y1;
			if (tickCount == 1) {
				y0 = y1 = uiLayout.getSliderThumbCenter(bounds, sg, 0.5);
			} else {
				y0 = uiLayout.getSliderThumbCenter(bounds, sg, 0);
				y1 = uiLayout.getSliderThumbCenter(bounds, sg, 1);
				if (sw == AquaUIPainter.SliderWidget.SLIDER_VERTICAL) {
					double temp = y0;
					y0 = y1;
					y1 = temp;
				}
			}
			SliderVerticalTickPainter tp = new SliderVerticalTickPainter(TICK_COLOR, w, h, x, y0, y1, tickCount);
			tp.paint(g);
		}
	}
}