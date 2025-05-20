package movie.model;

import movie.entity.DinhDangEntity;

public class DinhDangModel {
    private String maDinhDang;
    private String tenDinhDang;

    // Constructors
    public DinhDangModel() {}

    public DinhDangModel(DinhDangEntity entity) {
        if (entity != null) {
            this.maDinhDang = entity.getMaDinhDang();
            this.tenDinhDang = entity.getTenDinhDang();
        }
    }

    // Getters and Setters
    public String getMaDinhDang() { return maDinhDang; }
    public void setMaDinhDang(String maDinhDang) { this.maDinhDang = maDinhDang; }
    public String getTenDinhDang() { return tenDinhDang; }
    public void setTenDinhDang(String tenDinhDang) { this.tenDinhDang = tenDinhDang; }
}