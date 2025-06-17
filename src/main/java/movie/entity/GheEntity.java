package movie.entity;

import javax.persistence.*;

@Entity
@Table(name = "Ghe") // Đã sửa tên bảng cho đúng với schema
public class GheEntity {
    @Id
    @Column(name = "MaGhe", length = 10) // Cập nhật tên cột
    private String maGhe;

    @Column(name = "SoGhe") // Đã đổi kiểu từ int sang String
    private String soGhe; // Đã sửa từ int sang String

    @Column(name = "TenHang", length = 10) // Cập nhật tên cột
    private String tenHang;
    
    @Column(name = "SoGheAdmin", length = 10)
    private String soGheAdmin;

    @Column(name = "TenHangAdmin", length = 10)
    private String tenHangAdmin;

    @ManyToOne
    @JoinColumn(name = "MaLoaiGhe", referencedColumnName = "MaLoaiGhe") // Cập nhật tên cột
    private LoaiGheEntity loaiGhe;

    @ManyToOne
    @JoinColumn(name = "MaPhongChieu", referencedColumnName = "MaPhongChieu") // Cập nhật tên cột
    private PhongChieuEntity phongChieu;

    // Getters và Setters
    public String getMaGhe() {
        return maGhe;
    }

    public void setMaGhe(String maGhe) {
        this.maGhe = maGhe;
    }

    public String getSoGhe() { // Đã sửa kiểu trả về thành String
        return soGhe;
    }

    public void setSoGhe(String soGhe) { // Đã sửa tham số thành String
        this.soGhe = soGhe;
    }

    public String getTenHang() {
        return tenHang;
    }

    public void setTenHang(String tenHang) {
        this.tenHang = tenHang;
    }
    
    public String getSoGheAdmin() {
        return soGheAdmin;
    }

    public void setSoGheAdmin(String soGheAdmin) {
        this.soGheAdmin = soGheAdmin;
    }

    public String getTenHangAdmin() {
        return tenHangAdmin;
    }

    public void setTenHangAdmin(String tenHangAdmin) {
        this.tenHangAdmin = tenHangAdmin;
    }

    public LoaiGheEntity getLoaiGhe() {
        return loaiGhe;
    }

    public void setLoaiGhe(LoaiGheEntity loaiGhe) {
        this.loaiGhe = loaiGhe;
    }

    public PhongChieuEntity getPhongChieu() {
        return phongChieu;
    }

    public void setPhongChieu(PhongChieuEntity phongChieu) {
        this.phongChieu = phongChieu;
    }
}
