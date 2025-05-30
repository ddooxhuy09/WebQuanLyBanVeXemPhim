<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản Lý Suất Chiếu</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/admin/css/reset.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/admin/css/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/admin/css/movies_manager.css">
    <style>
        .tag-container {
            display: flex;
            flex-wrap: wrap;
            min-height: 38px;
            padding: 5px;
            border: 1px solid #ced4da;
            border-radius: 4px;
            margin-bottom: 10px;
            background-color: #fff;
        }
        .tag {
            display: inline-flex;
            align-items: center;
            background: #e9ecef;
            padding: 5px 10px;
            margin: 3px;
            border-radius: 5px;
            font-size: 14px;
        }
        .remove-tag {
            cursor: pointer;
            color: #dc3545;
            margin-left: 5px;
            font-weight: bold;
        }
        .remove-tag:hover {
            color: #bd2130;
        }
        .phu-thu-select {
            width: 100%;
            margin-top: 5px;
        }
    </style>
</head>
<body>
    <div class="showtime-management">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h2>Quản Lý Suất Chiếu</h2>
            <div>
                <button id="toggleViewBtn" class="custom-btn mr-2" onclick="toggleShowtimeView()">
                    <i class="fas fa-list"></i> Chuyển Sang Dạng Bảng
                </button>
                <button class="custom-btn add-showtime-btn" onclick="showAddModal()">
                    <i class="fas fa-plus"></i> Thêm Suất
                </button>
            </div>
        </div>

        <!-- Filters -->
        <form id="filterForm" class="filter-section mb-4">
            <div class="form-group">
                <label for="filterStatus">Trạng thái</label>
                <select id="filterStatus" name="filterStatus" class="form-control">
                    <option value="all">Tất cả</option>
                    <option value="not_started">Chưa chiếu</option>
                    <option value="playing">Đang chiếu</option>
                    <option value="finished">Đã chiếu</option>
                    <option value="has_surcharge">Có phụ thu</option>
                </select>
            </div>
            <div class="form-group">
                <label for="filterTime">Tìm theo ngày</label>
                <input type="date" id="filterTime" name="filterTime" class="form-control">
            </div>
            <div class="form-group">
                <label for="filterRap">Rạp Chiếu</label>
                <select id="filterRap" name="filterRap" class="form-control">
                    <option value="all">Tất cả rạp</option>
                    <c:forEach var="rap" items="${rapList}">
                        <option value="${rap.maRapChieu}">${rap.tenRapChieu}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="form-group">
                <label for="filterPhong">Phòng Chiếu</label>
                <select id="filterPhong" name="filterPhong" class="form-control">
                    <option value="all">Tất cả phòng</option>
                    <c:forEach var="phong" items="${phongList}">
                        <option value="${phong.maPhongChieu}" data-rap="${phong.maRapChieu}">${phong.tenPhongChieu}</option>
                    </c:forEach>
                </select>
            </div>
            <button type="button" class="custom-btn filter-btn" onclick="applyFilterAndRender()">Lọc</button>
        </form>

        <!-- Calendar View -->
        <div id="calendarView" style="display: block;">
            <div class="calendar-navigation">
                <button type="button" class="custom-btn" onclick="previousWeek()">Tuần trước</button>
                <span id="weekRange"></span>
                <button type="button" class="custom-btn" onclick="nextWeek()">Tuần sau</button>
            </div>
            <div class="calendar-container">
                <div id="calendarMessage" class="calendar-message" style="display: none;"></div>
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
        <div id="tableView" style="display: none;">
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
                            <td colspan="9" class="text-center">Không có dữ liệu suất chiếu phù hợp.</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- Add Modal -->
    <div class="modal" id="addModal" style="display: none;">
        <div class="modal-content">
            <form id="addShowtimeForm" method="POST" action="${pageContext.request.contextPath}/admin/showtimes/add">
                <h3>Thêm Suất Chiếu Mới</h3>
                <div class="detail-field">
                    <label for="addPhim">Phim</label>
                    <select id="addPhim" name="maPhim" class="form-control" onchange="updateAddThoiLuong()" required>
                        <option value="" disabled selected>-- Chọn Phim --</option>
                        <c:forEach var="phim" items="${phimList}">
                            <option value="${phim.maPhim}" data-thoiluong="${phim.thoiLuong}">${phim.tenPhim} (${phim.thoiLuong} phút)</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="detail-field">
                    <label for="addThoiLuong">Thời Lượng (phút)</label>
                    <input type="number" id="addThoiLuong" class="form-control" readonly>
                </div>
                <div class="detail-field">
                    <label for="addRapChieu">Rạp Chiếu</label>
                    <select id="addRapChieu" name="maRap" class="form-control" onchange="filterAddPhongChieu()" required>
                        <option value="" disabled selected>-- Chọn Rạp --</option>
                        <c:forEach var="rap" items="${rapList}">
                            <option value="${rap.maRapChieu}">${rap.tenRapChieu}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="detail-field">
                    <label for="addPhongChieu">Phòng Chiếu</label>
                    <select id="addPhongChieu" name="maPhongChieu" class="form-control" required>
                        <option value="" disabled selected>-- Chọn Phòng --</option>
                        <c:forEach var="phong" items="${phongList}">
                            <option value="${phong.maPhongChieu}" data-rap="${phong.maRapChieu}" style="display:none;">${phong.tenPhongChieu}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="detail-field">
                    <label for="addLoaiManChieu">Loại Màn Chiếu</label>
                    <select id="addLoaiManChieu" name="loaiManChieu" class="form-control">
                        <option value="2D">2D</option>
                        <option value="3D">3D</option>
                        <option value="IMAX">IMAX</option>
                    </select>
                </div>
                <div class="detail-field">
                    <label for="addPhuThuSelect">Phụ Thu (nếu có)</label>
                    <div class="tag-container" id="phuThuContainer"></div>
                    <select id="addPhuThuSelect" class="form-control phu-thu-select" onchange="addPhuThuTag()">
                        <option value="" disabled selected>-- Chọn Phụ Thu --</option>
                        <c:forEach var="phuThu" items="${phuThuList}">
                            <option value="${phuThu.maPhuThu}">${phuThu.maPhuThu} - ${phuThu.tenPhuThu}</option>
                        </c:forEach>
                    </select>
                    <input type="hidden" name="maPhuThu" id="phuThuHidden">
                </div>
                <hr>
                <label>Các Khung Giờ Chiếu:</label>
                <div id="addTimeSlotsContainer"></div>
                <button type="button" class="custom-btn btn-sm mt-2" onclick="addMoreTimeSlot()">
                    <i class="fas fa-plus"></i> Thêm Khung Giờ
                </button>
                <hr>
                <div id="hiddenShowtimeInputsContainer"></div>
                <div class="modal-actions">
                    <button type="button" class="custom-btn" onclick="prepareAndSubmitAddForm()">Thêm Suất Chiếu</button>
                    <button type="button" class="custom-btn btn-secondary" onclick="closeAddModal()">Hủy</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Edit Modal -->
    <div class="modal" id="editModal" style="display: none;">
        <div class="modal-content">
            <form id="editShowtimeForm" method="POST" action="${pageContext.request.contextPath}/admin/showtimes/update">
                <h3>Sửa Thông Tin Suất Chiếu</h3>
                <input type="hidden" id="editMaSuatHidden" name="maSuatChieu">
                <div class="detail-field">
                    <label for="editMaSuatDisplay">Mã Suất</label>
                    <input type="text" id="editMaSuatDisplay" class="form-control" readonly>
                </div>
                <div class="detail-field">
                    <label for="editPhim">Phim</label>
                    <select id="editPhim" name="maPhim" class="form-control" onchange="updateEditThoiLuongAndEndTime()" required>
                        <option value="" disabled selected>-- Chọn Phim --</option>
                        <c:forEach var="phim" items="${phimList}">
                            <option value="${phim.maPhim}" data-thoiluong="${phim.thoiLuong}">${phim.tenPhim}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="detail-field">
                    <label for="editThoiLuong">Thời Lượng (phút)</label>
                    <input type="number" id="editThoiLuong" class="form-control" readonly>
                </div>
                <div class="detail-field">
                    <label for="editRapChieu">Rạp Chiếu</label>
                    <select id="editRapChieu" name="maRap" class="form-control" onchange="filterEditPhongChieu()" required>
                        <option value="" disabled selected>-- Chọn Rạp --</option>
                        <c:forEach var="rap" items="${rapList}">
                            <option value="${rap.maRapChieu}">${rap.tenRapChieu}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="detail-field">
                    <label for="editPhongChieu">Phòng Chiếu</label>
                    <select id="editPhongChieu" name="maPhongChieu" class="form-control" required>
                        <option value="" disabled selected>-- Chọn Phòng --</option>
                        <c:forEach var="phong" items="${phongList}">
                            <option value="${phong.maPhongChieu}" data-rap="${phong.maRapChieu}" style="display:none;">${phong.tenPhongChieu}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="detail-field">
                    <label for="editNgayGioChieu">Ngày Giờ Chiếu</label>
                    <input type="datetime-local" id="editNgayGioChieu" name="ngayGioChieu" class="form-control" onchange="updateEditEndTime()" required>
                </div>
                <div class="detail-field">
                    <label for="editNgayGioKetThuc">Ngày Giờ Kết Thúc</label>
                    <input type="datetime-local" id="editNgayGioKetThuc" name="ngayGioKetThuc" class="form-control" readonly>
                </div>
                <div class="detail-field">
                    <label for="editLoaiManChieu">Loại Màn Chiếu</label>
                    <select id="editLoaiManChieu" name="loaiManChieu" class="form-control">
                        <option value="2D">2D</option>
                        <option value="3D">3D</option>
                        <option value="IMAX">IMAX</option>
                    </select>
                </div>
                <div class="detail-field">
                    <label for="editPhuThu">Phụ Thu</label>
                    <select id="editPhuThu" name="maPhuThu" class="form-control">
                        <option value="">Không có</option>
                        <c:forEach var="phuThu" items="${phuThuList}">
                            <option value="${phuThu.maPhuThu}">${phuThu.maPhuThu} - ${phuThu.tenPhuThu}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="modal-actions">
                    <button type="button" class="custom-btn" onclick="prepareAndSubmitEditForm()">Lưu</button>
                    <button type="button" class="custom-btn btn-secondary" onclick="closeModal()">Hủy</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Delete Form -->
    <form id="deleteShowtimeForm" method="POST" action="${pageContext.request.contextPath}/admin/showtimes/delete" style="display: none;">
        <input type="hidden" id="deleteMaSuatHidden" name="maSuatChieu">
    </form>

    <!-- Dữ liệu từ server -->
    <script>
        // Truyền dữ liệu từ server sang JavaScript
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
	                maPhuThu: null,
	                tenPhuThu: null,
	                status: "${suat.ngayGioChieu > now ? 'not_started' : (suat.ngayGioKetThuc > now ? 'playing' : 'finished')}"
	            }<c:if test="${!status.last}">,</c:if>
	        </c:forEach>
	    </c:if>
	    ];

        // Phim Map
        const phimMap = {
            <c:forEach var="phim" items="${phimList}" varStatus="status">
                "${phim.maPhim}": { tenPhim: "${phim.tenPhim}", thoiLuong: ${phim.thoiLuong} }<c:if test="${!status.last}">,</c:if>
            </c:forEach>
        };

        // Phong Map
        const phongMap = {
            <c:forEach var="phong" items="${phongList}" varStatus="status">
                "${phong.maPhongChieu}": { tenPhongChieu: "${phong.tenPhongChieu}", maRapChieu: "${phong.maRapChieu}" }<c:if test="${!status.last}">,</c:if>
            </c:forEach>
        };

        // Rap Map
        const rapMap = {
            <c:forEach var="rap" items="${rapList}" varStatus="status">
                "${rap.maRapChieu}": { tenRapChieu: "${rap.tenRapChieu}" }<c:if test="${!status.last}">,</c:if>
            </c:forEach>
        };
        
        // Context path cho các URL
        const contextPath = "${pageContext.request.contextPath}";
    </script>

    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.5.2/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/resources/admin/js/showtime-manager.js"></script>
</body>
</html>