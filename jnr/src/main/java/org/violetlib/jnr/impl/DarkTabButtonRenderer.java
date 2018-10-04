/*
 * Copyright (c) 2018 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

package org.violetlib.jnr.impl;

import java.awt.Color;

import javax.annotation.Nonnull;

/**
	A basic renderer that corrects the output of a specified basic renderer for tab buttons in dark mode.
*/

public class DarkTabButtonRenderer
	implements BasicRenderer
{
	private final @Nonnull
	BasicRenderer compositeRenderer;

	public DarkTabButtonRenderer(@Nonnull BasicRenderer maskRenderer, @Nonnull BasicRenderer tabRenderer)
	{
		Color bc = new Color(60, 60, 60);
		BasicRenderer backgroundRenderer = new PaintUsingMaskRenderer(maskRenderer, bc);
		this.compositeRenderer = Renderer.createCompositeBasicRenderer(backgroundRenderer, tabRenderer);
	}

	@Override
	public void render(@Nonnull int[] data, int rw, int rh, float w, float h)
	{
		compositeRenderer.render(data, rw, rh, w, h);
	}
}
