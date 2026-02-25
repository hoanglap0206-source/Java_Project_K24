package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ThanhBen extends JPanel {

    private final Color SIDEBAR_COLOR = new Color(160, 205, 255);
    private final Color HOVER_COLOR = new Color(86, 166, 255);

    private JLabel selectedLabel = null;
    private ManHinhChinh manHinhChinh;

    public ThanhBen(ManHinhChinh manHinhChinh) {

        this.manHinhChinh = manHinhChinh;

        setBackground(SIDEBAR_COLOR);
        setLayout(new GridLayout(13, 1));
        setPreferredSize(new Dimension(180, 0));

        setBorder(BorderFactory.createMatteBorder(
                0, 0, 0, 1,
                new Color(210, 220, 230)  // xám rất nhạt
        ));

        addMenu("Tổng quan");
        addMenu("Sản phẩm");
        addMenu("Nhà cung cấp");
        addMenu("Khách hàng");
        addMenu("Kệ kho");
        addMenu("Nhập kho");
        addMenu("Xuất kho");
        addMenu("Phiếu nhập");
        addMenu("Phiếu xuất");
        addMenu("Báo cáo");
        addMenu("Áp thuế");
        addMenu("Quản lý tài khoản");
        addMenu("Phân quyền");
    }

    private void addMenu(String text) {
        add(createMenuLabel(text));
    }

    private JLabel createMenuLabel(String text) {

        JLabel label = new JLabel(text);
        label.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
        label.setHorizontalAlignment(SwingConstants.LEFT);

        label.setForeground(Color.BLACK);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setOpaque(true);
        label.setBackground(SIDEBAR_COLOR);

        label.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                if (selectedLabel != null) {
                    selectedLabel.setBackground(SIDEBAR_COLOR);
                    selectedLabel.setForeground(Color.BLACK);
                }

                label.setBackground(HOVER_COLOR);
                label.setForeground(Color.WHITE);

                selectedLabel = label;

                manHinhChinh.hienThiTrang(text);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (label != selectedLabel) {
                    label.setBackground(HOVER_COLOR);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (label != selectedLabel) {
                    label.setBackground(SIDEBAR_COLOR);
                }
            }
        });

        return label;
    }
}
