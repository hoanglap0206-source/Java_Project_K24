package GUI;

import javax.swing.*;
import java.awt.*;

public class Style {
    public static void styleButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Dialog", Font.PLAIN, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(198,226,255), 2), // viền xanh RGB
                BorderFactory.createEmptyBorder(2, 15, 3, 15)              // padding
        ));
    }

    public static void styleLoc(JComboBox<?> comboBox) {
        comboBox.setBackground(new Color(214, 238, 253));
        comboBox.setPreferredSize(new Dimension(90, 30));
        comboBox.setFont(new Font("Segoe UI", Font.BOLD, 13));
        comboBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
