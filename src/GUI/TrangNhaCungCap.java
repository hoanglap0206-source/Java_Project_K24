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
        String[] itemLoc = {"Lọc","1-N","N-1"};
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

                RowSorter.setSortKeys(sortKeys);
                RowSorter.sort();
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
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Chọn nơi lưu file");
        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getPath();
            if (!path.endsWith(".xlsx")) path += ".xlsx";

            try (org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
                org.apache.poi.xssf.usermodel.XSSFSheet sheet = workbook.createSheet("Danh Sách Nhà Cung Cấp");

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