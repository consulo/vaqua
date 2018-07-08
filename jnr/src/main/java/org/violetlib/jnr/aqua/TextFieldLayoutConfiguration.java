/*
 * Copyright (c) 2015-2016 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

package org.violetlib.jnr.aqua;

import java.util.Objects;

import javax.annotation.*;

import org.violetlib.jnr.aqua.AquaUIPainter.TextFieldWidget;
import org.violetlib.jnr.aqua.AquaUIPainter.Size;
import org.violetlib.jnr.aqua.AquaUIPainter.UILayoutDirection;

/**
	A layout configuration for a text field.
*/

public class TextFieldLayoutConfiguration
	extends LayoutConfiguration
{
	private final @Nonnull
	TextFieldWidget tw;
	private final @Nonnull
	Size size;
	private final @Nonnull
	UILayoutDirection ld;

	public TextFieldLayoutConfiguration(@Nonnull TextFieldWidget tw, @Nonnull Size size, @Nonnull UILayoutDirection ld)
	{
		// Layout direction affects search fields, in particular, the locations of the search and cancel icons

		this.tw = tw;
		this.size = size;
		this.ld = ld;
	}

	public @Nonnull
	TextFieldWidget getWidget()
	{
		return tw;
	}

	public @Nonnull
	Size getSize()
	{
		return size;
	}

	public @Nonnull
	UILayoutDirection getLayoutDirection()
	{
		return ld;
	}

	public boolean isLeftToRight()
	{
		return ld == UILayoutDirection.LEFT_TO_RIGHT;
	}

	public boolean isSearchField()
	{
		return tw.isSearch();
	}

	@Override
	public boolean equals(@Nullable Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TextFieldLayoutConfiguration that = (TextFieldLayoutConfiguration) o;
		return tw == that.tw && size == that.size && ld == that.ld;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(tw, size, ld);
	}

	@Override
	public @Nonnull
	String toString()
	{
		String lds = ld == UILayoutDirection.RIGHT_TO_LEFT ? " RTL" : "";
		return tw + " " + size + lds;
	}
}
