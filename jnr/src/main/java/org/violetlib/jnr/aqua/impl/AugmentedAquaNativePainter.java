/*
 * Copyright (c) 2015-2018 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

package org.violetlib.jnr.aqua.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.violetlib.jnr.Insetter;
import org.violetlib.jnr.aqua.ScrollBarConfiguration;
import org.violetlib.jnr.aqua.TableColumnHeaderConfiguration;
import org.violetlib.jnr.aqua.TextFieldConfiguration;
import org.violetlib.jnr.aqua.TitleBarConfiguration;
import org.violetlib.jnr.impl.PainterExtension;
import org.violetlib.jnr.impl.Renderer;

/**
	This class augments the native painting code with painting to work around deficiencies in the native painting code.
*/

public class AugmentedAquaNativePainter
	extends AquaNativePainter
{
	@Override
	public @Nonnull
	AugmentedAquaNativePainter copy()
	{
		return new AugmentedAquaNativePainter();
	}

	@Override
	public @Nonnull
	Renderer getTableColumnHeaderRenderer(@Nonnull TableColumnHeaderConfiguration g)
	{
		// Do not use the native renderer. Use our simulation instead.

		PainterExtension px = new TableColumnHeaderCellPainterExtension(g);
		return Renderer.create(px);
	}

	@Override
	protected @Nonnull
	Renderer getTitleBarRenderer(@Nonnull TitleBarConfiguration g)
	{
		Renderer r = super.getTitleBarRenderer(g);
		PainterExtension px = getTitleBarButtonPainter(g);
		if (px == null) {
			return r;
		}
		Renderer pr = Renderer.create(px);
		return Renderer.createCompositeRenderer(r, pr);
	}

	protected @Nullable
	PainterExtension getTitleBarButtonPainter(@Nonnull TitleBarConfiguration g)
	{
		return new TitleBarPainterExtension(getTitleBarLayoutInfo(), g);
	}

	@Override
	protected @Nonnull
	Renderer getScrollBarRenderer(@Nonnull ScrollBarConfiguration g)
	{
		ScrollBarWidget sw = g.getWidget();

		if (sw == ScrollBarWidget.LEGACY) {
			return super.getScrollBarRenderer(g);
		} else {
			PainterExtension px = new OverlayScrollBarPainterExtension(uiLayout, g);
			return Renderer.create(px);
		}
	}

	@Override
	protected @Nonnull
	Renderer getTextFieldRenderer(@Nonnull TextFieldConfiguration g)
	{
		Renderer r = super.getTextFieldRenderer(g);
		TextFieldWidget w = g.getWidget();
		if (w.hasMenu()) {
			Insetter insets = uiLayout.getSearchButtonPaintingInsets(g);
			if (insets != null) {
				PainterExtension px = new SearchFieldMenuIconPainter(g, insets, appearance);
				Renderer pr = Renderer.create(px);
				return Renderer.createCompositeRenderer(r, pr);
			}
		}
		return r;
	}

	@Override
	public @Nonnull
	String toString()
	{
		return "Augmented " + super.toString();
	}
}
