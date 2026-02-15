package GUI;

import javax.swing.*;
import java.awt.*;

public class TrangXuatKho extends JPanel {

    public TrangXuatKho() {

        setBackground(Color.WHITE);
        setLayout(new GridBagLayout());

        JLabel label = new JLabel("TRANG XUẤT KHO");
        label.setFont(new Font("Arial", Font.BOLD, 28));

        add(label);
    }
}
