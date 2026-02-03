package UI;

import javax.swing.*;
import java.awt.*;

public class ThongTinKH_GUI extends JPanel {
    public ThongTinKH_GUI(){
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBackground(new Color(200,201,111));
        add(new JLabel("GIAO DIỆN THÔNG TIN KHÁCH HÀNG", SwingConstants.CENTER));
    }
}
