package movie.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "DienVien")
public class DienVienEntity {
    @Id
    @Column(name = "MaDienVien", length = 10)
    private String maDienVien;

    @Column(name = "HoTen", columnDefinition = "nvarchar(50)")
    private String hoTen;

    @Column(name = "NgaySinh")
    @Temporal(TemporalType.DATE)
    private Date ngaySinh;

    @Column(name = "QuocGia", columnDefinition = "nvarchar(50)")
    private String quocGia;

    @Column(name = "UrlDienVien", length = 255)
    private String urlDienVien;

    @ManyToMany(mappedBy = "dienViens")
    private List<PhimEntity> phims;

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
    public List<PhimEntity> getPhims() { return phims; }
    public void setPhims(List<PhimEntity> phims) { this.phims = phims; }
}