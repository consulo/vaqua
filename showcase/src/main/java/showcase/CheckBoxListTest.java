package showcase;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Arrays;

/**
 * @author VISTALL
 * @since 7/16/18
 */
public class CheckBoxListTest {
    public static class EarlyAccessCellRender implements ListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

            JCheckBox checkbox = (JCheckBox) value;

            checkbox.setEnabled(list.isEnabled());
            checkbox.setFocusPainted(false);
            checkbox.setBorderPainted(true);


            checkbox.setEnabled(true);

            JPanel panel = new JPanel(new VerticalFlowLayout(VerticalFlowLayout.TOP, true, true)) {
                @Override
                public Dimension getPreferredSize() {
                    Dimension size = super.getPreferredSize();
                    return new Dimension(Math.min(size.width, 200), size.height);
                }
            };
            panel.setEnabled(true);

            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.add(checkbox, BorderLayout.WEST);

            if (Boolean.TRUE) {
                JLabel comp = new JLabel("Restart required");
                comp.setForeground(Color.GRAY);
                topPanel.add(comp, BorderLayout.EAST);
            }

            panel.add(topPanel);
            panel.setBorder(new LineBorder(Color.gray));

            String description = "descr";
            JTextPane textPane = new JTextPane();
            textPane.setText(description);
            textPane.setEditable(false);

            panel.add(textPane);
            return panel;

        }
    }

    public CheckBoxListTest() {
        JFrame fr = new JFrame("Check ListBox");
        fr.setResizable(false);
        JPanel panel = new JPanel(new VerticalFlowLayout());
        fr.setContentPane(panel);


        CheckboxList<String> list = new CheckboxList<String>();
        list.setCellRenderer(new EarlyAccessCellRender());
        list.setItems(Arrays.<String>asList("Run line marker"), (e) -> e);
        panel.add(list);

        panel.add(new JCheckBox("test"));
        panel.add(new JCheckBox("test"));
        panel.add(new JCheckBox("test"));
        panel.add(new JCheckBox(UIManager.getIcon("FileView.fileIcon")));
        panel.add(new JCheckBox("test"));
        panel.add(new JButton("test", UIManager.getIcon("FileView.fileIcon")));
        panel.add(new JButton("test"));
        panel.add(new JRadioButton("test"));
        panel.add(new JRadioButton("test"));
        panel.add(new JRadioButton("test"));
        panel.add(new JRadioButton("test"));
        fr.setLocationRelativeTo(null);
        fr.setLocationByPlatform(true);
        fr.setSize(700, 400);
        fr.setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        UIManager.setLookAndFeel("org.violetlib.aqua.AquaLookAndFeel");

        SwingUtilities.invokeAndWait(() -> new CheckBoxListTest());
    }
}
