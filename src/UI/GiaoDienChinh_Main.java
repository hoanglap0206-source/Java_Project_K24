package UI;

import BUS.NV_BUS;

import javax.swing.*;
import java.awt.*;

public class GiaoDienChinh_Main extends JFrame {
    private GiaoDienChinh_Content content;

    public GiaoDienChinh_Main(String maNV) {
//        try {
//            Thread.sleep(3000); // delay 3 giay
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        setTitle("Giao diện chính");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setUndecorated(true);

        NV_BUS nv = new NV_BUS();

        GiaoDienChinh_Header header =new GiaoDienChinh_Header(nv.getTenNV_BUS(maNV));

        //Dùng để sử dùng các panel từ các Button Menu
        content = new GiaoDienChinh_Content();

        add(header,BorderLayout.NORTH);
        JPanel pnl=new JPanel();
        ImageIcon img = new ImageIcon(getClass().getResource("/Img/shopee.jpg"));//create an ImageIcon
        setIconImage(img.getImage()); // change icon of frame

        pnl.setLayout(new BorderLayout());
        pnl.add(content,BorderLayout.CENTER);

        add(pnl,BorderLayout.CENTER);

        add(new GiaoDienChinh_Menu(header, content, maNV),BorderLayout.WEST);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GiaoDienChinh_Main("NV99").setVisible(true));
    }
}