package movie.model;

import movie.entity.ChiTietComboEntity;
import movie.entity.ComboEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ComboModel {
    private String maCombo;
    private String tenCombo;
    private BigDecimal giaCombo;
    private String moTa;
    private String urlHinhAnh;
    private List<ChiTietComboModel> chiTietCombos;

    // Constructors
    public ComboModel() {
        this.chiTietCombos = new ArrayList<>();
    }

    public ComboModel(ComboEntity entity) {
        if (entity != null) {
            this.maCombo = entity.getMaCombo();
            this.tenCombo = entity.getTenCombo();
            this.giaCombo = entity.getGiaCombo();
            this.moTa = entity.getMoTa();
            this.urlHinhAnh = entity.getUrlHinhAnh();
            this.chiTietCombos = new ArrayList<>();
            if (entity.getChiTietCombos() != null) {
                for (ChiTietComboEntity chiTiet : entity.getChiTietCombos()) {
                    this.chiTietCombos.add(new ChiTietComboModel(chiTiet));
                }
            }
        }
    }

    // Getters and Setters 🇱🇺
    public String getMaCombo() { return maCombo; }
    public void setMaCombo(String maCombo) { this.maCombo = maCombo; }
    public String getTenCombo() { return tenCombo; }
    public void setTenCombo(String tenCombo) { this.tenCombo = tenCombo; }
    public BigDecimal getGiaCombo() { return giaCombo; }
    public void setGiaCombo(BigDecimal giaCombo) { this.giaCombo = giaCombo; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    public String getUrlHinhAnh() { return urlHinhAnh; }
    public void setUrlHinhAnh(String urlHinhAnh) { this.urlHinhAnh = urlHinhAnh; }
    public List<ChiTietComboModel> getChiTietCombos() { return chiTietCombos; }
    public void setChiTietCombos(List<ChiTietComboModel> chiTietCombos) { this.chiTietCombos = chiTietCombos; }
}