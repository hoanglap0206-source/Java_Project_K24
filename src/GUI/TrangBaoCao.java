package GUI;

import BUS.BaoCao_BUS;
import Model.BaoCao;
import Model.ChiTietBaoCao;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.SwingConstants;

public class TrangBaoCao extends JPanel implements QuyenTrang {

    private JTable table;
    private DefaultTableModel model;
    private BaoCao_BUS bus;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JButton btnIn;

    private JComboBox<String> cboloai;
    private JTextField txtTuNgay;
    private JTextField txtDenNgay;
    private JLabel lblTong;
    private JTextField txtSearch;
    private JTextField txtMaBC = new JTextField();

    public TrangBaoCao() {
        bus = new BaoCao_BUS();

        setLayout(new BorderLayout(0, 15));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(15, 20, 15, 20));

        JPanel pnlNorth = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnlNorth.setBackground(new Color(230, 245, 255));
        pnlNorth.setBorder(new LineBorder(new Color(200, 220, 255, 1)));

        JPanel pnlSearch = new JPanel(new BorderLayout());
        txtSearch = new JTextField(" Tìm kiếm ", 15);
        txtSearch.setPreferredSize(new Dimension(150, 30));

        txtSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (txtSearch.getText().equals(" Tìm kiếm ")) {
                    txtSearch.setText("");
                    txtSearch.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (txtSearch.getText().trim().isEmpty()) {
                    txtSearch.setForeground(Color.GRAY);
                    txtSearch.setText(" Tìm kiếm ");
                }
            }
        });

        JButton btnSearchIcon = new JButton("🔍");
        btnSearchIcon.setBackground(Color.WHITE);
        btnSearchIcon.setFocusPainted(false);
        pnlSearch.add(txtSearch, BorderLayout.CENTER);
        pnlSearch.add(btnSearchIcon, BorderLayout.EAST);

        JButton btnRefresh = new JButton("🔄 Làm mới");
        btnRefresh.setBackground(Color.WHITE);
        btnRefresh.setFocusPainted(false);

        btnRefresh.addActionListener(e -> {
            txtTuNgay.setText("");
            txtDenNgay.setText("");
            txtSearch.setText(" Tìm kiếm ");
            txtSearch.setForeground(Color.GRAY);
            if (rowSorter != null) rowSorter.setRowFilter(null);
            loadBaoCao();
        });
        Image scaledImage = new ImageIcon(
                getClass().getResource("/Img/Excel.png")
        ).getImage().getScaledInstance(20,20,Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JButton btnExcel = new JButton(" Xuất excel",scaledIcon);
        btnExcel.setBackground(Color.WHITE);
        btnExcel.setFocusPainted(false);

        btnExcel.addActionListener(e -> {
            if (table.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất Excel!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chọn vị trí lưu file Báo Cáo");

            // Lấy mã báo cáo làm tên file, nếu trống thì tạo tên mặc định
            String tenFile = txtMaBC.getText().isEmpty() ? "BaoCao_" + new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date()) : txtMaBC.getText();
            fileChooser.setSelectedFile(new java.io.File(tenFile + ".xlsx"));

            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                java.io.File fileToSave = fileChooser.getSelectedFile();
                String filePath = fileToSave.getAbsolutePath();
                if (!filePath.endsWith(".xlsx")) filePath += ".xlsx";

                try (java.io.FileOutputStream out = new java.io.FileOutputStream(filePath);
                     org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {

                    // Lấy tên Loại báo cáo để đặt tên cho Sheet
                    String tenSheet = cboloai.getSelectedItem().toString();
                    org.apache.poi.xssf.usermodel.XSSFSheet sheet = workbook.createSheet(tenSheet);

                    // --- TẠO STYLE CHỮ IN ĐẬM CHO HEADER VÀ TỔNG CỘNG ---
                    org.apache.poi.xssf.usermodel.XSSFCellStyle boldStyle = workbook.createCellStyle();
                    org.apache.poi.xssf.usermodel.XSSFFont font = workbook.createFont();
                    font.setBold(true);
                    boldStyle.setFont(font);

                    // 1. Ghi dòng Header (Tiêu đề bảng)
                    org.apache.poi.xssf.usermodel.XSSFRow headerRow = sheet.createRow(0);
                    // table.getColumnCount() - 1 để LỌC BỎ CỘT "XEM CHI TIẾT" CUỐI CÙNG
                    for (int i = 0; i < table.getColumnCount() - 1; i++) {
                        org.apache.poi.xssf.usermodel.XSSFCell cell = headerRow.createCell(i);
                        cell.setCellValue(table.getColumnName(i));
                        cell.setCellStyle(boldStyle); // Tô đậm tiêu đề
                    }

                    // 2. Chép dữ liệu từ bảng ra file Excel
                    for (int i = 0; i < table.getRowCount(); i++) {
                        org.apache.poi.xssf.usermodel.XSSFRow row = sheet.createRow(i + 1);
                        for (int j = 0; j < table.getColumnCount() - 1; j++) {
                            Object value = table.getValueAt(i, j);

                            if (value != null) {
                                // Cột 3 (SL Nhập), Cột 4 (SL Xuất), Cột 5 (Đơn giá) -> ÉP VỀ DẠNG SỐ
                                if (j == 3 || j == 4 || j == 5) {
                                    String strNum = value.toString().replaceAll("[,\\s]", "");
                                    try {
                                        row.createCell(j).setCellValue(Double.parseDouble(strNum));
                                    } catch (NumberFormatException ex) {
                                        row.createCell(j).setCellValue(value.toString());
                                    }
                                } else {
                                    row.createCell(j).setCellValue(value.toString());
                                }
                            } else {
                                row.createCell(j).setCellValue("");
                            }
                        }
                    }

                    // 3. Chép dòng Tổng Tiền xuống cuối cùng
                    int lastRowNum = table.getRowCount() + 2;
                    org.apache.poi.xssf.usermodel.XSSFRow totalRow = sheet.createRow(lastRowNum);

                    org.apache.poi.xssf.usermodel.XSSFCell lblTotalCell = totalRow.createCell(4); // Cột ghi chữ "TỔNG CỘNG"
                    lblTotalCell.setCellValue("TỔNG CỘNG:");
                    lblTotalCell.setCellStyle(boldStyle);

                    // Lọc bỏ thẻ HTML và chữ dư thừa để lấy đúng số tiền
                    String tongTienRaw = lblTong.getText().replaceAll("<[^>]*>", "").replace("TỔNG TIỀN ĐÃ THANH TOÁN:", "").replaceAll("VNĐ", "").trim();
                    String tienSo = tongTienRaw.replaceAll("[,\\s]", ""); // Bỏ dấu phẩy để thành số nguyên

                    org.apache.poi.xssf.usermodel.XSSFCell valueTotalCell = totalRow.createCell(5); // Cột ghi giá trị tiền
                    try {
                        valueTotalCell.setCellValue(Double.parseDouble(tienSo));
                    } catch (Exception ex) {
                        valueTotalCell.setCellValue(tongTienRaw);
                    }
                    valueTotalCell.setCellStyle(boldStyle);

                    // 4. Tự động kéo dãn độ rộng các cột cho đẹp
                    for (int i = 0; i < table.getColumnCount() - 1; i++) {
                        sheet.autoSizeColumn(i);
                    }

                    workbook.write(out);
                    JOptionPane.showMessageDialog(this, "Xuất Excel thành công!\nĐã lưu tại: " + filePath, "Thành công", JOptionPane.INFORMATION_MESSAGE);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi xuất file: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        JPanel pnlLoai = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlLoai.setOpaque(false);
        pnlLoai.add(new JLabel("Loại báo cáo"));
        cboloai = new JComboBox<>(new String[]{"Nhập hàng", "Xuất hàng"});
        cboloai.setPreferredSize(new Dimension(100, 28));
        cboloai.setBackground(Color.WHITE);
        cboloai.addActionListener(e -> loadBaoCao());
        pnlLoai.add(cboloai);

        JPanel pnlThoiGian = new JPanel(new GridLayout(2, 2, 5, 5));
        pnlThoiGian.setOpaque(false);
        pnlThoiGian.add(new JLabel("Từ ngày:"));
        txtTuNgay = new JTextField(8);
        txtTuNgay.setToolTipText("Nhập yyyy-MM-dd rồi nhấn Enter");
        txtTuNgay.addActionListener(e -> loadBaoCao());
        pnlThoiGian.add(txtTuNgay);

        pnlThoiGian.add(new JLabel("Đến ngày:"));
        txtDenNgay = new JTextField(8);
        txtDenNgay.setToolTipText("Nhập yyyy-MM-dd rồi nhấn Enter");
        txtDenNgay.addActionListener(e -> loadBaoCao());
        pnlThoiGian.add(txtDenNgay);

        pnlNorth.add(pnlSearch);
        pnlNorth.add(btnRefresh);
        pnlNorth.add(btnExcel);
        pnlNorth.add(pnlLoai);
        pnlNorth.add(pnlThoiGian);

        add(pnlNorth, BorderLayout.NORTH);

        JPanel pnlCenter = new JPanel(new BorderLayout(0, 10));
        pnlCenter.setBackground(Color.WHITE);

        JPanel pnlTableHeader = new JPanel(new BorderLayout());
        pnlTableHeader.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("BẢNG THÔNG TIN BÁO CÁO");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel pnlMaBaoCao = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        pnlMaBaoCao.setBackground(Color.WHITE);
        pnlMaBaoCao.add(new JLabel("Mã báo cáo:"));

        txtMaBC.setEditable(false);
        txtMaBC.setBackground(Color.WHITE);
        pnlMaBaoCao.add(txtMaBC);

        pnlTableHeader.add(lblTitle, BorderLayout.WEST);
        pnlTableHeader.add(pnlMaBaoCao, BorderLayout.EAST);


        String[] columns = {"Mã SP", "Tên SP", "Đơn vị tính", "Số lượng (nhập)", "Số lượng (xuất)", "Đơn giá", "Xem chi tiết"};

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };

        table = new JTable(model);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }


        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        table.getTableHeader().setPreferredSize(new Dimension(0,35));

        table.getTableHeader().setBackground(new Color(230, 230, 230));
        table.getTableHeader().setReorderingAllowed(false);
        table.setShowGrid(true);
        table.setGridColor(Color.BLACK);
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);


        JTableHeader header = table.getTableHeader();

        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setBackground(new Color(210,230,255)); // màu xanh
                lbl.setForeground(Color.BLACK);
                lbl.setOpaque(true);
                lbl.setBorder(BorderFactory.createMatteBorder(0,0,2,1,Color.BLACK));

                return lbl;
            }
        });


        rowSorter = new TableRowSorter<>(model);
        table.setRowSorter(rowSorter);
        java.util.List<RowSorter.SortKey> sortKeys=new java.util.ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(0,SortOrder.ASCENDING));
        rowSorter.setSortKeys(sortKeys);
        setupSearchLogic();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(Color.BLACK, 1));

        table.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox()));

        pnlCenter.add(pnlTableHeader, BorderLayout.NORTH);
        pnlCenter.add(scrollPane, BorderLayout.CENTER);

        add(pnlCenter, BorderLayout.CENTER);

        JPanel pnlSouth = new JPanel(new BorderLayout());
        pnlSouth.setBackground(Color.WHITE);
        pnlSouth.setBorder(new EmptyBorder(5, 0, 0, 0));

        lblTong = new JLabel("<html>TỔNG TIỀN ĐÃ THANH TOÁN: <font color='#1A932B'><b>0 VNĐ</b></font></html>");
        lblTong.setFont(new Font("Arial", Font.PLAIN, 14));

        btnIn = new JButton("In danh sách");
        Style.styleButton(btnIn);

        btnIn.setBackground(new Color(14, 129, 239));

        btnIn.addActionListener(e->{inDanhSach();

        });


        btnIn.setForeground(Color.WHITE);
        btnIn.setFont(new Font("Arial", Font.BOLD, 12));
        btnIn.setFocusPainted(false);
        btnIn.setPreferredSize(new Dimension(140, 35));

        pnlSouth.add(lblTong, BorderLayout.WEST);
        pnlSouth.add(btnIn, BorderLayout.EAST);

        add(pnlSouth, BorderLayout.SOUTH);

        // Tải dữ liệu ngay khi vừa bật Form
        loadBaoCao();
    }
    private void inDanhSach() {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để in!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        PrinterJob job = PrinterJob.getPrinterJob();
        String loaiBaoCao = cboloai.getSelectedItem().toString();
        job.setJobName("Bao_Cao_" + loaiBaoCao);

        PageFormat pf = job.defaultPage();
        pf.setOrientation(PageFormat.PORTRAIT); // Khổ dọc A4
        final PageFormat finalPf = pf;

        // Cột và độ rộng (Loại bỏ nút Xem)
        String[] cols = {"Mã SP", "Tên sản phẩm", "ĐVT", "SL (Nhập)", "SL (Xuất)", "Đơn giá"};
        float[] colW = {50, 170, 45, 65, 65, 115};

        Printable printable = new Printable() {
            @Override
            public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                float ox = (float) pageFormat.getImageableX();
                float oy = (float) pageFormat.getImageableY();
                float pageHeight = (float) pageFormat.getImageableHeight();
                g2.translate(ox, oy);

                // --- THUẬT TOÁN TÍNH TOÁN PHÂN TRANG ---
                float rowH = 22f; // Chiều cao 1 dòng
                float headerHeight = 30f + 35f + rowH; // Khúc tiêu đề phía trên tốn bao nhiêu chỗ?
                float footerHeight = 40f; // Khúc chừa lại ở dưới cùng cho chữ "Tổng tiền"

                // Tính xem 1 trang nhét được tối đa bao nhiêu dòng dữ liệu
                float maxRowsHeight = pageHeight - headerHeight - footerHeight;
                int rowsPerPage = (int) (maxRowsHeight / rowH);
                if (rowsPerPage <= 0) rowsPerPage = 1;

                int totalRows = model.getRowCount();
                int totalPages = (int) Math.ceil((double) totalRows / rowsPerPage);

                // Nếu số trang yêu cầu vượt quá tổng số trang -> Dừng in
                if (pageIndex >= totalPages) {
                    return Printable.NO_SUCH_PAGE;
                }
                // ----------------------------------------

                float curY = 0f;

                // --- 1. IN TIÊU ĐỀ BÁO CÁO (Lặp lại ở mỗi trang) ---
                g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
                g2.setColor(new java.awt.Color(37, 100, 180));
                g2.drawString("BÁO CÁO " + loaiBaoCao.toUpperCase() + " (Trang " + (pageIndex + 1) + "/" + totalPages + ")", 0, curY + 18);
                curY += 30;

                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                g2.setColor(Color.BLACK);
                g2.drawString("Mã báo cáo: " + txtMaBC.getText(), 0, curY);

                String tuNgay = txtTuNgay.getText().isEmpty() ? "Tất cả" : txtTuNgay.getText();
                String denNgay = txtDenNgay.getText().isEmpty() ? "Tất cả" : txtDenNgay.getText();
                g2.drawString("Thời gian: " + tuNgay + " đến " + denNgay, 0, curY + 16);
                curY += 35;

                // --- 2. IN HEADER BẢNG ---
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                float cx = 0;
                for (int i = 0; i < cols.length; i++) {
                    g2.setColor(new java.awt.Color(200, 218, 240));
                    g2.fillRect((int) cx, (int) curY, (int) colW[i], (int) rowH);
                    g2.setColor(java.awt.Color.BLACK);
                    g2.drawRect((int) cx, (int) curY, (int) colW[i], (int) rowH);
                    g2.drawString(cols[i], cx + 4, curY + rowH - 6);
                    cx += colW[i];
                }
                curY += rowH;

                // --- 3. IN NỘI DUNG BẢNG (Chỉ in đúng số dòng của trang hiện tại) ---
                int startRow = pageIndex * rowsPerPage;
                int endRow = Math.min(startRow + rowsPerPage, totalRows);

                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                for (int r = startRow; r < endRow; r++) {
                    cx = 0;
                    for (int c = 0; c < 6; c++) {
                        g2.setColor(r % 2 == 0 ? java.awt.Color.WHITE : new java.awt.Color(242, 247, 255));
                        g2.fillRect((int) cx, (int) curY, (int) colW[c], (int) rowH);
                        g2.setColor(new java.awt.Color(200, 215, 235));
                        g2.drawRect((int) cx, (int) curY, (int) colW[c], (int) rowH);
                        g2.setColor(java.awt.Color.BLACK);

                        Object val = model.getValueAt(r, c);
                        String text = val != null ? val.toString() : "";

                        // Căn phải cho số lượng và đơn giá
                        if (c >= 3) {
                            int textWidth = g2.getFontMetrics().stringWidth(text);
                            g2.drawString(text, cx + colW[c] - textWidth - 5, curY + rowH - 6);
                        } else {
                            g2.drawString(text, cx + 4, curY + rowH - 6);
                        }
                        cx += colW[c];
                    }
                    curY += rowH;
                }

                // --- 4. IN TỔNG TIỀN (CHỈ IN KHI ĐANG Ở TRANG CUỐI CÙNG) ---
                if (pageIndex == totalPages - 1) {
                    curY += 20;
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    g2.setColor(new java.awt.Color(26, 147, 43));
                    String tongTienIn = lblTong.getText().replaceAll("<[^>]*>", "");
                    g2.drawString(tongTienIn, 0, curY);
                }

                return Printable.PAGE_EXISTS;
            }
        };

        Window owner = SwingUtilities.getWindowAncestor(this);
        new PrintPreviewDialog(owner, printable, finalPf, "Báo Cáo " + loaiBaoCao).setVisible(true);
    }

    // Hàm thiết lập event cho thanh tìm kiếm (Đã được gọi ở trên)
    private void setupSearchLogic() {
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { performSearch(); }
            @Override
            public void removeUpdate(DocumentEvent e) { performSearch(); }
            @Override
            public void changedUpdate(DocumentEvent e) { performSearch(); }

            private void performSearch() {
                String text = txtSearch.getText();
                // Bỏ qua nếu là chữ gợi ý hoặc rỗng
                if (text.trim().isEmpty() || text.equals("Tìm kiếm") || text.equals(" Tìm kiếm ")) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text.trim()));
                }
            }
        });
    }

    // Hàm gọi BUS lấy dữ liệu
    private void loadBaoCao() {
        try {
            String loai = cboloai.getSelectedItem().toString();
            String strTuNgay = txtTuNgay.getText().trim();
            String strDenNgay = txtDenNgay.getText().trim();

            if (strTuNgay.isEmpty()) strTuNgay = "1970-01-01";
            if (strDenNgay.isEmpty()) strDenNgay = "2100-12-31";

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date tuNgay = sdf.parse(strTuNgay);
            Date denNgay = sdf.parse(strDenNgay);

            BaoCao bc = bus.taoBaoCao(loai, tuNgay, denNgay);
            phatSinhMaBaoCao();

            model.setRowCount(0);
            for (ChiTietBaoCao ct : bc.getDanhSachChiTiet()) {
                Object[] row = {
                        ct.getSanPham().getMaSP(),
                        ct.getSanPham().getTenSP(),
                        ct.getSanPham().getDonViTinh(),
                        ct.getSoLuongNhap(),
                        ct.getSoLuongXuat(),
                        String.format("%,.0f", ct.getSanPham().getGiaTien()),
                        "Xem"
                };
                model.addRow(row);
            }

            lblTong.setText("<html>TỔNG TIỀN ĐÃ THANH TOÁN: <font color='#1A932B'><b>"
                    + String.format("%,.0f", bc.getTongTien()) + " VNĐ</b></font></html>");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi định dạng ngày! Vui lòng nhập chuẩn theo yyyy-MM-dd", "Lỗi", JOptionPane.ERROR_MESSAGE);
            // Reset ô nhập để không bị lỗi liên hoàn
            txtTuNgay.setText("");
            txtDenNgay.setText("");
            loadBaoCao();
        }
    }

    // --- CÁC CLASS RENDERER VÀ EDITOR ---

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setText("Xem");
            setFocusPainted(false);
            setBackground(new Color(220, 220, 220));
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            return this;
        }
    }
    public void phatSinhMaBaoCao() {
        String loai = cboloai.getSelectedItem().toString();


        String prefix = loai.equals("Nhập hàng") ? "BCN" : "BCX";


        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy_HHmmss");
        String timeStamp = sdf.format(new Date());


        String maBaoCaoMoi = prefix + "-" + timeStamp;
        txtMaBC.setText(maBaoCaoMoi);
    }

    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private JTable table;
        private int currentRow;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("Xem");
            button.setFocusPainted(false);
            button.setBackground(new Color(147, 211, 255));
            button.addActionListener(e -> {
                fireEditingStopped();
                hienThiChiTietSanPham();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.table = table;
            this.currentRow = row;
            return button;
        }

        private void hienThiChiTietSanPham() {
            // Lấy Mã SP và Tên SP ở dòng mà người dùng vừa click
            String maSP = table.getValueAt(currentRow, 0).toString();
            String tenSP = table.getValueAt(currentRow, 1).toString();

            // Lấy loại báo cáo đang hiển thị (Nhập hay Xuất) từ Combobox
            String loai = cboloai.getSelectedItem().toString();

            // Lấy cửa sổ cha để làm nền
            Window parentWindow = SwingUtilities.getWindowAncestor(button);

            // Mở Dialog
            ChiTietSanPham_Dialog dialog = new ChiTietSanPham_Dialog(parentWindow, maSP, tenSP, loai);
            dialog.setVisible(true);
        }


    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Báo cáo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new TrangBaoCao());
            frame.setSize(1000, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    @Override
    public void apDungQuyen(boolean coQuyen_Xem, boolean coQuyen_Them,
                            boolean coQuyen_Sua, boolean coQuyen_Xoa) {
        btnIn.setVisible(coQuyen_Them);
    }
}