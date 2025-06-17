package movie.model;

public class ChiTietDonHangComboModel {
    private String maDonHang;
    private String maCombo;
    private int soLuong;

    // Constructors
    public ChiTietDonHangComboModel() {}

    public ChiTietDonHangComboModel(movie.entity.ChiTietDonHangComboEntity entity) {
        if (entity != null) {
            this.maDonHang = entity.getDonHang().getMaDonHang();
            this.maCombo = entity.getMaCombo();
            this.soLuong = entity.getSoLuong();
        }
    }

    // Getters và Setters
    public String getMaDonHang() { return maDonHang; }
    public void setMaDonHang(String maDonHang) { this.maDonHang = maDonHang; }
    public String getMaCombo() { return maCombo; }
    public void setMaCombo(String maCombo) { this.maCombo = maCombo; }
    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
}