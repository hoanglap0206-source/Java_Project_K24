package UI;

import javax.swing.*;
import java.awt.*;

public class NhapKho_GUI extends JPanel {
    public NhapKho_GUI() {
        setLayout(new BorderLayout());

        //Code ở pnlCode_NhapKho
        JPanel pnlCode_NhapKho = new JPanel();
        pnlCode_NhapKho.setBackground(new Color(255,153,204));//Xóa trước khi code
        pnlCode_NhapKho.add(new JLabel("GIAO DIỆN NHẬP KHO", SwingConstants.CENTER));//Xóa trước khi code

        add(pnlCode_NhapKho, BorderLayout.CENTER);
    }
}
