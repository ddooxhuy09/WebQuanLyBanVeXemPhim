# Hướng dẫn cài đặt và chạy Web Bán Vé Xem Phim

## Yêu cầu hệ thống
- Eclipse IDE for Enterprise Java and Web Developers
- Apache Tomcat Server (version 9.0 trở lên)
- Microsoft SQL Server
- JDK 8 trở lên

## Cài đặt môi trường

### 1. Cài đặt Eclipse
1. Tải Eclipse IDE for Enterprise Java and Web Developers từ trang chủ Eclipse
2. Giải nén và chạy Eclipse
3. Chọn workspace phù hợp

### 2. Cài đặt Tomcat
1. Tải Apache Tomcat từ trang chủ Apache
2. Giải nén vào thư mục mong muốn
3. Trong Eclipse:
   - Vào Window > Preferences > Server > Runtime Environments
   - Click Add > Apache Tomcat
   - Chọn thư mục đã giải nén Tomcat
   - Click Finish

### 3. Cài đặt SQL Server
1. Tải và cài đặt Microsoft SQL Server
2. Cài đặt SQL Server Management Studio (SSMS)
3. Tạo database mới cho ứng dụng
4. Run file db.sql

## Cấu hình project

### 1. Import project vào Eclipse
1. File > Import > General > Existing Projects into Workspace
2. Chọn thư mục chứa source code
3. Click Finish

### 2. Cấu hình Database
1. Mở file cấu hình database là spring-config-mvc.xml
2. Cập nhật thông tin kết nối:
   ```
   jdbc:sqlserver://localhost:1433;databaseName=WebBanVeXemPhim
   username=your_username
   password=your_password
   ```

### 3. Cấu hình Server
1. Click chuột phải vào project
2. Properties > Project Facets > Runtimes
3. Chọn Apache Tomcat đã cài đặt
4. Click Apply and Close

## Chạy ứng dụng
1. Click chuột phải vào project
2. Run As > Run on Server
3. Chọn Tomcat server
4. Click Finish

## Truy cập ứng dụng
- Mở trình duyệt web
- Truy cập địa chỉ: `http://localhost:8080/WebBanVeXemPhim`

## Xử lý lỗi thường gặp
1. Lỗi kết nối database:
   - Kiểm tra thông tin kết nối trong file cấu hình
   - Đảm bảo SQL Server đang chạy
   - Kiểm tra firewall

2. Lỗi Tomcat:
   - Kiểm tra port 8080 có đang được sử dụng
   - Kiểm tra cấu hình server trong Eclipse
   - Xem log trong thư mục logs của Tomcat
