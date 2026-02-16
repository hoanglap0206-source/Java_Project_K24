package UI;

import BUS.NV_BUS;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.MatteBorder;

public class GiaoDienDK_Right extends JPanel {
    public GiaoDienDK_Right(){
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        DangNhap();
    }
    public void DangNhap(){
        GridBagConstraints gbc=new GridBagConstraints();
        gbc.insets=new Insets(10,10,10,10);
        gbc.fill=GridBagConstraints.HORIZONTAL;

        JLabel Title=new JLabel("ĐĂNG NHẬP NGƯỜI DÙNG");
        Title.setFont(new Font("Arial",Font.BOLD,15));
        Title.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx=0;
        gbc.gridy=0;
        gbc.gridwidth=2;

        add(Title,gbc);

        JTextField username=new JTextField();
        username.setBorder(BorderFactory.createTitledBorder(
                new MatteBorder(0, 0, 2, 0, Color.BLACK), "Tên đăng nhập"
        ));
        username.setBackground(Color.WHITE);
        gbc.gridx=0;
        gbc.gridy=1;
        gbc.gridwidth=2;
        gbc.ipadx=10;
        add(username,gbc);

        JPasswordField password=new JPasswordField();
        password.setBorder(BorderFactory.createTitledBorder(
                new MatteBorder(0, 0, 2, 0, Color.BLACK), "Mật khẩu"
        ));
        password.setBackground(Color.WHITE);
        gbc.gridx=0;
        gbc.gridy=2;
        gbc.gridwidth=2;


        add(password,gbc);

        JPanel option=new JPanel(new BorderLayout());
        option.setBackground(Color.WHITE);

        JCheckBox keeppass=new JCheckBox("Ghi nhớ mật khẩu      ");
        keeppass.setFont(new Font("Arial", Font.PLAIN, 12));
        keeppass.setBackground(Color.WHITE);

        JLabel forgotpass=new JLabel("Quên mật khẩu");
        forgotpass.setFont(new Font("Arial", Font.PLAIN, 12));
        forgotpass.setCursor(new Cursor(Cursor.HAND_CURSOR));

        option.add(keeppass,BorderLayout.WEST);
        option.add(forgotpass,BorderLayout.EAST);

        gbc.gridx=0;
        gbc.gridy=3;
        gbc.gridwidth=2;
        gbc.ipadx=0;
        add(option,gbc);

        JButton confrim=new JButton("XÁC NHẬN");
        confrim.setBackground(new Color(0x0BA895));
        confrim.setForeground(Color.WHITE);
        confrim.setFont(new Font("Arial", Font.BOLD, 14));
        confrim.setFocusPainted(false);
        confrim.setBorderPainted(false);
        confrim.setPreferredSize(new Dimension(200, 40));
        gbc.gridx=0;
        gbc.gridy=4;
        gbc.gridwidth=2;
        gbc.insets = new Insets(30, 10, 10, 10);
        add(confrim,gbc);

//        Lập  sửa
        confrim.addActionListener(e->{
            String txtAcc = username.getText().trim();
            char[] pass = password.getPassword();
            String txtPass = new String(pass);
            if(txtAcc.isEmpty() || txtPass.isEmpty()){
                JOptionPane.showMessageDialog(this, "Không được để trống dữ liệu");
                return;
            }
            NV_BUS nv = new NV_BUS();
            boolean login_Success = nv.login(txtAcc,txtPass);
            if (login_Success){
                JOptionPane.showMessageDialog(this, "Đăng nhập thành công");
                username.setText("");
                password.setText("");
//                GiaoDienChinh_Main gd = new GiaoDienChinh_Main();
            }else {
                JOptionPane.showMessageDialog(this, "Đăng nhập thất bại,sai tài khoản hoặc mật khẩu");
                username.setText("");
                password.setText("");
            }
        });
    }
}

