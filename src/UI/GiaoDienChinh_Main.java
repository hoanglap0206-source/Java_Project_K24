package UI;

import javax.swing.*;
import java.awt.*;

public class GiaoDienChinh_Main extends JFrame {
    private GiaoDienChinh_Center main;

    public GiaoDienChinh_Main() {
        setTitle("Giao diện chính");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(new GiaoDienChinh_Content(),BorderLayout.NORTH);
        JPanel pnl=new JPanel();
        ImageIcon img = new ImageIcon(getClass().getResource("/Img/shopee.jpg"));//create an ImageIcon
        setIconImage(img.getImage()); // change icon of frame

        pnl.setLayout(new BorderLayout());
        pnl.add(new GiaoDienChinh_Head(),BorderLayout.NORTH);
        pnl.add(new GiaoDienChinh_Center(),BorderLayout.CENTER);
        add(pnl,BorderLayout.CENTER);
        add(new GiaoDienChinh_Menu(),BorderLayout.WEST);



    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GiaoDienChinh_Main().setVisible(true));
    }
}