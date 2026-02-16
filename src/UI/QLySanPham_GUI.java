package UI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class QLySanPham_GUI extends JPanel {
    private JTable table;
    private DefaultTableModel model;

    public QLySanPham_GUI(){
        setLayout(new BorderLayout());

        // Nếu file GiaoDienChinh_TopContent.java đã có, giữ lại dòng này
        // add(new GiaoDienChinh_TopContent(), BorderLayout.NORTH);

        // --- 1. Toolbar Panel (Chứa các nút chức năng và tìm kiếm) ---
        JPanel pnlToolbar = new JPanel(new BorderLayout());
        pnlToolbar.setBackground(Color.WHITE);

        // Cụm nút bên trái: Thêm, Xóa, Sửa, Xuất Excel
        JPanel pnlLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        pnlLeft.setOpaque(false);

        JButton btnAdd = new JButton("Thêm"); btnAdd.setBackground(new Color(144, 238, 144));
        JButton btnDel = new JButton("Xóa"); btnDel.setBackground(new Color(255, 99, 71));
        JButton btnEdit = new JButton("Sửa"); btnEdit.setBackground(new Color(255, 255, 153));
        JButton btnExcel = new JButton("Xuất Excel");

        pnlLeft.add(btnAdd);
        pnlLeft.add(btnDel);
        pnlLeft.add(btnEdit);
        pnlLeft.add(new JSeparator(SwingConstants.VERTICAL));
        pnlLeft.add(btnExcel);

        // Cụm tìm kiếm bên phải
        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pnlRight.setOpaque(false);
        JTextField txtSearch = new JTextField(20);
        JButton btnLamMoi = new JButton("Làm mới");

        pnlRight.add(new JLabel("Tìm kiếm: "));
        pnlRight.add(txtSearch);
        pnlRight.add(btnLamMoi);

        pnlToolbar.add(pnlLeft, BorderLayout.WEST);
        pnlToolbar.add(pnlRight, BorderLayout.EAST);

        // --- 2. Bảng sản phẩm (JTable) ---
        String[] columns = {"Mã sản phẩm", "Tên sản phẩm", "Đơn vị tính", "Số lượng", "Giá nhập", "Mã kệ"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setRowHeight(30); // Độ cao dòng theo Figma
        JScrollPane scrollPane = new JScrollPane(table);

        // Gom Toolbar và Table
        JPanel pnlMainContent = new JPanel(new BorderLayout());
        pnlMainContent.add(pnlToolbar, BorderLayout.NORTH);
        pnlMainContent.add(scrollPane, BorderLayout.CENTER);

        add(pnlMainContent, BorderLayout.CENTER);
    }
}