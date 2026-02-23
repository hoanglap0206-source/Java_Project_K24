package UI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class QLySanPham_GUI extends JPanel {
    private JTable table;
    private DefaultTableModel model;

    public QLySanPham_GUI(){
        setLayout(new BorderLayout());

        // --- 1. Toolbar Panel (Chứa các nút chức năng và tìm kiếm) ---
        JPanel pnlToolbar = new JPanel(new BorderLayout());
        pnlToolbar.setBackground(Color.WHITE);

        // Cụm tìm kiếm bên phải
        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pnlRight.setOpaque(false);
        JTextField txtSearch = new JTextField(20);
        JButton btnLamMoi = new JButton("Làm mới");

        pnlRight.add(new JLabel("Tìm kiếm: "));
        pnlRight.add(txtSearch);
        pnlRight.add(btnLamMoi);

        pnlToolbar.add(pnlRight, BorderLayout.EAST);

        // --- 2. Bảng sản phẩm (JTable) ---
        String[] columns = {"Mã sản phẩm", "Tên sản phẩm", "Đơn vị tính", "Số lượng", "Giá nhập", "Mã kệ"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setRowHeight(30); // Độ cao dòng theo Figma
        JScrollPane scrollPane = new JScrollPane(table);

        // Gom Toolbar và Table
        JPanel pnlMainContent = new JPanel(new BorderLayout());
        pnlMainContent.add(new GiaoDienChinh_TopContent(), BorderLayout.NORTH);
        pnlMainContent.add(scrollPane, BorderLayout.CENTER);

        add(pnlMainContent, BorderLayout.CENTER);
    }
}