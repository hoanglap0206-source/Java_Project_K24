package UI;

import javax.swing.*;
import java.awt.*;

public class QLyTaiKhoan_GUI extends JPanel {
    public QLyTaiKhoan_GUI(){
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBackground(new Color(121,220,060));
        add(new GiaoDienChinh_TopContent(), BorderLayout.NORTH);
        add(new JLabel("GIAO DIỆN QUẢN LÝ TÀI KHOẢN", SwingConstants.CENTER));
    }
}
