package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.MouseAdapter;


//Tạo ra giao diện mờ và đè lên giao diện chính
public class PopupChaoMung extends JDialog {
    public PopupChaoMung(JFrame main, String nameUser) {
        super(main, true);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0)); // Để JDialog hoàn toàn trong suốt

        // Set kích thước Dialog
        Dimension mainSize = main.getSize();
        int dialogWidth = mainSize.width;
        int dialogHeigh = mainSize.height;
        setSize(dialogWidth, dialogHeigh);
        setLocationRelativeTo(main);

        // Lớp nền mờ đen
        // Sử dụng GridBagLayout để pnlCard tự động vào giữa
        JPanel pnlGreeting = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(0, 0, 0, 150)); // Màu đen mờ
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        pnlGreeting.setOpaque(false);

        //pnlCard
        JPanel pnlCard = new JPanel();
        pnlCard.setLayout(new BoxLayout(pnlCard, BoxLayout.Y_AXIS));
        pnlCard.setBackground(new Color(210, 210, 210));
        pnlCard.setPreferredSize(new Dimension(400, 250));
        pnlCard.setBorder(new EmptyBorder(20, 20, 20, 20));

        pnlCard.add(Box.createVerticalGlue());

        //Cho pnlCard bằng 1/2 kích thước của Dialog
        pnlCard.setPreferredSize(new Dimension(dialogWidth / 2, dialogHeigh / 2));

        // Panel hiện Icon
        JPanel pnlIcon = new JPanel() {
            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
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
        pnlIcon.setPreferredSize(new Dimension(100, 100));
        pnlIcon.setMaximumSize(new Dimension(100, 100));
        pnlIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblWelcome = new JLabel("Chào mừng " + nameUser);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 20));
        lblWelcome.setAlignmentX(Component.CENTER_ALIGNMENT);

        pnlCard.add(Box.createVerticalStrut(10)); // Tạo một khoảng trống nhỏ ở trên cùng (nếu muốn)
        pnlCard.add(pnlIcon);                     // Icon nằm trên
        pnlCard.add(Box.createVerticalStrut(20)); // Khoảng cách giữa Icon và Chữ
        pnlCard.add(lblWelcome);                  // Chữ nằm dưới
        pnlCard.add(Box.createVerticalGlue());

        pnlGreeting.add(pnlCard);

        // Sửa lỗi MouseEvent
        pnlGreeting.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                dispose();
            }
        });

        setContentPane(pnlGreeting);

        // Sẽ tự đóng sau 4s
        new Thread(() -> {
            try { Thread.sleep(4000); } catch (InterruptedException e) {}
            dispose();
        }).start();

        setVisible(true);
    }
}

