<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản Lý Quy Đổi Điểm</title>
    <!-- CSS đã được tích hợp trong layout.jsp -->
</head>
<body>
    <div class="promotion-header">
        <h2>Quản Lý Quy Đổi Điểm</h2>
        <div class="add-btn-container">
            <button class="custom-btn" onclick="showAddModal()">Thêm Quy Đổi</button>
        </div>
    </div>

    <!-- Filter Section -->
    <div class="filter-section mb-3">
        <div class="form-group">
            <label for="sort">Sắp xếp theo:</label>
            <select class="form-control" id="sort" onchange="applyFiltersAndSort()">
                <option value="all" ${sortBy == 'all' ? 'selected' : ''}>Mặc định</option>
                <option value="sodiem_asc" ${sortBy == 'sodiem_asc' ? 'selected' : ''}>Số Điểm Cần (tăng dần)</option>
                <option value="sodiem_desc" ${sortBy == 'sodiem_desc' ? 'selected' : ''}>Số Điểm Cần (giảm dần)</option>
                <option value="giatri_asc" ${sortBy == 'giatri_asc' ? 'selected' : ''}>Giá Trị Giảm (tăng dần)</option>
                <option value="giatri_desc" ${sortBy == 'giatri_desc' ? 'selected' : ''}>Giá Trị Giảm (giảm dần)</option>
            </select>
        </div>
        <div class="form-group">
            <label for="filterLoaiUuDai">Loại Ưu Đãi:</label>
            <select class="form-control" id="filterLoaiUuDai" onchange="applyFiltersAndSort()">
                <option value="all" ${loaiUuDai == 'all' ? 'selected' : ''}>Tất cả</option>
                <option value="Giảm giá vé" ${loaiUuDai == 'Giảm giá vé' ? 'selected' : ''}>Giảm giá vé</option>
                <option value="Tặng đồ ăn" ${loaiUuDai == 'Tặng đồ ăn' ? 'selected' : ''}>Tặng đồ ăn</option>
                <option value="Tặng voucher" ${loaiUuDai == 'Tặng voucher' ? 'selected' : ''}>Tặng voucher</option>
            </select>
        </div>
    </div>

    <!-- Error Message -->
    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <!-- List Table -->
    <div class="table-responsive">
        <table class="table table-bordered table-striped" id="redemptionTable">
            <thead>
                <tr>
                    <th>Mã Quy Đổi</th>
                    <th>Tên Ưu Đãi</th>
                    <th>Số Điểm Cần</th>
                    <th>Loại Ưu Đãi</th>
                    <th>Giá Trị Giảm</th>
                    <th>Hành Động</th>
                </tr>
            </thead>
            <tbody id="redemptionList">
                <c:choose>
                    <c:when test="${empty quyDoiList}">
                        <tr><td colspan="6" class="no-data">Không có dữ liệu quy đổi điểm</td></tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="quyDoi" items="${quyDoiList}">
                            <tr data-redemption-id="${quyDoi.maQuyDoi}">
                                <td>${quyDoi.maQuyDoi}</td>
                                <td>${quyDoi.tenUuDai}</td>
                                <td>${quyDoi.soDiemCan}</td>
                                <td>${quyDoi.loaiUuDai}</td>
                                <td><fmt:formatNumber value="${quyDoi.giaTriGiam}" type="currency" currencyCode="VND" /></td>
                                <td>
                                    <button class="custom-btn btn-sm edit-btn" data-redemption-id="${quyDoi.maQuyDoi}">Sửa</button>
                                    <button class="custom-btn btn-sm delete-btn" data-redemption-id="${quyDoi.maQuyDoi}" onclick="deleteRedemption('${quyDoi.maQuyDoi}')">Xóa</button>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>

    <!-- Pagination -->
    <div class="pagination" id="pagination">
        <c:if test="${totalPages > 1}">
            <c:if test="${currentPage > 1}">
                <li class="page-item">
                    <a class="page-link" href="?page=${currentPage - 1}&sort=${sortBy}&loai=${loaiUuDai}">Trước</a>
                </li>
            </c:if>
            <c:forEach begin="1" end="${totalPages}" var="i">
                <li class="page-item ${i == currentPage ? 'active' : ''}">
                    <a class="page-link" href="?page=${i}&sort=${sortBy}&loai=${loaiUuDai}">${i}</a>
                </li>
            </c:forEach>
            <c:if test="${currentPage < totalPages}">
                <li class="page-item">
                    <a class="page-link" href="?page=${currentPage + 1}&sort=${sortBy}&loai=${loaiUuDai}">Sau</a>
                </li>
            </c:if>
        </c:if>
    </div>

    <!-- Add Modal -->
    <div class="modal" id="addModal" style="display: none;">
        <div class="modal-content">
            <h3>Thêm Quy Đổi Điểm Mới</h3>
            <div class="row">
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="addMaQuyDoi">Mã Quy Đổi</label>
                        <input type="text" class="form-control" id="addMaQuyDoi" placeholder="VD: QD001">
                    </div>
                    <div class="form-group">
                        <label for="addTenUuDai">Tên Ưu Đãi</label>
                        <input type="text" class="form-control" id="addTenUuDai" placeholder="VD: Giảm giá vé 10%">
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="addSoDiemCan">Số Điểm Cần</label>
                        <input type="number" class="form-control" id="addSoDiemCan" placeholder="VD: 100">
                    </div>
                    <div class="form-group">
                        <label for="addLoaiUuDai">Loại Ưu Đãi</label>
                        <select class="form-control" id="addLoaiUuDai">
                            <option value="Giảm giá vé">Giảm giá vé</option>
                            <option value="Tặng đồ ăn">Tặng đồ ăn</option>
                            <option value="Tặng voucher">Tặng voucher</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="addGiaTriGiam">Giá Trị Giảm</label>
                        <input type="number" step="0.01" class="form-control" id="addGiaTriGiam" placeholder="VD: 50000.00">
                    </div>
                </div>
            </div>
            <div class="modal-actions">
                <button class="custom-btn" onclick="addRedemptionFromModal()">Thêm</button>
                <button class="custom-btn" onclick="closeAddModal()">Hủy</button>
            </div>
        </div>
    </div>

    <!-- Edit Modal -->
    <div class="modal" id="editModal" style="display: none;">
        <div class="modal-content">
            <h3>Sửa Quy Đổi Điểm</h3>
            <div id="editRedemptionContent"></div>
            <div class="modal-actions">
                <button class="custom-btn" onclick="saveEdit()">Lưu</button>
                <button class="custom-btn" onclick="closeEditModal()">Hủy</button>
            </div>
        </div>
    </div>

    <script>
        // Áp dụng bộ lọc và sắp xếp
        function applyFiltersAndSort() {
            const sort = document.getElementById('sort').value;
            const loaiUuDai = document.getElementById('filterLoaiUuDai').value;
            window.location.href = "${pageContext.request.contextPath}/admin/point-redemptions?page=1&sort=" + sort + "&loai=" + loaiUuDai;
        }

        // Hiển thị modal thêm
        function showAddModal() {
            document.getElementById('addModal').style.display = 'flex';
        }

        // Đóng modal thêm
        function closeAddModal() {
            document.getElementById('addModal').style.display = 'none';
            // Reset form
            document.getElementById('addMaQuyDoi').value = '';
            document.getElementById('addTenUuDai').value = '';
            document.getElementById('addSoDiemCan').value = '';
            document.getElementById('addLoaiUuDai').value = 'Giảm giá vé';
            document.getElementById('addGiaTriGiam').value = '';
        }

        // Thêm quy đổi từ modal
        function addRedemptionFromModal() {
            const redemption = {
                maQuyDoi: document.getElementById('addMaQuyDoi').value,
                tenUuDai: document.getElementById('addTenUuDai').value,
                soDiemCan: parseInt(document.getElementById('addSoDiemCan').value),
                loaiUuDai: document.getElementById('addLoaiUuDai').value,
                giaTriGiam: parseFloat(document.getElementById('addGiaTriGiam').value)
            };

            if (!redemption.maQuyDoi || !redemption.tenUuDai || !redemption.soDiemCan || !redemption.loaiUuDai || !redemption.giaTriGiam) {
                alert('Vui lòng điền đầy đủ thông tin bắt buộc!');
                return;
            }

            fetch('${pageContext.request.contextPath}/admin/point-redemptions/add', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(redemption)
            })
            .then(response => response.json())
            .then(data => {
                if (data.error) {
                    alert(data.error);
                } else {
                    window.location.reload();
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Lỗi khi thêm quy đổi điểm');
            });
        }

     // Hiển thị modal sửa
        function showEditModal(redemptionId) {
            fetch('${pageContext.request.contextPath}/admin/point-redemptions/edit/' + redemptionId)
            .then(response => response.json())
            .then(redemption => {
                if (redemption.error) {
                    alert(redemption.error);
                    return;
                }
                const content = document.getElementById('editRedemptionContent');
                content.innerHTML = `
                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="editMaQuyDoi">Mã Quy Đổi</label>
                                <input type="text" class="form-control" id="editMaQuyDoi" value="${redemption.maQuyDoi}" readonly>
                            </div>
                            <div class="form-group">
                                <label for="editTenUuDai">Tên Ưu Đãi</label>
                                <input type="text" class="form-control" id="editTenUuDai" value="${redemption.tenUuDai}">
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="editSoDiemCan">Số Điểm Cần</label>
                                <input type="number" class="form-control" id="editSoDiemCan" value="${redemption.soDiemCan}">
                            </div>
                            <div class="form-group">
                                <label for="editLoaiUuDai">Loại Ưu Đãi</label>
                                <select class="form-control" id="editLoaiUuDai">
                                    <option value="Giảm giá vé" ${redemption.loaiUuDai == 'Giảm giá vé' ? 'selected' : ''}>Giảm giá vé</option>
                                    <option value="Tặng đồ ăn" ${redemption.loaiUuDai == 'Tặng đồ ăn' ? 'selected' : ''}>Tặng đồ ăn</option>
                                    <option value="Tặng voucher" ${redemption.loaiUuDai == 'Tặng voucher' ? 'selected' : ''}>Tặng voucher</option>
                                </select>
                            </div>
                            <div class="form-group">
                                <label for="editGiaTriGiam">Giá Trị Giảm</label>
                                <input type="number" step="0.01" class="form-control" id="editGiaTriGiam" value="${redemption.giaTriGiam}">
                            </div>
                        </div>
                    </div>`;
                document.getElementById('editModal').style.display = 'flex';
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Lỗi khi lấy thông tin quy đổi điểm');
            });
        }

        // Lưu thông tin sửa
        function saveEdit() {
            const updatedRedemption = {
                maQuyDoi: document.getElementById('editMaQuyDoi').value,
                tenUuDai: document.getElementById('editTenUuDai').value,
                soDiemCan: parseInt(document.getElementById('editSoDiemCan').value),
                loaiUuDai: document.getElementById('editLoaiUuDai').value,
                giaTriGiam: parseFloat(document.getElementById('editGiaTriGiam').value)
            };

            if (!updatedRedemption.tenUuDai || !updatedRedemption.soDiemCan || !updatedRedemption.loaiUuDai || !updatedRedemption.giaTriGiam) {
                alert('Vui lòng điền đầy đủ thông tin bắt buộc!');
                return;
            }

            fetch('${pageContext.request.contextPath}/admin/point-redemptions/update', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(updatedRedemption)
            })
            .then(response => response.json())
            .then(data => {
                if (data.error) {
                    alert(data.error);
                } else {
                    window.location.reload();
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Lỗi khi cập nhật quy đổi điểm');
            });
        }

        // Xóa quy đổi
        function deleteRedemption(redemptionId) {
            if (confirm(`Bạn có chắc muốn xóa quy đổi "${redemptionId}" không? Hành động này không thể hoàn tác.`)) {
                fetch('${pageContext.request.contextPath}/admin/point-redemptions/delete/' + redemptionId, {
                    method: 'GET'
                })
                .then(response => response.json())
                .then(data => {
                    if (data.error) {
                        alert(data.error);
                    } else {
                        window.location.reload();
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Lỗi khi xóa quy đổi điểm');
                });
            }
        }

        // Đóng modal sửa
        function closeEditModal() {
            document.getElementById('editModal').style.display = 'none';
        }

        // Khởi tạo sự kiện cho các nút sửa
        document.querySelectorAll('.edit-btn').forEach(button => {
            button.addEventListener('click', () => {
                const redemptionId = button.getAttribute('data-redemption-id');
                showEditModal(redemptionId);
            });
        });
    </script>
</body>
</html>