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

    private JSplitPane splitPane;
    private JPanel panelForm;
    private JLabel lblFormTitle;
    private JTextField txtMaKH, txtTenKH, txtSdt, txtDiaChi,txtChiTieu;
    private String currentMode = "THEM";

    public TrangKhachHang() {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 255, 255));
        initUI();
        fillToTable();
    }
    private void initUI() {
        // 1. Thanh công cụ (Tìm kiếm, Thêm, Sửa, Xóa)
        add(taoThanhCongCu(), BorderLayout.NORTH);

        // 2. Nội dung chính: Bảng bên trái, Form bên phải
        JPanel panelContent = new JPanel(new BorderLayout());
        panelContent.setBackground(Color.WHITE);
        panelContent.setBorder(new EmptyBorder(10, 20, 20, 20));

        // Tạo bảng
        JScrollPane scrollPane = taoBang();

        // Tạo Form nhập liệu (Side Panel)
        panelForm = taoPanelForm();
        panelForm.setVisible(false); // Mặc định ẩn

        // JSplitPane chia đôi màn hình
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, panelForm);
        splitPane.setResizeWeight(1.0);
        splitPane.setDividerSize(0);
        splitPane.setBorder(null);

        panelContent.add(new JLabel("DANH SÁCH KHÁCH HÀNG", JLabel.LEFT), BorderLayout.NORTH); // Tiêu đề nhỏ
        panelContent.add(splitPane, BorderLayout.CENTER);

        add(panelContent, BorderLayout.CENTER);
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
        txtMaKH = new JTextField();
        txtTenKH = new JTextField();
        txtSdt = new JTextField();
        txtDiaChi = new JTextField();
        txtChiTieu = new JTextField("0");
        txtChiTieu.setEditable(false);
        txtChiTieu.setBackground(new Color(230, 230, 230));


        pnl.add(taoNhomInput("Mã khách hàng:", txtMaKH));
        pnl.add(Box.createVerticalStrut(10));
        pnl.add(taoNhomInput("Họ và tên:", txtTenKH));
        pnl.add(Box.createVerticalStrut(10));
        pnl.add(taoNhomInput("Số điện thoại:", txtSdt));
        pnl.add(Box.createVerticalStrut(10));
        pnl.add(taoNhomInput("Địa chỉ:", txtDiaChi));
        pnl.add(Box.createVerticalStrut(10));
        pnl.add(taoNhomInput("Chi tiêu:", txtChiTieu));

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
        pnlBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30)); // Giới hạn chiều cao panel nút

        pnlBtn.add(btnSave);
        pnlBtn.add(btnCancel);

        pnl.add(pnlBtn);

        // Thêm scrollPane vào CENTER thay vì NORTH để nó chiếm trọn không gian
        outer.add(pnl, BorderLayout.CENTER);
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
        btn.setPreferredSize(new Dimension(90, 30));
    }
    private JPanel taoNhomInput(String label, JTextField tf) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(new Color(245, 247, 250));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200)),
                new EmptyBorder(5, 10, 5, 10)
        ));

        p.add(lbl);
        p.add(Box.createVerticalStrut(5));
        p.add(tf);
        p.add(Box.createVerticalStrut(15));
        return p;
    }
    private void xuLyLuu() {
        String ma = txtMaKH.getText().trim();
        String ten = txtTenKH.getText().trim();
        String sdt = txtSdt.getText().trim();
        String dc = txtDiaChi.getText().trim();
        String ct = txtChiTieu.getText().replaceAll("[^0-9]", "");
        if(ct.isEmpty()) ct = "0";

        // --- PHẦN RÀNG BUỘC (VALIDATION) ---

        // 1. Kiểm tra Tên khách hàng
        if (ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên khách hàng không được để trống!", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            txtTenKH.requestFocusInWindow(); // Focus vào ô tên
            return;
        }

        // 2. Kiểm tra Số điện thoại (Phải là 10 số)
        if (sdt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không được để trống!", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            txtSdt.requestFocusInWindow();
            return;
        }
        if (!sdt.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại phải có đúng 10 chữ số!", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
            txtSdt.requestFocusInWindow();
            return;
        }

        // 3. Kiểm tra Địa chỉ
        if (dc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Địa chỉ không được để trống!", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            txtDiaChi.requestFocusInWindow();
            return;
        }

        // --- THỰC HIỆN LƯU ---
        KhachHang kh = new KhachHang(ma, ten, dc, sdt, ct);
        String res = currentMode.equals("THEM") ? khBUS.addKhachHang(kh) : khBUS.updateKH(kh);

        if (res.toLowerCase().contains("thành công")) {
            JOptionPane.showMessageDialog(this, res, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            khBUS.refeshData(); // Cập nhật lại list trong BUS
            fillToTable();      // Vẽ lại bảng
            hideForm();         // Đóng form
        } else {
            JOptionPane.showMessageDialog(this, res, "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void showForm(String mode, KhachHang kh) {
        this.currentMode = mode;
        if (mode.equals("THEM")) {
            lblFormTitle.setText("THÊM KHÁCH HÀNG");
            String maKH = khBUS.getNewMaKH();
            txtMaKH.setText(maKH);
            txtMaKH.setEditable(false);
            txtMaKH.setBackground(new Color(235, 235, 235));
            txtTenKH.setText("");
            txtSdt.setText("");
            txtDiaChi.setText("");
            txtChiTieu.setText("0");
            panelForm.setVisible(true);
            SwingUtilities.invokeLater(()->{
                txtTenKH.requestFocusInWindow();
            });
            splitPane.setDividerLocation(this.getWidth()-350);
        } else {
            lblFormTitle.setText("SỬA THÔNG TIN KH");
            txtMaKH.setText(kh.getMaKH());
            txtMaKH.setEditable(false);
            txtMaKH.setBackground(new Color(230, 230, 230));

            txtTenKH.setText(kh.getHoTenKH());
            txtSdt.setText(kh.getSdt());
            txtDiaChi.setText(kh.getDiaChi());
        }
        panelForm.setVisible(true);
        SwingUtilities.invokeLater(()->{
            txtTenKH.requestFocusInWindow();
        });
        splitPane.setDividerLocation(this.getWidth() - 300);
    }

    private void hideForm() {
        panelForm.setVisible(false);
    }
    private JPanel taoThanhCongCu() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(new EmptyBorder(15,20,5,20));

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT,12,8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(4,10,4,10));

        // Thanh tìm kiếm
        String place ="Tìm kiếm (VD:KH1)";
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
        String[] itemLoc = {"Mặc định","MaKH(1-N)", "Tên(A-Z)","Tên(Z-A)"};
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

        Image scaledImage = new ImageIcon(
                getClass().getResource("/Img/Excel.png")
        ).getImage().getScaledInstance(20,20,Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JButton btnExcel = new JButton("Xuất excel",scaledIcon);
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


        btnAdd.addActionListener(e ->showForm("THEM",null));
        btnDelete.addActionListener(e->{
            int row = table.getSelectedRow();
            if(row==-1){
                JOptionPane.showMessageDialog(this,"Vui lòng chọn khách hàng cần xoá từ bảng!");
                return;
            }
            int modelRow= table.convertRowIndexToModel(row);
            String maKH=model.getValueAt(modelRow,1).toString();
            String tenKH=model.getValueAt(row,2).toString();

            //hộp thoại để tránh bấm nhầm
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc chắn muốn xoá khách hàng"+tenKH+"("+maKH+")?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if(choice== JOptionPane.YES_OPTION){
                String result = khBUS.deleteKH(maKH);

                if(result.contains("thành công!")){
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
                JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần sửa từ bảng!");
                return;
            }

            // Lấy chỉ số dòng thực tế trong model (phòng trường hợp bảng đang bị sắp xếp/lọc)
            int modelRow = table.convertRowIndexToModel(row);

            // Trích xuất dữ liệu từ Model
            String ma = model.getValueAt(modelRow, 1).toString();
            String ten = model.getValueAt(modelRow, 2).toString();
            String sdt = model.getValueAt(modelRow, 3).toString();
            String dc = model.getValueAt(modelRow, 4).toString();
            // Giả sử cột 5 là Chi tiêu hoặc bạn lấy từ đối tượng KH trong BUS
            String ct = model.getValueAt(modelRow, 5).toString();

            // Tạo đối tượng tạm để truyền vào Form
            KhachHang kh = new KhachHang(ma, ten, dc, sdt, ct);

            // Hiển thị form ở chế độ SUA
            showForm("SUA", kh);
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
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu file Excel");
        fileChooser.setSelectedFile(new java.io.File("DanhSachKhachHang.xlsx"));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Excel Files (*.xlsx)", "xlsx"));

        if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        java.io.File file = fileChooser.getSelectedFile();
        if (!file.getName().endsWith(".xlsx"))
            file = new java.io.File(file.getAbsolutePath() + ".xlsx");

        try (org.apache.poi.xssf.usermodel.XSSFWorkbook workbook =
                     new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {

            org.apache.poi.ss.usermodel.Sheet sheet =
                    workbook.createSheet("Danh sách khách hàng");

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

            // Dòng tiêu đề — 6 cột
            String[] cols = {"STT", "Mã KH", "Tên KH", "SĐT", "Địa chỉ", "Chi tiêu"};
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
            for (int i = 0; i < cols.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(cols[i]);
                cell.setCellStyle(headerStyle);
            }

            // Ghi dữ liệu từ model
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