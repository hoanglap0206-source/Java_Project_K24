package GUI;

import BUS.SanPham_BUS;
import Model.SanPham;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.table.*;
import java.util.ArrayList;

public class TrangSanPham extends JPanel implements QuyenTrang {
    private JTable table;
    private DefaultTableModel model;

    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnRefresh;

    private JSplitPane splitPane;
    private JPanel panelForm;
    private JPanel panelTableWrapper;
    private JLabel lblFormTitle;

    private JTextField txtMaSP;
    private JTextField txtTenSP;
    private JTextField txtDVT;
    private JTextField txtSoLuong;
    private JTextField txtGia;

    // === SEARCH ===
    private JTextField txtSearch;
    private Timer searchTimer;
    // Lưu toàn bộ dữ liệu gốc để lọc mà không cần reload DB mỗi lần
    private ArrayList<Object[]> allRows = new ArrayList<>();

    private SanPham_BUS spBus = new SanPham_BUS();

    public TrangSanPham() {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 255, 255));

        add(taoThanhCongCu(), BorderLayout.NORTH);
        add(taoNoiDung(), BorderLayout.CENTER);
        loadTableData();

        // Timer debounce 400ms — gõ xong mới lọc, không lọc từng ký tự
        searchTimer = new Timer(400, e -> applyFilter());
        searchTimer.setRepeats(false);

        // DocumentListener: lắng nghe thay đổi trong ô tìm kiếm
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { restartTimer(); }
            @Override public void removeUpdate(DocumentEvent e) { restartTimer(); }
            @Override public void changedUpdate(DocumentEvent e) { }
        });

        addEvents();
    }

    private JPanel taoThanhCongCu() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(new EmptyBorder(15, 20, 5, 20));

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(4, 10, 4, 10));

        // ===== Ô TÌM KIẾM =====
        txtSearch = new JTextField("Tìm kiếm...");
        txtSearch.setColumns(15);
        txtSearch.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        txtSearch.setForeground(Color.GRAY);

        JPanel pnlSearchInput = new JPanel(new BorderLayout());
        pnlSearchInput.setBackground(Color.WHITE);
        pnlSearchInput.setPreferredSize(new Dimension(260, 30));
        pnlSearchInput.setBorder(new CompoundBorder(
                new LineBorder(new Color(198, 226, 255), 2, true),
                new EmptyBorder(0, 2, 0, 0)
        ));

        // Nút 🔍 — click thì tìm ngay lập tức (không chờ debounce)
        JButton btnSearchIcon = new JButton("🔍");
        btnSearchIcon.setBackground(new Color(214, 238, 253));
        btnSearchIcon.setBorderPainted(false);
        btnSearchIcon.setFocusPainted(false);
        btnSearchIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSearchIcon.addActionListener(e -> applyFilter());

        pnlSearchInput.add(txtSearch, BorderLayout.CENTER);
        pnlSearchInput.add(btnSearchIcon, BorderLayout.EAST);

        // Placeholder focus
        txtSearch.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtSearch.getText().equals("Tìm kiếm...")) {
                    txtSearch.setText("");
                    txtSearch.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (txtSearch.getText().isEmpty()) {
                    txtSearch.setText("Tìm kiếm...");
                    txtSearch.setForeground(Color.GRAY);
                }
            }
        });

        // Combobox Lọc (giữ nguyên như cũ)
        String[] itemLoc = {"Lọc", "1", "2", "3", "4", "5"};
        JComboBox<String> comboBoxLoc = new JComboBox<>(itemLoc);
        comboBoxLoc.setBackground(new Color(214, 238, 253));
        comboBoxLoc.setPreferredSize(new Dimension(90, 30));
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

        // Các nút
        btnEdit = new JButton("Chỉnh sửa");
        Style.styleButton(btnEdit);
        btnDelete = new JButton("Xóa");
        Style.styleButton(btnDelete);
        btnAdd = new JButton("+ Thêm");
        Style.styleButton(btnAdd);
        btnRefresh = new JButton("↻ Làm mới");
        Style.styleButton(btnRefresh);
        JButton btnExcel = new JButton("Xuất excel");
        Style.styleButton(btnExcel);

        panel.add(pnlSearchInput);
        panel.add(btnRefresh);
        panel.add(comboBoxLoc);
        panel.add(btnEdit);
        panel.add(btnDelete);
        panel.add(btnAdd);
        panel.add(btnExcel);

        wrapper.add(panel, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel taoNoiDung() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 25, 20, 25));

        panel.add(taoTieuDe(), BorderLayout.NORTH);

        panelTableWrapper = new JPanel(new BorderLayout());
        panelTableWrapper.add(taoBang(), BorderLayout.CENTER);

        panelForm = taoPanelForm();
        panelForm.setVisible(false);

        splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                panelTableWrapper,
                panelForm
        );
        splitPane.setResizeWeight(1.0);
        splitPane.setDividerSize(6);
        splitPane.setBorder(null);

        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel taoPanelForm() {
        JPanel pnl = new JPanel();
        pnl.setPreferredSize(new Dimension(320, 0));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(new EmptyBorder(20, 20, 20, 20));
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));

        lblFormTitle = new JLabel("THÊM SẢN PHẨM");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFormTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        pnl.add(lblFormTitle);
        pnl.add(Box.createVerticalStrut(20));

        txtMaSP = new JTextField();
        txtTenSP = new JTextField();
        txtDVT = new JTextField();
        txtSoLuong = new JTextField();
        txtSoLuong.setText("0");
        txtSoLuong.setEditable(false);
        txtGia = new JTextField();

        pnl.add(new JLabel("Mã SP"));
        pnl.add(txtMaSP);
        pnl.add(Box.createVerticalStrut(10));
        pnl.add(new JLabel("Tên SP"));
        pnl.add(txtTenSP);
        pnl.add(Box.createVerticalStrut(10));
        pnl.add(new JLabel("Đơn vị tính"));
        pnl.add(txtDVT);
        pnl.add(Box.createVerticalStrut(10));
        pnl.add(new JLabel("Số lượng"));
        pnl.add(txtSoLuong);
        pnl.add(Box.createVerticalStrut(10));
        pnl.add(new JLabel("Giá nhập"));
        pnl.add(txtGia);
        pnl.add(Box.createVerticalStrut(20));

        JButton btnSave = new JButton("Lưu");
        JButton btnCancel = new JButton("Hủy");

        btnSave.addActionListener(e -> saveSanPham());
        btnCancel.addActionListener(e -> hideFormPanel());

        JPanel pnlBtn = new JPanel();
        pnlBtn.add(btnSave);
        pnlBtn.add(btnCancel);

        pnl.add(pnlBtn);
        return pnl;
    }

    private void showFormPanel() {
        panelForm.setVisible(true);
        splitPane.setDividerLocation(getWidth() - 320);
    }

    private JPanel taoTieuDe() {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnl.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("DANH SÁCH SẢN PHẨM");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.BLACK);

        pnl.add(lblTitle);
        return pnl;
    }

    private JScrollPane taoBang() {
        String[] columns = {
                "STT", "Mã SP", "Tên SP", "Đơn vị tính",
                "Số lượng", "Giá nhập", "Mã kệ", "Trạng thái"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(210, 230, 255));
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < columns.length; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Render cột trạng thái
        table.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                if (value != null) {
                    if (value.toString().equals("Còn hàng")) {
                        lbl.setForeground(new Color(0, 128, 0));
                    } else {
                        lbl.setForeground(Color.RED);
                    }
                }
                return lbl;
            }
        });

        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(80);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        table.getColumnModel().getColumn(5).setPreferredWidth(120);
        table.getColumnModel().getColumn(6).setPreferredWidth(60);
        table.getColumnModel().getColumn(7).setPreferredWidth(90);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(new Color(180, 180, 180)));
        return scrollPane;
    }

    // ==================== LOAD DỮ LIỆU ====================

    private void loadTableData() {
        if (model == null) return;
        model.setRowCount(0);
        allRows.clear(); // Xóa cache cũ

        int stt = 1;
        java.text.DecimalFormat df = new java.text.DecimalFormat("#,###đ");

        for (SanPham sp : spBus.getAll()) {
            String trangThai = (sp.getSoLuong() > 0) ? "Còn hàng" : "Hết hàng";
            String maKe = (sp.getKeKho() != null) ? sp.getKeKho().getMaKe() : "Chưa có";

            Object[] row = {
                    stt++,
                    sp.getMaSP(),
                    sp.getTenSP(),
                    sp.getDonViTinh(),
                    sp.getSoLuong(),
                    df.format(sp.getGiaTien()),
                    maKe,
                    trangThai
            };
            allRows.add(row);   // Lưu vào cache để lọc
            model.addRow(row);
        }
    }

    // ==================== TÌM KIẾM / LỌC ====================

    /**
     * Lọc bảng theo từ khoá trong txtSearch.
     * Tìm kiếm trên: STT, Mã SP, Tên SP, Đơn vị tính, Mã kệ, Trạng thái
     * — không reload DB, dùng allRows đã cache sẵn.
     */
    private void applyFilter() {
        String keyword = txtSearch.getText().trim();
        // Bỏ qua nếu đang hiển thị placeholder
        if (keyword.equalsIgnoreCase("Tìm kiếm...")) keyword = "";

        model.setRowCount(0);
        int counter = 1;

        for (Object[] row : allRows) {
            if (!keyword.isEmpty()) {
                String kw = keyword.toLowerCase();
                // So khớp với STT, Mã SP, Tên SP, DVT, Mã kệ, Trạng thái
                boolean match = row[0].toString().contains(kw)          // STT
                        || row[1].toString().toLowerCase().contains(kw) // Mã SP
                        || row[2].toString().toLowerCase().contains(kw) // Tên SP
                        || row[3].toString().toLowerCase().contains(kw) // DVT
                        || row[6].toString().toLowerCase().contains(kw) // Mã kệ
                        || row[7].toString().toLowerCase().contains(kw);// Trạng thái
                if (!match) continue;
            }

            // Cập nhật lại STT theo thứ tự hiển thị (giống TrangPhieuNhap)
            Object[] displayRow = row.clone();
            displayRow[0] = counter++;
            model.addRow(displayRow);
        }
    }

    /** Khởi động lại timer debounce mỗi khi người dùng gõ */
    private void restartTimer() {
        if (searchTimer.isRunning()) searchTimer.restart();
        else searchTimer.start();
    }

    // ==================== SỰ KIỆN NÚT ====================

    private void addEvents() {
        btnAdd.addActionListener(e -> handleAdd());
        btnEdit.addActionListener(e -> handleEdit());
        btnDelete.addActionListener(e -> handleDelete());
        btnRefresh.addActionListener(e -> handleRefresh());
    }

    private void handleAdd() {
        clearForm();
        showFormPanel();
    }

    private void handleEdit() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chọn sản phẩm cần sửa");
            return;
        }

        lblFormTitle.setText("SỬA SẢN PHẨM");
        txtMaSP.setText(model.getValueAt(row, 1).toString());
        txtTenSP.setText(model.getValueAt(row, 2).toString());
        txtDVT.setText(model.getValueAt(row, 3).toString());
        txtSoLuong.setText(model.getValueAt(row, 4).toString());

        String gia = model.getValueAt(row, 5).toString()
                .replace("đ", "")
                .replace(",", "");
        txtGia.setText(gia);

        txtMaSP.setEditable(false);
        txtSoLuong.setEditable(false);

        showFormPanel();
    }

    private void handleDelete() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chọn sản phẩm cần xóa");
            return;
        }

        String maSP = model.getValueAt(row, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(
                this, "Bạn có chắc muốn xóa?", "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        String msg = spBus.deleteSanPham(maSP);
        JOptionPane.showMessageDialog(this, msg);
        loadTableData();
    }

    private void handleRefresh() {
        // Reset ô tìm kiếm về placeholder
        txtSearch.setText("Tìm kiếm...");
        txtSearch.setForeground(Color.GRAY);

        spBus.refeshdata();   // Reload từ DB
        loadTableData();      // Reload bảng + cache allRows
        clearForm();
        hideFormPanel();
        table.clearSelection();
    }

    private void saveSanPham() {
        String ma = txtMaSP.getText().trim();
        String ten = txtTenSP.getText().trim();
        String dvt = txtDVT.getText().trim();

        int sl;
        float gia;
        try {
            sl = Integer.parseInt(txtSoLuong.getText().trim());
            gia = Float.parseFloat(txtGia.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Số lượng hoặc giá không hợp lệ");
            return;
        }

        SanPham sp = new SanPham();
        sp.setMaSP(ma);
        sp.setTenSP(ten);
        sp.setDonViTinh(dvt);
        sp.setSoLuong(sl);
        sp.setGiaTien(gia);

        String msg;
        if (lblFormTitle.getText().equals("THÊM SẢN PHẨM")) {
            msg = spBus.addSanPham(sp);
        } else {
            msg = spBus.updateSanPham(sp);
        }
        JOptionPane.showMessageDialog(this, msg);

        loadTableData();
        clearForm();
        hideFormPanel();
    }

    private void clearForm() {
        txtMaSP.setText("");
        txtTenSP.setText("");
        txtDVT.setText("");
        txtSoLuong.setText("0");
        txtGia.setText("");
        txtMaSP.setEditable(true);
        txtSoLuong.setEditable(false);
        lblFormTitle.setText("THÊM SẢN PHẨM");
    }

    private void hideFormPanel() {
        panelForm.setVisible(false);
    }
    @Override
    public void apDungQuyen(boolean coQuyen_Xem, boolean coQuyen_Them,
                            boolean coQuyen_Sua, boolean coQuyen_Xoa) {
        btnAdd.setVisible(coQuyen_Them);
        btnEdit.setVisible(coQuyen_Sua);
        btnDelete.setVisible(coQuyen_Xoa);
    }
}