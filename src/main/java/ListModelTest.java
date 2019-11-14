import javax.swing.*;

import java.awt.event.*;
import java.util.Vector;

public class ListModelTest extends JFrame
{
    // Модель списка
    private DefaultListModel<String> dlm = new DefaultListModel<String>();

    private final String[] data1 = { "Чай" ,"Кофе"  ,"Минеральная","Морс"};
    private final String[] data2 = { "Ясли","Детсад", "Школа"     , "Институт",
            "Университет"};

    public ListModelTest()
    {
        super("Пример со списком JList");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // Создание панели
        JPanel contents = new JPanel();

        // Наполнение модели данными
        for (String string : data2) {
            dlm.add(0, string);
        }
        // Создание кнопки
        JButton add = new JButton("Добавить");
        add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dlm.add(dlm.getSize(), "-- Новая запись --");
                validate();
            }
        });
        JList<String> list1 = new JList<String>(data1);
        JList<String> list2 = new JList<String>(dlm);

        // Вектор данных
        Vector<String> big = new Vector<String>();
        for (int i=0; i < 15; i++) {
            big.add("~ " + i + ". ~");
        }
        JList<String> bigList = new JList<String>(big);
        bigList.setPrototypeCellValue("12345");

        // Размещение компонентов в панели
        contents.add(add);
        contents.add(new JScrollPane(list1));
//        contents.add(new JScrollPane(list2));
//        contents.add(new JScrollPane(bigList));

        setContentPane(contents);
        // Вывод окна
        setSize(400, 200);
        setVisible(true);
    }
    public static void main(String[] args) {
        new ListModelTest();
    }
}