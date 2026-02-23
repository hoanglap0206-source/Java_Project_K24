package GUI;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class TrangTongQuan extends JPanel {

    private final Color BACKGROUND1 = new Color(235, 235, 235);
    private final Color BACKGROUND2 = new Color(255, 255, 255);
    private final Color BACKGROUND3 = new Color(230, 244, 255);

    private final Color CARD_COLOR = Color.WHITE;

    public TrangTongQuan() {
        setLayout(new BorderLayout(15,15));
        setBackground(BACKGROUND1);

        add(taoThongKe(), BorderLayout.NORTH);
        add(taoHaiBang(), BorderLayout.CENTER);
    }

    private JPanel taoThongKe() {
        JPanel panel = new JPanel(new GridLayout(1,3,20,0));
        panel.setBorder(new EmptyBorder(20,20,20,20)); // tránh dính sát mép panel
        panel.setBackground(BACKGROUND2);

        panel.add(taoCard("Tổng loại sản phẩm", "16"));
        panel.add(taoCard("Tổng tồn kho", "1.245 thùng"));
        panel.add(taoCard("Sắp hết hạn", "7 loại"));

        return panel;
    }

    private JPanel taoCard(String title, String value) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BACKGROUND3);
        card.setBorder(new EmptyBorder(20,20,20,20));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(BACKGROUND3);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblValue.setAlignmentX(Component.CENTER_ALIGNMENT);

        textPanel.add(lblTitle);
        textPanel.add(Box.createVerticalStrut(10)); // tạo một khoảng trống vô hình cao 10px
        textPanel.add(lblValue);
        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel taoHaiBang() {
        JPanel panel = new JPanel(new GridLayout(1,2,20,0));
        panel.setBackground(BACKGROUND1);

        panel.add(taoBangHoatDong());
        panel.add(taoBangSapHetHan());

        return panel;
    }

    private JPanel taoBangHoatDong() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15)); // tránh dính mép

        JLabel title = new JLabel("Hoạt động 8 ngày gần đây");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(0,0,10,0)); // khoảng cách giữa tiêu đề và cái bảng
        panel.add(title, BorderLayout.NORTH);

        String[] column = {"Ngày", "Nhập", "Xuất"};
        Object[][] data = {
                {"T2",20,15},
                {"T3",20,15},
                {"T4",20,15},
                {"T5",20,15},
                {"T6",20,15},
                {"T7",20,15},
                {"CN",20,15},
                {"T2",20,15}
        };

        JTable table = new JTable(new DefaultTableModel(data, column));
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(30);

        // Căn giữa nội dung
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Style Header
        JTableHeader header = table.getTableHeader();
        header.setForeground(Color.BLACK);
        header.setBackground(new Color(217, 249, 255));
        header.setFont(new Font("Arial", Font.BOLD, 13));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);

        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel taoBangSapHetHan() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        JLabel title = new JLabel("Sản phẩm sắp hết hạn");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
        panel.add(title, BorderLayout.NORTH);

        String[] column = {"Tên SP", "HSD", "Số lượng"};
        Object[][] data = {
                {"Coca Cola 1.5L","16/6/2026","15 thùng"},
                {"Coca Cola 1.5L","16/6/2026","15 thùng"},
                {"Coca Cola 1.5L","16/6/2026","15 thùng"}
        };

        JTable table = new JTable(new DefaultTableModel(data, column));
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(30);

        // Căn giữa nội dung
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Style Header
        JTableHeader header = table.getTableHeader();
        header.setForeground(Color.BLACK);
        header.setBackground(new Color(217, 249, 255));
        header.setFont(new Font("Arial", Font.BOLD, 13));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);

        JButton btn = new JButton("Xem chi tiết");
        Style.styleButton(btn);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.setBackground(CARD_COLOR);
        bottom.add(btn);

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }
}