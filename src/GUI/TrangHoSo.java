package GUI;

import BUS.NV_BUS;
import BUS.PhieuNhap_BUS;
import BUS.PhieuXuat_BUS;
import BUS.ChiTietPN_BUS;
import BUS.ChiTietPX_BUS;
import Model.NhanVien;
import Model.PhieuNhap;
import Model.PhieuXuat;
import Model.ChiTiet_PhieuNhap;
import Model.ChiTiet_PhieuXuat;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class TrangHoSo extends JPanel {
    private static final Color CLR_BG       = new Color(245, 248, 252);
    private static final Color CLR_CARD     = Color.WHITE;
    private static final Color CLR_BORDER   = new Color(210, 220, 235);
    private static final Color CLR_HEADER   = new Color(86, 166, 255);
    private static final Color CLR_ACCENT   = new Color(0, 180, 90);
    private static final Color CLR_LABEL    = new Color(80, 80, 100);
    private static final Color CLR_VALUE    = new Color(20, 20, 40);
    private static final Font  FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 26);
    private static final Font  FONT_KEY     = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font  FONT_VAL     = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font  FONT_TBL_HDR = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font  FONT_TBL     = new Font("Segoe UI", Font.PLAIN, 13);

    //  Tên trường hiển thị
    private final String[] FIELD_NAMES = {"Mã nhân viên", "Tên nhân viên", "Số điện thoại", "Chức vụ", "Tổng số đơn hàng", "Tổng chi tiêu"};

    private JLabel[] lblValues;
    private JTable tbHistory;
    private DefaultTableModel tableModel;
    private JComboBox<String> cboLoaiPhieu;

    // BUS
    private PhieuNhap_BUS pnBUS;
    private PhieuXuat_BUS pxBUS;
    private ChiTietPN_BUS ctpnBUS;
    private ChiTietPX_BUS ctpxBUS;

    // Tham chieu ManHinhChinh để nút Thoát dùng
    private ManHinhChinh manHinhChinh;

    public TrangHoSo() {
        String maNV = ManHinhChinh.currentMaNV;
        initBUS();
        initComponents(maNV);
        loadData(maNV);
    }

    private void initBUS() {
        pnBUS   = new PhieuNhap_BUS();
        pxBUS   = new PhieuXuat_BUS();
        ctpnBUS = new ChiTietPN_BUS();
        ctpxBUS = new ChiTietPX_BUS();
    }

    private void initComponents(String maNV) {
        setLayout(new BorderLayout(0, 0));
        setBackground(CLR_BG);
        setBorder(new EmptyBorder(24, 28, 16, 28));

        // Tiêu đề + card thông tin
        JPanel pnlNorth = new JPanel(new BorderLayout(0, 16));
        pnlNorth.setOpaque(false);
        pnlNorth.add(buildTitleBar(), BorderLayout.NORTH);
        pnlNorth.add(buildInfoCard(),  BorderLayout.CENTER);
        add(pnlNorth, BorderLayout.NORTH);

        // Bảng lịch sử đơn hàng
        add(buildTableSection(), BorderLayout.CENTER);

        // Nút bấm
        add(buildButtonBar(), BorderLayout.SOUTH);
    }

    // Tiêu đề trang
    private JPanel buildTitleBar() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setOpaque(false);
        pnl.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel lbl = new JLabel("Thông tin cá nhân");
        lbl.setFont(FONT_TITLE);
        lbl.setForeground(CLR_VALUE);

        // Đường gạch chân màu xanh
        JPanel underline = new JPanel();
        underline.setPreferredSize(new Dimension(0, 3));
        underline.setBackground(CLR_HEADER);

        pnl.add(lbl, BorderLayout.NORTH);
        pnl.add(underline, BorderLayout.SOUTH);
        return pnl;
    }

    // Card thông tin nhân viên
    private JPanel buildInfoCard() {
        JPanel card = new JPanel(new GridLayout(3, 4, 20, 12));
        card.setBackground(CLR_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CLR_BORDER, 1, true),
                new EmptyBorder(16, 20, 16, 20)
        ));

        lblValues = new JLabel[FIELD_NAMES.length];

        for (int i = 0; i < FIELD_NAMES.length; i++) {
            JLabel lblKey = new JLabel(FIELD_NAMES[i] + ":");
            lblKey.setFont(FONT_KEY);
            lblKey.setForeground(CLR_LABEL);

            lblValues[i] = new JLabel("—");
            lblValues[i].setFont(FONT_VAL);
            lblValues[i].setForeground(CLR_VALUE);

            // Hai ô cuối highlight màu đặc biệt
            if (i == 4) {  // Tổng số đơn
                lblValues[i].setForeground(CLR_HEADER);
                lblValues[i].setFont(new Font("Segoe UI", Font.BOLD, 14));
            }
            if (i == 5) {  // Tổng chi tiêu
                lblValues[i].setForeground(CLR_ACCENT);
                lblValues[i].setFont(new Font("Segoe UI", Font.BOLD, 14));
            }

            card.add(lblKey);
            card.add(lblValues[i]);
        }
        return card;
    }

    // Bảng + combobox lọc
    private JPanel buildTableSection() {
        JPanel pnl = new JPanel(new BorderLayout(0, 8));
        pnl.setOpaque(false);
        pnl.setBorder(new EmptyBorder(14, 0, 10, 0));

        // Thanh tiêu đề bảng + ComboBox
        JPanel pnlTableTop = new JPanel(new BorderLayout(10, 0));
        pnlTableTop.setOpaque(false);

        JLabel lblTableTitle = new JLabel("Lịch sử đơn hàng");
        lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTableTitle.setForeground(CLR_VALUE);

        // ComboBox chọn loại phiếu
        String[] loai = {"Phiếu nhập", "Phiếu xuất"};
        cboLoaiPhieu = new JComboBox<>(loai);
        cboLoaiPhieu.setFont(FONT_VAL);
        cboLoaiPhieu.setPreferredSize(new Dimension(160, 30));
        cboLoaiPhieu.addActionListener(e -> reloadTable());

        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pnlRight.setOpaque(false);
        pnlRight.add(new JLabel("Loại phiếu: "));
        pnlRight.add(cboLoaiPhieu);

        pnlTableTop.add(lblTableTitle, BorderLayout.WEST);
        pnlTableTop.add(pnlRight, BorderLayout.EAST);
        pnl.add(pnlTableTop, BorderLayout.NORTH);

        // Bảng dữ liệu
        String[] cols = {"STT", "Mã đơn", "Ngày lập", "Tổng tiền"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbHistory = new JTable(tableModel);

        // Style bảng
        tbHistory.setFont(FONT_TBL);
        tbHistory.setRowHeight(34);
        tbHistory.setShowGrid(false);
        tbHistory.setIntercellSpacing(new Dimension(0, 0));
        tbHistory.setSelectionBackground(new Color(220, 235, 255));
        tbHistory.setSelectionForeground(CLR_VALUE);

        // Header bảng
        JTableHeader header = tbHistory.getTableHeader();
        header.setFont(FONT_TBL_HDR);
        header.setBackground(CLR_HEADER);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 36));
        header.setReorderingAllowed(false);

        // Căn cột STT và Tổng tiền vào giữa
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tbHistory.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tbHistory.getColumnModel().getColumn(0).setMaxWidth(55);
        tbHistory.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        // Renderer xen kẽ màu dòng
        tbHistory.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 249, 255));
                }
                setHorizontalAlignment(column == 0 || column == 3
                        ? SwingConstants.CENTER : SwingConstants.LEFT);
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(tbHistory);
        scroll.setBorder(BorderFactory.createLineBorder(CLR_BORDER, 1));
        scroll.getViewport().setBackground(Color.WHITE);
        pnl.add(scroll, BorderLayout.CENTER);

        return pnl;
    }

    // Thanh nút bấm
    private JPanel buildButtonBar() {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 6));
        pnl.setOpaque(false);

        JButton btnChinhSua = makeButton("Chỉnh sửa", CLR_HEADER, Color.WHITE);
        JButton btnThoat = makeButton("Thoát", new Color(220, 53, 69), Color.WHITE);

        // Khi bấm nút Thoát chuyển về trang đầu tiên
        btnThoat.addActionListener(e -> {
            ManHinhChinh mhc = getManHinhChinh();
            if (mhc != null) {
                mhc.hienThiTrang("Tổng quan");
                // Kích hoạt highlight menu đầu tiên nếu ThanhBen hỗ trợ
                if (mhc.getThanhBen() != null) {
                    mhc.getThanhBen().clickFirstMenu();
                }
            }
        });

        pnl.add(btnChinhSua);
        pnl.add(btnThoat);
        return pnl;
    }

    private JButton makeButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(150, 36));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Khi hover vào
        Color hoverBg = bg.darker();
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(hoverBg); }
            @Override public void mouseExited(java.awt.event.MouseEvent e)  { btn.setBackground(bg); }
        });
        return btn;
    }

    public void loadData(String maNV) {
        if (maNV == null || maNV.isEmpty()) return;

        // Thông tin nhân viên
        NV_BUS nvBus = new NV_BUS();
        ArrayList<NhanVien> list = nvBus.getInfo_NV_BUS(maNV);
        if (list == null || list.isEmpty()) return;

        NhanVien nv = list.get(0);
        lblValues[0].setText(nv.getMaNV());
        lblValues[1].setText(nv.getHoTen());
        lblValues[2].setText(nv.getSDT());
        lblValues[3].setText(nv.getChucVu());

        // Tính tổng số đơn & tổng chi tiêu (nhập + xuất)
        int tongDon = 0;
        long tongChiTieu = 0;

        // Đơn nhập của nhân viên này
        for (PhieuNhap pn : pnBUS.getListPN()) {
            if (pn.getNhanVien().getMaNV().equalsIgnoreCase(maNV)) {
                tongDon++;
                ArrayList<ChiTiet_PhieuNhap> details =
                        ctpnBUS.getChiTietByMaPN_DB(pn.getMaPN());
                for (ChiTiet_PhieuNhap ct : details) {
                    tongChiTieu += ct.getThanhTien();
                }
            }
        }

        // Đơn xuất của nhân viên này
        for (PhieuXuat px : pxBUS.getListPX()) {
            if (px.getNhanVien().getMaNV().equalsIgnoreCase(maNV)) {
                tongDon++;
                ArrayList<ChiTiet_PhieuXuat> details =
                        ctpxBUS.getChiTietByMaPx_DB(px.getMaPX());
                for (ChiTiet_PhieuXuat ct : details) {
                    tongChiTieu += ct.getThanhTien();
                }
            }
        }

        lblValues[4].setText(tongDon + " đơn");
        lblValues[5].setText(String.format("%,dđ", tongChiTieu));

        reloadTable();
    }

    // Load dữ liệu bảng theo combobox đã chọn
    private void reloadTable() {
        if (tableModel == null) return;
        tableModel.setRowCount(0);

        String maNV = ManHinhChinh.currentMaNV;
        if (maNV == null || maNV.isEmpty()) return;

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        int loai = cboLoaiPhieu.getSelectedIndex(); // 0 = Nhập, 1 = Xuất

        if (loai == 0) {
            // Phiếu nhập
            int stt = 1;
            for (PhieuNhap pn : pnBUS.getListPN()) {
                if (!pn.getNhanVien().getMaNV().equalsIgnoreCase(maNV)) continue;

                long tongTien = 0;
                ArrayList<ChiTiet_PhieuNhap> details =
                        ctpnBUS.getChiTietByMaPN_DB(pn.getMaPN());
                for (ChiTiet_PhieuNhap ct : details) tongTien += ct.getThanhTien();

                String ngay = pn.getNgay_ct() != null
                        ? pn.getNgay_ct().format(fmt) : "—";
                tableModel.addRow(new Object[]{
                        stt++,
                        pn.getMaPN(),
                        ngay,
                        String.format("%,dđ", tongTien)
                });
            }
        } else {
            // Phiếu xuất
            int stt = 1;
            for (PhieuXuat px : pxBUS.getListPX()) {
                if (!px.getNhanVien().getMaNV().equalsIgnoreCase(maNV)) continue;

                long tongTien = 0;
                ArrayList<ChiTiet_PhieuXuat> details =
                        ctpxBUS.getChiTietByMaPx_DB(px.getMaPX());
                for (ChiTiet_PhieuXuat ct : details) tongTien += ct.getThanhTien();

                String ngay = px.getNgay_ct() != null
                        ? px.getNgay_ct().format(fmt) : "—";
                tableModel.addRow(new Object[]{
                        stt++,
                        px.getMaPX(),
                        ngay,
                        String.format("%,dđ", tongTien)
                });
            }
        }
    }

    // Tìm ManHinhChinh từ component
    private ManHinhChinh getManHinhChinh() {
        Container c = getParent();
        while (c != null) {
            if (c instanceof ManHinhChinh) return (ManHinhChinh) c;
            c = c.getParent();
        }
        return null;
    }

    public static void main(String[] args) {
        ManHinhChinh.currentMaNV = "NV01";
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Thông tin cá nhân");
            frame.setSize(1200, 700);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.add(new TrangHoSo());
            frame.setVisible(true);
        });
    }
}