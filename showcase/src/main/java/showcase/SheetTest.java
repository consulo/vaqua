package showcase;

import org.violetlib.aqua.VSheet;

import javax.swing.*;

/**
 * @author VISTALL
 * @since 7/10/18
 */
public class SheetTest {
    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        UIManager.setLookAndFeel("org.violetlib.aqua.AquaLookAndFeel");

        SwingUtilities.invokeAndWait(() -> new SheetTest());
    }

    public SheetTest() {
        JFrame frame = new JFrame();

        frame.setSize(500, 500);
        frame.setContentPane(new JPanel());

        JButton button  = new JButton("Simple");
        button.addActionListener(e -> {
            JOptionPane pane = new JOptionPane("test", JOptionPane.INFORMATION_MESSAGE);

            VSheet.showOptionPane(frame, pane, "test", null);
        });

        frame.getContentPane().add(button);

        JButton chooser  = new JButton("Chooser");
        chooser.addActionListener(e -> {
            JFileChooser c = new JFileChooser("/");

            VSheet.showFileChooserDialog(frame, c, integer -> {});
        });

        frame.getContentPane().add(chooser);
        frame.setVisible(true);
    }
}
