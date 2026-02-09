package UI;

import javax.swing.*;
import java.awt.*;

public class BaoCao_GUI extends JPanel {
    public BaoCao_GUI(){
        setLayout(new BorderLayout());

        //Code ở pnlCode_BaoCao
        JPanel pnlCode_BaoCao = new JPanel();
        pnlCode_BaoCao.setBackground(new Color(255,153,153));//Xóa trước khi code
        pnlCode_BaoCao.add(new JLabel("GIAO DIỆN BÁO CÁO", SwingConstants.CENTER)); //Xóa trước khi code

        add(pnlCode_BaoCao, BorderLayout.CENTER);
    }
}
