package UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GiaoDienChinh_Header extends JPanel {
    private JLabel title;
    private String titleCN = "";
    private JButton closeBtn, hideBtn, bigBtn;
    public String getTitleCN() {
        return titleCN;
    }
    public void setTitleCN(String titleCN) {
        this.titleCN = titleCN;
        updateTitle();
    }

    public GiaoDienChinh_Header(){
        this("");
    }

    public GiaoDienChinh_Header(String nameUser) {
        setLayout(new BorderLayout());
        setBackground(new Color(170, 211, 255)); // Màu nền xám
        setPreferredSize(new Dimension(0, 35)); // Chiều cao cố định 35px
        setBorder(new EmptyBorder(0, 15, 0, 15)); // Padding 2 bên

        title = new JLabel();
        updateTitle();
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setForeground(Color.BLACK);
        this.add(title, BorderLayout.WEST);


       JPanel pnlRight = new JPanel(new GridLayout(1, 4, 10, 0));
        pnlRight.setBackground(new Color(170, 211, 255));

        JLabel greet = new JLabel("Chào bạn " + nameUser); // Sau này có thể truyền tên vào đây
        greet.setFont(new Font("Arial", Font.BOLD, 12));
        greet.setForeground(Color.BLACK);


        JPanel pnlBtn_Out_Zoom_Hide = new JPanel(new GridLayout(0, 3, 10, 0));
        pnlBtn_Out_Zoom_Hide.setBackground(new Color(170, 211, 255));
        //Button thoát
        Image iconExit = new ImageIcon(getClass().getResource("/Img/exit.jpg")).getImage();
        Image scaleExit = iconExit.getScaledInstance(22,22,Image.SCALE_SMOOTH);
        this.closeBtn = new JButton(new ImageIcon(scaleExit));
        this.styleBtn(closeBtn);
        this.closeBtn.addActionListener(e ->System.exit(0));

        //Button phóng lớn/nhỏ
        //Nút phóng to
        Image iconBig = new ImageIcon(getClass().getResource("/Img/square.jpg")).getImage();
        Image scaleBig = iconBig.getScaledInstance(17,17,Image.SCALE_SMOOTH);

        //Nút thu nhỏ
        Image iconRestore = new ImageIcon(getClass().getResource("/Img/zoomout.jpg")).getImage();
        Image scaleZoom = iconRestore.getScaledInstance(20,20, Image.SCALE_SMOOTH);
        this.bigBtn = new JButton(new ImageIcon(scaleBig));

        this.styleBtn(bigBtn);
        this.bigBtn.addActionListener(e->{
            Window wind = SwingUtilities.getWindowAncestor(this);
            if(wind instanceof Frame){
                Frame frame = (Frame) wind;
                //Nếu cửa sổ đang phóng to thì sẽ thu nhỏ
                if(frame.getExtendedState() == Frame.MAXIMIZED_BOTH) {
                    frame.setExtendedState(Frame.NORMAL);
                    bigBtn.setIcon(new ImageIcon(scaleBig));
                }
                //Nếu cửa sổ đang thu nhỏ thì sẽ phóng to
                else {
                    frame.setExtendedState(Frame.MAXIMIZED_BOTH);
                    bigBtn.setIcon(new ImageIcon(scaleZoom));
                }
            }
        });

        //Button ẩn
        Image iconZoomOut = new ImageIcon(getClass().getResource("/Img/Hide.jpg")).getImage();
        Image scaleHide = iconZoomOut.getScaledInstance(20,20,Image.SCALE_SMOOTH);
        this.hideBtn = new JButton(new ImageIcon(scaleHide));
        this.styleBtn(hideBtn);
        this.hideBtn.addActionListener(e->{
            Window wind = SwingUtilities.getWindowAncestor(this);
            if(wind instanceof JFrame){
                JFrame frame = (JFrame) wind;
                frame.setExtendedState(JFrame.ICONIFIED);
            }
        });

        pnlBtn_Out_Zoom_Hide.add(this.hideBtn);
        pnlBtn_Out_Zoom_Hide.add(this.bigBtn);
        pnlBtn_Out_Zoom_Hide.add(this.closeBtn);

        pnlRight.add(greet);
        pnlRight.add(pnlBtn_Out_Zoom_Hide);

        add(pnlRight, BorderLayout.EAST);
    }

    //Hàm khởi tạo cơ bản của các BUTTON
    private void styleBtn(JButton btn){
        Color colorDef = new Color(170, 211, 255);
        Color colorHover = new Color(255,200,200);

        setPreferredSize(new Dimension(30, 30));
        btn.setBorder(null);
        btn.setBackground(colorDef);

        btn.setContentAreaFilled(true);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e){
                btn.setBackground(colorHover);
            }

            @Override
            public void mouseExited(MouseEvent e){
                btn.setBackground(colorDef);
            }
        });
    }



    private void updateTitle(){
        title.setText("QUẢN LÝ KHO NƯỚC GIẢI KHÁT  |  " + titleCN);
    }
}

