package GUI;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import BUS.PhieuXuat_BUS;
import BUS.ChiTietPX_BUS;
import Model.PhieuXuat;
import Model.ChiTiet_PhieuXuat;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;

public class TrangPhieuXuat extends JPanel {

    private DefaultTableModel model;
    private JTable table;

    private PhieuXuat_BUS pxBus = new PhieuXuat_BUS();
    private ChiTietPX_BUS ctpxBus = new ChiTietPX_BUS();

    public TrangPhieuXuat() {
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

        JPanel PXlSearchInput = new JPanel(new BorderLayout());
        PXlSearchInput.setBackground(Color.WHITE);
        PXlSearchInput.setPreferredSize(new Dimension(260,30));
        PXlSearchInput.setBorder(new CompoundBorder(
                new LineBorder(new Color(198,226,255), 2, true),
                new EmptyBorder(0,2,0,0)
        ));

        JButton btnSearchIcon = new JButton("🔍");
        btnSearchIcon.setBackground(new Color(214,238,253));
        btnSearchIcon.setBorderPainted(false);
        btnSearchIcon.setFocusPainted(false);
        btnSearchIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));

        PXlSearchInput.add(txtSearch, BorderLayout.CENTER);
        PXlSearchInput.add(btnSearchIcon, BorderLayout.EAST);

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
                "Khách hàng"
        });
        cbNCC.setBackground(new Color(204, 227, 253));

        JComboBox<String> cbTrangThai = new JComboBox<>(new String[]{
                "Trạng thái"
        });
        cbNCC.setBackground(new Color(204, 227, 253));

        cbNCC.setPreferredSize(new Dimension(130,30));
        cbTrangThai.setPreferredSize(new Dimension(120,30));

        panel.add(PXlSearchInput);
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
                "Khách hàng","Tổng tiền","Trạng thái","Thao tác"
        };

        model = new DefaultTableModel(columns,0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
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

        // Thiết kế sự kiện click chuột cho bảng
        table.setCursor(new Cursor(Cursor.HAND_CURSOR));
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                // Kiểm tra nếu click vào hàng hợp lệ và đúng cột Xem
                if (row >= 0 && col == 6) {
                    String maPX = model.getValueAt(row, 1).toString();
                    String ngay = model.getValueAt(row, 2).toString();
                    String khach = model.getValueAt(row, 3).toString();
                    String tongTien = model.getValueAt(row, 4).toString();

                    // Mở Dialog chi tiết
                    JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(TrangPhieuXuat.this);
                    ChiTietPhieuXuat_GUI dialog = new ChiTietPhieuXuat_GUI(parent, maPX, ngay, khach, tongTien);
                    dialog.setVisible(true);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        panel.add(scroll, BorderLayout.CENTER);

        loadDataToTable();

        return panel;
    }

    public void loadDataToTable() {
        model.setRowCount(0); // Xóa sạch bảng cũ
        ArrayList<PhieuXuat> dsPX = pxBus.getListPX(); // Lấy danh sách từ BUS
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        int stt = 1;
        for (PhieuXuat px : dsPX) {
            // Tính tổng tiền cho mỗi phiếu bằng cách duyệt chi tiết
            long tongTien = 0;
            ArrayList<ChiTiet_PhieuXuat> dsCT = ctpxBus.getListByMaPX(px.getMaPX());
            for (ChiTiet_PhieuXuat ct : dsCT) {
                tongTien += ct.getThanhTien();
            }

            model.addRow(new Object[]{
                    stt++,
                    px.getMaPX(),
                    px.getNgay_ct().format(formatter), // Định dạng ngày giờ 2026
                    px.getKhachHang().getMaKH(), // Mã khách hàng ngẫu nhiên KHxx
                    String.format("%,dđ", tongTien), // Định dạng tiền tệ
                    "Đã xuất kho",
                    "<html><font color='blue'><u>Xem</u></font></html>"
            });
        }
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