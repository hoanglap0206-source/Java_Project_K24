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

    public TrangNhaCungCap() {
        setLayout(new BorderLayout());
        setBackground(new Color(255,255,255));

        table=new JTable(model);

        RowSorter=new TableRowSorter<>(model);
        table.setRowSorter(RowSorter);

        add(taoNoiDung(), BorderLayout.CENTER);
        add(taoThanhCongCu(), BorderLayout.NORTH);
        fillToTable();
    }

    private JPanel taoThanhCongCu() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(new EmptyBorder(15,20,5,20));

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT,12,8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(4,10,4,10));


        // Thanh tìm kiếm
        String place="Tìm kiếm (VD:NCC01)";
        JTextField txtSearch = new JTextField(place);
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
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
            private void  filter(){
                SwingUtilities.invokeLater(()->{
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
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e){filter();}
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e){filter();}
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e){filter();}
        });


        // Nút làm mới
        JButton btnLamMoi = new JButton("↻ Làm mới");
        btnLamMoi.addActionListener(e->{
            txtSearch.setText(place);
            txtSearch.setForeground(Color.GRAY);
            if(RowSorter!=null)  RowSorter.setRowFilter(null);

            fillToTable();

            nccBUS.refeshData();
            this.revalidate();
            this.repaint();
            JOptionPane.showMessageDialog(this,"Dữ Liệu được cập thành công!");
        });
        Style.styleButton(btnLamMoi);


        // Combobox Lọc
        String[] itemLoc = {"Lọc","1-N","A-Z","Z-A"};
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
        btnEdit   = new JButton("Chỉnh sửa");
        Style.styleButton(btnEdit);
        btnDelete = new JButton("Xóa");
        Style.styleButton(btnDelete);
        btnAdd    = new JButton("+ Thêm");
        Style.styleButton(btnAdd);
        JButton btnExcel = new JButton("Xuất excel");
        Style.styleButton(btnExcel);
        btnExcel.addActionListener(e->xuatExcel());


        // Thêm vào panel
        panel.add(pnlSearchInput);
        panel.add(btnLamMoi);
        panel.add(comboBoxLoc);
        panel.add(btnEdit);
        panel.add(btnDelete);
        panel.add(btnAdd);
        panel.add(btnExcel);

        btnAdd.addActionListener(e -> {
            new FormNCC(this, "THEM", null).setVisible(true);
        });
        btnDelete.addActionListener(e->{
            int row = table.getSelectedRow();
            if(row==-1){
                JOptionPane.showMessageDialog(this,"Vui lòng chọn nhà cung cấp cần xoá từ bảng!");
                return;
            }
            int modelRow= table.convertRowIndexToModel(row);
            String maNCC=table.getValueAt(modelRow,1).toString();
            //String tenNCC=table.getValueAt(row,2).toString();

            //hộp thoại để tránh bấm nhầm
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc chắn muốn xoá nhà cung cấp"+maNCC+"?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if(choice== JOptionPane.YES_OPTION){
                String result = nccBUS.deleteNCC(maNCC);
                if(result.toLowerCase().contains("thành công!")){
                    JOptionPane.showMessageDialog(this,result);
                    fillToTable();

                }
                else {
                    JOptionPane.showMessageDialog(this, "Lỗi: " + result);
                }
            }

        });
        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng cần sửa!");
                return;
            }
            // Lấy dữ liệu dòng đang chọn từ table để truyền qua form
            int modelRow= table.convertRowIndexToModel(row);
            String ma =table.getModel().getValueAt(modelRow,1).toString();
            String ten=table.getModel().getValueAt(modelRow,2).toString();
            String sdt=table.getModel().getValueAt(modelRow,3).toString();
            String dc=table.getModel().getValueAt(modelRow,4).toString();

            NhaCungCap ncc=new NhaCungCap(ma,ten,sdt,dc);

            new FormNCC(this,"SUA",ncc).setVisible(true);
        });

        wrapper.add(panel, BorderLayout.CENTER);
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