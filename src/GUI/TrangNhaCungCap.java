package GUI;

import javax.swing.*;
import java.awt.*;

public class TrangNhaCungCap extends JPanel {

    public TrangNhaCungCap() {

        setBackground(Color.WHITE);
        setLayout(new GridBagLayout());

        JLabel label = new JLabel("TRANG NHÀ CUNG CẤP");
        label.setFont(new Font("Arial", Font.BOLD, 28));

        add(label);
    }
}
