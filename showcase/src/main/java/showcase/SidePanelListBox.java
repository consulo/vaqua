package showcase;

import javax.swing.*;
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

        JPanel panel = new JPanel(new BorderLayout());
        fr.setContentPane(panel);

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
        w.setPreferredSize(new Dimension(300, -1));
        w.putClientProperty("Aqua.backgroundStyle", "vibrantPopover");
        w.add(comp);

        panel.add(w, BorderLayout.WEST);
        panel.add(new JLabel("test"), BorderLayout.CENTER);

        fr.setLocationRelativeTo(null);
        fr.setLocationByPlatform(true);
        fr.setSize(700, 400);
        fr.setVisible(true);
    }
}
