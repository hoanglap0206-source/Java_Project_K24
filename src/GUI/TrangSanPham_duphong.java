//package GUI;
//
//import BUS.SanPham_BUS;
//import BUS.KeKho_BUS;
//import Model.SanPham;
//import Model.KeKho;
//
//import javax.swing.*;
//import javax.swing.border.*;
//import javax.swing.event.DocumentEvent;
//import javax.swing.event.DocumentListener;
//import java.awt.*;
//import java.awt.event.FocusAdapter;
//import java.awt.event.FocusEvent;
//import javax.swing.table.*;
//import java.util.ArrayList;
//
//public class TrangSanPham extends JPanel {
//    private JTable table;
//    private DefaultTableModel model;
//
//    private JButton btnAdd;
//    private JButton btnEdit;
//    private JButton btnDelete;
//    private JButton btnRefresh;
//
//    private JSplitPane splitPane;
//    private JPanel panelForm;
//    private JPanel panelTableWrapper;
//    private JLabel lblFormTitle;
//
//    private JTextField txtMaSP;
//    private JTextField txtTenSP;
//    private JTextField txtSoLuong;
//    private JTextField txtGia;
//    private JComboBox<String> cboKeKho;
//    private JComboBox<String> cboDVT;
//
//    private SanPham_BUS spBus = new SanPham_BUS();
//    private KeKho_BUS kkBus = new KeKho_BUS();
//
//    // Search
//    private JTextField txtSearch;
//    private Timer searchTimer;
//    private ArrayList<Object[]> allRows = new ArrayList<>();
//    private JComboBox<String> comboBoxLoc;
//
//
//
//    public TrangSanPham() {
//        setLayout(new BorderLayout());
//        setBackground(new Color(255, 255, 255));
//
//        add(taoThanhCongCu(), BorderLayout.NORTH);
//        add(taoNoiDung(), BorderLayout.CENTER);
//        loadTableData();
//
//        searchTimer = new Timer(400, e -> applyFilter());
//        searchTimer.setRepeats(false);
//        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
//            @Override public void insertUpdate(DocumentEvent e) { restartTimer(); }
//            @Override public void removeUpdate(DocumentEvent e) { restartTimer(); }
//            @Override public void changedUpdate(DocumentEvent e) {}
//        });
//
//        addEvents();
//    }
//
//    private JPanel taoThanhCongCu() {
//        JPanel wrapper = new JPanel(new BorderLayout());
//        wrapper.setBackground(Color.WHITE);
//        wrapper.setBorder(new EmptyBorder(15, 20, 5, 20));
//
//        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
//        panel.setBackground(Color.WHITE);
//        panel.setBorder(new EmptyBorder(4,10,4,10));
//
//        // Thanh tìm kiếm
//        txtSearch = new JTextField("Tìm kiếm...");
//        txtSearch.setColumns(15);
//        txtSearch.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
//        txtSearch.setForeground(Color.GRAY);
//
//        JPanel pnlSearchInput = new JPanel(new BorderLayout());
//        pnlSearchInput.setBackground(Color.WHITE);
//        pnlSearchInput.setPreferredSize(new Dimension(260, 30));
//        pnlSearchInput.setBorder(new CompoundBorder(
//                new LineBorder(new Color(198, 226, 255), 2, true),
//                new EmptyBorder(0, 2, 0, 0)
//        ));
//
//        JButton btnSearchIcon = new JButton("🔍");
//        btnSearchIcon.setBackground(new Color(214, 238, 253));
//        btnSearchIcon.setBorderPainted(false);
//        btnSearchIcon.setFocusPainted(false);
//        btnSearchIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
//        btnSearchIcon.addActionListener(e -> applyFilter());
//
//        pnlSearchInput.add(txtSearch, BorderLayout.CENTER);
//        pnlSearchInput.add(btnSearchIcon, BorderLayout.EAST);
//
//        txtSearch.addFocusListener(new FocusAdapter() {
//            @Override public void focusGained(FocusEvent e) {
//                if (txtSearch.getText().equals("Tìm kiếm...")) { txtSearch.setText(""); txtSearch.setForeground(Color.BLACK); }
//            }
//            @Override public void focusLost(FocusEvent e) {
//                if (txtSearch.getText().isEmpty()) { txtSearch.setText("Tìm kiếm..."); txtSearch.setForeground(Color.GRAY); }
//            }
//        });
//
//        // Combobox Lọc
//        String[] itemLoc = {"Mặc định", "Tên A → Z", "Tên Z → A", "STT nhỏ → lớn", "STT lớn → nhỏ"};
//        JComboBox<String> comboBoxLoc = new JComboBox<>(itemLoc);
//        comboBoxLoc.setBackground(new Color(214, 238, 253));
//        comboBoxLoc.setPreferredSize(new Dimension(145, 30));
//        comboBoxLoc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
//        comboBoxLoc.setCursor(new Cursor(Cursor.HAND_CURSOR));
//        comboBoxLoc.setSelectedIndex(0);
//        comboBoxLoc.addActionListener(e -> applyFilter());
//        this.comboBoxLoc = comboBoxLoc;
//
//
//        // Các nút khác
//        btnEdit = new JButton("Chỉnh sửa");
//        Style.styleButton(btnEdit);
//        btnDelete = new JButton("Xóa");
//        Style.styleButton(btnDelete);
//        btnAdd = new JButton("+ Thêm");
//        Style.styleButton(btnAdd);
//        btnRefresh = new JButton("↻ Làm mới");
//        Style.styleButton(btnRefresh);
//        JButton btnExcel = new JButton("Xuất excel");
//        Style.styleButton(btnExcel);
//        btnExcel.addActionListener(e -> xuatExcel());
//
//        // Thêm vào panel
//        panel.add(pnlSearchInput);
//        panel.add(btnRefresh);
//        panel.add(comboBoxLoc);
//        panel.add(btnEdit);
//        panel.add(btnDelete);
//        panel.add(btnAdd);
//        panel.add(btnExcel);
//
//        wrapper.add(panel, BorderLayout.CENTER);
//        return wrapper;
//    }
//
//    private JPanel taoNoiDung() {
//        JPanel panel = new JPanel(new BorderLayout());
//        panel.setBackground(Color.WHITE);
//        panel.setBorder(new EmptyBorder(10, 25, 20, 25));
//
//        panel.add(taoTieuDe(), BorderLayout.NORTH);
//
//        // ===== LEFT: bảng =====
//        panelTableWrapper = new JPanel(new BorderLayout());
//        panelTableWrapper.add(taoBang(), BorderLayout.CENTER);
//
//        // ===== RIGHT: form (ẩn ban đầu) =====
//        panelForm = taoPanelForm();
//        panelForm.setVisible(false);
//
//        // ===== SPLIT =====
//        splitPane = new JSplitPane(
//                JSplitPane.HORIZONTAL_SPLIT,
//                panelTableWrapper,
//                panelForm
//        );
//        splitPane.setResizeWeight(1.0);
//        splitPane.setDividerSize(6);
//        splitPane.setBorder(null);
//
//        panel.add(splitPane, BorderLayout.CENTER);
//        return panel;
//    }
//
//    private JPanel taoPanelForm() {
//        JPanel outer = new JPanel(new BorderLayout());
//        outer.setPreferredSize(new Dimension(340, 0));
//        outer.setBackground(new Color(245, 247, 250));
//        outer.setBorder(new MatteBorder(0, 1, 0, 0, new Color(210, 220, 235)));
//
//        JPanel pnl = new JPanel();
//        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
//        pnl.setBackground(new Color(245, 247, 250));
//        pnl.setBorder(new EmptyBorder(30, 24, 24, 24));
//
//        lblFormTitle = new JLabel("THÊM SẢN PHẨM");
//        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
//        lblFormTitle.setForeground(new Color(30, 80, 160));
//        lblFormTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
//        JSeparator sep = new JSeparator();
//        sep.setForeground(new Color(198, 220, 255));
//        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
//        pnl.add(lblFormTitle); pnl.add(Box.createVerticalStrut(8));
//        pnl.add(sep); pnl.add(Box.createVerticalStrut(20));
//
//        txtMaSP    = new JTextField();
//        txtTenSP   = new JTextField();
//        txtSoLuong = new JTextField("0");
//        txtSoLuong.setEditable(false);
//        txtGia     = new JTextField();
//
//        // ComboBox đơn vị tính
//        cboDVT = new JComboBox<>(new String[]{"Thùng", "Chai", "Lon", "Hộp"});
//        cboDVT.setFont(new Font("Segoe UI", Font.PLAIN, 13));
//        cboDVT.setBackground(Color.WHITE);
//        cboDVT.setBorder(new LineBorder(new Color(198, 218, 245), 1, true));
//        cboDVT.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
//        cboDVT.setAlignmentX(Component.LEFT_ALIGNMENT);
//
//        // ComboBox kệ kho — load từ KeKho_BUS
//        cboKeKho = new JComboBox<>();
//        for (KeKho kk : kkBus.getListKK()) cboKeKho.addItem(kk.getMaKe());
//        cboKeKho.setFont(new Font("Segoe UI", Font.PLAIN, 13));
//        cboKeKho.setBackground(Color.WHITE);
//        cboKeKho.setBorder(new LineBorder(new Color(198, 218, 245), 1, true));
//        cboKeKho.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
//        cboKeKho.setAlignmentX(Component.LEFT_ALIGNMENT);
//
//        pnl.add(nhomField("Mã sản phẩm", txtMaSP, false));  pnl.add(Box.createVerticalStrut(10));
//        pnl.add(nhomField("Tên sản phẩm", txtTenSP, false)); pnl.add(Box.createVerticalStrut(10));
//        pnl.add(nhomCombo("Đơn vị tính", cboDVT));           pnl.add(Box.createVerticalStrut(10));
//        pnl.add(nhomField("Số lượng", txtSoLuong, true));    pnl.add(Box.createVerticalStrut(10));
//        pnl.add(nhomField("Giá nhập (VNĐ)", txtGia, false)); pnl.add(Box.createVerticalStrut(10));
//        pnl.add(nhomCombo("Kệ kho", cboKeKho));              pnl.add(Box.createVerticalStrut(24));
//
//        JButton btnSave = new JButton("💾  Lưu");
//        btnSave.setBackground(new Color(37, 120, 220)); btnSave.setForeground(Color.WHITE);
//        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
//        btnSave.setFocusPainted(false); btnSave.setBorderPainted(false);
//        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR)); btnSave.setPreferredSize(new Dimension(110, 36));
//        btnSave.addActionListener(e -> saveSanPham());
//
//        JButton btnCancel = new JButton("✕  Hủy");
//        btnCancel.setBackground(new Color(220, 225, 235)); btnCancel.setForeground(new Color(60, 60, 60));
//        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 13));
//        btnCancel.setFocusPainted(false); btnCancel.setBorderPainted(false);
//        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR)); btnCancel.setPreferredSize(new Dimension(110, 36));
//        btnCancel.addActionListener(e -> hideFormPanel());
//
//        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
//        pnlBtn.setBackground(new Color(245, 247, 250));
//        pnlBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
//        pnlBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
//        pnlBtn.add(btnSave); pnlBtn.add(btnCancel);
//        pnl.add(pnlBtn);
//
//        JScrollPane formScroll = new JScrollPane(pnl);
//        formScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//        formScroll.setBorder(null);
//        outer.add(formScroll, BorderLayout.CENTER);
//        return outer;
//    }
//
//    private JPanel nhomField(String label, JTextField field, boolean disabled) {
//        JPanel g = new JPanel(); g.setLayout(new BoxLayout(g, BoxLayout.Y_AXIS));
//        g.setBackground(new Color(245, 247, 250)); g.setAlignmentX(Component.LEFT_ALIGNMENT);
//        g.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
//        JLabel lbl = new JLabel(label);
//        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12)); lbl.setForeground(new Color(80, 100, 130)); lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
//        field.setFont(new Font("Segoe UI", Font.PLAIN, 13)); field.setBackground(disabled ? new Color(235, 238, 245) : Color.WHITE);
//        if (disabled) field.setForeground(new Color(120, 130, 150));
//        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34)); field.setAlignmentX(Component.LEFT_ALIGNMENT);
//        field.setBorder(new CompoundBorder(new LineBorder(new Color(198, 218, 245), 1, true), new EmptyBorder(4, 10, 4, 10)));
//        g.add(lbl); g.add(Box.createVerticalStrut(4)); g.add(field);
//        return g;
//    }
//
//    private JPanel nhomCombo(String label, JComboBox<String> combo) {
//        JPanel g = new JPanel(); g.setLayout(new BoxLayout(g, BoxLayout.Y_AXIS));
//        g.setBackground(new Color(245, 247, 250)); g.setAlignmentX(Component.LEFT_ALIGNMENT);
//        g.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
//        JLabel lbl = new JLabel(label);
//        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12)); lbl.setForeground(new Color(80, 100, 130)); lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
//        g.add(lbl); g.add(Box.createVerticalStrut(4)); g.add(combo);
//        return g;
//    }
//
//    private void showFormPanel() {
//        panelForm.setVisible(true);
//        splitPane.setDividerLocation(
//                getWidth() - 320
//        );
//    }
//
//    private JPanel taoTieuDe() {
//        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
//        pnl.setBackground(Color.WHITE);
//
//        JLabel lblTitle = new JLabel("DANH SÁCH SẢN PHẨM");
//        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
//        lblTitle.setForeground(Color.BLACK);
//
//        pnl.add(lblTitle);
//        return pnl;
//    }
//
//    private JScrollPane taoBang() {
//        String[] columns = {
//                "STT", "Mã SP", "Tên SP", "Đơn vị tính",
//                "Số lượng", "Giá nhập", "Mã kệ", "Trạng thái"
//        };
//
//        model = new DefaultTableModel(columns, 0) {
//            @Override
//            public boolean isCellEditable(int row, int column) {
//                return false; // Không cho sửa trực tiếp trên bảng
//            }
//        };
//
//        table = new JTable(model);
//        table.setRowHeight(30);
//        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
//        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
//
//        // Màu header
//        table.getTableHeader().setBackground(new Color(210, 230, 255));
//        table.getTableHeader().setForeground(Color.BLACK);
//
//        // Tắt reorder
//        table.getTableHeader().setReorderingAllowed(false);
//
//        // Căn giữa toàn bộ nội dung
//        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
//        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
//
//        // Áp dụng cho tất cả cột
//        for (int i = 0; i < table.getColumnCount(); i++) {
//            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
//        }
//
//        // Căn trái riêng cột Tên SP (cột 2)
//        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
//        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
//        leftRenderer.setBorder(new EmptyBorder(0, 8, 0, 0));
//        table.getColumnModel().getColumn(2).setCellRenderer(leftRenderer);
//
//        // Render cột trạng thái
//        table.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
//            @Override
//            public Component getTableCellRendererComponent(
//                    JTable table, Object value, boolean isSelected,
//                    boolean hasFocus, int row, int column) {
//
//                JLabel lbl = (JLabel) super.getTableCellRendererComponent(
//                        table, value, isSelected, hasFocus, row, column);
//
//                lbl.setHorizontalAlignment(SwingConstants.CENTER);
//
//                if (value != null) {
//                    if (value.toString().equals("Còn hàng")) {
//                        lbl.setForeground(new Color(0, 128, 0));
//                    } else {
//                        lbl.setForeground(Color.RED);
//                    }
//                }
//
//                return lbl;
//            }
//        });
//
//        // Chỉnh width từng cột cho giống mockup
//        table.getColumnModel().getColumn(0).setPreferredWidth(40);
//        table.getColumnModel().getColumn(1).setPreferredWidth(80);
//        table.getColumnModel().getColumn(2).setPreferredWidth(120);
//        table.getColumnModel().getColumn(3).setPreferredWidth(100);
//        table.getColumnModel().getColumn(4).setPreferredWidth(80);
//        table.getColumnModel().getColumn(5).setPreferredWidth(120);
//        table.getColumnModel().getColumn(6).setPreferredWidth(60);
//        table.getColumnModel().getColumn(7).setPreferredWidth(90);
//
//        JScrollPane scrollPane = new JScrollPane(table);
//        scrollPane.setBorder(new LineBorder(new Color(180, 180, 180)));
//
//        return scrollPane;
//    }
//
//    private void loadTableData() {
//        if (model == null) return;
//        model.setRowCount(0);
//        allRows.clear();
//        int stt = 1;
//        java.text.DecimalFormat df = new java.text.DecimalFormat("#,###đ");
//
//        // Dùng LinkedHashMap để nhóm sản phẩm và kệ
//        java.util.LinkedHashMap<String, SanPham> mapSP = new java.util.LinkedHashMap<>();
//        java.util.LinkedHashMap<String, java.util.List<String>> mapKe = new java.util.LinkedHashMap<>();
//
//        for (SanPham sp : spBus.getAll()) {
//            String maSP = sp.getMaSP();
//            String maKe = (sp.getKeKho() != null && sp.getKeKho().getMaKe() != null)
//                    ? sp.getKeKho().getMaKe() : "Chưa có";
//
//            if (!mapSP.containsKey(maSP)) {
//                mapSP.put(maSP, sp);
//                mapKe.put(maSP, new java.util.ArrayList<>());
//            }
//
//            if (!mapKe.get(maSP).contains(maKe)) {
//                mapKe.get(maSP).add(maKe);
//            }
//        }
//
//        // Đổ dữ liệu vào bảng
//        for (String maSP : mapSP.keySet()) {
//            SanPham sp = mapSP.get(maSP);
//            int soLuong = spBus.getSoLuongTon(maSP); // Lấy số lượng từ ChiTietKe
//            String trangThai = (soLuong > 0) ? "Còn hàng" : "Hết hàng";
//
//            Object[] row = new Object[]{
//                    stt++,                          // STT
//                    sp.getMaSP(),                   // Mã SP
//                    sp.getTenSP(),                  // Tên SP
//                    sp.getDonViTinh(),              // Đơn vị tính
//                    soLuong,                        // Số lượng (lấy từ ChiTietKe)
//                    df.format(sp.getGiaTien()),     // Giá nhập
//                    String.join(", ", mapKe.get(maSP)), // Mã kệ
//                    trangThai                       // Trạng thái
//            };
//
//            allRows.add(row);
//            model.addRow(row);
//        }
//    }
//
//    private void applyFilter() {
//        String kw = txtSearch.getText().trim();
//        if (kw.equalsIgnoreCase("Tìm kiếm...")) kw = "";
//
//        // Lọc trước
//        ArrayList<Object[]> filtered = new ArrayList<>();
//        for (Object[] row : allRows) {
//            if (!kw.isEmpty()) {
//                String k = kw.toLowerCase();
//                boolean match = row[1].toString().toLowerCase().contains(k)
//                        || row[2].toString().toLowerCase().contains(k)
//                        || row[3].toString().toLowerCase().contains(k)
//                        || row[6].toString().toLowerCase().contains(k)
//                        || row[7].toString().toLowerCase().contains(k);
//                if (!match) continue;
//            }
//            filtered.add(row);
//        }
//
//        // Sắp xếp
//        String sortOpt = comboBoxLoc != null && comboBoxLoc.getSelectedItem() != null
//                ? comboBoxLoc.getSelectedItem().toString() : "Mặc định";
//        switch (sortOpt) {
//            case "Tên A → Z" -> filtered.sort((a, b) -> a[2].toString().compareToIgnoreCase(b[2].toString()));
//            case "Tên Z → A" -> filtered.sort((a, b) -> b[2].toString().compareToIgnoreCase(a[2].toString()));
//            case "STT nhỏ → lớn" -> filtered.sort((a, b) -> Integer.compare((int) a[0], (int) b[0]));
//            case "STT lớn → nhỏ" -> filtered.sort((a, b) -> Integer.compare((int) b[0], (int) a[0]));
//        }
//
//        // Đổ vào bảng, đánh lại STT
//        model.setRowCount(0);
//        int counter = 1;
//        for (Object[] row : filtered) {
//            Object[] d = row.clone();
//            d[0] = counter++;
//            model.addRow(d);
//        }
//    }
//
//    private void restartTimer() { if (searchTimer.isRunning()) searchTimer.restart(); else searchTimer.start(); }
//    private void addEvents() {
//        btnAdd.addActionListener(e -> handleAdd());
//        btnEdit.addActionListener(e -> handleEdit());
//        btnDelete.addActionListener(e -> handleDelete());
//        btnRefresh.addActionListener(e -> handleRefresh());
//    }
//    private void handleAdd() {
//        kkBus.refreshData();
//        cboKeKho.removeAllItems();
//
//        // Chỉ hiển thị kệ còn chỗ trống
//        for (KeKho kk : kkBus.getListKeConTrong()) {
//            cboKeKho.addItem(kk.getMaKe());
//        }
//
//        if (cboKeKho.getItemCount() == 0) {
//            JOptionPane.showMessageDialog(this,
//                    "Tất cả kệ đã đầy! Vui lòng thêm kệ mới trước khi nhập hàng.",
//                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
//        }
//
//        clearForm();
//        txtMaSP.setText(taoMaSPMoi());
//        txtMaSP.setEditable(false);
//        txtMaSP.setBackground(new Color(245, 247, 250));
//        txtMaSP.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
//        txtMaSP.setForeground(new Color(100, 110, 130));
//
//        showFormPanel();
//    }
//
//    /** Lấy số lớn nhất trong danh sách mã SP rồi cộng 1, định dạng SPxx */
//    private String taoMaSPMoi() {
//        int maxSo = 0;
//        for (SanPham sp : spBus.getAll()) {
//            String ma = sp.getMaSP().toUpperCase();
//            if (ma.startsWith("SP")) {
//                try {
//                    int so = Integer.parseInt(ma.substring(2));
//                    if (so > maxSo) maxSo = so;
//                } catch (NumberFormatException ignored) {}
//            }
//        }
//        return String.format("SP%02d", maxSo + 1);
//    }
//    private void handleEdit() {
//        int row = table.getSelectedRow();
//        if (row == -1) {
//            JOptionPane.showMessageDialog(this, "Chọn sản phẩm cần sửa");
//            return;
//        }
//
//        lblFormTitle.setText("SỬA SẢN PHẨM");
//        txtMaSP.setText(model.getValueAt(row, 1).toString());
//        txtTenSP.setText(model.getValueAt(row, 2).toString());
//        cboDVT.setSelectedItem(model.getValueAt(row, 3).toString());
//        txtSoLuong.setText(model.getValueAt(row, 4).toString());
//        String gia = model.getValueAt(row, 5).toString().replace("đ","").replace(",","");
//        txtGia.setText(gia);
//
//        // Khi sửa, chỉ hiển thị kệ còn chỗ trống + kệ hiện tại của sản phẩm
//        String maKeHienTai = model.getValueAt(row, 6).toString();
//        cboKeKho.removeAllItems();
//
//        // Thêm kệ hiện tại trước (nếu nó đã đầy vẫn cho chọn)
//        if (maKeHienTai != null && !maKeHienTai.equals("Chưa có")) {
//            cboKeKho.addItem(maKeHienTai);
//        }
//
//        // Thêm các kệ còn trống
//        for (KeKho kk : kkBus.getListKeConTrong()) {
//            if (!kk.getMaKe().equals(maKeHienTai)) {
//                cboKeKho.addItem(kk.getMaKe());
//            }
//        }
//
//        cboKeKho.setSelectedItem(maKeHienTai);
//        cboKeKho.setEnabled(true);
//        txtMaSP.setEditable(false);
//        txtMaSP.setBackground(new Color(245, 247, 250));
//        txtMaSP.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
//        txtMaSP.setForeground(new Color(100, 110, 130));
//        txtSoLuong.setEditable(false);
//        showFormPanel();
//    }
//    private void handleDelete() {
//        int row = table.getSelectedRow();
//        if (row == -1) {
//            JOptionPane.showMessageDialog(this, "Chọn sản phẩm cần xóa");
//            return;
//        }
//
//        String maSP = model.getValueAt(row, 1).toString();
//
//        int confirm = JOptionPane.showConfirmDialog(
//                this,
//                "Bạn có chắc muốn xóa?",
//                "Xác nhận",
//                JOptionPane.YES_NO_OPTION
//        );
//
//        if (confirm != JOptionPane.YES_OPTION) return;
//
//        String msg = spBus.deleteSanPham(maSP);
//        JOptionPane.showMessageDialog(this, msg);
//        loadTableData();
//    }
//    private void handleRefresh() {
//
//        loadTableData();   // load lại bảng
//
//        clearForm();       // reset form
//
//        hideFormPanel();   // ẩn panel thêm/sửa
//
//        table.clearSelection(); // bỏ chọn dòng
//    }
//    private void saveSanPham() {
//        String ma  = txtMaSP.getText().trim();
//        String ten = txtTenSP.getText().trim();
//        String dvt = cboDVT.getSelectedItem() != null ? cboDVT.getSelectedItem().toString() : "Chai";
//        String maKe = cboKeKho.getSelectedItem() != null ? cboKeKho.getSelectedItem().toString() : "";
//
//        if (ten.isEmpty()) {
//            JOptionPane.showMessageDialog(this, "Tên sản phẩm không được để trống!");
//            return;
//        }
//
//        if (maKe.isEmpty()) {
//            JOptionPane.showMessageDialog(this, "Vui lòng chọn kệ kho!");
//            return;
//        }
//
//        int sl;
//        float gia;
//        try {
//            sl = Integer.parseInt(txtSoLuong.getText().trim());
//            if (sl < 0) {
//                JOptionPane.showMessageDialog(this, "Số lượng không thể âm!");
//                return;
//            }
//        } catch (Exception e) {
//            JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ");
//            return;
//        }
//        try {
//            gia = Float.parseFloat(txtGia.getText().trim());
//            if (gia < 0) {
//                JOptionPane.showMessageDialog(this, "Giá nhập không thể âm!");
//                return;
//            }
//        } catch (Exception e) {
//            JOptionPane.showMessageDialog(this, "Giá nhập không hợp lệ");
//            return;
//        }
//
//        SanPham sp = new SanPham();
//        sp.setMaSP(ma);
//        sp.setTenSP(ten);
//        sp.setDonViTinh(dvt);
//        sp.setGiaTien(gia);
//        sp.setMaKe(maKe);
//
//        String msg;
//        if (lblFormTitle.getText().equals("THÊM SẢN PHẨM")) {
//            msg = spBus.addSanPham(sp, sl);
//        } else {
//            msg = spBus.updateSanPham(sp, sl);
//        }
//
//        JOptionPane.showMessageDialog(this, msg);
//
//        if (msg.toLowerCase().contains("thành công")) {
//            loadTableData();
//            clearForm();
//            hideFormPanel();
//            kkBus.refreshData();
//        }
//    }
//
//    private void clearForm() {
//        txtMaSP.setText(""); txtTenSP.setText("");
//        txtSoLuong.setText("0"); txtGia.setText("");
//        if (cboDVT != null) cboDVT.setSelectedIndex(0);
//        if (cboKeKho != null && cboKeKho.getItemCount() > 0) { cboKeKho.setSelectedIndex(0); cboKeKho.setEnabled(true); }
//        // Reset style txtMaSP về mặc định
//        txtMaSP.setEditable(false);
//        txtMaSP.setBackground(new Color(245, 247, 250));
//        txtMaSP.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
//        txtMaSP.setForeground(new Color(100, 110, 130));
//        txtSoLuong.setEditable(false);
//        lblFormTitle.setText("THÊM SẢN PHẨM");
//    }
//
//    private void hideFormPanel() {
//        panelForm.setVisible(false);
//    }
//
//    private void xuatExcel() {
//        JFileChooser fileChooser = new JFileChooser();
//        fileChooser.setDialogTitle("Lưu file Excel");
//        fileChooser.setSelectedFile(new java.io.File("DanhSachSanPham.xlsx"));
//        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel file (*.xlsx)", "xlsx"));
//
//        if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
//
//        java.io.File file = fileChooser.getSelectedFile();
//        if (!file.getName().endsWith(".xlsx"))
//            file = new java.io.File(file.getAbsolutePath() + ".xlsx");
//
//        try (org.apache.poi.xssf.usermodel.XSSFWorkbook workbook =
//                     new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
//
//            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Danh sách sản phẩm");
//
//            // Style tiêu đề
//            org.apache.poi.ss.usermodel.CellStyle headerStyle = workbook.createCellStyle();
//            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
//            headerFont.setBold(true);
//            headerFont.setFontHeightInPoints((short) 12);
//            headerStyle.setFont(headerFont);
//            headerStyle.setFillForegroundColor(new org.apache.poi.xssf.usermodel.XSSFColor(
//                    new byte[]{(byte) 200, (byte) 220, (byte) 240}, null));
//            headerStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
//            headerStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
//            headerStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
//            headerStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
//            headerStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
//            headerStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);
//
//            // Style dữ liệu
//            org.apache.poi.ss.usermodel.CellStyle dataStyle = workbook.createCellStyle();
//            dataStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
//            dataStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
//            dataStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
//            dataStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
//            dataStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);
//
//            // Dòng tiêu đề — khớp với cột bảng (bỏ cột Trạng thái nếu muốn, ở đây giữ đủ)
//            String[] cols = {"STT", "Mã SP", "Tên SP", "Đơn vị tính", "Số lượng", "Giá nhập", "Mã kệ", "Trạng thái"};
//            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
//            for (int i = 0; i < cols.length; i++) {
//                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
//                cell.setCellValue(cols[i]);
//                cell.setCellStyle(headerStyle);
//            }
//
//            // Ghi dữ liệu từ model
//            for (int r = 0; r < model.getRowCount(); r++) {
//                org.apache.poi.ss.usermodel.Row row = sheet.createRow(r + 1);
//                for (int c = 0; c < cols.length; c++) {
//                    org.apache.poi.ss.usermodel.Cell cell = row.createCell(c);
//                    Object val = model.getValueAt(r, c);
//                    cell.setCellValue(val != null ? val.toString() : "");
//                    cell.setCellStyle(dataStyle);
//                }
//            }
//
//            for (int i = 0; i < cols.length; i++) sheet.autoSizeColumn(i);
//
//            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(file)) {
//                workbook.write(fos);
//            }
//
//            JOptionPane.showMessageDialog(this,
//                    "Xuất Excel thành công!\nFile: " + file.getAbsolutePath(),
//                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            JOptionPane.showMessageDialog(this,
//                    "Xuất Excel thất bại: " + ex.getMessage(),
//                    "Lỗi", JOptionPane.ERROR_MESSAGE);
//        }
//    }
//
//
//}