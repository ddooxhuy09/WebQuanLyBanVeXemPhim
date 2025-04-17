package movie.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PhuThuModel {
    private String maPhuThu;
    private String tenPhuThu;
    private BigDecimal gia; // Đổi từ giaPhuThu thành gia
    private List<String> maSuatChieus;

    // Constructors
    public PhuThuModel() {
        this.maSuatChieus = new ArrayList<>();
    }

    public PhuThuModel(movie.entity.PhuThuEntity entity) {
        if (entity != null) {
            this.maPhuThu = entity.getMaPhuThu();
            this.tenPhuThu = entity.getTenPhuThu();
            this.gia = entity.getGiaPhuThu(); // Ánh xạ từ getGiaPhuThu của entity
            this.maSuatChieus = new ArrayList<>();
            if (entity.getSuatChieus() != null) {
                for (movie.entity.SuatChieuEntity suatChieu : entity.getSuatChieus()) {
                    this.maSuatChieus.add(suatChieu.getMaSuatChieu());
                }
            }
        }
    }

    // Getters và Setters
    public String getMaPhuThu() { return maPhuThu; }
    public void setMaPhuThu(String maPhuThu) { this.maPhuThu = maPhuThu; }
    public String getTenPhuThu() { return tenPhuThu; }
    public void setTenPhuThu(String tenPhuThu) { this.tenPhuThu = tenPhuThu; }
    public BigDecimal getGia() { return gia; } // Đổi từ getGiaPhuThu thành getGia
    public void setGia(BigDecimal gia) { this.gia = gia; } // Đổi từ setGiaPhuThu thành setGia
    public List<String> getMaSuatChieus() { return maSuatChieus; }
    public void setMaSuatChieus(List<String> maSuatChieus) { this.maSuatChieus = maSuatChieus; }
}