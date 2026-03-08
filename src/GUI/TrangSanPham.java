package GUI;

import BUS.SanPham_BUS;
import Model.SanPham;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import javax.swing.table.*;

public class TrangSanPham extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JSplitPane splitPane;
    private JPanel panelForm;
    private JPanel panelTableWrapper;

    private SanPham_BUS spBus = new SanPham_BUS();

    public TrangSanPham() {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 255, 255));

        add(taoThanhCongCu(), BorderLayout.NORTH);
        add(taoNoiDung(), BorderLayout.CENTER);
        loadTableData();
        addEvents();
    }

    private JPanel taoThanhCongCu() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(new EmptyBorder(15, 20, 5, 20));

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        panel.setBackground(new Color(231, 242, 245));
        panel.setBorder(new CompoundBorder(
                new LineBorder(new Color(198, 226, 255), 2),
                new EmptyBorder(2, 12, 2, 12)
        ));


        // Thanh tìm kiếm
        JTextField txtSearch = new JTextField("Tìm kiếm");
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

        JButton btnSearchIcon = new JButton("🔍");
        btnSearchIcon.setBackground(new Color(214, 238, 253));
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
        JButton btnLamMoi = new JButton("↻ Làm mới");
        Style.styleButton(btnLamMoi);


        // Combobox Lọc
        String[] itemLoc = {"Lọc", "1", "2", "3", "4", "5"};
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


        // Các nút khác
        btnEdit = new JButton("Chỉnh sửa");
        Style.styleButton(btnEdit);
        btnDelete = new JButton("Xóa");
        Style.styleButton(btnDelete);
        btnAdd = new JButton("+ Thêm");
        Style.styleButton(btnAdd);
        JButton btnExcel = new JButton("Xuất excel");
        Style.styleButton(btnExcel);

        // Thêm vào panel
        panel.add(pnlSearchInput);
        panel.add(btnLamMoi);
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

        // ===== LEFT: bảng =====
        panelTableWrapper = new JPanel(new BorderLayout());
        panelTableWrapper.add(taoBang(), BorderLayout.CENTER);

        // ===== RIGHT: form (ẩn ban đầu) =====
        panelForm = taoPanelForm();
        panelForm.setVisible(false);

        // ===== SPLIT =====
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
        pnl.setBorder(new LineBorder(new Color(200,200,200)));

        pnl.setLayout(new BorderLayout());

        JLabel lbl = new JLabel("FORM SẢN PHẨM", SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));

        pnl.add(lbl, BorderLayout.NORTH);

        // sau này bạn nhét JTextField vào đây

        return pnl;
    }

    private void showFormPanel() {
        panelForm.setVisible(true);
        splitPane.setDividerLocation(
                getWidth() - 320
        );
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
                return false; // Không cho sửa trực tiếp trên bảng
            }
        };

        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Màu header
        table.getTableHeader().setBackground(new Color(210, 230, 255));
        table.getTableHeader().setForeground(Color.BLACK);

        // Tắt reorder
        table.getTableHeader().setReorderingAllowed(false);

        // Căn giữa toàn bộ nội dung
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        // Áp dụng cho tất cả cột
        for (int i = 0; i < table.getColumnCount(); i++) {
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

        // Chỉnh width từng cột cho giống mockup
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


    private void loadTableData() {
        if (model == null) return;
        model.setRowCount(0);

        int stt = 1;
        java.text.DecimalFormat df = new java.text.DecimalFormat("#,###đ");

        // Sửa lại thành getAll() theo đúng file BUS của bạn
        for (SanPham sp : spBus.getAll()) {

            // Logic trạng thái
            String trangThai = (sp.getSoLuong() > 0) ? "Còn hàng" : "Hết hàng";

            // Lấy mã kệ từ đối tượng KeKho bên trong SanPham (dùng đúng tên biến make)
            String make = (sp.getKeKho() != null) ? sp.getKeKho().getMaKe() : "Chưa có";

            model.addRow(new Object[]{
                    stt++,
                    sp.getMaSP(),
                    sp.getTenSP(),
                    sp.getDonViTinh(),
                    sp.getSoLuong(),
                    df.format(sp.getGiaTien()),
                    make, // Phải trùng với tên biến khai báo ở trên
                    trangThai
            });
        }
    }
    private void addEvents() {
        btnAdd.addActionListener(e -> handleAdd());
        btnEdit.addActionListener(e -> handleEdit());
        btnDelete.addActionListener(e -> handleDelete());
        btnAdd.addActionListener(e -> showFormPanel());
        btnEdit.addActionListener(e -> showFormPanel());
    }
    private void handleAdd() {
        String maSP = JOptionPane.showInputDialog(this, "Nhập mã SP:");
        if (maSP == null || maSP.trim().isEmpty()) return;

        String tenSP = JOptionPane.showInputDialog(this, "Nhập tên SP:");
        if (tenSP == null || tenSP.trim().isEmpty()) return;

        String dvt = JOptionPane.showInputDialog(this, "Đơn vị tính:");
        String slStr = JOptionPane.showInputDialog(this, "Số lượng:");
        String giaStr = JOptionPane.showInputDialog(this, "Giá nhập:");

        int soLuong;
        float gia;

        try {
            soLuong = Integer.parseInt(slStr);
            gia = Float.parseFloat(giaStr);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ");
            return;
        }

        SanPham sp = new SanPham();
        sp.setMaSP(maSP);
        sp.setTenSP(tenSP);
        sp.setDonViTinh(dvt);
        sp.setSoLuong(soLuong);
        sp.setGiaTien(gia);

        String msg = spBus.addSanPham(sp);
        JOptionPane.showMessageDialog(this, msg);
        loadTableData();
    }
    private void handleEdit() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chọn sản phẩm cần sửa");
            return;
        }

        String maSP = model.getValueAt(row, 1).toString();

        String tenMoi = JOptionPane.showInputDialog(
                this,
                "Tên mới:",
                model.getValueAt(row, 2)
        );

        String slMoiStr = JOptionPane.showInputDialog(
                this,
                "Số lượng mới:",
                model.getValueAt(row, 4)
        );

        String giaMoiStr = JOptionPane.showInputDialog(
                this,
                "Giá mới:",
                model.getValueAt(row, 5)
        );

        int slMoi;
        float giaMoi;

        try {
            slMoi = Integer.parseInt(slMoiStr);
            giaMoi = Float.parseFloat(giaMoiStr.replace("đ","").replace(",",""));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ");
            return;
        }

        SanPham sp = new SanPham();
        sp.setMaSP(maSP);
        sp.setTenSP(tenMoi);
        sp.setSoLuong(slMoi);
        sp.setGiaTien(giaMoi);

        String msg = spBus.updateSanPham(sp);
        JOptionPane.showMessageDialog(this, msg);
        loadTableData();
    }
    private void handleDelete() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chọn sản phẩm cần xóa");
            return;
        }

        String maSP = model.getValueAt(row, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        String msg = spBus.deleteSanPham(maSP);
        JOptionPane.showMessageDialog(this, msg);
        loadTableData();
    }
}