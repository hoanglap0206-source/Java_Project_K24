package GUI;

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
           JTable table= (JTable) SwingUtilities.getAncestorOfClass(JTable.class,JB);
           int row= table.getEditingRow();

           if(row !=1){
               String ma= table.getValueAt(row,1).toString();
               String ten= table.getValueAt(row,2).toString();
               String sdt= table.getValueAt(row,3).toString();
               String diaChi= table.getValueAt(row,4).toString();

               Model.NhaCungCap ncc=new Model.NhaCungCap(ma,ten,sdt,diaChi);

               //khởi tạo Frame
               ChiTietNCC_GUI detal=new ChiTietNCC_GUI(ncc);
               detal.setVisible(true);
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
