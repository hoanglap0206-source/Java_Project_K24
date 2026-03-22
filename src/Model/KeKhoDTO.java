package Model;

public class KeKhoDTO {
    private String ma_ke;
    private int tongSL;

    public KeKhoDTO() {
    }

    public KeKhoDTO(String ma_ke, int tongSL) {
        this.ma_ke = ma_ke;
        this.tongSL = tongSL;
    }

    public String getMa_ke() {
        return ma_ke;
    }

    public void setMa_ke(String ma_ke) {
        this.ma_ke = ma_ke;
    }

    public int getTongSL() {
        return tongSL;
    }

    public void setTongSL(int tongSL) {
        this.tongSL = tongSL;
    }
}
