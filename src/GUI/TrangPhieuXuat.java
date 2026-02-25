package GUI;

import javax.swing.*;
import java.awt.*;

public class TrangPhieuXuat extends JPanel {

    public TrangPhieuXuat() {

        setBackground(Color.WHITE);
        setLayout(new GridBagLayout());

        JLabel label = new JLabel("TRANG PHIẾU XUẤT");
        label.setFont(new Font("Arial", Font.BOLD, 28));

        add(label);
    }
}
