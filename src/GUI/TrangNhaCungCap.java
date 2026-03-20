package GUI;

import BUS.NCC_BUS;
import Model.KhachHang;
import Model.NhaCungCap;
import com.mysql.cj.result.Row;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;


public class TrangNhaCungCap extends JPanel implements QuyenTrang {
    private JTable table;
    private DefaultTableModel model;
    private NCC_BUS nccBUS = new NCC_BUS();
    private TableRowSorter<DefaultTableModel> RowSorter;
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JComboBox<String> comboBoxLoc;
    // Components cho SplitPane và Form
    private JSplitPane splitPane;
    private JPanel panelForm;
    private JLabel lblFormTitle;
    private JTextField txtMaNCC, txtTenNCC, txtSdt, txtDiaChi;
    private String currentMode = "THEM";

    public TrangNhaCungCap() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        initUI();
        fillToTable();
    }

    private void initUI() {
        add(taoThanhCongCu(), BorderLayout.NORTH);

        // Phần thân chứa Table (Trái) và Form (Phải)
        JPanel panelContent = new JPanel(new BorderLayout());
        panelContent.setBackground(Color.WHITE);
        panelContent.setBorder(new EmptyBorder(10, 20, 20, 20));

        // 1. Tạo bảng và bọc trong ScrollPane
        JScrollPane scrollPane = taoBang();

        // 2. Tạo Panel Form (ẩn ban đầu)
        panelForm = taoPanelForm();
        panelForm.setVisible(false);

        // 3. Sử dụng JSplitPane để chia không gian
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, panelForm);
        splitPane.setResizeWeight(1.0);
        splitPane.setDividerSize(0);
        splitPane.setBorder(null);

        panelContent.add(taoTieuDe(), BorderLayout.NORTH);
        panelContent.add(splitPane, BorderLayout.CENTER);

        add(panelContent, BorderLayout.CENTER);
    }
    private JPanel taoNhomInput(String label, JTextField tf) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(new Color(245, 247, 250));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        p.add(lbl);
        p.add(Box.createVerticalStrut(5));
        p.add(tf);
        p.add(Box.createVerticalStrut(12));
        return p;
    }

    private JPanel taoPanelForm() {
        // 1. Panel ngoài cùng (Cố định chiều rộng và border)
        JPanel outer = new JPanel(new BorderLayout());
        outer.setPreferredSize(new Dimension(350, 0)); // Tăng nhẹ chiều rộng cho thoải mái
        outer.setBackground(new Color(245, 247, 250));
        outer.setBorder(new MatteBorder(0, 1, 0, 0, new Color(210, 220, 235)));

        // 2. Panel nội dung
        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setBackground(new Color(245, 247, 250));
        pnl.setBorder(new EmptyBorder(30, 24, 24, 24));

        // Tiêu đề
        lblFormTitle = new JLabel("THÊM KHÁCH HÀNG");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFormTitle.setForeground(new Color(30, 80, 160));
        lblFormTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(198, 220, 255));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnl.add(lblFormTitle);
        pnl.add(Box.createVerticalStrut(10));
        pnl.add(sep);
        pnl.add(Box.createVerticalStrut(25));

        // Khởi tạo các ô nhập liệu
        txtMaNCC = new JTextField();
        txtTenNCC = new JTextField();
        txtSdt = new JTextField();
        txtDiaChi = new JTextField();

        // Thêm các nhóm input (Bỏ đoạn add trùng lặp lblFormTitle ở đây)
        pnl.add(taoNhomInput("Mã khách hàng:", txtMaNCC));
        pnl.add(Box.createVerticalStrut(10));
        pnl.add(taoNhomInput("Họ và tên:", txtTenNCC));
        pnl.add(Box.createVerticalStrut(10));
        pnl.add(taoNhomInput("Số điện thoại:", txtSdt));
        pnl.add(Box.createVerticalStrut(10));
        pnl.add(taoNhomInput("Địa chỉ:", txtDiaChi));
        pnl.add(Box.createVerticalStrut(10));

        // Tạo khoảng trống co dãn để đẩy các nút xuống dưới nếu cần
        pnl.add(Box.createVerticalStrut(20));

        // 3. Panel chứa nút bấm
        JButton btnSave = new JButton("💾  Lưu");
        styleButton(btnSave, new Color(37, 120, 220), Color.WHITE);
        btnSave.addActionListener(e -> xuLyLuu());

        JButton btnCancel = new JButton("✕  Hủy");
        styleButton(btnCancel, new Color(220, 225, 235), new Color(60, 60, 60));
        btnCancel.addActionListener(e -> hideForm());

        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        pnlBtn.setBackground(new Color(245, 247, 250));
        pnlBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45)); // Giới hạn chiều cao panel nút

        pnlBtn.add(btnSave);
        pnlBtn.add(btnCancel);

        pnl.add(pnlBtn);

        // QUAN TRỌNG: Thêm pnl vào outer và trả về outer
        outer.add(pnl, BorderLayout.NORTH);
        return outer;
    }

    // Hàm hỗ trợ style nút cho gọn code
    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 38));
    }
    private void showForm(String mode, NhaCungCap ncc) {
        this.currentMode = mode;
        if (mode.equals("THEM")) {
            lblFormTitle.setText("THÊM KHÁCH HÀNG");
            String maKH = nccBUS.getNewMaKH();
            txtMaNCC.setText(maKH);
            txtMaNCC.setEditable(false);
            txtMaNCC.setBackground(new Color(235, 235, 235));
            txtTenNCC.setText("");
            txtSdt.setText("");
            txtDiaChi.setText("");

            panelForm.setVisible(true);
            SwingUtilities.invokeLater(()->{
                txtTenNCC.requestFocusInWindow();
            });

            splitPane.setDividerLocation(this.getWidth()-350);
        } else {
            lblFormTitle.setText("SỬA THÔNG TIN KH");
            txtMaNCC.setText(ncc.getMaNCC());
            txtMaNCC.setEditable(false);
            txtMaNCC.setBackground(new Color(230, 230, 230));

            txtTenNCC.setText(ncc.getTenNCC());
            txtSdt.setText(ncc.getSdt());
            txtDiaChi.setText(ncc.getDiaChi());

            txtTenNCC.requestFocus();
        }
        panelForm.setVisible(true);
        SwingUtilities.invokeLater(()->{
            txtTenNCC.requestFocusInWindow();
        });

        splitPane.setDividerLocation(this.getWidth()-350);
    }

    private void hideForm() { panelForm.setVisible(false); }

    private void xuLyLuu() {
        String ma = txtMaNCC.getText().trim();
        String ten = txtTenNCC.getText().trim();
        String sdt = txtSdt.getText().trim();
        String dc = txtDiaChi.getText().trim();

        // VALIDATION (Ràng buộc dữ liệu)
        if (ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên nhà cung cấp không được để trống!");
            txtTenNCC.requestFocus();
            return;
        }
        if (sdt.isEmpty() || !sdt.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại phải là 10 chữ số!");
            txtSdt.requestFocus();
            return;
        }
        if (dc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Địa chỉ không được để trống!");
            txtDiaChi.requestFocus();
            return;
        }

        NhaCungCap ncc = new NhaCungCap(ma, ten, dc, sdt);
        String res = currentMode.equals("THEM") ? nccBUS.addNCC(ncc) : nccBUS.updateNCC(ncc);

        if (res.toLowerCase().contains("thành công")) {
            JOptionPane.showMessageDialog(this, res);
            nccBUS.refeshData();
            fillToTable();
            hideForm();
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi: " + res);
        }

    }
    private JPanel taoThanhCongCu() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(new EmptyBorder(15, 20, 5, 20));

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(4, 10, 4, 10));


        // Thanh tìm kiếm
        String place = "Tìm kiếm (VD:NCC1)";
        JTextField txtSearch = new JTextField(place);
        txtSearch.setColumns(15);
        txtSearch.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        txtSearch.setForeground(Color.GRAY);

        JPanel pnlSearchInput = new JPanel(new BorderLayout());
        pnlSearchInput.setBackground(Color.WHITE);
        pnlSearchInput.setPreferredSize(new Dimension(260, 30));
        pnlSearchInput.setBorder(new CompoundBorder(
                new LineBorder(new Color(198, 226, 255), 2, true),
                new EmptyBorder(0, 2, 0, 0)
        ));

        JButton btnSearchIcon = new JButton("🔍");
        btnSearchIcon.setBackground(new Color(214, 238, 253));
        btnSearchIcon.setBorderPainted(false); //bỏ đường viền
        btnSearchIcon.setFocusPainted(false);//bỏ đường viền nét đứt
        btnSearchIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));//thay đổi hình con chuột thành bàn tay

        pnlSearchInput.add(txtSearch, BorderLayout.CENTER);
        pnlSearchInput.add(btnSearchIcon, BorderLayout.EAST);

        txtSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                // Khi người dùng click vào ô
                if (txtSearch.getText().equals(place)) {
                    txtSearch.setText("");           // Xóa chữ "Tìm kiếm"
                    txtSearch.setForeground(Color.BLACK); // Đổi màu chữ sang đen để người dùng nhập
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                // Khi người dùng click ra chỗ khác mà không nhập gì
                if (txtSearch.getText().isEmpty()) {
                    txtSearch.setForeground(Color.GRAY);
                    txtSearch.setText(place);    // Hiện lại chữ gợi ý
                }
            }
        });
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void filter() {
                SwingUtilities.invokeLater(() -> {
                    {
                        String text = txtSearch.getText();
                        if (text.equals(place) || text.trim().isEmpty()) {
                            if (RowSorter != null) RowSorter.setRowFilter(null);
                        } else {
                            if (RowSorter != null)
                                RowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1));
                        }
                    }
                    reIndex();
                });


            }

            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }
        });


        // Nút làm mới
        JButton btnLamMoi = new JButton("↻ Làm mới");
        btnLamMoi.addActionListener(e -> {
            txtSearch.setText(place);
            txtSearch.setForeground(Color.GRAY);
            if (RowSorter != null) RowSorter.setRowFilter(null);

            fillToTable();

            nccBUS.refeshData();
            this.revalidate(); //resest bố cục
            this.repaint(); //resest giao diện
            JOptionPane.showMessageDialog(this, "Dữ Liệu được cập thành công!");
        });
        Style.styleButton(btnLamMoi);


        // Combobox Lọc
        String[] itemLoc = {"Mặc định", "1-N", "A-Z", "Z-A"};
        comboBoxLoc = new JComboBox<>(itemLoc);

        // Style cơ bản
        comboBoxLoc.setBackground(new Color(214, 238, 253));
        comboBoxLoc.setPreferredSize(new Dimension(90, 30));
        comboBoxLoc.setFont(new Font("Segoe UI", Font.BOLD, 13));
        comboBoxLoc.setCursor(new Cursor(Cursor.HAND_CURSOR));
        comboBoxLoc.setSelectedIndex(0);

        // Placeholder "Lọc"
        comboBoxLoc.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                return lbl;
            }
        });


        // 3. Sự kiện sắp xếp (Sửa lại index cột cho chuẩn với bảng của bạn)
        comboBoxLoc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (RowSorter == null) return; // Kiểm tra tránh lỗi NullPointer

                java.util.List<RowSorter.SortKey> sortKeys = new ArrayList<>();
                int luachon = comboBoxLoc.getSelectedIndex();

                switch (luachon) {
                    case 1: // Mã KH 1-N (Cột index 1)
                        sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
                        break;
                    case 2: // Tên A-Z
                        // Cột index 2 là cột Tên
                        sortKeys.add(new RowSorter.SortKey(2, SortOrder.ASCENDING));
                        break;
                    case 3: // Tên Z-A
                        sortKeys.add(new RowSorter.SortKey(2, SortOrder.DESCENDING));
                        break;

                    default: // Mặc định STT tăng dần (Cột index 0)
                        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
                        break;
                }

                RowSorter.setSortKeys(sortKeys);
                RowSorter.sort();
                reIndex();
            }
        });


        // Các nút khác
        btnEdit = new JButton("Chỉnh sửa");
        Style.styleButton(btnEdit);
        btnDelete = new JButton("Xóa");
        Style.styleButton(btnDelete);
        btnAdd = new JButton("+ Thêm");
        Style.styleButton(btnAdd);
        Image scaledImage = new ImageIcon(
                getClass().getResource("/Img/Excel.png")
        ).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JButton btnExcel = new JButton("Xuất excel", scaledIcon);
        Style.styleButton(btnExcel);
        btnExcel.addActionListener(e -> xuatExcel());


        // Thêm vào panel
        panel.add(pnlSearchInput);
        panel.add(btnLamMoi);
        panel.add(comboBoxLoc);
        panel.add(btnEdit);
        panel.add(btnDelete);
        panel.add(btnAdd);
        panel.add(btnExcel);

        btnAdd.addActionListener(e -> showForm("THEM", null));
        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà cung cấp cần xoá từ bảng!");
                table.requestFocus();
                return;
            }
            int modelRow = table.convertRowIndexToModel(row);
            String maNCC = model.getValueAt(modelRow, 1).toString();
            String tenNCC = table.getValueAt(row, 2).toString();

            //hộp thoại để tránh bấm nhầm
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc chắn muốn xoá nhà cung cấp" + tenNCC + "(" + maNCC + ")?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (choice == JOptionPane.YES_OPTION) {
                String result = nccBUS.deleteNCC(maNCC);
                if (result.toLowerCase().contains("thành công!")) {
                    JOptionPane.showMessageDialog(this, result);
                    fillToTable();

                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi: " + result);
                }
            }

        });
        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Chọn nhà cung cấp cần sửa!");
                table.requestFocusInWindow(); // Focus vào bảng nếu chưa chọn dòng
                return;
            }
            try {
                // Lấy index chuẩn từ Model (tránh lỗi khi lọc/sắp xếp bảng)
                int modelRow = table.convertRowIndexToModel(row);

                // Lấy dữ liệu an toàn
                String ma = model.getValueAt(modelRow, 1).toString();
                String ten = model.getValueAt(modelRow, 2).toString();
                String sdt = model.getValueAt(modelRow, 3).toString();
                String dc = model.getValueAt(modelRow, 4).toString();

                // Tạo đối tượng và hiển thị Form
                NhaCungCap ncc = new NhaCungCap(ma, ten, dc, sdt);
                showForm("SUA", ncc);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi lấy dữ liệu: " + ex.getMessage());
            }
        });
        wrapper.add(panel,BorderLayout.CENTER);
        return wrapper;
    }
    public void reIndex(){
        for(int i=0; i<table.getRowCount();i++)
            table.setValueAt(i+1,i,0);
    }
    private JPanel taoNoiDung() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 25, 20, 25));

        panel.add(taoTieuDe(), BorderLayout.NORTH);
        panel.add(taoBang(), BorderLayout.CENTER);

        RowSorter=new TableRowSorter<>(model);
        table.setRowSorter(RowSorter);
        return panel;
    }

    private JPanel taoTieuDe() {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnl.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("DANH SÁCH NHÀ CUNG CẤP");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.BLACK);

        pnl.add(lblTitle);
        return pnl;
    }

    private JScrollPane taoBang() {
        String[] columns = {"STT","Mã NCC","Tên NCC","SĐT","Địa chỉ","Xem chi tiết"};

        model = new DefaultTableModel(columns,0){
            @Override
            public boolean isCellEditable(int row,int column){
                return column ==5;
            }
        };

        table = new JTable(model);

        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(210,230,255));
        table.getTableHeader().setReorderingAllowed(false);

        //hàm gọi class ButtonEditor và ButtonRender chạy chitietNCC
        table.getColumnModel().getColumn(5).setCellRenderer(new buttonRender());
        table.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox()));

        // Căn giữa toàn bộ
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        for(int i=0;i<table.getColumnCount();i++){
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(new Color(180,180,180)));

        return scrollPane;
    }

    private void xuatExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu file Excel");
        fileChooser.setSelectedFile(new java.io.File("DanhSachNhaCungCap.xlsx"));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Excel Files (*.xlsx)", "xlsx"));

        if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        java.io.File file = fileChooser.getSelectedFile();
        if (!file.getName().endsWith(".xlsx"))
            file = new java.io.File(file.getAbsolutePath() + ".xlsx");

        try (org.apache.poi.xssf.usermodel.XSSFWorkbook workbook =
                     new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {

            org.apache.poi.ss.usermodel.Sheet sheet =
                    workbook.createSheet("Danh sách nhà cung cấp");

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

            // Dòng tiêu đề — bỏ cột "Xem chi tiết" (index 5)
            String[] cols = {"STT", "Mã NCC", "Tên NCC", "SĐT", "Địa chỉ"};
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
            for (int i = 0; i < cols.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(cols[i]);
                cell.setCellStyle(headerStyle);
            }

            // Ghi dữ liệu từ model (chỉ 5 cột đầu, bỏ cột "Xem chi tiết")
            for (int r = 0; r < model.getRowCount(); r++) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(r + 1);
                for (int c = 0; c < cols.length; c++) {
                    org.apache.poi.ss.usermodel.Cell cell = row.createCell(c);
                    Object val = model.getValueAt(r, c);
                    cell.setCellValue(val != null ? val.toString() : "");
                    cell.setCellStyle(dataStyle);
                }
            }

            // Tự động điều chỉnh độ rộng cột
            for (int i = 0; i < cols.length; i++) sheet.autoSizeColumn(i);

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

    public void fillToTable(){
        if(table.isEditing()){
            table.getCellEditor().stopCellEditing();
        }

        model.setRowCount(0);
        ArrayList<NhaCungCap> list = nccBUS.getListNCC();
        int stt = 1;

        for(NhaCungCap ncc : list){
            Object[] row = {
                    stt++,
                    ncc.getMaNCC(),
                    ncc.getTenNCC(),
                    ncc.getSdt(),
                    ncc.getDiaChi(),
                    "Xem"
            };
            model.addRow(row);
        }
        model.fireTableDataChanged();
        if(RowSorter!=null)
            RowSorter.setSortKeys(null);
        comboBoxLoc.setSelectedIndex(0);

    }

    public NCC_BUS getNccBUS() {
        return this.nccBUS;
    }
    @Override
    public void apDungQuyen(boolean coQuyen_Xem, boolean coQuyen_Them,
                            boolean coQuyen_Sua, boolean coQuyen_Xoa) {
        btnAdd.setVisible(coQuyen_Them);
        btnEdit.setVisible(coQuyen_Sua);
        btnDelete.setVisible(coQuyen_Xoa);
    }
}