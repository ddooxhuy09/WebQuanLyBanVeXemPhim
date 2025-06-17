package movie.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "KhachHang")
public class KhachHangEntity {
    @Id
    @Column(name = "MaKhachHang")
    private String maKhachHang;

    @Column(name = "HoKhachHang")
    private String hoKhachHang;

    @Column(name = "TenKhachHang")
    private String tenKhachHang;

    @Column(name = "SoDienThoai")
    private String soDienThoai;

    @Column(name = "Email")
    private String email;

    @Column(name = "MatKhau")
    private String matKhau;

    @Column(name = "NgaySinh")
    @Temporal(TemporalType.DATE)
    private Date ngaySinh;

    @Column(name = "NgayDangKy")
    @Temporal(TemporalType.DATE)
    private Date ngayDangKy;

    @Column(name = "TongDiem")
    private int tongDiem;

    // Getters và Setters
    public String getMaKhachHang() { return maKhachHang; }
    public void setMaKhachHang(String maKhachHang) { this.maKhachHang = maKhachHang; }
    public String getHoKhachHang() { return hoKhachHang; }
    public void setHoKhachHang(String hoKhachHang) { this.hoKhachHang = hoKhachHang; }
    public String getTenKhachHang() { return tenKhachHang; }
    public void setTenKhachHang(String tenKhachHang) { this.tenKhachHang = tenKhachHang; }
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }
    public Date getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(Date ngaySinh) { this.ngaySinh = ngaySinh; }
    public Date getNgayDangKy() { return ngayDangKy; }
    public void setNgayDangKy(Date ngayDangKy) { this.ngayDangKy = ngayDangKy; }
    public int getTongDiem() { return tongDiem; }
    public void setTongDiem(int tongDiem) { this.tongDiem = tongDiem; }
    public void congDiem(int diemThem) {
        this.tongDiem += diemThem;
    }

    // Thêm phương thức để ghép họ và tên
    public String getHoVaTen() {
        return (hoKhachHang != null ? hoKhachHang + " " : "")
                + (tenKhachHang != null ? tenKhachHang : "N/A");
    }
}