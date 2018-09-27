/*
 * Changes copyright (c) 2016-2018 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

package consulo.internal.vaqua.impl;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageFilter;
import java.util.ServiceLoader;
import java.util.function.Function;

import javax.swing.JComponent;
import javax.swing.UIDefaults;

import org.jetbrains.annotations.NotNull;
import org.violetlib.aqua.AquaMultiResolutionImage;
import org.violetlib.aqua.AquaPopupFactory;

/**
 * Platform support that varies based on the Java version.
 */
public class JavaSupport
{
	private final static JavaSupportImpl impl = findImpl();

	private static JavaSupportImpl findImpl()
	{
		ServiceLoader<JavaSupportImpl> loader = ServiceLoader.load(JavaSupportImpl.class, JavaSupportImpl.class.getClassLoader());
		for(JavaSupportImpl javaSupport : loader)
		{
			if(javaSupport.isAvaliable())
			{
				return javaSupport;
			}
		}

		throw new Error("Unsupported java version: " + System.getProperty("java.version"));
	}

	public static int getScaleFactor(Graphics g)
	{
		return impl.getScaleFactor(g);
	}

	public static boolean hasOpaqueBeenExplicitlySet(final JComponent c)
	{
		return impl.hasOpaqueBeenExplicitlySet(c);
	}

	public static Image getDockIconImage()
	{
		return impl.getDockIconImage();
	}

	public static void drawString(JComponent c, Graphics2D g, String string, float x, float y)
	{
		impl.drawString(c, g, string, x, y);
	}

	public static void drawStringUnderlineCharAt(JComponent c, Graphics2D g, String string, int underlinedIndex, float x, float y)
	{
		impl.drawStringUnderlineCharAt(c, g, string, underlinedIndex, x, y);
	}

	public static String getClippedString(JComponent c, FontMetrics fm, String string, int availTextWidth)
	{
		return impl.getClippedString(c, fm, string, availTextWidth);
	}

	public static float getStringWidth(JComponent c, FontMetrics fm, String string)
	{
		return impl.getStringWidth(c, fm, string);
	}

	public static void installAATextInfo(UIDefaults table)
	{
		impl.installAATextInfo(table);
	}

	public static AquaMultiResolutionImage createMultiResolutionImage(BufferedImage im)
	{
		return impl.createMultiResolutionImage(im);
	}

	public static AquaMultiResolutionImage createMultiResolutionImage(BufferedImage im1, BufferedImage im2)
	{
		return impl.createMultiResolutionImage(im1, im2);
	}

	public static Image applyFilter(Image image, ImageFilter filter)
	{
		return impl.applyFilter(image, filter);
	}

	public static Image applyMapper(Image source, Function<Image, Image> mapper)
	{
		return impl.applyMapper(source, mapper);
	}

	public static Image applyMapper(Image source, AquaMultiResolutionImage.Mapper mapper)
	{
		return impl.applyMapper(source, mapper);
	}

	/**
	 * Create a buffered image from a raster (created by native code).
	 */
	public static BufferedImage createImage(int width, int height, int[] data)
	{
		return impl.createImage(width, height, data);
	}

	public static void preload(Image image, int availableInfo)
	{
		impl.preload(image, availableInfo);
	}

	// This method supports a work around for bug JDK-8046290, which causes the transient display of garbage pixels.
	public static void lockRenderQueue()
	{
		impl.lockRenderQueue();
	}

	public static void unlockRenderQueue()
	{
		impl.unlockRenderQueue();
	}

	public static AquaPopupFactory createPopupFactory()
	{
		return impl.createPopupFactory();
	}

	@NotNull
	public static Image getResolutionVariant(@NotNull Image source, double width, double height)
	{
		return impl.getResolutionVariant(source, width, height);
	}
}
