package GUI;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class TrangApThue extends JPanel {

    public TrangApThue() {
        setLayout(new BorderLayout(0, 20));
        setBorder(new EmptyBorder(20, 25, 20, 25));
        setBackground(Color.WHITE);

        add(cauHinhThue(), BorderLayout.NORTH);
        add(danhSachThue(), BorderLayout.CENTER);
    }

    public JPanel cauHinhThue() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));

        JLabel lblCauHinhThue = new JLabel("CẤU HÌNH THUẾ");
        lblCauHinhThue.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(lblCauHinhThue, BorderLayout.NORTH);

        JPanel thongTin = new JPanel(new GridBagLayout());




        panel.add(thongTin, BorderLayout.CENTER);
        JButton btnLuu = new JButton("Lưu");
        Style.styleButton(btnLuu);
        JPanel btnLuuWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnLuuWrapper.setBackground(Color.WHITE);
        btnLuuWrapper.add(btnLuu);
        panel.add(btnLuuWrapper, BorderLayout.SOUTH);
        return panel;
    }

    public JPanel danhSachThue() {
        JPanel panel = new JPanel();
        return panel;
    }
}
