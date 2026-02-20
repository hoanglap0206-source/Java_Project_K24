package UI;

import BUS.PhanQuyen_BUS;
import Model.ChucNang;
import Model.PhanQuyen;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

public class GiaoDienChinh_Menu extends JPanel {
    private JButton[] btnList;
    private GiaoDienChinh_Header hd ;
    private GiaoDienChinh_Content ct;
    private String maNV;

    private JPanel listMenu;

    public GiaoDienChinh_Menu(GiaoDienChinh_Header hd, GiaoDienChinh_Content ct) {
        this(hd, ct, null); // gọi constructor chính
    }

    public GiaoDienChinh_Menu(GiaoDienChinh_Header hd, GiaoDienChinh_Content ct, String maNV){ // Truyền hd vào để lưu title
        this.hd=hd;
        this.ct = ct;
        this.maNV = maNV;

        setPreferredSize(new Dimension(220,0));
        setBackground(new Color(196,225,255));
        setLayout(new BorderLayout());

        this.iniBaseGui();
        //gọi hàm thay đổi title và PANEL content
        this.clickButton(maNV);

        if(maNV != null && !maNV.isEmpty())
            this.LoadMenuButton(maNV);
        else
            this.hideAllBtn();
    }

    public void iniBaseGui(){
        //this.initAvatar();

        listMenu = new JPanel(new GridLayout(0,1,5,5));
        listMenu.setBackground(new Color(196, 225, 255));
        listMenu.setBorder(BorderFactory.createEmptyBorder(8,0,8,0));

        JPanel pnlCenterWrap = new JPanel(new BorderLayout());
        pnlCenterWrap.setBackground(new Color(196, 225, 255));
        pnlCenterWrap.add(listMenu,BorderLayout.NORTH);
        add(pnlCenterWrap,BorderLayout.CENTER);

        JScrollPane scrollPane=new JScrollPane(pnlCenterWrap);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane,BorderLayout.CENTER);

        JButton btnlogout = new JButton("Đăng xuất");
        styleMenuButton(btnlogout);
        JPanel pnlLogout=new JPanel(new FlowLayout());
        pnlLogout.setBackground(new Color(196, 225, 255));

        btnlogout.addActionListener(e->{
            Window window = SwingUtilities.getWindowAncestor(this);

            if (window != null) {
                window.dispose(); // Đóng cửa sổ GiaoDienChinh_Main
            }

            // Khởi tạo và hiển thị lại màn hình Đăng nhập
            GiaoDienDangNhap login = new GiaoDienDangNhap();
            login.setVisible(true);
        });

        pnlLogout.add(btnlogout);
        add(pnlLogout,BorderLayout.SOUTH);
    }

    public void LoadMenuButton(String maNV){
        this.maNV = maNV;

        //Clear sạch các nút cũ
        listMenu.removeAll();

        //Lấy danh sách các chức năng từ BUS của người dùng
        PhanQuyen_BUS PQbus = new PhanQuyen_BUS();
        ArrayList<PhanQuyen> dsQuyenCaNhan = PQbus.getDSQuyenCaNhan(maNV);

        //Vòng lặp để xuất hiện các button
        if(dsQuyenCaNhan != null){
            for(PhanQuyen pq : dsQuyenCaNhan){
                ChucNang cn = pq.getChucNang();

                //Lấy tên của từng nút trong danh sách
                String tenButton = cn.getTenCN();
                String maCn = cn.getMaCN();

                //Tạo button
                JButton btn = new JButton(tenButton);
                this.styleMenuButton(btn);

                btn.setName(maCn);//Lưu để sau này nhận diện dùng để xem-them-xoa-sua

                btn.addActionListener(e ->{
                    if(this.hd != null)
                        hd.setTitleCN(tenButton.toUpperCase());
                    if(this.ct != null)
                        ct.showPanel(tenButton);
                });
                listMenu.add(btn);
            }
        }
        listMenu.revalidate();
        listMenu.repaint();
    }

    public void reloadForUser(String newMaNV) {
        this.LoadMenuButton(newMaNV);
    }

    public void styleMenuButton(JButton btn){
        btn.setPreferredSize(new Dimension(200, 40));
        btn.setBackground(new Color(179, 213, 226));
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public void initAvatar(){
        JPanel pnlAvatar =new JPanel(new GridBagLayout());
        pnlAvatar.setPreferredSize(new Dimension(0,150));
        pnlAvatar.setBackground(new Color(196, 225, 255));
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
    public void clickButton(String maNV){

        PhanQuyen_BUS PQbus = new PhanQuyen_BUS();
        ArrayList<PhanQuyen> dsQuyenCaNhan = PQbus.getDSQuyenCaNhan(maNV);

        //Kích thước của mảng
        int size = dsQuyenCaNhan.size();
        this.btnList = new JButton[size];

        // Ví dụ đổi title
        for (int i = 0; i < size; i++){
            PhanQuyen pq = dsQuyenCaNhan.get(i);
            ChucNang cn = pq.getChucNang();

            //Lấy tên của từng nút trong danh sách
            String txtContent = cn.getTenCN(); //Lấy text từ button để ví dụ cho phần panel Content
            String txtHeader = cn.getTenCN().toUpperCase();

            btnList[i] = new JButton(txtContent);
            this.styleMenuButton(btnList[i]);

            btnList[i].addActionListener(e -> {
                hd.setTitleCN(txtHeader);
                ct.showPanel(txtContent); //Xuất tên của từng nút lên panel
            });
        }
    }

    public void hideAllBtn(){
        for(JButton btn : btnList)
            if(btn != null){
                btn.setVisible(false);
                btn.setEnabled(false);
            }
    }

    public static void main(String[] args){
        JFrame frame = new JFrame("Menu Layout");
        frame.setSize(300, 700); // Kích thước cửa sổ test
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GiaoDienChinh_Header header = new GiaoDienChinh_Header();
        GiaoDienChinh_Content content = new GiaoDienChinh_Content();

        GiaoDienChinh_Menu menu = new GiaoDienChinh_Menu(header, content, "NV99");
        frame.add(menu, BorderLayout.WEST);

        frame.setVisible(true);
    }
}

