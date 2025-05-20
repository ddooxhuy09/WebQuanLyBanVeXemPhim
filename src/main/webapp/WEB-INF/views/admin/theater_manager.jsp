<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản Lý Rạp Chiếu</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <!-- Các file CSS của bạn đã được giả định là đã bao gồm trong layout -->
</head>
<body>
    <div class="container">
        <!-- Danh sách rạp chiếu -->
        <div class="theater-list">
            <div class="header">
                <h1>Danh Sách Rạp Chiếu</h1>
                <button class="custom-btn" onclick="showAddForm()">Thêm Rạp Chiếu</button>
            </div>
            <c:if test="${not empty error}">
                <div class="alert alert-danger">${error}</div>
            </c:if>
            <div class="table-responsive">
                <table class="table">
                    <thead>
                        <tr>
                            <th>Mã Rạp</th>
                            <th>Tên Rạp</th>
                            <th>Địa Chỉ</th>
                            <th>SĐT Liên Hệ</th>
                            <th>Hành Động</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="rap" items="${rapChieuList}">
                            <tr data-theater-id="${rap.maRapChieu}">
                                <td>${rap.maRapChieu}</td>
                                <td>${rap.tenRapChieu}</td>
                                <td>${rap.diaChi}</td>
                                <td>${rap.soDienThoaiLienHe}</td>
                                <td>
                                    <button class="custom-btn btn-sm mr-1" onclick="showEditModal('${rap.maRapChieu}', '${rap.tenRapChieu}', '${rap.diaChi}', '${rap.soDienThoaiLienHe}')">Sửa</button>
                                    <button class="custom-btn btn-sm" onclick="confirmDelete('${rap.maRapChieu}', '${rap.tenRapChieu}')">Xóa</button>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- Form thêm rạp chiếu -->
        <div class="theater-management" id="addTheaterForm" style="display: none;">
            <h2>Thêm Rạp Chiếu Mới</h2>
            <form action="${pageContext.request.contextPath}/admin/theaters/add" method="post">
                <div class="row">
                    <div class="col-md-6">
                        <div class="form-group">
                            <label for="maRap">Mã Rạp Chiếu</label>
                            <input type="text" class="form-control" id="maRap" name="maRapChieu" value="${newMaRapChieu}" readonly>
                        </div>
                        <div class="form-group">
                            <label for="tenRap">Tên Rạp Chiếu</label>
                            <input type="text" class="form-control" id="tenRap" name="tenRapChieu" placeholder="Rạp 1 - Quận 1" required>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="form-group">
                            <label for="diaChi">Địa Chỉ</label>
                            <input type="text" class="form-control" id="diaChi" name="diaChi" placeholder="123 Đường ABC, Quận 1" required>
                        </div>
                        <div class="form-group">
                            <label for="sdtLienHe">SĐT Liên Hệ</label>
                            <input type="text" class="form-control" id="sdtLienHe" name="soDienThoaiLienHe" placeholder="0909123456" required>
                        </div>
                    </div>
                </div>
                <button type="submit" class="custom-btn">Thêm Rạp Chiếu</button>
                <button type="button" class="custom-btn" onclick="hideAddForm()">Hủy</button>
            </form>
        </div>

        <!-- Modal sửa rạp chiếu -->
        <div class="modal" id="editModal">
            <div class="modal-content">
                <h3>Sửa Thông Tin Rạp Chiếu</h3>
                <form action="${pageContext.request.contextPath}/admin/theaters/update" method="post">
                    <div class="form-group">
                        <label for="editMaRap">Mã Rạp</label>
                        <input type="text" id="editMaRap" name="maRapChieu" class="form-control" readonly>
                    </div>
                    <div class="form-group">
                        <label for="editTenRap">Tên Rạp</label>
                        <input type="text" id="editTenRap" name="tenRapChieu" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label for="editDiaChi">Địa Chỉ</label>
                        <input type="text" id="editDiaChi" name="diaChi" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label for="editSdtLienHe">SĐT Liên Hệ</label>
                        <input type="text" id="editSdtLienHe" name="soDienThoaiLienHe" class="form-control" required>
                    </div>
                    <div class="modal-actions">
                        <button type="submit" class="custom-btn">Lưu</button>
                        <button type="button" class="custom-btn" onclick="closeModal()">Hủy</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.5.2/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function showAddForm() {
            document.getElementById('addTheaterForm').style.display = 'block';
        }

        function hideAddForm() {
            document.getElementById('addTheaterForm').style.display = 'none';
        }

        function showEditModal(maRap, tenRap, diaChi, sdtLienHe) {
            document.getElementById('editMaRap').value = maRap;
            document.getElementById('editTenRap').value = tenRap;
            document.getElementById('editDiaChi').value = diaChi;
            document.getElementById('editSdtLienHe').value = sdtLienHe;
            document.getElementById('editModal').style.display = 'flex';
        }

        function closeModal() {
            document.getElementById('editModal').style.display = 'none';
        }

        function confirmDelete(maRap, tenRap) {
            if (confirm(`Bạn có chắc muốn xóa rạp "${tenRap}" không? Hành động này không thể hoàn tác.`)) {
                window.location.href = "${pageContext.request.contextPath}/admin/theaters/delete/" + maRap;
            }
        }
    </script>
</body>
</html>