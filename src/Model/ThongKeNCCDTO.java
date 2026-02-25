package Model;

public class ThongKeNCCDTO {
    private int soDonHang;
    private double tongGiaTri;

    public ThongKeNCCDTO(int soDonHang, double tongGiaTri) {
        this.soDonHang = soDonHang;
        this.tongGiaTri = tongGiaTri;
    }

    public int getSoDonHang() { return soDonHang; }
    public double getTongGiaTri() { return tongGiaTri; }
}
