package GUI;

import javax.swing.*;
import java.awt.*;

public class TrangBaoCao extends JPanel {

    public TrangBaoCao() {

        setBackground(Color.WHITE);
        setLayout(new GridBagLayout());

        JLabel label = new JLabel("TRANG BÁO CÁO");
        label.setFont(new Font("Arial", Font.BOLD, 28));

        add(label);
    }
}
