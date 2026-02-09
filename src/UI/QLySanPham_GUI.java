package UI;

import javax.swing.*;
import java.awt.*;

public class QLySanPham_GUI extends JPanel {
    public QLySanPham_GUI(){
        setLayout(new BorderLayout());
        add(new GiaoDienChinh_TopContent(), BorderLayout.NORTH);

        //Code ở pnlCode_QlySP
        JPanel pnlCode_QlySP = new JPanel();
        pnlCode_QlySP.setBackground(new Color(224,224,224));//Xóa trước khi code
        pnlCode_QlySP.add(new JLabel("GIAO DIỆN QUẢN LÝ SẢN PHẨM", SwingConstants.CENTER));//Xóa trước khi code

        add(pnlCode_QlySP, BorderLayout.CENTER);
    }
}
