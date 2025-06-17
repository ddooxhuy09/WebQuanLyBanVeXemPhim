package movie.model;

import java.math.BigDecimal;

public class QuyDoiDiemModel {

    private String maQuyDoi;
    private String tenUuDai;
    private Integer soDiemCan;
    private String loaiUuDai;
    private BigDecimal giaTriGiam;

    // Constructors
    public QuyDoiDiemModel() {}

    public QuyDoiDiemModel(movie.entity.QuyDoiDiemEntity entity) {
        if (entity != null) {
            this.maQuyDoi = entity.getMaQuyDoi();
            this.tenUuDai = entity.getTenUuDai();
            this.soDiemCan = entity.getSoDiemCan();
            this.loaiUuDai = entity.getLoaiUuDai();
            this.giaTriGiam = entity.getGiaTriGiam();
        }
    }

    // Getters và Setters
    public String getMaQuyDoi() {
        return maQuyDoi;
    }

    public void setMaQuyDoi(String maQuyDoi) {
        this.maQuyDoi = maQuyDoi;
    }

    public String getTenUuDai() {
        return tenUuDai;
    }

    public void setTenUuDai(String tenUuDai) {
        this.tenUuDai = tenUuDai;
    }

    public Integer getSoDiemCan() {
        return soDiemCan;
    }

    public void setSoDiemCan(Integer soDiemCan) {
        this.soDiemCan = soDiemCan;
    }

    public String getLoaiUuDai() {
        return loaiUuDai;
    }

    public void setLoaiUuDai(String loaiUuDai) {
        this.loaiUuDai = loaiUuDai;
    }

    public BigDecimal getGiaTriGiam() {
        return giaTriGiam;
    }

    public void setGiaTriGiam(BigDecimal giaTriGiam) {
        this.giaTriGiam = giaTriGiam;
    }
}