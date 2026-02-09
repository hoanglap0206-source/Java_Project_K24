package UI;

import javax.swing.*;
import java.awt.*;

public class QLyTaiKhoan_GUI extends JPanel {
    public QLyTaiKhoan_GUI(){
        setLayout(new BorderLayout());
        add(new GiaoDienChinh_TopContent(), BorderLayout.NORTH);

        //Code ở pnlCode_QlyTK
        JPanel pnlCode_QlyTK = new JPanel();
        pnlCode_QlyTK.setBackground(new Color(121,220,060));//Xóa trước khi code
        pnlCode_QlyTK.add(new JLabel("GIAO DIỆN QUẢN LÝ TÀI KHOẢN", SwingConstants.CENTER));//Xóa trước khi code

        add(pnlCode_QlyTK, BorderLayout.CENTER);
    }
}
