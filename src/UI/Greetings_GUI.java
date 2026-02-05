package UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.FontUIResource;
import java.awt.*;

public class Greetings_GUI extends JFrame {
    public Greetings_GUI (){
        setTitle("Chào mừng");

        setSize(800,600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel pnlMain=new JPanel();
        pnlMain.setBackground(new Color(100,100,100));
        pnlMain.setLayout(new GridBagLayout());

        JPanel pnlCard=new JPanel();
        pnlCard.setBackground(new Color(210,210,210));
        pnlCard.setLayout(new GridBagLayout());
        pnlCard.setPreferredSize(new Dimension(400,250));
        pnlCard.setBorder(new EmptyBorder(20,20,20,20));

        JPanel pnlIcon=new JPanel(){
            @Override
                    protected void paintComponent(Graphics g){
                super.paintComponents(g);
                Graphics2D g2=(Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                int w=getWidth();
                int h=getHeight();
                int size=Math.min(w,h)-10;
                int x=(w-size)/2;
                int y=(h-size)/2;

                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(4));
                g2.drawOval(x,y,size,size);

                int headSize=size/3;
                g2.fillOval(x+(size-headSize)/2,y+size/4,headSize,headSize);

                int bodyW=size/2;
                int bodyH=size/3;
                g2.fillArc(x+(size-bodyW)/2,y+size/2+5,bodyW,bodyH*2,0,180);
            }
        };
        pnlIcon.setOpaque(false);
        pnlIcon.setPreferredSize(new Dimension(100,100));
        pnlIcon.setMaximumSize(new Dimension(100,100));
        pnlIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblWelcome=new JLabel("Chào mừng   ");
        lblWelcome.setFont(new Font("Arial",Font.BOLD,24));
        lblWelcome.setForeground(Color.BLACK);
        lblWelcome.setAlignmentX(Component.CENTER_ALIGNMENT);

        pnlCard.add(Box.createVerticalBox());
        pnlCard.add(pnlIcon);
        pnlCard.add(Box.createVerticalStrut(20));
        pnlCard.add(lblWelcome);
        pnlCard.add(Box.createVerticalGlue());
        pnlMain.add(pnlCard);
        setContentPane(pnlMain);
    }
    public static void main(String[] args){
        SwingUtilities.invokeLater(()->{
            new Greetings_GUI().setVisible(true);
        });
    }
}
