package GUI;

import javax.swing.*;
import java.awt.*;

public class TrangKhachHang extends JPanel {

    public TrangKhachHang() {

        setBackground(Color.WHITE);
        setLayout(new GridBagLayout());

        JLabel label = new JLabel("TRANG KHÁCH HÀNG");
        label.setFont(new Font("Arial", Font.BOLD, 28));

        add(label);
    }
}
