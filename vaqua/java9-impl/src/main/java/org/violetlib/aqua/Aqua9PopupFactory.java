/*
 * Copyright (c) 2015-2018 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

package org.violetlib.aqua;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * A popup factory for Java 9 and later.
 */

public class Aqua9PopupFactory extends AquaPopupFactory
{
	private static final Method ourGetPopupMethod;

	static
	{
		try
		{
			ourGetPopupMethod = PopupFactory.class.getDeclaredMethod("getPopup", Component.class, Component.class, int.class, int.class, boolean.class);
			ourGetPopupMethod.setAccessible(true);
		}
		catch(NoSuchMethodException e)
		{
			throw new Error(e);
		}
	}

	@Override
	protected Popup getHeavyweightPopup(Component owner, Component contents, int x, int y)
	{
		// Heavy weight popups are required to support vibrant backgrounds and rounded corners.
		Popup p = null;
		try
		{
			p = (Popup) ourGetPopupMethod.invoke(this, owner, contents, x, y, true);
		}
		catch(IllegalAccessException | InvocationTargetException e)
		{
			throw new Error(e);
		}

		// Reusing popups is not working reliably. Not sure if there is a general timing problem or a change in
		// behavior in El Capitan. The problem is that the stale contents are briefly displayed.
		// See bug JDK-8040630.

		try
		{
			AquaUtils.disablePopupCache(p);
		}
		catch(Throwable th)
		{
			System.err.println("Unable to prevent popup from being reused");
		}

		return p;
	}
}
