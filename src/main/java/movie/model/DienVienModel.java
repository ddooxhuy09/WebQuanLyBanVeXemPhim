package movie.model;

import java.util.Date;

public class DienVienModel {
    private String maDienVien;
    private String hoTen;
    private Date ngaySinh;
    private String quocGia;
    private String urlDienVien;

    // Constructors
    public DienVienModel() {}

    public DienVienModel(movie.entity.DienVienEntity entity) {
        if (entity != null) {
            this.maDienVien = entity.getMaDienVien();
            this.hoTen = entity.getHoTen();
            this.ngaySinh = entity.getNgaySinh();
            this.quocGia = entity.getQuocGia();
            this.urlDienVien = entity.getUrlDienVien();
        }
    }

    // Getters và Setters
    public String getMaDienVien() { return maDienVien; }
    public void setMaDienVien(String maDienVien) { this.maDienVien = maDienVien; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public Date getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(Date ngaySinh) { this.ngaySinh = ngaySinh; }
    public String getQuocGia() { return quocGia; }
    public void setQuocGia(String quocGia) { this.quocGia = quocGia; }
    public String getUrlDienVien() { return urlDienVien; }
    public void setUrlDienVien(String urlDienVien) { this.urlDienVien = urlDienVien; }
}