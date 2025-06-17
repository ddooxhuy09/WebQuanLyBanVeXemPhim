package movie.model;

public class ChiTietDonHangBapNuocModel {
    private String maDonHang;
    private String maBapNuoc;
    private int soLuong;

    // Constructors
    public ChiTietDonHangBapNuocModel() {}

    public ChiTietDonHangBapNuocModel(movie.entity.ChiTietDonHangBapNuocEntity entity) {
        if (entity != null) {
            this.maDonHang = entity.getDonHang().getMaDonHang();
            this.maBapNuoc = entity.getMaBapNuoc();
            this.soLuong = entity.getSoLuong();
        }
    }

    // Getters và Setters
    public String getMaDonHang() { return maDonHang; }
    public void setMaDonHang(String maDonHang) { this.maDonHang = maDonHang; }
    public String getMaBapNuoc() { return maBapNuoc; }
    public void setMaBapNuoc(String maBapNuoc) { this.maBapNuoc = maBapNuoc; }
    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
}