/*
 * Copyright (c) 2015 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

package org.violetlib.jnr.aqua.impl;

import java.util.Objects;

import javax.annotation.*;

import org.violetlib.jnr.aqua.Configuration;
import org.violetlib.jnr.aqua.LayoutConfiguration;
import org.violetlib.jnr.aqua.TextFieldConfiguration;

/**
	A pseudo configuration for internal and evaluation use. Should not be used by clients.
*/

public class SearchFieldFindButtonConfiguration
	extends LayoutConfiguration
	implements Configuration
{
	private final @Nonnull
	TextFieldConfiguration g;

	public SearchFieldFindButtonConfiguration(@Nonnull TextFieldConfiguration g)
	{
		this.g = g;
	}

	public @Nonnull
	TextFieldConfiguration getTextFieldConfiguration()
	{
		return g;
	}

	@Override
	public boolean equals(@Nullable Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SearchFieldFindButtonConfiguration that = (SearchFieldFindButtonConfiguration) o;
		return g == that.g;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(g);
	}
}
