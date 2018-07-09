package showcase;

import javax.swing.*;
import java.awt.*;

/**
 * @author VISTALL
 * @since 7/9/18
 */
public class UnifiedToolbarWithCustomComponent {
    private ToolBarButton fileButton
            = new ToolBarButton("File", UIManager.getIcon("FileView.fileIcon"));
    private ToolBarButton folderButton
            = new ToolBarButton("Folder", UIManager.getIcon("FileView.directoryIcon"));

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        UIManager.setLookAndFeel("org.violetlib.aqua.AquaLookAndFeel");

        SwingUtilities.invokeAndWait(() -> new UnifiedToolbarWithCustomComponent());
    }

    public UnifiedToolbarWithCustomComponent() {
        JPanel tb = new JPanel();
        tb.putClientProperty("Aqua.toolbarPanel", "true");
        tb.add(fileButton);
        tb.add(folderButton);
        tb.setPreferredSize(new Dimension(-1, 50));

        JFrame fr = new JFrame("TT Tool Bar");
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
