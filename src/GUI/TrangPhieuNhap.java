package GUI;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;

public class TrangPhieuNhap extends JPanel {

    private DefaultTableModel model;
    private JTable table;

    public TrangPhieuNhap() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(15,20,20,20));

        add(taoTieuDe(), BorderLayout.NORTH);
        add(taoNoiDung(), BorderLayout.CENTER);
        add(taoFooter(), BorderLayout.SOUTH);
    }

    private JPanel taoTieuDe() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel lblTitle = new JLabel("DANH SÁCH PHIẾU NHẬP");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));

        panel.add(lblTitle, BorderLayout.WEST);
        panel.add(taoThanhCongCu(), BorderLayout.SOUTH);

        return panel;
    }

    private JPanel taoThanhCongCu() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT,10,10));
        panel.setOpaque(false);

        // Thanh tìm kiếm
        JTextField txtSearch = new JTextField("Tìm kiếm");
        txtSearch.setColumns(15);
        txtSearch.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
        txtSearch.setForeground(Color.GRAY);

        JPanel pnlSearchInput = new JPanel(new BorderLayout());
        pnlSearchInput.setBackground(Color.WHITE);
        pnlSearchInput.setPreferredSize(new Dimension(260,30));
        pnlSearchInput.setBorder(new CompoundBorder(
                new LineBorder(new Color(198,226,255), 2, true),
                new EmptyBorder(0,2,0,0)
        ));

        JButton btnSearchIcon = new JButton("🔍");
        btnSearchIcon.setBackground(new Color(214,238,253));
        btnSearchIcon.setBorderPainted(false);
        btnSearchIcon.setFocusPainted(false);
        btnSearchIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));

        pnlSearchInput.add(txtSearch, BorderLayout.CENTER);
        pnlSearchInput.add(btnSearchIcon, BorderLayout.EAST);

        txtSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) { // Khi người dùng click vào ô
                if (txtSearch.getText().equals("Tìm kiếm")) {
                    txtSearch.setText("");           // Xóa chữ "Tìm kiếm"
                    txtSearch.setForeground(Color.BLACK); // Đổi màu chữ sang đen để người dùng nhập
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) { // Khi người dùng click ra chỗ khác mà không nhập gì
                if (txtSearch.getText().isEmpty()) {
                    txtSearch.setForeground(Color.GRAY);
                    txtSearch.setText("Tìm kiếm");    // Hiện lại chữ gợi ý
                }
            }
        });

        JButton btnReload = new JButton("⟳ Làm mới");
        Style.styleButton(btnReload);

        JTextField txtFrom = new JTextField("Từ ngày");
        txtFrom.setPreferredSize(new Dimension(100,30));

        JTextField txtTo = new JTextField("Đến ngày");
        txtTo.setPreferredSize(new Dimension(100,30));

        JComboBox<String> cbNCC = new JComboBox<>(new String[]{
                "Nhà cung cấp"
        });

        JComboBox<String> cbTrangThai = new JComboBox<>(new String[]{
                "Trạng thái"
        });

        cbNCC.setPreferredSize(new Dimension(130,30));
        cbTrangThai.setPreferredSize(new Dimension(120,30));

        panel.add(pnlSearchInput);
        panel.add(btnReload);
        panel.add(txtFrom);
        panel.add(txtTo);
        panel.add(cbNCC);
        panel.add(cbTrangThai);

        return panel;
    }

    private JPanel taoNoiDung() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new LineBorder(new Color(200,200,200)));

        String[] columns = {
                "STT","Mã phiếu nhập","Ngày nhập",
                "Nhà cung cấp","Tổng tiền","Trạng thái","Thao tác"
        };

        model = new DefaultTableModel(columns,0);
        table = new JTable(model);

        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI",Font.BOLD,13));
        table.setFont(new Font("Segoe UI",Font.PLAIN,13));
        table.getTableHeader().setBackground(new Color(200,220,240));

        // Căn giữa toàn bộ
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        // ===== Render màu trạng thái =====
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                lbl.setHorizontalAlignment(SwingConstants.CENTER);

                if (value != null) {
                    String status = value.toString();

                    if (status.equals("Đã xuất kho")) {
                        lbl.setForeground(new Color(61, 130, 72));
                    } else if (status.equals("Đã hủy")) {
                        lbl.setForeground(new Color(206, 0, 3));
                    } else if (status.equals("Chờ duyệt")) {
                        lbl.setForeground(new Color(0, 24, 209));
                    }
                }
                return lbl;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        panel.add(scroll, BorderLayout.CENTER);

        themDuLieuMau();

        return panel;
    }

    private void themDuLieuMau() {
        model.addRow(new Object[]{
                1,"PN001","28/06/2024",
                "Pepsi Việt Nam","150.000.000đ",
                "Đã xuất kho","Xem"
        });

        model.addRow(new Object[]{
                2,"PN002","11/07/2024",
                "Vinamilk","150.000.000đ",
                "Đã hủy","Xem"
        });

        model.addRow(new Object[]{
                3,"PN003","29/09/2024",
                "Red Bull","150.000.000đ",
                "Chờ duyệt","Xem | Sửa | Hủy"
        });
    }

    private JPanel taoFooter() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(Color.WHITE);
        panel.setOpaque(true);

        JButton btnExcel = new JButton("Xuất excel");
        btnExcel.setBackground(new Color(220,240,220));

        panel.add(btnExcel);

        return panel;
    }
}