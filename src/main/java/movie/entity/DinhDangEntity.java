package movie.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "DinhDang")
public class DinhDangEntity {
    @Id
    @Column(name = "MaDinhDang", length = 10)
    private String maDinhDang;

    @Column(name = "TenDinhDang", columnDefinition = "nvarchar(50)")
    private String tenDinhDang;

    @ManyToMany(mappedBy = "dinhDangs")
    private Set<PhimEntity> phims = new HashSet<>();

    // Constructors
    public DinhDangEntity() {}

    // Getters and Setters
    public String getMaDinhDang() { return maDinhDang; }
    public void setMaDinhDang(String maDinhDang) { this.maDinhDang = maDinhDang; }
    public String getTenDinhDang() { return tenDinhDang; }
    public void setTenDinhDang(String tenDinhDang) { this.tenDinhDang = tenDinhDang; }
    public Set<PhimEntity> getPhims() { return phims; }
    public void setPhims(Set<PhimEntity> phims) { this.phims = phims; }
}