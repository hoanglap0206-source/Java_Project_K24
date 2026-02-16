package UI;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class QLyTaiKhoan_GUI extends JPanel {
    private JTable table;
    private DefaultTableModel model;

    public QLyTaiKhoan_GUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // --- 1. Toolbar Panel (Dựa trên Figma mới nhất) ---
        JPanel pnlToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        pnlToolbar.setBackground(Color.WHITE);

        JTextField txtSearch = new JTextField("Tìm kiếm", 15);

        JComboBox<String> cbRole = new JComboBox<>(new String[]{"Role", "Quản lý", "Nhân viên"});
        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"Status", "Active", "Banned"});

        JButton btnRefresh = new JButton("Làm mới");
        JButton btnExport = new JButton("Export");

        JButton btnAdd = new JButton("+ Add User");
        btnAdd.setBackground(new Color(66, 165, 243)); // Màu xanh từ Figma
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);

        pnlToolbar.add(txtSearch);
        pnlToolbar.add(cbRole);
        pnlToolbar.add(cbStatus);
        pnlToolbar.add(btnRefresh);
        pnlToolbar.add(btnExport);
        pnlToolbar.add(btnAdd);

        // --- 2. Bảng hiển thị tài khoản ---
        String[] columns = {"", "Full Name", "Email", "Username", "Status", "Role", "Actions"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : super.getColumnClass(columnIndex);
            }
        };

        table = new JTable(model);
        setupTableStyle(); // Hàm tùy chỉnh giao diện bảng

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(pnlToolbar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void setupTableStyle() {
        // Chỉnh độ cao dòng thoáng đãng như Figma (35-40px)
        table.setRowHeight(40);
        table.setGridColor(new Color(240, 240, 240));
        table.setShowVerticalLines(false); // Chỉ hiện đường kẻ ngang

        // Căn giữa dữ liệu
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 1; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Tùy chỉnh Header
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(245, 245, 245));
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
    }
}