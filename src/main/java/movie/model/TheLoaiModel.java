package movie.model;

public class TheLoaiModel {
    private String maTheLoai;
    private String tenTheLoai;

    // Constructors
    public TheLoaiModel() {}

    public TheLoaiModel(movie.entity.TheLoaiEntity entity) {
        if (entity != null) {
            this.maTheLoai = entity.getMaTheLoai();
            this.tenTheLoai = entity.getTenTheLoai();
        }
    }

    // Getters và Setters
    public String getMaTheLoai() { return maTheLoai; }
    public void setMaTheLoai(String maTheLoai) { this.maTheLoai = maTheLoai; }
    public String getTenTheLoai() { return tenTheLoai; }
    public void setTenTheLoai(String tenTheLoai) { this.tenTheLoai = tenTheLoai; }
}