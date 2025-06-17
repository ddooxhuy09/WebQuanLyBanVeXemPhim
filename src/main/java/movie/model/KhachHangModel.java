package movie.model;

import movie.entity.KhachHangEntity;
import java.util.Date;

public class KhachHangModel {
    private String maKhachHang;
    private String hoKhachHang;
    private String tenKhachHang;
    private String soDienThoai;
    private String email;
    private String matKhau;
    private Date ngaySinh;
    private Date ngayDangKy;
    private int tongDiem;

    public KhachHangModel() {}

    public KhachHangModel(KhachHangEntity entity) {
        if (entity != null) {
            this.maKhachHang = entity.getMaKhachHang();
            this.hoKhachHang = entity.getHoKhachHang();
            this.tenKhachHang = entity.getTenKhachHang();
            this.soDienThoai = entity.getSoDienThoai();
            this.email = entity.getEmail();
            this.matKhau = entity.getMatKhau();
            this.ngaySinh = entity.getNgaySinh();
            this.ngayDangKy = entity.getNgayDangKy();
            this.tongDiem = entity.getTongDiem();
        }
    }

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

    // Thêm phương thức để ghép họ và tên
    public String getHoVaTen() {
        return (hoKhachHang != null ? hoKhachHang + " " : "")
                + (tenKhachHang != null ? tenKhachHang : "N/A");
    }
}