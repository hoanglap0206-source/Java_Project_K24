package UI;

import javax.swing.*;
import java.awt.*;

public class XuatKho_GUI extends JPanel {
    XuatKho_GUI(){
        setLayout(new BorderLayout());
        add(new GiaoDienChinh_TopContent(), BorderLayout.NORTH);

        //Code ở pnlCode_XuatKho
        JPanel pnlCode_XuatKho = new JPanel();
        pnlCode_XuatKho.setBackground(Color.WHITE);//Xóa trước khi code
        pnlCode_XuatKho.add(new JLabel("GIAO DIỆN XUẤT KHO", SwingConstants.CENTER));//Xóa trước khi code

        add(pnlCode_XuatKho, BorderLayout.CENTER);
    }
}
