package GUI;

import BUS.NhomQuyen_BUS;
import BUS.PhanQuyen_BUS;
import Model.ChucNang;
import Model.NhomQuyen;
import Model.NhomQuyenCT;
import Model.NhanVien;
import Model.PhanQuyen;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class TrangPhanQuyen extends JPanel {
    private static final String MODE_NV   = "NV";
    private static final String MODE_NHOM = "NHOM";

    private final PhanQuyen_BUS pqBUS   = new PhanQuyen_BUS();
    private final NhomQuyen_BUS nhomBUS = new NhomQuyen_BUS();

    private String currentMode = MODE_NV;
    private String currentMaNhom = "";
    private boolean isUpdatingTable = false;

    private ArrayList<PhanQuyen>   cachedNV   = new ArrayList<>();
    private ArrayList<NhomQuyenCT> cachedNhom = new ArrayList<>();

    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel lblCount;
    private JButton btnModeNV, btnModeNhom;
    private JButton btnCapTat, btnThuHoi;

    //Card layout
    private CardLayout cardLayout;
    private JPanel     cardPanel;

    //NV mode
    private JTextField txtMaNV;

    // Nhóm quyền mode
    private JComboBox<NhomQuyen> cboNhomQuyen;

    public TrangPhanQuyen() {
        setLayout(new BorderLayout(0, 0));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel centerPanel = new JPanel(new BorderLayout(0, 8));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(taoThanhCongCu(), BorderLayout.NORTH);
        centerPanel.add(taoNoiDung(), BorderLayout.CENTER);

        add(taoModeToggle(), BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        setupTableListener();
        loadDataNV("");
    }

    // 1. THANH TOGGLE CHẾ ĐỘ
    private JPanel taoModeToggle() {
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(new MatteBorder(0, 0, 2, 0, new Color(210, 228, 255)));

        JLabel lbl = new JLabel("Phân quyền theo: ");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(new Color(55, 80, 130));

        btnModeNV   = new JButton("  Mã Nhân Viên");
        btnModeNhom = new JButton("  Nhóm Quyền");
        applyModeStyle(btnModeNV, true);
        applyModeStyle(btnModeNhom, false);

        btnModeNV.addActionListener(e -> switchMode(MODE_NV));
        btnModeNhom.addActionListener(e -> switchMode(MODE_NHOM));

        wrapper.add(lbl);
        wrapper.add(btnModeNV);
        wrapper.add(btnModeNhom);
        return wrapper;
    }

    private void applyModeStyle(JButton btn, boolean active) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setBorder(new CompoundBorder(
                new LineBorder(new Color(100, 150, 220), 2, true),
                new EmptyBorder(6, 18, 6, 18)));
        if (active) {
            btn.setBackground(new Color(37, 120, 220));
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(new Color(235, 243, 255));
            btn.setForeground(new Color(55, 100, 180));
        }
    }

    private void switchMode(String mode) {
        currentMode = mode;
        applyModeStyle(btnModeNV,   MODE_NV.equals(mode));
        applyModeStyle(btnModeNhom, MODE_NHOM.equals(mode));
        cardLayout.show(cardPanel, mode);

        // Cấp / Thu hồi dùng được cho cả 2 mode
        btnCapTat.setVisible(true);
        btnThuHoi.setVisible(true);
        clearTable();

        boolean isNV = MODE_NV.equals(mode);
        if (isNV) {
            currentMaNhom = "";
            loadDataNV(txtMaNV.getText().trim());
        } else {
            NhomQuyen sel = (NhomQuyen) cboNhomQuyen.getSelectedItem();
            if (sel != null) {
                currentMaNhom = sel.getMaNhom();
                loadDataNhom(currentMaNhom);
            }
        }
    }

    // TOOLBAR
    private JPanel taoThanhCongCu() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, new Color(220, 235, 255)),
                new EmptyBorder(8, 10, 8, 10)));

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        panel.setBackground(Color.WHITE);

        JTextField txtSearch = new JTextField();
        txtSearch.setColumns(18);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setForeground(Color.GRAY);
        txtSearch.setText(" Tìm tên chức năng... ");
        txtSearch.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));

        JPanel pnlSearch = new JPanel(new BorderLayout());
        pnlSearch.setBackground(Color.WHITE);
        pnlSearch.setPreferredSize(new Dimension(272, 32));
        pnlSearch.setBorder(new CompoundBorder(
                new LineBorder(new Color(198, 226, 255), 2, true),
                new EmptyBorder(0, 4, 0, 4)));
        pnlSearch.add(txtSearch, BorderLayout.CENTER);

        txtSearch.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (txtSearch.getText().startsWith(" ")) {
                    txtSearch.setText(""); txtSearch.setForeground(Color.BLACK);
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (txtSearch.getText().isEmpty()) {
                    txtSearch.setForeground(Color.GRAY);
                    txtSearch.setText(" Tìm tên chức năng... ");
                    filterTable("");
                }
            }
        });
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            private void doFilter() {
                String kw = txtSearch.getText().trim();
                filterTable(kw.startsWith("Tìm") ? "" : kw);
            }
            @Override public void insertUpdate(DocumentEvent e)  { doFilter(); }
            @Override public void removeUpdate(DocumentEvent e)  { doFilter(); }
            @Override public void changedUpdate(DocumentEvent e) { doFilter(); }
        });

        JButton btnLamMoi = makeBtn(" Làm mới ", new Color(214, 238, 253), new Color(180, 215, 245));
        btnLamMoi.addActionListener(e -> {
            txtSearch.setText(" Tìm tên chức năng... ");
            txtSearch.setForeground(Color.GRAY);
            if (MODE_NV.equals(currentMode)) loadDataNV(txtMaNV.getText().trim());
            else if (!currentMaNhom.isEmpty())  loadDataNhom(currentMaNhom);
        });

        btnCapTat = makeBtn(" Cấp tất cả ", new Color(212, 245, 220), new Color(160, 220, 175));
        btnThuHoi = makeBtn(" Thu hồi tất cả ", new Color(255, 220, 220), new Color(240, 175, 175));

        btnCapTat.addActionListener(e -> {
            if (MODE_NV.equals(currentMode)) {
                String maNV = txtMaNV.getText().trim();
                if (maNV.isEmpty()) { warn("Vui lòng nhập Mã nhân viên trước!"); return; }
                if (confirm("Cấp toàn bộ quyền cho nhân viên này?"))
                    setAllPermissionsNV(maNV, true);
            } else {
                if (currentMaNhom.isEmpty()) { warn("Vui lòng chọn nhóm quyền trước!"); return; }
                NhomQuyen sel = (NhomQuyen) cboNhomQuyen.getSelectedItem();
                String ten = sel != null ? sel.getTenNhom() : currentMaNhom;
                if (confirm("Cấp toàn bộ quyền cho nhóm \"" + ten + "\"?"))
                    setAllPermissionsNhom(currentMaNhom, true);
            }
        });
        btnThuHoi.addActionListener(e -> {
            if (MODE_NV.equals(currentMode)) {
                String maNV = txtMaNV.getText().trim();
                if (maNV.isEmpty()) { warn("Vui lòng nhập Mã nhân viên trước!"); return; }
                if (confirm("Thu hồi toàn bộ quyền của nhân viên này?"))
                    setAllPermissionsNV(maNV, false);
            } else {
                if (currentMaNhom.isEmpty()) { warn("Vui lòng chọn nhóm quyền trước!"); return; }
                NhomQuyen sel = (NhomQuyen) cboNhomQuyen.getSelectedItem();
                String ten = sel != null ? sel.getTenNhom() : currentMaNhom;
                if (confirm("Thu hồi toàn bộ quyền của nhóm \"" + ten + "\"?"))
                    setAllPermissionsNhom(currentMaNhom, false);
            }
        });

        lblCount = new JLabel("0 chức năng");
        lblCount.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCount.setForeground(new Color(120, 140, 170));
        lblCount.setBorder(new EmptyBorder(0, 6, 0, 0));

        panel.add(pnlSearch); panel.add(btnLamMoi);
        panel.add(makeSep());
        panel.add(btnCapTat); panel.add(btnThuHoi);
        panel.add(makeSep());
        panel.add(lblCount);

        wrapper.add(panel, BorderLayout.CENTER);
        return wrapper;
    }

    //NỘI DUNG
    public JPanel taoNoiDung() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);

        cardLayout = new CardLayout();
        cardPanel  = new JPanel(cardLayout);
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setPreferredSize(new Dimension(0, 46));
        cardPanel.add(taoNVInputPanel(),   MODE_NV);
        cardPanel.add(taoNhomInputPanel(), MODE_NHOM);

        String[] cols = {"Mã CN", "Tên chức năng", "Xem", "Thêm", "Sửa", "Xóa"};
        tableModel = new DefaultTableModel(null, cols) {
            @Override public Class<?> getColumnClass(int c) {
                return c < 2 ? String.class : Boolean.class;
            }
            @Override public boolean isCellEditable(int r, int c) {
                return c >= 2;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(231, 242, 255));
        table.getTableHeader().setForeground(new Color(50, 80, 130));
        table.setGridColor(new Color(235, 240, 250));
        table.setSelectionBackground(new Color(225, 240, 255));

        hideColumn(0);

        table.setDefaultRenderer(Boolean.class, (t, val, sel, foc, row, col) -> {
            boolean checked = val != null && (boolean) val;
            JPanel box = new JPanel(new GridBagLayout());
            box.setBackground(sel ? t.getSelectionBackground() : Color.WHITE);
            JLabel tick = new JLabel(checked ? "v" : "");
            tick.setOpaque(true);
            tick.setPreferredSize(new Dimension(20, 20));
            tick.setHorizontalAlignment(SwingConstants.CENTER);
            tick.setFont(new Font("Segoe UI", Font.BOLD, 13));
            if (checked) {
                tick.setBackground(new Color(60, 145, 230));
                tick.setForeground(Color.WHITE);
                tick.setBorder(new LineBorder(new Color(40, 110, 200), 1, true));
            } else {
                tick.setBackground(Color.WHITE);
                // Border 2 lớp: viền trong xanh nhạt + viền ngoài xanh mờ → hiệu ứng phát sáng nổi
                tick.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(180, 210, 255), 1, true),
                        new LineBorder(new Color(140, 185, 245), 1, true)
                ));
            }
            box.add(tick);
            return box;
        });

        table.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (!sel) c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(247, 251, 255));
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(new Color(200, 215, 235)));
        scroll.getViewport().setBackground(Color.WHITE);

        panel.add(cardPanel, BorderLayout.NORTH);
        panel.add(scroll,    BorderLayout.CENTER);
        return panel;
    }

    // PANEL INPUT MÃ NHÂN VIÊN
    private JPanel taoNVInputPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        panel.setBackground(Color.WHITE);

        JLabel lbl = new JLabel("Mã nhân viên:");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));

        txtMaNV = new JTextField();
        txtMaNV.setPreferredSize(new Dimension(200, 30));
        txtMaNV.setBackground(Color.WHITE);
        txtMaNV.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtMaNV.setBorder(new CompoundBorder(
                new LineBorder(new Color(198, 226, 255), 2, true),
                new EmptyBorder(0, 8, 0, 8)));
        txtMaNV.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    loadDataNV(txtMaNV.getText().trim());
            }
        });

        JButton btnXem = makeBtn("Xem quyền", new Color(214, 238, 253), new Color(170, 210, 245));
        btnXem.addActionListener(e -> loadDataNV(txtMaNV.getText().trim()));

        panel.add(lbl); panel.add(txtMaNV); panel.add(btnXem);
        return panel;
    }

    // 5. PANEL INPUT NHÓM QUYỀN
    private JPanel taoNhomInputPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        panel.setBackground(Color.WHITE);

        JLabel lbl = new JLabel("Nhóm quyền:");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));

        cboNhomQuyen = new JComboBox<>();
        cboNhomQuyen.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboNhomQuyen.setPreferredSize(new Dimension(230, 30));
        cboNhomQuyen.setBackground(Color.WHITE);
        refreshNhomCombo();

        cboNhomQuyen.addActionListener(e -> {
            if (isUpdatingTable) return;
            NhomQuyen sel = (NhomQuyen) cboNhomQuyen.getSelectedItem();
            if (sel != null) {
                currentMaNhom = sel.getMaNhom();
                loadDataNhom(currentMaNhom);
            }
        });

        JButton btnTao = makeBtn("+ Tạo nhóm mới", new Color(212, 245, 220), new Color(155, 215, 170));
        btnTao.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnTao.addActionListener(e -> moDialogTaoNhom());

        JButton btnXoa = makeBtn("x Xóa nhóm", new Color(255, 218, 218), new Color(235, 170, 170));
        btnXoa.addActionListener(e -> {
            NhomQuyen sel = (NhomQuyen) cboNhomQuyen.getSelectedItem();
            if (sel == null) { warn("Chưa chọn nhóm nào!"); return; }
            if (confirm("Xóa nhóm \"" + sel.getTenNhom() + "\" và toàn bộ quyền của nhóm?")) {
                String msg = nhomBUS.deleteNhomQuyen(sel.getMaNhom());
                JOptionPane.showMessageDialog(this, msg);
                if (msg.contains("thành công")) {
                    nhomBUS.refreshData(); refreshNhomCombo(); clearTable();
                }
            }
        });

        panel.add(lbl); panel.add(cboNhomQuyen); panel.add(btnTao);
        panel.add(makeSep()); panel.add(btnXoa);
        return panel;
    }

    // DIALOG TẠO NHÓM QUYỀN MỚI
    private void moDialogTaoNhom() {
        Window ancestor = SwingUtilities.getWindowAncestor(this);
        JDialog dlg = new JDialog(
                ancestor instanceof Frame ? (Frame) ancestor : null,
                "Tạo nhóm quyền mới", true);
        dlg.setLayout(new BorderLayout());
        dlg.getContentPane().setBackground(Color.WHITE);

        // Header
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 14));
        pnlHeader.setBackground(new Color(37, 120, 220));
        JLabel lblTitle = new JLabel("  Tạo nhóm quyền mới");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);
        pnlHeader.add(lblTitle);
        dlg.add(pnlHeader, BorderLayout.NORTH);

        // Body
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(Color.WHITE);
        body.setBorder(new EmptyBorder(16, 20, 10, 20));

        JTextField txtMa  = new JTextField(nhomBUS.getNextMaNhom());
        JTextField txtTen = new JTextField();
        txtTen.setToolTipText("Ví dụ: Quản lý kho, Bán hàng, Admin…");

        body.add(dlgRow("Mã nhóm :", txtMa,  140));
        body.add(Box.createVerticalStrut(6));
        body.add(dlgRow("Tên nhóm:", txtTen, 280));
        body.add(Box.createVerticalStrut(10));

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(200, 220, 240));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        body.add(sep);
        body.add(Box.createVerticalStrut(6));

        JLabel lblTable = new JLabel("Thiết lập quyền cho nhóm:");
        lblTable.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTable.setForeground(new Color(50, 90, 160));
        lblTable.setBorder(new EmptyBorder(0, 0, 6, 0));
        lblTable.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(lblTable);

        // Bảng quyền trong dialog
        String[] cols = {"Mã CN", "Tên chức năng", "Xem", "Thêm", "Sửa", "Xóa"};
        DefaultTableModel dlgModel = new DefaultTableModel(null, cols) {
            @Override public Class<?> getColumnClass(int c) {
                return c < 2 ? String.class : Boolean.class;
            }
            @Override public boolean isCellEditable(int r, int c) {
                return c >= 2;
            }
        };

        // Lấy toàn bộ chức năng với "__NONE__" → LEFT JOIN không khớp → tất cả false
        for (NhomQuyenCT ct : nhomBUS.getBangQuyenCuaNhom("__NONE__")) {
            dlgModel.addRow(new Object[]{
                    ct.getChucNang().getMaCN(),
                    ct.getChucNang().getTenCN(),
                    false, false, false, false
            });
        }

        JTable dlgTable = new JTable(dlgModel);
        dlgTable.setRowHeight(36);
        dlgTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dlgTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        dlgTable.getTableHeader().setBackground(new Color(231, 242, 255));
        dlgTable.getTableHeader().setForeground(new Color(50, 80, 130));
        dlgTable.getColumnModel().getColumn(0).setMinWidth(0);
        dlgTable.getColumnModel().getColumn(0).setMaxWidth(0);
        dlgTable.getColumnModel().getColumn(0).setPreferredWidth(0);

        // Nút chọn/bỏ tất cả
        JButton btnAll  = makeBtn("Chọn tất cả", new Color(212, 245, 220), new Color(155, 215, 170));
        JButton btnNone = makeBtn("Bỏ tất cả",   new Color(255, 218, 218), new Color(235, 170, 170));
        btnAll .addActionListener(e -> {
            for (int r=0;r<dlgModel.getRowCount();r++)
                for(int c=2;c<=5;c++)
                    dlgModel.setValueAt(true,r,c);
        });
        btnNone.addActionListener(e -> {
            for (int r=0;r<dlgModel.getRowCount();r++)
                for(int c=2;c<=5;c++)
                    dlgModel.setValueAt(false,r,c);
        });

        JPanel pnlQ = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        pnlQ.setBackground(Color.WHITE);
        pnlQ.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlQ.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        pnlQ.add(btnAll); pnlQ.add(btnNone);
        body.add(pnlQ);
        body.add(Box.createVerticalStrut(4));

        JScrollPane scrollDlg = new JScrollPane(dlgTable);
        scrollDlg.setPreferredSize(new Dimension(520, 280));
        scrollDlg.setBorder(new LineBorder(new Color(200, 215, 235)));
        JPanel pnlScroll = new JPanel(new BorderLayout());
        pnlScroll.setBackground(Color.WHITE);
        pnlScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        pnlScroll.add(scrollDlg);
        body.add(pnlScroll);
        dlg.add(body, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        footer.setBackground(new Color(245, 248, 255));
        footer.setBorder(new MatteBorder(1, 0, 0, 0, new Color(210, 225, 245)));

        JButton btnLuu = new JButton(" Lưu nhóm");
        btnLuu.setBackground(new Color(37, 120, 220));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLuu.setFocusPainted(false); btnLuu.setBorderPainted(false);
        btnLuu.setPreferredSize(new Dimension(148, 36));
        btnLuu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLuu.setOpaque(true);

        JButton btnHuy = new JButton("x  Hủy");
        btnHuy.setBackground(new Color(220, 225, 235));
        btnHuy.setForeground(new Color(60,60,60));
        btnHuy.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnHuy.setFocusPainted(false); btnHuy.setBorderPainted(false);
        btnHuy.setPreferredSize(new Dimension(100, 36));
        btnHuy.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHuy.setOpaque(true);
        btnHuy.addActionListener(e -> dlg.dispose());

        btnLuu.addActionListener(e -> {
            String maNhom  = txtMa.getText().trim();
            String tenNhom = txtTen.getText().trim();
            if (maNhom.isEmpty())  {
                warnDlg(dlg, "Mã nhóm không được để trống!");
                txtMa.requestFocusInWindow();
                return;
            }
            if (tenNhom.isEmpty()) {
                warnDlg(dlg, "Tên nhóm không được để trống!");
                txtTen.requestFocusInWindow();
                return;
            }

            String res = nhomBUS.addNhomQuyen(new NhomQuyen(maNhom, tenNhom));
            if (!res.contains("thành công")) {
                warnDlg(dlg, res); return;
            }

            // Lưu các quyền đã tích
            for (int r = 0; r < dlgModel.getRowCount(); r++) {
                String maCN  = dlgModel.getValueAt(r, 0).toString();
                boolean xem  = (Boolean) dlgModel.getValueAt(r, 2);
                boolean them = (Boolean) dlgModel.getValueAt(r, 3);
                boolean sua  = (Boolean) dlgModel.getValueAt(r, 4);
                boolean xoa  = (Boolean) dlgModel.getValueAt(r, 5);
                if (xem || them || sua || xoa)
                    nhomBUS.luuThayDoiQuyenNhom(maNhom, maCN, xem, xoa, sua, them);
            }

            JOptionPane.showMessageDialog(dlg, "Tạo nhóm \"" + tenNhom + "\" thành công!");
            nhomBUS.refreshData();
            refreshNhomCombo();
            // Tự chọn nhóm vừa tạo
            for (int i = 0; i < cboNhomQuyen.getItemCount(); i++) {
                if (cboNhomQuyen.getItemAt(i).getMaNhom().equals(maNhom)) {
                    cboNhomQuyen.setSelectedIndex(i); break;
                }
            }
            dlg.dispose();
        });

        footer.add(btnLuu); footer.add(btnHuy);
        dlg.add(footer, BorderLayout.SOUTH);
        dlg.pack();
        dlg.setMinimumSize(new Dimension(580, 540));
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    // TABLE LISTENER
    private void setupTableListener() {
        tableModel.addTableModelListener(e -> {
            if (isUpdatingTable || e.getType() != TableModelEvent.UPDATE) return;

            int row = e.getFirstRow();
            String maCN = tableModel.getValueAt(row, 0).toString();
            boolean xem = (boolean) tableModel.getValueAt(row, 2);
            boolean them = (boolean) tableModel.getValueAt(row, 3);
            boolean sua = (boolean) tableModel.getValueAt(row, 4);
            boolean xoa = (boolean) tableModel.getValueAt(row, 5);

            if (MODE_NV.equals(currentMode)) {
                String maNV = txtMaNV.getText().trim();
                if (maNV.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã nhân viên trước khi phân quyền!");
                    txtMaNV.requestFocusInWindow();
                    loadDataNV(""); return;
                }
                NhanVien nv = new NhanVien(); nv.setMaNV(maNV);
                ChucNang cn = new ChucNang(); cn.setMaCN(maCN);
                // PhanQuyen đúng nghĩa: NhanVien × ChucNang
                pqBUS.LuuThayDoiPQ(new PhanQuyen(nv, cn, xem, xoa, sua, them));

            } else { // MODE_NHOM
                if (currentMaNhom.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn nhóm quyền!");
                    return;
                }
                // NhomQuyenCT đúng nghĩa: NhomQuyen × ChucNang
                nhomBUS.luuThayDoiQuyenNhom(currentMaNhom, maCN, xem, xoa, sua, them);
            }
        });
    }

    // LOAD DATA
    private void loadDataNV(String maNV) {
        isUpdatingTable = true;
        tableModel.setRowCount(0);
        cachedNV = pqBUS.getBangPhanQuyen(maNV);
        for (PhanQuyen pq : cachedNV)
            tableModel.addRow(new Object[]{
                    pq.getChucNang().getMaCN(), pq.getChucNang().getTenCN(),
                    pq.isXem(), pq.isThem(), pq.isSua(), pq.isXoa()});
        updateCountLabel(cachedNV.size(), cachedNV.size());
        isUpdatingTable = false;
    }

    private void loadDataNhom(String maNhom) {
        currentMaNhom = maNhom;
        isUpdatingTable = true;
        tableModel.setRowCount(0);
        cachedNhom = nhomBUS.getBangQuyenCuaNhom(maNhom);
        for (NhomQuyenCT ct : cachedNhom)
            tableModel.addRow(new Object[]{
                    ct.getChucNang().getMaCN(), ct.getChucNang().getTenCN(),
                    ct.isXem(), ct.isThem(), ct.isSua(), ct.isXoa()});
        updateCountLabel(cachedNhom.size(), cachedNhom.size());
        isUpdatingTable = false;
    }

    // CẤP / THU HỒI TẤT CẢ (chỉ mode NV)
    private void setAllPermissionsNV(String maNV, boolean grant) {
        isUpdatingTable = true;
        for (int r = 0; r < tableModel.getRowCount(); r++)
            for (int c = 2; c <= 5; c++) tableModel.setValueAt(grant, r, c);
        isUpdatingTable = false;

        for (int r = 0; r < tableModel.getRowCount(); r++) {
            String maCN = tableModel.getValueAt(r, 0).toString();
            NhanVien nv = new NhanVien(); nv.setMaNV(maNV);
            ChucNang cn = new ChucNang(); cn.setMaCN(maCN);
            pqBUS.LuuThayDoiPQ(new PhanQuyen(nv, cn, grant, grant, grant, grant));
        }
        loadDataNV(maNV);
        JOptionPane.showMessageDialog(this,
                grant ? "Đã cấp toàn bộ quyền!" : "Đã thu hồi toàn bộ quyền!",
                "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    // 11. CẤP / THU HỒI TẤT CẢ (mode NHÓM)
    private void setAllPermissionsNhom(String maNhom, boolean grant) {
        isUpdatingTable = true;

        for (int r = 0; r < tableModel.getRowCount(); r++)
            for (int c = 2; c <= 5; c++) tableModel.setValueAt(grant, r, c);
        isUpdatingTable = false;

        for (int r = 0; r < tableModel.getRowCount(); r++) {
            String maCN = tableModel.getValueAt(r, 0).toString();
            nhomBUS.luuThayDoiQuyenNhom(maNhom, maCN, grant, grant, grant, grant);
        }
        loadDataNhom(maNhom);
        JOptionPane.showMessageDialog(this,
                grant ? "Đã cấp toàn bộ quyền cho nhóm!" :
                        "Đã thu hồi toàn bộ quyền của nhóm!",
                "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    // HELPERS
    private void filterTable(String keyword) {
        isUpdatingTable = true;
        tableModel.setRowCount(0);
        int matched = 0;

        if (MODE_NV.equals(currentMode)) {
            for (PhanQuyen pq : cachedNV) {
                if (keyword.isEmpty() || pq.getChucNang().getTenCN().toLowerCase().contains(keyword.toLowerCase())) {
                    tableModel.addRow(new Object[]{
                            pq.getChucNang().getMaCN(), pq.getChucNang().getTenCN(),
                            pq.isXem(), pq.isThem(), pq.isSua(), pq.isXoa()});
                    matched++;
                }
            }
            updateCountLabel(matched, cachedNV.size());
        } else {
            for (NhomQuyenCT ct : cachedNhom) {
                if (keyword.isEmpty() || ct.getChucNang().getTenCN().toLowerCase().contains(keyword.toLowerCase())) {
                    tableModel.addRow(new Object[]{
                            ct.getChucNang().getMaCN(), ct.getChucNang().getTenCN(),
                            ct.isXem(), ct.isThem(), ct.isSua(), ct.isXoa()});
                    matched++;
                }
            }
            updateCountLabel(matched, cachedNhom.size());
        }
        isUpdatingTable = false;
    }

    private void refreshNhomCombo() {
        if (cboNhomQuyen == null)
            return;
        isUpdatingTable = true;
        cboNhomQuyen.removeAllItems();
        for (NhomQuyen nhom : nhomBUS.getAll())
            cboNhomQuyen.addItem(nhom);
        isUpdatingTable = false;
        if (cboNhomQuyen.getItemCount() > 0 && MODE_NHOM.equals(currentMode)) {
            NhomQuyen first = cboNhomQuyen.getItemAt(0);
            currentMaNhom = first.getMaNhom();
            loadDataNhom(currentMaNhom);
        }
    }

    private void clearTable() {
        isUpdatingTable = true;
        tableModel.setRowCount(0);
        cachedNV.clear(); cachedNhom.clear();
        isUpdatingTable = false;
        updateCountLabel(0, 0);
    }

    private void updateCountLabel(int shown, int total) {
        if (lblCount == null) return;
        lblCount.setText(shown == total ? total + " chức năng" : shown + " / " + total + " chức năng");
    }

    private void hideColumn(int col) {
        table.getColumnModel().getColumn(col).setMinWidth(0);
        table.getColumnModel().getColumn(col).setMaxWidth(0);
        table.getColumnModel().getColumn(col).setPreferredWidth(0);
    }

    private JButton makeBtn(String text, Color bg, Color hover) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setBorder(new CompoundBorder(
                new LineBorder(hover, 1, true), new EmptyBorder(4, 12, 4, 12)));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            @Override public void mouseExited (MouseEvent e) { btn.setBackground(bg);   }
        });
        return btn;
    }

    private JPanel makeSep() {
        JPanel sep = new JPanel();
        sep.setBackground(new Color(210, 225, 245));
        sep.setPreferredSize(new Dimension(1, 24));
        return sep;
    }

    private JPanel dlgRow(String labelText, JTextField field, int fieldWidth) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        row.setBackground(Color.WHITE);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setPreferredSize(new Dimension(90, 28));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setPreferredSize(new Dimension(fieldWidth, 30));
        field.setBorder(new CompoundBorder(
                new LineBorder(new Color(198, 226, 255), 1, true),
                new EmptyBorder(2, 8, 2, 8)));
        row.add(lbl); row.add(field);
        return row;
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Thông báo", JOptionPane.WARNING_MESSAGE);
    }
    private void warnDlg(JDialog dlg, String msg) {
        JOptionPane.showMessageDialog(dlg, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
    private boolean confirm(String msg) {
        return JOptionPane.showConfirmDialog(this, msg, "Xác nhận",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}