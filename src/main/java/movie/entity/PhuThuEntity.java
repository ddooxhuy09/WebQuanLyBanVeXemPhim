package movie.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "PhuThu")
public class PhuThuEntity {
    @Id
    @Column(name = "MaPhuThu", length = 10)
    private String maPhuThu;

    @Column(name = "TenPhuThu", columnDefinition = "nvarchar(50)")
    private String tenPhuThu;

    @Column(name = "Gia")
    private BigDecimal gia;

    @ManyToMany
    @JoinTable(
        name = "SuatChieu_PhuThu",
        joinColumns = @JoinColumn(name = "MaPhuThu"),
        inverseJoinColumns = @JoinColumn(name = "MaSuatChieu")
    )
    private Set<SuatChieuEntity> suatChieus = new HashSet<>();

    // Constructors
    public PhuThuEntity() {}

    // Getters và Setters
    public String getMaPhuThu() { return maPhuThu; }
    public void setMaPhuThu(String maPhuThu) { this.maPhuThu = maPhuThu; }
    public String getTenPhuThu() { return tenPhuThu; }
    public void setTenPhuThu(String tenPhuThu) { this.tenPhuThu = tenPhuThu; }
    public BigDecimal getGia() { return gia; }
    public void setGia(BigDecimal gia) { this.gia = gia; }
    public Set<SuatChieuEntity> getSuatChieus() { return suatChieus; }
    public void setSuatChieus(Set<SuatChieuEntity> suatChieus) { this.suatChieus = suatChieus; }
}