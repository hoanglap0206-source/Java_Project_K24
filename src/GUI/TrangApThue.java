package GUI;

import javax.swing.*;
import java.awt.*;

public class TrangApThue extends JPanel {

    public TrangApThue() {

        setBackground(Color.WHITE);
        setLayout(new GridBagLayout());

        JLabel label = new JLabel("TRANG ÁP THUẾ");
        label.setFont(new Font("Arial", Font.BOLD, 28));

        add(label);
    }
}
