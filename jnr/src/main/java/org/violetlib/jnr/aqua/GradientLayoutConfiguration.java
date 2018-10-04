/*
 * Copyright (c) 2015 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

package org.violetlib.jnr.aqua;

import javax.annotation.Nonnull;

/**
 A layout configuration for a gradient painter.
*/

public class GradientLayoutConfiguration
	extends LayoutConfiguration
{
	@Override
	public @Nonnull
	String toString()
	{
		return "Gradient";
	}
}
