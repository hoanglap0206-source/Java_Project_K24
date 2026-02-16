package UI;

import BUS.PhanQuyen_BUS;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GiaoDienChinh_Menu extends JPanel {

    private Map<String, JButton> mapCNToButton = new HashMap<>();

    private String[] menuItem={"Quản lý tài khoản", "Quản lý nhà cung cấp", "Thông tin khách hàng", "Quản lý sản phẩm", "Nhập kho","Phiếu nhập", "Xuất kho","Phiếu xuất","Tồn kho","Báo cáo","Áp thuế" ,"Kệ kho"};
    private String[] maCNs = {"CN01","CN02","CN03","CN04","CN05","CN06","CN07","CN08","CN09","CN10", "CN11", "CN12"};

    private JButton[] btnList = new JButton[menuItem.length];
    private GiaoDienChinh_Header hd ;
    private GiaoDienChinh_Content ct;
    private String maNV = null;

    public GiaoDienChinh_Menu(GiaoDienChinh_Header hd, GiaoDienChinh_Content ct) {
        this(hd, ct, null); // gọi constructor chính
    }

    public GiaoDienChinh_Menu(GiaoDienChinh_Header hd, GiaoDienChinh_Content ct, String maNV){ // Truyền hd vào để lưu title
        this.hd=hd;
        this.ct = ct;
        this.maNV = maNV;

        setPreferredSize(new Dimension(220,0));
        setBackground(new Color(66,160,203));
        setLayout(new BorderLayout());

        this.intComponents();
        this.buildMapComponents();
        //gọi hàm thay đổi title và PANEL content
        this.clickButton();

        if(maNV != null && !maNV.isEmpty())
            this.applyPhanQuyen(maNV);
        else
            this.hideAllBtn();
    }

    public void intComponents(){
        this.initAvatar();

        JPanel listMenu = new JPanel(new GridLayout(0,1,5,5));
        listMenu.setBackground(new Color(188, 204, 209));
        listMenu.setBorder(BorderFactory.createEmptyBorder(8,0,8,0));

        for(int i = 0; i < menuItem.length; i++){
            JButton btn = new JButton(menuItem[i]);
            styleMenuButton(btn);
            btnList[i] = btn;
            if(i< maCNs.length)
                btn.setName(maCNs[i]);
            listMenu.add(btn);
        }

        JPanel pnlCenterWrap = new JPanel(new BorderLayout());
        pnlCenterWrap.setBackground(new Color(66,160,203));
        pnlCenterWrap.add(listMenu,BorderLayout.NORTH);
        add(pnlCenterWrap,BorderLayout.CENTER);

        JScrollPane scrollPane=new JScrollPane(pnlCenterWrap);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane,BorderLayout.CENTER);

        JButton btnlogout = new JButton("Đăng xuất");
        styleMenuButton(btnlogout);
        JPanel pnlLogout=new JPanel(new FlowLayout());
        pnlLogout.setBackground(new Color(66,160,203));

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

    private void buildMapComponents(){
        mapCNToButton.clear();
        int len = Math.min(maCNs.length, btnList.length);
        for(int i = 0; i < len; i++)
            if(btnList[i] != null && maCNs[i] != null && !maCNs[i].isEmpty())
                mapCNToButton.put(maCNs[i], btnList[i]);
    }

    public void hideAllBtn(){
        for(JButton btn : btnList)
            if(btn != null){
                btn.setVisible(false);
                btn.setEnabled(false);
            }
    }

    public void applyPhanQuyen(String maNV){
        this.maNV = maNV;

        this.hideAllBtn();

        ArrayList<String> dsCNCaNhan;

        //Nếu như người đó không có chức năng nào
        try{
            PhanQuyen_BUS pqBUS = new PhanQuyen_BUS();
            dsCNCaNhan = pqBUS.getDSQuyenCaNhan(maNV);
            if(dsCNCaNhan == null)
                dsCNCaNhan = new ArrayList<>();
        }
        catch (Exception e){
            e.printStackTrace();
            dsCNCaNhan = new ArrayList<>();
        }

        for(String cn : dsCNCaNhan){
            JButton btn = (JButton) mapCNToButton.get(cn);
            if(btn != null){
                btn.setVisible(true);
                btn.setEnabled(true);
            }
            else
                System.out.println("[PhanQuyen] Chưa map component cho: " + cn);
        }
        this.revalidate();
        this.repaint();
    }

    public void reloadForUser(String newMaNV) {
        applyPhanQuyen(newMaNV);
    }

    public static void main(String[] args){
        JFrame frame = new JFrame("Menu Layout");
        frame.setSize(300, 700); // Kích thước cửa sổ test
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GiaoDienChinh_Header header = new GiaoDienChinh_Header();
        GiaoDienChinh_Content content = new GiaoDienChinh_Content();

        GiaoDienChinh_Menu menu = new GiaoDienChinh_Menu(header, content);
        frame.add(menu, BorderLayout.WEST);

        menu.applyPhanQuyen("NV01");

        frame.setVisible(true);
    }
}

