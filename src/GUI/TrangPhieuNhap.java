package GUI;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import javax.swing.text.MaskFormatter;
import java.text.ParseException;

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

        LineBorder lineBorder = new LineBorder(new Color(198,226,255),1,true);

        // ===== SEARCH =====
        JPanel pnlSearchInput = new JPanel(new BorderLayout());
        pnlSearchInput.setBackground(Color.WHITE);
        pnlSearchInput.setPreferredSize(new Dimension(220,35));
        pnlSearchInput.setBorder(lineBorder);

        JTextField txtSearch = new JTextField("Tìm mã phiếu");
        txtSearch.setBorder(new EmptyBorder(0,10,0,0));
        txtSearch.setForeground(Color.GRAY);

        txtSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (txtSearch.getText().equals("Tìm mã phiếu")) {
                    txtSearch.setText("");
                    txtSearch.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (txtSearch.getText().isEmpty()) {
                    txtSearch.setText("Tìm mã phiếu");
                    txtSearch.setForeground(Color.GRAY);
                }
            }
        });

        JButton btnSearch = new JButton("🔍");
        btnSearch.setBorderPainted(false);
        btnSearch.setBackground(new Color(214,238,253));

        pnlSearchInput.add(txtSearch, BorderLayout.CENTER);
        pnlSearchInput.add(btnSearch, BorderLayout.EAST);

        // ===== RELOAD =====
        JButton btnReload = new JButton("⟳");
        btnReload.setPreferredSize(new Dimension(45,35));
        btnReload.setBorder(lineBorder);
        btnReload.setBackground(Color.WHITE);

        // ===== DATE FILTER =====
        MaskFormatter dateMask = null;
        try {
            dateMask = new MaskFormatter("##/##/####");
            dateMask.setPlaceholderCharacter('_');
        } catch (Exception e) { }

        JFormattedTextField tfFrom = new JFormattedTextField(dateMask);
        tfFrom.setPreferredSize(new Dimension(110,35));
        tfFrom.setBorder(lineBorder);

        JFormattedTextField tfTo = new JFormattedTextField(dateMask);
        tfTo.setPreferredSize(new Dimension(110,35));
        tfTo.setBorder(lineBorder);

        // ===== COMBOBOX =====
        JComboBox<String> cbNCC = new JComboBox<>(new String[]{
                "Nhà cung cấp"
        });
        cbNCC.setPreferredSize(new Dimension(150,35));

        JComboBox<String> cbTrangThai = new JComboBox<>(new String[]{
                "Trạng thái",
                "Đã xuất kho",
                "Đã hủy",
                "Chờ duyệt"
        });
        cbTrangThai.setPreferredSize(new Dimension(130,35));

        panel.add(pnlSearchInput);
        panel.add(btnReload);
        panel.add(tfFrom);
        panel.add(tfTo);
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