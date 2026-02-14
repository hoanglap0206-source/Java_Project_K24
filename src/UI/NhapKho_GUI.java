package UI;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class NhapKho_GUI extends JPanel {
    public NhapKho_GUI() {
        JPanel main = new JPanel(new GridLayout(1, 2));
        JPanel uiLeft = new JPanel(new BorderLayout());
        JPanel uiRight = new JPanel(new BorderLayout());
        main.add(uiLeft);
        main.add(uiRight);
        JPanel outerPanel = new JPanel();
        outerPanel.setBorder(new EmptyBorder(30,20,10,10));
        JPanel innerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        innerPanel.setBackground(new Color(230, 230, 230));
        innerPanel.setBorder(
                new CompoundBorder(
                        new LineBorder(Color.GRAY, 2, true),
                        new EmptyBorder(8, 10, 8, 10)
                )
        );
        JTextField txtSearch = new JTextField("Tìm kiếm", 18);
        txtSearch.setForeground(Color.GRAY);
        txtSearch.setBorder(
                new CompoundBorder(
                        new LineBorder(Color.GRAY, 1, true),
                        new EmptyBorder(5, 10, 5, 30)
                )
        );
        // ===== Nút làm mới =====
        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.setFocusPainted(false);
        btnRefresh.setBackground(Color.WHITE);
        innerPanel.add(txtSearch);
        innerPanel.add(btnRefresh);

        outerPanel.add(innerPanel);
        uiLeft.add(outerPanel,BorderLayout.NORTH);

        String[] cols = {"Mã SP", "Tên sản phẩm", "Số lượng", "Đơn giá"};
        Object[][] data = {};

        JTable table = new JTable(data, cols);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
//        int tableWidth = 600;          // chiều rộng mong muốn
//        int colCount = table.getColumnCount();
//        int colWidth = tableWidth / colCount;
//
//        for (int i = 0; i < colCount; i++) {
//            table.getColumnModel()
//                    .getColumn(i)
//                    .setPreferredWidth(colWidth);
//        }
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new EmptyBorder(15, 20, 20, 20));
        uiLeft.add(scroll,BorderLayout.CENTER);
        JPanel tailLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        tailLeft.setBorder(new EmptyBorder(10, 89, 40, 20));
        JLabel lblSoLuong = new JLabel("SỐ LƯỢNG");

        JTextField txtSoLuong = new JTextField("3", 5);

        JButton btnThem = new JButton("Thêm");
        btnThem.setBackground(new Color(102, 255, 102));
        btnThem.setFocusPainted(false);
        tailLeft.add(lblSoLuong);
        tailLeft.add(txtSoLuong);
        tailLeft.add(btnThem);
        uiLeft.add(tailLeft, BorderLayout.SOUTH);
        setLayout(new BorderLayout());
        add(main, BorderLayout.CENTER);

    }
    public static void main(String[] arg){
        new NhapKho_GUI();
    }
}
