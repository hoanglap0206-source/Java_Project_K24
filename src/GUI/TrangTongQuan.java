package GUI;

import javax.swing.*;
import java.awt.*;

public class TrangTongQuan extends JPanel {

    public TrangTongQuan() {

        setBackground(Color.WHITE);
        setLayout(new GridBagLayout());

        JLabel label = new JLabel("TỔNG QUAN");
        label.setFont(new Font("Arial", Font.BOLD, 28));

        add(label);
    }
}
