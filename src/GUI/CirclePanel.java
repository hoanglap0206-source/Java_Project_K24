package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.net.URL;

public class CirclePanel extends JPanel {

    private Image image;

    public CirclePanel(String resourcePath, int size) {

        URL url = getClass().getResource(resourcePath);

        if (url != null) {
            image = new ImageIcon(url).getImage();
        } else {
            System.out.println("Không tìm thấy ảnh: " + resourcePath);
        }

        Dimension dimension = new Dimension(size, size);
        setPreferredSize(dimension);
        setMinimumSize(dimension);
        setMaximumSize(dimension);

        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (image == null) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        Shape circle = new Ellipse2D.Double(0, 0, getWidth(), getHeight());
        g2.setClip(circle);

        g2.drawImage(image, 0, 0, getWidth(), getHeight(), this);

        g2.dispose();
    }
}
