package UI;

import javax.swing.*;
import java.awt.*;

public class KeKho_GUI extends JPanel {
    public KeKho_GUI(){
        setLayout(new BorderLayout());
        add(new GiaoDienChinh_TopContent(), BorderLayout.NORTH);

        //Code ở pnlCode_KeKho
        JPanel pnlCode_KeKho = new JPanel();
        pnlCode_KeKho.setBackground(new Color(204,255,153)); //Xóa trước khi code
        pnlCode_KeKho.add(new JLabel("GIAO DIỆN KỆ KHO", SwingConstants.CENTER)); //Xóa trước khi code

        add(pnlCode_KeKho, BorderLayout.CENTER);
    }
}
