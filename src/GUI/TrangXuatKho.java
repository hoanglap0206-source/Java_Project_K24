package GUI;

import BUS.SanPham_BUS;
import Model.SanPham;
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
import java.time.format.DateTimeFormatter;

public class TrangXuatKho extends JPanel {
    private SanPham_BUS spBus = new SanPham_BUS();

    private DefaultTableModel tableModel;
    private JTable table;

    private DefaultTableModel tableModelRight;
    private JTable tableRight;

    private JTextField txtSoLuong;
    private JLabel lblTongTien;
    private JTextField txtTongTien;

    private JTextField txtSearch;
    private Timer searchTimer;

    private JTextField txtSoLuongRight;
    private JButton btnXuat, btnXoa, btnSua;
    private boolean isEditMode = false;

    private JTextField txtID, txtNTP;
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public TrangXuatKho() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(createContent(), BorderLayout.CENTER);

        loadTableData();

        searchTimer = new Timer(500, e -> searchSP());
        searchTimer.setRepeats(false);
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

        txtSearchEvent();

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

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        for(int i=0;i<table.getColumnCount();i++){
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(new Color(180,180,180)));

        return scrollPane;
    }

    private JPanel createFormRight() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("THÔNG TIN PHIẾU XUẤT");
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
        panel.setBorder(new TitledBorder("THÔNG TIN PHIẾU XUẤT"));

        panel.add(new JLabel("Mã phiếu xuất:"));
        panel.add(new JTextField("Tự động tạo"));

        panel.add(new JLabel("Khách hàng:"));
        panel.add(new JTextField());

        panel.add(new JLabel("Người tạo phiếu:"));
        panel.add(new JTextField());

        return panel;
    }

    private JPanel createTableRight() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        tableModelRight = new DefaultTableModel(
                new String[]{"STT","Mã SP","Tên sản phẩm","SL","Đơn giá"},
                0
        ){
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableRight = new JTable(tableModelRight);

        tableRight.setRowHeight(30);
        tableRight.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableRight.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableRight.getTableHeader().setBackground(new Color(210,230,255));
        tableRight.getTableHeader().setReorderingAllowed(false);

        // Căn giữa toàn bộ
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < tableRight.getColumnCount(); i++) {
            tableRight.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        JScrollPane scroll = new JScrollPane(tableRight);

        // Wrapper chứa bảng + VAT
        JPanel centerWrapper = new JPanel();
        centerWrapper.setLayout(new BorderLayout());
        centerWrapper.setBackground(Color.WHITE);

        JPanel vatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        vatPanel.setBackground(new Color(240,240,240));
        vatPanel.setBorder(new MatteBorder(1, 1, 1, 1, new Color(180,180,180)));

        JLabel lblVAT = new JLabel("Thuế (VAT): 10%");
        vatPanel.add(lblVAT);

        centerWrapper.add(scroll, BorderLayout.CENTER);
        centerWrapper.add(vatPanel, BorderLayout.SOUTH);

        JPanel soLuongPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        soLuongPanel.setBackground(Color.WHITE);

        JLabel lblSoLuong = new JLabel("SỐ LƯỢNG");
        txtSoLuongRight = new JTextField("0",5);

        soLuongPanel.add(lblSoLuong);
        soLuongPanel.add(txtSoLuongRight);

        panel.add(soLuongPanel, BorderLayout.NORTH);

        // Nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);

        btnXuat = new JButton("Xuất excel");
        btnSua = new JButton("Sửa số lượng");
        btnXoa = new JButton("Xóa sản phẩm");

        Style.styleButton(btnXuat);
        Style.styleButton(btnSua);
        Style.styleButton(btnXoa);

        buttonPanel.add(btnXuat);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnXoa);

        bTnRightEvent();

        panel.add(centerWrapper, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createTotalAmount() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10,0,0,0));

        JPanel leftWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftWrapper.setBackground(Color.WHITE);

        lblTongTien = new JLabel("TỔNG TIỀN XUẤT: ");
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTongTien.setForeground(new Color(0,128,0));

        txtTongTien = new JTextField("0", 10);
        txtTongTien.setEditable(false);
        txtTongTien.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtTongTien.setForeground(new Color(0,128,0));

        leftWrapper.add(lblTongTien);
        leftWrapper.add(txtTongTien);

        JButton btnNhap = new JButton("Xuất kho");
        Style.styleButton(btnNhap);

        panel.add(leftWrapper, BorderLayout.WEST);
        panel.add(btnNhap, BorderLayout.EAST);

        return panel;
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
        int quantity = Integer.parseInt(
                tableModel.getValueAt(row,2).toString()
        );
        String maSP = tableModel.getValueAt(row, 0).toString();
        String tenSP = tableModel.getValueAt(row, 1).toString();
        double donGia = Double.parseDouble(
                tableModel.getValueAt(row, 3).toString()
        );
        if(soLuong>quantity){
            JOptionPane.showMessageDialog(this, "Số lượng không đủ để cung cấp!");
            return;
        }
        for (int i = 0; i < tableModelRight.getRowCount(); i++) {
            Object val = tableModelRight.getValueAt(i, 1);
            if (val != null && val.toString().equals(maSP)) {
                int slCu = (int) tableModelRight.getValueAt(i, 3);
                if((slCu+soLuong)>quantity){
                    JOptionPane.showMessageDialog(this, "Số lượng không đủ để cung cấp!");
                    return;
                }
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
                stt, maSP, tenSP, soLuong, donGia
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
        if (searchTimer.isRunning()) {
            searchTimer.restart();
        } else {
            searchTimer.start();
        }
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
                    tableModelRight.getValueAt(i, 4).toString()
            );
//                        Cập nhật STT sau khi xóa dòng
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
            String maSP = String.valueOf(tableModelRight.getValueAt(row,1));
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
            for(int i=0;i<tableModel.getRowCount();i++){
                Object val = tableModel.getValueAt(i,0);
                if(val!=null && val.toString().equals(maSP)){
                    int soLuongLeft = Integer.parseInt(tableModel.getValueAt(i,2).toString());
                    if (soLuongLeft<soLuong){
                        JOptionPane.showMessageDialog(this, "Số lượng không đủ để cung cấp!");
                        return;
                    }
                }
            }
            tableModelRight.setValueAt(soLuong,row,3);
            long sumMoney = 0;
            for (int i = 0; i < tableModelRight.getRowCount(); i++) {
                int quantity = Integer.parseInt(
                        tableModelRight.getValueAt(i, 3).toString()
                );

                double money = Double.parseDouble(
                        tableModelRight.getValueAt(i, 4).toString()
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
}
