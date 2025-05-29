package movie.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "DonHang")
public class DonHangEntity {
    @Id
    @Column(name = "MaDonHang", length = 10)
    private String maDonHang;

    @Column(name = "MaKhachHang", length = 10)
    private String maKhachHang;

    @ManyToOne
    @JoinColumn(name = "MaKhachHang", referencedColumnName = "MaKhachHang", insertable = false, updatable = false)
    private KhachHangEntity khachHang;

    @Column(name = "MaKhuyenMai", length = 10)
    private String maKhuyenMai;

    @Column(name = "MaQuyDoi", length = 10)
    private String maQuyDoi;

    @Column(name = "TongTien")
    private BigDecimal tongTien;

    @Column(name = "DatHang")
    private boolean datHang;

    @Column(name = "NgayDat")
    @Temporal(TemporalType.DATE)
    private Date ngayDat;

    @Column(name = "DiemSuDung")
    private Integer diemSuDung;

    @OneToMany(mappedBy = "donHang", cascade = CascadeType.ALL)
    private List<ChiTietDonHangBapNuocEntity> chiTietBapNuoc;

    @OneToMany(mappedBy = "donHang", cascade = CascadeType.ALL)
    private List<ChiTietDonHangComboEntity> chiTietCombo;

    @OneToMany(mappedBy = "donHang", cascade = CascadeType.ALL)
    private List<VeEntity> veList;

    @OneToMany(mappedBy = "donHang", cascade = CascadeType.ALL)
    private List<ThanhToanEntity> thanhToans;

    // Thêm phương thức tính điểm dựa trên tổng tiền
    public int tinhDiem() {
        if (tongTien == null) return 0;
        return tongTien.divide(new BigDecimal("1000"), RoundingMode.DOWN).intValue();
    }

    // Getters và Setters
    public String getMaDonHang() { return maDonHang; }
    public void setMaDonHang(String maDonHang) { this.maDonHang = maDonHang; }
    public String getMaKhachHang() { return maKhachHang; }
    public void setMaKhachHang(String maKhachHang) { this.maKhachHang = maKhachHang; }
    public KhachHangEntity getKhachHang() { return khachHang; }
    public void setKhachHang(KhachHangEntity khachHang) { this.khachHang = khachHang; }
    public String getMaKhuyenMai() { return maKhuyenMai; }
    public void setMaKhuyenMai(String maKhuyenMai) { this.maKhuyenMai = maKhuyenMai; }
    public String getMaQuyDoi() { return maQuyDoi; }
    public void setMaQuyDoi(String maQuyDoi) { this.maQuyDoi = maQuyDoi; }
    public BigDecimal getTongTien() { return tongTien; }
    public void setTongTien(BigDecimal tongTien) { this.tongTien = tongTien; }
    public boolean isDatHang() { return datHang; }
    public void setDatHang(boolean datHang) { this.datHang = datHang; }
    public Date getNgayDat() { return ngayDat; }
    public void setNgayDat(Date ngayDat) { this.ngayDat = ngayDat; }
    public Integer getDiemSuDung() { return diemSuDung; }
    public void setDiemSuDung(Integer diemSuDung) { this.diemSuDung = diemSuDung; }
    public List<ChiTietDonHangBapNuocEntity> getChiTietBapNuoc() { return chiTietBapNuoc; }
    public void setChiTietBapNuoc(List<ChiTietDonHangBapNuocEntity> chiTietBapNuoc) { this.chiTietBapNuoc = chiTietBapNuoc; }
    public List<ChiTietDonHangComboEntity> getChiTietCombo() { return chiTietCombo; }
    public void setChiTietCombo(List<ChiTietDonHangComboEntity> chiTietCombo) { this.chiTietCombo = chiTietCombo; }
    public List<VeEntity> getVeList() { return veList; }
    public void setVeList(List<VeEntity> veList) { this.veList = veList; }
    public List<ThanhToanEntity> getThanhToans() { return thanhToans; }
    public void setThanhToans(List<ThanhToanEntity> thanhToans) { this.thanhToans = thanhToans; }
}