package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class ThanhTieuDe extends JPanel {

    private ManHinhChinh mainFrame;
    private final Color HEADER_COLOR = new Color(170, 211, 255);
    private JPopupMenu popupMenu;
    private JButton closeBtn, hideBtn, bigBtn;
    private JLabel lblChucNang;

    // Biến dùng để xử lý kéo thả cửa sổ
    private int xMouse, yMouse;

    public ThanhTieuDe(ManHinhChinh mainFrame, String nameUser) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(HEADER_COLOR);
        setPreferredSize(new Dimension(1000, 50));

        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 220, 230)));

        // Xử lý kéo thả cửa sổ khi nhấn vào thanh tiêu đề
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                xMouse = e.getX();
                yMouse = e.getY();
            }
        });
        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int x = e.getXOnScreen();
                int y = e.getYOnScreen();
                mainFrame.setLocation(x - xMouse, y - yMouse);
            }
        });

        // ===== PANEL BÊN TRÁI (Tên công ty + Tên chức năng) =====
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        leftPanel.setOpaque(false);

        CirclePanel logo = new CirclePanel("/Img/ConRua.jpg", 35);

        JLabel title = new JLabel("QUẢN LÝ KHO NƯỚC GIẢI KHÁT");
        title.setForeground(new Color(20, 50, 100)); // Màu xanh đậm cho sang trọng
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));

        lblChucNang = new JLabel("");
        lblChucNang.setForeground(new Color(255, 255, 255)); // Màu đỏ cho nổi bật giống file cũ
        lblChucNang.setFont(new Font("Segoe UI", Font.BOLD, 15));

        leftPanel.add(logo);
        leftPanel.add(title);
        leftPanel.add(new JLabel("|"));
        leftPanel.add(lblChucNang);

        // ===== PANEL BÊN PHẢI (User + Nút điều khiển) =====
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        rightPanel.setOpaque(false);

        // Icon User và Popup Menu
        CirclePanel userIcon = new CirclePanel("/Img/user.png", 35);
        setupUserPopup(userIcon);

        JLabel user = new JLabel("Chào bạn " + nameUser);
        user.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Cụm nút điều khiển cửa sổ (Tắt, Thu nhỏ, Phóng to)
        JPanel pnlWindowControls = new JPanel(new GridLayout(1, 3, 0, 0));
        pnlWindowControls.setOpaque(false);
        setupWindowButtons(pnlWindowControls);

        rightPanel.add(userIcon);
        rightPanel.add(user);
        rightPanel.add(Box.createHorizontalStrut(15));
        rightPanel.add(pnlWindowControls);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
    }

    // Hàm cập nhật tên chức năng khi bấm Menu bên trái
    public void setTitleCN(String text) {
        this.lblChucNang.setText(text.toUpperCase());
    }

    private void setupWindowButtons(JPanel pnl) {
        Image iconExit = new ImageIcon(getClass().getResource("/Img/exit.jpg")).getImage();
        Image scaleExit = iconExit.getScaledInstance(22, 22, Image.SCALE_SMOOTH);

        Image iconBig = new ImageIcon(getClass().getResource("/Img/square.jpg")).getImage();
        Image scaleBig = iconBig.getScaledInstance(17, 17, Image.SCALE_SMOOTH);

        Image iconRestore = new ImageIcon(getClass().getResource("/Img/zoomout.jpg")).getImage();
        Image scaleZoom = iconRestore.getScaledInstance(20, 20, Image.SCALE_SMOOTH);

        Image iconHide = new ImageIcon(getClass().getResource("/Img/Hide.jpg")).getImage();
        Image scaleHide = iconHide.getScaledInstance(20, 20, Image.SCALE_SMOOTH);

        // Nút ẩn (Minimize)
        hideBtn = new JButton(new ImageIcon(scaleHide));
        styleBtn(hideBtn);
        hideBtn.addActionListener(e -> {
            Window wind = SwingUtilities.getWindowAncestor(this);
            if (wind instanceof JFrame) {
                JFrame frame = (JFrame) wind;
                frame.setExtendedState(JFrame.ICONIFIED);
            } else if (wind instanceof Frame) { // phòng khi
                ((Frame) wind).setExtendedState(Frame.ICONIFIED);
            }
        });

        // Nút phóng to / thu nhỏ
        bigBtn = new JButton(new ImageIcon(scaleBig));
        styleBtn(bigBtn);
        bigBtn.addActionListener(e -> {
            Window wind = SwingUtilities.getWindowAncestor(this);
            if (wind instanceof Frame) {
                Frame frame = (Frame) wind;
                // Nếu cửa sổ đang phóng to thì sẽ thu nhỏ
                if (frame.getExtendedState() == Frame.MAXIMIZED_BOTH) {
                    frame.setExtendedState(Frame.NORMAL);
                    bigBtn.setIcon(new ImageIcon(scaleBig));
                }
                // Nếu cửa sổ đang thu nhỏ thì sẽ phóng to
                else {
                    frame.setExtendedState(Frame.MAXIMIZED_BOTH);
                    bigBtn.setIcon(new ImageIcon(scaleZoom));
                }
            }
        });

        // Nút đóng
        closeBtn = new JButton(new ImageIcon(scaleExit));
        styleBtn(closeBtn);
        // Giữ hành vi hover màu đỏ cho nút đóng (tương tự file cũ)
        closeBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { closeBtn.setBackground(Color.RED); }
            @Override
            public void mouseExited(MouseEvent e) { closeBtn.setBackground(HEADER_COLOR); }
        });
        closeBtn.addActionListener(e -> System.exit(0));

        pnl.add(hideBtn);
        pnl.add(bigBtn);
        pnl.add(closeBtn);
    }

    private void styleBtn(JButton btn) {
        Color colorDef = HEADER_COLOR;
        Color colorHover = new Color(255, 200, 200);

        btn.setPreferredSize(new Dimension(30, 30));
        btn.setBorder(null);
        btn.setBackground(colorDef);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // nếu là nút đóng, ta đã override để thành đỏ; kiểm tra để tránh ghi đè
                if (btn != closeBtn) btn.setBackground(colorHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(colorDef);
            }
        });
    }


    private void setupUserPopup(CirclePanel userIcon) {
        popupMenu = new JPopupMenu();
        JMenuItem hoSoItem = new JMenuItem("Hồ sơ");
        JMenuItem doiMatKhauItem = new JMenuItem("Đổi mật khẩu");
        JMenuItem dangXuatItem = new JMenuItem("Đăng xuất");

        popupMenu.add(hoSoItem);
        popupMenu.add(doiMatKhauItem);
        popupMenu.add(dangXuatItem);

        userIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        userIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                popupMenu.show(userIcon, 0, userIcon.getHeight());
            }
        });

        hoSoItem.addActionListener(e -> mainFrame.hienThiTrang("Hồ sơ"));
        dangXuatItem.addActionListener(e -> {
            mainFrame.dispose();
            new ManHinhDangNhap().setVisible(true);
        });
    }
}