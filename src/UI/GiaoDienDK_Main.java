package UI;

import javax.swing.*;
import java.awt.*;

public class GiaoDienDK_Main extends JFrame {

    public GiaoDienDK_Main(){
        setTitle("Đăng ký hệ thống");
        setSize(800,600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(1,2));
        add(new GiaoDienDK_Left());
        add(new GiaoDienDK_Right());

    }
    public static void main(String[] args) {
        // Chạy trên luồng giao diện (EDT) để an toàn
        SwingUtilities.invokeLater(() -> {
            new GiaoDienDK_Main().setVisible(true);
        });
    }
}