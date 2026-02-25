package GUI;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class TrangApThue extends JPanel {
    public TrangApThue() {
        setLayout(new BorderLayout(0, 20));
        setBorder(new EmptyBorder(20, 25, 20, 25));
        setBackground(Color.WHITE);

        add(cauHinhThue(), BorderLayout.NORTH);
        add(danhSachThue(), BorderLayout.CENTER);
    }

    public JPanel cauHinhThue() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);

        JLabel lblCauHinhThue = new JLabel("CẤU HÌNH THUẾ");
        lblCauHinhThue.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(lblCauHinhThue, BorderLayout.NORTH);

        JPanel thongTin = new JPanel(new GridBagLayout());
        thongTin.setBackground(Color.WHITE);
        thongTin.setBorder(new LineBorder(Color.BLACK));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 20);
        gbc.anchor = GridBagConstraints.WEST;

        // Hàng 1
        gbc.gridx = 0;
        gbc.gridy = 0;
        thongTin.add(new JLabel("Loại thuế:"), gbc);

        gbc.gridx = 1;
        thongTin.add(new JTextField("VAT", 12), gbc);

        gbc.gridx = 2;
        thongTin.add(new JLabel("Trạng thái:"), gbc);

        gbc.gridx = 3;
        thongTin.add(new JComboBox<>(new String[]{"Đang áp dụng", "Hết hiệu lực"}), gbc);

        // Hàng 2
        gbc.gridx = 0; gbc.gridy = 1;
        thongTin.add(new JLabel("Áp dụng cho:"), gbc);

        gbc.gridx = 1;
        thongTin.add(new JComboBox<>(new String[]{"Xuất kho", "Nhập kho"}), gbc);

        // Hàng 3
        gbc.gridx = 0; gbc.gridy = 2;
        thongTin.add(new JLabel("Mức thuế (%):"), gbc);

        gbc.gridx = 1;
        thongTin.add(new JTextField("10", 12), gbc);

        // Hàng 4
        gbc.gridx = 0; gbc.gridy = 3;
        thongTin.add(new JLabel("Ngày hiệu lực:"), gbc);
        gbc.gridx = 1;
        thongTin.add(new JTextField("10/7/2025", 12), gbc);

        panel.add(thongTin, BorderLayout.CENTER);

        JButton btnLuu = new JButton("Lưu");
        Style.styleButton(btnLuu);
        JPanel btnLuuWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnLuuWrapper.setBackground(Color.WHITE);
        btnLuuWrapper.add(btnLuu);
        panel.add(btnLuuWrapper, BorderLayout.SOUTH);

        return panel;
    }

    public JPanel danhSachThue() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);

        JLabel lblDanhSachThue = new JLabel("DANH SÁCH THUẾ");
        lblDanhSachThue.setFont(new Font("Segoe UI", Font.BOLD, 18));

        panel.add(lblDanhSachThue, BorderLayout.NORTH);
        panel.add(taoBang(), BorderLayout.CENTER);

        return panel;
    }

    public JScrollPane taoBang() {
        String[] columns = {
                "STT", "Loại thuế", "Áp dụng cho",
                "%", "Ngày hiệu lực", "Trạng thái", "Thao tác"
        };

        Object[][] data = {
                {1, "VAT", "Xuất kho", "10%", "10/07/2025", "Đang áp dụng", "Xem"},
                {2, "VAT", "Nhập kho", "8%", "10/07/2025", "Đang áp dụng", "Xem"},
                {3, "VAT", "Nhập kho", "7%", "10/07/2025", "Hết hiệu lực", "Xem"}
        };

        JTable table = new JTable(data, columns) {
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };

        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(210,230,255));
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

                    if (status.equals("Đang áp dụng")) {
                        lbl.setForeground(new Color(0,128,0));
                    } else if (status.equals("Hết hiệu lực")) {
                        lbl.setForeground(Color.GRAY);
                    }
                }

                return lbl;
            }
        });

        // ===== Nút Xem =====
        table.getColumnModel().getColumn(6).setCellRenderer(new TrangApThue.ButtonRenderer());
        table.getColumnModel().getColumn(6).setCellEditor(new TrangApThue.ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(new Color(180,180,180)));

        return scrollPane;
    }
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setText("Xem");
            setFocusPainted(false);
            setBackground(new Color(220,220,220));
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
            JOptionPane.showMessageDialog(button, "Xem chi tiết thuế");
            return "Xem";
        }
    }
}
