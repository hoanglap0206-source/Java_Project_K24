package GUI;

import javax.swing.*;
import java.awt.*;

public class TrangSanPham extends JPanel {

    public TrangSanPham() {

        setBackground(Color.WHITE);
        setLayout(new GridBagLayout());

        JLabel label = new JLabel("TRANG SẢN PHẨM");
        label.setFont(new Font("Arial", Font.BOLD, 28));

        add(label);
    }
}
