//package GUI;
//
//import BUS.NV_BUS;
//import Model.NhanVien;
//
//import javax.swing.*;
//import javax.swing.border.EmptyBorder;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.util.ArrayList;
//
//public class TrangHoSo extends JPanel {
//    //Panel chính chứa các panel, label con khác
//    private JPanel pnlInfo;
//    private JLabel[] lblValues;
//    private JTable tbHistory;
//
//    //Tạo mảng chứa các dữ liệu sẽ hiển thị
//    private String[] txtHaveInfo = {"Mã nhân viên", "Tên nhân viên", "Số điện thoại", "Chức vụ", "Tổng số đơn hàng", "Tổng chi tiêu"};
//
//    private NV_BUS nvBus;
//
//    public void Initcomponents(String maNV){
//        this.setLayout(new BorderLayout(10, 20));
//        this.setBorder(new EmptyBorder(20, 20, 20, 20));
//
//        // 1. Dựng khung tiêu đề
//        JPanel pnlTop = new JPanel(new BorderLayout(10, 15));
//        JLabel lblTitle = new JLabel("Thông tin cá nhân");
//        lblTitle.setFont(new Font("Arial", Font.BOLD, 35));
//        pnlTop.add(lblTitle, BorderLayout.NORTH);
//
//        // 2. Dựng khung pnlInfo với các Label rỗng
//        this.pnlInfo = new JPanel(new GridLayout(6, 2, 10, 10));
//        this.pnlInfo.setBackground(Color.WHITE);
//        this.pnlInfo.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
//                new EmptyBorder(15, 15, 15, 15)));
//
//        lblValues = new JLabel[txtHaveInfo.length]; // Khởi tạo mảng chứa các label giá trị
//
//        for (int i = 0; i < txtHaveInfo.length; i++) {
//            JLabel lblListText = new JLabel(txtHaveInfo[i]);
//            lblListText.setFont(new Font("Arial", Font.BOLD, 18));
//
//            lblValues[i] = new JLabel("Đang tải..."); // Giá trị mặc định khi chưa có dữ liệu
//            lblValues[i].setFont(new Font("Arial", Font.PLAIN, 18));
//
//            if (i == txtHaveInfo.length - 1) {
//                lblValues[i].setForeground(new Color(0, 190, 0));
//                lblValues[i].setFont(new Font("Arial", Font.BOLD, 18));
//            }
//
//            this.pnlInfo.add(lblListText);
//            this.pnlInfo.add(lblValues[i]);
//        }
//
//        pnlTop.add(this.pnlInfo, BorderLayout.CENTER);
//        this.add(pnlTop, BorderLayout.NORTH);
//
//        this.tableList_HoaDon();
//        this.btnMain();
//
//        this.loadData(maNV);
//    }
//
//    public void loadData(String maNV) {
//        NV_BUS nvBus = new NV_BUS();
//        ArrayList<NhanVien> list = nvBus.getInfo_NV_BUS(maNV);
//
//        if (list != null && !list.isEmpty()) {
//            NhanVien nv = list.get(0);
//            // Cập nhật nội dung cho các Label đã tạo sẵn
//            lblValues[0].setText(nv.getMaNV());
//            lblValues[1].setText(nv.getHoTen());
//            lblValues[2].setText(nv.getSDT());
//            lblValues[3].setText(nv.getChucVu());
//            lblValues[4].setText("12"); // Giả định
//            lblValues[5].setText("20,000,000đ"); // Giả định
//
//            DefaultTableModel model = (DefaultTableModel) tbHistory.getModel();
//            model.setRowCount(0); // Xóa sạch dữ liệu cũ trong bảng
//
//            // Test đổ dữ liệu giả vào bảng
//            model.addRow(new Object[]{"1", "DH-NEW", "23/02/2026", "5,000,000đ"});
//        }
//    }
//
//    //Bảng chứa lịch sữ đã đặt hàng
//    private void tableList_HoaDon(){
//        String[] cols = {"STT", "Mã đơn", "Ngày nhập", "Tổng tiền"};
//
//        // Sử dụng DefaultTableModel để sau này dễ dàng thêm/xóa dòng
//        DefaultTableModel model = new DefaultTableModel(cols, 0);
//        this.tbHistory = new JTable(model);
//
//        //  Các phần chỉnh Font, RowHeight
//        this.tbHistory.setFont(new Font("Arial", Font.PLAIN, 18));
//        this.tbHistory.setRowHeight(35);
//        this.tbHistory.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18));
//
//        this.add(new JScrollPane(tbHistory), BorderLayout.CENTER);
//    }
//
//    //Button chỉnh sửa, thoát
//    private void btnMain(){
//        JButton btnChinhSua, btnThoat;
//        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10,10));
//
//        btnChinhSua = new JButton("Chỉnh sửa");
//        btnChinhSua.setFont(new Font("Arial", Font.PLAIN, 18));
//        btnChinhSua.setPreferredSize(new Dimension(200, btnChinhSua.getPreferredSize().height + 5));
//
//        btnThoat = new JButton("Thoát");
//        btnThoat.setFont(new Font("Arial", Font.PLAIN, 18));
//        btnThoat.setPreferredSize(new Dimension(200, btnThoat.getPreferredSize().height + 5));
//
//        pnlBtn.add(btnChinhSua);
//        pnlBtn.add(btnThoat);
//
//        this.add(pnlBtn, BorderLayout.SOUTH);
//    }
//
//    public TrangHoSo(){
//        String maNV = ManHinhChinh.currentMaNV;
//        Initcomponents(maNV);
//    }
//
//    public static void main(String[] args) {
//        JFrame frame = new JFrame("Thông tin cá nhân");
//        frame.setSize(1200, 700);
//        frame.setLocationRelativeTo(null);
//        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//
//        // Khởi tạo không cần tham số
//        TrangHoSo cn = new TrangHoSo();
//
//        // Đổ dữ liệu sau khi khởi tạo
//        cn.loadData("NV01");
//
//        frame.add(cn);
//        frame.setVisible(true);
//    }
//}
