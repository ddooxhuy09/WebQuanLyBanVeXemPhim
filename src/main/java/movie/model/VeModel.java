package movie.model;

import java.math.BigDecimal;
import java.util.Date;

public class VeModel {
    private String maVe;
    private String maKhachHang;
    private String maSuatChieu;
    private String maGhe;
    private BigDecimal giaVe;
    private Date ngayMua;

    // Constructors
    public VeModel() {}

    public VeModel(movie.entity.VeEntity entity) {
        if (entity != null) {
            this.maVe = entity.getMaVe();
            this.maKhachHang = entity.getMaKhachHang();
            this.maSuatChieu = entity.getMaSuatChieu();
            this.maGhe = entity.getMaGhe();
            this.giaVe = entity.getGiaVe();
            this.ngayMua = entity.getNgayMua();
        }
    }

    // Getters and Setters
    public String getMaVe() { return maVe; }
    public void setMaVe(String maVe) { this.maVe = maVe; }
    public String getMaKhachHang() { return maKhachHang; }
    public void setMaKhachHang(String maKhachHang) { this.maKhachHang = maKhachHang; }
    public String getMaSuatChieu() { return maSuatChieu; }
    public void setMaSuatChieu(String maSuatChieu) { this.maSuatChieu = maSuatChieu; }
    public String getMaGhe() { return maGhe; }
    public void setMaGhe(String maGhe) { this.maGhe = maGhe; }
    public BigDecimal getGiaVe() { return giaVe; }
    public void setGiaVe(BigDecimal giaVe) { this.giaVe = giaVe; }
    public Date getNgayMua() { return ngayMua; }
    public void setNgayMua(Date ngayMua) { this.ngayMua = ngayMua; }
}