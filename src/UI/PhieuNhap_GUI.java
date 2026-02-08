package UI;

import javax.swing.*;
import java.awt.*;

public class PhieuNhap_GUI extends JPanel {
    public PhieuNhap_GUI(){
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBackground(new Color(153,204,255));
        add(new GiaoDienChinh_TopContent(), BorderLayout.NORTH);
        add(new JLabel("GIAO DIỆN PHIẾU NHẬP", SwingConstants.CENTER));
    }
}
