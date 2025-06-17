<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div class="header">
    <h2>Danh Sách Phòng Chiếu</h2>
    <div class="add-btn-container">
        <button class="custom-btn" onclick="showAddModal()">Thêm Phòng Chiếu</button>
        <button class="custom-btn" id="manageSeatTypesBtn" onclick="showManageSeatTypesModal()">Quản Lý Loại Ghế</button>
    </div>
</div>
<!-- Phần thông báo lỗi/thành công -->
<c:if test="${not empty notificationMessage}">
    <div class="alert ${notificationType == 'success' ? 'alert-success' : 'alert-danger'}" role="alert" style="margin-top: 10px;">
        ${fn:escapeXml(notificationMessage)}
    </div>
</c:if>
<!-- Combo box filter -->
<div class="filter-section mb-3">
    <div class="form-group">
        <label for="filterMaRap">Lọc Theo Rạp Chiếu</label>
         <select class="form-control" id="filterMaRap">
            <option value="" ${empty param.maRapChieu ? 'selected' : ''}>Tất cả</option>
            <c:forEach var="rap" items="${rapChieuList}">
                <option value="${rap.maRapChieu}" ${param.maRapChieu == rap.maRapChieu ? 'selected' : ''}>
                    ${rap.tenRapChieu}</option>
            </c:forEach>
        </select>
    </div>
</div>
<div class="table-responsive">
    <table class="table table-bordered table-striped" id="roomTable">
        <thead>
            <tr>
                <th>Mã Phòng</th>
                <th>Tên Phòng</th>
                <th>Sức Chứa</th>
                <th>Rạp Chiếu</th>
                <th>Hình Ảnh</th>
                <th>Sơ đồ Ghế</th>
                <th>Hành Động</th>
            </tr>
        </thead>
        <tbody id="roomList">
            <c:choose>
                <c:when test="${empty roomList}">
                    <tr>
                        <td colspan="7" class="no-data">Không có phòng chiếu nào</td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <c:forEach var="room" items="${roomList}">
                        <tr data-room-id="${room.maPhongChieu}">
                            <td data-field="maPhong">${room.maPhongChieu}</td>
                            <td data-field="tenPhong">${room.tenPhongChieu}</td>
                            <td data-field="sucChua">${room.sucChua}</td>
                            <td data-field="maRap">${room.tenRapChieu}</td>
                            <td data-field="hinhAnh">
                                <button class="image-zoom-btn">
                                    <img src="${not empty room.urlHinhAnh ? pageContext.request.contextPath.concat('/resources/images/').concat(room.urlHinhAnh) : pageContext.request.contextPath.concat('/resources/images/default-room.jpg')}" alt="Hình ảnh phòng ${room.maPhongChieu}" class="room-image">
                                </button>
                            </td>
                            <td>
                                <button class="custom-btn btn-sm view-seat-map" data-room-id="${room.maPhongChieu}" onclick="showSeatMap('${room.maPhongChieu}')">Xem</button>
                            </td>
                            <td>
                                <button class="custom-btn btn-sm mr-1" onclick="showEditModal('${room.maPhongChieu}', '${fn:escapeXml(room.tenPhongChieu)}', ${room.sucChua}, '${room.maRapChieu}', '${not empty room.urlHinhAnh ? room.urlHinhAnh : ''}')">Sửa</button>
                                <a href="${pageContext.request.contextPath}/admin/theater-rooms/delete/${room.maPhongChieu}" class="custom-btn btn-sm" onclick="return confirm('Bạn có chắc muốn xóa phòng ${room.maPhongChieu} không?')">Xóa</a>
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
                        <input type="text" class="form-control" id="addMaPhong" name="maPhongChieu" value="${newMaPhongChieu}" readonly placeholder="Tự động tạo">
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="addTenPhong">Tên Phòng</label>
                        <input type="text" class="form-control" id="addTenPhong" name="tenPhongChieu" placeholder="VD: Phòng 3">
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="addSucChua">Sức Chứa</label>
                        <input type="number" class="form-control" id="addSucChua" name="sucChua" min="1" placeholder="VD: 100">
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="addMaRap">Rạp Chiếu</label>
                        <select class="form-control" id="addMaRap" name="maRapChieu">
                            <c:forEach var="rap" items="${rapChieuList}">
                                <option value="${rap.maRapChieu}">${rap.tenRapChieu}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-md-12">
                    <div class="form-group">
                        <label for="addHinhAnh">Hình Ảnh</label>
                        <input type="file" class="form-control" id="addHinhAnh" name="hinhAnh" accept="image/jpeg,image/png" onchange="validateFile(this)" required>
                        <small class="form-text text-muted">Chọn file hình ảnh (jpg, png, tối đa 5MB).</small>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-md-12">
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
        <form id="editRoomForm" action="${pageContext.request.contextPath}/admin/theater-rooms/update" method="post" enctype="multipart/form-data">
            <div class="detail-field">
                <label for="editMaPhong">Mã Phòng</label>
                <input type="text" id="editMaPhong" name="maPhongChieu" class="form-control" readonly>
            </div>
            <div class="detail-field">
                <label for="editTenPhong">Tên Phòng</label>
                <input type="text" id="editTenPhong" name="tenPhongChieu" class="form-control">
            </div>
            <div class="detail-field">
                <label for="editSucChua">Sức Chứa</label>
                <input type="number" id="editSucChua" name="sucChua" class="form-control">
            </div>
            <div class="detail-field">
                <label for="editMaRap">Rạp Chiếu</label>
                <select id="editMaRap" name="maRapChieu" class="form-control">
                    <c:forEach var="rap" items="${rapChieuList}">
                        <option value="${rap.maRapChieu}">${rap.tenRapChieu}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="detail-field">
                <label for="editHinhAnh">Hình Ảnh</label>
                <div class="hinhAnh-view">
                    <c:if test="${not empty room.urlHinhAnh}">
                        <c:set var="imagePath" value="${fn:replace(room.urlHinhAnh, 'resources/images/', '')}" />
                        <img id="editHinhAnhPreview" src="${pageContext.request.contextPath}/resources/images/${imagePath}" alt="Hình ảnh hiện tại" style="max-width: 100px; max-height: 100px;">
                    </c:if>
                    <c:if test="${empty room.urlHinhAnh}">
                        <p>Chưa có hình ảnh.</p>
                    </c:if>
                </div>
                <div class="hinhAnh-edit">
                    <input type="file" class="form-control" id="editHinhAnh" name="hinhAnh" accept="image/jpeg,image/png" onchange="validateFile(this)">
                    <small class="form-text text-muted">Chọn file hình ảnh mới (jpg, png, tối đa 5MB). Để trống để giữ hình ảnh hiện tại.</small>
                </div>
            </div>
            <div class="detail-field">
                <label>Chỉnh sửa sơ đồ ghế</label>
                <button type="button" class="custom-btn" id="editSeatMapBtn" onclick="editSeatMap(window.currentRoomId)">Chỉnh sửa</button>
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
    <div class="modal-content">
        <h3>Sơ đồ Ghế</h3>
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
        <div class="form-group">
            <label>Chọn Loại Ghế</label>
            <select class="form-control mb-2" id="loaiGheEdit" name="loaiGhe">
                <!-- Được điền động qua AJAX -->
            </select>
        </div>
        <div id="capacityInfo" class="mb-2"></div>
        <div class="seat-grid-container">
            <div class="screen">MÀN HÌNH</div>
            <div class="seat-grid" id="editSeatGrid"></div>
            <button type="button" class="custom-btn mt-2" id="resetGridBtn">Xóa Toàn Bộ Ghế</button>
        </div>
        <div class="modal-actions">
            <button class="custom-btn mt-2" id="saveSeatMapBtn">Lưu</button>
            <button type="button" class="custom-btn mt-2" onclick="closeEditSeatMapModal()">Đóng</button>
        </div>
    </div>
</div>

<!-- Modal Quản Lý Loại Ghế -->
<div class="modal" id="manageSeatTypesModal" style="display: none;">
    <div class="modal-content">
        <h3>Quản Lý Loại Ghế</h3>
        <div class="form-group seat-type-management">
            <label>Thêm Loại Ghế Mới</label>
            <form id="addSeatTypeForm" action="${pageContext.request.contextPath}/admin/theater-rooms/seat-types/add" method="POST">
                <div class="input-group mb-2">
                    <input type="text" class="form-control" id="newSeatTypeId" name="maLoaiGhe" value="${newMaLoaiGhe}" readonly>
                    <input type="text" class="form-control" id="newSeatTypeName" name="tenLoaiGhe" placeholder="Tên loại ghế" required>
                    <input type="number" step="0.1" class="form-control" id="newSeatTypePrice" name="heSoGia" placeholder="Hệ số giá" min="0" required>
                    <input type="color" class="form-control" id="newSeatTypeColor" name="mauGhe" value="#FFD700" required>
                    <input type="number" class="form-control" id="newSeatTypeCapacity" name="soCho" placeholder="Số chỗ. VD: 1" min="1" value="1">
                    <button type="submit" class="custom-btn">Thêm</button>
                </div>
            </form>
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
                                <td colspan="6" class="no-data">Không có loại ghế nào</td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="seatType" items="${seatTypeList}">
                                <tr data-id="${seatType.maLoaiGhe}">
                                    <td>${seatType.maLoaiGhe}</td>
                                    <td class="seat-type-name">${seatType.tenLoaiGhe}</td>
                                    <td>${seatType.heSoGia}</td>
                                    <td class="seat-type-color"><span class="color-preview" style="background-color: ${seatType.mauGhe};"></span> ${seatType.mauGhe}</td>
                                    <td>${seatType.soCho}</td>
                                    <td>
                                        <button class="custom-btn btn-sm mr-1" onclick="editSeatType('${seatType.maLoaiGhe}', '${fn:escapeXml(seatType.tenLoaiGhe)}', ${seatType.heSoGia}, '${seatType.mauGhe}', ${seatType.soCho})">Sửa</button>
                                        <form action="${pageContext.request.contextPath}/admin/theater-rooms/seat-types/delete/${seatType.maLoaiGhe}" method="POST" style="display: inline;">
                                            <button type="submit" class="custom-btn btn-sm" onclick="return confirm('Bạn có chắc muốn xóa loại ghế ${seatType.maLoaiGhe} không?')">Xóa</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
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
                <input type="number" step="0.1" class="form-control" id="editSeatTypePrice" name="heSoGia" required>
            </div>
            <div class="form-group">
                <label for="editSeatTypeColor">Màu Ghế</label>
                <input type="color" class="form-control" id="editSeatTypeColor" name="mauGhe" required>
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
        <img id="zoomedImage" src="" alt="Hình ảnh phóng to">
        <button class="custom-btn mt-2" onclick="closeImageZoomModal()">Đóng</button>
    </div>
</div>

<!-- Khu vực debug -->
<div class="debug-section" style="display: none; margin-top: 30px; padding: 15px; background: #f8f9fa; border-radius: 5px;">
    <h4>Debug Info</h4>
    <div id="debug-output" style="white-space: pre-wrap; font-family: monospace; background: #eee; padding: 10px;"></div>
    <button class="custom-btn mt-2" onclick="toggleDebug()">Toggle Debug</button>
</div>

<script>
    var contextPath = '${pageContext.request.contextPath}';
    console.log('Context Path:', contextPath);
</script>
<script src="${pageContext.request.contextPath}/resources/admin/js/seatGrid.js?v=1.3"></script>
<script src="${pageContext.request.contextPath}/resources/admin/js/imageZoom.js?v=1.0"></script>

<script>
// Xử lý debug
function toggleDebug() {
    const debugSection = document.querySelector('.debug-section');
    debugSection.style.display = debugSection.style.display === 'none' ? 'block' : 'none';
}

function debugOutput(message) {
    const output = document.getElementById('debug-output');
    if (output) {
        output.innerHTML += message + '\n';
    }
    console.log(message);
}

// Xử lý filter theo rạp chiếu
document.getElementById('filterMaRap').addEventListener('change', function () {
    const selectedMaRap = this.value;
    let url = '${pageContext.request.contextPath}/admin/theater-rooms';
    if (selectedMaRap) {
        url += '?maRapChieu=' + encodeURIComponent(selectedMaRap);
    }
    window.location.href = url;
});

// Hiển thị modal thêm phòng chiếu
function showAddModal() {
    document.getElementById('addModal').style.display = 'flex';
    window.tempSeatData = [];
}

// Đóng modal thêm phòng chiếu
function closeAddModal() {
    document.getElementById('addModal').style.display = 'none';
    document.getElementById('addTenPhong').value = '';
    document.getElementById('addSucChua').value = '';
    document.getElementById('addMaRap').selectedIndex = 0;
    document.getElementById('addHinhAnh').value = '';
    document.getElementById('addSeatData').value = '';
}

// Hiển thị modal sửa phòng chiếu
function showEditModal(maPhong, tenPhong, sucChua, maRap, hinhAnh) {
    console.log('Starting showEditModal with params:', { maPhong, tenPhong, sucChua, maRap, hinhAnh });
    const editModal = document.getElementById('editModal');
    if (!editModal) {
        console.error('editModal not found');
        alert('Lỗi: Không tìm thấy modal chỉnh sửa.');
        return;
    }
    window.currentRoomId = maPhong;
    document.getElementById('editMaPhong').value = maPhong;
    document.getElementById('editTenPhong').value = tenPhong;
    document.getElementById('editSucChua').value = sucChua;
    document.getElementById('editMaRap').value = maRap;
    document.getElementById('editHinhAnh').value = '';
    const hinhAnhView = document.querySelector('.hinhAnh-view');
    const hinhAnhP = hinhAnhView.querySelector('p');
    const hinhAnhImg = hinhAnhView.querySelector('img');
    if (hinhAnh && hinhAnh.trim() !== '') {
        hinhAnhP.textContent = 'Tên file: ' + hinhAnh;
        if (hinhAnhImg) {
            hinhAnhImg.src = contextPath + '/resources/images/' + hinhAnh;
            hinhAnhImg.style.display = 'block';
        }
    } else {
        hinhAnhP.textContent = 'Chưa có hình ảnh.';
        if (hinhAnhImg) {
            hinhAnhImg.style.display = 'none';
        }
    }
    console.log('Setting editModal display to flex');
    editModal.style.display = 'flex';
}

// Đóng modal sửa phòng chiếu
function closeEditModal() {
    document.getElementById('editModal').style.display = 'none';
}

// Hiển thị sơ đồ ghế
function showSeatMap(roomId) {
    fetch('${pageContext.request.contextPath}/admin/theater-rooms/seats/' + roomId)
        .then(response => {
            if (!response.ok) throw new Error('Lỗi khi lấy sơ đồ ghế: ' + response.status);
            return response.json();
        })
        .then(seats => {
            console.log('Seats data:', seats);
            const mappedSeats = seats.map(seat => {
                if (!seat.tenHangAdmin || !seat.soGheAdmin || !seat.maLoaiGhe) {
                    console.warn('Invalid seat data:', seat);
                    return null;
                }
                const row = seat.tenHangAdmin.charCodeAt(0) - 65;
                const col = parseInt(seat.soGheAdmin) - 1;
                if (isNaN(row) || isNaN(col) || row < 0 || col < 0) {
                    console.warn('Invalid seat position for seat:', seat);
                    return null;
                }
                return {
                    row: row,
                    col: col,
                    type: seat.maLoaiGhe,
                    color: seat.mauGhe || '#f0f0f0',
                    tenHang: seat.tenHang,
                    soGhe: seat.soGhe
                };
            }).filter(seat => seat !== null);
            console.log('Mapped seats:', mappedSeats);
            const seatGridManager = initSeatGrid('modalSeatGrid', false, mappedSeats);
            document.getElementById('seatMapModal').style.display = 'flex';
        })
        .catch(error => {
            console.error('Lỗi khi lấy sơ đồ ghế:', error);
            alert('Không thể tải sơ đồ ghế. Vui lòng thử lại.');
        });
}

// Đóng modal sơ đồ ghế
function closeSeatMapModal() {
    document.getElementById('seatMapModal').style.display = 'none';
}

// Hiển thị modal quản lý loại ghế
function showManageSeatTypesModal() {
    document.getElementById('manageSeatTypesModal').style.display = 'flex';
}

// Đóng modal quản lý loại ghế
function closeManageSeatTypesModal() {
    document.getElementById('manageSeatTypesModal').style.display = 'none';
}

// Chỉnh sửa loại ghế
function editSeatType(maLoaiGhe, tenLoaiGhe, heSoGia, mauGhe, soCho) {
    debugOutput('editSeatType called with:');
    debugOutput('maLoaiGhe: ' + maLoaiGhe);
    debugOutput('tenLoaiGhe: ' + tenLoaiGhe);
    debugOutput('heSoGia: ' + heSoGia);
    debugOutput('mauGhe: ' + mauGhe);
    debugOutput('soCho: ' + soCho);
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

// Thiết lập/chỉnh sửa sơ đồ ghế
function editSeatMap(roomId, isAddMode = false, maxCapacity = null) {
    console.log('editSeatMap called with:', { roomId, isAddMode, maxCapacity });
    window.currentRoomId = roomId;
    const modal = document.getElementById('editSeatMapModal');
    document.getElementById('seatMapModalTitle').textContent = isAddMode ? 'Thiết lập sơ đồ ghế' : 'Chỉnh sửa sơ đồ ghế';
    fetch('${pageContext.request.contextPath}/admin/theater-rooms/seat-types/list')
        .then(response => {
            if (!response.ok) throw new Error('Lỗi khi lấy danh sách loại ghế: ' + response.status);
            return response.json();
        })
        .then(seatTypes => {
            console.log('Seat types:', seatTypes);
            const select = document.getElementById('loaiGheEdit');
            select.innerHTML = '';
            if (seatTypes.length === 0) {
                select.innerHTML = '<option value="">Không có loại ghế</option>';
                alert('Vui lòng thêm loại ghế trước khi chỉnh sửa sơ đồ ghế.');
                return;
            }
            seatTypes.forEach(type => {
                const option = document.createElement('option');
                option.value = type.maLoaiGhe;
                option.dataset.color = type.mauGhe;
                option.textContent = type.tenLoaiGhe;
                select.appendChild(option);
            });
            if (!isAddMode) {
                fetch('${pageContext.request.contextPath}/admin/theater-rooms/seats/' + roomId)
                    .then(response => {
                        if (!response.ok) throw new Error('Lỗi khi lấy sơ đồ ghế: ' + response.status);
                        return response.json();
                    })
                    .then(seats => {
                        console.log('Seats data for edit:', seats);
                        const mappedSeats = seats.map(seat => {
                            if (!seat.tenHangAdmin || !seat.soGheAdmin || !seat.maLoaiGhe) {
                                console.warn('Invalid seat data:', seat);
                                return null;
                            }
                            const row = seat.tenHangAdmin.charCodeAt(0) - 65;
                            const col = parseInt(seat.soGheAdmin) - 1;
                            if (isNaN(row) || isNaN(col) || row < 0 || col < 0) {
                                console.warn('Invalid seat position for seat:', seat);
                                return null;
                            }
                            return {
                                row: row,
                                col: col,
                                type: seat.maLoaiGhe,
                                color: seat.mauGhe || '#f0f0f0',
                                tenHang: seat.tenHang,
                                soGhe: seat.soGhe
                            };
                        }).filter(seat => seat !== null);
                        console.log('Mapped seats for edit:', mappedSeats);
                        window.editSeatGridManager = initSeatGrid('editSeatGrid', true, mappedSeats, maxCapacity);
                        modal.style.display = 'flex';
                    })
                    .catch(error => {
                        console.error('Lỗi khi lấy sơ đồ ghế:', error);
                        alert('Không thể tải sơ đồ ghế: ' + error.message);
                    });
            } else {
                window.editSeatGridManager = initSeatGrid('editSeatGrid', true, window.tempSeatData, maxCapacity);
                modal.style.display = 'flex';
            }
        })
        .catch(error => {
            console.error('Lỗi khi lấy loại ghế:', error);
            alert('Không thể tải danh sách loại ghế: ' + error.message);
        });
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
        window.editSeatGridManager.resetGrid();
        console.log('Grid reset');
    }
});

document.getElementById('saveSeatMapBtn').addEventListener('click', () => {
    if (!window.editSeatGridManager || !window.currentRoomId) {
        alert('Lỗi: Không thể lưu sơ đồ ghế do thiếu thông tin phòng.');
        return;
    }
    const seatData = window.editSeatGridManager.getSeatData();
    console.log('Saving seats for maPhongChieu:', window.currentRoomId, seatData);
    fetch(contextPath + '/admin/theater-rooms/seats/save?maPhongChieu=' + window.currentRoomId, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(seatData.seats)
    }).then(response => {
        if (!response.ok) throw new Error('Lỗi: ' + response.status);
        return response.text();
    }).then(() => {
        alert('Lưu sơ đồ ghế thành công!');
        closeEditSeatMapModal();
        window.location.reload();
    }).catch(error => {
        console.error('Lỗi:', error);
        alert('Lỗi khi lưu sơ đồ ghế: ' + error.message);
    });
});

// Đóng modal chỉnh sửa sơ đồ ghế
function closeEditSeatMapModal() {
    document.getElementById('editSeatMapModal').style.display = 'none';
}

// Xử lý submit form thêm phòng chiếu
document.getElementById('addRoomForm').addEventListener('submit', function(event) {
    const maPhong = document.getElementById('addMaPhong').value;
    if (!maPhong) {
        event.preventDefault();
        alert('Mã phòng chiếu không được để trống. Vui lòng tải lại trang và thử lại.');
        return;
    }
    const seatDataInput = document.getElementById('addSeatData');
    if (window.tempSeatData && window.tempSeatData.length > 0) {
        seatDataInput.value = JSON.stringify(window.tempSeatData);
    } else {
        seatDataInput.value = '';
    }
});

// Tự động ẩn thông báo sau 5 giây
document.addEventListener('DOMContentLoaded', function () {
    const alert = document.querySelector('.alert');
    if (alert) {
        setTimeout(() => {
            alert.style.transition = 'opacity 0.5s';
            alert.style.opacity = '0';
            setTimeout(() => {
                alert.style.display = 'none';
            }, 500);
        }, 5000);
    }
});
</script>