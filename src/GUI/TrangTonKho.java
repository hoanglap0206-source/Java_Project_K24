package GUI;

import BUS.BaoCaoTonKho_BUS;
import Model.BaoCaoTonKho;
import Model.SanPham;
import BUS.SanPham_BUS;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class TrangTonKho extends JPanel implements QuyenTrang {
    private JTable table;
    private DefaultTableModel model;
    private BaoCaoTonKho_BUS bus;
    private TableRowSorter<DefaultTableModel> rowSorter;


    private JButton btnAdd, btnEdit, btnDelete, btnRefresh, btnExcel;


    private JSplitPane splitPane;
    private JPanel panelForm;
    private JLabel lblFormTitle;
    private JTextField txtMaSP, txtSL, txtDG, txtCanhBao, txtMaKe;
    private JComboBox<String> cbTenSP;
    private JComboBox<String> cbDVT;
    private boolean isEditMode = false;

    public TrangTonKho() {
        bus = new BaoCaoTonKho_BUS();
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);


        add(taoThanhCongCu(), BorderLayout.NORTH);


        JPanel pnlCenter = new JPanel(new BorderLayout());
        pnlCenter.setBackground(Color.WHITE);
        pnlCenter.setBorder(new EmptyBorder(10, 20, 20, 20));


        JPanel tableWrapper = new JPanel(new BorderLayout());


        JPanel pnlCompactInputs = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlCompactInputs.setOpaque(false);
        pnlCompactInputs.setBorder(new EmptyBorder(0, 0, 10, 0));

        tableWrapper.add(pnlCompactInputs, BorderLayout.NORTH);
        tableWrapper.add(taoBang(), BorderLayout.CENTER);


        panelForm = taoFormBenPhai();
        panelForm.setVisible(false);

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableWrapper, panelForm);
        splitPane.setResizeWeight(1.0);
        splitPane.setDividerSize(6);
        splitPane.setBorder(null);
        pnlCenter.add(splitPane, BorderLayout.CENTER);

        add(pnlCenter, BorderLayout.CENTER);


        loadDataToTable();
        addEvents();
        kiemTraVaTruNgayCanhBao();
    }



    private JScrollPane taoBang() {
        String[] columnNames = {"Mã SP", "Tên SP", "Đơn vị tính", "Số lượng", "Đơn giá", "Số ngày hết hạn", "Mã kệ"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(model);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setPreferredSize(new Dimension(0, 35));
        table.getTableHeader().setBackground(new Color(230, 230, 230));
        table.getTableHeader().setReorderingAllowed(false);
        table.setShowGrid(true);
        table.setGridColor(Color.LIGHT_GRAY);

        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setBackground(new Color(210, 230, 255));
                lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.GRAY));
                return lbl;
            }
        });

        rowSorter = new TableRowSorter<>(model);
        table.setRowSorter(rowSorter);

        JScrollPane scrollPane = new JScrollPane(table);
        return scrollPane;
    }

    private JPanel taoFormBenPhai() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setPreferredSize(new Dimension(320, 0));
        outer.setBackground(new Color(245, 247, 250));
        outer.setBorder(new MatteBorder(0, 1, 0, 0, new Color(210, 220, 235)));

        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setBackground(new Color(245, 247, 250));
        pnl.setBorder(new EmptyBorder(20, 20, 20, 20));


        lblFormTitle = new JLabel("THÊM SẢN PHẨM");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFormTitle.setForeground(new Color(30, 80, 160));
        pnl.add(lblFormTitle);
        pnl.add(Box.createVerticalStrut(20));


        txtMaSP = new JTextField();
        cbTenSP = new JComboBox<>();
        loadComboboxTenSP();
        cbDVT = new JComboBox<>(new String[]{"Lon", "Chai", "Thùng"});
        txtSL = new JTextField();
        txtDG = new JTextField();
        txtCanhBao = new JTextField();
        txtMaKe = new JTextField();

        pnl.add(nhomField("Mã sản phẩm", txtMaSP)); pnl.add(Box.createVerticalStrut(10));
        pnl.add(nhomCombo("Tên sản phẩm", cbTenSP)); pnl.add(Box.createVerticalStrut(10));
        pnl.add(nhomCombo("Đơn vị tính", cbDVT)); pnl.add(Box.createVerticalStrut(10));
        pnl.add(nhomField("Số lượng", txtSL)); pnl.add(Box.createVerticalStrut(10));
        pnl.add(nhomField("Đơn giá", txtDG)); pnl.add(Box.createVerticalStrut(10));
        pnl.add(nhomField("Cảnh báo (ngày)", txtCanhBao)); pnl.add(Box.createVerticalStrut(10));
        pnl.add(nhomField("Mã kệ", txtMaKe)); pnl.add(Box.createVerticalStrut(20));


        JButton btnSave = new JButton("💾  Lưu");
        btnSave.setBackground(new Color(37, 120, 220));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.addActionListener(e -> luuDuLieu());

        JButton btnCancel = new JButton("✕  Hủy");
        btnCancel.setBackground(Color.LIGHT_GRAY);
        btnCancel.setFocusPainted(false);
        btnCancel.addActionListener(e -> hideFormPanel());

        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        pnlBtn.setOpaque(false);
        pnlBtn.add(btnSave); pnlBtn.add(btnCancel);
        pnl.add(pnlBtn);

        outer.add(pnl, BorderLayout.CENTER);
        return outer;
    }


    private JPanel nhomField(String label, JTextField field) {
        JPanel g = new JPanel(new BorderLayout(0, 5));
        g.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        field.setPreferredSize(new Dimension(280, 30));
        g.add(lbl, BorderLayout.NORTH);
        g.add(field, BorderLayout.CENTER);
        return g;
    }

    private JPanel nhomCombo(String label, JComboBox<String> combo) {
        JPanel g = new JPanel(new BorderLayout(0, 5));
        g.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        combo.setPreferredSize(new Dimension(280, 30));
        g.add(lbl, BorderLayout.NORTH);
        g.add(combo, BorderLayout.CENTER);
        return g;
    }

    private JPanel createCompactInput(String labelText, Color bgColor, Color textColor) {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnl.setOpaque(false);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Arial", Font.BOLD, 11));
        JTextField txt = new JTextField(5);
        txt.setBackground(bgColor);
        txt.setForeground(textColor);
        txt.setBorder(new CompoundBorder(new LineBorder(Color.LIGHT_GRAY, 1), new EmptyBorder(2, 5, 2, 5)));
        pnl.add(lbl); pnl.add(txt);
        return pnl;
    }

    private JPanel taoThanhCongCu() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(new EmptyBorder(15, 20, 5, 20));

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        panel.setBackground(Color.WHITE);

        JTextField txtSearch = new JTextField("Tìm kiếm", 15);
        txtSearch.setForeground(Color.GRAY);
        txtSearch.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JPanel pnlSearchInput = new JPanel(new BorderLayout());
        pnlSearchInput.setBackground(Color.WHITE);
        pnlSearchInput.setBorder(new LineBorder(new Color(198, 226, 255), 2, true));
        JButton btnSearchIcon = new JButton("🔍");
        btnSearchIcon.setBackground(new Color(214, 238, 253));
        btnSearchIcon.setBorderPainted(false);
        pnlSearchInput.add(txtSearch, BorderLayout.CENTER);
        pnlSearchInput.add(btnSearchIcon, BorderLayout.EAST);

        txtSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) { if (txtSearch.getText().equals("Tìm kiếm")) { txtSearch.setText(""); txtSearch.setForeground(Color.BLACK); } }
            public void focusLost(java.awt.event.FocusEvent e) { if (txtSearch.getText().isEmpty()) { txtSearch.setForeground(Color.GRAY); txtSearch.setText("Tìm kiếm"); } }
        });
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
            public void insertUpdate(javax.swing.event.DocumentEvent e) { s(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { s(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { s(); }
            private void s() { String t = txtSearch.getText(); rowSorter.setRowFilter(t.trim().isEmpty() || t.equals("Tìm kiếm") ? null : RowFilter.regexFilter("(?i)" + t)); }
        });
        // hàm tìm kiếm classic
        /*
        ActionListener searchAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String t = txtSearch.getText().trim();


                if (t.isEmpty() || t.equals("Tìm kiếm")) {
                    loadDataToTable();
                } else {

                    timKiemTheoMa(t);
                }
            }
        };


        txtSearch.addActionListener(searchAction);
        btnSearchIcon.addActionListener(searchAction);
        // ----------------------------------------
*/
        String[] itemLoc = {"Lọc", "1-N", "A-Z", "Z-A"};
        JComboBox<String> cbLoc = new JComboBox<>(itemLoc);
        cbLoc.setBackground(new Color(214, 238, 253));
        cbLoc.setPreferredSize(new Dimension(90, 30));
        cbLoc.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cbLoc.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cbLoc.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                return lbl;
            }
        });

        cbLoc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (rowSorter == null) return;

                java.util.List<RowSorter.SortKey> sortKeys = new ArrayList<>();
                int luachon = cbLoc.getSelectedIndex();

                switch (luachon) {
                    case 1: // 1-N: Sắp xếp theo Mã SP (Cột index 0)
                        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
                        break;
                    case 2: // A-Z: Sắp xếp theo Tên SP (Cột index 1)
                        sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
                        break;
                    case 3: // Z-A: Sắp xếp theo Tên SP ngược lại
                        sortKeys.add(new RowSorter.SortKey(1, SortOrder.DESCENDING));
                        break;
                    default: // Mặc định khi chọn "Lọc"
                        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
                        break;
                }
                rowSorter.setSortKeys(sortKeys);
                rowSorter.sort();
            }
        });

        btnRefresh = new JButton("⟳ Làm mới"); Style.styleButton(btnRefresh);
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("Tìm kiếm");
            txtSearch.setForeground(Color.GRAY);
            cbLoc.setSelectedIndex(0); // Reset Combobox về chữ "Lọc"
            if (rowSorter != null) rowSorter.setRowFilter(null);

            bus.refreshData();
            loadDataToTable();
            hideFormPanel();
        });
        Style.styleButton(btnRefresh);
        btnEdit = new JButton("Chỉnh sửa"); Style.styleButton(btnEdit);
        btnDelete = new JButton("Xóa"); Style.styleButton(btnDelete);
        btnAdd = new JButton("+ Thêm"); Style.styleButton(btnAdd);
        Image scaledImage = new ImageIcon(
                getClass().getResource("/Img/Excel.png")
        ).getImage().getScaledInstance(20,20,Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        btnExcel = new JButton("Xuất excel",scaledIcon); Style.styleButton(btnExcel);

        panel.add(pnlSearchInput); panel.add(btnRefresh);panel.add(cbLoc);
        panel.add(btnEdit); panel.add(btnDelete); panel.add(btnAdd); panel.add(btnExcel);
        wrapper.add(panel, BorderLayout.CENTER);
        return wrapper;
    }


    private void addEvents() {
        btnAdd.addActionListener(e -> {
            lblFormTitle.setText("THÊM SẢN PHẨM TỒN KHO");

            clearForm();
            isEditMode = false;
            txtMaSP.setEditable(false);
            txtDG.setEditable(false);
            txtSL.setEditable(false);
            txtMaKe.setEditable(false);
            cbDVT.setEnabled(false);
            showFormPanel();
            cbTenSP.setEnabled(true);
            if(cbTenSP.getItemCount() > 0) cbTenSP.setSelectedIndex(-1);
        });
        cbTenSP.addActionListener(e -> {
            if(cbTenSP.getSelectedIndex()==-1){
                return;
            }
            String dachon=cbTenSP.getSelectedItem().toString();
            SanPham_BUS spbus=new SanPham_BUS();
            for(SanPham sp:spbus.getAll()){
                if(sp.getTenSP().equalsIgnoreCase(dachon)){
                    txtMaSP.setText(sp.getMaSP());
                    cbDVT.setSelectedItem(sp.getDonViTinh());
                    txtDG.setText(String.format("%.0f", sp.getGiaTien()));
                    txtMaKe.setText(sp.getMaKe() != null ? sp.getMaKe() : "");
                    txtSL.requestFocus();
                    break;
                }
            }
        });

        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần sửa!");
                return;
            }

            lblFormTitle.setText("SỬA SẢN PHẨM TỒN KHO");
            txtMaSP.setText(model.getValueAt(row, 0).toString());
            cbTenSP.setSelectedItem(model.getValueAt(row, 1).toString());
            cbDVT.setSelectedItem(model.getValueAt(row, 2).toString());
            txtSL.setText(model.getValueAt(row, 3).toString());
            txtDG.setText(model.getValueAt(row, 4).toString().replace(",", "").replace(".", ""));
            txtCanhBao.setText(model.getValueAt(row, 5).toString());
            txtMaKe.setText(model.getValueAt(row, 6) != null ? model.getValueAt(row, 6).toString() : "");

            isEditMode = true;
            txtMaSP.setEditable(false);
            cbTenSP.setEnabled(false);
            txtSL.setEditable(false);
            cbDVT.setEnabled(false);
            txtDG.setEditable(false);
            showFormPanel();
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần xoá!");
                return;
            }
            String masp = table.getValueAt(row, 0).toString();
            String tensp = table.getValueAt(row, 1).toString();

            if (JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa: " + tensp + "?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                String tb = bus.deleteBaoCao(masp);
                JOptionPane.showMessageDialog(this, tb);
                if (tb.contains("thành công")) {
                    loadDataToTable();
                    hideFormPanel();
                }
            }
        });

        btnRefresh.addActionListener(e -> {
            bus.refreshData();
            loadDataToTable();
            hideFormPanel();
        });
        btnExcel.addActionListener(e -> {
            if (table.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Không có dữ liệu trong bảng để xuất Excel!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chọn vị trí lưu file Báo Cáo Tồn Kho");

            // Tự động tạo tên file có ngày giờ hiện tại cho chuyên nghiệp
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("ddMMyyyy_HHmmss");
            String defaultFileName = "TonKho_" + sdf.format(new java.util.Date()) + ".xlsx";
            fileChooser.setSelectedFile(new java.io.File(defaultFileName));

            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                java.io.File fileToSave = fileChooser.getSelectedFile();
                String filePath = fileToSave.getAbsolutePath();

                // Đảm bảo đuôi file luôn là .xlsx
                if (!filePath.endsWith(".xlsx")) {
                    filePath += ".xlsx";
                }

                try (org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
                     java.io.FileOutputStream out = new java.io.FileOutputStream(filePath)) {

                    org.apache.poi.xssf.usermodel.XSSFSheet sheet = workbook.createSheet("Tồn Kho Hiện Tại");

                    // --- 1. Tạo Style cho dòng Tiêu đề (In đậm, Căn giữa) ---
                    org.apache.poi.xssf.usermodel.XSSFCellStyle headerStyle = workbook.createCellStyle();
                    org.apache.poi.xssf.usermodel.XSSFFont font = workbook.createFont();
                    font.setBold(true);
                    headerStyle.setFont(font);
                    headerStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);

                    // --- 2. Ghi dòng Tiêu đề ---
                    org.apache.poi.xssf.usermodel.XSSFRow headerRow = sheet.createRow(0);
                    for (int i = 0; i < table.getColumnCount(); i++) {
                        org.apache.poi.xssf.usermodel.XSSFCell cell = headerRow.createCell(i);
                        cell.setCellValue(table.getColumnName(i));
                        cell.setCellStyle(headerStyle);
                    }

                    // --- 3. Ghi Dữ liệu từ bảng JTable ra Excel ---
                    for (int i = 0; i < table.getRowCount(); i++) {
                        org.apache.poi.xssf.usermodel.XSSFRow row = sheet.createRow(i + 1);
                        for (int j = 0; j < table.getColumnCount(); j++) {
                            Object value = table.getValueAt(i, j); // Lấy đúng dữ liệu đang hiển thị trên màn hình

                            if (value != null) {
                                // Xử lý thông minh: Ép các cột Số lượng (3), Đơn giá (4), Cảnh báo (5) về dạng SỐ
                                // để khi mở Excel lên nó có thể tính toán hàm SUM(), AVG()... được ngay.
                                if (j == 3 || j == 4 || j == 5) {
                                    String strNum = value.toString().replaceAll("[,\\s]", ""); // Xóa dấu phẩy ngàn
                                    try {
                                        row.createCell(j).setCellValue(Double.parseDouble(strNum));
                                    } catch (NumberFormatException ex) {
                                        row.createCell(j).setCellValue(value.toString()); // Nếu lỗi thì cứ in ra dạng chữ
                                    }
                                } else {
                                    row.createCell(j).setCellValue(value.toString()); // Các cột Mã, Tên thì in dạng chữ
                                }
                            } else {
                                row.createCell(j).setCellValue("");
                            }
                        }
                    }

                    // Tự động căn chỉnh độ rộng của các cột cho vừa khít với chữ
                    for (int i = 0; i < table.getColumnCount(); i++) {
                        sheet.autoSizeColumn(i);
                    }

                    // Xuất file và đóng luồng
                    workbook.write(out);
                    JOptionPane.showMessageDialog(this, "Xuất file Excel Tồn Kho thành công!\nĐã lưu tại: " + filePath, "Thành công", JOptionPane.INFORMATION_MESSAGE);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi xuất file Excel:\n" + ex.getMessage(), "Lỗi Xuất File", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }

        });
        //hàm tự động trừ //
        /*
        Timer demoTimer = new Timer(10000, e -> {
            boolean hasChanges = false;

            // Lướt qua từng dòng trên bảng (trong RAM)
            for (BaoCaoTonKho bc : bus.getAll()) {
                if (bc.getCanhBaoHH() > 0) {
                    bc.setCanhBaoHH(bc.getCanhBaoHH() - 1); // Trừ đi 1

                    // Gọi DAO để trừ luôn dưới Database cho chắc
                    // (Bạn cần tạo thêm 1 hàm updateCanhBao trong DAO/BUS cho nhẹ,
                    // hoặc dùng luôn hàm updateBaoCao hiện tại cũng được)
                    bus.updateBaoCao(bc);
                    hasChanges = true;
                }
            }

            // Nếu có dòng nào bị trừ, thì load lại cái bảng JTable để thầy cô thấy số nhảy
            if (hasChanges) {
                loadDataToTable();
                System.out.println("Đã tự động trừ 1 ngày cảnh báo (Demo Mode)!");
            }
        });

        // Bắt đầu đếm giờ
        demoTimer.start();

         */
    }


    private void luuDuLieu() {
        try {
            String ma = txtMaSP.getText().trim();
            if (cbTenSP.getSelectedIndex() == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn Tên Sản Phẩm từ danh sách!");
                return;
            }
            String ten = cbTenSP.getSelectedItem().toString();
            if (ma.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Mã sản phẩm đang trống!\nVui lòng gõ Tên sản phẩm và BẤM ENTER để hệ thống tự tìm Mã SP trước khi Lưu.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                cbTenSP.requestFocus(); // Đẩy chuột về lại ô Tên SP
                return; // Dừng lại, không chạy code bên dưới nữa
            }
            if ( ten.isEmpty()) { JOptionPane.showMessageDialog(this, "Mã và tên không được để trống!"); return; }
            String strSL = txtSL.getText().replaceAll("[,\\s]", "");

            int sl = Integer.parseInt(strSL);

            if (sl<0){
                JOptionPane.showMessageDialog(this, "Số lượng tồn kho không được là số âm!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                txtSL.requestFocus();
                txtSL.selectAll();
                return;
            }

            String strGia = txtDG.getText().replaceAll("[,\\s]", "");
            float gia = Float.parseFloat(strGia);
            String strCanhBao = txtCanhBao.getText().replaceAll("[,\\s]", "");
            int canhbao = Integer.parseInt(strCanhBao);

            if (canhbao < 0) {

                JOptionPane.showMessageDialog(this, "Số ngày cảnh báo không được là số âm!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                txtCanhBao.requestFocus();
                txtCanhBao.selectAll();
                return;
            }

            SanPham sp = new SanPham(); sp.setMaSP(ma); sp.setTenSP(ten); sp.setDonViTinh(cbDVT.getSelectedItem().toString()); sp.setGiaTien(gia); sp.setMaKe(txtMaKe.getText().trim());
            BaoCaoTonKho bc = new BaoCaoTonKho(); bc.setMaBC(ma); bc.setsLTon(sl); bc.setCanhBaoHH(canhbao); bc.setSanPham(sp);



            String kq;
            if (isEditMode){
                kq=bus.updateBaoCao(bc);
            }else {
                kq = bus.addBaoCao(bc);
            }

            JOptionPane.showMessageDialog(this, kq);

            if (kq.contains("thành công")) { loadDataToTable(); hideFormPanel(); }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Số lượng, Đơn giá và Cảnh báo phải là số hợp lệ!");
        }
    }

    public void loadDataToTable(){
        model.setRowCount(0);
        for(BaoCaoTonKho tk: bus.getAll()){
            model.addRow(new Object[]{ tk.getSanPham().getMaSP(), tk.getSanPham().getTenSP(), tk.getSanPham().getDonViTinh(), tk.getsLTon(), String.format("%,.0f", tk.getSanPham().getGiaTien()), tk.getCanhBaoHH(), tk.getSanPham().getMaKe() });
        }
    }
    public void loadComboboxTenSP(){
        cbTenSP.removeAllItems();
        SanPham_BUS spbus=new SanPham_BUS();
        for(SanPham sp:spbus.getAll()){
            cbTenSP.addItem(sp.getTenSP());

        }
        cbTenSP.setSelectedIndex(-1);
    }

    private void clearForm() {
        txtMaSP.setText(""); txtSL.setText("0"); txtDG.setText("0"); txtCanhBao.setText("10"); txtMaKe.setText("");
        cbDVT.setSelectedIndex(0);
        if (cbTenSP.getItemCount() > 0) cbTenSP.setSelectedIndex(-1); // Thêm dòng này
    }

    private void showFormPanel() {
        panelForm.setVisible(true);
        splitPane.setDividerLocation(getWidth() - 320);
    }

    private void hideFormPanel() {
        panelForm.setVisible(false);
    }

    private void kiemTraVaTruNgayCanhBao() {
        try {

            java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userNodeForPackage(TrangTonKho.class);

            // Lấy ngày hôm nay
            java.time.LocalDate today = java.time.LocalDate.now();

            // Lấy "Ngày mở app lần cuối" (Nếu app mở lần đầu tiên, mặc định là ngày hôm nay)
            String lastRunStr = prefs.get("LAST_RUN_DATE", today.toString());
            java.time.LocalDate lastRun = java.time.LocalDate.parse(lastRunStr);

            // Tính xem app đã bị tắt bao nhiêu ngày
            long soNgayBiHut = java.time.temporal.ChronoUnit.DAYS.between(lastRun, today);

            if (soNgayBiHut > 0) {
                boolean hasChanges = false;

                // Quét toàn bộ kho, trừ BÙ số ngày bị hụt cho tất cả sản phẩm
                for (BaoCaoTonKho bc : bus.getAll()) {
                    if (bc.getCanhBaoHH() > 0) {
                        int ngayMoi = (int) (bc.getCanhBaoHH() - soNgayBiHut);
                        // Không cho phép số ngày âm, bét nhất là 0 (Đã hết hạn)
                        bc.setCanhBaoHH(Math.max(ngayMoi, 0));

                        bus.updateBaoCao(bc); // Lưu xuống DB
                        hasChanges = true;
                    }
                }

                // Cập nhật lại giao diện nếu có thay đổi
                if (hasChanges) {
                    loadDataToTable();
                    System.out.println("Đã tự động trừ bù " + soNgayBiHut + " ngày trong lúc tắt app!");
                }

                // Cập nhật lại "Ngày mở app lần cuối" thành hôm nay để ngày mai tính tiếp
                prefs.put("LAST_RUN_DATE", today.toString());
            }

            // BẬT TIMER VÔ CỰC (Xử lý trường hợp người dùng treo máy qua đêm không tắt app)
            // Cứ mỗi 1 tiếng (3.600.000 ms) nó sẽ quét 1 lần xem đã sang ngày mới chưa
            Timer midnightTimer = new Timer(3600000, e -> {
                java.time.LocalDate currentDay = java.time.LocalDate.now();
                java.time.LocalDate savedDay = java.time.LocalDate.parse(prefs.get("LAST_RUN_DATE", currentDay.toString()));

                // Nếu đang treo app mà đồng hồ điểm qua 12h đêm -> Trừ đi 1
                if (currentDay.isAfter(savedDay)) {
                    for (BaoCaoTonKho bc : bus.getAll()) {
                        if (bc.getCanhBaoHH() > 0) {
                            bc.setCanhBaoHH(bc.getCanhBaoHH() - 1);
                            bus.updateBaoCao(bc);
                        }
                    }
                    loadDataToTable();
                    prefs.put("LAST_RUN_DATE", currentDay.toString());
                    System.out.println("Vừa sang ngày mới, đã tự động trừ 1 ngày!");
                }
            });
            midnightTimer.start(); // Chạy vô cực

        } catch (Exception e) {
            System.err.println("Lỗi tính ngày tự động: " + e.getMessage());
        }
    }
    // -----------------------------------------------------------------
    public static void main(String[]  args){
        SwingUtilities.invokeLater(()->
        {
            JFrame frame =new JFrame(" Ton kho");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            frame.add(new TrangTonKho());
            frame.setSize(1000, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

    }

    @Override
    public void apDungQuyen(boolean coQuyen_Xem, boolean coQuyen_Them,
                            boolean coQuyen_Sua, boolean coQuyen_Xoa) {
        btnAdd.setVisible(coQuyen_Them);
        btnEdit.setVisible(coQuyen_Sua);
        btnDelete.setVisible(coQuyen_Xoa);
    }
}







//
 /*
 //
    // --- HÀM TÌM KIẾM ĐÃ ĐƯỢC TỐI ƯU BẰNG CÁCH GỌI BUS ---
    private void timKiemTheoMa(String tuKhoa) {
        // 1. Nhờ BUS đi tìm giùm danh sách kết quả
        java.util.List<BaoCaoTonKho> ketQua = bus.findBySku(tuKhoa);

        // 2. Nếu danh sách rỗng -> Báo lỗi và load lại bảng gốc
        if (ketQua.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy sản phẩm nào có mã chứa: " + tuKhoa, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            loadDataToTable();
            return;
        }

        // 3. Nếu có kết quả -> Xóa bảng cũ và đổ kết quả mới lên
        model.setRowCount(0);
        for (BaoCaoTonKho tk : ketQua) {
            model.addRow(new Object[]{
                tk.getSanPham().getMaSP(),
                tk.getSanPham().getTenSP(),
                tk.getSanPham().getDonViTinh(),
                tk.getsLTon(),
                String.format("%,.0f", tk.getSanPham().getGiaTien()),
                tk.getCanhBaoHH(),
                tk.getSanPham().getMaKe()
            });
        }
    }
  */















//Bảng san_pham   đang làm "Cha" của rất nhiều bảng khác
//các bảng có thể gây ra lỗi
//bao_cao_ton_kho
//
//chitiet_phieu_nhap
//
//chitiet_phieu_xuat

//hướng giải quyết ( tạm ẩn hoặc "ngừng kinh doanh")


//*khi thêm sản phẩm mới vào( đã thấy xuất hiện )nhưng chưa ai order -> chưa có trong hoá đơn(ct phiếu nhập / xuất) chưa có khoá ngoại
// có thể xoá đơn giản đc