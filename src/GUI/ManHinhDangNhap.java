package GUI;

import BUS.NV_BUS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class ManHinhDangNhap extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;

    // Màu thanh tiêu đề
    private final Color HEADER_COLOR = new Color(0, 128, 255);

    // Biến kéo thả cửa sổ
    private int xMouse, yMouse;

    public ManHinhDangNhap() {
        setTitle("Đăng nhập hệ thống");
        ImageIcon icon = new ImageIcon(getClass().getResource("/Img/Logo.jpg"));
        setIconImage(icon.getImage());

        setUndecorated(true);

        setSize(800, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Thanh tiêu đề tùy chỉnh nằm ở trên cùng
        add(buildTitleBar(), BorderLayout.NORTH);

        // Phần nội dung chính
        JPanel content = new JPanel(new GridLayout(1, 2));
        initLeft(content);
        initRight(content);
        add(content, BorderLayout.CENTER);
    }

    //THANH TIÊU ĐỀ TÙY CHỈNH
    private JPanel buildTitleBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(HEADER_COLOR);
        bar.setPreferredSize(new Dimension(800, 36));
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 220, 230)));

        // Nhãn tên ứng dụng bên trái
        JLabel lblTitle = new JLabel("  HỆ THỐNG QUẢN LÝ KHO NƯỚC GIẢI KHÁT");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitle.setForeground(new Color(20, 50, 100));
        bar.add(lblTitle, BorderLayout.WEST);

        // Cụm 3 nút điều khiển bên phải
        JPanel pnlBtns = new JPanel(new GridLayout(1, 3, 0, 0));
        pnlBtns.setOpaque(false);
        buildWindowButtons(pnlBtns);
        bar.add(pnlBtns, BorderLayout.EAST);

        // Kéo thả cửa sổ khi nhấn vào thanh tiêu đề
        bar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                xMouse = e.getX();
                yMouse = e.getY();
            }
        });
        bar.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                setLocation(e.getXOnScreen() - xMouse, e.getYOnScreen() - yMouse);
            }
        });

        return bar;
    }

    private void buildWindowButtons(JPanel pnl) {
        // Load & scale icon
        Image iconHide = new ImageIcon(getClass().getResource("/Img/Hide.jpg")).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        Image iconBig = new ImageIcon(getClass().getResource("/Img/square.jpg")).getImage().getScaledInstance(17, 17, Image.SCALE_SMOOTH);
        Image iconRestore = new ImageIcon(getClass().getResource("/Img/zoomout.jpg")).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        Image iconExit = new ImageIcon(getClass().getResource("/Img/exit.jpg")).getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH);

        //Nút ẩn
        JButton hideBtn = new JButton(new ImageIcon(iconHide));
        styleWindowBtn(hideBtn, false);
        hideBtn.addActionListener(e -> setState(JFrame.ICONIFIED));

         //Nút phóng to / khôi phục
        JButton bigBtn = new JButton(new ImageIcon(iconBig));
        styleWindowBtn(bigBtn, false);
        bigBtn.addActionListener(e -> {
            if (getExtendedState() == JFrame.MAXIMIZED_BOTH) {
                setExtendedState(JFrame.NORMAL);
                bigBtn.setIcon(new ImageIcon(iconBig));
            } else {
                setExtendedState(JFrame.MAXIMIZED_BOTH);
                bigBtn.setIcon(new ImageIcon(iconRestore));
            }
        });

        // Nút đóng
        JButton closeBtn = new JButton(new ImageIcon(iconExit));
        styleWindowBtn(closeBtn, true); // true = hover đỏ
        closeBtn.addActionListener(e -> System.exit(0));

        pnl.add(hideBtn);
        pnl.add(bigBtn);
        pnl.add(closeBtn);
    }

    // Style chung cho nút cửa sổ, isClose=true thì hover màu đỏ
    private void styleWindowBtn(JButton btn, boolean isClose) {
        btn.setPreferredSize(new Dimension(36, 36));
        btn.setBorder(null);
        btn.setBackground(HEADER_COLOR);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(isClose ? Color.RED : new Color(200, 225, 255));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(HEADER_COLOR);
            }
        });
    }

    // PANEL TRÁI
    private void initLeft(JPanel container) {
        JPanel left = new JPanel();
        left.setBackground(Color.decode("#0A213D"));
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        left.add(Box.createVerticalGlue());

        CirclePanel logo = new CirclePanel("/Img/Logo.jpg", 120);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(logo);

        left.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel lblTitle = new JLabel("HỆ THỐNG QUẢN LÝ KHO");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(lblTitle);

        left.add(Box.createVerticalGlue());
        container.add(left);
    }

    //PANEL PHẢI
    private void initRight(JPanel container) {
        JPanel right = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        right.setBackground(new Color(250,250,250));
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel header = new JLabel("ĐĂNG NHẬP NGƯỜI DÙNG");
        header.setFont(new Font("Segoe UI", Font.BOLD, 20));
        right.add(header, gbc);

        gbc.gridy++;
        right.add(new JLabel("Mã người dùng"), gbc);

        gbc.gridy++;
        txtUsername = createUnderlineField();
        right.add(txtUsername, gbc);

        gbc.gridy++;
        right.add(new JLabel("Mật khẩu"), gbc);

        gbc.gridy++;
        txtPassword = createUnderlinePassword();
        right.add(txtPassword, gbc);

        gbc.gridy++;
        JCheckBox showPass = new JCheckBox("Hiện mật khẩu");
        showPass.setBackground(new Color(250,250,250));
        showPass.addActionListener(e ->
                txtPassword.setEchoChar(showPass.isSelected() ? (char) 0 : '•')
        );
        right.add(showPass, gbc);

        gbc.gridy++;
        JButton btnLogin = new JButton("XÁC NHẬN");
        btnLogin.setBackground(Color.decode("#0058CA"));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setPreferredSize(new Dimension(200, 36));
        btnLogin.addActionListener(e -> handleLogin());
        gbc.insets = new Insets(20, 8, 0, 8);
        right.add(btnLogin, gbc);

        container.add(right);
    }

    private JTextField createUnderlineField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setOpaque(false);
        tf.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(120, 120, 120)));
        return tf;
    }

    private JPasswordField createUnderlinePassword() {
        JPasswordField pf = new JPasswordField();
        pf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pf.setOpaque(false);
        pf.setEchoChar('•');
        pf.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(120, 120, 120)));
        return pf;
    }

    private void handleLogin() {
        String txtAcc  = txtUsername.getText().trim();
        String txtPass = new String(txtPassword.getPassword());

        if (txtAcc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã người dùng");
            txtUsername.requestFocusInWindow();
            return;
        }
        if (txtPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mật khẩu");
            txtPassword.requestFocusInWindow();
            return;
        }

        NV_BUS nvBus = new NV_BUS();
        if (nvBus.login(txtAcc, txtPass)) {
            ManHinhChinh mainFrame = new ManHinhChinh(txtAcc);
            mainFrame.setVisible(true);
            SwingUtilities.invokeLater(() -> new PopupChaoMung(mainFrame, nvBus.getTenNV_BUS(txtAcc)));
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu");
            txtUsername.setText("");
            txtPassword.setText("");
            txtUsername.requestFocusInWindow();
        }
    }

    public static void main(String[] args) {
        new ManHinhDangNhap().setVisible(true);
    }
}