/*
 * Copyright (c) 2015-2016 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

/*
 * Copyright (c) 2011, 2014, Oracle and/or its affiliates. All rights reserved.
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
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;

import org.violetlib.aqua.AquaUtils.RecyclableSingleton;
import org.violetlib.jnr.aqua.AquaUIPainter;
import org.violetlib.jnr.aqua.AquaUIPainter.ButtonWidget;
import org.violetlib.jnr.aqua.AquaUIPainter.Position;
import org.violetlib.jnr.aqua.AquaUIPainter.SegmentedButtonWidget;

import static org.violetlib.jnr.aqua.AquaUIPainter.ButtonWidget.*;
import static org.violetlib.jnr.aqua.AquaUIPainter.PopupButtonWidget.*;
import static org.violetlib.jnr.aqua.AquaUIPainter.SegmentedButtonWidget.*;

/**
 * Map client properties to borders.
 */
public class AquaButtonExtendedTypes {

    public static class ColorDefaults {
        public Color enabledTextColor;
        public Color selectedTextColor;
        public Color disabledTextColor;

        public Color getTextColor(boolean isEnabled, boolean isSelected) {
            if (isEnabled) {
                return isSelected ? selectedTextColor : enabledTextColor;
            } else {
                return disabledTextColor;
            }
        }
    }

    /**
     * Identify a button type specifier based on the client properties of a button.
     * @param b The button component.
     * @return the button type specifier, or null if a button type was not declared or the button type was not recognized.
     */
    public static TypeSpecifier getTypeSpecifier(AbstractButton b) {
        Object buttonTypeProperty = b.getClientProperty(AquaButtonUI.BUTTON_TYPE);
        Object segmentPositionProperty = b.getClientProperty(AquaButtonUI.SEGMENTED_BUTTON_POSITION);

        if (buttonTypeProperty == null) {
            if (segmentPositionProperty != null || b.getUI().getClass() == AquaButtonToggleUI.class) {
                buttonTypeProperty = "segmented";
            } else {
                return null;
            }
        }

        if (buttonTypeProperty instanceof String) {
            String buttonType = (String) buttonTypeProperty;

            if (segmentPositionProperty instanceof String) {
                String segmentPosition = (String) segmentPositionProperty;
                if (buttonType.equals("segmented")) {
                    if (AquaButtonUI.isOnToolbar(b)) {
                        buttonType = "segmentedTextured";
                    }
                } else if (buttonType.equals("segmentedSeparated")) {
                    if (AquaButtonUI.isOnToolbar(b)) {
                        buttonType = "segmentedTexturedSeparated";
                    }
                }

                String typeName = buttonType + "-" + getRealPositionForLogicalPosition(segmentPosition, b.getComponentOrientation().isLeftToRight());
                final TypeSpecifier specifier = getSpecifierByName(b, typeName);
                if (specifier != null) {
                    return specifier;
                }
            }

            if (buttonType.equals("round") && AquaButtonUI.isOnToolbar(b)) {
                buttonType = "roundTextured";
            }

            return getSpecifierByName(b, buttonType);
        }

        return null;
    }

    public static WidgetInfo getTabWidgetInfo(AquaUIPainter.Size sz, Position pos) {
        return widgetDefinitions.get().get(BUTTON_TAB);
    }

    protected static String getRealPositionForLogicalPosition(String logicalPosition, boolean leftToRight) {
        if (!leftToRight) {
            if ("first".equalsIgnoreCase(logicalPosition)) return "last";
            if ("last".equalsIgnoreCase(logicalPosition)) return "first";
        }
        return logicalPosition;
    }

    public static abstract class TypeSpecifier {
        final String name;

        protected TypeSpecifier(final String name) {
            this.name = name;
        }

        public abstract Border getBorder();
    }

    public static class FixedBorderTypeSpecifier extends TypeSpecifier {
        private final AquaButtonBorder border;

        public FixedBorderTypeSpecifier(final String name, final AquaButtonBorder border) {
            super(name);

            this.border = border;
        }

        public AquaButtonBorder getBorder() {
            return border;
        }
    }

    public static class BorderDefinedTypeSpecifier extends TypeSpecifier {
        private final ButtonWidget widget;
        private final WidgetInfo info;

        public BorderDefinedTypeSpecifier(final String name, final ButtonWidget widget) {
            super(name);

            this.widget = widget;
            this.info = getWidgetInfo(widget);
        }

        public AquaButtonBorder getBorder() {
            return new AquaNamedButtonBorder(widget, info);
        }
    }

    public static class SegmentedTypeSpecifier extends TypeSpecifier {
        private final SegmentedButtonWidget widget;
        private final WidgetInfo info;
        private final Position position;

        public SegmentedTypeSpecifier(final String name,
                                      final SegmentedButtonWidget widget,
                                      final Position position) {
            super(name);

            this.widget = widget;
            this.info = getWidgetInfo(widget);
            this.position = position;
        }

        public AquaButtonBorder getBorder() {
            return new AquaSegmentedButtonBorder(widget, info, position);
        }
    }

    public static TypeSpecifier getSpecifierByName(AbstractButton b, String name) {
        if (AquaButtonUI.isOnToolbar(b)) {
            String toolbarName = name + "-onToolbar";
            TypeSpecifier specifier = typeDefinitions.get().get(toolbarName);
            if (specifier != null) {
                return specifier;
            }
        }

        return typeDefinitions.get().get(name);
    }

    protected final static RecyclableSingleton<Map<String, TypeSpecifier>> typeDefinitions = new RecyclableSingleton<Map<String, TypeSpecifier>>() {
        protected Map<String, TypeSpecifier> getInstance() {
            return getAllTypes();
        }
    };

    /**
     * Return the font to use for a button based on a button widget.
     * This capability is needed for button types that determine the widget based on the button content size.
     * @param buttonFont The default font without taking the button widget into account.
     * @param widget The button widget.
     * @param size The size variant.
     * @return the font to use.
     */
    public static Font getFont(Font buttonFont, Object widget, AquaUIPainter.Size size) {
        WidgetInfo info = getWidgetInfo(widget);
        assert info != null;
        Font font = info.getFont(size);
        if (font != null) {
            return font;
        }
        if (size != null && size != AquaUIPainter.Size.REGULAR) {
            float fontSize = getFontSize(size);
            return buttonFont.deriveFont(fontSize);
        }

        return buttonFont;
    }

    protected static float getFontSize(AquaUIPainter.Size size) {
        switch (size) {
            case SMALL:
                return 11;
            case MINI:
                return 9;
            default:
                return 13;
        }
    }

    protected final static WidgetInfo defaultButtonWidgetInfo = new WidgetInfo();
    protected final static WidgetInfo defaultSegmentedButtonWidgetInfo = new WidgetInfo().withSegmented();

    public static WidgetInfo getWidgetInfo(Object widget) {
        WidgetInfo info = widgetDefinitions.get().get(widget);
        if (info != null) {
            return info;
        }
        if (widget instanceof SegmentedButtonWidget) {
            return defaultSegmentedButtonWidgetInfo;
        }
        if (widget instanceof ButtonWidget) {
            return defaultButtonWidgetInfo;
        }
        return defaultButtonWidgetInfo;
    }

    protected final static RecyclableSingleton<Map<Object, WidgetInfo>> widgetDefinitions = new RecyclableSingleton<Map<Object, WidgetInfo>>() {
        protected Map<Object, WidgetInfo> getInstance() {
            return getAllWidgets();
        }
    };

    protected interface FontFinder {
        Font getFont(AquaUIPainter.Size size);
    }

    public static class WidgetInfo implements Cloneable {
        private boolean isSegmented;
        private boolean isTextured;
        private Color foreground;
        private Color selectedForeground;
        private Color inactiveForeground;
        private Color inactiveSelectedForeground;
        private Color disabledForeground;
        private Color disabledSelectedForeground;
        private Color inactiveDisabledForeground;
        private Color inactiveDisabledSelectedForeground;
        private Color rolloverForeground;
        private Color pressedForeground;
        private Color selectedPressedForeground;
        private Color iconPressedForeground;
        private Color activeDefaultButtonForeground;
        private Font font;
        private FontFinder fontFinder;
        private float fontSize;
        private boolean isRolloverEnabled;
        private int iconTextGap;
        private int margin;

        WidgetInfo() {
        }

        WidgetInfo copy() {
            try {
                return (WidgetInfo) clone();
            } catch (CloneNotSupportedException ex) {
                // should not happen
                throw new RuntimeException("Unable to clone WidgetInfo");
            }
        }

        WidgetInfo withSegmented() {
            this.isSegmented = true;
            return this;
        }

        WidgetInfo withTextured() {
            this.isTextured = true;
            return this;
        }

        WidgetInfo withFont(Font f) {
            this.font = f;
            return this;
        }

        WidgetInfo withFont(FontFinder ff) {
            this.fontFinder = ff;
            return this;
        }

        WidgetInfo withFontSize(float fontSize) {
            this.fontSize = fontSize;
            return this;
        }

        WidgetInfo withIconTextGap(int gap) {
            this.iconTextGap = gap;
            return this;
        }

        WidgetInfo withMargin(int margin) {
            this.margin = margin;
            return this;
        }

        WidgetInfo withPressed(Color pressed) {
            pressedForeground = pressed;
            return this;
        }

        WidgetInfo withPressed(Color selected, Color unselected) {
            selectedPressedForeground = selected;
            pressedForeground = unselected;
            return this;
        }

        WidgetInfo withIconPressed(Color pressed) {
            iconPressedForeground = pressed;
            return this;
        }

        WidgetInfo withForeground(Color fg) {
            foreground = fg;
            return this;
        }

        WidgetInfo withForeground(Color fg, Color disabled) {
            foreground = fg;
            disabledForeground = disabled;
            return this;
        }

        WidgetInfo withForeground(Color fg, Color disabled, Color pressed) {
            foreground = fg;
            disabledForeground = disabled;
            pressedForeground = pressed;
            return this;
        }

        WidgetInfo withForeground(Color fg, Color selected, Color disabled, Color pressed) {
            foreground = fg;
            selectedForeground = selected;
            disabledForeground = disabled;
            pressedForeground = pressed;
            return this;
        }

        WidgetInfo withEnabledForeground(Color selected, Color unselected) {
            foreground = unselected;
            selectedForeground = selected;
            return this;
        }

        WidgetInfo withDisabledForeground(Color selected, Color unselected) {
            disabledSelectedForeground = selected;
            disabledForeground = unselected;
            return this;
        }

        WidgetInfo withInactiveForeground(Color c) {
            inactiveForeground = c;
            return this;
        }

        WidgetInfo withInactiveForeground(Color selected, Color unselected) {
            inactiveSelectedForeground = selected;
            inactiveForeground = unselected;
            return this;
        }

        WidgetInfo withInactiveDisabledForeground(Color c) {
            inactiveDisabledForeground = c;
            return this;
        }

        WidgetInfo withInactiveDisabledForeground(Color selected, Color unselected) {
            inactiveDisabledSelectedForeground = selected;
            inactiveDisabledForeground = unselected;
            return this;
        }

        WidgetInfo withActiveDefaultButtonForeground(Color c) {
            activeDefaultButtonForeground = c;
            return this;
        }

        WidgetInfo withRolloverEnabled(Color rolloverForeground) {
            this.isRolloverEnabled = true;
            this.rolloverForeground = rolloverForeground;
            return this;
        }

        public int getIconTextGap() {
            return iconTextGap;
        }

        public int getMargin() {
            return margin;
        }

        public Font getFont(AquaUIPainter.Size size) {
            if (fontFinder != null) {
                return fontFinder.getFont(size);
            }

            if (font != null) {
                if (size != null && size != AquaUIPainter.Size.REGULAR) {
                    float fontSize = getFontSize(size);
                    return font.deriveFont(fontSize);
                }
            }

            return font;
        }

        public Color getForeground(AquaUIPainter.State state,
                                   AquaUIPainter.ButtonState bs,
                                   ColorDefaults colorDefaults,
                                   boolean useNonexclusiveStyle,
                                   boolean isIcon) {

            // Special case for a textured segmented button that is not in a button group.
            if (useNonexclusiveStyle) {
                if (state == AquaUIPainter.State.DISABLED || state == AquaUIPainter.State.DISABLED_INACTIVE) {
                    return UIManager.getColor("Button.texturedDisabledNonexclusiveSelectedColor");
                } else {
                    return UIManager.getColor("Button.texturedNonexclusiveSelectedColor");
                }
            }

            if (isRolloverEnabled && state == AquaUIPainter.State.ROLLOVER && rolloverForeground != null) {
                return rolloverForeground;
            }

            if (state == AquaUIPainter.State.PRESSED) {
                if (isIcon && iconPressedForeground != null) {
                    return iconPressedForeground;
                }
                if (bs == AquaUIPainter.ButtonState.ON && selectedPressedForeground != null) {
                    return selectedPressedForeground;
                }
                if (pressedForeground != null) {
                    return pressedForeground;
                }
            }

            if (state == AquaUIPainter.State.DISABLED_INACTIVE) {
                if (bs == AquaUIPainter.ButtonState.ON) {
                    if (inactiveDisabledSelectedForeground != null) {
                        return inactiveDisabledSelectedForeground;
                    }
                }

                if (inactiveDisabledForeground != null) {
                    return inactiveDisabledForeground;
                } else {
                    // Many buttons do not change when they are inactive, so use the disabled values
                    state = AquaUIPainter.State.DISABLED;
                }
            }

            if (state == AquaUIPainter.State.DISABLED) {
                if (bs == AquaUIPainter.ButtonState.ON) {
                    if (disabledSelectedForeground != null) {
                        return disabledSelectedForeground;
                    }
                }

                if (disabledForeground != null) {
                    return disabledForeground;
                } else {
                    return colorDefaults.disabledTextColor;
                }
            }

            if (state == AquaUIPainter.State.INACTIVE) {
                if (bs == AquaUIPainter.ButtonState.ON) {
                    if (inactiveSelectedForeground != null) {
                        return inactiveSelectedForeground;
                    }
                }

                if (inactiveForeground != null) {
                    return inactiveForeground;
                }
            }

            if (bs == AquaUIPainter.ButtonState.ON) {
                if (selectedForeground != null) {
                    return selectedForeground;
                }

                if (colorDefaults.selectedTextColor != null) {
                    return colorDefaults.selectedTextColor;
                }
            }

            if (state == AquaUIPainter.State.ACTIVE_DEFAULT) {
                if (activeDefaultButtonForeground != null) {
                    return activeDefaultButtonForeground;
                }
            }

            if (foreground != null) {
                return foreground;
            } else {
                return colorDefaults.enabledTextColor;
            }
        }

        public Color getTemplateSelectedColor(boolean useNonexclusive, ColorDefaults colorDefaults) {
            return getForeground(AquaUIPainter.State.ACTIVE, AquaUIPainter.ButtonState.ON, colorDefaults, useNonexclusive, true);
        }

        public Color getTemplateDisabledSelectedColor(boolean useNonexclusive, ColorDefaults colorDefaults) {
            return getForeground(AquaUIPainter.State.DISABLED, AquaUIPainter.ButtonState.ON, colorDefaults, useNonexclusive, true);
        }

        public Color getTemplateUnselectedColor(ColorDefaults colorDefaults) {
            return getForeground(AquaUIPainter.State.ACTIVE, AquaUIPainter.ButtonState.OFF, colorDefaults, false, true);
        }

        public Color getTemplateDisabledUnselectedColor(ColorDefaults colorDefaults) {
            return getForeground(AquaUIPainter.State.DISABLED, AquaUIPainter.ButtonState.OFF, colorDefaults, false, true);
        }

        public boolean isSegmented() {
            return isSegmented;
        }

        public boolean isTextured() {
            return isTextured;
        }

        public boolean isRolloverEnabled() {
            return isRolloverEnabled;
        }
    }

    protected static Map<Object, WidgetInfo> getAllWidgets() {
        final Map<Object, WidgetInfo> result = new HashMap<>();

        Color black34 = new ColorUIResource(new Color(34, 34, 34));
        Color dark64 = new ColorUIResource(new Color(0, 0, 0, 64));
        Color dark140 = new ColorUIResource(new Color(0, 0, 0, 140));
        Color dark170 = new ColorUIResource(new Color(0, 0, 0, 170));
        Color dark220 = new ColorUIResource(new Color(0, 0, 0, 220));
        Color light150 = new ColorUIResource(new Color(255, 255, 255, 150));
        Color light180 = new ColorUIResource(new Color(255, 255, 255, 180));
        Color white = new ColorUIResource(Color.WHITE);
        Color pressedWhite = new ColorUIResource(new Color(255, 255, 255, 220));
        Color defaultWhite = new ColorUIResource(250, 250, 250);

        Color texturedUnselected = UIManager.getColor("Button.texturedUnselectedColor");
        Color texturedSelected = UIManager.getColor("Button.texturedSelectedColor");
        Color texturedDisabledUnselected = UIManager.getColor("Button.texturedDisabledUnselectedColor");
        Color texturedDisabledSelected = UIManager.getColor("Button.texturedDisabledSelectedColor");

        result.put(BUTTON_CHECK_BOX, new WidgetInfo());
        result.put(BUTTON_RADIO, new WidgetInfo());

        result.put(BUTTON_PUSH, new WidgetInfo()
                .withMargin(5)
                .withForeground(black34, dark64, pressedWhite)
                .withActiveDefaultButtonForeground(defaultWhite)
        );

        WidgetInfo segmentedRounded = new WidgetInfo()
                .withSegmented()
                .withMargin(9)
                .withForeground(black34, white, null, null)
                .withDisabledForeground(new AquaUtils.GrayUIResource(172), dark64)
                .withInactiveForeground(black34, null)
                ;

        result.put(BUTTON_SEGMENTED, segmentedRounded);
        result.put(BUTTON_TAB, segmentedRounded);
        result.put(BUTTON_SEGMENTED_SEPARATED, segmentedRounded);

        WidgetInfo gradient = new WidgetInfo().withForeground(dark220, dark220, dark64, null);
        result.put(BUTTON_GRADIENT, gradient.withMargin(2));
        result.put(BUTTON_BEVEL, gradient.withMargin(4));
        result.put(BUTTON_BEVEL_ROUND, gradient.withMargin(6));
        result.put(BUTTON_ROUNDED_RECT, gradient.withMargin(4));

        // Round buttons are like gradient buttons, but white looks better on the blue background
        result.put(BUTTON_ROUND, new WidgetInfo().withForeground(dark220, white, dark64, null));

        result.put(BUTTON_TOOLBAR_ITEM, new WidgetInfo()
                .withForeground(new AquaUtils.GrayUIResource(89), new AquaUtils.GrayUIResource(81), null, null)
                .withDisabledForeground(new AquaUtils.GrayUIResource(134), new AquaUtils.GrayUIResource(150))
                .withInactiveForeground(new AquaUtils.GrayUIResource(153), new AquaUtils.GrayUIResource(172))
                .withInactiveDisabledForeground(new AquaUtils.GrayUIResource(198), new AquaUtils.GrayUIResource(173))
                .withFont(size -> size == AquaUIPainter.Size.SMALL || size == AquaUIPainter.Size.MINI
                                ? UIManager.getFont("IconButton.smallFont")
                                : UIManager.getFont("IconButton.font"))
                .withIconTextGap(2)
        );

        result.put(BUTTON_INLINE, new WidgetInfo()
                .withFont(UIManager.getFont("Button.inline.font"))
                .withForeground(white, new AquaUtils.GrayUIResource(240 /* 208 */))  // 208 is unreadable
                .withIconPressed(pressedWhite)
        );

        // Textured toggle push buttons and segmented buttons are not identical, but they are getting closer and
        // probably should be identical.

        WidgetInfo textured = new WidgetInfo()
                .withTextured()
                .withEnabledForeground(texturedSelected, texturedUnselected)
                .withPressed(white, black34)
                .withDisabledForeground(texturedDisabledSelected, texturedDisabledUnselected)
                .withInactiveForeground(new AquaUtils.GrayUIResource(164), new AquaUtils.GrayUIResource(178))
                .withInactiveDisabledForeground(new AquaUtils.GrayUIResource(195), new AquaUtils.GrayUIResource(211))
                ;

        result.put(BUTTON_TEXTURED, textured);
        result.put(BUTTON_TEXTURED_TOOLBAR, textured);
        result.put(BUTTON_ROUND_TEXTURED, textured);

        WidgetInfo segmentedTextured = textured.copy().withSegmented().withMargin(9);

        result.put(BUTTON_SEGMENTED_TEXTURED, segmentedTextured);
        result.put(BUTTON_SEGMENTED_TEXTURED_SEPARATED, segmentedTextured);
        result.put(BUTTON_SEGMENTED_TEXTURED_TOOLBAR, segmentedTextured);
        result.put(BUTTON_SEGMENTED_TEXTURED_SEPARATED_TOOLBAR, segmentedTextured);

        WidgetInfo segmentedGradient = gradient.copy().withSegmented().withMargin(9);
        result.put(BUTTON_SEGMENTED_INSET, segmentedGradient);
        result.put(BUTTON_SEGMENTED_SCURVE, segmentedGradient);
        result.put(BUTTON_SEGMENTED_SMALL_SQUARE, segmentedGradient);

        result.put(BUTTON_RECESSED, new WidgetInfo()
                .withFont(UIManager.getFont("Button.recessed.font"))
                .withRolloverEnabled(UIManager.getColor("Button.recessed.rolloverText"))
                .withForeground(dark170, white)
                .withDisabledForeground(light150, dark64)
                .withInactiveForeground(dark64)
                .withInactiveDisabledForeground(light180, dark64 /* dark140 */) // dark140 looks wrong
                .withPressed(white)
        );

        WidgetInfo pushPopUp = new WidgetInfo()
                .withMargin(5)
                .withForeground(black34, dark64, dark220)
                ;

        result.put(BUTTON_POP_DOWN, pushPopUp);
        result.put(BUTTON_POP_UP, pushPopUp);

        result.put(BUTTON_POP_DOWN_BEVEL, result.get(BUTTON_BEVEL));
        result.put(BUTTON_POP_UP_BEVEL, result.get(BUTTON_BEVEL));

        result.put(BUTTON_POP_DOWN_ROUND_RECT, result.get(BUTTON_ROUNDED_RECT));
        result.put(BUTTON_POP_UP_ROUND_RECT, result.get(BUTTON_ROUNDED_RECT));

        result.put(BUTTON_POP_DOWN_RECESSED, result.get(BUTTON_RECESSED));
        result.put(BUTTON_POP_UP_RECESSED, result.get(BUTTON_RECESSED));

        result.put(BUTTON_POP_DOWN_TEXTURED, result.get(BUTTON_TEXTURED));
        result.put(BUTTON_POP_UP_TEXTURED, result.get(BUTTON_TEXTURED));

        result.put(BUTTON_POP_DOWN_TEXTURED_TOOLBAR, result.get(BUTTON_TEXTURED_TOOLBAR));
        result.put(BUTTON_POP_UP_TEXTURED_TOOLBAR, result.get(BUTTON_TEXTURED_TOOLBAR));

        result.put(BUTTON_POP_DOWN_GRADIENT, result.get(BUTTON_GRADIENT));
        result.put(BUTTON_POP_UP_GRADIENT, result.get(BUTTON_GRADIENT));

        return result;
    }

    protected static Map<String, TypeSpecifier> getAllTypes() {
        final Map<String, TypeSpecifier> specifiersByName = new HashMap<String, TypeSpecifier>();

        final TypeSpecifier[] specifiers = {
            new FixedBorderTypeSpecifier("toolbar", AquaButtonBorder.getToolBarToggleButtonBorder()),
            new FixedBorderTypeSpecifier("icon", AquaButtonBorder.getIconToggleButtonBorder()),
            new FixedBorderTypeSpecifier("text", (AquaButtonBorder) UIManager.getBorder("Button.border")),
            new FixedBorderTypeSpecifier("toggle", AquaButtonBorder.getToggleButtonBorder()),
            new FixedBorderTypeSpecifier("disclosureTriangle", AquaButtonBorder.getDisclosureTriangleButtonBorder()),
            new FixedBorderTypeSpecifier("disclosure", AquaButtonBorder.getDisclosureButtonBorder()),

            new BorderDefinedTypeSpecifier("checkbox", BUTTON_CHECK_BOX),
            new BorderDefinedTypeSpecifier("radio", BUTTON_RADIO),

            new BorderDefinedTypeSpecifier("square", BUTTON_BEVEL),
            new BorderDefinedTypeSpecifier("gradient", BUTTON_GRADIENT),
            new BorderDefinedTypeSpecifier("bevel", BUTTON_BEVEL_ROUND),

            new BorderDefinedTypeSpecifier("textured", BUTTON_TEXTURED),
            new BorderDefinedTypeSpecifier("textured-onToolbar", BUTTON_TEXTURED_TOOLBAR),
            new BorderDefinedTypeSpecifier("roundRect", BUTTON_ROUNDED_RECT),
            new BorderDefinedTypeSpecifier("recessed", BUTTON_RECESSED),
            new BorderDefinedTypeSpecifier("inline", BUTTON_INLINE),
            new BorderDefinedTypeSpecifier("well", BUTTON_TOOLBAR_ITEM),   // old name from Aqua LAF
            new BorderDefinedTypeSpecifier("toolbarItem", BUTTON_TOOLBAR_ITEM),
            new BorderDefinedTypeSpecifier("help", BUTTON_HELP),
            new BorderDefinedTypeSpecifier("round", BUTTON_ROUND),
            new BorderDefinedTypeSpecifier("round-onToolbar", OSXSystemProperties.OSVersion >= 1011 ? BUTTON_ROUND_TOOLBAR : BUTTON_ROUND),
            new BorderDefinedTypeSpecifier("texturedRound", BUTTON_ROUND_INSET),   // TBD: this is not correct, but the button type is undocumented
            new BorderDefinedTypeSpecifier("roundTextured", BUTTON_ROUND_TEXTURED),
            new BorderDefinedTypeSpecifier("roundTextured-onToolbar", OSXSystemProperties.OSVersion >= 1011 ? BUTTON_ROUND_TOOLBAR : BUTTON_ROUND_TEXTURED),
            new BorderDefinedTypeSpecifier("roundInset", BUTTON_ROUND_INSET),
            new BorderDefinedTypeSpecifier("colorWell", BUTTON_COLOR_WELL),

            new SegmentedTypeSpecifier("segmented-first", BUTTON_SEGMENTED, Position.FIRST),
            new SegmentedTypeSpecifier("segmented-middle", BUTTON_SEGMENTED, Position.MIDDLE),
            new SegmentedTypeSpecifier("segmented-last", BUTTON_SEGMENTED, Position.LAST),
            new SegmentedTypeSpecifier("segmented-only", BUTTON_SEGMENTED, Position.ONLY),

            new SegmentedTypeSpecifier("segmentedSeparated-first", BUTTON_SEGMENTED_SEPARATED, Position.FIRST),
            new SegmentedTypeSpecifier("segmentedSeparated-middle", BUTTON_SEGMENTED_SEPARATED, Position.MIDDLE),
            new SegmentedTypeSpecifier("segmentedSeparated-last", BUTTON_SEGMENTED_SEPARATED, Position.LAST),
            new SegmentedTypeSpecifier("segmentedSeparated-only", BUTTON_SEGMENTED_SEPARATED, Position.ONLY),

            new SegmentedTypeSpecifier("segmentedRoundRect-first", BUTTON_SEGMENTED_INSET, Position.FIRST),
            new SegmentedTypeSpecifier("segmentedRoundRect-middle", BUTTON_SEGMENTED_INSET, Position.MIDDLE),
            new SegmentedTypeSpecifier("segmentedRoundRect-last", BUTTON_SEGMENTED_INSET, Position.LAST),
            new SegmentedTypeSpecifier("segmentedRoundRect-only", BUTTON_SEGMENTED_INSET, Position.ONLY),

            new SegmentedTypeSpecifier("segmentedTexturedRounded-first", BUTTON_SEGMENTED_SCURVE, Position.FIRST),
            new SegmentedTypeSpecifier("segmentedTexturedRounded-middle", BUTTON_SEGMENTED_SCURVE, Position.MIDDLE),
            new SegmentedTypeSpecifier("segmentedTexturedRounded-last", BUTTON_SEGMENTED_SCURVE, Position.LAST),
            new SegmentedTypeSpecifier("segmentedTexturedRounded-only", BUTTON_SEGMENTED_SCURVE, Position.ONLY),

            new SegmentedTypeSpecifier("segmentedTextured-first", BUTTON_SEGMENTED_TEXTURED, Position.FIRST),
            new SegmentedTypeSpecifier("segmentedTextured-middle", BUTTON_SEGMENTED_TEXTURED, Position.MIDDLE),
            new SegmentedTypeSpecifier("segmentedTextured-last", BUTTON_SEGMENTED_TEXTURED, Position.LAST),
            new SegmentedTypeSpecifier("segmentedTextured-only", BUTTON_SEGMENTED_TEXTURED, Position.ONLY),

            new SegmentedTypeSpecifier("segmentedTextured-first-onToolbar", BUTTON_SEGMENTED_TEXTURED_TOOLBAR, Position.FIRST),
            new SegmentedTypeSpecifier("segmentedTextured-middle-onToolbar", BUTTON_SEGMENTED_TEXTURED_TOOLBAR, Position.MIDDLE),
            new SegmentedTypeSpecifier("segmentedTextured-last-onToolbar", BUTTON_SEGMENTED_TEXTURED_TOOLBAR, Position.LAST),
            new SegmentedTypeSpecifier("segmentedTextured-only-onToolbar", BUTTON_SEGMENTED_TEXTURED_TOOLBAR, Position.ONLY),

            new SegmentedTypeSpecifier("segmentedCapsule-first", BUTTON_SEGMENTED_TOOLBAR, Position.FIRST),
            new SegmentedTypeSpecifier("segmentedCapsule-middle", BUTTON_SEGMENTED_TOOLBAR, Position.MIDDLE),
            new SegmentedTypeSpecifier("segmentedCapsule-last", BUTTON_SEGMENTED_TOOLBAR, Position.LAST),
            new SegmentedTypeSpecifier("segmentedCapsule-only", BUTTON_SEGMENTED_TOOLBAR, Position.ONLY),

            new SegmentedTypeSpecifier("segmentedGradient-first", BUTTON_SEGMENTED_SMALL_SQUARE, Position.FIRST),
            new SegmentedTypeSpecifier("segmentedGradient-middle", BUTTON_SEGMENTED_SMALL_SQUARE, Position.MIDDLE),
            new SegmentedTypeSpecifier("segmentedGradient-last", BUTTON_SEGMENTED_SMALL_SQUARE, Position.LAST),
            new SegmentedTypeSpecifier("segmentedGradient-only", BUTTON_SEGMENTED_SMALL_SQUARE, Position.ONLY),

            new SegmentedTypeSpecifier("segmentedTexturedSeparated-first", BUTTON_SEGMENTED_TEXTURED_SEPARATED, Position.FIRST),
            new SegmentedTypeSpecifier("segmentedTexturedSeparated-middle", BUTTON_SEGMENTED_TEXTURED_SEPARATED, Position.MIDDLE),
            new SegmentedTypeSpecifier("segmentedTexturedSeparated-last", BUTTON_SEGMENTED_TEXTURED_SEPARATED, Position.LAST),
            new SegmentedTypeSpecifier("segmentedTexturedSeparated-only", BUTTON_SEGMENTED_TEXTURED_SEPARATED, Position.ONLY),

            new SegmentedTypeSpecifier("segmentedTexturedSeparated-first-onToolbar", BUTTON_SEGMENTED_TEXTURED_SEPARATED_TOOLBAR, Position.FIRST),
            new SegmentedTypeSpecifier("segmentedTexturedSeparated-middle-onToolbar", BUTTON_SEGMENTED_TEXTURED_SEPARATED_TOOLBAR, Position.MIDDLE),
            new SegmentedTypeSpecifier("segmentedTexturedSeparated-last-onToolbar", BUTTON_SEGMENTED_TEXTURED_SEPARATED_TOOLBAR, Position.LAST),
            new SegmentedTypeSpecifier("segmentedTexturedSeparated-only-onToolbar", BUTTON_SEGMENTED_TEXTURED_SEPARATED_TOOLBAR, Position.ONLY),
        };

        for (final TypeSpecifier specifier : specifiers) {
            specifiersByName.put(specifier.name, specifier);
        }

        return specifiersByName;
    }
}
