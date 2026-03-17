package GUI;

import BUS.KhachHang_BUS;
import DAO.KhachHangDAO;
import Model.KhachHang;
//import org.apache.poi.xwpf.usermodel.TableRowHeightRule;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class TrangKhachHang extends JPanel implements QuyenTrang {
    private JTable table;
    private DefaultTableModel model;
    private KhachHang_BUS khBUS = new KhachHang_BUS();
    //private KhachHangDAO khDAO=new KhachHangDAO();
    private DecimalFormat dfVND = new DecimalFormat("#,### VNĐ");
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private  JComboBox<String> comboBoxLoc;

    public TrangKhachHang() {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 255, 255));

        // 1. Khởi tạo Model và Table trước (để lấy dữ liệu gốc cho Sorter)
        String[] columns = {"STT", "Mã KH", "Tên KH", "SĐT", "Địa chỉ", "Chi tiêu"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        rowSorter = new TableRowSorter<>(model);
        table.setRowSorter(rowSorter);


        add(taoThanhCongCu(), BorderLayout.NORTH);
        add(taoNoiDung(), BorderLayout.CENTER);

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
        String place ="Tìm kiếm (VD:KH01)";
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
                            if (rowSorter != null) rowSorter.setRowFilter(null);
                        } else {
                            if (rowSorter != null)
                                rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1));
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
//        btnLamMoi.setPreferredSize(new Dimension(120,35));
//        btnLamMoi.setMinimumSize(new Dimension(120,35));
        btnLamMoi.addActionListener(e->{
            txtSearch.setText(place);
            txtSearch.setForeground(Color.GRAY);
            if(rowSorter !=null) rowSorter.setRowFilter(null);

            fillToTable();// cập nhật lại bảng
            this.revalidate();
            this.repaint();
            JOptionPane.showMessageDialog(this,"Dữ liệu được cập nhật thành công!");
        });
        Style.styleButton(btnLamMoi);


        // Combobox Lọc
        String[] itemLoc = {"Lọc","1-N", "A-Z","Z-A"};
         comboBoxLoc = new JComboBox<>(itemLoc);

        // 1. Chỉ khai báo style 1 lần duy nhất
        comboBoxLoc.setBackground(new Color(214, 238, 253));
        comboBoxLoc.setPreferredSize(new Dimension(90, 30));
        comboBoxLoc.setFont(new Font("Segoe UI", Font.BOLD, 13));
        comboBoxLoc.setCursor(new Cursor(Cursor.HAND_CURSOR));

// 2. Renderer để căn giữa chữ và hiện placeholder
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
                if (rowSorter == null) return; // Kiểm tra tránh lỗi NullPointer

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

                rowSorter.setSortKeys(sortKeys);
                rowSorter.sort();
                reIndex();
            }
        });

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


        // Các nút khác
        btnEdit   = new JButton("Chỉnh sửa");
        Style.styleButton(btnEdit);
        btnDelete = new JButton("Xóa");
        Style.styleButton(btnDelete);
        btnAdd    = new JButton("+ Thêm");
        Style.styleButton(btnAdd);

        JButton btnExcel = new JButton("Xuất excel");
        btnExcel.addActionListener(e->xuatExcel());
        Style.styleButton(btnExcel);


        // Thêm vào panel
        panel.add(pnlSearchInput);
        panel.add(btnLamMoi);
        panel.add(comboBoxLoc);
        panel.add(btnEdit);
        panel.add(btnDelete);
        panel.add(btnAdd);
        panel.add(btnExcel);


        btnAdd.addActionListener(e -> {
            new FormKhachHang(this, "THEM", null).setVisible(true);
        });
        btnDelete.addActionListener(e->{
            int row = table.getSelectedRow();
            if(row==-1){
                JOptionPane.showMessageDialog(this,"Vui lòng chọn khách hàng cần xoá từ bảng!");
                return;
            }
            int modelRow= table.convertRowIndexToModel(row);
            String maKH=table.getValueAt(modelRow,1).toString();
            //String tenKH=table.getValueAt(row,2).toString();

            //hộp thoại để tránh bấm nhầm
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc chắn muốn xoá khách hàng"+maKH+"?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if(choice== JOptionPane.YES_OPTION){
                String result = khBUS.deleteKH(maKH);

                if(result.contains("Thành công!")){
                    JOptionPane.showMessageDialog(this,result,"Thông báo",JOptionPane.INFORMATION_MESSAGE);
                    fillToTable();
                }else{
                    JOptionPane.showMessageDialog(this,"Lỗi"+result);
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
            KhachHang kh = khBUS.getListKH().get(row);
            new FormKhachHang(this, "SUA", kh).setVisible(true);
        });
        wrapper.add(panel, BorderLayout.CENTER);
        return wrapper;


    }

    private JPanel taoNoiDung() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 25, 20, 25));

        panel.add(taoTieuDe(), BorderLayout.NORTH);
        panel.add(taoBang(), BorderLayout.CENTER);

        rowSorter = new TableRowSorter<>(model);
        table.setRowSorter(rowSorter);
        return panel;
    }

    private JPanel taoTieuDe() {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnl.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("DANH SÁCH KHÁCH HÀNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.BLACK);

        pnl.add(lblTitle);
        return pnl;
    }

    private JScrollPane taoBang() {
        String[] columns = {"STT","Mã KH","Tên KH","SĐT","Địa chỉ","Chi tiêu"};

        model = new DefaultTableModel(columns,0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };

        table = new JTable(model);

        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(210,230,255));
        table.getTableHeader().setReorderingAllowed(false);

        rowSorter= new TableRowSorter<>(model);
        table.setRowSorter(rowSorter);
        // Căn giữa toàn bộ
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(new Color(180,180,180)));

        return scrollPane;
    }
    private void xuatExcel() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Chọn nơi lưu file");
        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getPath();
            if (!path.endsWith(".xlsx")) path += ".xlsx";

            try (org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
                org.apache.poi.xssf.usermodel.XSSFSheet sheet = workbook.createSheet("Danh sách Khách hàng");

                // Tạo tiêu đề cột (Header)
                org.apache.poi.xssf.usermodel.XSSFRow headerRow = sheet.createRow(0);
                for (int i = 0; i < table.getColumnCount(); i++) {
                    headerRow.createCell(i).setCellValue(table.getColumnName(i));
                }

                // Đổ dữ liệu từ JTable vào Excel
                for (int i = 0; i < table.getRowCount(); i++) {
                    org.apache.poi.xssf.usermodel.XSSFRow row = sheet.createRow(i + 1);
                    for (int j = 0; j < table.getColumnCount(); j++) {
                        Object value = table.getValueAt(i, j);
                        row.createCell(j).setCellValue(value != null ? value.toString() : "");
                    }
                }

                // Tự động căn chỉnh độ rộng cột
                for (int i = 0; i < table.getColumnCount(); i++) sheet.autoSizeColumn(i);

                try (java.io.FileOutputStream out = new java.io.FileOutputStream(path)) {
                    workbook.write(out);
                }
                JOptionPane.showMessageDialog(this, "Xuất Excel thành công!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi xuất Excel: " + e.getMessage());
            }
        }
    }
    public void reIndex(){
        for(int i=0;i<table.getRowCount();i++)
            table.setValueAt(i+1,i,0);
    }
    public void fillToTable(){
        if(table.isEditing()){
            table.getCellEditor().stopCellEditing();
        }
        khBUS.DongBoCT();
        model.setRowCount(0);
        khBUS.refeshData();
        ArrayList<KhachHang> list = khBUS.getListKH();
        int stt = 1;

        for(KhachHang kh : list){

            String strChiTieu = kh.getCT();


            double soChiTieu = 0;
            try {
                if (strChiTieu != null && !strChiTieu.isEmpty()) {
                    soChiTieu = Double.parseDouble(strChiTieu);
                }
            } catch (NumberFormatException e) {
                soChiTieu = 0; // Nếu chuỗi lỗi, mặc định là 0
            }

            Object[] row = {
                    stt++,
                    kh.getMaKH(),
                    kh.getHoTenKH(),
                    kh.getSdt(),
                    kh.getDiaChi(),
                    dfVND.format(soChiTieu) // Bây giờ truyền số vào sẽ không còn lỗi
            };
            model.addRow(row);
        }
        if(rowSorter!=null)
             rowSorter.setSortKeys(null);
        comboBoxLoc.setSelectedIndex(0);
    }
    public KhachHang_BUS get_khBUS(){
        return this.khBUS;
    }
    @Override
    public void apDungQuyen(boolean coQuyen_Xem, boolean coQuyen_Them,
                            boolean coQuyen_Sua, boolean coQuyen_Xoa) {
        btnAdd.setVisible(coQuyen_Them);
        btnEdit.setVisible(coQuyen_Sua);
        btnDelete.setVisible(coQuyen_Xoa);
    }

}