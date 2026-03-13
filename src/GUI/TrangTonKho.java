    package GUI;

    import UI.TonKho_GUI;
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
    import javax.swing.table.DefaultTableModel;
    import javax.swing.table.JTableHeader;
    import javax.swing.table.TableRowSorter;
    import java.awt.*;
    import java.awt.event.ActionEvent;
    import java.awt.event.ActionListener;
    import java.awt.image.BandCombineOp;
    import java.util.ArrayList;

    public class TrangTonKho extends JPanel {
        private JTable table;
        private DefaultTableModel model;
        private BaoCaoTonKho_BUS bus;
        private TableRowSorter<DefaultTableModel> rowSorter;
        public TrangTonKho() {
            bus= new BaoCaoTonKho_BUS();
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);

            JPanel pnlCompactInputs = new JPanel(new GridLayout(2, 1, 0, 5));
            pnlCompactInputs.setOpaque(false);


            JPanel pnlWarn = createCompactInput("Cảnh báo (ngày):", new Color(255, 255, 204), Color.BLACK);

            JPanel pnlExp = createCompactInput("Sắp hết hạn (SP):", new Color(255, 102, 0), Color.WHITE);

            pnlCompactInputs.add(pnlWarn);
            pnlCompactInputs.add(pnlExp);


            add(taoThanhCongCu(), BorderLayout.NORTH);



            String[] columnNames = {"Mã SP", "Tên SP", "Đơn vị tính", "Số lượng", "Đơn giá", "Số ngày hết hạn", "Mã kệ"};



             model=new DefaultTableModel(columnNames,0){
                @Override
                public boolean isCellEditable(int row,int column){
                    return false;
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

            JScrollPane scrollPane = new JScrollPane(table);
            add(scrollPane, BorderLayout.CENTER);

            loadDataToTable();
        }
        public void loadDataToTable(){
            model.setRowCount(0);
            ArrayList<BaoCaoTonKho> danhsach=bus.getAll();
            for(BaoCaoTonKho tk: danhsach){
                Object[] row={
                        tk.getSanPham().getMaSP(),
                        tk.getSanPham().getTenSP(),
                        tk.getSanPham().getDonViTinh(),
                        tk.getsLTon(),
                        tk.getSanPham().getGiaTien(),
                        tk.getCanhBaoHH(),

                        tk.getSanPham().getMaKe(),
                };
                model.addRow(row);
            }
        }


        private JPanel createCompactInput(String labelText, Color bgColor, Color textColor) {
            JPanel pnl = new JPanel(new BorderLayout(5, 0));
            pnl.setBackground(Color.WHITE);

            JLabel lbl = new JLabel(labelText);
            lbl.setFont(new Font("Arial", Font.BOLD, 11));

            JTextField txt = new JTextField();
            txt.setBackground(bgColor);
            txt.setForeground(textColor);
            txt.setCaretColor(textColor);
            txt.setFont(new Font("Arial", Font.BOLD, 12));
            // Tạo viền mỏng và padding cho ô nhập
            txt.setBorder(new CompoundBorder(
                    new LineBorder(Color.LIGHT_GRAY, 1),
                    new EmptyBorder(2, 5, 2, 5)
            ));
            txt.setPreferredSize(new Dimension(80, 25));

            pnl.add(lbl, BorderLayout.WEST);
            pnl.add(txt, BorderLayout.CENTER);
            return pnl;
        }

        private JPanel taoThanhCongCu() {
            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.setBackground(Color.WHITE);
            wrapper.setBorder(new EmptyBorder(15,20,5,20));

            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT,12,8));
            panel.setBackground(Color.WHITE);
            panel.setBorder(new EmptyBorder(4,10,4,10));


            // Thanh tìm kiếm
            JTextField txtSearch = new JTextField("Tìm kiếm");
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
                    if (txtSearch.getText().equals("Tìm kiếm")) {
                        txtSearch.setText("");           // Xóa chữ "Tìm kiếm"
                        txtSearch.setForeground(Color.BLACK); // Đổi màu chữ sang đen để người dùng nhập
                    }
                }

                @Override
                public void focusLost(java.awt.event.FocusEvent e) {
                    // Khi người dùng click ra chỗ khác mà không nhập gì
                    if (txtSearch.getText().isEmpty()) {
                        txtSearch.setForeground(Color.GRAY);
                        txtSearch.setText("Tìm kiếm");    // Hiện lại chữ gợi ý
                    }
                }
            });
            txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
                @Override
                        public void insertUpdate(javax.swing.event.DocumentEvent e){
                    thuchientimkiem();
                }
                @Override
                        public void removeUpdate(javax.swing.event.DocumentEvent e){
                    thuchientimkiem();
                }
                @Override
                        public void changedUpdate(javax.swing.event.DocumentEvent e){
                    thuchientimkiem();
                }
                private void thuchientimkiem(){
                    String text=txtSearch.getText();
                    if(text.trim().length()==0||text.equals("Tìm kiếm")){
                        rowSorter.setRowFilter(null);
                    }
                    else{
                        rowSorter.setRowFilter(RowFilter.regexFilter("(?i)"+text));
                    }
                }
            });


            // Nút làm mới
            JButton btnLamMoi = new JButton("⟳ Làm mới");
            Style.styleButton(btnLamMoi);
            btnLamMoi.addActionListener(e->{
                bus.refreshData();
                loadDataToTable();
                JOptionPane.showMessageDialog(this,"Đã làm mới");
            });


            // Combobox Lọc
            String[] itemLoc = {"Lọc", "A-Z","Z-A"};
            JComboBox<String> comboBoxLoc = new JComboBox<>(itemLoc);

            // Style cơ bản
            comboBoxLoc.setBackground(new Color(214, 238, 253));
            comboBoxLoc.setPreferredSize(new Dimension(90, 30));
            comboBoxLoc.setFont(new Font("Segoe UI", Font.BOLD, 13));
            comboBoxLoc.setCursor(new Cursor(Cursor.HAND_CURSOR));
            comboBoxLoc.setSelectedIndex(0);

            // Placeholder "Lọc"
            comboBoxLoc.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(
                        JList<?> list, Object value, int index,
                        boolean isSelected, boolean cellHasFocus) {

                    JLabel lbl = (JLabel) super.getListCellRendererComponent(
                            list, value, index, isSelected, cellHasFocus);

                    lbl.setHorizontalAlignment(SwingConstants.CENTER);

                    if (index == -1 && comboBoxLoc.getSelectedIndex() == -1) {
                        lbl.setText("Lọc");
                        lbl.setForeground(Color.GRAY);
                    }
                    return lbl;
                }
            });
        comboBoxLoc.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                java.util.List<RowSorter.SortKey> sortKeys=new ArrayList<>();
                int luachon=comboBoxLoc.getSelectedIndex();
                if(luachon==1){
                    sortKeys.add(new RowSorter.SortKey(1,SortOrder.ASCENDING));
                }
                else if(luachon==2){
                    sortKeys.add(new RowSorter.SortKey(1,SortOrder.DESCENDING));
                }
                else {
                    sortKeys.add(new RowSorter.SortKey(0,SortOrder.ASCENDING));
                }

                rowSorter.setSortKeys(sortKeys);
                rowSorter.sort();
            }
        });


            JButton btnEdit = new JButton("Chỉnh sửa");
            Style.styleButton(btnEdit);
            JButton btnDelete = new JButton("Xóa");
            btnDelete.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    xulyxoa();
                }
            });

            Style.styleButton(btnDelete);
            JButton btnAdd = new JButton("+ Thêm");
            Style.styleButton(btnAdd);
            btnAdd.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    xulythem();
                }
            });
            JButton btnExcel = new JButton("Xuất excel");
            Style.styleButton(btnExcel);
            btnExcel.addActionListener(e -> {
                if (table.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this, "Không có dữ liệu trong kho để xuất Excel!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Chọn vị trí lưu file Báo Cáo Tồn Kho");

                // Đặt tên file mặc định có chứa ngày tháng hiện tại cho chuyên nghiệp
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("ddMMyyyy_HHmm");
                String defaultFileName = "BaoCaoTonKho_" + sdf.format(new java.util.Date()) + ".xlsx";
                fileChooser.setSelectedFile(new java.io.File(defaultFileName));

                int userSelection = fileChooser.showSaveDialog(this);
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    java.io.File fileToSave = fileChooser.getSelectedFile();
                    String filePath = fileToSave.getAbsolutePath();

                    // Đảm bảo file luôn có đuôi .xlsx
                    if (!filePath.endsWith(".xlsx")) {
                        filePath += ".xlsx";
                    }

                    try (java.io.FileOutputStream out = new java.io.FileOutputStream(filePath);
                         org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {

                        org.apache.poi.xssf.usermodel.XSSFSheet sheet = workbook.createSheet("Tồn Kho Hiện Tại");

                        // 1. Tạo dòng Tiêu đề (Header)
                        org.apache.poi.xssf.usermodel.XSSFRow headerRow = sheet.createRow(0);
                        // Tạo font in đậm cho Header
                        org.apache.poi.xssf.usermodel.XSSFCellStyle headerStyle = workbook.createCellStyle();
                        org.apache.poi.xssf.usermodel.XSSFFont font = workbook.createFont();
                        font.setBold(true);
                        headerStyle.setFont(font);

                        for (int i = 0; i < table.getColumnCount(); i++) {
                            org.apache.poi.xssf.usermodel.XSSFCell cell = headerRow.createCell(i);
                            cell.setCellValue(table.getColumnName(i));
                            cell.setCellStyle(headerStyle);
                        }

                        // 2. Chép toàn bộ Dữ liệu từ bảng (JTable) ra file
                        for (int i = 0; i < table.getRowCount(); i++) {
                            org.apache.poi.xssf.usermodel.XSSFRow row = sheet.createRow(i + 1);
                            for (int j = 0; j < table.getColumnCount(); j++) {
                                Object value = table.getValueAt(i, j);
                                if (value != null) {
                                    // Nếu là cột số lượng (Cột 3) hoặc giá (Cột 4), có thể ép kiểu số để Excel hiểu
                                    String strValue = value.toString().replace(",", "").replace(".", ""); // Bỏ dấu phẩy/chấm ngàn nếu có
                                    try {
                                        if (j == 3 || j == 4 || j == 5) {
                                            row.createCell(j).setCellValue(Double.parseDouble(strValue));
                                        } else {
                                            row.createCell(j).setCellValue(value.toString());
                                        }
                                    } catch (NumberFormatException nfe) {
                                        row.createCell(j).setCellValue(value.toString());
                                    }
                                } else {
                                    row.createCell(j).setCellValue("");
                                }
                            }
                        }

                        // Tự động căn chỉnh độ rộng cột cho đẹp
                        for (int i = 0; i < table.getColumnCount(); i++) {
                            sheet.autoSizeColumn(i);
                        }

                        // Xuất file
                        workbook.write(out);
                        JOptionPane.showMessageDialog(this, "Xuất file Excel báo cáo Tồn Kho thành công!\nĐã lưu tại: " + filePath, "Thành công", JOptionPane.INFORMATION_MESSAGE);

                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi xuất file Excel:\n" + ex.getMessage(), "Lỗi Xuất File", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                }
            });

            btnAdd.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                xulythem();
                }
            });

            btnEdit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    xulysua();
                }
            });
            // Thêm vào panel
            panel.add(pnlSearchInput);
            panel.add(btnLamMoi);
            panel.add(comboBoxLoc);
            panel.add(btnEdit);
            panel.add(btnDelete);
            panel.add(btnAdd);
            panel.add(btnExcel);

            wrapper.add(panel, BorderLayout.CENTER);
            return wrapper;
        }
        public void xulysua() {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần chỉnh sửa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }


            Object objMa = table.getValueAt(row, 0);
            Object objTen = table.getValueAt(row, 1);

            String maSP = (objMa != null) ? objMa.toString() : "";
            String tenSP = (objTen != null) ? objTen.toString() : "";


            String[] cacMucCanSua = {"Tên sản phẩm", "Đơn vị tính", "Số lượng", "Đơn giá", "Hủy bỏ"};


            int luaChon = JOptionPane.showOptionDialog(this,
                    "Bạn muốn chỉnh sửa thông tin gì của sản phẩm:\n" + maSP + " - " + tenSP,
                    "Chọn thông tin chỉnh sửa",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    cacMucCanSua,
                    cacMucCanSua[0]);


            switch (luaChon) {
                case 0: // Sửa Tên sản phẩm
                    Object objTenHT = table.getValueAt(row, 1);
                    String tenHienTai = (objTenHT != null) ? objTenHT.toString() : "";
                    String tenMoi = JOptionPane.showInputDialog(this, "Nhập tên sản phẩm mới:", tenHienTai);
                    if (tenMoi != null && !tenMoi.trim().isEmpty()) {


                        table.setValueAt(tenMoi, row, 1);
                        JOptionPane.showMessageDialog(this, "Đã cập nhật tên thành công!");
                    }
                    break;

                case 1: // Sửa Đơn vị tính
                    Object objDvtHT = table.getValueAt(row, 2);
                    String dvtHienTai = (objDvtHT != null) ? objDvtHT.toString() : "";
                    String dvtMoi = JOptionPane.showInputDialog(this, "Nhập đơn vị tính mới:", dvtHienTai);
                    if (dvtMoi != null && !dvtMoi.trim().isEmpty()) {

                        table.setValueAt(dvtMoi, row, 2);
                    }
                    break;

                case 2: // Sửa Số lượng
                    Object objSlHT = table.getValueAt(row, 3);
                    String slHienTai = (objSlHT != null) ? objSlHT.toString() : "0"; // Gán mặc định là 0 nếu rỗng
                    String slMoiStr = JOptionPane.showInputDialog(this, "Nhập số lượng:", slHienTai);
                    if (slMoiStr != null && !slMoiStr.trim().isEmpty()) {
                        try {
                            int slMoi = Integer.parseInt(slMoiStr);

                            table.setValueAt(slMoi, row, 3);
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(this, "Số lượng phải là số nguyên hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    break;

                case 3: // Sửa Đơn giá
                    Object objGiaHT = table.getValueAt(row, 4);
                    String giaHienTai = (objGiaHT != null) ? objGiaHT.toString() : "0";
                    String giaMoiStr = JOptionPane.showInputDialog(this, "Nhập đơn giá mới:", giaHienTai);
                    if (giaMoiStr != null && !giaMoiStr.trim().isEmpty()) {

                        table.setValueAt(giaMoiStr, row, 4);
                    }
                    break;

                default:

                    break;
            }
        }
        public void xulythem(){
            JTextField txtMaSP=new JTextField();
            JTextField txtTenSP=new JTextField();
            JComboBox<String> DVTs=new JComboBox<>(new String[]{"Lon","Chai","Thùng"});
            JTextField txtSL=new JTextField("0");
            JTextField txtDG=new JTextField("0");
            JTextField txtCanhBao=new JTextField("10");
            JTextField txtMake=new JTextField();
            Object[] formMessage = {
                    "Mã Sản Phẩm (VD: SP05):", txtMaSP,
                    "Tên Sản Phẩm:", txtTenSP,
                    "Đơn Vị Tính:", DVTs,
                    "Số Lượng Khởi Tạo:", txtSL,
                    "Đơn Giá (VNĐ):", txtDG,
                    "Mức Cảnh Báo Sắp Hết Hàng:", txtCanhBao,
                    "Mã Kệ (Ví dụ: K01):", txtMake
            };
            int option = JOptionPane.showConfirmDialog(this, formMessage, "Thêm Sản Phẩm Mới", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (option==JOptionPane.OK_OPTION){
                try{
                    String ma=txtMaSP.getText().trim();
                    String ten=txtTenSP.getText().trim();
                    String dvt=DVTs.getSelectedItem().toString();
                    String make=txtMake.getText().trim();

                    int sl=Integer.parseInt(txtSL.getText().trim());
                    float gia=Float.parseFloat(txtDG.getText().trim());
                    int canhbao=Integer.parseInt(txtCanhBao.getText().trim());
                    if(ma.isEmpty()||ten.isEmpty()){
                        JOptionPane.showMessageDialog(this,"Mã và tên không được để trống");
                        return;
                    }
                    SanPham sp=new SanPham();
                    sp.setMaSP(ma);
                    sp.setTenSP(ten);
                    sp.setDonViTinh(dvt);
                    sp.setGiaTien(gia);
                    sp.setMaKe(make);

                    BaoCaoTonKho bc=new BaoCaoTonKho();
                    bc.setMaBC(ma);
                    bc.setsLTon(sl);
                    bc.setCanhBaoHH(canhbao);
                    bc.setSanPham(sp);

                    String ketqua=bus.addBaoCao(bc);
                    JOptionPane.showMessageDialog(this,ketqua);

                    if (ketqua.contains("thành công")) {
                        loadDataToTable();
                    }

                }
                catch (NumberFormatException ex){
                    JOptionPane.showMessageDialog(this, "Số lượng, Đơn giá và Cảnh báo phải là số hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                }
            }

        }
        public void xulyxoa(){
            int row =table.getSelectedRow();
            if(row==-1){
                JOptionPane.showMessageDialog(this,"Vui lòng chọn sản phẩm cần xoá");
                return;
            }
            Object objma=table.getValueAt(row,0);
            Object objten=table.getValueAt(row,1);
            String masp=(objma!=null)?objma.toString():"";
            String tensp=(objten!=null)?objten.toString():"";
            int xacNhan = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn xóa sản phẩm:\n" + masp + " - " + tensp + " không?",
                    "Xác nhận xóa",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if(xacNhan==JOptionPane.YES_OPTION){
                String thongbao=bus.deleteBaoCao(masp);
                if(thongbao.contains("thành công")){
                    JOptionPane.showMessageDialog(this,thongbao);
                    loadDataToTable();
                }
                else{
                    JOptionPane.showMessageDialog(this,thongbao,"Lỗi",JOptionPane.ERROR_MESSAGE);
                }
            }
        }
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
    }