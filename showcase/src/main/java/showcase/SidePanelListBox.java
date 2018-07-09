package showcase;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

/**
 * @author VISTALL
 * @since 7/9/18
 */
public class SidePanelListBox {

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        UIManager.setLookAndFeel("org.violetlib.aqua.AquaLookAndFeel");


        SwingUtilities.invokeAndWait(() -> invoke());
    }

    private static void invoke() {
        JFrame fr = new JFrame("Side ListBox");
        fr.setResizable(false);
        JPanel panel = new JPanel(new CardLayout());
        fr.setContentPane(panel);

        JRootPane rootPane = fr.getRootPane();
        rootPane.putClientProperty("Aqua.windowTopMargin", 0);
        rootPane.putClientProperty("Aqua.windowStyle", "transparentTitleBar");

        JList jlist = new JList(new DefaultListModel());
        jlist.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                System.out.println(jlist.getSelectedValuesList());
            }
        });

        jlist.setCellRenderer(new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JPanel p = new JPanel(new BorderLayout());
                p.putClientProperty("test", "hacky");
                p.setPreferredSize(new Dimension(list.getWidth(), 50));
                p.add(new JLabel((String) value));
                return p;
            }
        });

        ((DefaultListModel) jlist.getModel()).addElement("c34xgc3");
        ((DefaultListModel) jlist.getModel()).addElement("hc3hx");
        ((DefaultListModel) jlist.getModel()).addElement("fasf1x");
        ((DefaultListModel) jlist.getModel()).addElement("dsadas");


        JScrollPane comp = new JScrollPane(jlist);
        comp.setBorder(null);

        JPanel w = new JPanel(new BorderLayout());

        JTextField field = new JTextField();
        field.setOpaque(false);
        field.setVisible(false);
        w.add(field, BorderLayout.NORTH);

        w.setBorder(new EmptyBorder(22, 0, 0, 0));
        w.setPreferredSize(new Dimension(300, 400));
        w.putClientProperty("Aqua.backgroundStyle", "vibrantPopover");
        w.add(comp);

        JPanel main = new JPanel(new BorderLayout());
        main.add(w, BorderLayout.WEST);
        JPanel er = new JPanel();
        er.setOpaque(false);
        er.add(new JLabel("dasd"));
        main.add(er, BorderLayout.CENTER);

        panel.add(main, "test");

        fr.setLocationRelativeTo(null);
        fr.setLocationByPlatform(true);
        fr.setSize(700, 400);
        fr.setVisible(true);
    }
}
