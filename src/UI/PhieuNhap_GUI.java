package UI;

import javax.swing.*;
import java.awt.*;

public class PhieuNhap_GUI extends JPanel {
    public PhieuNhap_GUI(){
        setLayout(new BorderLayout());
        add(new GiaoDienChinh_TopContent(), BorderLayout.NORTH);

        //Code ở pnlCode_PhieuNhap
        JPanel pnlCode_PhieuNhap = new JPanel();
        pnlCode_PhieuNhap.setBackground(new Color(153,204,255));//Xóa trước khi code
        pnlCode_PhieuNhap.add(new JLabel("GIAO DIỆN PHIẾU NHẬP", SwingConstants.CENTER));//Xóa trước khi code

        add(pnlCode_PhieuNhap, BorderLayout.CENTER);
    }
}
