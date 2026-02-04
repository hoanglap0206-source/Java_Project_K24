package UI;

import javax.sound.sampled.Line;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class GiaoDienDK_Left extends JPanel {
    public GiaoDienDK_Left(){
        setLayout(new GridBagLayout());

        setBackground(new Color(93, 191, 218));

        initAvatar();
    }
    public void initAvatar(){
        JPanel pnlLogo=new JPanel(){
            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                Graphics g2= (Graphics) g;

                g2.setColor(new Color(148, 185, 210));
                g2.fillOval(0, 0, getWidth(), getHeight());

            }
        };
        pnlLogo.setPreferredSize(new Dimension(150, 150));
        pnlLogo.setOpaque(false);
        pnlLogo.setLayout(new GridBagLayout());

        JLabel lblText = new JLabel("logo");
        lblText.setFont(new Font("Arial", Font.BOLD, 30));
        lblText.setForeground(Color.BLACK);
        pnlLogo.add(lblText);
        this.add(pnlLogo);
    }

}

