package GUI;



import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class ChiTietSanPham_Dialog extends JDialog {


    public  ChiTietSanPham_Dialog(Window parent, String mabc, String dvt, String soluong, String dongia) {

        super(parent, "Chi tiết sản phẩm", ModalityType.APPLICATION_MODAL);
        setSize(500,280);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        JPanel pnlmain=new JPanel(new BorderLayout(20,10));
        pnlmain.setBackground(new Color(255, 255, 255));
        pnlmain.setBorder(new EmptyBorder(20,20,10,20));

        JPanel pnlInfo = new JPanel(new GridLayout(4, 1, 0, 5));
        pnlInfo.setBackground(new Color(255, 255, 255));


        pnlInfo.add(createInfoRow("Mã báo cáo",mabc));

        pnlInfo.add(createInfoRow("Đơn vị tính:", dvt));
        pnlInfo.add(createInfoRow("Số lượng:", soluong));
        pnlInfo.add(createInfoRow("Đơn giá:", dongia + " VNĐ"));

        pnlmain.add(pnlInfo, BorderLayout.CENTER);
        add(pnlmain, BorderLayout.CENTER);


        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBottom.setBackground(Color.WHITE);
        pnlBottom.setBorder(new EmptyBorder(0, 0, 10, 20));

        JButton btnClose = new JButton("Đóng");
        btnClose.setBackground(new Color(210,230,255));
        btnClose.setFocusPainted(false);
        btnClose.setPreferredSize(new Dimension(90, 30));

        btnClose.addActionListener(e -> dispose());

        pnlBottom.add(btnClose);
        add(pnlBottom, BorderLayout.SOUTH);


    }
    private JPanel createInfoRow(String label, String value) {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel(label);
        lblTitle.setPreferredSize(new Dimension(100, 20)); // Cố định chiều rộng cột tiêu đề
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Arial", Font.PLAIN, 14));
        lblValue.setForeground(Color.DARK_GRAY);

        pnl.add(lblTitle, BorderLayout.WEST);
        pnl.add(lblValue, BorderLayout.CENTER);
        return pnl;
    }
}