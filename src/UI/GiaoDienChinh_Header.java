package UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class GiaoDienChinh_Header extends JPanel {
    private JLabel title;
    private String titleCN = "";
    public String getTitleCN() {
        return titleCN;
    }
    public void setTitleCN(String titleCN) {
        this.titleCN = titleCN;
        updateTitle();
    }

    public GiaoDienChinh_Header() {
        setLayout(new BorderLayout());
        setBackground(new Color(230, 230, 230)); // Màu nền xá m
        setPreferredSize(new Dimension(0, 35)); // Chiều cao cố định 35px
        setBorder(new EmptyBorder(0, 15, 0, 15)); // Padding 2 bên

        title = new JLabel();
        updateTitle();
        title.setFont(new Font("Arial", Font.BOLD, 12));
        title.setForeground(Color.BLACK);

        JLabel greet = new JLabel("Chào: Admin"); // Sau này có thể truyền tên vào đây
        greet.setFont(new Font("Arial", Font.BOLD, 12));
        greet.setForeground(Color.BLACK);

        add(title, BorderLayout.WEST);
        add(greet, BorderLayout.EAST);
    }

    private void updateTitle(){
        title.setText("QUẢN LÝ KHO NƯỚC GIẢI KHÁT  |  " + titleCN);
    }
}

