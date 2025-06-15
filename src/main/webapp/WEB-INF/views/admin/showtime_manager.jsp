<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<div class="showtime-management">
	<div class="d-flex justify-content-between align-items-center mb-3">
		<h2>Quản Lý Suất Chiếu</h2>
		<div>
			<button id="toggleViewBtn" class="custom-btn mr-2"
				onclick="toggleShowtimeView()">
				<i
					class="fas ${viewMode == 'table' ? 'fa-calendar-alt' : 'fa-list'}"></i>
				${viewMode == 'table' ? 'Chuyển Sang Dạng Lịch' : 'Chuyển Sang Dạng Bảng'}
			</button>
			<button class="custom-btn add-showtime-btn" onclick="showAddModal()">
				<i class="fas fa-plus"></i> Thêm Suất
			</button>
		</div>
	</div>

	<!-- Error and Success Messages -->
	<c:if test="${not empty error}">
		<div class="alert alert-danger" id="errorMessage">${error}</div>
	</c:if>
	<c:if test="${not empty success}">
		<div class="alert alert-success" id="successMessage">${success}</div>
	</c:if>
	<c:if test="${not empty warning}">
		<div class="alert alert-warning" id="warningMessage">${warning}</div>
	</c:if>

	<!-- Filters -->
	<form id="filterForm" class="filter-section mb-4">
		<div class="form-group">
			<label for="filterSearch">Tìm kiếm phim</label> <input type="text"
				id="filterSearch" name="filterSearch" class="form-control"
				placeholder="Nhập tên phim..." oninput="applyFilterAndRender()">
		</div>
		<div class="form-group">
			<label for="filterStatus">Trạng thái</label> <select
				id="filterStatus" name="filterStatus" class="form-control">
				<option value="all">Tất cả</option>
				<option value="not_started">Chưa chiếu</option>
				<option value="playing">Đang chiếu</option>
				<option value="finished">Đã chiếu</option>
				<option value="has_surcharge">Có phụ thu</option>
			</select>
		</div>
		<div class="form-group">
			<label for="filterTime">Tìm theo ngày</label> <input type="date"
				id="filterTime" name="filterTime" class="form-control">
		</div>
		<div class="form-group">
			<label for="filterRap">Rạp Chiếu</label> <select id="filterRap"
				name="filterRap" class="form-control">
				<option value="all">Tất cả rạp</option>
				<c:forEach var="rap" items="${rapList}">
					<option value="${rap.maRapChieu}">${rap.tenRapChieu}</option>
				</c:forEach>
			</select>
		</div>
		<div class="form-group">
			<label for="filterPhong">Phòng Chiếu</label> <select id="filterPhong"
				name="filterPhong" class="form-control">
				<option value="all">Tất cả phòng</option>
				<c:forEach var="phong" items="${phongList}">
					<option value="${phong.maPhongChieu}"
						data-rap="${phong.maRapChieu}">${phong.tenPhongChieu}</option>
				</c:forEach>
			</select>
		</div>
	</form>

	<!-- Calendar View -->
	<div id="calendarView"
		style="display: ${viewMode == 'calendar' ? 'block' : 'none'};">
		<div class="calendar-navigation">
			<button type="button" class="custom-btn" onclick="previousWeek()">Tuần
				trước</button>
			<span id="weekRange"></span>
			<button type="button" class="custom-btn" onclick="nextWeek()">Tuần
				sau</button>
		</div>
		<div class="calendar-container">
			<div id="calendarMessage" class="calendar-message"
				style="display: none;"></div>
			<table class="calendar" id="calendarTable" style="display: none;">
				<thead>
					<tr id="calendarHeader">
						<th>Giờ</th>
					</tr>
				</thead>
				<tbody id="calendarBody"></tbody>
			</table>
		</div>
	</div>

	<!-- Table View -->
	<div id="tableView"
		style="display: ${viewMode == 'table' ? 'block' : 'none'};">
		<div class="table-responsive">
			<table class="table table-bordered table-striped table-hover">
				<thead>
					<tr>
						<th>Mã Suất</th>
						<th>Phim</th>
						<th>Rạp Chiếu</th>
						<th>Phòng Chiếu</th>
						<th>Bắt Đầu</th>
						<th>Kết Thúc</th>
						<th>Loại Màn</th>
						<th>Phụ Thu</th>
						<th>Hành Động</th>
					</tr>
				</thead>
				<tbody id="showtimeTableBody">
					<tr class="no-data-row" style="display: none;">
						<td colspan="9" class="text-center">Không có dữ liệu suất
							chiếu phù hợp.</td>
					</tr>
				</tbody>
			</table>
		</div>
		<!-- Client-Side Pagination -->
		<nav aria-label="Page navigation">
			<ul class="pagination justify-content-center"
				id="paginationContainer"></ul>
		</nav>
	</div>

	<!-- Add Modal -->
	<div class="modal" id="addModal" style="display: none;">
		<div class="modal-content">
			<form id="addShowtimeForm" method="POST"
				action="${pageContext.request.contextPath}/admin/showtimes/add">
				<input type="hidden" id="addViewHidden" name="view"
					value="${viewMode}">
				<h3>Thêm Suất Chiếu Mới</h3>
				<div class="detail-field">
					<label for="addPhim">Phim</label> <select id="addPhim"
						name="maPhim" class="form-control" onchange="updateAddThoiLuong()"
						required>
						<option value="" disabled selected>-- Chọn Phim --</option>
						<c:forEach var="phim" items="${phimList}">
							<option value="${phim.maPhim}" data-thoiluong="${phim.thoiLuong}">${phim.tenPhim}
								(${phim.thoiLuong} phút)</option>
						</c:forEach>
					</select>
				</div>
				<div class="detail-field">
					<label for="addThoiLuong">Thời Lượng (phút)</label> <input
						type="number" id="addThoiLuong" class="form-control" readonly>
				</div>
				<div class="detail-field">
					<label for="addRapChieu">Rạp Chiếu</label> <select id="addRapChieu"
						name="maRap" class="form-control" onchange="filterAddPhongChieu()"
						required>
						<option value="" disabled selected>-- Chọn Rạp --</option>
						<c:forEach var="rap" items="${rapList}">
							<option value="${rap.maRapChieu}">${rap.tenRapChieu}</option>
						</c:forEach>
					</select>
				</div>
				<div class="detail-field">
					<label for="addPhongChieu">Phòng Chiếu</label> <select
						id="addPhongChieu" name="maPhongChieu" class="form-control"
						required>
						<option value="" disabled selected>-- Chọn Phòng --</option>
						<c:forEach var="phong" items="${phongList}">
							<option value="${phong.maPhongChieu}"
								data-rap="${phong.maRapChieu}" style="display: none;">${phong.tenPhongChieu}</option>
						</c:forEach>
					</select>
				</div>
				<div class="detail-field">
					<label for="addLoaiManChieu">Loại Màn Chiếu</label> <select
						id="addLoaiManChieu" name="loaiManChieu" class="form-control">
						<option value="2D">2D</option>
						<option value="3D">3D</option>
						<option value="IMAX">IMAX</option>
					</select>
				</div>
				<div class="detail-field">
					<label for="addPhuThuSelect">Phụ Thu (nếu có)</label>
					<div class="tag-container" id="phuThuContainer"></div>
					<select id="addPhuThuSelect" class="form-control phu-thu-select"
						onchange="addPhuThuTag()">
						<option value="" disabled selected>-- Chọn Phụ Thu --</option>
						<c:forEach var="phuThu" items="${phuThuList}">
							<option value="${phuThu.maPhuThu}">${phuThu.maPhuThu}-
								${phuThu.tenPhuThu}</option>
						</c:forEach>
					</select> <input type="hidden" name="maPhuThu" id="phuThuHidden">
				</div>
				<hr>
				<label>Các Khung Giờ Chiếu:</label>
				<div id="addTimeSlotsContainer"></div>
				<button type="button" class="custom-btn btn-sm mt-2"
					onclick="addMoreTimeSlot()">
					<i class="fas fa-plus"></i> Thêm Khung Giờ
				</button>
				<hr>
				<div id="hiddenShowtimeInputsContainer"></div>
				<div class="modal-actions">
					<button type="button" class="custom-btn"
						onclick="prepareAndSubmitAddForm()">Thêm Suất Chiếu</button>
					<button type="button" class="custom-btn btn-secondary"
						onclick="closeAddModal()">Hủy</button>
				</div>
			</form>
		</div>
	</div>

	<!-- Edit Modal -->
	<div class="modal" id="editModal" style="display: none;">
		<div class="modal-content">
			<form id="editShowtimeForm" method="POST"
				action="${pageContext.request.contextPath}/admin/showtimes/update">
				<input type="hidden" id="editViewHidden" name="view"
					value="${viewMode}">
				<h3>Sửa Thông Tin Suất Chiếu</h3>
				<input type="hidden" id="editMaSuatHidden" name="maSuatChieu">
				<div class="detail-field">
					<label for="editMaSuatDisplay">Mã Suất</label> <input type="text"
						id="editMaSuatDisplay" class="form-control" readonly>
				</div>
				<div class="detail-field">
					<label for="editPhim">Phim</label> <select id="editPhim"
						name="maPhim" class="form-control"
						onchange="updateEditThoiLuongAndEndTime()" required>
						<option value="" disabled selected>-- Chọn Phim --</option>
						<c:forEach var="phim" items="${phimList}">
							<option value="${phim.maPhim}" data-thoiluong="${phim.thoiLuong}">${phim.tenPhim}</option>
						</c:forEach>
					</select>
				</div>
				<div class="detail-field">
					<label for="editThoiLuong">Thời Lượng (phút)</label> <input
						type="number" id="editThoiLuong" class="form-control" readonly>
				</div>
				<div class="detail-field">
					<label for="editRapChieu">Rạp Chiếu</label> <select
						id="editRapChieu" name="maRap" class="form-control"
						onchange="filterEditPhongChieu()" required>
						<option value="" disabled selected>-- Chọn Rạp --</option>
						<c:forEach var="rap" items="${rapList}">
							<option value="${rap.maRapChieu}">${rap.tenRapChieu}</option>
						</c:forEach>
					</select>
				</div>
				<div class="detail-field">
					<label for="editPhongChieu">Phòng Chiếu</label> <select
						id="editPhongChieu" name="maPhongChieu" class="form-control"
						required>
						<option value="" disabled selected>-- Chọn Phòng --</option>
						<c:forEach var="phong" items="${phongList}">
							<option value="${phong.maPhongChieu}"
								data-rap="${phong.maRapChieu}" style="display: none;">
								${phong.tenPhongChieu}</option>
						</c:forEach>
					</select>
				</div>
				<div class="detail-field">
					<label for="editNgayGioChieu">Ngày Giờ Chiếu</label> <input
						type="datetime-local" id="editNgayGioChieu" name="ngayGioChieu"
						class="form-control" onchange="updateEditEndTime()" required>
				</div>
				<div class="detail-field">
					<label for="editNgayGioKetThuc">Ngày Giờ Kết Thúc</label> <input
						type="datetime-local" id="editNgayGioKetThuc"
						name="ngayGioKetThuc" class="form-control" readonly>
				</div>
				<div class="detail-field">
					<label for="editLoaiManChieu">Loại Màn Chiếu</label> <select
						id="editLoaiManChieu" name="loaiManChieu" class="form-control">
						<option value="2D">2D</option>
						<option value="3D">3D</option>
						<option value="IMAX">IMAX</option>
					</select>
				</div>
				<div class="detail-field">
					<label for="editPhuThuSelect">Phụ Thu (nếu có)</label>
					<div class="tag-container" id="editPhuThuContainer"></div>
					<select id="editPhuThuSelect" class="form-control phu-thu-select"
						onchange="addEditPhuThuTag()">
						<option value="" disabled selected>-- Chọn Phụ Thu --</option>
						<c:forEach var="phuThu" items="${phuThuList}">
							<option value="${phuThu.maPhuThu}">${phuThu.maPhuThu}-
								${phuThu.tenPhuThu}</option>
						</c:forEach>
					</select> <input type="hidden" name="maPhuThus" id="editPhuThuHidden">
				</div>
				<div class="modal-actions">
					<button type="button" class="custom-btn"
						onclick="prepareAndSubmitEditForm()">Lưu</button>
					<button type="button" class="custom-btn btn-secondary"
						onclick="closeModal()">Hủy</button>
				</div>
			</form>
		</div>
	</div>

	<!-- Delete Form -->
	<form id="deleteShowtimeForm" method="POST"
		action="${pageContext.request.contextPath}/admin/showtimes/delete"
		style="display: none;">
		<input type="hidden" id="deleteMaSuatHidden" name="maSuatChieu">
		<input type="hidden" id="deleteViewHidden" name="view"
			value="${viewMode}">
	</form>

	<!-- Dữ liệu từ server -->
	<script>
        const serverData = [
            <c:if test="${not empty suatChieuList}">
                <c:forEach var="suat" items="${suatChieuList}" varStatus="status">
                    {
                        maSuat: "${suat.maSuatChieu}",
                        maPhim: "${suat.maPhim}",
                        tenPhim: "${phimMap[suat.maPhim].tenPhim}",
                        thoiLuong: ${phimMap[suat.maPhim].thoiLuong},
                        maRap: "${phongMap[suat.maPhongChieu].maRapChieu}",
                        tenRap: "${rapMap[phongMap[suat.maPhongChieu].maRapChieu].tenRapChieu}",
                        maPhong: "${suat.maPhongChieu}",
                        tenPhong: "${phongMap[suat.maPhongChieu].tenPhongChieu}",
                        ngayGioChieu: "<fmt:formatDate value='${suat.ngayGioChieu}' pattern='yyyy-MM-dd\'T\'HH:mm:ss'/>",
                        ngayGioKetThuc: "<fmt:formatDate value='${suat.ngayGioKetThuc}' pattern='yyyy-MM-dd\'T\'HH:mm:ss'/>",
                        loaiManChieu: "${suat.loaiManChieu}",
                        maPhuThu: [<c:forEach var="phuThu" items="${suat.phuThus}" varStatus="phuThuStatus">"${phuThu.maPhuThu}"<c:if test="${!phuThuStatus.last}">,</c:if></c:forEach>],
                        tenPhuThu: "${suat.danhSachTenPhuThu}",
                        status: "${suat.ngayGioChieu > now ? 'not_started' : (suat.ngayGioKetThuc > now ? 'playing' : 'finished')}"
                    }<c:if test="${!status.last}">,</c:if>
                </c:forEach>
            </c:if>
        ];

        const phimMap = {
            <c:forEach var="phim" items="${phimList}" varStatus="status">
                "${phim.maPhim}": { tenPhim: "${phim.tenPhim}", thoiLuong: ${phim.thoiLuong} }<c:if test="${!status.last}">,</c:if>
            </c:forEach>
        };

        const phongMap = {
            <c:forEach var="phong" items="${phongList}" varStatus="status">
                "${phong.maPhongChieu}": { tenPhongChieu: "${phong.tenPhongChieu}", maRapChieu: "${phong.maRapChieu}" }<c:if test="${!status.last}">,</c:if>
            </c:forEach>
        };

        const rapMap = {
            <c:forEach var="rap" items="${rapList}" varStatus="status">
                "${rap.maRapChieu}": { tenRapChieu: "${rap.tenRapChieu}" }<c:if test="${!status.last}">,</c:if>
            </c:forEach>
        };

        const contextPath = "${pageContext.request.contextPath}";

        document.addEventListener('DOMContentLoaded', function() {
            const errorMessage = document.getElementById('errorMessage');
            if (errorMessage) {
                setTimeout(() => {
                    errorMessage.style.display = 'none';
                }, 5000);
            }
            const successMessage = document.getElementById('successMessage');
            if (successMessage) {
                setTimeout(() => {
                    successMessage.style.display = 'none';
                }, 5000);
            }
            const warningMessage = document.getElementById('warningMessage');
            if (warningMessage) {
                setTimeout(() => {
                    warningMessage.style.display = 'none';
                }, 5000);
            }
        });
    </script>
    <c:if test="${not empty editableShowtime}">
		<script>
        document.addEventListener('DOMContentLoaded', function() {
            handleEditClick('${editableShowtime}', true);
        });
    </script>
	</c:if>
	<script
		src="${pageContext.request.contextPath}/resources/admin/js/showtime-manager.js"></script>
</div>