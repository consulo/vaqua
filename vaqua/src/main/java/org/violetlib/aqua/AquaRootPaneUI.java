/*
 * Changes Copyright (c) 2015-2018 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

/*
 * Copyright (c) 2011, 2012, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package org.violetlib.aqua;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.MenuBarUI;
import javax.swing.plaf.basic.BasicRootPaneUI;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.violetlib.aqua.AquaVibrantSupport.NO_VIBRANT_STYLE;
import static org.violetlib.aqua.AquaVibrantSupport.WINDOW_BACKGROUND_STYLE;

public class AquaRootPaneUI extends BasicRootPaneUI {

    public final static String AQUA_WINDOW_STYLE_KEY = "Aqua.windowStyle";
    public final static String AQUA_WINDOW_TOP_MARGIN_KEY = "Aqua.windowTopMargin";
    public final static String AQUA_WINDOW_BOTTOM_MARGIN_KEY = "Aqua.windowBottomMargin";

    // Internally, an appearance is represented by an instance of AquaAppearance.
    // Externally, the colors are made available as a map from string to color.

    // TBD: allow the application to set an appearance name for a window

    static final boolean sUseScreenMenuBar = AquaUtils.getScreenMenuBarProperty();

    public static ComponentUI createUI(JComponent c) {
        return new AquaRootPaneUI();
    }

    private static boolean forceActiveWindowDisplay;

    protected JRootPane rootPane;
    protected WindowHierarchyListener hierarchyListener;
    protected AncestorListener ancestorListener;
    protected PropertyChangeListener windowStylePropertyChangeListener;
    protected ChangeListener appearanceChangeListener;
    protected WindowListener windowListener;
    protected ContainerListener containerListener;
    protected boolean isInitialized;
    protected AquaCustomStyledWindow customStyledWindow;
    protected int vibrantStyle = NO_VIBRANT_STYLE;
    protected boolean vibrantStyleIsExplicitlySet;

    /**
     * Set the debugging option to force all windows to display as active. Used to compare a Java window with a native
     * active window.
     */
    public static void setForceActiveWindowDisplay(boolean b) {
        if (b != forceActiveWindowDisplay) {
            forceActiveWindowDisplay = b;
            Window[] windows = Window.getWindows();
            for (Window w : windows) {
                JRootPane rp = AquaUtils.getRootPane(w);
                if (rp != null) {
                    AquaRootPaneUI ui = AquaUtils.getUI(rp, AquaRootPaneUI.class);
                    if (ui != null) {
                        boolean shouldBeActive = b || w.isActive();
                        boolean isActiveStyle = Boolean.TRUE.equals(rp.getClientProperty(AquaFocusHandler.FRAME_ACTIVE_PROPERTY));
                        if (shouldBeActive != isActiveStyle) {
                            rp.repaint();
                            AquaFocusHandler.updateComponentTreeUIActivation(rp, shouldBeActive);
                        }
                    }
                }
            }
        }
    }

    public void installUI(JComponent c) {

        rootPane = (JRootPane) c;

        super.installUI(c);

        // for <rdar://problem/3750909> OutOfMemoryError swapping menus.
        // Listen for layered pane/JMenuBar updates if the screen menu bar is active.
        if (sUseScreenMenuBar) {
            containerListener = new MyContainerListener();
            JRootPane root = (JRootPane)c;
            root.addContainerListener(containerListener);
            root.getLayeredPane().addContainerListener(containerListener);
        }

        configure();
    }

    public void uninstallUI(JComponent c) {
        c.removeAncestorListener(ancestorListener);
        disposeCustomWindowStyle();
        removeVisualEffectView();

        if (sUseScreenMenuBar) {
            JRootPane root = (JRootPane)c;
            root.removeContainerListener(containerListener);
            root.getLayeredPane().removeContainerListener(containerListener);
            containerListener = null;
        }

        isInitialized = false;
        vibrantStyle = NO_VIBRANT_STYLE;
        vibrantStyleIsExplicitlySet = false;

        super.uninstallUI(c);
    }

    @Override
    protected void installListeners(JRootPane root) {
        super.installListeners(root);
        hierarchyListener = new WindowHierarchyListener();
        root.addHierarchyListener(hierarchyListener);
        appearanceChangeListener = new AppearanceChangeListener();
        AquaAppearances.addChangeListener(appearanceChangeListener);
        ancestorListener = new MyAncestorListener();
        root.addAncestorListener(ancestorListener);
        windowListener = new MyWindowListener();
    }

    @Override
    protected void uninstallListeners(JRootPane root) {
        AquaAppearances.removeChangeListener(appearanceChangeListener);
        appearanceChangeListener = null;
        root.removeHierarchyListener(hierarchyListener);
        hierarchyListener = null;
        root.removeAncestorListener(ancestorListener);
        ancestorListener = null;
        Window w = getWindow();
        if (w != null) {
            w.removeWindowListener(windowListener);
        }
        windowListener = null;
        super.uninstallListeners(root);
        if (windowStylePropertyChangeListener != null) {
            root.removePropertyChangeListener(windowStylePropertyChangeListener);
            windowStylePropertyChangeListener = null;
        }
    }

    private class MyContainerListener implements ContainerListener {

        /**
         * If the screen menu bar is active we need to listen to the layered pane of the root pane
         * because it holds the JMenuBar.  So, if a new layered pane was added, listen to it.
         * If a new JMenuBar was added, tell the menu bar UI, because it will need to update the menu bar.
         */
        public void componentAdded(ContainerEvent e) {
            if (e.getContainer() instanceof JRootPane) {
                JRootPane root = (JRootPane) e.getContainer();
                if (e.getChild() == root.getLayeredPane()) {
                    JLayeredPane layered = root.getLayeredPane();
                    layered.addContainerListener(this);
                }
            } else {
                if (e.getChild() instanceof JMenuBar) {
                    JMenuBar jmb = (JMenuBar) e.getChild();
                    MenuBarUI mbui = jmb.getUI();

                    if (mbui.getClass().getName().equals("com.apple.laf.AquaMenuBarUI")) {
                        Window owningWindow = SwingUtilities.getWindowAncestor(jmb);

                        // Could be a JDialog, and may have been added to a JRootPane not yet in a window.
                        if (owningWindow instanceof JFrame) {
                            JFrame fr = (JFrame) owningWindow;
                            AquaUtils.setScreenMenuBar(fr, mbui);
                        }
                    }
                }
            }
        }

        /**
         * Likewise, when the layered pane is removed from the root pane, stop listening to it.
         * If the JMenuBar is removed, tell the menu bar UI to clear the menu bar.
         */
        public void componentRemoved(ContainerEvent e) {
            if (e.getContainer() instanceof JRootPane) {
                JRootPane root = (JRootPane) e.getContainer();
                if (e.getChild() == root.getLayeredPane()) {
                    JLayeredPane layered = root.getLayeredPane();
                    layered.removeContainerListener(this);
                }
            } else {
                if (e.getChild() instanceof JMenuBar) {
                    JMenuBar jmb = (JMenuBar) e.getChild();
                    MenuBarUI mbui = jmb.getUI();

                    if (mbui.getClass().getName().equals("com.apple.laf.AquaMenuBarUI")) {
                        Window owningWindow = SwingUtilities.getWindowAncestor(jmb);

                        // Could be a JDialog, and may have been added to a JRootPane not yet in a window.
                        if (owningWindow instanceof JFrame) {
                            JFrame fr = (JFrame) owningWindow;
                            AquaUtils.clearScreenMenuBar(fr, mbui);
                        }
                    }
                }
            }
        }
    }

    /**
     * Invoked when a property changes on the root pane.
     */
    public void propertyChange(PropertyChangeEvent e) {
        super.propertyChange(e);

        String prop = e.getPropertyName();
        if (AquaVibrantSupport.BACKGROUND_STYLE_KEY.equals(prop)) {
            Object o = e.getNewValue();
            updateVibrantStyleFromProperty(o);
        } else if (AQUA_WINDOW_STYLE_KEY.equals(prop) || AQUA_WINDOW_TOP_MARGIN_KEY.equals(prop) || AQUA_WINDOW_BOTTOM_MARGIN_KEY.equals(prop)) {
            reconfigureCustomWindowStyle();
        } else if ("ancestor".equals(prop)) {
            Component parent = rootPane.getParent();
            if (parent instanceof Window) {
                Window w = (Window) parent;
                KeyWindowPatch.applyPatchIfNeeded(w);
            }
        } else if (AppearanceManager.AQUA_APPEARANCE_NAME_KEY.equals(prop)) {
            configureSpecifiedWindowAppearance(true);
        } else if (AquaFocusHandler.FRAME_ACTIVE_PROPERTY.equals(prop)) {
            Component parent = rootPane.getParent();
            if (parent instanceof Window) {
                Window w = (Window) parent;
                setupBackground(w);
            }
        }
    }

    protected class MyAncestorListener implements AncestorListener {
        /**
         * This is sort of like viewDidMoveToWindow:.  When the root pane is put into a window
         * this method gets called for the notification.
         * We need to set up the listener relationship so we can pick up activation events.
         * And, if a JMenuBar was added before the root pane was added to the window, we now need
         * to notify the menu bar UI.
         */
        public void ancestorAdded(AncestorEvent event) {
            // this is so we can handle window activated and deactivated events so
            // our swing controls can color/enable/disable/focus draw correctly

            Window w = getWindow();
            if (w != null) {
                // We get this message even when a dialog is opened and the owning window is a window
                // that could already be listened to. We should only be a listener once.
                w.removeWindowListener(windowListener);
                w.addWindowListener(windowListener);
            }
        }

        /**
         * If the JRootPane was removed from the window we should clear the screen menu bar.
         * That's a non-trivial problem, because you need to know which window the JRootPane was in
         * before it was removed.  By the time ancestorRemoved was called, the JRootPane has already been removed
         */

        public void ancestorRemoved(AncestorEvent event) {
        }

        public void ancestorMoved(AncestorEvent event) {
        }
    }

    private class MyWindowListener extends WindowAdapter {

        public void windowActivated(WindowEvent e) {
            updateWindowActivation(e, true);
        }

        public void windowDeactivated(WindowEvent e) {
            updateWindowActivation(e, false);
        }
    }

    private static void updateWindowActivation(WindowEvent e, boolean active) {
        if (forceActiveWindowDisplay) {
            active = true;
        }
        Component c = (Component) e.getSource();
        c.repaint(); // much faster to make one repaint call rather than repainting individual components
        AquaFocusHandler.updateComponentTreeUIActivation(c, active);
    }

    @Override
    public final void update(Graphics g, JComponent c) {

        if (!isInitialized) {
            configure();
        }

        AquaAppearance appearance = AppearanceManager.registerCurrentAppearance(c);
        if (c.isOpaque() || vibrantStyle >= 0) {
            // Need a special case here. A dark mode textured window has a vibrant background (needed for a unified
            // title/toolbar), but the content area is opaque.
            int eraserMode = AquaUtils.ERASE_IF_TEXTURED|AquaUtils.ERASE_IF_VIBRANT;
            if (appearance != null && appearance.isDark() && customStyledWindow != null && customStyledWindow.isTextured()) {
                eraserMode = 0;
            }
            AquaUtils.fillRect(g, c, eraserMode);
        }
        if (customStyledWindow != null) {
            customStyledWindow.paintMarginBackgrounds(g);
        }
        paint(g, c);
        AppearanceManager.restoreCurrentAppearance(appearance);
    }

    protected class WindowHierarchyListener implements HierarchyListener {
        @Override
        public void hierarchyChanged(HierarchyEvent e) {
            long flags = e.getChangeFlags();
            if (!isInitialized && (flags & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0) {
                configure();
            }
        }
    }

    protected class AppearanceChangeListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            updateAppearances(null, false);
        }
    }

    /**
     * Configure or reconfigure the window based on root pane client properties and popup client properties, if
     * appropriate. This method has no effect if the root pane parent is not displayable.
     */
    public void configure() {
        if (rootPane.getParent() != null && rootPane.getParent().isDisplayable()) {
            isInitialized = true;
            configureSpecifiedWindowAppearance(false);
            updateAppearances(null, false);
            updatePopupStyle(rootPane);
            reconfigureCustomWindowStyle();
            updateVisualEffectView();
        }
    }

    /**
     * Configure or reconfigure the window based on root pane client properties and popup client properties, if
     * appropriate. This method has no effect if the root pane has no parent.
     * This method forces the root pane parent to become displayable.
     *
     * @param appearanceName If not null, this appearance is installed on the window, overriding the appearance
     * inherited from the application.
     */
    public void configure(@Nullable String appearanceName) {
        Container parent = rootPane.getParent();
        if (parent != null) {
            if (!parent.isDisplayable()) {
                parent.addNotify();
            }
            isInitialized = true;
            updateAppearances(appearanceName, true);  // TBD: must install on native window
            updatePopupStyle(rootPane);
            reconfigureCustomWindowStyle();
            updateVisualEffectView();
        }
    }

    private void configureSpecifiedWindowAppearance(boolean resetIfNull) {
        Window w = getWindow();
        if (w != null && w.isDisplayable()) {
            String appearanceName = getSpecifiedWindowAppearance();
            if (appearanceName != null || resetIfNull) {
                AquaUtils.setWindowAppearance(w, appearanceName);
            }
        }
    }

    private @Nullable String getSpecifiedWindowAppearance() {
        Object o = rootPane.getClientProperty(AppearanceManager.AQUA_APPEARANCE_NAME_KEY);
        if (o instanceof String) {
            return (String) o;
        }
        return null;
    }

    /**
     * This method is called when the native window discovers that it has been assigned a different appearance,
     * meaning an appearance with a different appearance name.
     * @param appearanceName The new appearance name.
     */
    public void windowAppearanceChanged(@NotNull String appearanceName) {
        updateAppearances(appearanceName, false);
    }

    /**
     * Update the appearances in the component tree based on the most current information.
     */

    protected void updateAppearances(@Nullable String appearanceName, boolean forceInitialization) {
        Window w = getWindow();
        if (w != null) {
            updateWindowAppearances(w, appearanceName, forceInitialization);
        }
    }

    protected void updateWindowAppearances(@NotNull Window w,
                                           @Nullable String appearanceName,
                                           boolean forceInitialization) {
        if (appearanceName == null) {
            appearanceName = AquaUtils.getWindowEffectiveAppearanceName(w);
        }
        if (appearanceName != null) {
            AquaAppearance appearance = AquaAppearances.get(appearanceName);
            AquaAppearance oldAppearance = (AquaAppearance) rootPane.getClientProperty(AppearanceManager.AQUA_APPEARANCE_KEY);
            if (appearance != oldAppearance) {
                if (false) {
                    // debug
                    String windowName = AquaUtils.getWindowNameForDebugging(w);
                    String message = "Updating appearances for window " + windowName + ": ";
                    if (oldAppearance == null) {
                        message += "initial appearance " + appearanceName;
                    } else {
                        String oldAppearanceName = oldAppearance.getName();
                        if (appearanceName.equals(oldAppearanceName)) {
                            message += "updated appearance " + appearanceName;
                        } else {
                            message += oldAppearanceName + " -> " + appearanceName;
                        }
                    }
                    System.err.println(message);
                }
                AppearanceManager.installAppearance(rootPane, appearance);
                setupBackground(w);
                AppearanceManager.updateAppearancesInSubtree(rootPane);
                appearanceHasChanged();
            } else if (forceInitialization) {
                setupBackground(w);
                appearanceHasChanged();
            }
        }
    }

    protected void setupBackground(@NotNull Window w) {

        if (OSXSystemProperties.OSVersion >= 1014 && !vibrantStyleIsExplicitlySet) {
            int vibrantStyle = getDefaultWindowVibrantStyle();
            updateVibrantStyle(vibrantStyle, false);
        }

        Color c = AquaUtils.getWindowBackground(rootPane);
        AquaUtils.setBackgroundCarefully(rootPane, c);
    }

    protected int getDefaultWindowVibrantStyle() {
        // Non-textured windows use a vibrant background in dark mode
        // Textured windows use a vibrant background in dark mode but reveal it only in the title/toolbar
        AquaAppearance appearance = AppearanceManager.getAppearance(rootPane);
        if (appearance.isDark()) {
            return WINDOW_BACKGROUND_STYLE;
        }
        return NO_VIBRANT_STYLE;
    }

    protected void appearanceHasChanged() {
        rootPane.repaint();
    }

    protected void updatePopupStyle(JRootPane rp) {
        Window w = SwingUtilities.getWindowAncestor(rp);
        if (w != null && w.getType() == Window.Type.POPUP) {
            Container cp = rp.getContentPane();
            if (cp != null) {
                int count = cp.getComponentCount();
                if (count == 1) {
                    Component c = cp.getComponent(0);
                    if (c instanceof JComponent) {
                        JComponent jc = (JComponent) c;
                        Object o = jc.getClientProperty(AquaVibrantSupport.POPUP_BACKGROUND_STYLE_KEY);
                        updateVibrantStyleFromProperty(o);
                        o = jc.getClientProperty(AquaVibrantSupport.POPUP_CORNER_RADIUS_KEY);
                        configurePopup(o);
                    }
                }
            }
        }
    }

    protected void updateVibrantStyleFromProperty(Object o) {
        int style = AquaVibrantSupport.parseVibrantStyle(o, false);
        updateVibrantStyle(style, style != NO_VIBRANT_STYLE);
    }

    protected void updateVibrantStyle(int style, boolean isSet)
    {
        int oldVibrantStyle = vibrantStyle;
        vibrantStyle = style;
        vibrantStyleIsExplicitlySet = isSet;
        if (vibrantStyle != oldVibrantStyle && isInitialized) {
            updateVisualEffectView();
        }
    }

    protected void configurePopup(Object o) {
        float radius = 0;
        if (o != null) {
            if (o instanceof String) {
                String s = (String) o;
                if (s.equals("default")) {
                    radius = 6;
                }
            } else if (o instanceof Number) {
                radius = ((Number) o).floatValue();
            }
            if (radius < 0) {
                radius = 0;
            }
        }
        Window w = SwingUtilities.getWindowAncestor(rootPane);
        AquaUtils.configurePopup(w, radius);
    }

    protected @Nullable Window getWindow() {
        Container parent = rootPane.getParent();
        return parent instanceof Window ? (Window) parent : null;
    }

    protected void updateVisualEffectView() {
        Window w = getWindow();
        if (w != null) {
            if (false) {
                // debug
                System.err.println("Updating visual effect view: " + vibrantStyle);
            }
            if (vibrantStyle >= 0) {
                try {
                    AquaVibrantSupport.addFullWindowVibrantView(w, vibrantStyle);
                    if (!AquaColors.isPriority(rootPane.getBackground())) {
                        rootPane.setBackground(AquaColors.CLEAR);
                    }
                } catch (IllegalArgumentException ex) {
                    System.err.println("Unable to install visual effect view: " + ex.getMessage());
                }
            } else {
                AquaVibrantSupport.removeFullWindowVibrantView(w);
                // TBD: cannot really undo this
            }
        }
    }

    protected void removeVisualEffectView() {
        Window w = getWindow();
        if (w != null) {
            AquaVibrantSupport.removeFullWindowVibrantView(w);
            if (!AquaColors.isPriority(rootPane.getBackground())) {
                rootPane.setBackground(null);
            }
        }
    }

    protected void reconfigureCustomWindowStyle() {
        Window w = getWindow();
        int style = getCustomWindowStyle();
        if (style < 0 || w == null) {
            uninstallCustomWindowStyle();
        } else {
            int topMarginHeight = getTopMarginHeight();
            int bottomMarginHeight = getBottomMarginHeight();
            if (customStyledWindow != null && !customStyledWindow.isValid(style, topMarginHeight, bottomMarginHeight)) {
                customStyledWindow = customStyledWindow.reconfigure(style, topMarginHeight, bottomMarginHeight);
                setupBackground(w);
            } else if (customStyledWindow == null) {
                try {
                    customStyledWindow = new AquaCustomStyledWindow(w, style, topMarginHeight, bottomMarginHeight);
                    setupBackground(w);
                } catch (AquaCustomStyledWindow.RequiredToolBarNotFoundException ex) {
                    // This exception would be thrown if the window style is set before adding the tool bar to the
                    // content pane, which would not be an error.
                } catch (IllegalArgumentException ex) {
                    AquaUtils.syslog("Unable to install custom window style: " + ex.getMessage());
                }
            }
        }
    }

    protected void uninstallCustomWindowStyle() {
        if (customStyledWindow != null) {
            customStyledWindow.dispose();
            customStyledWindow = null;
            Window w = getWindow();
            if (w != null) {
                setupBackground(w);
            }
        }
    }

    private void disposeCustomWindowStyle() {
        if (customStyledWindow != null) {
            customStyledWindow.dispose();
            customStyledWindow = null;
        }
    }

    public AquaCustomStyledWindow getCustomStyledWindow() {
        return customStyledWindow;
    }

    public static @Nullable String getWindowStyleKey(@NotNull JRootPane rp) {
        Object o = rp.getClientProperty(AQUA_WINDOW_STYLE_KEY);
        return o instanceof String ? (String) o : null;
    }

    protected int getCustomWindowStyle() {
        String key = getWindowStyleKey(rootPane);
        if (key != null) {
            if ("unifiedToolBar".equals(key)) {
                return AquaCustomStyledWindow.STYLE_UNIFIED;
            }
            if ("texturedToolBar".equals(key)) {
                return AquaCustomStyledWindow.STYLE_TEXTURED_HIDDEN;
            }
            if ("combinedToolBar".equals(key)) {
                return AquaCustomStyledWindow.STYLE_COMBINED;
            }
            if ("overlayTitleBar".equals(key)) {
                return AquaCustomStyledWindow.STYLE_OVERLAY;
            }
            if ("transparentTitleBar".equals(key)) {
                return AquaCustomStyledWindow.STYLE_TRANSPARENT;
            }
            if ("noTitleBar".equals(key)) {
                return AquaCustomStyledWindow.STYLE_HIDDEN;
            }
            if ("undecorated".equals(key)) {
                return AquaCustomStyledWindow.STYLE_UNDECORATED;
            }
        }

        return -1;
    }

    protected int getTopMarginHeight() {
        Integer n = AquaUtils.getIntegerProperty(rootPane, AquaRootPaneUI.AQUA_WINDOW_TOP_MARGIN_KEY);
        return n != null ? n : -1;
    }

    protected int getBottomMarginHeight() {
        Integer n = AquaUtils.getIntegerProperty(rootPane, AquaRootPaneUI.AQUA_WINDOW_BOTTOM_MARGIN_KEY);
        return n != null ? n : -1;
    }
}
