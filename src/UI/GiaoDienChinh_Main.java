package UI;

import javax.swing.*;
import java.awt.*;

public class GiaoDienChinh_Main extends JFrame {
    private GiaoDienChinh_Content main;

    public GiaoDienChinh_Main() {
        setTitle("Giao diện chính");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        GiaoDienChinh_Header header =new GiaoDienChinh_Header();
        add(header,BorderLayout.NORTH);
        JPanel pnl=new JPanel();
        ImageIcon img = new ImageIcon(getClass().getResource("/Img/shopee.jpg"));//create an ImageIcon
        setIconImage(img.getImage()); // change icon of frame

        pnl.setLayout(new BorderLayout());
        pnl.add(new GiaoDienChinh_TopCenter(),BorderLayout.NORTH);
        pnl.add(new GiaoDienChinh_Content(),BorderLayout.CENTER);
        add(pnl,BorderLayout.CENTER);
        add(new GiaoDienChinh_Menu(header),BorderLayout.WEST);// Lập đã sửa

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GiaoDienChinh_Main().setVisible(true));
    }
}