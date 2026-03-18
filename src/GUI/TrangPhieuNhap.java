package GUI;

import BUS.ChiTietPN_BUS;
import BUS.NCC_BUS;
import BUS.PhieuNhap_BUS;
import Model.ChiTiet_PhieuNhap;
import Model.NhaCungCap;
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

public class TrangPhieuNhap extends JPanel implements QuyenTrang {

    private DefaultTableModel model;
    private JTable table;

    private PhieuNhap_BUS pnBus = new PhieuNhap_BUS();
    private ChiTietPN_BUS ctBus = new ChiTietPN_BUS();
    private NCC_BUS nccBus      = new NCC_BUS();
    private DecimalFormat df = new DecimalFormat("#,### VNĐ");
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Component tìm kiếm / lọc
    private JTextField txtSearch;
    private JTextField tfFrom;
    private JTextField tfTo;
    private Timer searchTimer;
    private JComboBox<String> cbNhaCungCap;

    // Flag ngăn focusLost kích applyFilter trong khi đang reset
    private boolean isResetting = false;
    private boolean coQuyen_Xem = true;
    private boolean coQuyen_Xoa = true;

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

    //TIÊU ĐỀ + THANH CÔNG CỤ

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
            //Bật flag trước khi setText để chặn focusLost -> applyFilter
            isResetting = true;
            txtSearch.setText("Tìm kiếm...");
            txtSearch.setForeground(Color.GRAY);
            resetPlaceholder(tfFrom, "dd/mm/yyyy");
            resetPlaceholder(tfTo, "dd/mm/yyyy");
            cbNhaCungCap.setSelectedIndex(0);
            isResetting = false;
            // Refresh dữ liệu từ DB
            pnBus.refeshData();
            ctBus.refeshData();
            reloadComboNCC();
            loadDataToTable();
        });

        // Ô từ ngày
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
        setupDateAutoFormat(tfFrom);
        //Kiểm tra isResetting trước khi lọc
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
        setupDateAutoFormat(tfTo);
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

        //COMBOBOX NHÀ CUNG CẤP
        ArrayList<String> dsNCC = new ArrayList<>();
        dsNCC.add("Tất cả NCC");
        for (NhaCungCap ncc : nccBus.getListNCC()) {
            dsNCC.add(ncc.getMaNCC());
        }
        cbNhaCungCap = new JComboBox<>(dsNCC.toArray(new String[0]));
        cbNhaCungCap.setPreferredSize(new Dimension(130, 35));
        cbNhaCungCap.setBackground(new Color(214, 238, 253));
        cbNhaCungCap.setBorder(new LineBorder(new Color(198, 226, 255), 2, true));
        cbNhaCungCap.addActionListener(e -> {
            if (!isResetting) applyFilter();
        });
        panel.add(cbNhaCungCap);

        return panel;
    }

    //NỘI DUNG BẢNG

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

        // Căn giữa toàn bộ cột (trừ cột Thao tác)
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < columns.length - 1; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        // Renderer 2 nút cho cột Thao tác
        table.getColumnModel().getColumn(6).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean isSelected,
                    boolean hasFocus, int row, int col) {
                JPanel pnl = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 3));
                pnl.setBackground(isSelected ? t.getSelectionBackground() : Color.WHITE);

                JButton btnXem = new JButton("Xem");
                btnXem.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                btnXem.setForeground(new Color(0, 100, 220));
                btnXem.setBackground(new Color(214, 238, 253));
                btnXem.setBorder(new CompoundBorder(
                        new LineBorder(new Color(150, 200, 255), 1, true),
                        new EmptyBorder(2, 8, 2, 8)));
                btnXem.setFocusPainted(false);
                btnXem.setVisible(coQuyen_Xem);

                JButton btnXoa = new JButton("Xóa");
                btnXoa.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                btnXoa.setForeground(Color.WHITE);
                btnXoa.setBackground(new Color(220, 60, 60));
                btnXoa.setBorder(new CompoundBorder(
                        new LineBorder(new Color(190, 40, 40), 1, true),
                        new EmptyBorder(2, 8, 2, 8)));
                btnXoa.setFocusPainted(false);
                btnXoa.setVisible(coQuyen_Xoa);

                pnl.add(btnXem);
                pnl.add(btnXoa);
                return pnl;
            }
        });
        table.getColumnModel().getColumn(6).setPreferredWidth(150);

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

        // Click cột "Thao tác" — phân biệt nút Xem / Xóa
        table.setCursor(new Cursor(Cursor.HAND_CURSOR));
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row < 0 || col != 6) return;

                java.awt.Rectangle cellRect = table.getCellRect(row, col, false);
                int xInCell = e.getX() - cellRect.x;
                int cellWidth = cellRect.width;

                String maPN = model.getValueAt(row, 1).toString();
                String ngayNhap = model.getValueAt(row, 2).toString();
                String ncc = model.getValueAt(row, 3).toString();
                String tongTien = model.getValueAt(row, 4).toString();

                if (xInCell < cellWidth / 2) {
                    // Nút XEM
                    if(!coQuyen_Xem) return;
                    JFrame frameDialog = (JFrame) SwingUtilities.getWindowAncestor(TrangPhieuNhap.this);
                    ChiTietPhieuNhap_GUI dialog = new ChiTietPhieuNhap_GUI(
                            frameDialog, maPN, ngayNhap, ncc, tongTien);
                    dialog.setVisible(true);
                } else {
                    // Nút XÓA
                    if(!coQuyen_Xoa) return;
                    int confirm = JOptionPane.showConfirmDialog(
                            TrangPhieuNhap.this,
                            "Bạn có chắc muốn xóa phiếu nhập " + maPN + "?",
                            "Xác nhận xóa",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);
                    if (confirm == JOptionPane.YES_OPTION) {
                        boolean ok = pnBus.deletePN(maPN);
                        if (ok) {
                            JOptionPane.showMessageDialog(TrangPhieuNhap.this,
                                    "Xóa phiếu nhập thành công!",
                                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                            loadDataToTable();
                        } else {
                            JOptionPane.showMessageDialog(TrangPhieuNhap.this,
                                    "Xóa thất bại! Vui lòng thử lại.",
                                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        loadDataToTable();

        return panel;
    }

    //LOAD + LỌC DỮ LIỆU

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

            // Lọc nhà cung cấp
            String selNCC = (String) cbNhaCungCap.getSelectedItem();
            if (selNCC != null && !selNCC.equals("Tất cả NCC"))
                if (!ncc.equalsIgnoreCase(selNCC)) continue;

            Object[] displayRow = row.clone();
            displayRow[0] = counter++; // Cập nhật lại STT theo thứ tự hiển thị
            model.addRow(displayRow);
        }
    }

    private void restartTimer() {
        if (searchTimer.isRunning()) searchTimer.restart();
        else searchTimer.start();
    }

    /**
     * Tự động chèn dấu / khi gõ số liên tục: "01022026" → "01/02/2026"
     * Nhấn Enter khi đủ 10 ký tự sẽ lọc ngay lập tức.
     */
    private void setupDateAutoFormat(JTextField field) {
        field.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private boolean isFormatting = false;

            private void format() {
                if (isFormatting) return;
                String raw = field.getText();
                String digits = raw.replaceAll("[^0-9]", "");
                if (digits.length() > 8) digits = digits.substring(0, 8);

                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < digits.length(); i++) {
                    sb.append(digits.charAt(i));
                    if (i == 1 || i == 3) sb.append('/');
                }
                String formatted = sb.toString();

                if (!formatted.equals(raw)) {
                    isFormatting = true;
                    SwingUtilities.invokeLater(() -> {
                        field.setText(formatted);
                        field.setCaretPosition(formatted.length());
                        isFormatting = false;
                    });
                }
            }

            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { format(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { }
        });

        field.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER
                        && field.getText().length() == 10) {
                    applyFilter();
                }
            }
        });
    }

    /** Cập nhật lại danh sách NCC trong combobox sau khi refresh DB */
    private void reloadComboNCC() {
        nccBus.refeshData();
        cbNhaCungCap.removeAllItems();
        cbNhaCungCap.addItem("Tất cả NCC");
        for (NhaCungCap ncc : nccBus.getListNCC()) {
            cbNhaCungCap.addItem(ncc.getMaNCC());
        }
        cbNhaCungCap.setSelectedIndex(0);
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

    //FOOTER

    private JPanel taoFooter() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(Color.WHITE);

        JButton btnExcel = new JButton("Xuất excel");
        btnExcel.setBackground(new Color(220, 240, 220));
        btnExcel.addActionListener(e -> xuatExcel());
        panel.add(btnExcel);

        return panel;
    }

    //  XUẤT EXCEL

    private void xuatExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu file Excel");
        fileChooser.setSelectedFile(new java.io.File("DanhSachPhieuNhap.xlsx"));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Excel Files (*.xlsx)", "xlsx"));

        if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        java.io.File file = fileChooser.getSelectedFile();
        if (!file.getName().endsWith(".xlsx"))
            file = new java.io.File(file.getAbsolutePath() + ".xlsx");

        try (org.apache.poi.xssf.usermodel.XSSFWorkbook workbook =
                     new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {

            org.apache.poi.ss.usermodel.Sheet sheet =
                    workbook.createSheet("Danh sách phiếu nhập");

            // Style tiêu đề
            org.apache.poi.ss.usermodel.CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(
                    new org.apache.poi.xssf.usermodel.XSSFColor(
                            new byte[]{(byte)200, (byte)220, (byte)240}, null));
            headerStyle.setFillPattern(
                    org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(
                    org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
            headerStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);

            // Style dữ liệu
            org.apache.poi.ss.usermodel.CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
            dataStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            dataStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            dataStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            dataStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);

            // Dòng tiêu đề — bỏ cột "Thao tác" (index 6)
            String[] cols = {"STT", "Mã phiếu nhập", "Ngày nhập",
                    "Nhà cung cấp", "Tổng tiền", "Trạng thái"};
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
            for (int i = 0; i < cols.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(cols[i]);
                cell.setCellStyle(headerStyle);
            }

            // Ghi dữ liệu từ model (chỉ 6 cột đầu, bỏ cột "Thao tác")
            for (int r = 0; r < model.getRowCount(); r++) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(r + 1);
                for (int c = 0; c < 6; c++) {
                    org.apache.poi.ss.usermodel.Cell cell = row.createCell(c);
                    Object val = model.getValueAt(r, c);
                    cell.setCellValue(val != null ? val.toString() : "");
                    cell.setCellStyle(dataStyle);
                }
            }

            // Tự động điều chỉnh độ rộng cột
            for (int i = 0; i < cols.length; i++)
                sheet.autoSizeColumn(i);

            // Ghi file
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(file)) {
                workbook.write(fos);
            }

            JOptionPane.showMessageDialog(this,
                    "Xuất Excel thành công!\nFile: " + file.getAbsolutePath(),
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Xuất Excel thất bại: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void apDungQuyen(boolean coQuyen_Xem, boolean coQuyen_Them,
                            boolean coQuyen_Sua, boolean coQuyen_Xoa) {
        this.coQuyen_Xem = coQuyen_Xem;
        this.coQuyen_Xoa = coQuyen_Xoa;
        if (model != null) model.fireTableDataChanged();
    }
}