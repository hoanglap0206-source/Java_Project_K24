package GUI;

import Model.NhanVien;
import BUS.NV_BUS;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.table.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TrangQuanLyTaiKhoan extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private NV_BUS nvBUS = new NV_BUS();

    private Set<Integer> visiblePasswordRows = new HashSet<>();
    private ArrayList<Object[]> allRows = new ArrayList<>();

    private JTextField txtSearch;
    private Timer searchTimer;

    private JSplitPane splitPane;
    private JPanel panelForm;
    private JLabel lblFormTitle;

    private JTextField txtMaNV, txtHoTen, txtSDT;
    private JPasswordField txtMatKhau;
    private JComboBox<String> cboChucVu, cboTrangThai;
    private JButton btnAdd, btnEdit, btnDelete, btnRefresh;

    public TrangQuanLyTaiKhoan() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        add(taoThanhCongCu(), BorderLayout.NORTH);
        add(taoNoiDung(), BorderLayout.CENTER);
        loadDataFromBUS();

        searchTimer = new Timer(400, e -> applyFilter());
        searchTimer.setRepeats(false);
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { restartTimer(); }
            @Override public void removeUpdate(DocumentEvent e) { restartTimer(); }
            @Override public void changedUpdate(DocumentEvent e) {}
        });
        addEvents();
    }

    // ==================== THANH CÔNG CỤ ====================

    private JPanel taoThanhCongCu() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(new EmptyBorder(15, 20, 5, 20));

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(4, 10, 4, 10));

        txtSearch = new JTextField("Tìm kiếm...");
        txtSearch.setColumns(15);
        txtSearch.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        txtSearch.setForeground(Color.GRAY);
        txtSearch.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (txtSearch.getText().equals("Tìm kiếm...")) { txtSearch.setText(""); txtSearch.setForeground(Color.BLACK); }
            }
            @Override public void focusLost(FocusEvent e) {
                if (txtSearch.getText().isEmpty()) { txtSearch.setText("Tìm kiếm..."); txtSearch.setForeground(Color.GRAY); }
            }
        });

        JButton btnSearchIcon = new JButton("🔍");
        btnSearchIcon.setBackground(new Color(214, 238, 253));
        btnSearchIcon.setBorderPainted(false); btnSearchIcon.setFocusPainted(false);
        btnSearchIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSearchIcon.addActionListener(e -> applyFilter());

        JPanel pnlSearch = new JPanel(new BorderLayout());
        pnlSearch.setBackground(Color.WHITE);
        pnlSearch.setPreferredSize(new Dimension(260, 30));
        pnlSearch.setBorder(new CompoundBorder(new LineBorder(new Color(198, 226, 255), 2, true), new EmptyBorder(0, 2, 0, 0)));
        pnlSearch.add(txtSearch, BorderLayout.CENTER);
        pnlSearch.add(btnSearchIcon, BorderLayout.EAST);

        btnRefresh = new JButton("↻ Làm mới"); Style.styleButton(btnRefresh);
        btnEdit    = new JButton("Chỉnh sửa"); Style.styleButton(btnEdit);
        btnDelete  = new JButton("Xóa");       Style.styleButton(btnDelete);
        btnAdd     = new JButton("+ Thêm");    Style.styleButton(btnAdd);
        JButton btnExcel = new JButton("Xuất excel"); Style.styleButton(btnExcel);
        btnExcel.addActionListener(e -> xuatExcel());

        panel.add(pnlSearch); panel.add(btnRefresh);
        panel.add(btnEdit); panel.add(btnDelete); panel.add(btnAdd); panel.add(btnExcel);
        wrapper.add(panel, BorderLayout.CENTER);
        return wrapper;
    }

    // ==================== NỘI DUNG ====================

    private JPanel taoNoiDung() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 25, 20, 25));

        JPanel pnlTitle = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlTitle.setBackground(Color.WHITE);
        JLabel lbl = new JLabel("DANH SÁCH TÀI KHOẢN NHÂN VIÊN");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnlTitle.add(lbl);
        panel.add(pnlTitle, BorderLayout.NORTH);

        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.add(taoBang(), BorderLayout.CENTER);

        panelForm = taoPanelForm();
        panelForm.setVisible(false);

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableWrapper, panelForm);
        splitPane.setResizeWeight(1.0); splitPane.setDividerSize(6); splitPane.setBorder(null);
        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }

    // ==================== BẢNG ====================

    private JScrollPane taoBang() {
        String[] cols = {"STT", "Họ tên", "Số điện thoại", "Username", "Vai trò", "Trạng thái", "Mật khẩu", "Hiện mật khẩu"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return col == 7; }
        };

        table = new JTable(model);
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(210, 230, 255));
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < cols.length; i++) table.getColumnModel().getColumn(i).setCellRenderer(center);

        // Render trạng thái màu (cột 5)
        table.getColumnModel().getColumn(5).setCellRenderer((t, val, sel, foc, row, col) -> {
            JLabel l = new JLabel(val != null ? val.toString() : "");
            l.setHorizontalAlignment(SwingConstants.CENTER); l.setOpaque(true);
            l.setBackground(sel ? t.getSelectionBackground() : t.getBackground());
            l.setForeground("Active".equals(val) ? new Color(0, 150, 0) : Color.RED);
            return l;
        });

        // Render mật khẩu ẩn/hiện (cột 6)
        table.getColumnModel().getColumn(6).setCellRenderer((t, val, sel, foc, row, col) -> {
            JLabel l = new JLabel(); l.setHorizontalAlignment(SwingConstants.CENTER); l.setOpaque(true);
            l.setBackground(sel ? t.getSelectionBackground() : t.getBackground());
            if (val != null) {
                if (visiblePasswordRows.contains(row)) { l.setText(val.toString()); l.setForeground(new Color(30, 80, 160)); }
                else { l.setText("••••••••"); l.setForeground(Color.GRAY); }
            }
            return l;
        });

        // Nút Xem/Ẩn mật khẩu (cột 7)
        table.getColumnModel().getColumn(7).setCellRenderer(new TogglePwRenderer());
        table.getColumnModel().getColumn(7).setCellEditor(new TogglePwEditor());
        table.getColumnModel().getColumn(7).setPreferredWidth(110);
        table.getColumnModel().getColumn(7).setMinWidth(110);
        table.getColumnModel().getColumn(7).setMaxWidth(110);

        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(6).setPreferredWidth(120);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(new LineBorder(new Color(180, 180, 180)));
        return sp;
    }

    // Helper tạo nút Xem/Ẩn với màu tương ứng
    private void styleToggleBtn(JButton btn, boolean visible) {
        btn.setText(visible ? "Ẩn" : "Xem");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false); btn.setBorderPainted(false); btn.setOpaque(true);
        btn.setBackground(visible ? new Color(255, 210, 210) : new Color(214, 238, 253));
        btn.setForeground(visible ? new Color(180, 0, 0) : new Color(30, 80, 160));
    }

    class TogglePwRenderer extends JButton implements TableCellRenderer {
        public TogglePwRenderer() { setFocusPainted(false); setBorderPainted(false); setOpaque(true); }
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
            styleToggleBtn(this, visiblePasswordRows.contains(row)); return this;
        }
    }

    class TogglePwEditor extends AbstractCellEditor implements TableCellEditor {
        private JButton btn = new JButton();
        private int currentRow;
        public TogglePwEditor() {
            btn.addActionListener(e -> {
                if (visiblePasswordRows.contains(currentRow)) visiblePasswordRows.remove(currentRow);
                else visiblePasswordRows.add(currentRow);
                fireEditingStopped(); table.repaint();
            });
        }
        @Override public Component getTableCellEditorComponent(JTable t, Object v, boolean sel, int row, int col) {
            currentRow = row; styleToggleBtn(btn, visiblePasswordRows.contains(row)); return btn;
        }
        @Override public Object getCellEditorValue() { return ""; }
    }

    // ==================== FORM THÊM / SỬA ====================

    private JPanel taoPanelForm() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setPreferredSize(new Dimension(340, 0));
        outer.setBackground(new Color(245, 247, 250));
        outer.setBorder(new MatteBorder(0, 1, 0, 0, new Color(210, 220, 235)));

        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setBackground(new Color(245, 247, 250));
        pnl.setBorder(new EmptyBorder(30, 24, 24, 24));

        lblFormTitle = new JLabel("THÊM TÀI KHOẢN");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblFormTitle.setForeground(new Color(30, 80, 160));
        lblFormTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(198, 220, 255));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        pnl.add(lblFormTitle); pnl.add(Box.createVerticalStrut(8));
        pnl.add(sep); pnl.add(Box.createVerticalStrut(20));

        txtMaNV    = new JTextField();
        txtHoTen   = new JTextField();
        txtSDT     = new JTextField();
        txtMatKhau = new JPasswordField();
        cboChucVu    = new JComboBox<>(new String[]{"Admin", "QuanLy", "ThuKho", "BanHang"});
        cboTrangThai = new JComboBox<>(new String[]{"HoatDong", "BiKhoa"});

        pnl.add(nhomField("Mã nhân viên", txtMaNV));    pnl.add(Box.createVerticalStrut(10));
        pnl.add(nhomField("Họ và tên", txtHoTen));      pnl.add(Box.createVerticalStrut(10));
        pnl.add(nhomField("Số điện thoại", txtSDT));    pnl.add(Box.createVerticalStrut(10));
        pnl.add(nhomPasswordField());                    pnl.add(Box.createVerticalStrut(10));
        pnl.add(nhomCombo("Vai trò", cboChucVu));       pnl.add(Box.createVerticalStrut(10));
        pnl.add(nhomCombo("Trạng thái", cboTrangThai)); pnl.add(Box.createVerticalStrut(24));

        JButton btnSave = new JButton("💾  Lưu");
        btnSave.setBackground(new Color(37, 120, 220)); btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSave.setFocusPainted(false); btnSave.setBorderPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR)); btnSave.setPreferredSize(new Dimension(110, 36));
        btnSave.addActionListener(e -> saveNhanVien());

        JButton btnCancel = new JButton("✕  Hủy");
        btnCancel.setBackground(new Color(220, 225, 235)); btnCancel.setForeground(new Color(60, 60, 60));
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCancel.setFocusPainted(false); btnCancel.setBorderPainted(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR)); btnCancel.setPreferredSize(new Dimension(110, 36));
        btnCancel.addActionListener(e -> hideFormPanel());

        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        pnlBtn.setBackground(new Color(245, 247, 250));
        pnlBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        pnlBtn.add(btnSave); pnlBtn.add(btnCancel);
        pnl.add(pnlBtn);

        JScrollPane formScroll = new JScrollPane(pnl);
        formScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        formScroll.setBorder(null);
        outer.add(formScroll, BorderLayout.CENTER);
        return outer;
    }

    private JPanel nhomField(String label, JTextField field) {
        JPanel g = new JPanel(); g.setLayout(new BoxLayout(g, BoxLayout.Y_AXIS));
        g.setBackground(new Color(245, 247, 250)); g.setAlignmentX(Component.LEFT_ALIGNMENT);
        g.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12)); lbl.setForeground(new Color(80, 100, 130)); lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13)); field.setBackground(Color.WHITE);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34)); field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setBorder(new CompoundBorder(new LineBorder(new Color(198, 218, 245), 1, true), new EmptyBorder(4, 10, 4, 10)));
        g.add(lbl); g.add(Box.createVerticalStrut(4)); g.add(field);
        return g;
    }

    private JPanel nhomPasswordField() {
        JPanel g = new JPanel(); g.setLayout(new BoxLayout(g, BoxLayout.Y_AXIS));
        g.setBackground(new Color(245, 247, 250)); g.setAlignmentX(Component.LEFT_ALIGNMENT);
        g.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        JLabel lbl = new JLabel("Mật khẩu");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12)); lbl.setForeground(new Color(80, 100, 130)); lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtMatKhau.setFont(new Font("Segoe UI", Font.PLAIN, 13)); txtMatKhau.setBackground(Color.WHITE);
        txtMatKhau.setBorder(new CompoundBorder(new LineBorder(new Color(198, 218, 245), 1, true), new EmptyBorder(4, 10, 4, 10)));
        JButton btnToggle = new JButton("Xem");
        btnToggle.setFocusPainted(false); btnToggle.setBorderPainted(false);
        btnToggle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnToggle.setBackground(new Color(214, 238, 253)); btnToggle.setPreferredSize(new Dimension(60, 34));
        btnToggle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnToggle.addActionListener(e -> {
            if (txtMatKhau.getEchoChar() == 0) { txtMatKhau.setEchoChar('•'); btnToggle.setText("Xem"); btnToggle.setBackground(new Color(214, 238, 253)); btnToggle.setForeground(new Color(30, 80, 160)); }
            else { txtMatKhau.setEchoChar((char) 0); btnToggle.setText("Ẩn"); btnToggle.setBackground(new Color(255, 210, 210)); btnToggle.setForeground(new Color(180, 0, 0)); }
        });
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(new Color(245, 247, 250)); row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        row.add(txtMatKhau, BorderLayout.CENTER); row.add(btnToggle, BorderLayout.EAST);
        g.add(lbl); g.add(Box.createVerticalStrut(4)); g.add(row);
        return g;
    }

    private JPanel nhomCombo(String label, JComboBox<String> combo) {
        JPanel g = new JPanel(); g.setLayout(new BoxLayout(g, BoxLayout.Y_AXIS));
        g.setBackground(new Color(245, 247, 250)); g.setAlignmentX(Component.LEFT_ALIGNMENT);
        g.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12)); lbl.setForeground(new Color(80, 100, 130)); lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13)); combo.setBackground(Color.WHITE);
        combo.setBorder(new LineBorder(new Color(198, 218, 245), 1, true));
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34)); combo.setAlignmentX(Component.LEFT_ALIGNMENT);
        g.add(lbl); g.add(Box.createVerticalStrut(4)); g.add(combo);
        return g;
    }

    private void showFormPanel() { panelForm.setVisible(true); splitPane.setDividerLocation(getWidth() - 340); }
    private void hideFormPanel() { panelForm.setVisible(false); }

    // ==================== LOAD / TÌM KIẾM ====================

    private void loadDataFromBUS() {
        model.setRowCount(0); allRows.clear(); visiblePasswordRows.clear();
        int stt = 1;
        for (NhanVien nv : nvBUS.getAll()) {
            String status = "HoatDong".equalsIgnoreCase(nv.getTrangThai()) ? "Active" : "Banned";
            Object[] row = { stt++, nv.getHoTen(), nv.getSDT(), nv.getMaNV(), nv.getChucVu(), status, nv.getMatKhau(), "" };
            allRows.add(row); model.addRow(row);
        }
    }

    private void applyFilter() {
        String kw = txtSearch.getText().trim();
        if (kw.equalsIgnoreCase("Tìm kiếm...")) kw = "";
        visiblePasswordRows.clear(); model.setRowCount(0); int counter = 1;
        for (Object[] row : allRows) {
            if (!kw.isEmpty()) {
                String k = kw.toLowerCase();
                boolean match = row[1].toString().toLowerCase().contains(k) || row[2].toString().toLowerCase().contains(k)
                        || row[3].toString().toLowerCase().contains(k) || row[4].toString().toLowerCase().contains(k)
                        || row[5].toString().toLowerCase().contains(k);
                if (!match) continue;
            }
            Object[] d = row.clone(); d[0] = counter++; model.addRow(d);
        }
    }

    private void restartTimer() { if (searchTimer.isRunning()) searchTimer.restart(); else searchTimer.start(); }

    // ==================== SỰ KIỆN ====================

    private void addEvents() {
        btnAdd.addActionListener(e -> handleAdd());
        btnEdit.addActionListener(e -> handleEdit());
        btnDelete.addActionListener(e -> handleDelete());
        btnRefresh.addActionListener(e -> handleRefresh());
    }

    private void handleAdd() { clearForm(); lblFormTitle.setText("THÊM TÀI KHOẢN"); txtMaNV.setEditable(true); showFormPanel(); }

    private void handleEdit() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản cần sửa!"); return; }
        lblFormTitle.setText("SỬA TÀI KHOẢN");
        txtMaNV.setText(model.getValueAt(row, 3).toString()); txtMaNV.setEditable(false);
        txtHoTen.setText(model.getValueAt(row, 1).toString());
        txtSDT.setText(model.getValueAt(row, 2).toString());
        txtMatKhau.setText(model.getValueAt(row, 6).toString());
        cboChucVu.setSelectedItem(model.getValueAt(row, 4).toString());
        cboTrangThai.setSelectedItem("Active".equals(model.getValueAt(row, 5).toString()) ? "HoatDong" : "BiKhoa");
        showFormPanel();
    }

    private void handleDelete() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản cần xóa!"); return; }
        String maNV = model.getValueAt(row, 3).toString();
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa tài khoản \"" + model.getValueAt(row, 1) + "\" ?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        JOptionPane.showMessageDialog(this, nvBUS.deleteNV(maNV));
        nvBUS.refesh(); loadDataFromBUS();
    }

    private void handleRefresh() {
        txtSearch.setText("Tìm kiếm..."); txtSearch.setForeground(Color.GRAY);
        nvBUS.refesh(); loadDataFromBUS(); clearForm(); hideFormPanel(); table.clearSelection();
    }

    private void saveNhanVien() {
        String maNV = txtMaNV.getText().trim(), hoTen = txtHoTen.getText().trim();
        String sdt = txtSDT.getText().trim(), matKhau = new String(txtMatKhau.getPassword()).trim();
        if (maNV.isEmpty() || hoTen.isEmpty() || matKhau.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã NV, họ tên và mật khẩu không được để trống!"); return;
        }
        NhanVien nv = new NhanVien();
        nv.setMaNV(maNV); nv.setHoTen(hoTen); nv.setSDT(sdt); nv.setMatKhau(matKhau);
        nv.setChucVu(cboChucVu.getSelectedItem().toString()); nv.setTrangThai(cboTrangThai.getSelectedItem().toString());
        String msg = lblFormTitle.getText().equals("THÊM TÀI KHOẢN") ? nvBUS.addNV(nv) : nvBUS.updateNV(nv);
        JOptionPane.showMessageDialog(this, msg);
        nvBUS.refesh(); loadDataFromBUS(); clearForm(); hideFormPanel();
    }

    private void clearForm() {
        txtMaNV.setText(""); txtHoTen.setText(""); txtSDT.setText("");
        txtMatKhau.setText(""); txtMatKhau.setEchoChar('•');
        cboChucVu.setSelectedIndex(0); cboTrangThai.setSelectedIndex(0);
        txtMaNV.setEditable(true);
    }

    private void xuatExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu file Excel");
        fileChooser.setSelectedFile(new java.io.File("DanhSachTaiKhoan.xlsx"));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel file (*.xlsx)", "xlsx"));

        if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        java.io.File file = fileChooser.getSelectedFile();
        if (!file.getName().endsWith(".xlsx"))
            file = new java.io.File(file.getAbsolutePath() + ".xlsx");

        try (org.apache.poi.xssf.usermodel.XSSFWorkbook workbook =
                     new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {

            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Danh sách tài khoản");

            // Style tiêu đề
            org.apache.poi.ss.usermodel.CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(new org.apache.poi.xssf.usermodel.XSSFColor(
                    new byte[]{(byte) 200, (byte) 220, (byte) 240}, null));
            headerStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
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

            // Tiêu đề — bỏ cột Mật khẩu và cột nút Hiện mật khẩu
            String[] cols = {"STT", "Họ tên", "Số điện thoại", "Username", "Vai trò", "Trạng thái"};
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
            for (int i = 0; i < cols.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(cols[i]);
                cell.setCellStyle(headerStyle);
            }

            // Ghi dữ liệu — chỉ lấy 6 cột đầu (bỏ cột Mật khẩu và nút)
            for (int r = 0; r < model.getRowCount(); r++) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(r + 1);
                for (int c = 0; c < cols.length; c++) {
                    org.apache.poi.ss.usermodel.Cell cell = row.createCell(c);
                    Object val = model.getValueAt(r, c);
                    cell.setCellValue(val != null ? val.toString() : "");
                    cell.setCellStyle(dataStyle);
                }
            }

            for (int i = 0; i < cols.length; i++) sheet.autoSizeColumn(i);

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
}