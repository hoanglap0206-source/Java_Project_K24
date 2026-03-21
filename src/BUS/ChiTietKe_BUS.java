package BUS;

import DAO.ChiTietKe_DAO;
import DAO.KeKho_DAO;
import DAO.SanPham_DAO;
import Model.SanPham;
import Model.KeKho;
import Model.ChiTietKe;

import java.util.ArrayList;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ChiTietKe_BUS {
    private ChiTietKe_DAO ctDAO = new ChiTietKe_DAO();
    private KeKho_DAO keDAO = new KeKho_DAO();
    private SanPham_DAO spDAO = new SanPham_DAO();
    private KeKho_BUS keBus = new KeKho_BUS();

    private ArrayList<SanPham> listSP;

    public ChiTietKe_BUS() {
        listSP = spDAO.getAllSanPham();
    }

    // Lấy vị trí của sản phẩm
    public String getViTriSanPham(String maSP) {
        ArrayList<ChiTietKe> list = ctDAO.getByMaSP(maSP);
        StringBuilder sb = new StringBuilder();

        for (ChiTietKe ct : list) {
            sb.append(ct.getMaKe())
                    .append(" (")
                    .append(ct.getSoLuong())
                    .append("), ");
        }

        if (sb.length() == 0) return "Chưa có";
        return sb.substring(0, sb.length() - 2);
    }

    // Tính tổng số lượng sản phẩm trong kệ
    private int tinhTongSoLuongTheoKe(String maKe) {
        int tong = 0;
        ArrayList<ChiTietKe> list = ctDAO.getByMaKe(maKe);

        for (ChiTietKe ct : list) {
            tong += ct.getSoLuong();
        }
        return tong;
    }

    // Lấy tên sản phẩm từ danh sách
    private String layTenSanPham(String maSP) {
        for (SanPham sp : listSP) {
            if (sp.getMaSP().equals(maSP)) {
                return sp.getTenSP();
            }
        }
        return "";
    }

    // Xuất Excel
    public boolean exportExcel(String filePath) {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("DanhSachKeKho");

            // Tạo header
            Row headerRow = sheet.createRow(0);
            String[] headers = {"STT", "Mã kệ", "Vị trí", "Sức chứa",
                    "Mã sản phẩm", "Tên sản phẩm", "Đơn vị tính", "Số lượng"};

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 4000);
            }

            // Lấy dữ liệu
            ArrayList<KeKho> listKe = keBus.getListKK();
            int rowNum = 1;
            int stt = 1;

            for (KeKho ke : listKe) {
                ArrayList<SanPham> listSPTrongKe = keBus.laySanPhamTheoKe(ke.getMaKe());

                if (listSPTrongKe.isEmpty()) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(stt++);
                    row.createCell(1).setCellValue(ke.getMaKe());
                    row.createCell(2).setCellValue(ke.getViTri());
                    row.createCell(3).setCellValue(ke.getSucChua());
                    row.createCell(4).setCellValue("");
                    row.createCell(5).setCellValue("");
                    row.createCell(6).setCellValue("");
                    row.createCell(7).setCellValue("");
                } else {
                    for (SanPham sp : listSPTrongKe) {
                        Row row = sheet.createRow(rowNum++);
                        row.createCell(0).setCellValue(stt++);
                        row.createCell(1).setCellValue(ke.getMaKe());
                        row.createCell(2).setCellValue(ke.getViTri());
                        row.createCell(3).setCellValue(ke.getSucChua());
                        row.createCell(4).setCellValue(sp.getMaSP());
                        row.createCell(5).setCellValue(sp.getTenSP());
                        row.createCell(6).setCellValue(sp.getDonViTinh());
                        row.createCell(7).setCellValue(sp.getSoLuong());
                    }
                }
            }

            // Auto size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                if (sheet.getColumnWidth(i) > 8000) {
                    sheet.setColumnWidth(i, 8000);
                }
            }

            FileOutputStream fos = new FileOutputStream(filePath);
            workbook.write(fos);
            workbook.close();
            fos.close();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Import Excel
    public String importExcel(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            Workbook workbook = WorkbookFactory.create(fis);
            Sheet sheet = workbook.getSheetAt(0);

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                workbook.close();
                fis.close();
                return "File không có dữ liệu";
            }

            // Kiểm tra header
            if (headerRow.getCell(1) == null ||
                    !headerRow.getCell(1).getStringCellValue().contains("Mã kệ")) {
                workbook.close();
                fis.close();
                return "File không đúng định dạng. Thiếu cột 'Mã kệ'";
            }

            if (headerRow.getCell(4) == null ||
                    !headerRow.getCell(4).getStringCellValue().contains("Mã sản phẩm")) {
                workbook.close();
                fis.close();
                return "File không đúng định dạng. Thiếu cột 'Mã sản phẩm'";
            }

            if (headerRow.getCell(7) == null ||
                    !headerRow.getCell(7).getStringCellValue().contains("Số lượng")) {
                workbook.close();
                fis.close();
                return "File không đúng định dạng. Thiếu cột 'Số lượng'";
            }

            // Đọc dữ liệu
            ArrayList<ChiTietKe> listImport = new ArrayList<>();
            String currentMaKe = "";

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Cell cellMaKe = row.getCell(1);
                if (cellMaKe != null) {
                    String value = getCellValue(cellMaKe);
                    if (!value.isEmpty()) {
                        currentMaKe = value;
                    }
                }

                Cell cellMaSP = row.getCell(4);
                String maSP = "";
                if (cellMaSP != null) {
                    maSP = getCellValue(cellMaSP);
                }

                Cell cellSoLuong = row.getCell(7);
                int soLuong = 0;
                if (cellSoLuong != null) {
                    if (cellSoLuong.getCellType() == CellType.NUMERIC) {
                        soLuong = (int) cellSoLuong.getNumericCellValue();
                    } else {
                        String val = getCellValue(cellSoLuong);
                        if (!val.isEmpty()) {
                            soLuong = Integer.parseInt(val);
                        }
                    }
                }

                if (!maSP.isEmpty() && soLuong > 0 && !currentMaKe.isEmpty()) {
                    listImport.add(new ChiTietKe(currentMaKe, maSP, soLuong));
                }
            }

            workbook.close();
            fis.close();

            return saveImportData(listImport);

        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi khi đọc file: " + e.getMessage();
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                double value = cell.getNumericCellValue();
                if (value == (int) value) {
                    return String.valueOf((int) value);
                }
                return String.valueOf(value);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }

    private String saveImportData(ArrayList<ChiTietKe> listImport) {
        if (listImport.isEmpty()) {
            return "Không có dữ liệu để nhập";
        }

        try {
            ArrayList<String> errors = new ArrayList<>();
            ArrayList<ChiTietKe> validList = new ArrayList<>();

            for (ChiTietKe ct : listImport) {
                // Kiểm tra sản phẩm tồn tại
                SanPham sp = spDAO.getSanPhamByMa(ct.getMaSP());
                if (sp == null) {
                    errors.add("Mã sản phẩm " + ct.getMaSP() + " không tồn tại");
                    continue;
                }

                // Kiểm tra kệ tồn tại
                KeKho ke = keBus.getKeTheoMa(ct.getMaKe());
                if (ke == null) {
                    errors.add("Mã kệ " + ct.getMaKe() + " không tồn tại");
                    continue;
                }

                // Kiểm tra sức chứa
                int tongHienTai = tinhTongSoLuongTheoKe(ct.getMaKe());
                ArrayList<ChiTietKe> listCT = ctDAO.getByMaKe(ct.getMaKe());
                int soLuongCu = 0;
                for (ChiTietKe item : listCT) {
                    if (item.getMaSP().equals(ct.getMaSP())) {
                        soLuongCu = item.getSoLuong();
                        break;
                    }
                }

                int tongSauKhiCapNhat = tongHienTai - soLuongCu + ct.getSoLuong();

                if (tongSauKhiCapNhat > ke.getSucChua()) {
                    errors.add("Kệ " + ct.getMaKe() + " không đủ sức chứa cho sản phẩm " + ct.getMaSP());
                    continue;
                }

                validList.add(ct);
            }

            if (!errors.isEmpty()) {
                return "Lỗi dữ liệu:\n" + String.join("\n", errors);
            }

            // Lưu dữ liệu
            int success = 0;
            for (ChiTietKe ct : validList) {
                if (ctDAO.insertOrUpdate(ct)) {
                    success++;
                }
            }

            keBus.refreshData();

            return "Nhập thành công " + success + "/" + validList.size() + " bản ghi";

        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi khi lưu dữ liệu: " + e.getMessage();
        }
    }

    // Các phương thức khác
    public ArrayList<ChiTietKe> getByMaKe(String maKe) {
        return ctDAO.getByMaKe(maKe);
    }

    public ArrayList<ChiTietKe> getByMaSP(String maSP) {
        return ctDAO.getByMaSP(maSP);
    }

    public boolean updateSoLuong(String maKe, String maSP, int soLuong) {
        KeKho ke = keBus.getKeTheoMa(maKe);
        if (ke != null) {
            int tongHienTai = tinhTongSoLuongTheoKe(maKe);
            ArrayList<ChiTietKe> listCT = ctDAO.getByMaKe(maKe);
            int soLuongCu = 0;
            for (ChiTietKe ct : listCT) {
                if (ct.getMaSP().equals(maSP)) {
                    soLuongCu = ct.getSoLuong();
                    break;
                }
            }

            int tongMoi = tongHienTai - soLuongCu + soLuong;
            if (tongMoi > ke.getSucChua()) {
                return false;
            }
        }

        ChiTietKe ct = new ChiTietKe(maKe, maSP, soLuong);
        boolean result = ctDAO.insertOrUpdate(ct);

        if (result) {
            keBus.refreshData();
        }

        return result;
    }

    public boolean delete(String maKe, String maSP) {
        boolean result = ctDAO.delete(maKe, maSP);
        if (result) {
            keBus.refreshData();
        }
        return result;
    }

    public int getTongSoLuongToanKho() {
        int tong = 0;
        ArrayList<KeKho> listKe = keBus.getListKK();
        for (KeKho ke : listKe) {
            tong += tinhTongSoLuongTheoKe(ke.getMaKe());
        }
        return tong;
    }
}