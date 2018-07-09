package showcase;

import javax.swing.*;
import java.awt.*;

public class UnifiedToolBar {
    private ToolBarButton fileButton
            = new ToolBarButton("File", UIManager.getIcon("FileView.fileIcon"));
    private ToolBarButton folderButton
            = new ToolBarButton("Folder", UIManager.getIcon("FileView.directoryIcon"));

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        UIManager.setLookAndFeel("org.violetlib.aqua.AquaLookAndFeel");

        SwingUtilities.invokeAndWait(() -> new UnifiedToolBar());
    }

    public UnifiedToolBar() {
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);
        tb.add(fileButton);
        tb.add(folderButton);

        JFrame fr = new JFrame("Sample Tool Bar");
        fr.getRootPane().putClientProperty("Aqua.windowStyle", "unifiedToolBar");
        Container cp = fr.getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(tb, BorderLayout.NORTH);
        cp.add(new JPanel());
        fr.setSize(300, 200);
        fr.setVisible(true);
    }

    private class ToolBarButton extends JButton {
        public ToolBarButton(String text, Icon icon) {
            setIcon(icon);
            setText(text);
            setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            //putClientProperty("JComponent.sizeVariant", "small");
        }
    }
}