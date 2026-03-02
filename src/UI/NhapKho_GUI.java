package UI;

import BUS.SanPham_BUS;
import Model.SanPham;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class NhapKho_GUI extends JPanel {
    private JTextField txtSearch;
    private SanPham_BUS spBus = new SanPham_BUS();
    private DefaultTableModel tableModel;
    private JTable table;
    JTextField txtSoLuong;
    private DefaultTableModel tableModelRight;
    private JTable tableRight;
    private JTextField txtTongTien;
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private Timer searchTimer; //Biến lưu giá trị thời gian khi người dùng nhập dữ liệu
    private JButton btnXuat,btnXoa,btnSua;
    private boolean isEditMode = false;
    private JTextField txtSoLuongRight;
    private JTextField txtNTP,txtID;
    private String maNV = GiaoDienChinh_Main.currentMaNV;

    public NhapKho_GUI() {
        searchTimer = new Timer(500, e -> searchSP()); // Đợi người dùng ngưng 1s mới gọi hàm
        searchTimer.setRepeats(false); // Chạy 1 lần duy nhất,không lặp

        JPanel main = new JPanel(new GridLayout(1, 2));
        JPanel uiLeft = new JPanel(new BorderLayout());
        JPanel uiRight = new JPanel(new BorderLayout());

        main.add(uiLeft);
        main.add(uiRight);
//================= Left =========================================
        uiLeft.add(createSearchBar(),BorderLayout.NORTH);
        uiLeft.add(createTableLeft(), BorderLayout.CENTER);
        loadTableData();
        uiLeft.add(createTailLeft(), BorderLayout.SOUTH);
//=================== Right ========================================
        uiRight.add(createFormRight(),BorderLayout.NORTH);
        uiRight.add(createTableRight(),BorderLayout.CENTER);
        uiRight.add(createTailRight(),BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(main, BorderLayout.CENTER);

    }

    public JPanel createSearchBar() {

        JPanel outerPanel = new JPanel();
        outerPanel.setBorder(new EmptyBorder(5, 5, 5, 15));

        JPanel outPanel = new JPanel();
        outPanel.setBorder(new CompoundBorder(
                new LineBorder(Color.BLACK, 2, true),
                new EmptyBorder(15, 20, 15, 20)   // ↓ GIẢM padding
        ));

        JPanel innerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4)); // ↓ giảm vertical gap
        innerPanel.setBackground(new Color(230, 230, 230));
        innerPanel.setBorder(
                new CompoundBorder(
                        new LineBorder(Color.GRAY, 2, true),
                        new EmptyBorder(6, 6, 6, 6)   // ↓ gọn hơn
                )
        );

        // ===== TEXT SEARCH =====
        txtSearch = new JTextField(22); // ↑ dài hơn
        txtSearch.setText("Tìm kiếm");
        txtSearch.setForeground(Color.GRAY);
        txtSearch.setPreferredSize(new Dimension(255, 28)); // chiều cao chuẩn
        txtSearch.setBorder(
                new CompoundBorder(
                        new LineBorder(Color.GRAY, 1, true),
                        new EmptyBorder(5, 10, 5, 10)
                )
        );
        txtSearchEvent();

        // ===== NÚT LÀM MỚI =====
        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.setFocusPainted(false);
        btnRefresh.setBackground(Color.WHITE);
        btnRefresh.setPreferredSize(new Dimension(115, 28)); // bằng chiều cao txtSearch
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("Tìm kiếm");
            txtSearch.setForeground(Color.GRAY);
            loadTableData();
        });

        innerPanel.add(txtSearch);
        innerPanel.add(btnRefresh);

        outPanel.add(innerPanel);
        outerPanel.add(outPanel);

        return outerPanel;
    }

    public JScrollPane createTableLeft(){
        String[] cols = {"Mã SP", "Tên sản phẩm", "Số lượng", "Đơn giá"};

        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(60); // Mã SP
        table.getColumnModel().getColumn(1).setPreferredWidth(200); // Tên SP
        table.getColumnModel().getColumn(2).setPreferredWidth(70); // Số lượng
        table.getColumnModel().getColumn(3).setPreferredWidth(100); // Đơn giá
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new EmptyBorder(15, 20, 20, 20));
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                int row = table.getSelectedRow();
                if (row == -1) return;
                // UX: cho con trỏ nhảy sang ô nhập số lượng
                txtSoLuong.requestFocus();
            }
        });
        return scroll;
    }

    public JPanel createTailLeft(){
        JPanel tailWrapper = new JPanel(new GridBagLayout());
        tailWrapper.setBorder(new EmptyBorder(10, 0, 60, 0));

        JPanel tailLeft = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));

        JLabel lblSoLuong = new JLabel("SỐ LƯỢNG");
        txtSoLuong = new JTextField("3", 5);

        JButton btnThem = new JButton("Thêm");
        btnThem.setBackground(new Color(102, 255, 102));
        btnThem.setFocusPainted(false);
        btnThem.addActionListener(e -> handleAddProduct());
        tailLeft.add(lblSoLuong);
        tailLeft.add(txtSoLuong);
        tailLeft.add(btnThem);

        tailWrapper.add(tailLeft);
        return tailWrapper;
    }

    public JPanel createFormRight(){
        JPanel headRightWrap = new JPanel(new GridLayout(3,2,0,20));
        headRightWrap.setBorder(new EmptyBorder(40,5,15,20));
        JLabel lblID = new JLabel("Mã phiếu nhập");
        JLabel lblNCC = new JLabel("Nhà cung cấp");
        JLabel lblNTP = new JLabel("Người tạo phiếu");
        txtID = new JTextField("Tự động tạo");
        txtID.setEnabled(false);
        JTextField txtNCC = new JTextField("Tên nhà cung cấp");
        txtNTP = new JTextField("Tên người dùng");
        txtNTP.setEnabled(false);
        headRightWrap.add(lblID);
        headRightWrap.add(txtID);
        headRightWrap.add(lblNCC);
        headRightWrap.add(txtNCC);
        headRightWrap.add(lblNTP);
        headRightWrap.add(txtNTP);
        return headRightWrap;
    }

    public JPanel createTableRight(){
        JPanel TableRightWrap = new JPanel(new BorderLayout(0, 20));

        // ===== TABLE =====
        String[] colsRight = {"STT", "Mã SP", "Tên sản phẩm", "Số lượng", "NN", "Đơn giá"};
        tableModelRight = new DefaultTableModel(colsRight, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return isEditMode && column == 3;
            }
        };
        tableRight = new JTable(tableModelRight);

        // cho phép bảng cao hơn
        tableRight.setPreferredScrollableViewportSize(
                new Dimension(520, 600)
        );

        tableRight.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tableRight.getColumnModel().getColumn(0).setPreferredWidth(40);
        tableRight.getColumnModel().getColumn(1).setPreferredWidth(60);
        tableRight.getColumnModel().getColumn(2).setPreferredWidth(150);
        tableRight.getColumnModel().getColumn(3).setPreferredWidth(60);
        tableRight.getColumnModel().getColumn(4).setPreferredWidth(70);
        tableRight.getColumnModel().getColumn(5).setPreferredWidth(70);

        JScrollPane scrollRight = new JScrollPane(tableRight);
        scrollRight.setBorder(new EmptyBorder(25, 10, 0, 10));
        // add bảng vào CENTER
        TableRightWrap.add(scrollRight, BorderLayout.CENTER);

        // ===== BUTTON & Text =====
        JPanel Container = new JPanel(new GridLayout(2,1,5,5));
        JPanel SoLGWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        JLabel lblSoLuong = new JLabel("SỐ LƯỢNG");
        txtSoLuongRight = new JTextField("0",5);
        SoLGWrap.add(lblSoLuong);
        SoLGWrap.add(txtSoLuongRight);
        JPanel btnWrap = new JPanel(new GridLayout(1, 3, 15, 0));

        btnXuat = new JButton("Xuất Excel");
        btnSua = new JButton("Sửa số lượng");
        btnXoa = new JButton("Xóa sản phẩm");

        bTnRightEvent();

        btnXuat.setFocusPainted(false);
        btnSua.setFocusPainted(false);
        btnXoa.setFocusPainted(false);

        btnXuat.setBackground(Color.WHITE);
        btnSua.setBackground(Color.WHITE);
        btnXoa.setBackground(Color.WHITE);

        btnWrap.add(btnXuat);
        btnWrap.add(btnSua);
        btnWrap.add(btnXoa);
        Container.add(SoLGWrap);
        Container.add(btnWrap);
        JPanel btnContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        btnContainer.add(Container);

        // add nút xuống SOUTH
        TableRightWrap.add(btnContainer, BorderLayout.SOUTH);

        return TableRightWrap;
    }

    public JPanel createTailRight(){
        JPanel tailRightWrapper = new JPanel(new GridBagLayout());
        tailRightWrapper.setBorder(new EmptyBorder(10, 0, 60, 0));

        JPanel tailRight = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));

        JLabel lblTongTien = new JLabel("TỔNG TIỀN NHẬP");
        txtTongTien = new JTextField("0", 10);
        txtTongTien.setEditable(false);
        JButton btnNhap = new JButton("Nhập hàng");
        btnNhap.setBackground(new Color(102, 255, 102));
        btnNhap.setFocusPainted(false);

        tailRight.add(lblTongTien);
        tailRight.add(txtTongTien);
        tailRight.add(btnNhap);

        tailRightWrapper.add(tailRight);
        return tailRightWrapper;
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
        for( SanPham sp : spBus.getAll()){
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
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Nhập kho");
            f.setContentPane(new NhapKho_GUI());
            f.setSize(1200, 800);
            f.setLocationRelativeTo(null);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setVisible(true);
        });
    }
}
