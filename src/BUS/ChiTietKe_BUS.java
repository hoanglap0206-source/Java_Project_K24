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
    private ChiTietKe_DAO dao = new ChiTietKe_DAO();
    private KeKho_DAO keDAO = new KeKho_DAO();
    private ArrayList<SanPham> listSP = new SanPham_DAO().getAllSanPham();

    public String getViTriSanPham(String maSP) {
        ArrayList<ChiTietKe> list = dao.getAll();

        StringBuilder sb = new StringBuilder();

        for (ChiTietKe ct : list) {
            if (ct.getMaSP().equals(maSP)) {
                sb.append(ct.getMaKe())
                        .append(" (")
                        .append(ct.getSoLuong())
                        .append("), ");
            }
        }

        if (sb.length() == 0) return "Chưa có";

        return sb.substring(0, sb.length() - 2);
    }

    public String exportExcel(String path) {
        listSP = new SanPham_DAO().getAllSanPham(); // refresh dữ liệu
        ArrayList<KeKho> listKe = keDAO.getAllKeKho();
        ArrayList<ChiTietKe> listCT = dao.getAll();

        try (Workbook wb = new XSSFWorkbook()) {

            // ===== STYLE =====
            CellStyle headerStyle = wb.createCellStyle();
            Font headerFont = wb.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // màu xanh nhạt
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle centerStyle = wb.createCellStyle();
            centerStyle.setAlignment(HorizontalAlignment.CENTER);

            // ===== SHEET 1: KE KHO =====
            Sheet sheetKe = wb.createSheet("KeKho");

            String[] colsKe = {"Mã kệ", "Vị trí", "Sức chứa", "Tổng SL", "% sử dụng"};

            Row headerKe = sheetKe.createRow(0);
            for (int i = 0; i < colsKe.length; i++) {
                Cell cell = headerKe.createCell(i);
                cell.setCellValue(colsKe[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIndex = 1;
            for (KeKho ke : listKe) {
                Row row = sheetKe.createRow(rowIndex++);

                int tong = tinhTongSoLuongTheoKe(ke.getMaKe());
                int percent = 0;
                if (ke.getSucChua() != 0)
                    percent = (int) ((tong * 100.0) / ke.getSucChua());

                row.createCell(0).setCellValue(ke.getMaKe());
                row.createCell(1).setCellValue(ke.getViTri());
                row.createCell(2).setCellValue(ke.getSucChua());
                row.createCell(3).setCellValue(tong);
                row.createCell(4).setCellValue(percent + "%");

                for (int i = 0; i < 5; i++) {
                    row.getCell(i).setCellStyle(centerStyle);
                }
            }

            for (int i = 0; i < colsKe.length; i++) {
                sheetKe.autoSizeColumn(i);
            }

            // ===== SHEET 2: CHI TIẾT KỆ =====
            Sheet sheetCT = wb.createSheet("ChiTietKe");

            String[] colsCT = {"Mã kệ", "Mã SP", "Tên SP", "Số lượng"};

            Row headerCT = sheetCT.createRow(0);
            for (int i = 0; i < colsCT.length; i++) {
                Cell cell = headerCT.createCell(i);
                cell.setCellValue(colsCT[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowCT = 1;
            for (ChiTietKe ct : listCT) {
                Row row = sheetCT.createRow(rowCT++);

                String tenSP = layTenSanPham(ct.getMaSP(), listSP);

                row.createCell(0).setCellValue(ct.getMaKe());
                row.createCell(1).setCellValue(ct.getMaSP());
                row.createCell(2).setCellValue(tenSP);
                row.createCell(3).setCellValue(ct.getSoLuong());

                for (int i = 0; i < 4; i++) {
                    row.getCell(i).setCellStyle(centerStyle);
                }
            }

            for (int i = 0; i < colsCT.length; i++) {
                sheetCT.autoSizeColumn(i);
            }

            // ===== GHI FILE =====
            FileOutputStream fos = new FileOutputStream(path);
            wb.write(fos);
            fos.close();

            return "Xuất Excel thành công!\n" + path;

        } catch (Exception e) {
            e.printStackTrace();
            return "Xuất Excel thất bại!";
        }
    }

    private int tinhTongSoLuongTheoKe(String maKe) {
        int tong = 0;
        ArrayList<ChiTietKe> list = dao.getByMaKe(maKe);

        for (ChiTietKe ct : list) {
            tong += ct.getSoLuong();
        }

        return tong;
    }

    private String layTenSanPham(String maSP, ArrayList<SanPham> list){
        for(SanPham sp : list){
            if(sp.getMaSP().equals(maSP)){
                return sp.getTenSP();
            }
        }
        return "";
    }

    public void importExcel(File file) {
        try (FileInputStream fis = new FileInputStream(file);
             Workbook wb = new XSSFWorkbook(fis)) {

            // Sheet KeKho
            Sheet sheetKe = wb.getSheet("KeKho");
            for (int i = 1; i <= sheetKe.getLastRowNum(); i++) {
                Row row = sheetKe.getRow(i);
                if (row == null) continue;
                KeKho ke = new KeKho(
                        row.getCell(0).getStringCellValue(),
                        (int) row.getCell(2).getNumericCellValue(),
                        row.getCell(1).getStringCellValue()
                );
                keDAO.insert(ke); // hoặc update nếu đã tồn tại
            }

            // Sheet ChiTietKe
            Sheet sheetCT = wb.getSheet("ChiTietKe");
            for (int i = 1; i <= sheetCT.getLastRowNum(); i++) {
                Row row = sheetCT.getRow(i);
                if (row == null) continue;
                ChiTietKe ct = new ChiTietKe(
                        row.getCell(0).getStringCellValue(),
                        row.getCell(1).getStringCellValue(),
                        (int) row.getCell(2).getNumericCellValue()
                );
                dao.insertOrUpdate(ct);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
