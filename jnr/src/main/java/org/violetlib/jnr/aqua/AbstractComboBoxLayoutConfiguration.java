/*
 * Copyright (c) 2015 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

package org.violetlib.jnr.aqua;

import javax.annotation.Nonnull;

import org.violetlib.jnr.aqua.AquaUIPainter.Size;
import org.violetlib.jnr.aqua.AquaUIPainter.UILayoutDirection;

/**
	A common base class for an editable or non-editable combo box layout configuration.
*/

public abstract class AbstractComboBoxLayoutConfiguration
	extends LayoutConfiguration
{
	public abstract @Nonnull
	Size getSize();

	public abstract @Nonnull
	UILayoutDirection getLayoutDirection();

	public abstract boolean isCell();

	public abstract boolean isLeftToRight();
}
