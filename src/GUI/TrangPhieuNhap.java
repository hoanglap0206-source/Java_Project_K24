package GUI;

import BUS.ChiTietPN_BUS;
import BUS.PhieuNhap_BUS;
import Model.ChiTiet_PhieuNhap;
import Model.PhieuNhap;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class TrangPhieuNhap extends JPanel {

    private DefaultTableModel model;
    private JTable table;

    private PhieuNhap_BUS pnBus = new PhieuNhap_BUS();
    private ChiTietPN_BUS ctBus = new ChiTietPN_BUS();
    private DecimalFormat df = new DecimalFormat("#,### VNĐ");
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Component tìm kiếm / lọc
    private JTextField txtSearch;
    private JTextField tfFrom;
    private JTextField tfTo;
    private Timer searchTimer;

    // FIX: Flag ngăn focusLost kích applyFilter trong khi đang reset
    private boolean isResetting = false;

    // Dữ liệu gốc để lọc không cần reload DB mỗi lần
    private ArrayList<Object[]> allRows = new ArrayList<>();

    public TrangPhieuNhap() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(15, 20, 20, 20));

        add(taoTieuDe(), BorderLayout.NORTH);
        add(taoNoiDung(), BorderLayout.CENTER);
        add(taoFooter(), BorderLayout.SOUTH);

        // Timer debounce 500ms
        searchTimer = new Timer(500, e -> applyFilter());
        searchTimer.setRepeats(false);

        // DocumentListener cho ô tìm kiếm
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { restartTimer(); }
            @Override public void removeUpdate(DocumentEvent e) { restartTimer(); }
            @Override public void changedUpdate(DocumentEvent e) { }
        });
    }

    // ==================== TIÊU ĐỀ + THANH CÔNG CỤ ====================

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
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setOpaque(false);

        // Ô TÌM KIẾM
        txtSearch = new JTextField("Tìm kiếm...");
        txtSearch.setColumns(15);
        txtSearch.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        txtSearch.setForeground(Color.GRAY);

        JPanel pnlSearchInput = new JPanel(new BorderLayout());
        pnlSearchInput.setBackground(Color.WHITE);
        pnlSearchInput.setPreferredSize(new Dimension(260, 35));
        pnlSearchInput.setBorder(new CompoundBorder(
                new LineBorder(new Color(198, 226, 255), 2, true),
                new EmptyBorder(0, 2, 0, 0)
        ));

        JButton btnSearchIcon = new JButton("🔍");
        btnSearchIcon.setBackground(new Color(214, 238, 253));
        btnSearchIcon.setBorderPainted(false);
        btnSearchIcon.setFocusPainted(false);
        btnSearchIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSearchIcon.addActionListener(e -> applyFilter());

        pnlSearchInput.add(txtSearch, BorderLayout.CENTER);
        pnlSearchInput.add(btnSearchIcon, BorderLayout.EAST);

        txtSearch.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtSearch.getText().equals("Tìm kiếm...")) {
                    txtSearch.setText("");
                    txtSearch.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (txtSearch.getText().isEmpty()) {
                    txtSearch.setText("Tìm kiếm...");
                    txtSearch.setForeground(Color.GRAY);
                }
            }
        });

        // NÚT LÀM MỚI
        JButton btnReload = new JButton("⟳ Làm mới");
        btnReload.setBackground(new Color(214, 238, 253));
        btnReload.setBorder(new CompoundBorder(
                new LineBorder(new Color(198, 226, 255), 2, true),
                new EmptyBorder(4, 10, 4, 10)
        ));
        btnReload.setFocusPainted(false);
        btnReload.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReload.addActionListener(e -> {
            // FIX: Bật flag trước khi setText để chặn focusLost -> applyFilter
            isResetting = true;
            txtSearch.setText("Tìm kiếm...");
            txtSearch.setForeground(Color.GRAY);
            resetPlaceholder(tfFrom, "dd/MM/yyyy");
            resetPlaceholder(tfTo, "dd/MM/yyyy");
            isResetting = false;
            // Refresh dữ liệu từ DB
            pnBus.refeshData();
            ctBus.refeshData();
            loadDataToTable();
        });

        // Ô NGÀY TỪ
        JLabel lblFrom = new JLabel("Từ ngày:");
        lblFrom.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        tfFrom = new JTextField();
        tfFrom.setPreferredSize(new Dimension(110, 35));
        tfFrom.setHorizontalAlignment(JTextField.CENTER);
        tfFrom.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tfFrom.setBorder(new CompoundBorder(
                new LineBorder(new Color(198, 226, 255), 2, true),
                new EmptyBorder(0, 5, 0, 5)
        ));
        setPlaceholder(tfFrom, "dd/MM/yyyy");
        // FIX: Kiểm tra isResetting trước khi lọc
        tfFrom.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                if (!isResetting) applyFilter();
            }
        });

        // Ô NGÀY ĐẾN
        JLabel lblTo = new JLabel("Đến ngày:");
        lblTo.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        tfTo = new JTextField();
        tfTo.setPreferredSize(new Dimension(110, 35));
        tfTo.setHorizontalAlignment(JTextField.CENTER);
        tfTo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tfTo.setBorder(new CompoundBorder(
                new LineBorder(new Color(198, 226, 255), 2, true),
                new EmptyBorder(0, 5, 0, 5)
        ));
        setPlaceholder(tfTo, "dd/MM/yyyy");
        // FIX: Kiểm tra isResetting trước khi lọc
        tfTo.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                if (!isResetting) applyFilter();
            }
        });

        panel.add(pnlSearchInput);
        panel.add(btnReload);
        panel.add(lblFrom);
        panel.add(tfFrom);
        panel.add(lblTo);
        panel.add(tfTo);

        return panel;
    }

    // ==================== NỘI DUNG BẢNG ====================

    private JPanel taoNoiDung() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new LineBorder(new Color(200, 200, 200)));

        String[] columns = {
                "STT", "Mã phiếu nhập", "Ngày nhập",
                "Nhà cung cấp", "Tổng tiền", "Trạng thái", "Thao tác"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);

        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setBackground(new Color(200, 220, 240));
        table.getTableHeader().setReorderingAllowed(false);

        // Căn giữa toàn bộ cột
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < columns.length; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        // Renderer màu cho cột Trạng thái
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean isSelected,
                    boolean hasFocus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                        t, value, isSelected, hasFocus, row, col);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                if (value != null) {
                    switch (value.toString()) {
                        case "Đã xuất kho" -> lbl.setForeground(new Color(61, 130, 72));
                        case "Đã hủy"      -> lbl.setForeground(new Color(206, 0, 3));
                        case "Chờ duyệt"   -> lbl.setForeground(new Color(0, 24, 209));
                        default            -> lbl.setForeground(new Color(61, 130, 72));
                    }
                }
                return lbl;
            }
        });

        // Click cột "Thao tác" mở dialog chi tiết
        table.setCursor(new Cursor(Cursor.HAND_CURSOR));
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row >= 0 && col == 6) {
                    String maPN     = model.getValueAt(row, 1).toString();
                    String ngayNhap = model.getValueAt(row, 2).toString();
                    String ncc      = model.getValueAt(row, 3).toString();
                    String tongTien = model.getValueAt(row, 4).toString();

                    JFrame frameDialog = (JFrame) SwingUtilities.getWindowAncestor(TrangPhieuNhap.this);
                    ChiTietPhieuNhap_GUI dialog = new ChiTietPhieuNhap_GUI(
                            frameDialog, maPN, ngayNhap, ncc, tongTien);
                    dialog.setVisible(true);
                }
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        loadDataToTable();

        return panel;
    }

    // ==================== LOAD + LỌC DỮ LIỆU ====================

    public void loadDataToTable() {
        allRows.clear();
        model.setRowCount(0);

        ArrayList<PhieuNhap> ds = pnBus.getListPN();
        int stt = 1;
        for (PhieuNhap pn : ds) {
            long tongTien = 0;
            for (ChiTiet_PhieuNhap ct : ctBus.getListPN(pn.getMaPN())) {
                tongTien += ct.getThanhTien();
            }

            Object[] row = {
                    stt++,
                    pn.getMaPN(),
                    pn.getNgay_ct().format(dtf),
                    (pn.getNhaCC() != null) ? pn.getNhaCC().getMaNCC() : "N/A",
                    df.format(tongTien),
                    "Đã nhập hàng",
                    "<html><font color='blue'><u>Xem</u></font></html>"
            };
            allRows.add(row);
            model.addRow(row);
        }
    }

    private void applyFilter() {
        String keyword = txtSearch.getText().trim();
        if (keyword.equalsIgnoreCase("Tìm kiếm...")) keyword = "";

        String fromText = tfFrom.getText().trim();
        String toText   = tfTo.getText().trim();
        if (fromText.equalsIgnoreCase("dd/MM/yyyy")) fromText = "";
        if (toText.equalsIgnoreCase("dd/MM/yyyy"))   toText   = "";

        LocalDate fromDate = null;
        LocalDate toDate   = null;
        try { if (!fromText.isEmpty()) fromDate = LocalDate.parse(fromText, dtf); }
        catch (DateTimeParseException ignored) { }
        try { if (!toText.isEmpty())   toDate   = LocalDate.parse(toText,   dtf); }
        catch (DateTimeParseException ignored) { }

        model.setRowCount(0);
        // FIX: Đổi tên biến counter để tránh trùng với sttStr bên dưới
        int counter = 1;

        for (Object[] row : allRows) {
            String sttStr  = row[0].toString(); // STT gốc — dùng để tìm kiếm
            String maPN    = row[1].toString().toLowerCase();
            String ngayStr = row[2].toString();
            String ncc     = row[3].toString().toLowerCase();

            // Lọc từ khoá: STT, Mã phiếu nhập, Ngày nhập, Nhà cung cấp
            if (!keyword.isEmpty()) {
                String kw = keyword.toLowerCase();
                boolean match = sttStr.contains(kw)
                        || maPN.contains(kw)
                        || ngayStr.contains(kw)
                        || ncc.contains(kw);
                if (!match) continue;
            }

            // Lọc khoảng ngày
            if (fromDate != null || toDate != null) {
                try {
                    LocalDate ngay = LocalDate.parse(ngayStr, dtf);
                    if (fromDate != null && ngay.isBefore(fromDate)) continue;
                    if (toDate   != null && ngay.isAfter(toDate))    continue;
                } catch (DateTimeParseException ignored) { }
            }

            Object[] displayRow = row.clone();
            displayRow[0] = counter++; // Cập nhật lại STT theo thứ tự hiển thị
            model.addRow(displayRow);
        }
    }

    private void restartTimer() {
        if (searchTimer.isRunning()) searchTimer.restart();
        else searchTimer.start();
    }

    // Gắn placeholder mờ cho JTextField
    private void setPlaceholder(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(Color.GRAY);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });
    }

    // Reset ô về trạng thái placeholder
    private void resetPlaceholder(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(Color.GRAY);
    }

    // ==================== FOOTER ====================

    private JPanel taoFooter() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(Color.WHITE);

        JButton btnExcel = new JButton("Xuất excel");
        btnExcel.setBackground(new Color(220, 240, 220));
        panel.add(btnExcel);

        return panel;
    }
}