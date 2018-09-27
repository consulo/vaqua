/*
 * Copyright (c) 2015 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

package org.violetlib.aqua;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.accessibility.AccessibleContext;
import javax.swing.*;
import javax.swing.plaf.FileChooserUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;

/**
 * Support for macOS sheets using the VAqua look and feel.
 */
public class VSheet {

    private VSheet() {}

    /**
     * Display an option pane as a document modal dialog. The dialog will be displayed as a sheet if possible. This
     * method can be used to simulate the behavior of the various {@code showXXX} methods of JOptionPane.
     * <p>
     * This method may block until the dialog is dismissed, or it may return immediately.
     * @param parent The parent component. Its window will own the sheet.
     * @param pane The option pane.
     * @param title The title for the dialog if displayed as an ordinary dialog window.
     * @param resultConsumer If not null, this consumer will be called when the dialog is dismissed with an integer
     * indicating the option chosen by the user, or <code>CLOSED_OPTION</code> if the user dismissed the dialog without
     * choosing an option.
     * @throws HeadlessException if the graphics environment is headless.
     */
    public static void showOptionPane(@Nullable Component parent,
                                      @NotNull JOptionPane pane,
                                      @Nullable String title,
                                      @Nullable Consumer<Integer> resultConsumer)
            throws HeadlessException {

        JDialog d = pane.createDialog(parent, title);

        if (parent != null) {
            try {
                tryShowOptionPane(d, pane, resultConsumer);
                return;
            } catch (HeadlessException ex) {
                throw ex;
            } catch (UnsupportedOperationException ex) {
            }
        }

        // Unable to display as sheet
        d.setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);
        d.setVisible(true);
        d.dispose();
        if (resultConsumer != null) {
            resultConsumer.accept(getOption(pane));
        }
    }

    private static void tryShowOptionPane(@NotNull JDialog d,
                                          @NotNull JOptionPane pane,
                                          @Nullable Consumer<Integer> resultConsumer)
            throws HeadlessException {

        LookAndFeel laf = UIManager.getLookAndFeel();
        if (laf != null) {
            try {
                Method m = laf.getClass().getMethod("showOptionPaneAsSheet",
                        JDialog.class, JOptionPane.class, Consumer.class);
                m.invoke(laf, d, pane, resultConsumer);
                return;
            } catch (NoSuchMethodException ex) {
                // Could be an older version of VAqua
            } catch (InvocationTargetException ex) {
                Throwable targetException = ex.getTargetException();
                if (targetException instanceof UnsupportedOperationException) {
                    throw (UnsupportedOperationException) targetException;
                }
                throw new UnsupportedOperationException("Unable to display as sheet because of an internal error",
                        targetException);
            } catch (Exception ex) {
                throw new UnsupportedOperationException("Unable to display as sheet because of an internal error", ex);
            }

            // Code for an older version of VAqua
            Runnable closeHandler = null;
            if (resultConsumer != null) {
                closeHandler = new Runnable() {
                    @Override
                    public void run() {
                        resultConsumer.accept(getOption(pane));
                    }
                };
            }
            displayAsSheet(laf, d, closeHandler);
        } else {
            throw new UnsupportedOperationException("Unable to display as sheet: VAqua look and feel is not installed");
        }
    }

    private static int getOption(@NotNull JOptionPane pane) {
        Object selectedValue = pane.getValue();

        if (selectedValue == null) {
            return JOptionPane.CLOSED_OPTION;
        }

        Object[] options = pane.getOptions();
        if (options == null) {
            if(selectedValue instanceof Integer)
                return (Integer) selectedValue;
            return JOptionPane.CLOSED_OPTION;
        }

        for(int counter = 0, maxCounter = options.length;
            counter < maxCounter; counter++) {
            if(options[counter].equals(selectedValue))
                return counter;
        }
        return JOptionPane.CLOSED_OPTION;
    }

    /**
     * Display a file chooser as a document modal open dialog. If possible, the dialog will be displayed as a document
     * modal sheet. This method simulates the behavior of the {@code showOpenDialog} method of {@code JFileChooser}.
     * <p>
     * This method may block until the dialog is dismissed, or it may return immediately.
     * @param parent The parent component. Its window will own the sheet.
     * @param fc The file chooser.
     * @param resultConsumer If not null, this object will be invoked upon dismissal of the dialog with the return state of
     * the file chooser.
     * @throws HeadlessException if the graphics environment is headless.
     */
    public static void showOpenDialog(@Nullable Component parent,
                                      @NotNull JFileChooser fc,
                                      @Nullable Consumer<Integer> resultConsumer)
            throws HeadlessException {
        fc.setDialogType(JFileChooser.OPEN_DIALOG);
        showFileChooserDialog(parent, fc, resultConsumer);
    }

    /**
     * Display a file chooser as a document modal save dialog. If possible, the dialog will be displayed as a document
     * modal sheet. This method simulates the behavior of the {@code showSaveDialog} method of {@code JFileChooser}.
     * <p>
     * This method may block until the dialog is dismissed, or it may return immediately.
     * @param parent The parent component. Its window will own the sheet.
     * @param fc The file chooser.
     * @param resultConsumer If not null, this object will be invoked upon dismissal of the dialog with the return state of
     * the file chooser.
     * @throws HeadlessException if the graphics environment is headless.
     */
    public static void showSaveDialog(@Nullable Component parent,
                                      @NotNull JFileChooser fc,
                                      @Nullable Consumer<Integer> resultConsumer)
            throws HeadlessException {
        fc.setDialogType(JFileChooser.SAVE_DIALOG);
        showFileChooserDialog(parent, fc, resultConsumer);
    }

    /**
     * Display a file chooser as a document modal dialog. If possible, the dialog will be displayed as a document modal
     * sheet. This method simulates the behavior of the {@code showDialog} method of {@code JFileChooser}.
     * <p>
     * This method may block until the dialog is dismissed, or it may return immediately.
     * @param parent The parent component. Its window will own the sheet.
     * @param fc The file chooser.
     * @param resultConsumer If not null, this object will be invoked upon dismissal of the dialog with the return state of
     * the file chooser.
     * @throws HeadlessException if the graphics environment is headless.
     */
    public static void showFileChooserDialog(@Nullable Component parent,
                                             @NotNull JFileChooser fc,
                                             @Nullable Consumer<Integer> resultConsumer)
            throws HeadlessException {

        if (parent != null) {
            try {
                tryShowFileChooserAsSheet(parent, fc, resultConsumer);
                return;
            } catch (HeadlessException ex) {
                throw ex;
            } catch (Exception ex) {
                // the exception is ignored
            }
        }

        int result = fc.showDialog(parent, null);
        if (resultConsumer != null) {
            resultConsumer.accept(result);
        }
    }

    /**
     * Display a file chooser as a document modal sheet.
     * @param parent The parent component. Its window will own the sheet.
     * @param fc The file chooser.
     * @param resultConsumer If not null, this object will be invoked upon dismissal of the dialog with the return state of
     * the file chooser.
     * @throws HeadlessException if the graphics environment is headless.
     * @throws UnsupportedOperationException if it is not possible to display a sheet.
     */
    private static void tryShowFileChooserAsSheet(@Nullable Component parent,
                                                  @NotNull JFileChooser fc,
                                                  @Nullable Consumer<Integer> resultConsumer)
            throws HeadlessException, UnsupportedOperationException {

        Window owner;
        if (parent == null || (owner = getWindow(parent)) == null) {
            throw new UnsupportedOperationException("Unable to display as sheet: no owner window");
        }

        if (!owner.isVisible()) {
            throw new UnsupportedOperationException("Unable to display as sheet: owner window is not visible");
        }

        LookAndFeel laf = UIManager.getLookAndFeel();
        if (laf != null) {
            try {
                Method m = laf.getClass().getMethod("showFileChooserAsSheet",
                        Window.class, JFileChooser.class, Consumer.class);
                m.invoke(laf, owner, fc, resultConsumer);
                return;
            } catch (NoSuchMethodException ex) {
                // Could be an older version of VAqua
            } catch (InvocationTargetException ex) {
                Throwable targetException = ex.getTargetException();
                if (targetException instanceof UnsupportedOperationException) {
                    throw (UnsupportedOperationException) targetException;
                }
                throw new UnsupportedOperationException("Unable to display as sheet because of an internal error",
                        targetException);
            } catch (Exception ex) {
                throw new UnsupportedOperationException("Unable to display as sheet because of an internal error", ex);
            }

            // Code for an older version of VAqua
            // We try to duplicate what JFileChooser does when showing a dialog
            // Cannot test for a dialog in progress the way that JFileChooser does...
            FileChooserUI ui = fc.getUI();
            String title = ui.getDialogTitle(fc);
            fc.putClientProperty(AccessibleContext.ACCESSIBLE_DESCRIPTION_PROPERTY, title);

            JDialog dialog;
            if (owner instanceof Frame) {
                dialog = new JDialog((Frame)owner, title, Dialog.ModalityType.MODELESS);
            } else {
                dialog = new JDialog((Dialog)owner, title, Dialog.ModalityType.MODELESS);
            }
            dialog.setComponentOrientation(fc.getComponentOrientation());

            Container contentPane = dialog.getContentPane();
            contentPane.setLayout(new BorderLayout());
            contentPane.add(fc, BorderLayout.CENTER);

            dialog.pack();
            dialog.setLocationRelativeTo(owner);

            FileChooserActionListener listener = new FileChooserActionListener(dialog);
            fc.addActionListener(listener);
            fc.rescanCurrentDirectory();

            Runnable closeHandler = new Runnable() {
                @Override
                public void run() {
                    int returnValue = listener.returnValue;
                    fc.removeActionListener(listener);

                    //fc.firePropertyChange("JFileChooserDialogIsClosingProperty", dialog, null);

                    // Remove all components from dialog. The MetalFileChooserUI.installUI() method (and other LAFs)
                    // registers AWT listener for dialogs and produces memory leaks. It happens when
                    // installUI invoked after the showDialog method.
                    dialog.getContentPane().removeAll();
                    dialog.dispose();
                    if (resultConsumer != null) {
                        resultConsumer.accept(returnValue);
                    }
                }
            };

            try {
                displayAsSheet(laf, dialog, closeHandler);
            } catch (UnsupportedOperationException ex) {
                dialog.getContentPane().removeAll();
                dialog.dispose();
                throw ex;
            }
        } else {
            throw new UnsupportedOperationException("Unable to display as sheet: VAqua look and feel is not installed");
        }
    }

    private static class FileChooserActionListener implements ActionListener {
        private JDialog d;

        public FileChooserActionListener(JDialog d) {
            this.d = d;
        }

        int returnValue = JFileChooser.ERROR_OPTION;
        public void actionPerformed(ActionEvent e) {
            String s = e.getActionCommand();
            if (s.equals(JFileChooser.APPROVE_SELECTION)) {
                returnValue = JFileChooser.APPROVE_OPTION;
                d.setVisible(false);
            } else if (s.equals(JFileChooser.CANCEL_SELECTION)) {
                returnValue = JFileChooser.CANCEL_OPTION;
                d.setVisible(false);
            }
        }
    }

    /**
     * Display a document modal dialog. The dialog will be displayed as a sheet if possible.
     * <p>
     * This method may block until the dialog is dismissed, or it may return immediately.
     * @param d The dialog.
     * @param closeHandler If not null, this object will be invoked when the dialog is dismissed.
     * @throws IllegalArgumentException if the dialog is visible, the dialog has no owner, or the dialog owner is not
     * visible.
     * @throws HeadlessException if the graphics environment is headless.
     */
    public static void showDialog(@NotNull JDialog d, @Nullable Runnable closeHandler)
            throws IllegalArgumentException, HeadlessException {
        if (d.isVisible()) {
            throw new IllegalArgumentException("Unable to display dialog: the dialog is already visible");
        }
        Window owner = d.getOwner();
        if (owner == null) {
            throw new IllegalArgumentException("Unable to display dialog: the dialog has no owner");
        }
        if (!owner.isVisible()) {
            throw new IllegalArgumentException("Unable to display dialog: the dialog owner is not visible");
        }

        Dialog.ModalityType oldType = d.getModalityType();
        try {
            try {
                displayAsSheet(d, closeHandler);
            } catch (HeadlessException ex) {
                throw ex;
            } catch (UnsupportedOperationException ex) {
                d.setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);
                d.setVisible(true);
                if (closeHandler != null) {
                    closeHandler.run();
                }
            }
        } finally {
            d.setModalityType(oldType);
        }
    }

    /**
     * Display a window as a sheet, if possible.
     * <p>
     * The behavior of a sheet is similar to a document modal dialog in that it prevents user interaction with the
     * existing windows in the hierarchy of the owner. Unlike {@code setVisible(true)} on a model dialog, however, this
     * method does not block waiting for the sheet to be dismissed. A sheet is dismissed when the window is hidden or
     * disposed.
     *
     * @param w the window. The window must have a visible owner. The window must not be visible. If the window is a
     * dialog, its modality will be set to modeless.
     * @param closeHandler If not null, this object will be invoked when the sheet is dismissed.
     * @throws UnsupportedOperationException if the window could not be displayed as a sheet.
     */
    public static void displayAsSheet(@NotNull Window w, @Nullable Runnable closeHandler)
            throws UnsupportedOperationException {
        LookAndFeel laf = UIManager.getLookAndFeel();
        if (laf != null) {
            displayAsSheet(laf, w, closeHandler);
        } else {
            throw new UnsupportedOperationException("Unable to display as sheet: VAqua look and feel is not installed");
        }
    }

    private static void displayAsSheet(@NotNull LookAndFeel laf, @NotNull Window w, @Nullable Runnable closeHandler) {
        try {
            Method m = laf.getClass().getMethod("displayAsSheet", Window.class, Runnable.class);
            m.invoke(laf, w, closeHandler);
        } catch (NoSuchMethodException ex) {
            String lafName = laf.getClass().getName();
            if (!lafName.contains("VAqua")) {
                throw new UnsupportedOperationException("Unable to display as sheet: VAqua look and feel is not installed");
            } else {
                throw new UnsupportedOperationException("Unable to display as sheet because of an internal error", ex);
            }
        } catch (InvocationTargetException ex) {
            Throwable targetException = ex.getTargetException();
            if (targetException instanceof UnsupportedOperationException) {
                throw (UnsupportedOperationException) targetException;
            }
            throw new UnsupportedOperationException("Unable to display as sheet because of an internal error",
                    targetException);
        } catch (Exception ex) {
            throw new UnsupportedOperationException("Unable to display as sheet because of an internal error", ex);
        }
    }

    private static Window getWindow(Component c) {
        if (c instanceof Window) {
            return (Window) c;
        }
        return SwingUtilities.getWindowAncestor(c);
    }

    public static String getReleaseName() {
        return getStringResource("VSHEET_RELEASE.txt");
    }

    public static String getBuildID() {
        return getStringResource("VSHEET_BUILD.txt");
    }

    public static void showVersion() {
        System.err.println("VSheet: release " + getReleaseName() + ", build " + getBuildID());
    }

    private static String getStringResource(String name) {
        InputStream s = VSheet.class.getResourceAsStream(name);
        if (s != null) {
            try {
                BufferedReader r = new BufferedReader(new InputStreamReader(s));
                StringBuilder sb = new StringBuilder();
                for (; ; ) {
                    int ch = r.read();
                    if (ch < 0) {
                        break;
                    }
                    sb.append((char) ch);
                }
                return sb.toString();
            } catch (IOException ex) {
            }
        }

        return "Unknown";
    }
}
