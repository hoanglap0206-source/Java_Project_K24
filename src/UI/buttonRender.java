package UI;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class buttonRender extends JButton implements TableCellRenderer {
    public buttonRender() {
        setText("Xem");
        // Bạn có thể chỉnh màu sắc cho nút ở đây nếu muốn
        setBackground(new Color(255,255,255));
        setForeground(Color.BLACK);
        setFocusPainted(false);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        return this;
    }
}