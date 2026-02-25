package UI;

import BUS.NV_BUS;

import javax.swing.*;
import java.awt.*;

public class  GiaoDienDangNhap extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public GiaoDienDangNhap() {
        setTitle("Đăng nhập hệ thống");
        setSize(800, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(1, 2));

        initLeft();
        initRight();
    }

    private void initLeft() {
        JPanel left = new JPanel();
        left.setBackground(Color.decode("#0A213D"));
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        left.add(Box.createVerticalGlue());

        CirclePanel logo = new CirclePanel("src/Img/ConRua.jpg");
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(logo);

        left.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel lblTitle = new JLabel("HỆ THỐNG QUẢN LÝ KHO");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(lblTitle);

        left.add(Box.createVerticalGlue());
        add(left);
    }

    private void initRight() {
        JPanel right = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel header = new JLabel("ĐĂNG NHẬP NGƯỜI DÙNG");
        header.setFont(new Font("Segoe UI", Font.BOLD, 20));
//        header.setHorizontalAlignment(SwingConstants.CENTER);
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
        showPass.addActionListener(e ->
                txtPassword.setEchoChar(
                        showPass.isSelected() ? (char) 0 : '•'
                )
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

        add(right);
    }

    private JTextField createUnderlineField() {
        JTextField tf = new JTextField();

        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setOpaque(false);

        tf.setBorder(BorderFactory.createMatteBorder(
                0, 0, 2, 0, new Color(120, 120, 120)
        ));

        return tf;
    }

    private JPasswordField createUnderlinePassword() {
        JPasswordField pf = new JPasswordField();

        pf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pf.setOpaque(false);
        pf.setEchoChar('•');

        pf.setBorder(BorderFactory.createMatteBorder(
                0, 0, 2, 0, new Color(120, 120, 120)
        ));

        return pf;
    }

    private void handleLogin() {
        String txtAcc = txtUsername.getText().trim();
        String txtPass = new String(txtPassword.getPassword());

        if(txtAcc.isEmpty()){
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã người dùng");
            txtUsername.requestFocusInWindow();
            return;
        }
        if(txtPass.isEmpty()){
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mật khẩu");
            txtPassword.requestFocusInWindow();
            return;
        }

        NV_BUS nvBus = new NV_BUS();
        if(nvBus.login(txtAcc, txtPass)){
            GiaoDienChinh_Main mainFrame = new GiaoDienChinh_Main(txtAcc);
            mainFrame.setVisible(true);

            //Frame chào mừng hiện lên + Tên người dùng
            SwingUtilities.invokeLater(() ->{
                new Greetings_GUI(mainFrame, nvBus.getTenNV_BUS(txtAcc));
            });
            this.dispose();//Thoát cái giao diện đăng nhập
        }
        else{
            JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu");

            txtUsername.setText("");
            txtPassword.setText("");

            txtUsername.requestFocusInWindow();
        }
    }
//hello
    public static void main(String[] args) {
        new GiaoDienDangNhap().setVisible(true);
    }
}
