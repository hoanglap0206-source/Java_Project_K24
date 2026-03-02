package GUI;

import Model.NhanVien;
import BUS.NV_BUS;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import javax.swing.table.*;
import java.util.ArrayList;

public class TrangQuanLyTaiKhoan extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private NV_BUS nvBUS = new NV_BUS();

    public TrangQuanLyTaiKhoan() {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 255, 255));

        add(taoThanhCongCu(), BorderLayout.NORTH);
        add(taoNoiDung(), BorderLayout.CENTER);
        loadDataFromBUS();

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
        JButton btnEdit = new JButton("Chỉnh sửa");
        Style.styleButton(btnEdit);
        JButton btnDelete = new JButton("Xóa");
        Style.styleButton(btnDelete);
        JButton btnAdd = new JButton("+ Thêm");
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
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 25, 20, 25));

        panel.add(taoTieuDe(), BorderLayout.NORTH);
        panel.add(taoBang(), BorderLayout.CENTER);

        return panel;
    }

    private JPanel taoTieuDe() {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnl.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("DANH SÁCH TÀI KHOẢN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.BLACK);

        pnl.add(lblTitle);
        return pnl;
    }

    private JScrollPane taoBang() {
        String[] columns = {
                "STT", "Full Name", "Số điện thoại",
                "Username", "Vai trò", "Trạng thái", "Xem chi tiết"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };

        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(210, 230, 255));
        table.getTableHeader().setReorderingAllowed(false);

        // Căn giữa toàn bộ
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        // ===== Render màu trạng thái =====
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                lbl.setHorizontalAlignment(SwingConstants.CENTER);

                if (value != null) {
                    String status = value.toString();

                    if (status.equals("Active")) {
                        lbl.setForeground(new Color(0, 128, 0));
                    } else if (status.equals("Banned")) {
                        lbl.setForeground(Color.RED);
                    }
                }

                return lbl;
            }
        });

        // ===== Nút Xem =====
        table.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(new Color(180, 180, 180)));

        return scrollPane;
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setText("Xem");
            setFocusPainted(false);
            setBackground(new Color(220, 220, 220));
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("Xem");
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected,
                int row, int column) {
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            JOptionPane.showMessageDialog(button, "Xem chi tiết tài khoản");
            return "Xem";
        }
    }


    private void loadDataFromBUS() {
        model.setRowCount(0);

        ArrayList<NhanVien> list = nvBUS.getAll();

        int stt = 1;
        for (NhanVien nv : list) {

            String statusRaw = nv.getTrangThai();
            String statusShow =
                    "HoatDong".equalsIgnoreCase(statusRaw)
                            ? "Active"
                            : "Banned";

            model.addRow(new Object[]{
                    stt++,
                    nv.getHoTen(),
                    nv.getSDT(),
                    nv.getMaNV(),
                    nv.getChucVu(),
                    statusShow,
                    "Xem"
            });
        }
    }
}