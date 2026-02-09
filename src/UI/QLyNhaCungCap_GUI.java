package UI;

import javax.swing.*;
import java.awt.*;

public class QLyNhaCungCap_GUI extends JPanel {
    public QLyNhaCungCap_GUI(){
        setLayout(new BorderLayout());
        add(new GiaoDienChinh_TopContent(), BorderLayout.NORTH);

        //Code ở pnlCode_QlyNCC
        JPanel pnlCode_QlyNCC = new JPanel();
        pnlCode_QlyNCC.setBackground(new Color(255,204,153));//Xóa trước khi code
        pnlCode_QlyNCC.add(new JLabel("GIAO DIỆN QUẢN LÝ NHÀ CUNG CẤP", SwingConstants.CENTER));//Xóa trước khi code

        add(pnlCode_QlyNCC, BorderLayout.CENTER);
    }
}
