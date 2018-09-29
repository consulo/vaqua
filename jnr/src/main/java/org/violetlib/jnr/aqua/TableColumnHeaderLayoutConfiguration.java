/*
 * Copyright (c) 2015 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

package org.violetlib.jnr.aqua;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.violetlib.jnr.aqua.AquaUIPainter.UILayoutDirection;

/**
	A layout configuration for a table column header.
*/

public class TableColumnHeaderLayoutConfiguration
	extends LayoutConfiguration
{
	private final @Nonnull
	UILayoutDirection ld;
	private final boolean isSortable;

	public TableColumnHeaderLayoutConfiguration(@Nonnull UILayoutDirection ld, boolean isSortable)
	{
		this.ld = ld;
		this.isSortable = isSortable;
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

	public boolean isSortable()
	{
		return isSortable;
	}

	@Override
	public boolean equals(@Nullable Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TableColumnHeaderLayoutConfiguration that = (TableColumnHeaderLayoutConfiguration) o;
		return ld == that.ld && isSortable == that.isSortable;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(ld, isSortable);
	}

	@Override
	public @Nonnull
	String toString()
	{
		String lds = ld == UILayoutDirection.RIGHT_TO_LEFT ? " RTL" : "";
		String ss = isSortable ? " - Sortable" : "";
		return "Table Column Header" + ss + lds;
	}
}
