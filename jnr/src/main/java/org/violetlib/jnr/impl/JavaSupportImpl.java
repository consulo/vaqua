package org.violetlib.jnr.impl;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.annotation.Nonnull;

public interface JavaSupportImpl
{
	boolean isAvaliable();

	int getScaleFactor(@Nonnull Graphics g);

	Image createMultiResolutionImage(int baseImageWidth, int baseImageHeight, @Nonnull BufferedImage im);
}
