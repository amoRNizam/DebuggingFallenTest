import javax.swing.*;
import java.math.BigDecimal;

public class JListExample {
    JFrame frame = new JFrame("Storage");
    JList<Product> list = new JList<>();
    public static DefaultListModel<Product> model = new DefaultListModel<>();
    JLabel label = new JLabel();
    JPanel panel = new JPanel();
    JSplitPane splitPane = new JSplitPane();
    public JListExample() {
        list.setModel(model);
//        model.addElement(new Product("Item1", new BigDecimal("49.00")));
//        model.addElement(new Product("Item2", new BigDecimal("150")));
//        model.addElement(new Product("Item3", new BigDecimal("54.5")));
//        model.addElement(new Product("Item4", new BigDecimal("120.00")));
        list.getSelectionModel().addListSelectionListener(e -> {
            Product p = list.getSelectedValue();
//            label.setText(p.getName() + " price is = " + p.getPrice().toPlainString());
        });
//
        splitPane.setLeftComponent(new JScrollPane(list));
        panel.add(label);
//        splitPane.setRightComponent(panel);
//        splitPane.setResizeWeight(0.5); //used to set the way it splits the left and right component
//        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        frame.add(splitPane);
//        frame.pack();
//        frame.setLocationRelativeTo(null);
//        frame.setVisible(true);
//        frame.setSize(500,300);
    }
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(JListExample::new);
//    }
    protected static class Product {
        String name;
        String path;

        public Product(String name, String path) {
            this.name = name;
            this.path = path;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getPath() {
            return path;
        }
        public void setPath(String path) {
            this.path = path;
        }
        @Override
        public String toString() {
            return name;
        }
    }
}