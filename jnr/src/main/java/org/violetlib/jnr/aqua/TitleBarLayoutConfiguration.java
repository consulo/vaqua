/*
 * Copyright (c) 2015 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

package org.violetlib.jnr.aqua;

import java.util.Objects;

import javax.annotation.*;

import org.violetlib.jnr.aqua.AquaUIPainter.TitleBarWidget;

/**
	A layout configuration for a title bar.
*/

public class TitleBarLayoutConfiguration
	extends LayoutConfiguration
{
	private final @Nonnull
	TitleBarWidget tw;

	public TitleBarLayoutConfiguration(@Nonnull TitleBarWidget tw)
	{
		this.tw = tw;
	}

	public @Nonnull
	TitleBarWidget getWidget()
	{
		return tw;
	}

	@Override
	public boolean equals(@Nullable Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TitleBarLayoutConfiguration that = (TitleBarLayoutConfiguration) o;
		return tw == that.tw ;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(tw);
	}

	@Override
	public @Nonnull
	String toString()
	{
		return tw.toString();
	}
}
