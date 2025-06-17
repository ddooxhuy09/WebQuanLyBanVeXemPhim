package movie.model;

import movie.entity.ChiTietComboEntity;

public class ChiTietComboModel {
    private String maCombo;
    private String maBapNuoc;
    private Integer soLuong;

    // Constructors
    public ChiTietComboModel() {}

    public ChiTietComboModel(ChiTietComboEntity entity) {
        if (entity != null) {
            this.maCombo = entity.getCombo() != null ? entity.getCombo().getMaCombo() : null; // Lấy maCombo từ ComboEntity
            this.maBapNuoc = entity.getMaBapNuoc();
            this.soLuong = entity.getSoLuong();
        }
    }

    // Getters and Setters
    public String getMaCombo() { return maCombo; }
    public void setMaCombo(String maCombo) { this.maCombo = maCombo; }
    public String getMaBapNuoc() { return maBapNuoc; }
    public void setMaBapNuoc(String maBapNuoc) { this.maBapNuoc = maBapNuoc; }
    public Integer getSoLuong() { return soLuong; }
    public void setSoLuong(Integer soLuong) { this.soLuong = soLuong; }
}