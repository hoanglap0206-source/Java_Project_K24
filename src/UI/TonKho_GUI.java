package UI;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TonKho_GUI extends JPanel {

    public TonKho_GUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);


        GiaoDienChinh_TopContent header = new GiaoDienChinh_TopContent();


        JPanel pnlCompactInputs = new JPanel(new GridLayout(2, 1, 0, 5));
        pnlCompactInputs.setOpaque(false);


        JPanel pnlWarn = createCompactInput("Cảnh báo (ngày):", new Color(255, 255, 204), Color.BLACK);

        JPanel pnlExp = createCompactInput("Sắp hết hạn (SP):", new Color(255, 102, 0), Color.WHITE);

        pnlCompactInputs.add(pnlWarn);
        pnlCompactInputs.add(pnlExp);


        header.setCustomCenterPanel(pnlCompactInputs);


        add(header, BorderLayout.NORTH);



        String[] columnNames = {"Mã SP", "Tên SP", "Đơn vị tính", "Số lượng", "Đơn giá", "Ngày nhập", "Ngày hết hạn", "Mã kệ"};
        Object[][] data = {
                {"SP001", "Pepsi", "Lon", "100", "10.000", "01/01/2024", "01/01/2025", "K01"},
                {"SP002", "Coca", "Chai", "50", "12.000", "15/02/2024", "15/02/2025", "K02"},
                {"SP003", "Sting", "Chai", "200", "11.000", "10/03/2024", "10/03/2025", "K01"},
                {"SP004", "Trà xanh", "Chai", "5", "9.000", "05/01/2024", "05/04/2024", "K03"}
        };


        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(230, 230, 230));

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }


    private JPanel createCompactInput(String labelText, Color bgColor, Color textColor) {
        JPanel pnl = new JPanel(new BorderLayout(5, 0));
        pnl.setBackground(Color.WHITE);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Arial", Font.BOLD, 11));

        JTextField txt = new JTextField();
        txt.setBackground(bgColor);
        txt.setForeground(textColor);
        txt.setCaretColor(textColor);
        txt.setFont(new Font("Arial", Font.BOLD, 12));
        // Tạo viền mỏng và padding cho ô nhập
        txt.setBorder(new CompoundBorder(
                new LineBorder(Color.LIGHT_GRAY, 1),
                new EmptyBorder(2, 5, 2, 5)
        ));
        txt.setPreferredSize(new Dimension(80, 25));

        pnl.add(lbl, BorderLayout.WEST);
        pnl.add(txt, BorderLayout.CENTER);
        return pnl;
    }
}