import java.awt.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Main extends JPanel{

    Image img = new ImageIcon("D:\\TestingResult_18.11.2019_01.45.05\\50804\\Screens\\difference_scr2.png").getImage();

    public static void main(String[] args){
        JFrame fr = new JFrame();
        //устанавливаем абсолюбтное позиционирование на фрейме
        Main m = new Main();
        //устанавливаем размеры и координаты компонента для размещения в родителя с абсолютным позиционированием
        m.setBounds(0,0,1024,868);
        fr.add(m);
        fr.setLayout(null);
        fr.setSize(1024,768);
        fr.setVisible(true);
        fr.add(new Main());

        fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fr.setVisible(true);

    }
//    public void paintComponent(){
//        Graphics2D g= (Graphics2D)getGraphics();
//        g.drawImage(img, 0, 0, null);
//    }

    public void paint (Graphics g) {
        JLabel j1 = new JLabel();
        JPanel jp = new JPanel();
        try {
            img = ImageIO.read(new File("D:\\TestingResult_18.11.2019_01.45.05\\50804\\Screens\\difference_scr2.png"));
        }
        catch (IOException ioe) {  System.exit(0);}

        j1 = new JLabel(new ImageIcon(img));
        jp.setLayout(new BorderLayout());
        jp.add(j1, BorderLayout.WEST);
        g.drawImage(img, 10, 100, jp);
    }
}