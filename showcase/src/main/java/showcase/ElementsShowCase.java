package showcase;

import javax.swing.*;
import java.awt.*;

/**
 * @author VISTALL
 * @since 7/10/18
 */
public class ElementsShowCase {
    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        UIManager.setLookAndFeel("org.violetlib.aqua.AquaLookAndFeel");

        SwingUtilities.invokeAndWait(() -> new ElementsShowCase());
    }

    public ElementsShowCase() {
        JFrame frame = new JFrame();

        frame.setSize(500, 500);

        JTabbedPane tabbedPane = new JTabbedPane();
        frame.setContentPane(tabbedPane);

        tabbedPane.addTab("ComboBoxes", comboBoxes());
        tabbedPane.addTab("ProgressBars", progressBars());
        tabbedPane.addTab("Labels", labels());

        frame.setVisible(true);
    }

    private JPanel labels() {
        JPanel panel = new JPanel();

        panel.add(new JLabel("test"));

        return panel;
    }
    private JComponent progressBars() {
        JPanel panel = new JPanel();

        JProgressBar progressBar = new JProgressBar();
        progressBar.setForeground(Color.red);
        progressBar.setValue(50);

        panel.add(progressBar);

        JProgressBar progressBar2 = new JProgressBar();
        progressBar2.putClientProperty("JProgressBar.style", "circular");
        progressBar2.setIndeterminate(true);
        progressBar2.setValue(50);

        panel.add(progressBar2);
        return panel;
    }

    private JComponent comboBoxes() {
        JPanel panel = new JPanel(new FlowLayout());

        JComboBox c1 = new JComboBox(new String[]{"test1", "test2"});

        panel.add(c1);

        JComboBox c2 = new JComboBox(new String[]{"test1", "test2"});
        c2.putClientProperty("JComponent.sizeVariant", "small");
        panel.add(c2);

        JComboBox c3 = new JComboBox(new String[]{"test1", "test2"});
        c3.putClientProperty("JComponent.sizeVariant", "mini");

        panel.add(c3);

        JComboBox c4 = new JComboBox(new String[]{"test1", "test2"});
        c4.putClientProperty("JComboBox.style", "borderless");

        panel.add(c4);

        JComboBox c5 = new JComboBox(new String[]{"test1", "test2"});
        c5.putClientProperty("JComboBox.style", "textured");

        panel.add(c5);

        JComboBox c6 = new JComboBox(new String[]{"test1", "test2"});
        c6.putClientProperty("JComboBox.style", "square");

        panel.add(c6);
        return panel;
    }
}
