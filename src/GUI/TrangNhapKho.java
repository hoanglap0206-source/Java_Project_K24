package GUI;

import BUS.SanPham_BUS;
import Model.SanPham;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TrangNhapKho extends JPanel {
    private SanPham_BUS spBus = new SanPham_BUS();
    private DefaultTableModel modelKho;
    private DefaultTableModel modelPhieu;
    private JTable tableKho;
    private JTable tablePhieu;
    private JTextField txtSoLuong;
    private JLabel lblTongTien;
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public TrangNhapKho() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(taoNoiDung(), BorderLayout.CENTER);
    }

    private JPanel taoNoiDung() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 0));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        panel.add(taoDanhSachKho());
        panel.add(taoPhieuNhap());

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

        txtSoLuong = new JTextField("3");
        txtSoLuong.setPreferredSize(new Dimension(60, 28));
        txtSoLuong.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        JButton btnThem = new JButton("+ Thêm");
        Style.styleButton(btnThem);

        btnThem.addActionListener(e -> handleAddProduct());

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

        modelKho = new DefaultTableModel(columns, 0);
        tableKho = new JTable(modelKho);

        tableKho.setRowHeight(30);
        tableKho.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableKho.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableKho.getTableHeader().setBackground(new Color(210,230,255));
        tableKho.getTableHeader().setReorderingAllowed(false);

        loadTableData();

        tableKho.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                txtSoLuong.requestFocus();
            }
        });

        return new JScrollPane(tableKho);
    }

    private void loadTableData() {
        modelKho.setRowCount(0);
        for (SanPham sp : spBus.getAll()) {
            modelKho.addRow(new Object[]{
                    sp.getMaSP(),
                    sp.getTenSP(),
                    sp.getSoLuong(),
                    sp.getGiaTien()
            });
        }
    }

    private JPanel taoPhieuNhap() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("THÔNG TIN PHIẾU NHẬP");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(taoThongTinPhieu(), BorderLayout.NORTH);
        panel.add(taoBangPhieuNhap(), BorderLayout.CENTER);
        panel.add(taoTongTien(), BorderLayout.SOUTH);

        return panel;
    }

    private JPanel taoThongTinPhieu() {
        JPanel panel = new JPanel(new GridLayout(3,2,10,10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new TitledBorder("THÔNG TIN PHIẾU NHẬP"));

        panel.add(new JLabel("Mã phiếu nhập:"));
        panel.add(new JTextField("Tự động tạo"));

        panel.add(new JLabel("Nhà cung cấp:"));
        panel.add(new JTextField());

        panel.add(new JLabel("Người tạo phiếu:"));
        panel.add(new JTextField());

        return panel;
    }

    private JPanel taoBangPhieuNhap() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        String[] columns = {"STT","Mã SP","Tên sản phẩm","SL","Ngày nhập","Đơn giá"};

        modelPhieu = new DefaultTableModel(columns, 0);
        tablePhieu = new JTable(modelPhieu);

        tablePhieu.setRowHeight(30);
        tablePhieu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablePhieu.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tablePhieu.getTableHeader().setBackground(new Color(210,230,255));
        tablePhieu.getTableHeader().setReorderingAllowed(false);

        // Căn giữa
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < tablePhieu.getColumnCount(); i++) {
            tablePhieu.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        // Nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnExcel = new JButton("Xuất Excel");
        Style.styleButton(btnExcel);

        JButton btnSua = new JButton("Sửa số lượng");
        Style.styleButton(btnSua);

        JButton btnXoa = new JButton("Xóa sản phẩm");
        Style.styleButton(btnXoa);

        buttonPanel.add(btnExcel);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnXoa);

        panel.add(new JScrollPane(tablePhieu), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel taoTongTien() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10,0,0,0));

        lblTongTien = new JLabel("TỔNG TIỀN NHẬP: 0đ");
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTongTien.setForeground(new Color(0,128,0));

        JButton btnNhap = new JButton("Nhập kho");
        Style.styleButton(btnNhap);

        panel.add(lblTongTien, BorderLayout.WEST);
        panel.add(btnNhap, BorderLayout.EAST);

        return panel;
    }

    private void handleAddProduct() {
        int row = tableKho.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chưa chọn sản phẩm");
            return;
        }

        int soLuong;
        long tongTien = 0;

        try {
            soLuong = Integer.parseInt(txtSoLuong.getText());
            if (soLuong <= 0) throw new Exception();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ");
            return;
        }

        // Lấy tổng tiền hiện tại từ label
        String current = lblTongTien.getText()
                .replace("TỔNG TIỀN NHẬP: ", "")
                .replace("đ", "");

        if (!current.isEmpty()) {
            tongTien = Long.parseLong(current);
        }

        String maSP = modelKho.getValueAt(row, 0).toString();
        String tenSP = modelKho.getValueAt(row, 1).toString();
        double donGia = Double.parseDouble(
                modelKho.getValueAt(row, 3).toString()
        );

        // Nếu đã tồn tại → cộng số lượng
        for (int i = 0; i < modelPhieu.getRowCount(); i++) {
            Object val = modelPhieu.getValueAt(i, 1);
            if (val != null && val.toString().equals(maSP)) {

                int slCu = (int) modelPhieu.getValueAt(i, 3);
                modelPhieu.setValueAt(slCu + soLuong, i, 3);

                tongTien += soLuong * donGia;
                lblTongTien.setText("TỔNG TIỀN NHẬP: " + tongTien + "đ");

                txtSoLuong.setText("");
                return;
            }
        }

        // Nếu chưa có → thêm mới
        String ngayNhap = LocalDate.now().format(DATE_FORMAT);
        int stt = modelPhieu.getRowCount() + 1;

        modelPhieu.addRow(new Object[]{
                stt, maSP, tenSP, soLuong, ngayNhap, donGia
        });

        tongTien += soLuong * donGia;
        lblTongTien.setText("TỔNG TIỀN NHẬP: " + tongTien + "đ");

        txtSoLuong.setText("");
    }
}
