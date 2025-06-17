package movie.model;

import java.math.BigDecimal;
import java.util.Date;

public class ThanhToanModel {
    private String maThanhToan;
    private String maDonHang;
    private String phuongThuc;
    private BigDecimal soTien;
    private Date ngayThanhToan;
    private String trangThai;

    // Constructors
    public ThanhToanModel() {}

    public ThanhToanModel(movie.entity.ThanhToanEntity entity) {
        if (entity != null) {
            this.maThanhToan = entity.getMaThanhToan();
            this.maDonHang = entity.getDonHang() != null ? entity.getDonHang().getMaDonHang() : null;
            this.phuongThuc = entity.getPhuongThuc();
            this.soTien = entity.getSoTien();
            this.ngayThanhToan = entity.getNgayThanhToan();
            this.trangThai = entity.getTrangThai();
        }
    }

    // Getters và Setters
    public String getMaThanhToan() { return maThanhToan; }
    public void setMaThanhToan(String maThanhToan) { this.maThanhToan = maThanhToan; }
    public String getMaDonHang() { return maDonHang; }
    public void setMaDonHang(String maDonHang) { this.maDonHang = maDonHang; }
    public String getPhuongThuc() { return phuongThuc; }
    public void setPhuongThuc(String phuongThuc) { this.phuongThuc = phuongThuc; }
    public BigDecimal getSoTien() { return soTien; }
    public void setSoTien(BigDecimal soTien) { this.soTien = soTien; }
    public Date getNgayThanhToan() { return ngayThanhToan; }
    public void setNgayThanhToan(Date ngayThanhToan) { this.ngayThanhToan = ngayThanhToan; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}