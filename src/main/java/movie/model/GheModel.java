package movie.model;

public class GheModel {
    private String maGhe;
    private String soGhe; // Đã sửa kiểu từ int sang String
    private String tenHang;
    private String soGheAdmin;
    private String tenHangAdmin;
    private String maLoaiGhe;
    private String maPhongChieu;

    // Constructors
    public GheModel() {}

    public GheModel(movie.entity.GheEntity entity) {
        if (entity != null) {
            this.maGhe = entity.getMaGhe();
            this.soGhe = entity.getSoGhe(); // Đã sửa để lấy giá trị String
            this.tenHang = entity.getTenHang();
            this.soGheAdmin = entity.getSoGheAdmin();
            this.tenHangAdmin = entity.getTenHangAdmin();
            this.maLoaiGhe = entity.getLoaiGhe() != null ? entity.getLoaiGhe().getMaLoaiGhe() : null;
            this.maPhongChieu = entity.getPhongChieu() != null ? entity.getPhongChieu().getMaPhongChieu() : null;
        }
    }

    // Getters và Setters
    public String getMaGhe() { return maGhe; }
    public void setMaGhe(String maGhe) { this.maGhe = maGhe; }
    public String getSoGhe() { return soGhe; } // Đã sửa kiểu trả về thành String
    public void setSoGhe(String soGhe) { this.soGhe = soGhe; } // Đã sửa tham số thành String
    public String getTenHang() { return tenHang; }
    public void setTenHang(String tenHang) { this.tenHang = tenHang; }
    public String getSoGheAdmin() {
        return soGheAdmin;
    }

    public void setSoGheAdmin(String soGheAdmin) {
        this.soGheAdmin = soGheAdmin;
    }

    public String getTenHangAdmin() {
        return tenHangAdmin;
    }

    public void setTenHangAdmin(String tenHangAdmin) {
        this.tenHangAdmin = tenHangAdmin;
    }
    public String getMaLoaiGhe() { return maLoaiGhe; }
    public void setMaLoaiGhe(String maLoaiGhe) { this.maLoaiGhe = maLoaiGhe; }
    public String getMaPhongChieu() { return maPhongChieu; }
    public void setMaPhongChieu(String maPhongChieu) { this.maPhongChieu = maPhongChieu; }
}
