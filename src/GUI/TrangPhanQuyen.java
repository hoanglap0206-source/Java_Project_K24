package GUI;

import javax.swing.*;
import java.awt.*;

public class TrangPhanQuyen extends JPanel {

    public TrangPhanQuyen() {

        setBackground(Color.WHITE);
        setLayout(new GridBagLayout());

        JLabel label = new JLabel("TRANG PHÂN QUYỀN");
        label.setFont(new Font("Arial", Font.BOLD, 28));

        add(label);
    }
}
