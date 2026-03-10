package GUI;

import BUS.NCC_BUS;
import BUS.PhieuNhap_BUS;
import BUS.SanPham_BUS;
import Model.NhaCungCap;
import Model.NhanVien;
import Model.PhieuNhap;
import Model.SanPham;
import UI.GiaoDienChinh_Main;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TrangNhapKho extends JPanel {
    private PhieuNhap_BUS pnBus = new PhieuNhap_BUS();
    private SanPham_BUS spBus = new SanPham_BUS();
    private NCC_BUS nccBus = new NCC_BUS();
    private DefaultTableModel tableModel;
    private DefaultTableModel tableModelRight;
    private JTable table;
    private JTable tableRight;
    private JTextField txtSoLuong;
    private JLabel lblTongTien;
    private JTextField txtTongTien;
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private JTextField txtSearch;
    private JTextField txtSoLuongRight;
    private JButton btnXuat, btnXoa, btnSua,btnNhap;
    private boolean isEditMode = false;
    private Timer searchTimer;
    private JTextField txtNTP,txtID,txtNCC;
    private String maNV = ManHinhChinh.currentMaNV;
    public TrangNhapKho() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(createContent(), BorderLayout.CENTER);

        searchTimer = new Timer(500, e -> searchSP());
        searchTimer.setRepeats(false);

        txtSearchEvent();
    }

    private JPanel createContent() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 0));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        panel.add(createFormLeft());
        panel.add(createFormRight());

        return panel;
    }

    private JPanel createFormLeft() {
        JPanel panel = new JPanel(new BorderLayout(0,10));
        panel.setBackground(Color.WHITE);

        JPanel NorthPanel = new JPanel(new BorderLayout(0, 5));
        NorthPanel.setBackground(Color.WHITE);
        JLabel title = new JLabel("DANH SÁCH SẢN PHẨM TRONG KHO");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JPanel timKiemWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT,5,5));
        timKiemWrapper.setBackground(Color.WHITE);
        // Thanh tìm kiếm
        txtSearch = new JTextField("Tìm kiếm");
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

        // Nút làm mới
        JButton btnLamMoi = new JButton("⟳ Làm mới");
        Style.styleButton(btnLamMoi);

        btnSearchIcon.addActionListener(e -> {
            searchSP();
            txtSearch.setText("Tìm kiếm");
        });
        btnLamMoi.addActionListener(e -> {
            loadTableData();
            txtSearch.setText("Tìm kiếm");
        });

        NorthPanel.add(title, BorderLayout.NORTH);
        timKiemWrapper.add(pnlSearchInput);
        timKiemWrapper.add(btnLamMoi);
        NorthPanel.add(timKiemWrapper, BorderLayout.CENTER);

        // Dòng số lượng
        JPanel SouthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        SouthPanel.setBackground(Color.WHITE);
        
        JLabel soLuong = new JLabel("Số lượng: ");

        txtSoLuong = new JTextField("3");
        txtSoLuong.setPreferredSize(new Dimension(60, 28));
        txtSoLuong.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        JButton btnThem = new JButton("+ Thêm");
        Style.styleButton(btnThem);

        btnThem.addActionListener(e -> handleAddProduct());

        SouthPanel.add(soLuong);
        SouthPanel.add(txtSoLuong);
        SouthPanel.add(btnThem);

        panel.add(NorthPanel, BorderLayout.NORTH);
        panel.add(createTableLeft(), BorderLayout.CENTER);
        panel.add(SouthPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JScrollPane createTableLeft() {
        String[] columns = {"Mã SP", "Tên sản phẩm", "SL", "Đơn giá"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);

        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(210,230,255));
        table.getTableHeader().setReorderingAllowed(false);

        loadTableData();

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                txtSoLuong.requestFocus();
            }
        });

        return new JScrollPane(table);
    }

    private JPanel createFormRight() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("THÔNG TIN PHIẾU NHẬP");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(createTailRight(), BorderLayout.NORTH);
        panel.add(createTableRight(), BorderLayout.CENTER);
        panel.add(createTotalAmount(), BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createTailRight() {
        JPanel panel = new JPanel(new GridLayout(3,2,10,10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new TitledBorder("THÔNG TIN PHIẾU NHẬP"));
        JLabel lblID = new JLabel("Mã phiếu nhập");
        JLabel lblNCC = new JLabel("Nhà Cung Cấp");

        String[] itemLoc = new String[nccBus.getListNCC().size()];
        itemLoc[0] = "Mã nhà Cung Cấp";
        for (int i = 1; i < nccBus.getListNCC().size(); i++) {
           itemLoc[i] = nccBus.getListNCC().get(i).getMaNCC();
        }
        JComboBox<String> comboBoxLoc = new JComboBox<>(itemLoc);
        comboBoxLoc.setBackground(new Color(214, 238, 253));
        comboBoxLoc.setFont(new Font("Segoe UI", Font.BOLD, 13));
        comboBoxLoc.setCursor(new Cursor(Cursor.HAND_CURSOR));
        comboBoxLoc.setSelectedIndex(0);

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
        comboBoxLoc.addActionListener(e -> {
            txtNCC.setText(comboBoxLoc.getSelectedItem().toString());
        });

        JLabel lblNTP = new JLabel("Người tạo phiếu");
        txtID = new JTextField("Tự động tạo");
        txtID.setEnabled(false);
        txtNCC = new JTextField("");
        txtNTP = new JTextField(maNV);
        txtNTP.setEnabled(false);
        panel.add(lblID);
        panel.add(txtID);

        panel.add(lblNCC);
        panel.add(comboBoxLoc);

        panel.add(lblNTP);
        panel.add(txtNTP);

        return panel;
    }

    private JPanel createTableRight() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        String[] columns = {"STT","Mã SP","Tên sản phẩm","SL","Ngày nhập","Đơn giá"};

        tableModelRight = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return isEditMode && column == 3;
            }
        };
        tableRight = new JTable(tableModelRight);

        tableRight.setRowHeight(30);
        tableRight.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableRight.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableRight.getTableHeader().setBackground(new Color(210,230,255));
        tableRight.getTableHeader().setReorderingAllowed(false);

        // Căn giữa
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < tableRight.getColumnCount(); i++) {
            tableRight.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        // Số lượng mới
        JPanel editPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        editPanel.setBackground(Color.WHITE);

        JLabel lblSoLuongRight = new JLabel("Số lượng mới:");

        txtSoLuongRight = new JTextField(5);
        txtSoLuongRight.setPreferredSize(new Dimension(80, 28));
        txtSoLuongRight.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        editPanel.add(lblSoLuongRight);
        editPanel.add(txtSoLuongRight);

        // Nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);

        btnXuat = new JButton("Xuất Excel");
        Style.styleButton(btnXuat);

        btnSua = new JButton("Sửa số lượng");
        Style.styleButton(btnSua);

        btnXoa = new JButton("Xóa sản phẩm");
        Style.styleButton(btnXoa);

        buttonPanel.add(btnXuat);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnXoa);

        bTnRightEvent();

        JPanel southWrapper = new JPanel(new BorderLayout());
        southWrapper.setBackground(Color.WHITE);

        southWrapper.add(editPanel, BorderLayout.NORTH);
        southWrapper.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(new JScrollPane(tableRight), BorderLayout.CENTER);
        panel.add(southWrapper, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createTotalAmount() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10,0,0,0));

        JPanel leftWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftWrapper.setBackground(Color.WHITE);

        lblTongTien = new JLabel("TỔNG TIỀN NHẬP: ");
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTongTien.setForeground(new Color(0,128,0));

        txtTongTien = new JTextField("0", 10);
        txtTongTien.setEditable(false);
        txtTongTien.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtTongTien.setForeground(new Color(0,128,0));

        leftWrapper.add(lblTongTien);
        leftWrapper.add(txtTongTien);

        btnNhap = new JButton("Nhập kho");
        Style.styleButton(btnNhap);
        btnNhap.addActionListener(e ->{
            if (tableModelRight.getRowCount()==0){
                JOptionPane.showMessageDialog(this, "Không có sản phẩm cần nhập!");
                return;
            }
            taoPN();
        });

        panel.add(leftWrapper, BorderLayout.WEST);
        panel.add(btnNhap, BorderLayout.EAST);

        return panel;
    }

    public void deleteSP(){
        int row = tableRight.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Sản phẩm không tồn tại,vui lòng chọn lại");
            return;
        }
        long sumMoney = 0;
        tableModelRight.removeRow(row);
        for (int i = 0; i < tableModelRight.getRowCount(); i++) {
            int quantity = Integer.parseInt(
                    tableModelRight.getValueAt(i, 3).toString()
            );

            double money = Double.parseDouble(
                    tableModelRight.getValueAt(i, 5).toString()
            );
//            Cập nhật STT sau khi xóa dòng
            tableModelRight.setValueAt(i+1,i,0);
            sumMoney += quantity * money;
        }
        txtTongTien.setText(String.valueOf(sumMoney));
    }

    public void UpdateSP(){
        tableRight.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                int row = tableRight.getSelectedRow();
                if (row == -1) return;
                // UX: cho con trỏ nhảy sang ô nhập số lượng
                txtSoLuongRight.requestFocus();
            }
        });
        btnSua.addActionListener(e->{
            int row = tableRight.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Sản phẩm không tồn tại,vui lòng chọn lại");
                return;
            }
            int soLuong;
            try {
                soLuong = Integer.parseInt(txtSoLuongRight.getText());
                if (soLuong <= 0) throw new Exception();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ");
                return;
            }
            tableModelRight.setValueAt(soLuong,row,3);
            txtSoLuongRight.setText("0");
            long sumMoney = 0;
            for (int i = 0; i < tableModelRight.getRowCount(); i++) {
                int quantity = Integer.parseInt(
                        tableModelRight.getValueAt(i, 3).toString()
                );

                double money = Double.parseDouble(
                        tableModelRight.getValueAt(i, 5).toString()
                );
                sumMoney += quantity * money;
            }
            txtTongTien.setText(String.valueOf(sumMoney));
        });
    }

    public void bTnRightEvent(){
        btnXoa.addActionListener(e -> deleteSP());
        UpdateSP();
    }

    private void handleAddProduct() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chưa chọn sản phẩm");
            return;
        }

        int soLuong;
        long tongTien = Long.parseLong(txtTongTien.getText());
        try {
            soLuong = Integer.parseInt(txtSoLuong.getText());
            if (soLuong <= 0) throw new Exception();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ");
            return;
        }

        String maSP = tableModel.getValueAt(row, 0).toString();
        String tenSP = tableModel.getValueAt(row, 1).toString();
        double donGia = Double.parseDouble(
                tableModel.getValueAt(row, 3).toString()
        );

        for (int i = 0; i < tableModelRight.getRowCount(); i++) {
            Object val = tableModelRight.getValueAt(i, 1);
            if (val != null && val.toString().equals(maSP)) {
                int slCu = (int) tableModelRight.getValueAt(i, 3);
                tableModelRight.setValueAt(slCu + soLuong, i, 3);
                tongTien += soLuong * donGia;
                txtTongTien.setText(String.valueOf(tongTien));
                txtSoLuong.setText("");
                return;
            }
        }

        String ngayNhap = LocalDate.now().format(DATE_FORMAT);
        int stt = tableModelRight.getRowCount() + 1;

        tableModelRight.addRow(new Object[]{
                stt, maSP, tenSP, soLuong, ngayNhap, donGia
        });
        tongTien += soLuong * donGia;
        String txtTien = String.valueOf(tongTien);
        txtTongTien.setText(txtTien);
        txtSoLuong.setText("");
    }

    public void loadTableData() {
        tableModel.setRowCount(0); // xóa dữ liệu cũ
        for( SanPham sp : spBus.getListSP()){
            tableModel.addRow(new Object[]{
                    sp.getMaSP(),
                    sp.getTenSP(),
                    sp.getSoLuong(),
                    sp.getGiaTien()
            });
        }
    }

    public void loadDataFromKey(){
        tableModel.setRowCount(0); // xóa dữ liệu cũ
        for( SanPham sp : spBus.gettSPByKeyWord(txtSearch.getText())){
            tableModel.addRow(new Object[]{
                    sp.getMaSP(),
                    sp.getTenSP(),
                    sp.getSoLuong(),
                    sp.getGiaTien()
            });
        }
    }

    public void searchSP(){
        String key = txtSearch.getText().trim();
        if(key.equalsIgnoreCase("Tìm kiếm") || key.isEmpty()){
            loadTableData();
            return;
        }
        loadDataFromKey();
    }

    public void txtSearchEvent(){
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                restartTimer();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                restartTimer();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
        txtSearch.addFocusListener(new FocusAdapter() {
            @Override
//            Khi nhấn vào ô search
            public void focusGained(FocusEvent e) {
                if(txtSearch.getText().equalsIgnoreCase("Tìm kiếm")){
                    txtSearch.setText("");
                    txtSearch.setForeground(Color.BLACK);
                }
            }
            //            Khi nhấn nơi khác
            @Override
            public void focusLost(FocusEvent e) {
                if(txtSearch.getText().isEmpty()){
                    txtSearch.setText("Tìm kiếm");
                    txtSearch.setForeground(Color.GRAY);
                }
            }
        });
    }

    private void restartTimer() {
        if (searchTimer.isRunning()) { // Nếu Timer đang chạy thì khởi tạo lại,đếm từ 0 -> 1s
            searchTimer.restart();
        } else {
            searchTimer.start();
        }
    }

    private void resetForm() {
        txtNCC.setText("");
        tableModelRight.setRowCount(0);
        txtTongTien.setText("0");

    }

    public void taoPN(){
        LocalDateTime dateTime = LocalDateTime.now();

        String maPN = "PN" + dateTime
                .format(DateTimeFormatter.ofPattern("ddMMyyyyHHmmss"));

        String ManCC = txtNCC.getText().trim();
        if(ManCC.isEmpty() || ManCC.equalsIgnoreCase("Mã nhà cung cấp")){
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã Nhà Cung Cấp","Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        NhaCungCap ncc = new NhaCungCap();
        ncc.setMaNCC(ManCC);
        NhanVien nv = new NhanVien();
        nv.setMaNV(maNV);

        PhieuNhap pn = new PhieuNhap(maPN,dateTime,ncc,nv);
        if (pnBus.taoPhieuNhap(pn,tableModelRight)){
            JOptionPane.showMessageDialog(
                    this,
                    "Thêm phiếu thành công",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE
            );
            resetForm();
        }else{
            JOptionPane.showMessageDialog(
                    this,
                    "Thêm phiếu thất bại",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
}
