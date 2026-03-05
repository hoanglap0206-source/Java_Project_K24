package GUI;

import BUS.PhanQuyen_BUS;
import Model.ChucNang;
import Model.PhanQuyen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class ThanhBen extends JPanel {

    private final Color SIDEBAR_COLOR = new Color(160, 205, 255);
    private final Color HOVER_COLOR = new Color(86, 166, 255);

    private JLabel selectedLabel = null;
    private ManHinhChinh manHinhChinh;
    private JPanel menuContainer;

    public ThanhBen(ManHinhChinh manHinhChinh, String maNV) {
        this.manHinhChinh = manHinhChinh;

        setBackground(SIDEBAR_COLOR);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(180, 0));

        setBorder(BorderFactory.createMatteBorder(
                0, 0, 0, 1,
                new Color(210, 220, 230)  // xám rất nhạt
        ));

        // Container dùng BoxLayout để các menu động gom gọn lên phía trên (NORTH)
        menuContainer = new JPanel();
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        menuContainer.setBackground(SIDEBAR_COLOR);

        // Luôn có menu Hồ sơ cá nhân cho mọi user
        addMenu("Tổng quan");
        addMenu("Phân quyền");

        // Gọi logic phân quyền từ DataBase
        if (maNV != null && !maNV.isEmpty()) {
            PhanQuyen_BUS PQbus = new PhanQuyen_BUS();
            ArrayList<PhanQuyen> dsQuyenCaNhan = PQbus.getDSQuyenCaNhan(maNV);

            if (dsQuyenCaNhan != null) {
                for (PhanQuyen pq : dsQuyenCaNhan) {
                    ChucNang cn = pq.getChucNang();
                    String tenButton = cn.getTenCN();
                    addMenu(tenButton);
                }
            }
        }

        // Đưa container vào vùng NORTH để các Item nằm sát phía trên
        add(menuContainer, BorderLayout.NORTH);
    }

    private void addMenu(String text) {
        menuContainer.add(createMenuLabel(text));
    }

    private JLabel createMenuLabel(String text) {
        JLabel label = new JLabel(text);
        label.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
        label.setHorizontalAlignment(SwingConstants.LEFT);

        // Cho JLabel lấp đầy chiều ngang của Container
        label.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

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

                // 1. Chuyển trang nội dung
                manHinhChinh.hienThiTrang(text);

                // 2. Cập nhật tên chức năng lên ThanhTieuDe
                manHinhChinh.getThanhTieuDe().setTitleCN(text);
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