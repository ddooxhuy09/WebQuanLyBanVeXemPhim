package movie.model;

public class LoaiGheModel {
    private String maLoaiGhe;
    private String tenLoaiGhe;
    private double heSoGia;
    private String mauGhe;
    private int soCho;

    // Constructors
    public LoaiGheModel() {}

    public LoaiGheModel(movie.entity.LoaiGheEntity entity) {
        if (entity != null) {
            this.maLoaiGhe = entity.getMaLoaiGhe();
            this.tenLoaiGhe = entity.getTenLoaiGhe();
            this.heSoGia = entity.getHeSoGia();
            this.mauGhe = entity.getMauGhe();
            this.soCho = entity.getSoCho();
        }
    }

    // Getters và Setters
    public String getMaLoaiGhe() { return maLoaiGhe; }
    public void setMaLoaiGhe(String maLoaiGhe) { this.maLoaiGhe = maLoaiGhe; }
    public String getTenLoaiGhe() { return tenLoaiGhe; }
    public void setTenLoaiGhe(String tenLoaiGhe) { this.tenLoaiGhe = tenLoaiGhe; }
    public double getHeSoGia() { return heSoGia; }
    public void setHeSoGia(double heSoGia) { this.heSoGia = heSoGia; }
    public String getMauGhe() { return mauGhe; }
    public void setMauGhe(String mauGhe) { this.mauGhe = mauGhe; }
    public int getSoCho() { return soCho; }
    public void setSoCho(int soCho) { this.soCho = soCho; }
}