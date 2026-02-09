package UI;

import javax.swing.*;
import java.awt.*;

public class ThongTinKH_GUI extends JPanel {
    public ThongTinKH_GUI(){
        setLayout(new BorderLayout());
        add(new GiaoDienChinh_TopContent(), BorderLayout.NORTH);

        //Code ở pnlCode_QLKh
        JPanel pnlCode_QLKh = new JPanel();
        pnlCode_QLKh.setBackground(new Color(200,201,111));//Xóa trước khi code
        pnlCode_QLKh.add(new JLabel("GIAO DIỆN THÔNG TIN KHÁCH HÀNG", SwingConstants.CENTER));//Xóa trước khi code

        add(pnlCode_QLKh, BorderLayout.CENTER);
    }
}
