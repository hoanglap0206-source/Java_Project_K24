package GUI;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;

public class TrangXuatKho extends JPanel {

    private DefaultTableModel modelKho;
    private DefaultTableModel modelPhieu;

    public TrangXuatKho() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(taoNoiDung(), BorderLayout.CENTER);
    }

    private JPanel taoNoiDung() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 0));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        panel.add(taoDanhSachKho());
        panel.add(taoPhieuXuat());

        return panel;
    }

    private JPanel taoDanhSachKho() {
        JPanel panel = new JPanel(new BorderLayout(0,10));
        panel.setBackground(Color.WHITE);

        JPanel NorthPanel = new JPanel(new BorderLayout(0, 5));
        NorthPanel.setBackground(Color.WHITE);
        JLabel title = new JLabel("DANH SÁCH SẢN PHẨM TRONG KHO");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JPanel timKiemWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT,5,5));
        timKiemWrapper.setBackground(Color.WHITE);
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

        // Nút làm mới
        JButton btnLamMoi = new JButton("⟳ Làm mới");
        Style.styleButton(btnLamMoi);

        NorthPanel.add(title, BorderLayout.NORTH);
        timKiemWrapper.add(pnlSearchInput);
        timKiemWrapper.add(btnLamMoi);
        NorthPanel.add(timKiemWrapper, BorderLayout.CENTER);

        // Dòng số lượng
        JPanel SouthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        SouthPanel.setBackground(Color.WHITE);

        JLabel soLuong = new JLabel("Số lượng: ");

        JTextField txtSoLuong = new JTextField("3");
        txtSoLuong.setPreferredSize(new Dimension(60, 28));
        txtSoLuong.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        JButton btnThem = new JButton("+ Thêm");
        Style.styleButton(btnThem);

        SouthPanel.add(soLuong);
        SouthPanel.add(txtSoLuong);
        SouthPanel.add(btnThem);

        panel.add(NorthPanel, BorderLayout.NORTH);
        panel.add(taoBangSPKho(), BorderLayout.CENTER);
        panel.add(SouthPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JScrollPane taoBangSPKho() {
        String[] columns = {"Mã SP", "Tên sản phẩm", "SL", "Đơn giá"};

        Object[][] data = {
                {"SP001", "Coca Cola", "12", "150.000.000đ"},
                {"SP002", "7Up", "15", "150.000.000đ"},
        };

        JTable table = new JTable(data, columns);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(210,230,255));
        table.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        for(int i=0;i<table.getColumnCount();i++){
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(new Color(180,180,180)));

        return scrollPane;
    }

    private JPanel taoPhieuXuat() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("THÔNG TIN PHIẾU XUẤT");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(taoThongTinPhieu(), BorderLayout.NORTH);
        panel.add(taoBangPhieuXuat(), BorderLayout.CENTER);
        panel.add(taoTongTien(), BorderLayout.SOUTH);

        return panel;
    }

    private JPanel taoThongTinPhieu() {
        JPanel panel = new JPanel(new GridLayout(3,2,10,10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new TitledBorder("THÔNG TIN PHIẾU XUẤT"));

        panel.add(new JLabel("Mã phiếu xuất:"));
        panel.add(new JTextField("Tự động tạo"));

        panel.add(new JLabel("Khách hàng:"));
        panel.add(new JTextField());

        panel.add(new JLabel("Người tạo phiếu:"));
        panel.add(new JTextField());

        return panel;
    }

    private JPanel taoBangPhieuXuat() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        String[] columns = {"STT","Mã SP","Tên sản phẩm","SL","Đơn giá"};
        Object[][] data = {
                {1, "SP001", "Coca Cola", "4", "01/01/2025", "200.000đ"},
                {2, "SP002", "7Up", "4", "01/01/2025", "200.000đ"},
        };

        JTable table = new JTable(data, columns);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(210,230,255));
        table.getTableHeader().setReorderingAllowed(false);

        // Căn giữa toàn bộ
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        JScrollPane scroll = new JScrollPane(table);

        // Wrapper chứa bảng + VAT
        JPanel centerWrapper = new JPanel();
        centerWrapper.setLayout(new BorderLayout());
        centerWrapper.setBackground(Color.WHITE);

        JPanel vatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        vatPanel.setBackground(new Color(240,240,240));
        vatPanel.setBorder(new MatteBorder(1, 1, 1, 1, new Color(180,180,180)));

        JLabel lblVAT = new JLabel("Thuế (VAT): 10%");
        vatPanel.add(lblVAT);

        centerWrapper.add(scroll, BorderLayout.CENTER);
        centerWrapper.add(vatPanel, BorderLayout.SOUTH);

        // Nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnExcel = new JButton("Xuất excel");
        JButton btnSua = new JButton("Sửa số lượng");
        JButton btnXoa = new JButton("Xóa sản phẩm");

        Style.styleButton(btnExcel);
        Style.styleButton(btnSua);
        Style.styleButton(btnXoa);

        buttonPanel.add(btnExcel);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnXoa);

        panel.add(centerWrapper, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel taoTongTien() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10,0,0,0));

        JLabel lblTongTien = new JLabel("TỔNG TIỀN XUẤT: 0đ");
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTongTien.setForeground(new Color(0,128,0));

        JButton btnXuat = new JButton("Xuất kho");
        Style.styleButton(btnXuat);

        panel.add(lblTongTien, BorderLayout.WEST);
        panel.add(btnXuat, BorderLayout.EAST);

        return panel;
    }
}
