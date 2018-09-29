/*
 * Copyright (c) 2018 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

package org.violetlib.aqua;

import javax.annotation.Nonnull;
import javax.swing.*;

/**

 */

public interface AquaComponentUI {

    void appearanceChanged(@Nonnull JComponent c, @Nonnull AquaAppearance appearance);

    void activeStateChanged(@Nonnull JComponent c, boolean isActive);
}
