package movie.model;

import movie.entity.KhuyenMaiEntity;
import java.math.BigDecimal;
import java.util.Date;

public class KhuyenMaiModel {

    private String maKhuyenMai;
    private String maCode;
    private String moTa;
    private String loaiGiamGia;
    private BigDecimal giaTriGiam;
    private Date ngayBatDau;
    private Date ngayKetThuc;
    private String apDungCho;

    // Constructors
    public KhuyenMaiModel() {}

    public KhuyenMaiModel(KhuyenMaiEntity entity) {
        if (entity != null) {
            this.maKhuyenMai = entity.getMaKhuyenMai();
            this.maCode = entity.getMaCode();
            this.moTa = entity.getMoTa();
            this.loaiGiamGia = entity.getLoaiGiamGia();
            this.giaTriGiam = entity.getGiaTriGiam();
            this.ngayBatDau = entity.getNgayBatDau();
            this.ngayKetThuc = entity.getNgayKetThuc();
            this.apDungCho = entity.getApDungCho();
        }
    }

    // Getters and Setters
    public String getMaKhuyenMai() { return maKhuyenMai; }
    public void setMaKhuyenMai(String maKhuyenMai) { this.maKhuyenMai = maKhuyenMai; }

    public String getMaCode() { return maCode; }
    public void setMaCode(String maCode) { this.maCode = maCode; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public String getLoaiGiamGia() { return loaiGiamGia; }
    public void setLoaiGiamGia(String loaiGiamGia) { this.loaiGiamGia = loaiGiamGia; }

    public BigDecimal getGiaTriGiam() { return giaTriGiam; }
    public void setGiaTriGiam(BigDecimal giaTriGiam) { this.giaTriGiam = giaTriGiam; }

    public Date getNgayBatDau() { return ngayBatDau; }
    public void setNgayBatDau(Date ngayBatDau) { this.ngayBatDau = ngayBatDau; }

    public Date getNgayKetThuc() { return ngayKetThuc; }
    public void setNgayKetThuc(Date ngayKetThuc) { this.ngayKetThuc = ngayKetThuc; }

    public String getApDungCho() { return apDungCho; }
    public void setApDungCho(String apDungCho) { this.apDungCho = apDungCho; }
}