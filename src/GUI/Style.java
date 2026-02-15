package GUI;

import javax.swing.*;
import java.awt.*;

public class Style {

    public static void styleButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(new Color(99, 170, 255)); // 63AAFF
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
    }
}
