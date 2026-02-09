package UI;

import javax.swing.*;
import java.awt.*;

public class PhieuXuat_GUI extends JPanel {
    public PhieuXuat_GUI(){
        setLayout(new BorderLayout());
        add(new GiaoDienChinh_TopContent(), BorderLayout.NORTH);

        //Code ở pnlCode_PhieuXuat
        JPanel pnlCode_PhieuXuat = new JPanel();
        pnlCode_PhieuXuat.add(new JLabel("GIAO DIỆN PHIẾU XUẤT", SwingConstants.CENTER));//Xóa trước khi code
        pnlCode_PhieuXuat.setBackground(new Color(255,255,153));//Xóa trước khi code

        add(pnlCode_PhieuXuat, BorderLayout.CENTER);
    }
}
