package UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class GiaoDienChinh_Head extends JPanel {
    public GiaoDienChinh_Head() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // --- 1. THANH TIÊU ĐỀ XÁM ---

        // --- 2. THANH CÔNG CỤ (TOOLBAR) ---
        JPanel pnlToolBar = new JPanel(new BorderLayout());
        pnlToolBar.setBackground(Color.WHITE);
        pnlToolBar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.LIGHT_GRAY)); // Kẻ dưới
        pnlToolBar.setPreferredSize(new Dimension(0, 70)); // Chiều cao thanh công cụ

        // 2a. Các nút chức năng (Trái)
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        pnlButtons.setBackground(Color.WHITE);

        // Tạo nút với icon tự vẽ (Hình tròn màu)
        pnlButtons.add(createToolButton("Thêm", new Color(146, 208, 80)));
        pnlButtons.add(createToolButton("Xoá", new Color(255, 0, 0)));
        pnlButtons.add(createToolButton("Sửa", new Color(255, 255, 0)));
        pnlButtons.add(createToolButton("Xuất Excel", new Color(33, 115, 70)));

        pnlToolBar.add(pnlButtons, BorderLayout.CENTER);


        JPanel pnlSearchArea = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        pnlSearchArea.setBackground(Color.WHITE);

        // Ô tìm kiếm + Icon kính lúp
        JPanel pnlSearchInput = new JPanel(new BorderLayout());
        pnlSearchInput.setBackground(Color.WHITE);
        pnlSearchInput.setBorder(new LineBorder(Color.GRAY, 1, true));
        pnlSearchInput.setPreferredSize(new Dimension(300, 35));

        JTextField txtSearch = new JTextField("Tìm kiếm");
        txtSearch.setBorder(new EmptyBorder(0, 10, 0, 0));



        JButton btnSearchIcon = new JButton("🔍");
        btnSearchIcon.setContentAreaFilled(false);
        btnSearchIcon.setBorderPainted(false);
        btnSearchIcon.setFocusPainted(false);

        pnlSearchInput.add(txtSearch, BorderLayout.CENTER);
        pnlSearchInput.add(btnSearchIcon, BorderLayout.WEST);

        // Nút làm mới
        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.setBackground(new Color(146, 208, 80)); // Màu xanh
        btnRefresh.setForeground(Color.BLACK);
        btnRefresh.setPreferredSize(new Dimension(100, 35));
        btnRefresh.setFocusPainted(false);

        pnlSearchArea.add(pnlSearchInput);
        pnlSearchArea.add(btnRefresh);

        pnlToolBar.add(pnlSearchArea, BorderLayout.EAST);
        add(pnlToolBar, BorderLayout.CENTER);
    }


    private JButton createToolButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 12));


        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);

        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(70, 60));


        btn.setIcon(new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillOval(x, y, 24, 24); // Vẽ hình tròn 24x24
            }
            @Override
            public int getIconWidth() { return 24; }
            @Override
            public int getIconHeight() { return 24; }
        });

        return btn;
    }
}