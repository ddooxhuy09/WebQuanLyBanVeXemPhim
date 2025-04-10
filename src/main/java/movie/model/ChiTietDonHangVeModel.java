package movie.model;

public class ChiTietDonHangVeModel {
    private String maDonHang;
    private String maVe;

    // Constructors
    public ChiTietDonHangVeModel() {}

    public ChiTietDonHangVeModel(movie.entity.ChiTietDonHangVeEntity entity) {
        if (entity != null) {
            this.maDonHang = entity.getDonHang().getMaDonHang();
            this.maVe = entity.getMaVe();
        }
    }

    // Getters và Setters
    public String getMaDonHang() { return maDonHang; }
    public void setMaDonHang(String maDonHang) { this.maDonHang = maDonHang; }
    public String getMaVe() { return maVe; }
    public void setMaVe(String maVe) { this.maVe = maVe; }
}