package showcase;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * @author VISTALL
 * @since 7/16/18
 */
public class JTableShow {

    //Массив содержащий заголоки таблицы
    Object[] headers = {"Name", "Surname", "Telephone"};

    //Массив содержащий информацию для таблицы
    Object[][] data = {
            {"John", "Smith", "1112221"},
            {"Ivan", "Black", "2221111"},
            {"George", "White", "3334444"},
            {"Bolvan", "Black", "2235111"},
            {"Serg", "Black", "2221511"},
            {"Pussy", "Black", "2221111"},
            {"Tonya", "Red", "2121111"},
            {"Elise", "Green", "2321111"},
    };

    //Объект таблицы
    JTable jTabPeople;

    JTableShow() {
        //Создаем новый контейнер JFrame
        JFrame jfrm = new JFrame("JTableExample");
        //Устанавливаем диспетчер компоновки
        jfrm.getContentPane().setLayout(new BorderLayout());
        //Устанавливаем размер окна
        jfrm.setSize(300, 170);
        //Устанавливаем завершение программы при закрытии окна
        jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Создаем новую таблицу на основе двумерного массива данных и заголовков
        jTabPeople = new JTable(data, headers);

        jTabPeople.getColumn("Name").setCellRenderer( new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JPanel panel = new JPanel();
                panel.setSize(-1, 50);
                panel.add(new JLabel((String) value));
                panel.add(new JCheckBox("Test"));
                return panel;
            }
        });

        jfrm.getContentPane().add(jTabPeople, BorderLayout.CENTER);
        jfrm.setVisible(true);
    }

    //Функция main, запускающаяся при старте приложения
    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        UIManager.setLookAndFeel("org.violetlib.aqua.AquaLookAndFeel");


        //Создаем фрейм в потоке обработки событий
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new JTableShow();
            }
        });
    }

}
