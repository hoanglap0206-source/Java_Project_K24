package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TrangPhieuNhap extends JPanel {

    public TrangPhieuNhap() {

        setLayout(new GridBagLayout()); // Căn giữa toàn bộ
        setBackground(new Color(245, 245, 245));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        contentPanel.setPreferredSize(new Dimension(950, 550));

        // ====== TITLE ======
        JLabel title = new JLabel("DANH SÁCH PHIẾU NHẬP");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(title);

        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // ====== FILTER PANEL ======
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtSearch = new JTextField(15);
        txtSearch.setPreferredSize(new Dimension(150, 30));
        txtSearch.setBorder(BorderFactory.createTitledBorder("Tìm mã phiếu"));

        JTextField txtFromDate = new JTextField(8);
        txtFromDate.setBorder(BorderFactory.createTitledBorder("Từ ngày"));

        JTextField txtToDate = new JTextField(8);
        txtToDate.setBorder(BorderFactory.createTitledBorder("Đến ngày"));

        JComboBox<String> cbNhaCungCap = new JComboBox<>(new String[]{
                "Nhà cung cấp", "Pepsi Việt Nam", "Vinamilk", "Red Bull"
        });

        JComboBox<String> cbTrangThai = new JComboBox<>(new String[]{
                "Trạng thái", "Đã xuất kho", "Đã hủy", "Chờ duyệt"
        });

        filterPanel.add(txtSearch);
        filterPanel.add(txtFromDate);
        filterPanel.add(txtToDate);
        filterPanel.add(cbNhaCungCap);
        filterPanel.add(cbTrangThai);

        contentPanel.add(filterPanel);

        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // ====== TABLE ======
        String[] columnNames = {
                "STT", "Mã phiếu nhập", "Ngày nhập",
                "Nhà cung cấp", "Tổng tiền", "Trạng thái", "Thao tác"
        };

        Object[][] data = {
                {"1", "PN001", "28/06/2024", "Pepsi Việt Nam", "150.000.000đ", "Đã xuất kho", "Xem"},
                {"2", "PN002", "11/07/2024", "Vinamilk", "150.000.000đ", "Đã hủy", "Xem"},
                {"3", "PN003", "29/09/2024", "Red Bull", "150.000.000đ", "Chờ duyệt", "Xem | Sửa | Hủy"},
        };

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable table = new JTable(model);
        table.setRowHeight(28);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setPreferredSize(new Dimension(880, 300));

        contentPanel.add(scrollPane);

        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // ====== EXPORT BUTTON ======
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnExport = new JButton("Xuất Excel");
        bottomPanel.add(btnExport);

        contentPanel.add(bottomPanel);

        // ====== ADD vào CENTER ======
        add(contentPanel);
    }
}