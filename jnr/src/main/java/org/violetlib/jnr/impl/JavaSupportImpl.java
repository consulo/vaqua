package org.violetlib.jnr.impl;

import javax.annotation.Nonnull;
import java.awt.*;
import java.awt.image.BufferedImage;

public interface JavaSupportImpl {
    boolean isAvaliable();

    int getScaleFactor(@Nonnull Graphics g);

    Image createMultiResolutionImage(int baseImageWidth, int baseImageHeight, @Nonnull BufferedImage im);
}
