package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class CirclePanel extends JPanel {

    private Image image;

    public CirclePanel(String imagePath) {
        image = new ImageIcon(imagePath).getImage();
        Dimension size = new Dimension(120, 120);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Shape circle = new Ellipse2D.Double(0, 0, getWidth(), getHeight());
        g2.setClip(circle);
        g2.drawImage(image, 0, 0, getWidth(), getHeight(), this);

        g2.dispose();
    }
}
