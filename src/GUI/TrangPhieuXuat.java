package GUI;

import BUS.ChiTietPX_BUS;
import BUS.KhachHang_BUS;
import BUS.PhieuXuat_BUS;
import Model.ChiTiet_PhieuXuat;
import Model.KhachHang;
import Model.PhieuXuat;

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

public class TrangPhieuXuat extends JPanel implements QuyenTrang {

    private DefaultTableModel model;
    private JTable table;

    private PhieuXuat_BUS pxBus   = new PhieuXuat_BUS();
    private ChiTietPX_BUS ctpxBus = new ChiTietPX_BUS();
    private KhachHang_BUS khBus   = new KhachHang_BUS();

    private DecimalFormat df  = new DecimalFormat("#,### VNĐ");
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private DateTimeFormatter dtfFull = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    //Component lọc
    private JTextField   txtSearch;
    private JTextField   tfFrom;
    private JTextField   tfTo;
    private JComboBox<String> cbKhachHang;
    private JComboBox<String> cbTrangThai;
    private Timer        searchTimer;

    // Flag ngăn focusLost kích applyFilter khi đang reset
    private boolean isResetting = false;
    private boolean coQuyen_Xem = true;
    private boolean coQuyen_Xoa = true;

    // Dữ liệu gốc để lọc không cần reload DB
    private ArrayList<Object[]> allRows = new ArrayList<>();

    public TrangPhieuXuat() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(15, 20, 20, 20));

        add(taoTieuDe(),   BorderLayout.NORTH);
        add(taoNoiDung(),  BorderLayout.CENTER);
        add(taoFooter(),   BorderLayout.SOUTH);

        // Timer debounce 500ms
        searchTimer = new Timer(500, e -> applyFilter());
        searchTimer.setRepeats(false);

        // DocumentListener cho ô tìm kiếm
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { restartTimer(); }
            @Override public void removeUpdate(DocumentEvent e)  { restartTimer(); }
            @Override public void changedUpdate(DocumentEvent e) { }
        });
    }

    //TIÊU ĐỀ + THANH CÔNG CỤ

    private JPanel taoTieuDe() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel lblTitle = new JLabel("DANH SÁCH PHIẾU XUẤT");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));

        panel.add(lblTitle, BorderLayout.WEST);
        panel.add(taoThanhCongCu(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel taoThanhCongCu() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setOpaque(false);

        // ===== Ô TÌM KIẾM =====
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
            @Override public void focusGained(FocusEvent e) {
                if (txtSearch.getText().equals("Tìm kiếm...")) {
                    txtSearch.setText("");
                    txtSearch.setForeground(Color.BLACK);
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (txtSearch.getText().isEmpty()) {
                    txtSearch.setText("Tìm kiếm...");
                    txtSearch.setForeground(Color.GRAY);
                }
            }
        });

        // ===== NÚT LÀM MỚI =====
        JButton btnReload = new JButton("⟳ Làm mới");
        btnReload.setBackground(new Color(214, 238, 253));
        btnReload.setBorder(new CompoundBorder(
                new LineBorder(new Color(198, 226, 255), 2, true),
                new EmptyBorder(4, 10, 4, 10)
        ));
        btnReload.setFocusPainted(false);
        btnReload.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReload.addActionListener(e -> {
            isResetting = true;
            txtSearch.setText("Tìm kiếm...");
            txtSearch.setForeground(Color.GRAY);
            resetPlaceholder(tfFrom, "dd/MM/yyyy");
            resetPlaceholder(tfTo,   "dd/MM/yyyy");
            cbTrangThai.setSelectedIndex(0);
            isResetting = false;
            // Refresh từ DB
            pxBus.refeshData();
            ctpxBus.refeshData();
            khBus.refeshData();
            // Cập nhật lại danh sách KH trong combobox (phòng trường hợp có KH mới)
            reloadComboKH();
            loadDataToTable();
        });

        // ===== Ô NGÀY TỪ =====
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
        tfFrom.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                if (!isResetting) applyFilter();
            }
        });

        // ===== Ô NGÀY ĐẾN =====
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
        tfTo.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                if (!isResetting) applyFilter();
            }
        });

        // ===== COMBOBOX KHÁCH HÀNG =====
        // Lấy danh sách mã KH từ BUS để điền vào combobox
        ArrayList<String> dsKH = new ArrayList<>();
        dsKH.add("Tất cả KH");
        for (KhachHang kh : khBus.getListKH()) {
            dsKH.add(kh.getMaKH());
        }
        cbKhachHang = new JComboBox<>(dsKH.toArray(new String[0]));
        cbKhachHang.setPreferredSize(new Dimension(130, 35));
        cbKhachHang.setBackground(new Color(214, 238, 253));
        cbKhachHang.setBorder(new LineBorder(new Color(198, 226, 255), 2, true));
        cbKhachHang.addActionListener(e -> {
            if (!isResetting) applyFilter();
        });

        //COMBOBOX TRẠNG THÁI
        cbTrangThai = new JComboBox<>(new String[]{
                "Tất cả", "Đã xuất kho", "Chờ xuất kho"
        });
        cbTrangThai.setPreferredSize(new Dimension(130, 35));
        cbTrangThai.setBackground(new Color(214, 238, 253));
        cbTrangThai.setBorder(new LineBorder(new Color(198, 226, 255), 2, true));
        cbTrangThai.addActionListener(e -> {
            if (!isResetting) applyFilter();
        });

        panel.add(pnlSearchInput);
        panel.add(btnReload);
        panel.add(lblFrom);
        panel.add(tfFrom);
        panel.add(lblTo);
        panel.add(tfTo);
        panel.add(cbKhachHang);
        panel.add(cbTrangThai);

        return panel;
    }

    //NỘI DUNG BẢNG
    private JPanel taoNoiDung() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new LineBorder(new Color(200, 200, 200)));

        String[] columns = {
                "STT", "Mã phiếu xuất", "Ngày xuất",
                "Khách hàng", "Tổng tiền", "Trạng thái", "Thao tác"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return column == 6; }
        };
        table = new JTable(model);

        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setBackground(new Color(200, 220, 240));
        table.getTableHeader().setReorderingAllowed(false);

        // Căn giữa toàn bộ
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < columns.length; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        // Renderer + Editor 2 nút cho cột Thao tác
        ThaoTacButtonsRenderer thaoTacRenderer = new ThaoTacButtonsRenderer();
        ThaoTacButtonsEditor   thaoTacEditor   = new ThaoTacButtonsEditor();
        table.getColumnModel().getColumn(6).setCellRenderer(thaoTacRenderer);
        table.getColumnModel().getColumn(6).setCellEditor(thaoTacEditor);
        table.getColumnModel().getColumn(6).setPreferredWidth(150);


        // Renderer màu trạng thái
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
                        case "Đã xuất kho"  -> lbl.setForeground(new Color(61, 130, 72));
                        case "Chờ xuất kho" -> lbl.setForeground(new Color(0, 24, 209));
                        case "Đã hủy"       -> lbl.setForeground(new Color(206, 0, 3));
                        default             -> lbl.setForeground(Color.BLACK);
                    }
                }
                return lbl;
            }
        });

        table.setCursor(new Cursor(Cursor.HAND_CURSOR));

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        loadDataToTable();
        return panel;
    }

    //LOAD + LỌC DỮ LIỆU
    public void loadDataToTable() {
        allRows.clear();
        model.setRowCount(0);

        ArrayList<PhieuXuat> dsPX = pxBus.getListPX();
        int stt = 1;
        for (PhieuXuat px : dsPX) {
            long tongTien = 0;
            for (ChiTiet_PhieuXuat ct : ctpxBus.getListByMaPX(px.getMaPX())) {
                tongTien += ct.getThanhTien();
            }

            // Ngày chỉ lấy dd/MM/yyyy để lọc được; hiển thị đầy đủ giờ:phút
            String ngayHienThi = px.getNgay_ct().format(dtf);

            Object[] row = {
                    stt++,
                    px.getMaPX(),
                    ngayHienThi,
                    (px.getKhachHang() != null) ? px.getKhachHang().getMaKH() : "N/A",
                    df.format(tongTien),
                    "Đã xuất kho",   // Trạng thái mặc định — thay bằng field thật nếu model có
                    "<html><font color='blue'><u>Xem</u></font></html>"
            };
            allRows.add(row);
            model.addRow(row);
        }
    }

    private void applyFilter() {
        // --- Từ khoá ---
        String keyword = txtSearch.getText().trim();
        if (keyword.equalsIgnoreCase("Tìm kiếm...")) keyword = "";

        // --- Ngày ---
        String fromText = tfFrom.getText().trim();
        String toText   = tfTo.getText().trim();
        if (fromText.equalsIgnoreCase("dd/MM/yyyy")) fromText = "";
        if (toText.equalsIgnoreCase("dd/MM/yyyy"))   toText   = "";

        LocalDate fromDate = null, toDate = null;
        try { if (!fromText.isEmpty()) fromDate = LocalDate.parse(fromText, dtf); }
        catch (DateTimeParseException ignored) { }
        try { if (!toText.isEmpty())   toDate   = LocalDate.parse(toText,   dtf); }
        catch (DateTimeParseException ignored) { }

        // --- Khách hàng ---
        String selKH = (String) cbKhachHang.getSelectedItem();
        boolean filterKH = selKH != null && !selKH.equals("Tất cả KH");

        // --- Trạng thái ---
        String selTT = (String) cbTrangThai.getSelectedItem();
        boolean filterTT = selTT != null && !selTT.equals("Tất cả");

        model.setRowCount(0);
        int counter = 1;

        for (Object[] row : allRows) {
            String sttStr  = row[0].toString();
            String maPX    = row[1].toString().toLowerCase();
            String ngayStr = row[2].toString(); // "dd/MM/yyyy HH:mm:ss"
            String khStr   = row[3].toString();
            String ttStr   = row[5].toString();

            // Lọc từ khoá: STT, Mã phiếu xuất, Ngày xuất, Khách hàng
            if (!keyword.isEmpty()) {
                String kw = keyword.toLowerCase();
                boolean match = sttStr.contains(kw)
                        || maPX.contains(kw)
                        || ngayStr.contains(kw)
                        || khStr.toLowerCase().contains(kw);
                if (!match) continue;
            }

            // Lọc khoảng ngày
            if (fromDate != null || toDate != null) {
                try {
                    LocalDate ngay = LocalDate.parse(ngayStr, dtf);
                    if (fromDate != null && ngay.isBefore(fromDate)) continue;
                    if (toDate   != null && ngay.isAfter(toDate))    continue;
                } catch (Exception ignored) { }
            }

            // Lọc khách hàng
            if (filterKH && !khStr.equalsIgnoreCase(selKH)) continue;

            // Lọc trạng thái
            if (filterTT && !ttStr.equalsIgnoreCase(selTT)) continue;

            Object[] displayRow = row.clone();
            displayRow[0] = counter++;
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

    private void setPlaceholder(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(Color.GRAY);
        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });
    }

    private void resetPlaceholder(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(Color.GRAY);
    }

    /** Cập nhật lại danh sách KH trong combobox sau khi refresh DB */
    private void reloadComboKH() {
        isResetting = true;
        cbKhachHang.removeAllItems();
        cbKhachHang.addItem("Tất cả KH");
        for (KhachHang kh : khBus.getListKH()) {
            cbKhachHang.addItem(kh.getMaKH());
        }
        cbKhachHang.setSelectedIndex(0);
        isResetting = false;
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

    //Hàm xuất file excel
    private void xuatExcel(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu file Excel");
        fileChooser.setSelectedFile(new java.io.File("DanhSachPhieuXuat.xlsx"));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel file (*.xlsx)", "xlsx"));

        int chon = fileChooser.showSaveDialog(this);
        if(chon != JFileChooser.APPROVE_OPTION) return;

        java.io.File file = fileChooser.getSelectedFile();

        if(!file.getName().endsWith(".xlsx"))
            file = new java.io.File(file.getAbsolutePath() + ".xlsx");

        try(org.apache.poi.xssf.usermodel.XSSFWorkbook workbook =
                    new org.apache.poi.xssf.usermodel.XSSFWorkbook()){

            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Danh sách phiếu xuất");

            //Tiêu đề
            org.apache.poi.ss.usermodel.CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();

            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(
                    new org.apache.poi.xssf.usermodel.XSSFColor(
                            new byte[]{(byte) 200, (byte) 200, (byte) 240}, null));
            headerStyle.setFillPattern(
                    org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(
                    org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
            headerStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);

            // --- Style dữ liệu ---
            org.apache.poi.ss.usermodel.CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
            dataStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            dataStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            dataStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            dataStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);

            //Ghi dòng tiêu đề lấy từ cột của JTable
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
            String[] cols = {"STT", "Mã phiếu xuất", "Ngày xuất",
                    "Khách hàng", "Tổng tiền", "Trạng thái"};
            for (int i = 0; i < cols.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(cols[i]);
                cell.setCellStyle(headerStyle);
            }

            // Ghi dữ liệu từ model
            for (int r = 0; r < model.getRowCount(); r++) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(r + 1);
                for (int c = 0; c < 6; c++) { // chỉ lấy 6 cột đầu
                    org.apache.poi.ss.usermodel.Cell cell = row.createCell(c);
                    Object val = model.getValueAt(r, c);
                    cell.setCellValue(val != null ? val.toString() : "");
                    cell.setCellStyle(dataStyle);
                }
            }

            for (int i = 0; i < cols.length; i++)
                sheet.autoSizeColumn(i);

            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(file)) {
                workbook.write(fos);
            }

            JOptionPane.showMessageDialog(this,
                    "Xuất Excel thành công!\nFile: " + file.getAbsolutePath(),
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Xuất Excel thất bại: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Vẽ 2 nút Xem / Xóa trong ô
    private class ThaoTacButtonsRenderer implements TableCellRenderer {
        private final JPanel  pnl    = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 3));
        private final JButton btnXem = makeXemBtn();
        private final JButton btnXoa = makeXoaBtn();

        ThaoTacButtonsRenderer() {
            pnl.add(btnXem);
            pnl.add(btnXoa);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable t, Object value, boolean isSelected,
                boolean hasFocus, int row, int col) {
            pnl.setBackground(isSelected ? t.getSelectionBackground() : Color.WHITE);
            btnXem.setVisible(coQuyen_Xem);
            btnXoa.setVisible(coQuyen_Xoa);
            return pnl;
        }
    }

    /**
     * Editor cho cột Thao tác — bắt sự kiện click thật trên từng nút.
     * Khi JTable kích hoạt editor, nó lưu currentRow rồi gắn ActionListener
     * để xử lý đúng logic Xem / Xóa.
     */
    private class ThaoTacButtonsEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel  pnl    = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 3));
        private final JButton btnXem = makeXemBtn();
        private final JButton btnXoa = makeXoaBtn();
        private int currentRow;

        ThaoTacButtonsEditor() {
            pnl.add(btnXem);
            pnl.add(btnXoa);

            btnXem.addActionListener(e -> {
                fireEditingStopped();
                if (!coQuyen_Xem) return;
                String maPX = model.getValueAt(currentRow, 1).toString();
                String ngay = model.getValueAt(currentRow, 2).toString();
                String khach = model.getValueAt(currentRow, 3).toString();
                String tongTien = model.getValueAt(currentRow, 4).toString();
                JFrame owner = (JFrame) SwingUtilities.getWindowAncestor(TrangPhieuXuat.this);
                ChiTietPhieuXuat_GUI dialog =
                        new ChiTietPhieuXuat_GUI(owner, maPX, ngay, khach, tongTien);
                dialog.setVisible(true);
            });

            btnXoa.addActionListener(e -> {
                fireEditingStopped();
                if (!coQuyen_Xoa) return;
                String maPX = model.getValueAt(currentRow, 1).toString();
                int confirm = JOptionPane.showConfirmDialog(
                        TrangPhieuXuat.this,
                        "Bạn có chắc muốn xóa phiếu xuất " + maPX + "?",
                        "Xác nhận xóa",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean ok = pxBus.deletePX(maPX);
                    if (ok) {
                        JOptionPane.showMessageDialog(TrangPhieuXuat.this,
                                "Xóa phiếu xuất thành công!",
                                "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        loadDataToTable();
                    } else {
                        JOptionPane.showMessageDialog(TrangPhieuXuat.this,
                                "Xóa thất bại! Vui lòng thử lại.",
                                "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable t, Object value, boolean isSelected, int row, int col) {
            currentRow = row;
            pnl.setBackground(t.getSelectionBackground());
            btnXem.setVisible(coQuyen_Xem);
            btnXoa.setVisible(coQuyen_Xoa);
            return pnl;
        }

        @Override public Object getCellEditorValue() { return null; }
    }

    // Tạo nút dùng chung cho Renderer và Editor

    private JButton makeXemBtn() {
        JButton btn = new JButton("Xem");
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setForeground(new Color(0, 100, 220));
        btn.setBackground(new Color(214, 238, 253));
        btn.setBorder(new CompoundBorder(
                new LineBorder(new Color(150, 200, 255), 1, true),
                new EmptyBorder(2, 8, 2, 8)));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton makeXoaBtn() {
        JButton btn = new JButton("Xóa");
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(220, 60, 60));
        btn.setBorder(new CompoundBorder(
                new LineBorder(new Color(190, 40, 40), 1, true),
                new EmptyBorder(2, 8, 2, 8)));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    @Override
    public void apDungQuyen(boolean coQuyen_Xem, boolean coQuyen_Them, boolean coQuyen_Sua, boolean coQuyen_Xoa) {
        this.coQuyen_Xem = coQuyen_Xem;
        this.coQuyen_Xoa = coQuyen_Xoa;
        if (model != null) model.fireTableDataChanged();
    }
}