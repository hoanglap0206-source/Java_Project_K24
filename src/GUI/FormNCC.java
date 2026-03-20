package GUI;
import BUS.NCC_BUS;
import Model.NhaCungCap;
import javax.swing.*;
import java.awt.*;
public class FormNCC extends JDialog {
    private JTextField txtMa, txtTen, txtSdt, txtDiaChi;
    private JButton btnluu;
    private NCC_BUS nccBus;
    private TrangNhaCungCap parent;
    private String mode;
    private NhaCungCap NCC;

    public FormNCC(TrangNhaCungCap parent, String model, NhaCungCap ncc){

        this.parent=parent;
        this.mode=model;

        this.NCC=ncc;
        this.nccBus=parent.getNccBUS();
        initUI();

        if(mode.equals("SUA")||ncc != null){
            setTitle("Chỉnh sửa thông tin Nhà Cung Cấp");
            txtMa.setText(ncc.getMaNCC());
            txtMa.setEditable(false);
            txtTen.setText(ncc.getTenNCC());
            txtSdt.setText(ncc.getSdt());
            txtDiaChi.setText(ncc.getDiaChi());
            btnluu.setText("Cập nhật");
        }else{
            setTitle("Thêm Nhà Cung Cấp Mới");
            btnluu.setText("Lưu mới");
        }
    }

    private void initUI(){
        setSize(450,350);
        setLocationRelativeTo(null);
        setModal(true);
        setLayout(new BorderLayout());

        JPanel pnlput=new JPanel(new GridBagLayout());
        pnlput.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        GridBagConstraints gbc=new GridBagConstraints();
        gbc.insets= new Insets(8,8,8,8);
        gbc.fill=GridBagConstraints.HORIZONTAL;
        String flag;
        addControl(pnlput,"Mã nhà cung cấp:",txtMa=new JTextField(),gbc,0);

        addControl(pnlput,"Tên nhà cung cấp",txtTen=new JTextField(),gbc,1);
        addControl(pnlput,"Số điện thoại:",txtSdt=new JTextField(),gbc,2);
        addControl(pnlput,"Dịa chỉ:",txtDiaChi=new JTextField(),gbc,3);

        JPanel pntButtom=new JPanel((new FlowLayout(FlowLayout.RIGHT)));
        btnluu=new JButton();
        btnluu.setPreferredSize(new Dimension(120,40));
        pntButtom.add(btnluu);

        add(pnlput,BorderLayout.CENTER);
        add(pntButtom,BorderLayout.SOUTH);

        btnluu.addActionListener(e->XuLyLuu());
    }

    private void addControl(JPanel p,String label,JTextField tf,GridBagConstraints gbc,int row){
        gbc.gridx=0;gbc.gridy=row; gbc.weightx=0.3;
        p.add(new JLabel(label),gbc);
        gbc.gridx=1;gbc.weightx=0.7;
        p.add(tf,gbc);
    }

    private void XuLyLuu() {
        // 1. Thu thập dữ liệu từ giao diện
        String ma = txtMa.getText().trim();
        String ten = txtTen.getText().trim();
        String sdt = txtSdt.getText().trim();
        String diaChi = txtDiaChi.getText().trim();

        // Kiểm tra trống sơ bộ
        if(ma.isEmpty() || ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Mã và Tên!");
            return;
        }

        NhaCungCap nccMoi = new NhaCungCap(ma, ten, diaChi, sdt);
        String mess;

        // 2. Gọi đúng đối tượng nccMoi vừa tạo
        if(mode.equals("THEM")){
            mess = nccBus.addNCC(nccMoi);
        } else {
            mess = nccBus.updateNCC(nccMoi);
        }

        JOptionPane.showMessageDialog(this, mess);
        if(mess.toLowerCase().contains("thành công")){
            parent.fillToTable();
            dispose();
        }
    }

}
