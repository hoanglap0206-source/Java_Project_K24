package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TrangQuanLyTaiKhoan extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    public TrangQuanLyTaiKhoan() {

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 248, 252));

        // TẠO KHOẢNG CÁCH VỚI THANH TIÊU ĐỀ
        setBorder(BorderFactory.createEmptyBorder(20, 15, 15, 15));

        add(taoThanhCongCu(), BorderLayout.NORTH);
        add(taoBang(), BorderLayout.CENTER);
    }

    // =======================
    // THANH CÔNG CỤ
    // =======================
    private JPanel taoThanhCongCu() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 248, 252));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        left.setBackground(new Color(245, 248, 252));

        JTextField txtTimKiem = new JTextField(15);
        txtTimKiem.setPreferredSize(new Dimension(180, 30));

        JComboBox<String> cbRole = new JComboBox<>(new String[]{"Role", "Admin", "Nhân viên"});
        cbRole.setPreferredSize(new Dimension(120, 30));

        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"Status", "Active", "Banned"});
        cbStatus.setPreferredSize(new Dimension(120, 30));

        JButton btnLamMoi = new JButton("Làm mới");

        left.add(txtTimKiem);
        left.add(cbRole);
        left.add(cbStatus);
        left.add(btnLamMoi);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        right.setBackground(new Color(245, 248, 252));

        JButton btnExport = new JButton("Export");
        JButton btnAdd = new JButton("+ Add User");

        btnAdd.setBackground(new Color(52, 152, 219));
        btnAdd.setForeground(Color.WHITE);

        right.add(btnExport);
        right.add(btnAdd);

        panel.add(left, BorderLayout.WEST);
        panel.add(right, BorderLayout.EAST);

        return panel;
    }

    // =======================
    // BẢNG DỮ LIỆU
    // =======================
    private JPanel taoBang() {

        String[] columnNames = {
                "Full Name", "Email", "Username", "Status", "Role", "Actions"
        };

        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        model.addRow(new Object[]{"Nguyễn Văn A", "nguyenvana@gmail.com", "nguyenvana", "Active", "Nhân viên", "Edit | Delete"});
        model.addRow(new Object[]{"Nguyễn Văn B", "nguyenvanb@gmail.com", "nguyenvanb", "Banned", "Nhân viên", "Edit | Delete"});

        table = new JTable(model);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.setFont(new Font("Arial", Font.PLAIN, 13));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);

        // PANEL BAO NGOÀI TẠO KHOẢNG CÁCH
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(245, 248, 252));
        wrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        wrapper.add(scrollPane, BorderLayout.CENTER);

        return wrapper;
    }

}
