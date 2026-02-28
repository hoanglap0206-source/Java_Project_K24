package GUI;

import BUS.SanPham_BUS;
import Model.SanPham;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;

public class TrangXuatKho extends JPanel {
    private SanPham_BUS spBus = new SanPham_BUS();

    private DefaultTableModel modelKho;
    private DefaultTableModel modelPhieu;

    private JTable tableKho;
    private JTable tablePhieu;

    private JTextField txtSoLuong;
    private JLabel lblTongTien;

    public TrangXuatKho() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(taoNoiDung(), BorderLayout.CENTER);
        loadTableKho();
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

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        for(int i=0;i<tableKho.getColumnCount();i++){
            tableKho.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        JScrollPane scrollPane = new JScrollPane(tableKho);
        scrollPane.setBorder(new LineBorder(new Color(180,180,180)));

        return scrollPane;
    }

    private void loadTableKho() {
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

        modelPhieu = new DefaultTableModel(
                new String[]{"STT","Mã SP","Tên sản phẩm","SL","Đơn giá"},
                0
        );
        tablePhieu = new JTable(modelPhieu);

        tablePhieu.setRowHeight(30);
        tablePhieu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablePhieu.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tablePhieu.getTableHeader().setBackground(new Color(210,230,255));
        tablePhieu.getTableHeader().setReorderingAllowed(false);

        // Căn giữa toàn bộ
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < tablePhieu.getColumnCount(); i++) {
            tablePhieu.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        JScrollPane scroll = new JScrollPane(tablePhieu);

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

        lblTongTien = new JLabel("TỔNG TIỀN XUẤT: 0đ");
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTongTien.setForeground(new Color(0,128,0));

        JButton btnXuat = new JButton("Xuất kho");
        Style.styleButton(btnXuat);

        panel.add(lblTongTien, BorderLayout.WEST);
        panel.add(btnXuat, BorderLayout.EAST);

        return panel;
    }

    private void handleAddProduct() {
        int rowLeft = tableKho.getSelectedRow();
        if (rowLeft == -1) {
            JOptionPane.showMessageDialog(this, "Chưa chọn sản phẩm");
            return;
        }

        int soLuongNhap;
        long tongTien = 0;

        try {
            soLuongNhap = Integer.parseInt(txtSoLuong.getText());
            if (soLuongNhap <= 0) throw new Exception();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ");
            return;
        }

        // Lấy tổng tiền hiện tại từ label
        String current = lblTongTien.getText()
                .replace("TỔNG TIỀN XUẤT: ", "")
                .replace("đ", "");

        if (!current.isEmpty()) {
            tongTien = (long) Double.parseDouble(current);
        }

        String maSP  = modelKho.getValueAt(rowLeft, 0).toString();
        String tenSP = modelKho.getValueAt(rowLeft, 1).toString();
        double donGia = Double.parseDouble(
                modelKho.getValueAt(rowLeft, 3).toString()
        );

        // Nếu đã tồn tại → cộng số lượng
        for (int i = 0; i < modelPhieu.getRowCount(); i++) {
            Object val = modelPhieu.getValueAt(i, 1);
            if (val != null && val.toString().equals(maSP)) {

                int slCu = (int) modelPhieu.getValueAt(i, 3);
                modelPhieu.setValueAt(slCu + soLuongNhap, i, 3);

                tongTien += soLuongNhap * donGia;
                lblTongTien.setText("TỔNG TIỀN XUẤT: " + tongTien + "đ");

                txtSoLuong.setText("");
                return;
            }
        }

        // Nếu chưa có → thêm mới
        int stt = modelPhieu.getRowCount() + 1;

        modelPhieu.addRow(new Object[]{
                stt, maSP, tenSP, soLuongNhap, donGia
        });

        tongTien += soLuongNhap * donGia;
        lblTongTien.setText("TỔNG TIỀN XUẤT: " + tongTien + "đ");

        txtSoLuong.setText("");
    }
}
