package UI;

import javax.swing.*;
import java.awt.*;

public class BaoCao_GUI extends JPanel {
    public BaoCao_GUI(){
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBackground(new Color(255,153,153));
        add(new JLabel("GIAO DIỆN BÁO CÁO", SwingConstants.CENTER));
    }
}
