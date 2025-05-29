<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div class="room-list mb-4">
    <div class="room-header">
        <h2>Danh Sách Phòng Chiếu</h2>
        <div class="add-btn-container">
            <button class="custom-btn" onclick="showAddModal()">Thêm Phòng Chiếu</button>
            <button class="custom-btn" id="manageSeatTypesBtn" onclick="showManageSeatTypesModal()">Quản Lý Loại Ghế</button>
        </div>
    </div>
    <!-- Combo box filter -->
    <div class="filter-section mb-3">
        <div class="form-group">
            <label for="filterMaRap">Lọc Theo Rạp Chiếu</label>
            <select class="form-control" id="filterMaRap">
                <option value="">Tất cả</option>
                <c:forEach var="rap" items="${rapChieuList}">
                    <%-- Use maRapChieu for value, tenRapChieu for display --%>
                    <option value="${rap.maRapChieu}">${rap.tenRapChieu} (${rap.maRapChieu})</option>
                </c:forEach>
            </select>
        </div>
    </div>

    <!-- Error and Success Messages from Redirect -->
    <c:if test="${param.error == 'invalid_rap'}">
        <div class="alert alert-danger">Lỗi: Rạp chiếu không hợp lệ.</div>
    </c:if>
    <c:if test="${param.error == 'add_failed'}">
        <div class="alert alert-danger">Lỗi khi thêm phòng chiếu. Vui lòng thử lại.</div>
    </c:if>
     <c:if test="${param.error == 'update_failed'}">
        <div class="alert alert-danger">Lỗi khi cập nhật phòng chiếu. Vui lòng thử lại.</div>
    </c:if>
    <c:if test="${param.error == 'not_found'}">
        <div class="alert alert-danger">Lỗi: Không tìm thấy phòng chiếu.</div>
    </c:if>
    <c:if test="${param.error == 'delete_constraint'}">
        <div class="alert alert-danger">Lỗi: Không thể xóa phòng chiếu vì có ghế hoặc lịch chiếu liên quan.</div>
    </c:if>
     <c:if test="${param.error == 'delete_failed'}">
        <div class="alert alert-danger">Lỗi khi xóa phòng chiếu. Vui lòng thử lại.</div>
    </c:if>
    <c:if test="${param.success == 'add_ok'}">
        <div class="alert alert-success">Thêm phòng chiếu thành công!</div>
    </c:if>
    <c:if test="${param.success == 'update_ok'}">
        <div class="alert alert-success">Cập nhật phòng chiếu thành công!</div>
    </c:if>
    <c:if test="${param.success == 'delete_ok'}">
        <div class="alert alert-success">Xóa phòng chiếu thành công!</div>
    </c:if>
    <%-- Seat Type messages --%>
    <c:if test="${param.error == 'seat_type_name_required'}">
        <div class="alert alert-danger">Lỗi: Tên loại ghế không được để trống.</div>
    </c:if>
    <c:if test="${param.error == 'seat_type_add_failed'}">
        <div class="alert alert-danger">Lỗi khi thêm loại ghế.</div>
    </c:if>
    <c:if test="${param.error == 'seat_type_update_failed'}">
        <div class="alert alert-danger">Lỗi khi cập nhật loại ghế.</div>
    </c:if>
    <c:if test="${param.error == 'seat_type_delete_constraint'}">
        <div class="alert alert-danger">Lỗi: Không thể xóa loại ghế vì đang được sử dụng.</div>
    </c:if>
    <c:if test="${param.error == 'seat_type_not_found'}">
        <div class="alert alert-danger">Lỗi: Không tìm thấy loại ghế.</div>
    </c:if>
    <c:if test="${param.error == 'seat_type_delete_failed'}">
        <div class="alert alert-danger">Lỗi khi xóa loại ghế.</div>
    </c:if>
    <c:if test="${param.success == 'seat_type_add_ok'}">
        <div class="alert alert-success">Thêm loại ghế thành công!</div>
    </c:if>
    <c:if test="${param.success == 'seat_type_update_ok'}">
        <div class="alert alert-success">Cập nhật loại ghế thành công!</div>
    </c:if>
    <c:if test="${param.success == 'seat_type_delete_ok'}">
        <div class="alert alert-success">Xóa loại ghế thành công!</div>
    </c:if>

    <div class="table-responsive">
        <table class="table table-bordered table-striped" id="roomTable">
            <thead>
                <tr>
                    <th>Mã Phòng</th>
                    <th>Tên Phòng</th>
                    <th>Sức Chứa</th>
                    <th>Rạp Chiếu</th> <%-- Changed header --%>
                    <th>Hình Ảnh</th>
                    <th>Sơ đồ Ghế</th>
                    <th>Hành Động</th>
                </tr>
            </thead>
            <tbody id="roomList">
                <c:choose>
                    <c:when test="${empty roomList}">
                        <tr>
                            <td colspan="7" class="no-data text-center">Không có phòng chiếu nào</td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="room" items="${roomList}">
                            <%-- Use maRapChieu for data-rap-id for filtering --%>
                            <tr data-room-id="${room.maPhongChieu}" data-rap-id="${room.maRapChieu}">
                                <td data-field="maPhong">${room.maPhongChieu}</td>
                                <td data-field="tenPhong">${fn:escapeXml(room.tenPhongChieu)}</td>
                                <td data-field="sucChua">${room.sucChua}</td>
                                <%-- Display tenRapChieu instead of maRapChieu --%>
                                <td data-field="tenRap">${fn:escapeXml(room.tenRapChieu)} (${room.maRapChieu})</td>
                                <td data-field="hinhAnh">
                                    <c:choose>
                                        <c:when test="${not empty room.urlHinhAnh}">
                                            <img src="${pageContext.request.contextPath}/${fn:escapeXml(room.urlHinhAnh)}"
                                                 alt="Hình ảnh phòng ${fn:escapeXml(room.tenPhongChieu)}"
                                                 class="room-image image-zoom-btn"
                                                 style="max-width: 60px; max-height: 60px; cursor: pointer;"
                                                 onerror="this.src='${pageContext.request.contextPath}/resources/images/default-poster.jpg'; this.alt='Lỗi tải ảnh';"/>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-muted">Chưa có ảnh</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <%-- Corrected onclick escaping --%>
                                    <button class="custom-btn btn-sm view-seat-map" data-room-id="${room.maPhongChieu}" onclick="showSeatMap('${room.maPhongChieu}')">Xem</button>
                                </td>
                                <td>
                                    <%-- Pass maRapChieu to showEditModal --%>
                                    <button class="custom-btn btn-sm mr-1" onclick="showEditModal('${room.maPhongChieu}', '${fn:escapeXml(room.tenPhongChieu)}', ${room.sucChua}, '${room.maRapChieu}', '${fn:escapeXml(room.urlHinhAnh)}')">Sửa</button>
                                    <%-- Use POST for delete operation --%>
                                    <form action="${pageContext.request.contextPath}/admin/theater-rooms/delete/${room.maPhongChieu}" method="post" style="display: inline;">
                                        <button type="submit" class="custom-btn btn-sm" onclick="return confirm('Bạn có chắc muốn xóa phòng ${fn:escapeXml(room.tenPhongChieu)} (${room.maPhongChieu}) không?')">Xóa</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>

    <!-- Modal Thêm Phòng Chiếu -->
    <div class="modal" id="addModal" style="display: none;">
        <div class="modal-content">
            <h3>Thêm Phòng Chiếu Mới</h3>
            <form id="addRoomForm" action="${pageContext.request.contextPath}/admin/theater-rooms/add" method="POST" enctype="multipart/form-data">
                <div class="row">
                    <div class="col-md-6">
                        <div class="form-group">
                            <label for="addMaPhong">Mã Phòng</label>
                            <input type="text" class="form-control" id="addMaPhong" name="maPhongChieu" value="${newMaPhongChieu}" readonly>
                        </div>
                        <div class="form-group">
                            <label for="addTenPhong">Tên Phòng</label>
                            <input type="text" class="form-control" id="addTenPhong" name="tenPhongChieu" placeholder="VD: Phòng 3" required>
                        </div>
                        <div class="form-group">
                            <label for="addSucChua">Sức Chứa</label>
                            <input type="number" class="form-control" id="addSucChua" name="sucChua" min="1" placeholder="VD: 100" required>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="form-group">
                            <label for="addMaRap">Rạp Chiếu</label>
                            <select class="form-control" id="addMaRap" name="maRapChieu" required>
                                <option value="">-- Chọn Rạp --</option>
                                <c:forEach var="rap" items="${rapChieuList}">
                                    <%-- Use maRapChieu for value, tenRapChieu for display --%>
                                    <option value="${rap.maRapChieu}">${rap.tenRapChieu} (${rap.maRapChieu})</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="addHinhAnh">Hình Ảnh</label>
                            <input type="file" class="form-control" id="addHinhAnh" name="hinhAnh" accept="image/jpeg,image/png" onchange="validateFile(this)">
                            <small class="form-text text-muted">Chọn file hình ảnh (jpg, png, tối đa 5MB).</small>
                        </div>
                         <div class="form-group">
                            <label>Sơ đồ Ghế</label>
                            <button type="button" class="custom-btn" id="addSeatMapBtn">Thiết lập sơ đồ ghế</button>
                            <input type="hidden" id="addSeatData" name="seatData">
                        </div>
                    </div>
                </div>
                <div class="modal-actions">
                    <button type="submit" class="custom-btn">Thêm</button>
                    <button type="button" class="custom-btn" onclick="closeAddModal()">Hủy</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Modal Sửa Thông Tin Phòng Chiếu -->
    <div class="modal" id="editModal" style="display: none;">
        <div class="modal-content">
            <h3>Sửa Thông Tin Phòng Chiếu</h3>
            <form id="editRoomForm" action="${pageContext.request.contextPath}/admin/theater-rooms/update" method="POST" enctype="multipart/form-data">
                 <div class="row">
                    <div class="col-md-6">
                        <div class="form-group">
                            <label for="editMaPhong">Mã Phòng</label>
                            <input type="text" id="editMaPhong" name="maPhongChieu" class="form-control" readonly>
                        </div>
                        <div class="form-group">
                            <label for="editTenPhong">Tên Phòng</label>
                            <input type="text" id="editTenPhong" name="tenPhongChieu" class="form-control" required>
                        </div>
                        <div class="form-group">
                            <label for="editSucChua">Sức Chứa</label>
                            <input type="number" id="editSucChua" name="sucChua" class="form-control" min="1" required>
                        </div>
                        <div class="form-group">
                            <label for="editMaRap">Rạp Chiếu</label>
                            <select id="editMaRap" name="maRapChieu" class="form-control" required>
                                 <option value="">-- Chọn Rạp --</option>
                                <c:forEach var="rap" items="${rapChieuList}">
                                     <%-- Use maRapChieu for value, tenRapChieu for display --%>
                                    <option value="${rap.maRapChieu}">${rap.tenRapChieu} (${rap.maRapChieu})</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="form-group">
                            <label for="editHinhAnh">Hình Ảnh</label>
                            <div class="image-view mb-2">
                                <img id="editHinhAnhPreview" src="#" alt="Hình ảnh hiện tại" style="max-width: 100px; max-height: 100px; display: none;">
                                <span id="noImageText" class="text-muted" style="display: none;">Chưa có hình ảnh.</span>
                            </div>
                            <div class="image-edit">
                                <input type="file" class="form-control" id="editHinhAnh" name="hinhAnh" accept="image/jpeg,image/png" onchange="validateFile(this)">
                                <small class="form-text text-muted">Chọn file hình ảnh mới (jpg, png, tối đa 5MB). Để trống để giữ hình ảnh hiện tại.</small>
                            </div>
                        </div>
                         <div class="form-group">
                            <label>Chỉnh sửa sơ đồ ghế</label>
                            <button type="button" class="custom-btn" id="editSeatMapBtn">Chỉnh sửa</button>
                        </div>
                    </div>
                </div>
                <div class="modal-actions">
                    <button type="submit" class="custom-btn">Lưu</button>
                    <button type="button" class="custom-btn" onclick="closeEditModal()">Hủy</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Modal Xem Sơ đồ Ghế -->
    <div class="modal" id="seatMapModal" style="display: none;">
        <div class="modal-content large-modal">
            <h3>Sơ đồ Ghế Phòng: <span id="seatMapRoomName"></span></h3>
            <div class="seat-legend mb-3">
                <!-- Legend will be populated dynamically -->
            </div>
            <div class="seat-grid-container">
                <div class="screen">MÀN HÌNH</div>
                <div class="seat-grid" id="modalSeatGrid"></div>
            </div>
            <button class="custom-btn mt-2" onclick="closeSeatMapModal()">Đóng</button>
        </div>
    </div>

    <!-- Modal Thiết lập/Chỉnh sửa Sơ đồ Ghế -->
    <div class="modal" id="editSeatMapModal" style="display: none;">
        <div class="modal-content large-modal">
            <h3 id="seatMapModalTitle">Chỉnh sửa sơ đồ ghế</h3>
            <div class="row">
                <div class="col-md-4">
                     <div class="form-group">
                        <label>Chọn Loại Ghế để vẽ</label>
                        <select class="form-control mb-2" id="loaiGheEdit" name="loaiGhe">
                            <!-- Được điền động qua AJAX -->
                        </select>
                    </div>
                    <div id="capacityInfo" class="mb-2 alert alert-info"></div>
                    <button type="button" class="custom-btn btn-danger mt-2" id="resetGridBtn">Xóa Toàn Bộ Ghế</button>
                </div>
                <div class="col-md-8">
                    <div class="seat-grid-container">
                        <div class="screen">MÀN HÌNH</div>
                        <div class="seat-grid" id="editSeatGrid"></div>
                    </div>
                </div>
            </div>
            <div class="modal-actions">
                <button class="custom-btn mt-2" id="saveSeatMapBtn">Lưu Sơ Đồ</button>
                <button type="button" class="custom-btn mt-2" onclick="closeEditSeatMapModal()">Đóng</button>
            </div>
        </div>
    </div>

    <!-- Modal Quản Lý Loại Ghế -->
    <div class="modal" id="manageSeatTypesModal" style="display: none;">
        <div class="modal-content large-modal">
            <h3>Quản Lý Loại Ghế</h3>
            <div class="form-group seat-type-management">
                <label>Thêm Loại Ghế Mới</label>
                <form id="addSeatTypeForm" action="${pageContext.request.contextPath}/admin/theater-rooms/seat-types/add" method="POST">
                    <div class="input-group mb-2">
                        <input type="text" class="form-control" id="newSeatTypeId" name="maLoaiGhe" value="${newMaLoaiGhe}" readonly>
                        <input type="text" class="form-control" name="tenLoaiGhe" placeholder="Tên loại ghế" required>
                        <input type="number" step="0.1" class="form-control" name="heSoGia" placeholder="Hệ số giá" min="0" required>
                        <input type="color" class="form-control form-control-color" name="mauGhe" value="#FFD700" title="Chọn màu ghế">
                        <input type="number" class="form-control" name="soCho" placeholder="Số chỗ" min="1" value="1" required>
                        <button type="submit" class="custom-btn">Thêm</button>
                    </div>
                </form>
                <div class="table-responsive">
                    <table class="table table-bordered table-striped" id="seatTypeTable">
                        <thead>
                            <tr>
                                <th>Mã Loại Ghế</th>
                                <th>Tên Loại Ghế</th>
                                <th>Hệ Số Giá</th>
                                <th>Màu</th>
                                <th>Số Chỗ</th>
                                <th>Hành Động</th>
                            </tr>
                        </thead>
                        <tbody id="seatTypeList">
                            <c:choose>
                                <c:when test="${empty seatTypeList}">
                                    <tr>
                                        <td colspan="6" class="no-data text-center">Không có loại ghế nào</td>
                                    </tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach var="seatType" items="${seatTypeList}">
                                        <tr data-id="${seatType.maLoaiGhe}">
                                            <td>${seatType.maLoaiGhe}</td>
                                            <td class="seat-type-name">${fn:escapeXml(seatType.tenLoaiGhe)}</td>
                                            <td><fmt:formatNumber value="${seatType.heSoGia}" type="number" minFractionDigits="1" maxFractionDigits="2"/></td>
                                            <td class="seat-type-color">
                                                <span class="color-preview" style="background-color: ${seatType.mauGhe};"></span>
                                                ${seatType.mauGhe}
                                            </td>
                                            <td>${seatType.soCho}</td>
                                            <td>
                                                 <%-- Corrected onclick escaping --%>
                                                <button class="custom-btn btn-sm mr-1" onclick="editSeatType('${seatType.maLoaiGhe}', '${fn:escapeXml(seatType.tenLoaiGhe)}', ${seatType.heSoGia}, '${seatType.mauGhe}', ${seatType.soCho})">Sửa</button>
                                                <form action="${pageContext.request.contextPath}/admin/theater-rooms/seat-types/delete/${seatType.maLoaiGhe}" method="POST" style="display: inline;">
                                                    <button type="submit" class="custom-btn btn-sm" onclick="return confirm('Bạn có chắc muốn xóa loại ghế ${fn:escapeXml(seatType.tenLoaiGhe)} (${seatType.maLoaiGhe}) không? Loại ghế này sẽ bị xóa khỏi tất cả sơ đồ ghế!')">Xóa</button>
                                                </form>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>
            <div class="modal-actions">
                <button class="custom-btn" onclick="closeManageSeatTypesModal()">Đóng</button>
            </div>
        </div>
    </div>

    <!-- Modal Chỉnh Sửa Loại Ghế -->
    <div class="modal" id="editSeatTypeModal" style="display: none;">
        <div class="modal-content">
            <h3>Chỉnh Sửa Loại Ghế</h3>
            <form id="editSeatTypeForm" action="${pageContext.request.contextPath}/admin/theater-rooms/seat-types/update" method="POST">
                <div class="form-group">
                    <label for="editSeatTypeId">Mã Loại Ghế</label>
                    <input type="text" class="form-control" id="editSeatTypeId" name="maLoaiGhe" readonly>
                </div>
                <div class="form-group">
                    <label for="editSeatTypeName">Tên Loại Ghế</label>
                    <input type="text" class="form-control" id="editSeatTypeName" name="tenLoaiGhe" required>
                </div>
                <div class="form-group">
                    <label for="editSeatTypePrice">Hệ Số Giá</label>
                    <input type="number" step="0.1" class="form-control" id="editSeatTypePrice" name="heSoGia" required min="0">
                </div>
                <div class="form-group">
                    <label for="editSeatTypeColor">Màu Ghế</label>
                    <input type="color" class="form-control form-control-color" id="editSeatTypeColor" name="mauGhe" required>
                </div>
                <div class="form-group">
                    <label for="editSeatTypeCapacity">Số Chỗ</label>
                    <input type="number" class="form-control" id="editSeatTypeCapacity" name="soCho" required min="1">
                </div>
                <div class="modal-actions">
                    <button type="submit" class="custom-btn">Lưu</button>
                    <button type="button" class="custom-btn" onclick="closeEditSeatTypeModal()">Hủy</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Modal hiển thị hình ảnh phóng to -->
    <div class="modal" id="imageZoomModal" style="display: none;">
        <div class="modal-content image-zoom-content">
            <span class="close-btn" onclick="closeImageZoomModal()">&times;</span>
            <img id="zoomedImage" src="" alt="Hình ảnh phóng to">
        </div>
    </div>

</div> <%-- End room-list --%>

<script>
    var contextPath = '${pageContext.request.contextPath}';
    var defaultRoomImage = contextPath + '/resources/images/default-room.jpg';
    var seatTypesMap = {}; // To store seat type details
    var editSeatGridManager = null; // Instance for the editable grid
    var tempSeatData = []; // Temporary storage for seats in add mode
    var currentRoomId = null; // Track which room is being edited or viewed
</script>
<script src="${pageContext.request.contextPath}/resources/admin/js/seatGrid.js?v=1.3"></script>
<script src="${pageContext.request.contextPath}/resources/admin/js/imageZoom.js?v=1.1"></script>

<script>
// Function to validate image file size and type
function validateFile(input) {
    const file = input.files[0];
    if (!file) return; // No file selected

    const allowedTypes = ["image/jpeg", "image/png"];
    if (!allowedTypes.includes(file.type)) {
        alert("Hình ảnh phải là file jpg hoặc png.");
        input.value = ""; // Clear the input
        return;
    }

    const maxSize = 5 * 1024 * 1024; // 5MB
    if (file.size > maxSize) {
        alert("Kích thước hình ảnh không được vượt quá 5MB.");
        input.value = ""; // Clear the input
        return;
    }

    // Optional: Preview image for edit modal
    if (input.id === 'editHinhAnh') {
        const reader = new FileReader();
        reader.onload = function(e) {
            const preview = document.getElementById('editHinhAnhPreview');
            preview.src = e.target.result;
            preview.style.display = 'block';
            document.getElementById('noImageText').style.display = 'none';
        }
        reader.readAsDataURL(file);
    }
}

// Xử lý filter theo rạp chiếu
document.getElementById('filterMaRap').addEventListener('change', function () {
    const selectedRap = this.value;
    const rows = document.querySelectorAll('#roomList tr');
    rows.forEach(row => {
        const maRap = row.dataset.rapId; // Use data attribute for filtering by code
        row.style.display = (selectedRap === '' || maRap === selectedRap) ? '' : 'none';
    });
});

// Hiển thị modal thêm phòng chiếu
function showAddModal() {
    // Reset form fields
    document.getElementById('addRoomForm').reset();
    document.getElementById('addMaPhong').value = '${newMaPhongChieu}'; // Set new ID again after reset
    window.tempSeatData = []; // Reset sơ đồ ghế tạm
    document.getElementById('addSeatData').value = '';
    document.getElementById('addModal').style.display = 'flex';
}

// Đóng modal thêm phòng chiếu
function closeAddModal() {
    document.getElementById('addModal').style.display = 'none';
}

// Hiển thị modal sửa phòng chiếu
function showEditModal(maPhong, tenPhong, sucChua, maRap, hinhAnhUrl) {
    document.getElementById('editMaPhong').value = maPhong;
    document.getElementById('editTenPhong').value = tenPhong;
    document.getElementById('editSucChua').value = sucChua;
    document.getElementById('editMaRap').value = maRap; // Set the select value using maRap
    document.getElementById('editHinhAnh').value = ''; // Clear file input
    window.currentRoomId = maPhong;

    // Set image preview
    const preview = document.getElementById('editHinhAnhPreview');
    const noImageText = document.getElementById('noImageText');
    if (hinhAnhUrl && hinhAnhUrl !== 'null' && hinhAnhUrl !== '') {
        preview.src = contextPath + '/' + hinhAnhUrl + '?t=' + new Date().getTime(); // Add timestamp to prevent caching
        preview.style.display = 'block';
        noImageText.style.display = 'none';
    } else {
        preview.style.display = 'none';
        noImageText.style.display = 'block';
    }

    document.getElementById('editModal').style.display = 'flex';

    // Setup edit seat map button
    document.getElementById('editSeatMapBtn').onclick = function () {
        const currentSucChua = parseInt(document.getElementById('editSucChua').value) || null;
         if (!currentSucChua) {
            alert('Vui lòng nhập sức chứa trước khi chỉnh sửa sơ đồ ghế!');
            return;
        }
        editSeatMap(maPhong, false, currentSucChua);
    };
}

// Đóng modal sửa phòng chiếu
function closeEditModal() {
    document.getElementById('editModal').style.display = 'none';
}

// Hiển thị sơ đồ ghế
function showSeatMap(roomId) {
    const roomRow = document.querySelector(`tr[data-room-id='${roomId}']`);
    const roomName = roomRow ? roomRow.querySelector('td[data-field="tenPhong"]').textContent : roomId;
    document.getElementById('seatMapRoomName').textContent = roomName;

    Promise.all([
        fetch(`${contextPath}/admin/theater-rooms/seats/${roomId}`),
        fetch(`${contextPath}/admin/theater-rooms/seat-types/list`)
    ])
    .then(async ([seatsResponse, typesResponse]) => {
        if (!seatsResponse.ok) throw new Error(`Lỗi khi lấy sơ đồ ghế: ${seatsResponse.status}`);
        if (!typesResponse.ok) throw new Error(`Lỗi khi lấy loại ghế: ${typesResponse.status}`);

        const seats = await seatsResponse.json();
        const seatTypes = await typesResponse.json();

        console.log('Seats data:', seats);
        console.log('Seat Types data:', seatTypes);

        // Populate legend
        const legendContainer = document.querySelector('#seatMapModal .seat-legend');
        legendContainer.innerHTML = 'Chú thích: ';
        seatTypes.forEach(type => {
            const legendItem = document.createElement('span');
            legendItem.style.marginLeft = '15px';
            // Use textContent for safety against XSS
            const typeNameElement = document.createElement('span');
            typeNameElement.textContent = type.tenLoaiGhe;
            legendItem.innerHTML = `<span class="seat-example" style="background-color: ${type.mauGhe}; width: 15px; height: 15px; display: inline-block; margin-right: 5px; border: 1px solid #ccc;"></span> `;
            legendItem.appendChild(typeNameElement);
            legendContainer.appendChild(legendItem);
        });

        const mappedSeats = seats.map(seat => {
            if (!seat.tenHangAdmin || !seat.soGheAdmin || !seat.maLoaiGhe) {
                console.warn('Invalid seat data:', seat);
                return null;
            }
            return {
                row: seat.tenHangAdmin,
                col: parseInt(seat.soGheAdmin),
                type: seat.maLoaiGhe,
                color: seat.mauGhe || '#f0f0f0',
                tenHang: seat.tenHang,
                soGhe: seat.soGhe,
                maGhe: seat.maGhe
            };
        }).filter(seat => seat !== null);

        console.log('Mapped seats for display:', mappedSeats);

        // Initialize read-only seat grid
        const seatGridManager = initSeatGrid('modalSeatGrid', false, mappedSeats, null, seatTypes);
        document.getElementById('seatMapModal').style.display = 'flex';
    })
    .catch(error => {
        console.error('Lỗi khi tải sơ đồ ghế hoặc loại ghế:', error);
        alert('Không thể tải sơ đồ ghế. Vui lòng thử lại. Lỗi: ' + error.message);
    });
}

// Đóng modal sơ đồ ghế
function closeSeatMapModal() {
    document.getElementById('seatMapModal').style.display = 'none';
    document.getElementById('modalSeatGrid').innerHTML = ''; // Clear grid
}

// Hiển thị modal quản lý loại ghế
function showManageSeatTypesModal() {
    document.getElementById('manageSeatTypesModal').style.display = 'flex';
}

// Đóng modal quản lý loại ghế
function closeManageSeatTypesModal() {
    document.getElementById('manageSeatTypesModal').style.display = 'none';
}

// Chỉnh sửa loại ghế (populate form)
function editSeatType(maLoaiGhe, tenLoaiGhe, heSoGia, mauGhe, soCho) {
    if (!maLoaiGhe) {
        console.error('maLoaiGhe rỗng hoặc không hợp lệ');
        alert('Mã loại ghế không hợp lệ. Vui lòng thử lại.');
        return;
    }
    document.getElementById('editSeatTypeId').value = maLoaiGhe;
    document.getElementById('editSeatTypeName').value = tenLoaiGhe;
    document.getElementById('editSeatTypePrice').value = heSoGia;
    document.getElementById('editSeatTypeColor').value = mauGhe;
    document.getElementById('editSeatTypeCapacity').value = soCho;
    document.getElementById('editSeatTypeModal').style.display = 'flex';
}

// Đóng modal chỉnh sửa loại ghế
function closeEditSeatTypeModal() {
    document.getElementById('editSeatTypeModal').style.display = 'none';
}

// Thiết lập/chỉnh sửa sơ đồ ghế (main function)
async function editSeatMap(roomId, isAddMode = false, maxCapacity = null) {
    window.currentRoomId = roomId;
    const modal = document.getElementById('editSeatMapModal');
    document.getElementById('seatMapModalTitle').textContent = isAddMode ? 'Thiết lập sơ đồ ghế' : `Chỉnh sửa sơ đồ ghế (${roomId})`;

    try {
        // Fetch seat types
        const typesResponse = await fetch(`${contextPath}/admin/theater-rooms/seat-types/list`);
        if (!typesResponse.ok) throw new Error('Lỗi khi lấy danh sách loại ghế');
        const seatTypes = await typesResponse.json();
        window.seatTypesMap = seatTypes.reduce((map, type) => {
            map[type.maLoaiGhe] = type;
            return map;
        }, {});

        const select = document.getElementById('loaiGheEdit');
        select.innerHTML = '';
        seatTypes.forEach(type => {
            const option = document.createElement('option');
            option.value = type.maLoaiGhe;
            option.dataset.color = type.mauGhe;
            // Use textContent for safety
            option.textContent = `${type.tenLoaiGhe} (${type.maLoaiGhe})`;
            select.appendChild(option);
        });

        let initialSeats = [];
        if (!isAddMode) {
            // Fetch existing seats for editing
            const seatsResponse = await fetch(`${contextPath}/admin/theater-rooms/seats/${roomId}`);
            if (!seatsResponse.ok) throw new Error('Lỗi khi lấy sơ đồ ghế hiện tại');
            const seats = await seatsResponse.json();
            console.log('Seats data for edit:', seats);
            initialSeats = seats.map(seat => {
                 if (!seat.tenHangAdmin || !seat.soGheAdmin || !seat.maLoaiGhe) return null;
                 return {
                    row: seat.tenHangAdmin,
                    col: parseInt(seat.soGheAdmin),
                    type: seat.maLoaiGhe,
                    color: seat.mauGhe || '#f0f0f0'
                 };
            }).filter(seat => seat !== null);
            console.log('Mapped initial seats for edit:', initialSeats);
        } else {
            // Use temporary data if available
            initialSeats = window.tempSeatData || [];
        }

        // Initialize editable seat grid
        window.editSeatGridManager = initSeatGrid('editSeatGrid', true, initialSeats, maxCapacity, seatTypes);
        modal.style.display = 'flex';

    } catch (error) {
        console.error('Lỗi khi chuẩn bị chỉnh sửa sơ đồ ghế:', error);
        alert('Không thể tải dữ liệu cần thiết để chỉnh sửa sơ đồ ghế. Lỗi: ' + error.message);
    }
}

// Xử lý nút thiết lập sơ đồ ghế trong modal thêm
document.getElementById('addSeatMapBtn').addEventListener('click', () => {
    const maPhong = document.getElementById('addMaPhong').value;
    const sucChua = parseInt(document.getElementById('addSucChua').value) || null;
    if (!maPhong) {
        alert('Vui lòng đảm bảo mã phòng được tạo trước khi thiết lập sơ đồ ghế!');
        return;
    }
    if (!sucChua) {
        alert('Vui lòng nhập sức chứa trước khi thiết lập sơ đồ ghế!');
        return;
    }
    editSeatMap(maPhong, true, sucChua);
});

// Xử lý nút "Xóa Toàn Bộ Ghế"
document.getElementById('resetGridBtn').addEventListener('click', () => {
    if (window.editSeatGridManager) {
        if (confirm('Bạn có chắc muốn xóa toàn bộ ghế khỏi sơ đồ này không?')) {
            window.editSeatGridManager.resetGrid();
            console.log('Grid reset');
        }
    }
});

// Xử lý nút Lưu Sơ Đồ
document.getElementById('saveSeatMapBtn').addEventListener('click', async () => {
    if (!window.editSeatGridManager) return;
    try {
        const seatDataResult = window.editSeatGridManager.getSeatData();
        const seatsToSave = seatDataResult.seats;
        const totalCapacity = seatDataResult.totalCapacity;
        console.log('Saving seats:', seatsToSave);
        console.log('Total capacity from grid:', totalCapacity);

        // Get room's declared capacity
        const isAddMode = document.getElementById('seatMapModalTitle').textContent.startsWith('Thiết lập');
        const capacityInputId = isAddMode ? 'addSucChua' : 'editSucChua';
        const roomCapacity = parseInt(document.getElementById(capacityInputId).value) || 0;

        // Validate capacity
        if (totalCapacity > roomCapacity) {
            alert(`Tổng số chỗ ngồi (${totalCapacity}) vượt quá sức chứa đã khai báo của phòng (${roomCapacity}). Vui lòng điều chỉnh sơ đồ hoặc cập nhật sức chứa của phòng.`);
            return;
        }

        // Map seat data for backend
        const backendSeatData = seatsToSave.map(seat => ({
            tenHangAdmin: seat.row,
            soGheAdmin: seat.col,
            type: seat.type
        }));
        console.log('Data being sent to backend:', backendSeatData);

        if (isAddMode) {
            // Save temporarily for the add form
            window.tempSeatData = backendSeatData;
            document.getElementById('addSeatData').value = JSON.stringify(backendSeatData);
            alert('Sơ đồ ghế đã được lưu tạm thời cho phòng mới!');
            closeEditSeatMapModal();
        } else {
            // Save directly via API for existing room
            const response = await fetch(`${contextPath}/admin/theater-rooms/seats/save?maPhongChieu=${window.currentRoomId}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(backendSeatData)
            });

            const result = await response.json();

            if (!response.ok || !result.success) {
                throw new Error(result.message || `HTTP error! status: ${response.status}`);
            }

            alert(result.message || 'Sơ đồ ghế đã được lưu thành công!');
            closeEditSeatMapModal();
            // Optionally refresh
            // location.reload();
        }

    } catch (error) {
        alert(`Lỗi khi lưu sơ đồ ghế: ${error.message}`);
        console.error('Lỗi khi lưu sơ đồ ghế:', error);
    }
});

// Đóng modal chỉnh sửa sơ đồ ghế
function closeEditSeatMapModal() {
    document.getElementById('editSeatMapModal').style.display = 'none';
    document.getElementById('editSeatGrid').innerHTML = ''; // Clear grid
    window.editSeatGridManager = null; // Clean up manager instance
}

// Handle add form submission - ensure seat data is included if set
document.getElementById('addRoomForm').addEventListener('submit', function(event) {
    const seatDataInput = document.getElementById('addSeatData');
    if (window.tempSeatData && window.tempSeatData.length > 0) {
        seatDataInput.value = JSON.stringify(window.tempSeatData);
        console.log('Submitting add form with seat data:', seatDataInput.value);
    } else {
        seatDataInput.value = '[]';
         console.log('Submitting add form without seat data.');
    }
});

// Initial setup on page load
document.addEventListener('DOMContentLoaded', () => {
    // Add event listeners for image zoom buttons
    document.getElementById('roomList').addEventListener('click', function(event) {
        if (event.target.classList.contains('image-zoom-btn') || event.target.closest('.image-zoom-btn')) {
            const imgElement = event.target.tagName === 'IMG' ? event.target : event.target.querySelector('img');
            if (imgElement && imgElement.src && !imgElement.src.endsWith('default-room.jpg')) {
                showImageZoomModal(imgElement.src);
            }
        }
    });

    // Auto-dismiss success/error messages
    setTimeout(() => {
        document.querySelectorAll('.alert-success, .alert-danger').forEach(alert => {
            alert.style.transition = 'opacity 0.5s ease';
            alert.style.opacity = '0';
            setTimeout(() => alert.remove(), 500);
        });
    }, 5000);
});

</script>

