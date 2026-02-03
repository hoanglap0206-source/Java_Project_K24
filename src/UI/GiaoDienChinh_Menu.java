package UI;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class GiaoDienChinh_Menu extends JPanel {
    String[] menuItem={"Quản lý tài khoản", "Quản lý nhà cung cấp", "Thông tin khách hàng", "Quản lý sản phẩm", "Nhập kho","Phiếu nhập", "Xuất kho","Phiếu xuất","Tồn kho","Báo cáo","Áp thuế" ,"Kệ kho"};

    private JButton[] btnList = new JButton[menuItem.length];
    private GiaoDienChinh_Header hd ;
    private GiaoDienChinh_Content ct;

    public GiaoDienChinh_Menu(GiaoDienChinh_Header hd, GiaoDienChinh_Content ct){ // Truyền hd vào để lưu title
        this.hd=hd;
        this.ct = ct;

        setPreferredSize(new Dimension(220,0));
        setBackground(new Color(66,160,203));
        setLayout(new BorderLayout());

        initAvatar();

        JPanel listMenu=new JPanel(new GridLayout(0,1,5,5));
        listMenu.setBackground(new Color(188, 204, 209));

        for (int i=0; i<menuItem.length; i++){
            String text = menuItem[i];
            btnList[i] = new JButton(text);
            styleMenuButton(btnList[i]);
            listMenu.add(btnList[i]);
        }

        //gọi hàm thay đổi title và PANEL content
        clickButton();

        JPanel pnlCenterWrap=new JPanel(new BorderLayout());
        pnlCenterWrap.setBackground(new Color(66,160,203));
        pnlCenterWrap.add(listMenu,BorderLayout.NORTH);
        add(pnlCenterWrap,BorderLayout.CENTER);

        JScrollPane scrollPane=new JScrollPane(pnlCenterWrap);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane,BorderLayout.CENTER);

        JButton btnlogout=new JButton("Đăng xuất");
        styleMenuButton(btnlogout);
        JPanel pnlLogout=new JPanel(new FlowLayout());
        pnlLogout.setBackground(new Color(66,160,203));
        pnlLogout.add(btnlogout);
        add(pnlLogout,BorderLayout.SOUTH);
    }

    public void styleMenuButton(JButton btn){
        btn.setPreferredSize(new Dimension(200, 40));
        btn.setBackground(new Color(179, 213, 226));
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setFocusPainted(false);
    }

    public void initAvatar(){
        JPanel pnlAvatar =new JPanel(new GridBagLayout());
        pnlAvatar.setPreferredSize(new Dimension(0,150));
        pnlAvatar.setBackground(new Color(135,206,235));
        JLabel lblAvatar=new JLabel();
        lblAvatar.setPreferredSize(new Dimension(100,100));
        lblAvatar.setBorder(new LineBorder(Color.BLACK,2));
        lblAvatar.setHorizontalAlignment(JLabel.CENTER);

        String path= "Anh.jpg";
        File imgfile=new File(path);
        if(imgfile.exists()){
            ImageIcon icon=new ImageIcon(path);
            Image img=icon.getImage();
            Image newImg=img.getScaledInstance(100,100,Image.SCALE_SMOOTH);
            lblAvatar.setIcon(new ImageIcon(newImg));
        }
        else{
            lblAvatar.setText("No Image");
            lblAvatar.setForeground(Color.RED); // Chữ màu đỏ cho dễ thấy
            lblAvatar.setBackground(Color.WHITE);
            lblAvatar.setOpaque(true);
            System.out.println("Không tìm thấy file ảnh: " + imgfile.getAbsolutePath());
        }
        pnlAvatar.add(lblAvatar);
        this.add(pnlAvatar,BorderLayout.NORTH);
    }
    //Hàm đổi title
    public void clickButton(){
        // Ví dụ đổi title
        for (int i = 0; i < menuItem.length; i++){
            String txtContent = menuItem[i]; //Lấy text từ button để ví dụ cho phần panel Content
            String txtHeader = menuItem[i].toUpperCase();

            btnList[i].addActionListener(e -> {
                hd.setTitleCN(txtHeader);
                ct.showPanel(txtContent); //Xuất tên của từng nút lên panel
            });
        }
    }
    public static void main(String[] args){
        JFrame frame = new JFrame("Test Menu Layout");
        frame.setSize(300, 700); // Kích thước cửa sổ test
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GiaoDienChinh_Header header = new GiaoDienChinh_Header();
        GiaoDienChinh_Content content = new GiaoDienChinh_Content();
        // Thêm MenuPanel vào frame
        frame.add(new GiaoDienChinh_Menu(header, content)); //Đã sửa

        frame.setVisible(true);
    }

}

