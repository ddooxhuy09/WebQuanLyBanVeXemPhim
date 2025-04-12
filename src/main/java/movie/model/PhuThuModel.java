package movie.model;

import java.math.BigDecimal;

public class PhuThuModel {
    private String maPhuThu;
    private String tenPhuThu;
    private BigDecimal gia;

    // Constructors
    public PhuThuModel() {}

    public PhuThuModel(movie.entity.PhuThuEntity entity) {
        if (entity != null) {
            this.maPhuThu = entity.getMaPhuThu();
            this.tenPhuThu = entity.getTenPhuThu();
            this.gia = entity.getGia();
        }
    }

    // Getters và Setters
    public String getMaPhuThu() { return maPhuThu; }
    public void setMaPhuThu(String maPhuThu) { this.maPhuThu = maPhuThu; }
    public String getTenPhuThu() { return tenPhuThu; }
    public void setTenPhuThu(String tenPhuThu) { this.tenPhuThu = tenPhuThu; }
    public BigDecimal getGia() { return gia; }
    public void setGia(BigDecimal gia) { this.gia = gia; }
}