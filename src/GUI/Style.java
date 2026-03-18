package GUI;

import javax.swing.*;
import java.awt.*;

public class Style {
    public static void styleButton(JButton button) {
        Color normal = Color.WHITE;
        Color hover = new Color(214, 238, 253);
        Color pressed = new Color(180, 220, 245);
        Color borderColor = new Color(198,226,255);

        button.setFocusPainted(false);
        button.setBackground(normal);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 2, true),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));

        // Hover + Click effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hover);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(normal);
            }
        });
    }

    public static void styleLoc(JComboBox<?> comboBox) {
        comboBox.setPreferredSize(new Dimension(110, 32));
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(Color.BLACK);

        comboBox.setBorder(BorderFactory.createLineBorder(new Color(198,226,255), 2, true));

        comboBox.setFocusable(false);
    }
}
