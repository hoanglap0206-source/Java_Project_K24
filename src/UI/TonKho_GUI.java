package UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TonKho_GUI extends JPanel {
    public  TonKho_GUI(){
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel top=new JPanel(new FlowLayout(FlowLayout.LEFT,20,10));
        top.setBackground(Color.WHITE);
        top.setBorder(new EmptyBorder(10,0,10,0));

        JPanel pnlWarningTime = createInputPanel("Nhập thời gian cảnh báo:", new Color(255, 255, 204));

        JPanel pnlExpiring = createInputPanel("Số sản phẩm sắp hết hạn:", new Color(255, 102, 0));

        ((JTextField)pnlExpiring.getComponent(0)).setForeground(Color.WHITE);
        ((JTextField)pnlExpiring.getComponent(0)).setFont(new Font("Arial", Font.BOLD, 14));

        top.add(pnlWarningTime);
        top.add(pnlExpiring);

        add(top,BorderLayout.NORTH);

        String[] cot={"Mã SP","Tên SP","Đơn vị tính","Số lượng","Đơn giá","Ngày nhập","Mã kệ"};

        Object[][] data = {
                {"SP001", "Pepsi", "Lon", "100", "10.000", "01/01/2024", "01/01/2025", "K01"},
                {"SP002", "Coca", "Chai", "50", "12.000", "15/02/2024", "15/02/2025", "K02"},
                {"SP003", "Sting", "Chai", "200", "11.000", "10/03/2024", "10/03/2025", "K01"},
                {"SP004", "Trà xanh", "Chai", "5", "9.000", "05/01/2024", "05/04/2024", "K03"} // Sắp hết hạn
        };

        DefaultTableModel model=new DefaultTableModel(data,cot);
        JTable table=new JTable(model);

        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(230, 230, 230));

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);


    }
    private JPanel createInputPanel(String title, Color bgColor) {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(bgColor);
        pnl.setBorder(BorderFactory.createLineBorder(Color.BLACK));



        JTextField txt = new JTextField(title);
        txt.setBackground(bgColor);
        txt.setBorder(new EmptyBorder(5, 10, 5, 10));
        txt.setFont(new Font("Arial", Font.PLAIN, 13));

        txt.setPreferredSize(new Dimension(250, 35));

        pnl.add(txt);
        return pnl;
    }
}
