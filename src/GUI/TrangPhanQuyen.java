package GUI;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import BUS.PhanQuyen_BUS;
import Model.ChucNang;
import Model.NhanVien;
import Model.PhanQuyen;

public class TrangPhanQuyen extends JPanel {
    private JTextField txtMaNV;
    private DefaultTableModel model;
    private JTable table;
    private PhanQuyen_BUS pqBUS;
    private boolean isUpdatingTable = false;

    // Lưu tên các chức năng để tìm kím
    private ArrayList<PhanQuyen> cachedList = new ArrayList<>();

    //Label hiển thị số chức năng
    private JLabel lblCount;

    public TrangPhanQuyen() {
        pqBUS = new PhanQuyen_BUS();

        setLayout(new BorderLayout(0, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(taoThanhCongCu(), BorderLayout.NORTH);
        add(taoNoiDung(), BorderLayout.CENTER);
        setupTableListener();
        loadDataToTable("");
    }

    private JPanel taoThanhCongCu() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, new Color(220, 235, 255)),
                new EmptyBorder(10, 20, 10, 20)
        ));

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        panel.setBackground(Color.WHITE);

        //Tìm kiếm
        JTextField txtSearch = new JTextField();
        txtSearch.setColumns(18);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setForeground(Color.GRAY);
        txtSearch.setText(" Tìm tên chức năng... ");
        txtSearch.setBorder(BorderFactory.createEmptyBorder(0,4,0,4));

        JPanel pnlSearch = new JPanel(new BorderLayout());
        pnlSearch.setBackground(Color.WHITE);
        pnlSearch.setPreferredSize(new Dimension(272, 32));
        pnlSearch.setBorder(new CompoundBorder(
                new LineBorder(new Color(198, 226, 255), 2, true),
                new EmptyBorder(0, 4, 0, 4)
        ));
        pnlSearch.add(txtSearch, BorderLayout.CENTER);

        // Placeholder focus
        txtSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (txtSearch.getText().startsWith("")) {
                    txtSearch.setText("");
                    txtSearch.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (txtSearch.getText().isEmpty()) {
                    txtSearch.setForeground(Color.GRAY);
                    txtSearch.setText(" Tìm tên chức năng... ");
                    filterTable(""); // Hiện lại toàn bộ khi xóa hết
                }
            }
        });

        // Tìm kiếm realtime khi gõ
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            private void doFilter() {
                String keyword = txtSearch.getText().trim();
                if (!keyword.startsWith("🔍")) {
                    filterTable(keyword);
                }
            }
            @Override public void insertUpdate(DocumentEvent e) { doFilter(); }
            @Override public void removeUpdate(DocumentEvent e) { doFilter(); }
            @Override public void changedUpdate(DocumentEvent e) { doFilter(); }
        });

        //Nút Refesh
        JButton btnLamMoi = new JButton(" Làm mới ");
        styleBtn(btnLamMoi, new Color(214, 238, 253), new Color(180, 215, 245));
        btnLamMoi.addActionListener(e -> {
            txtSearch.setText(" Tìm tên chức năng... ");
            txtSearch.setForeground(Color.GRAY);
            loadDataToTable(txtMaNV.getText().trim());
        });

        //Nút Cấp tất cả
        JButton btnCapTat = new JButton(" Cấp tất cả ");
        styleBtn(btnCapTat, new Color(212, 245, 220), new Color(170, 225, 185));
        btnCapTat.addActionListener(e -> {
            String maNV = txtMaNV.getText().trim();
            if (maNV.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng nhập Mã nhân viên trước!", "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Cấp toàn bộ quyền cho nhân viên này ?", "Xác nhận",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                setAllPermissions(maNV, true);
            }
        });

        //Nút Thu hồi tất cả
        JButton btnThuHoi = new JButton(" Thu hồi tất cả ");
        styleBtn(btnThuHoi, new Color(255, 220, 220), new Color(240, 180, 180));
        btnThuHoi.addActionListener(e -> {
            String maNV = txtMaNV.getText().trim();
            if (maNV.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng nhập Mã nhân viên trước!", "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Thu hồi toàn bộ quyền của nhân viên này?", "Xác nhận",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                setAllPermissions(maNV, false);
            }
        });

        //Labeo đếm số chức năng
        lblCount = new JLabel("0 chức năng");
        lblCount.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCount.setForeground(new Color(120, 140, 170));
        lblCount.setBorder(new EmptyBorder(0, 6, 0, 0));

        panel.add(pnlSearch);
        panel.add(btnLamMoi);
        panel.add(makeSeparator());
        panel.add(btnCapTat);
        panel.add(btnThuHoi);
        panel.add(makeSeparator());
        panel.add(lblCount);

        wrapper.add(panel, BorderLayout.CENTER);
        return wrapper;
    }

    //Thanh dọc ngăn cách giữa các nhóm nút
    private JPanel makeSeparator() {
        JPanel sep = new JPanel();
        sep.setBackground(new Color(210, 225, 245));
        sep.setPreferredSize(new Dimension(1, 24));
        return sep;
    }

    //Style chung cho nút
    private void styleBtn(JButton btn, Color bg, Color hover) {
        btn.setBackground(bg);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setBorder(new CompoundBorder(
                new LineBorder(hover, 1, true),
                new EmptyBorder(4, 12, 4, 12)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(hover); }
            @Override public void mouseExited(java.awt.event.MouseEvent e)  { btn.setBackground(bg); }
        });
    }

    public JPanel taoNoiDung() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(Color.WHITE);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        top.setBackground(Color.WHITE);

        JLabel phanQuyenCho = new JLabel("Mã nhân viên: ");
        phanQuyenCho.setFont(new Font("Segoe UI", Font.BOLD, 14));

        this.txtMaNV = new JTextField();
        this.txtMaNV.setPreferredSize(new Dimension(200, 28));
        this.txtMaNV.setBackground(Color.WHITE);
        this.txtMaNV.setBorder(new CompoundBorder(
                new LineBorder(new Color(214, 238, 253), 2),
                new EmptyBorder(0, 5, 0, 5)
        ));

        this.txtMaNV.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    loadDataToTable(txtMaNV.getText().trim());
            }
        });

        top.add(phanQuyenCho);
        top.add(txtMaNV);

        // Tạo bảng
        String[] columnNames = {"Mã chức năng", "Tên chức năng", "Xem", "Thêm", "Sửa", "Xóa"};

        this.model = new DefaultTableModel(null, columnNames) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return (columnIndex < 2) ? String.class : Boolean.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return column >= 2;
            }
        };

        this.table = new JTable(model);
        this.table.setRowHeight(40);
        this.table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        this.table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        this.table.getTableHeader().setBackground(new Color(231, 242, 255));
        this.table.getTableHeader().setForeground(new Color(50, 80, 130));
        this.table.setGridColor(new Color(235, 240, 250));
        this.table.setSelectionBackground(new Color(225, 240, 255));

        // Ẩn cột Mã CN
        this.table.getColumnModel().getColumn(0).setMinWidth(0);
        this.table.getColumnModel().getColumn(0).setMaxWidth(0);
        this.table.getColumnModel().getColumn(0).setPreferredWidth(0);

        // Renderer checkbox đẹp
        this.table.setDefaultRenderer(Boolean.class, new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                boolean isChecked = (value != null && (boolean) value);

                JPanel container = new JPanel(new GridBagLayout());
                container.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);

                JLabel box = new JLabel();
                box.setOpaque(true);
                box.setPreferredSize(new Dimension(20, 20));
                box.setHorizontalAlignment(SwingConstants.CENTER);
                box.setFont(new Font("Segoe UI", Font.BOLD, 13));

                if (isChecked) {
                    box.setBackground(new Color(111, 179, 255));
                    box.setForeground(Color.WHITE);
                    box.setText("v");
                    box.setBorder(new LineBorder(new Color(111, 140, 255), 1, true));
                } else {
                    box.setBackground(new Color(248, 248, 248));
                    box.setText("");
                    box.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
                }

                container.add(box);
                return container;
            }
        });

        // Tô màu dòng xen kẽ
        this.table.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(247, 251, 255));
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(this.table);
        scrollPane.setBorder(new LineBorder(new Color(200, 215, 235)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        panel.add(top, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadDataToTable(String maNV) {
        isUpdatingTable = true;
        this.model.setRowCount(0);

        cachedList = pqBUS.getBangPhanQuyen(maNV);

        for (PhanQuyen pq : cachedList) {
            this.model.addRow(new Object[]{
                    pq.getChucNang().getMaCN(),
                    pq.getChucNang().getTenCN(),
                    pq.isXem(),
                    pq.isThem(),
                    pq.isSua(),
                    pq.isXoa()
            });
        }

        updateCountLabel(cachedList.size(), cachedList.size());
        this.isUpdatingTable = false;
    }

    //Lọc bảng theo tên chức năng
    private void filterTable(String keyword) {
        isUpdatingTable = true;
        this.model.setRowCount(0);

        int matched = 0;
        for (PhanQuyen pq : cachedList) {
            if (keyword.isEmpty() ||
                    pq.getChucNang().getTenCN().toLowerCase()
                            .contains(keyword.toLowerCase())) {
                this.model.addRow(new Object[]{
                        pq.getChucNang().getMaCN(),
                        pq.getChucNang().getTenCN(),
                        pq.isXem(),
                        pq.isThem(),
                        pq.isSua(),
                        pq.isXoa()
                });
                matched++;
            }
        }

        updateCountLabel(matched, cachedList.size());
        isUpdatingTable = false;
    }

    // Cập nhật label đếm
    private void updateCountLabel(int shown, int total) {
        if (lblCount != null) {
            if (shown == total) {
                lblCount.setText(total + " chức năng");
            } else {
                lblCount.setText(shown + " / " + total + " chức năng");
            }
        }
    }

    //Cấp hoặc thu hồi toàn bộ quyền
    private void setAllPermissions(String maNV, boolean grant) {
        isUpdatingTable = true;
        for (int r = 0; r < model.getRowCount(); r++) {
            model.setValueAt(grant, r, 2);
            model.setValueAt(grant, r, 3);
            model.setValueAt(grant, r, 4);
            model.setValueAt(grant, r, 5);
        }
        isUpdatingTable = false;

        // Lưu từng dòng
        for (int r = 0; r < model.getRowCount(); r++) {
            String maCN = model.getValueAt(r, 0).toString();
            NhanVien nv = new NhanVien();
            nv.setMaNV(maNV);
            ChucNang cn = new ChucNang();
            cn.setMaCN(maCN);
            PhanQuyen pq = new PhanQuyen(nv, cn, grant, grant, grant, grant);
            pqBUS.LuuThayDoiPQ(pq);
        }

        // Cập nhật cache
        loadDataToTable(maNV);
        JOptionPane.showMessageDialog(this,
                grant ? "Đã cấp toàn bộ quyền!" : "Đã thu hồi toàn bộ quyền!",
                "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void setupTableListener() {
        this.model.addTableModelListener(e -> {
            if (this.isUpdatingTable) return;

            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                String maNV = this.txtMaNV.getText().trim();

                if (maNV.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Vui lòng nhập Mã nhân viên trước khi phân quyền!");
                    loadDataToTable("");
                    return;
                }

                String maCN = this.model.getValueAt(row, 0).toString();
                boolean xem  = (boolean) model.getValueAt(row, 2);
                boolean them = (boolean) model.getValueAt(row, 3);
                boolean sua  = (boolean) model.getValueAt(row, 4);
                boolean xoa  = (boolean) model.getValueAt(row, 5);

                NhanVien nv = new NhanVien();
                nv.setMaNV(maNV);

                ChucNang cn = new ChucNang();
                cn.setMaCN(maCN);

                PhanQuyen pq = new PhanQuyen(nv, cn, xem, xoa, sua, them);
                pqBUS.LuuThayDoiPQ(pq);
            }
        });
    }
}