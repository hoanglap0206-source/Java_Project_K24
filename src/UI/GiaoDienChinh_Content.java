package UI;

import javax.swing.*;
import java.awt.*;

public class GiaoDienChinh_Content extends JPanel {
    private CardLayout cardLayout;
    public GiaoDienChinh_Content(){
        cardLayout=new CardLayout();
        setLayout(cardLayout);
        setBackground(Color.WHITE);
        JPanel pnlHome = new JPanel(new GridBagLayout());
        pnlHome.setBackground(Color.WHITE);
        pnlHome.add(new JLabel("GIAO DIỆN TRANG CHỦ "));
        add(pnlHome, "home"); // Đặt tên thẻ là "home"
        // Card 2: Sản phẩm
        JPanel pnlProduct = new JPanel(new GridBagLayout());
        pnlProduct.setBackground(new Color(255, 240, 240, 255));
        pnlProduct.add(new JLabel("GIAO DIỆN QUẢN LÝ SẢN PHẨM"));
        add(pnlProduct, "product"); // Đặt tên thẻ là "product"

        // Card 3: Nhà cung cấp
        JPanel pnlSupplier = new JPanel(new GridBagLayout());
        pnlSupplier.setBackground(new Color(255, 240, 240)); // Màu đỏ nhạt test
        pnlSupplier.add(new JLabel("GIAO DIỆN NHÀ CUNG CẤP"));
        add(pnlSupplier, "supplier");
    }
    public void showPanel(String cardName) {
        cardLayout.show(this, cardName);
    }
}

