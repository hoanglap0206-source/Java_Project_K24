package GUI;

import javax.swing.*;
import java.awt.*;

public class TrangLichSuNhapXuat extends JPanel {

    public TrangLichSuNhapXuat() {

        setBackground(Color.WHITE);
        setLayout(new GridBagLayout());

        JLabel label = new JLabel("TRANG LỊCH SỬ NHẬP XUẤT");
        label.setFont(new Font("Arial", Font.BOLD, 28));

        add(label);
    }
}
