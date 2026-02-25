package UI;
import javax.swing.*;
import java.awt.*;

public class ButtonEditor extends DefaultCellEditor{
    protected JButton JB;
    private String Label;
    private boolean isPushed;

    public ButtonEditor(JCheckBox CheckBox){
        super(CheckBox);
        JB=new JButton();
        JB.setOpaque(true);
        JB.addActionListener(e->fireEditingStopped());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,boolean  isSelect,int row,int column){
        Label =(value==null)?"Xem":value.toString();
        JB.setText(Label);
        isPushed=true;
        return JB;
    }

    @Override
    public Object getCellEditorValue(){
        if(isPushed){
            System.out.println("Đang mở chi tiết cho dòng này...");
            // Tìm JFrame chính để thực hiện chuyển Panel
            Window win = SwingUtilities.getWindowAncestor(JB);
            if (win instanceof JFrame) {
                // Tại đây bạn có thể gọi hàm chuyển sang trang chi tiết
                // Ví dụ: ((GiaoDienChinh_Main)win).showDetailPanel();
            }
        }
        isPushed =false;
        return Label;
    }

    @Override
    public boolean stopCellEditing(){
        isPushed=false;
        return super.stopCellEditing();
    }
}
