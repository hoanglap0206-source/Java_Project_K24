package GUI;

import BUS.PhieuNhap_BUS;
import Model.PhieuNhap;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import javax.swing.text.MaskFormatter;
import java.text.ParseException;
import java.util.ArrayList;

public class TrangPhieuNhap extends JPanel {

    private DefaultTableModel model;
    private JTable table;

    private PhieuNhap_BUS pnBus = new PhieuNhap_BUS();
    private java.text.DecimalFormat df = new java.text.DecimalFormat("#,### VNĐ"); // Phần cuối sẽ có VND
    private java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");

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
        cbNCC.setBackground(new Color(204, 227, 253));

        JComboBox<String> cbTrangThai = new JComboBox<>(new String[]{
                "Trạng thái",
                "Đã xuất kho",
                "Đã hủy",
                "Chờ duyệt"
        });
        cbTrangThai.setPreferredSize(new Dimension(130,35));
        cbTrangThai.setBackground(new Color(204, 227, 253));

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

        table.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Đổi con trỏ chuột thành hình bàn tay
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                // Nếu click vào hàng hợp lệ và đúng cột
                if (row >= 0 && col == 6) {
                    // Lấy các thông tin cần thiết từ dòng đang chọn
                    String maPN = model.getValueAt(row, 1).toString();
                    String ngayNhap = model.getValueAt(row, 2).toString();
                    String ncc = model.getValueAt(row, 3).toString();
                    String tongTien = model.getValueAt(row, 4).toString();

                    // Hiển thị Dialog
                    JFrame frameDialog = (JFrame) SwingUtilities.getWindowAncestor(TrangPhieuNhap.this);
                    ChiTietPhieuNhap_GUI dialog = new ChiTietPhieuNhap_GUI(frameDialog, maPN, ngayNhap, ncc, tongTien);
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
        model.setRowCount(0);
        ArrayList<PhieuNhap> ds = pnBus.getListPN();

        // Cần gọi thêm ChiTietPN_BUS để tính tiền
        BUS.ChiTietPN_BUS ctBus = new BUS.ChiTietPN_BUS();

        int stt = 1;
        for (PhieuNhap pn : ds) {
            // Tính tổng tiền từ danh sách chi tiết của phiếu này
            long tongTien = 0;
            ArrayList<Model.ChiTiet_PhieuNhap> dsChiTiet = ctBus.getListPN(pn.getMaPN());
            for (Model.ChiTiet_PhieuNhap ct : dsChiTiet) {
                tongTien += ct.getThanhTien();
            }

            model.addRow(new Object[]{
                    stt++,
                    pn.getMaPN(),
                    pn.getNgay_ct().format(dtf),
                    (pn.getNhaCC() != null) ? pn.getNhaCC().getMaNCC() : "N/A", // Lấy mã hoặc tên NCC
                    df.format(tongTien), // Hiển thị số tiền thực tế
                    "Đã nhập hàng",
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