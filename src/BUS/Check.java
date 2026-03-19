package BUS;

public class Check {
    public static boolean isValidManv(String manv){
        if(manv == null) return false;
        String laMaNV = "^NV\\d+$";
        return manv.matches(laMaNV);
    }

    public static boolean isValidNCC(String ncc){
        if(ncc == null) return false;
        String laNCC = "^NCC\\d+$";
        return ncc.matches(laNCC);
    }

    public static boolean isValidKH(String kh){
        if(kh == null) return false;
        String laKH = "^KH\\d+$";
        return kh.matches(laKH);
    }

    public static boolean isValidSP(String sp){
        if(sp == null) return false;
        String laSP = "^SP\\d+$";
        return sp.matches(laSP);
    }

    public static boolean isValidPN(String pn){
        if(pn == null) return false;
        String laPN = "^PN\\d{14}$";
        return pn.matches(laPN);
    }

    public static boolean isValidPX(String px){
        if(px == null) return false;
        String laPX = "^PX\\d{14}$";
        return px.matches(laPX);
    }
}
