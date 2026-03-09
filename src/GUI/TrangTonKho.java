    package GUI;

    import UI.TonKho_GUI;
    import BUS.BaoCaoTonKho_BUS;
    import Model.BaoCaoTonKho;
    import Model.SanPham;
    import BUS.SanPham_BUS;

    import javax.swing.*;
    import javax.swing.border.CompoundBorder;
    import javax.swing.border.EmptyBorder;
    import javax.swing.border.LineBorder;
    import javax.swing.table.DefaultTableModel;
    import java.awt.*;
    import java.awt.event.ActionEvent;
    import java.awt.event.ActionListener;
    import java.awt.image.BandCombineOp;
    import java.util.ArrayList;

    public class TrangTonKho extends JPanel {
        private JTable table;
        private DefaultTableModel model;
        private BaoCaoTonKho_BUS bus;
        public TrangTonKho() {
            bus= new BaoCaoTonKho_BUS();
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);

            JPanel pnlCompactInputs = new JPanel(new GridLayout(2, 1, 0, 5));
            pnlCompactInputs.setOpaque(false);


            JPanel pnlWarn = createCompactInput("Cảnh báo (ngày):", new Color(255, 255, 204), Color.BLACK);

            JPanel pnlExp = createCompactInput("Sắp hết hạn (SP):", new Color(255, 102, 0), Color.WHITE);

            pnlCompactInputs.add(pnlWarn);
            pnlCompactInputs.add(pnlExp);


            add(taoThanhCongCu(), BorderLayout.NORTH);



            String[] columnNames = {"Mã SP", "Tên SP", "Đơn vị tính", "Số lượng", "Đơn giá", "Số ngày hết hạn", "Mã kệ"};


            //hàm không cho thay đổi data//
             model=new DefaultTableModel(columnNames,0){
                @Override
                public boolean isCellEditable(int row,int column){
                    return false;
                }
            };

             table = new JTable(model);
            table.setRowHeight(30);
            table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
            table.getTableHeader().setBackground(new Color(230, 230, 230));
            table.getTableHeader().setReorderingAllowed(false);

            JScrollPane scrollPane = new JScrollPane(table);
            add(scrollPane, BorderLayout.CENTER);

            loadDataToTable();
        }
        public void loadDataToTable(){
            model.setRowCount(0);
            ArrayList<BaoCaoTonKho> danhsach=bus.getAll();
            for(BaoCaoTonKho tk: danhsach){
                Object[] row={
                        tk.getSanPham().getMaSP(),
                        tk.getSanPham().getTenSP(),
                        tk.getSanPham().getDonViTinh(),
                        tk.getsLTon(),
                        tk.getSanPham().getGiaTien(),
                        tk.getCanhBaoHH(),

                        tk.getSanPham().getMaKe(),
                };
                model.addRow(row);
            }
        }


        private JPanel createCompactInput(String labelText, Color bgColor, Color textColor) {
            JPanel pnl = new JPanel(new BorderLayout(5, 0));
            pnl.setBackground(Color.WHITE);

            JLabel lbl = new JLabel(labelText);
            lbl.setFont(new Font("Arial", Font.BOLD, 11));

            JTextField txt = new JTextField();
            txt.setBackground(bgColor);
            txt.setForeground(textColor);
            txt.setCaretColor(textColor);
            txt.setFont(new Font("Arial", Font.BOLD, 12));
            // Tạo viền mỏng và padding cho ô nhập
            txt.setBorder(new CompoundBorder(
                    new LineBorder(Color.LIGHT_GRAY, 1),
                    new EmptyBorder(2, 5, 2, 5)
            ));
            txt.setPreferredSize(new Dimension(80, 25));

            pnl.add(lbl, BorderLayout.WEST);
            pnl.add(txt, BorderLayout.CENTER);
            return pnl;
        }

        private JPanel taoThanhCongCu() {
            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.setBackground(Color.WHITE);
            wrapper.setBorder(new EmptyBorder(15,20,5,20));

            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT,12,8));
            panel.setBackground(Color.WHITE);
            panel.setBorder(new EmptyBorder(4,10,4,10));


            // Thanh tìm kiếm
            JTextField txtSearch = new JTextField("Tìm kiếm");
            txtSearch.setColumns(15);
            txtSearch.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
            txtSearch.setForeground(Color.GRAY);

            JPanel pnlSearchInput = new JPanel(new BorderLayout());
            pnlSearchInput.setBackground(Color.WHITE);
            pnlSearchInput.setPreferredSize(new Dimension(260,30));
            pnlSearchInput.setBorder(new CompoundBorder(
                    new LineBorder(new Color(198,226,255), 2, true),
                    new EmptyBorder(0,2,0,0)
            ));

            JButton btnSearchIcon = new JButton("🔍");
            btnSearchIcon.setBackground(new Color(214,238,253));
            btnSearchIcon.setBorderPainted(false);
            btnSearchIcon.setFocusPainted(false);
            btnSearchIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));

            pnlSearchInput.add(txtSearch, BorderLayout.CENTER);
            pnlSearchInput.add(btnSearchIcon, BorderLayout.EAST);

            txtSearch.addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusGained(java.awt.event.FocusEvent e) {
                    // Khi người dùng click vào ô
                    if (txtSearch.getText().equals("Tìm kiếm")) {
                        txtSearch.setText("");           // Xóa chữ "Tìm kiếm"
                        txtSearch.setForeground(Color.BLACK); // Đổi màu chữ sang đen để người dùng nhập
                    }
                }

                @Override
                public void focusLost(java.awt.event.FocusEvent e) {
                    // Khi người dùng click ra chỗ khác mà không nhập gì
                    if (txtSearch.getText().isEmpty()) {
                        txtSearch.setForeground(Color.GRAY);
                        txtSearch.setText("Tìm kiếm");    // Hiện lại chữ gợi ý
                    }
                }
            });


            // Nút làm mới
            JButton btnLamMoi = new JButton("⟳ Làm mới");
            Style.styleButton(btnLamMoi);
            btnLamMoi.addActionListener(e->{
                bus.refreshData();
                loadDataToTable();
                JOptionPane.showMessageDialog(this,"Đã làm mới");
            });


            // Combobox Lọc
            String[] itemLoc = {"Lọc", "1", "2", "3", "4", "5"};
            JComboBox<String> comboBoxLoc = new JComboBox<>(itemLoc);

            // Style cơ bản
            comboBoxLoc.setBackground(new Color(214, 238, 253));
            comboBoxLoc.setPreferredSize(new Dimension(90, 30));
            comboBoxLoc.setFont(new Font("Segoe UI", Font.BOLD, 13));
            comboBoxLoc.setCursor(new Cursor(Cursor.HAND_CURSOR));
            comboBoxLoc.setSelectedIndex(0);

            // Placeholder "Lọc"
            comboBoxLoc.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(
                        JList<?> list, Object value, int index,
                        boolean isSelected, boolean cellHasFocus) {

                    JLabel lbl = (JLabel) super.getListCellRendererComponent(
                            list, value, index, isSelected, cellHasFocus);

                    lbl.setHorizontalAlignment(SwingConstants.CENTER);

                    if (index == -1 && comboBoxLoc.getSelectedIndex() == -1) {
                        lbl.setText("Lọc");
                        lbl.setForeground(Color.GRAY);
                    }
                    return lbl;
                }
            });


            // Các nút khác
            JButton btnEdit = new JButton("Chỉnh sửa");
            Style.styleButton(btnEdit);
            JButton btnDelete = new JButton("Xóa");
            Style.styleButton(btnDelete);
            JButton btnAdd = new JButton("+ Thêm");
            Style.styleButton(btnAdd);
            JButton btnExcel = new JButton("Xuất excel");
            Style.styleButton(btnExcel);

            btnAdd.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                xulythem();
                }
            });

            btnEdit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    xulysua();
                }
            });
            // Thêm vào panel
            panel.add(pnlSearchInput);
            panel.add(btnLamMoi);
            panel.add(comboBoxLoc);
            panel.add(btnEdit);
            panel.add(btnDelete);
            panel.add(btnAdd);
            panel.add(btnExcel);

            wrapper.add(panel, BorderLayout.CENTER);
            return wrapper;
        }
        public void xulysua() {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần chỉnh sửa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 1. Lấy Mã SP để biết đang sửa dòng nào
            Object objMa = table.getValueAt(row, 0);
            Object objTen = table.getValueAt(row, 1);

            String maSP = (objMa != null) ? objMa.toString() : "";
            String tenSP = (objTen != null) ? objTen.toString() : "";

            // 2. Tạo danh sách các mục cho phép sửa
            String[] cacMucCanSua = {"Tên sản phẩm", "Đơn vị tính", "Số lượng", "Đơn giá", "Hủy bỏ"};

            // Hiển thị hộp thoại để người dùng chọn mục muốn sửa
            int luaChon = JOptionPane.showOptionDialog(this,
                    "Bạn muốn chỉnh sửa thông tin gì của sản phẩm:\n" + maSP + " - " + tenSP,
                    "Chọn thông tin chỉnh sửa",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    cacMucCanSua,
                    cacMucCanSua[0]);

            // 3. Xử lý Switch-Case theo lựa chọn
            switch (luaChon) {
                case 0: // Sửa Tên sản phẩm
                    Object objTenHT = table.getValueAt(row, 1);
                    String tenHienTai = (objTenHT != null) ? objTenHT.toString() : "";
                    String tenMoi = JOptionPane.showInputDialog(this, "Nhập tên sản phẩm mới:", tenHienTai);
                    if (tenMoi != null && !tenMoi.trim().isEmpty()) {
                        // TODO: Gọi BUS -> DAO để UPDATE tên vào CSDL
                        // Nếu SQL Update thành công thì cập nhật lại trên bảng JTable:
                        table.setValueAt(tenMoi, row, 1);
                        JOptionPane.showMessageDialog(this, "Đã cập nhật tên thành công!");
                    }
                    break;

                case 1: // Sửa Đơn vị tính
                    Object objDvtHT = table.getValueAt(row, 2);
                    String dvtHienTai = (objDvtHT != null) ? objDvtHT.toString() : "";
                    String dvtMoi = JOptionPane.showInputDialog(this, "Nhập đơn vị tính mới:", dvtHienTai);
                    if (dvtMoi != null && !dvtMoi.trim().isEmpty()) {
                        // TODO: Gọi BUS -> DAO
                        table.setValueAt(dvtMoi, row, 2);
                    }
                    break;

                case 2: // Sửa Số lượng
                    Object objSlHT = table.getValueAt(row, 3);
                    String slHienTai = (objSlHT != null) ? objSlHT.toString() : "0"; // Gán mặc định là 0 nếu rỗng
                    String slMoiStr = JOptionPane.showInputDialog(this, "Nhập số lượng:", slHienTai);
                    if (slMoiStr != null && !slMoiStr.trim().isEmpty()) {
                        try {
                            int slMoi = Integer.parseInt(slMoiStr);
                            // TODO: Gọi BUS -> DAO để UPDATE số lượng
                            table.setValueAt(slMoi, row, 3);
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(this, "Số lượng phải là số nguyên hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    break;

                case 3: // Sửa Đơn giá
                    Object objGiaHT = table.getValueAt(row, 4);
                    String giaHienTai = (objGiaHT != null) ? objGiaHT.toString() : "0";
                    String giaMoiStr = JOptionPane.showInputDialog(this, "Nhập đơn giá mới:", giaHienTai);
                    if (giaMoiStr != null && !giaMoiStr.trim().isEmpty()) {
                        // TODO: Kiểm tra số hợp lệ, gọi BUS -> DAO UPDATE giá
                        table.setValueAt(giaMoiStr, row, 4);
                    }
                    break;

                default:
                    // Người dùng chọn "Hủy bỏ" hoặc bấm dấu X tắt cửa sổ
                    break;
            }
        }
        public void xulythem(){

        }
        public static void main(String[]  args){
            SwingUtilities.invokeLater(()->
            {
                JFrame frame =new JFrame(" Ton kho");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                frame.add(new TrangTonKho());
                frame.setSize(1000, 600);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            });

        }
    }