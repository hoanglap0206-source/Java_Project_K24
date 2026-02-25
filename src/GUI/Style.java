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
}
