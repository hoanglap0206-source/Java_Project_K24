package UI;

import javax.swing.*;
import java.awt.*;

public class PhieuXuat_GUI extends JPanel {
    public PhieuXuat_GUI(){
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBackground(new Color(255,255,153));
        add(new GiaoDienChinh_TopContent(), BorderLayout.NORTH);
        add(new JLabel("GIAO DIỆN PHIẾU XUẤT", SwingConstants.CENTER));
    }
}
