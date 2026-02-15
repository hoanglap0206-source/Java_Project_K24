package GUI;

import javax.swing.*;
import java.awt.*;

public class ThanhTieuDe extends JPanel {

    private final Color HEADER_COLOR = new Color(170, 211, 255);

    public ThanhTieuDe() {

        setLayout(new BorderLayout());
        setBackground(HEADER_COLOR);
        setPreferredSize(new Dimension(1000, 50));

        setBorder(BorderFactory.createMatteBorder(
                0, 0, 1, 0,
                new Color(210, 220, 230)
        ));

        // ===== PANEL BÊN TRÁI =====
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        leftPanel.setOpaque(false);

        CirclePanel logo = new CirclePanel("/Img/ConRua.jpg", 35);

        JLabel title = new JLabel("Tên công ty");
        title.setForeground(Color.BLACK);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));

        leftPanel.add(logo);
        leftPanel.add(title);

        // ===== PANEL BÊN PHẢI =====
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        rightPanel.setOpaque(false);

        CirclePanel userIcon = new CirclePanel("/Img/user.png", 35);

        JLabel user = new JLabel("Chào, Admin");
        user.setForeground(Color.BLACK);
        user.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        rightPanel.add(userIcon);
        rightPanel.add(user);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
    }
}
