package UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GiaoDienChinh_TopContent extends JPanel {
    private JPanel pnlCenterContainer;
    public GiaoDienChinh_TopContent() {
        setLayout(new BorderLayout());
        setBackground(new Color(198,226,255));

        // --- 2. THANH CÔNG CỤ (TOOLBAR) ---
        JPanel pnlToolBar = new JPanel(new BorderLayout());
        pnlToolBar.setBackground(Color.WHITE);
        pnlToolBar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.LIGHT_GRAY)); // Kẻ dưới
        pnlToolBar.setPreferredSize(new Dimension(0, 70)); // Chiều cao thanh công cụ

        pnlCenterContainer = new JPanel(new BorderLayout());
        pnlCenterContainer.setBackground(Color.WHITE);

        add(pnlCenterContainer, BorderLayout.CENTER);


        JPanel pnlSearchArea = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
        pnlSearchArea.setBackground(Color.WHITE);

        // Ô tìm kiếm + Icon kính lúp
        JPanel pnlSearchInput = new JPanel(new BorderLayout());
        pnlSearchInput.setBackground(Color.WHITE);
        pnlSearchInput.setBorder(new LineBorder(Color.GRAY, 1, true));
        pnlSearchInput.setPreferredSize(new Dimension(220, 35));

        JTextField txtSearch = new JTextField("Tìm kiếm");
        txtSearch.setBorder(new EmptyBorder(0, 10, 0, 0));
        txtSearch.setForeground(Color.GRAY);

        txtSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                // Khi người dùng click vào ô
                if (txtSearch.getText().equals("Tìm kiếm")) {
                    txtSearch.setText("");           // Xóa chữ "Tìm kiếm"
                    txtSearch.setForeground(Color.BLACK); // Đổi màu chữ sang đen để người dùng nhập
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                // Khi người dùng click ra chỗ khác mà không nhập gì
                if (txtSearch.getText().isEmpty()) {
                    txtSearch.setForeground(Color.GRAY);
                    txtSearch.setText("Tìm kiếm");    // Hiện lại chữ gợi ý
                }
            }
        });


        JButton btnSearchIcon = new JButton("🔍");
        btnSearchIcon.setBackground(new Color(214,238,253));
//        btnSearchIcon.setContentAreaFilled(false);
        btnSearchIcon.setBorderPainted(false);
        btnSearchIcon.setFocusPainted(false);

        pnlSearchInput.add(txtSearch, BorderLayout.CENTER);
        pnlSearchInput.add(btnSearchIcon, BorderLayout.EAST);

        // Nút làm mới
        JButton btnRefresh = new JButton("\uD83D\uDD04 Làm mới");
        btnRefresh.setBackground(Color.WHITE); // Màu xanh
        btnRefresh.setForeground(Color.BLACK);
        btnRefresh.setPreferredSize(new Dimension(100, 35));
        btnRefresh.setFocusPainted(false);

        //ComboBox để lọc trong thanh TopContent
        String[] itemLoc = {"Lọc", "1", "2", "3", "4", "5"};
        JComboBox<String> comboBoxLoc = new JComboBox<>(itemLoc);

        comboBoxLoc.setBackground(new Color(204,227,253));
        comboBoxLoc.setPreferredSize(new Dimension(55,35));
        ((JLabel) comboBoxLoc.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        comboBoxLoc.setSelectedItem(0);
        comboBoxLoc.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                comboBoxLoc.removeItem("Lọc");
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                if (comboBoxLoc.getItemCount() == 1) {
                    comboBoxLoc.insertItemAt("Lọc", 0); // thêm lại
                    comboBoxLoc.setSelectedIndex(0);
                }
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {

            }
        });

        //Tạo icon EXCEL
        // Load icon
        ImageIcon excelIcon = new ImageIcon(getClass().getResource("/Img/Excel.png"));
        // Scale icon về 30x30
        Image scaledImage = excelIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        // Button Excel
        JButton btnExcel = new JButton("Xuất Excel", scaledIcon);
        btnExcel.setFont(new Font("Arial", Font.PLAIN, 14));
        btnExcel.setFocusPainted(false);
        btnExcel.setPreferredSize(new Dimension(
                btnExcel.getPreferredSize().width + 10, 35
        ));

        // Icon trái text phải
        btnExcel.setHorizontalAlignment(SwingConstants.CENTER);
        btnExcel.setHorizontalTextPosition(SwingConstants.RIGHT);
        btnExcel.setVerticalTextPosition(SwingConstants.CENTER);
        // Chỉnh màu
        btnExcel.setBackground(Color.WHITE);
        btnExcel.setFocusPainted(false);

        // Khoảng cách icon text
        btnExcel.setIconTextGap(3);

        pnlSearchArea.add(pnlSearchInput);
        pnlSearchArea.add(btnRefresh);
        pnlSearchArea.add(comboBoxLoc);
        //Add các button thêm xóa sửa
        pnlSearchArea.add(this.createToolButton("+ Thêm", Color.WHITE));
        pnlSearchArea.add(this.createToolButton("Xóa",Color.WHITE));
        pnlSearchArea.add(this.createToolButton("Sửa",Color.WHITE));
        //Add button Xuất excel
        pnlSearchArea.add(btnExcel);

        pnlToolBar.setOpaque(false);
        pnlCenterContainer.setOpaque(false);
        pnlSearchArea.setOpaque(false);

        pnlToolBar.add(pnlSearchArea, BorderLayout.CENTER);
        add(pnlToolBar, BorderLayout.CENTER);
        setOpaque(true);
    }

    public void setCustomCenterPanel(JPanel customPanel) {
        pnlCenterContainer.removeAll(); // Xóa cái cũ đi (nếu có)
        if (customPanel != null) {
            // Chỉnh lại màu nền cho đồng bộ
            customPanel.setBackground(Color.WHITE);
            pnlCenterContainer.add(customPanel);
        }
        pnlCenterContainer.revalidate();
        pnlCenterContainer.repaint();
    }

    //Tạo cơ bản các Button
    private JButton createToolButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 12));

        btn.setBackground(color);
        btn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 20,35));
        btn.setForeground(Color.BLACK);
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Cần tạo JFrame để chứa JPanel
            JFrame frame = new JFrame("Giao Diện Chính");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Thêm panel vào frame
            frame.add(new GiaoDienChinh_TopContent());

            frame.setSize(1000, 200); // Set kích thước demo
            frame.setLocationRelativeTo(null); // Ra giữa màn hình
            frame.setVisible(true);
        });
    }
}