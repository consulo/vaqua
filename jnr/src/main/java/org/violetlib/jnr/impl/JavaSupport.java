/*
 * Copyright (c) 2016 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

package org.violetlib.jnr.impl;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ServiceLoader;

import javax.annotation.Nonnull;

/**
 * Platform support that varies based on the Java version.
 */
public class JavaSupport
{

	private final static JavaSupportImpl impl = findImpl();

	public static int getScaleFactor(@Nonnull Graphics g)
	{
		return impl.getScaleFactor(g);
	}

	@Nonnull
	public static Image createMultiResolutionImage(int baseImageWidth, int baseImageHeight, @Nonnull BufferedImage im)
	{
		return impl.createMultiResolutionImage(baseImageWidth, baseImageHeight, im);
	}

	@Nonnull
	private static JavaSupportImpl findImpl()
	{
		ServiceLoader<JavaSupportImpl> loader = ServiceLoader.load(JavaSupportImpl.class, JavaSupportImpl.class.getClassLoader());

		for(JavaSupportImpl support : loader)
		{
			if(support.isAvaliable())
			{
				return support;
			}
		}

		throw new UnsupportedOperationException("Unsupported Java version: " + System.getProperty("java.version"));
	}
}
