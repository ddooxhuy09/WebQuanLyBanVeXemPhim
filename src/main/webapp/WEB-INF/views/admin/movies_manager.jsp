<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div class="header">
	<h2>Quản Lý Phim</h2>
	<div class="add-btn-container">
		<button class="custom-btn" onclick="showAddModal()">Thêm Phim</button>
	</div>
</div>

<!-- Filter Section -->
<div class="filter-section mb-3">
	<form id="filterForm"
		action="${pageContext.request.contextPath}/admin/movies" method="get"
		class="form-inline">
		<div class="form-group mr-3">
            <label for="searchTenPhim" class="mr-2">Tìm kiếm:</label>
            <input type="text" class="form-control" id="searchTenPhim" name="searchTenPhim" placeholder="Nhập tên phim..." value="${searchTenPhim}">
        </div>
		<div class="form-group mr-3">
			<label for="sort" class="mr-2">Sắp xếp theo:</label> <select
				class="form-control" id="sort" name="sort">
				<option value="all" ${sort == 'all' ? 'selected' : ''}>Tất
					cả</option>
				<option value="ngayKhoiChieu_asc"
					${sort == 'ngayKhoiChieu_asc' ? 'selected' : ''}>Ngày phát
					hành gần nhất</option>
				<option value="ngayKhoiChieu_desc"
					${sort == 'ngayKhoiChieu_desc' ? 'selected' : ''}>Ngày
					phát hành xa nhất</option>
			</select>
		</div>
		<div class="form-group mr-3">
			<label for="filterTheLoai" class="mr-2">Thể Loại:</label> <select
				class="form-control" id="filterTheLoai" name="theLoai">
				<option value="all" ${theLoai == 'all' ? 'selected' : ''}>Tất
					cả</option>
				<c:forEach var="tl" items="${theLoaiList}">
					<option value="${tl.tenTheLoai}"
						${theLoai == tl.tenTheLoai ? 'selected' : ''}>${tl.tenTheLoai}</option>
				</c:forEach>
			</select>
		</div>
		<div class="form-group mr-3">
			<label for="filterDinhDang" class="mr-2">Định Dạng:</label> <select
				class="form-control" id="filterDinhDang" name="dinhDang">
				<option value="all" ${dinhDang == 'all' ? 'selected' : ''}>Tất
					cả</option>
				<c:forEach var="dd" items="${dinhDangList}">
					<option value="${dd.tenDinhDang}"
						${dinhDang == dd.tenDinhDang ? 'selected' : ''}>${dd.tenDinhDang}</option>
				</c:forEach>
			</select>
		</div>
		<div class="form-group mr-3">
			<label for="filterDoTuoi" class="mr-2">Độ Tuổi:</label> <select
				class="form-control" id="filterDoTuoi" name="doTuoi">
				<option value="all" ${doTuoi == 'all' ? 'selected' : ''}>Tất
					cả</option>
				<option value="13" ${doTuoi == '13' ? 'selected' : ''}>13+</option>
				<option value="16" ${doTuoi == '16' ? 'selected' : ''}>16+</option>
				<option value="18" ${doTuoi == '18' ? 'selected' : ''}>18+</option>
			</select>
		</div>
		<div class="form-group mr-3">
			<label for="filterQuocGia" class="mr-2">Quốc Gia:</label> <select
				class="form-control" id="filterQuocGia" name="quocGia">
				<option value="all" ${quocGia == 'all' ? 'selected' : ''}>Tất
					cả</option>
				<option value="Việt Nam" ${quocGia == 'Việt Nam' ? 'selected' : ''}>Việt
					Nam</option>
				<option value="Mỹ" ${quocGia == 'Mỹ' ? 'selected' : ''}>Mỹ</option>
				<option value="Hàn Quốc" ${quocGia == 'Hàn Quốc' ? 'selected' : ''}>Hàn
					Quốc</option>
				<option value="Nhật Bản" ${quocGia == 'Nhật Bản' ? 'selected' : ''}>Nhật
					Bản</option>
			</select>
		</div>
		<button type="submit" class="custom-btn">Áp dụng</button>
	</form>
</div>

<!-- Error and Success Messages -->
<c:if test="${not empty error}">
	<div class="alert alert-danger" id="errorMessage">${error}</div>
</c:if>
<c:if test="${not empty success}">
	<div class="alert alert-success" id="successMessage">${success}</div>
</c:if>

<!-- List Table -->
<div class="table-responsive">
	<table class="table table-bordered table-striped" id="movieTable">
		<thead>
			<tr>
				<th>Mã Phim</th>
				<th>Tên Phim</th>
				<th>Nhà Sản Xuất</th>
				<th>Quốc Gia</th>
				<th>Ngày Khởi Chiếu</th>
				<th>Thời Lượng</th>
				<th>Giá Vé</th>
				<th>Hành Động</th>
			</tr>
		</thead>
		<tbody id="movieList">
			<c:forEach var="phim" items="${phimList}">
				<tr data-movie-id="${phim.maPhim}" data-ten-phim="${phim.tenPhim}"
					data-ngay-khoi-chieu="<fmt:formatDate value='${phim.ngayKhoiChieu}' pattern='yyyy-MM-dd'/>"
					data-nha-sx="${phim.nhaSanXuat}" data-quoc-gia="${phim.quocGia}"
					data-the-loai="<c:forEach var='tl' items='${phim.theLoais}' varStatus='loop'>${tl.tenTheLoai}<c:if test='${!loop.last}'>,</c:if></c:forEach>"
					data-dinh-dang="<c:forEach var='dd' items='${phim.dinhDangs}' varStatus='loop'>${dd.tenDinhDang}<c:if test='${!loop.last}'>,</c:if></c:forEach>"
					data-do-tuoi="${phim.doTuoi}" data-dao-dien="${phim.daoDien}"
					data-dv-chinh="<c:forEach var='dv' items='${phim.dienViens}' varStatus='loop'>${dv.hoTen}<c:if test='${!loop.last}'>,</c:if></c:forEach>"
					data-thoi-luong="${phim.thoiLuong}"
					data-url-poster="${phim.urlPoster}"
					data-url-trailer="${phim.urlTrailer}"
					data-gia-ve="${phim.giaVe.longValue()}" data-mo-ta="${phim.moTa}">
					<td data-field="maPhim">${phim.maPhim}</td>
					<td data-field="tenPhim">${phim.tenPhim}</td>
					<td data-field="nhaSX">${phim.nhaSanXuat}</td>
					<td data-field="quocGia">${phim.quocGia}</td>
					<td data-field="ngayKhoiChieu"><fmt:formatDate
							value="${phim.ngayKhoiChieu}" pattern="dd/MM/yyyy" /></td>
					<td data-field="thoiLuong">${phim.thoiLuong}</td>
					<td data-field="giaVe" class="currency"><fmt:formatNumber
							value="${phim.giaVe.longValue()}" type="number"
							groupingUsed="true" minFractionDigits="0" maxFractionDigits="0" />đ</td>
					<td><a
						href="${pageContext.request.contextPath}/admin/movies?viewMaPhim=${phim.maPhim}&sort=${sort}&theLoai=${theLoai}&dinhDang=${dinhDang}&doTuoi=${doTuoi}&quocGia=${quocGia}"
						class="custom-btn btn-sm">Xem Chi Tiết</a></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>

<!-- Add Modal -->
<div class="modal movie-modal" id="addModal" style="display: none;">
    <div class="modal-content">
        <h3>Thêm Phim Mới</h3>
        <form id="addMovieForm" action="${pageContext.request.contextPath}/admin/movies/add" method="post" enctype="multipart/form-data">
            <div class="row">
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="addMaPhim">Mã Phim</label>
                        <input type="text" class="form-control" id="addMaPhim" name="maPhim" value="${newMaPhim}" readonly>
                    </div>
                    <div class="form-group">
                        <label for="addTenPhim">Tên Phim</label>
                        <input type="text" class="form-control" id="addTenPhim" name="tenPhim" value="${phimModel.tenPhim}" placeholder="VD: Phim C" required>
                    </div>
                    <div class="form-group">
                        <label for="addNhaSanXuat">Nhà Sản Xuất</label>
                        <input type="text" class="form-control" id="addNhaSanXuat" name="nhaSanXuat" value="${phimModel.nhaSanXuat}" placeholder="VD: Nhà SX C" required>
                    </div>
                    <div class="form-group">
                        <label for="addQuocGia">Quốc Gia</label>
                        <input type="text" class="form-control" id="addQuocGia" name="quocGia" value="${phimModel.quocGia}" placeholder="VD: Hàn Quốc" required>
                    </div>
                    <div class="form-group">
                        <label for="addTheLoai">Thể Loại</label>
                        <div class="tag-container" id="addTagContainer">
                            <input type="text" class="form-control tag-input" id="addTheLoaiInput" list="theLoaiList" placeholder="Thêm thể loại..." autocomplete="off">
                        </div>
                        <datalist id="theLoaiList">
                            <c:forEach var="tl" items="${theLoaiList}">
                                <option value="${tl.tenTheLoai}"></option>
                            </c:forEach>
                        </datalist>
                        <input type="hidden" name="theLoai" id="addTheLoaiHidden" value="${theLoai}" required>
                    </div>
                    <div class="form-group">
                        <label for="addDinhDang">Định Dạng</label>
                        <div class="tag-container" id="addDinhDangContainer">
                            <input type="text" class="form-control tag-input" id="addDinhDangInput" list="dinhDangList" placeholder="Thêm định dạng..." autocomplete="off">
                        </div>
                        <datalist id="dinhDangList">
                            <c:forEach var="dd" items="${dinhDangList}">
                                <option value="${dd.tenDinhDang}"></option>
                            </c:forEach>
                        </datalist>
                        <input type="hidden" name="dinhDang" id="addDinhDangHidden" value="${dinhDang}" required>
                    </div>
                    <div class="form-group">
                        <label for="addDoTuoi">Độ Tuổi</label>
                        <input type="number" class="form-control" id="addDoTuoi" name="doTuoi" value="${phimModel.doTuoi}" placeholder="VD: 18">
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="addDaoDien">Đạo Diễn</label>
                        <input type="text" class="form-control" id="addDaoDien" name="daoDien" value="${phimModel.daoDien}" placeholder="VD: Đạo diễn C" required>
                    </div>
                    <div class="form-group">
                        <label for="addDvChinh">Diễn Viên Chính</label>
                        <div class="tag-container" id="addActorContainer">
                            <input type="text" class="form-control tag-input" id="addDvChinhInput" list="dvChinhList" placeholder="Thêm diễn viên..." autocomplete="off">
                        </div>
                        <datalist id="dvChinhList">
                            <c:forEach var="dv" items="${dienVienList}">
                                <option value="${dv.hoTen}"></option>
                            </c:forEach>
                        </datalist>
                        <input type="hidden" name="dvChinh" id="addDvChinhHidden" value="${dvChinh}" required>
                    </div>
                    <div class="form-group">
                        <label for="addNgayKhoiChieu">Ngày Khởi Chiếu</label>
                        <input type="date" class="form-control" id="addNgayKhoiChieu" name="ngayKhoiChieu" value="" required>
                    </div>
                    <div class="form-group">
                        <label for="addThoiLuong">Thời Lượng (phút)</label>
                        <input type="number" class="form-control" id="addThoiLuong" name="thoiLuong" value="${phimModel.thoiLuong}" placeholder="VD: 130" min="20" required>
                    </div>
                    <div class="form-group">
                        <label for="addPoster">Poster</label>
                        <input type="file" class="form-control" id="addPoster" name="poster" accept="image/jpeg,image/png" onchange="validateFile(this)" required>
                        <small class="form-text text-muted">Chọn file hình ảnh (jpg, png, tối đa 5MB).</small>
                    </div>
                    <div class="form-group">
                        <label for="addUrlTrailer">URL Trailer</label>
                        <input type="url" class="form-control" id="addUrlTrailer" name="urlTrailer" value="${phimModel.urlTrailer}" placeholder="VD: http://example.com/trailer.mp4">
                    </div>
                    <div class="form-group">
                        <label for="addGiaVe">Giá Vé</label>
                        <input type="text" class="form-control" id="addGiaVe" name="giaVe" value="${phimModel.giaVe}" placeholder="VD: 120,000 đ" required>
                    </div>
                    <div class="form-group">
                        <label for="addMoTa">Mô Tả</label>
                        <textarea class="form-control" id="addMoTa" name="moTa" rows="4">${phimModel.moTa}</textarea>
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

<!-- Combined Detail/Edit Modal -->
<div class="modal movie-modal" id="movieDetailModal" style="display: none;">
    <div class="modal-content">
        <h3>Chi Tiết và Chỉnh Sửa Phim</h3>
        <c:if test="${not isEditable}">
            <div class="alert alert-warning">
                Phim không thể chỉnh sửa do đã có suất chiếu hoặc ngày khởi chiếu đã qua/hôm nay.
            </div>
        </c:if>
        <form id="movieDetailForm" action="${pageContext.request.contextPath}/admin/movies/update" method="post" enctype="multipart/form-data">
            <div class="row">
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="maPhim">Mã Phim</label>
                        <input type="text" class="form-control" id="maPhim" name="maPhimDisplay" value="${phimModel.maPhim}" readonly>
                        <input type="hidden" name="maPhim" value="${phimModel.maPhim}">
                    </div>
                    <div class="form-group">
                        <label for="tenPhim">Tên Phim</label>
                        <input type="text" class="form-control field-value" id="tenPhim" name="tenPhim" value="${phimModel.tenPhim}" <c:if test="${not isEditable}">readonly</c:if>>
                    </div>
                    <div class="form-group">
                        <label for="nhaSanXuat">Nhà Sản Xuất</label>
                        <input type="text" class="form-control field-value" id="nhaSanXuat" name="nhaSanXuat" value="${phimModel.nhaSanXuat}" <c:if test="${not isEditable}">readonly</c:if>>
                    </div>
                    <div class="form-group">
                        <label for="quocGia">Quốc Gia</label>
                        <input type="text" class="form-control field-value" id="quocGia" name="quocGia" value="${phimModel.quocGia}" <c:if test="${not isEditable}">readonly</c:if>>
                    </div>
                    <div class="form-group">
                        <label for="theLoai">Thể Loại</label>
                        <div class="tag-container field-value" id="theLoaiContainer" <c:if test="${not isEditable}">style="display: none;"</c:if>>
                            <c:forEach var="tl" items="${phimModel.theLoais}">
                                <span class="tag">${tl.tenTheLoai} <span class="remove-tag" onclick="removeTag(this)">×</span></span>
                            </c:forEach>
                            <input type="text" class="form-control tag-input" id="theLoaiInput" list="theLoaiList" placeholder="Thêm thể loại..." autocomplete="off">
                        </div>
                        <div class="tag-display" id="theLoaiDisplay" <c:if test="${not isEditable}">style="display: block;"</c:if>>
                            <c:choose>
                                <c:when test="${not empty phimModel.theLoais}">
                                    <c:forEach var="tl" items="${phimModel.theLoais}" varStatus="loop">
                                        ${tl.tenTheLoai}${loop.last ? '' : ', '}
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>Chưa có thể loại</c:otherwise>
                            </c:choose>
                        </div>
                        <datalist id="theLoaiList">
                            <c:forEach var="tl" items="${theLoaiList}">
                                <option value="${tl.tenTheLoai}"></option>
                            </c:forEach>
                        </datalist>
                        <input type="hidden" name="theLoai" id="theLoaiHidden" value="${theLoaiString}">
                    </div>
                    <div class="form-group">
                        <label for="dinhDang">Định Dạng</label>
                        <div class="tag-container field-value" id="dinhDangContainer" <c:if test="${not isEditable}">style="display: none;"</c:if>>
                            <c:forEach var="dd" items="${phimModel.dinhDangs}">
                                <span class="tag">${dd.tenDinhDang} <span class="remove-tag" onclick="removeTag(this)">×</span></span>
                            </c:forEach>
                            <input type="text" class="form-control tag-input" id="dinhDangInput" list="dinhDangList" placeholder="Thêm định dạng..." autocomplete="off">
                        </div>
                        <div class="tag-display" id="dinhDangDisplay" <c:if test="${not isEditable}">style="display: block;"</c:if>>
                            <c:choose>
                                <c:when test="${not empty phimModel.dinhDangs}">
                                    <c:forEach var="dd" items="${phimModel.dinhDangs}" varStatus="loop">
                                        ${dd.tenDinhDang}${loop.last ? '' : ', '}
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>Chưa có định dạng</c:otherwise>
                            </c:choose>
                        </div>
                        <datalist id="dinhDangList">
                            <c:forEach var="dd" items="${dinhDangList}">
                                <option value="${dd.tenDinhDang}"></option>
                            </c:forEach>
                        </datalist>
                        <input type="hidden" name="dinhDang" id="dinhDangHidden" value="${dinhDangString}">
                    </div>
                    <div class="form-group">
                        <label for="doTuoi">Độ Tuổi</label>
                        <input type="number" class="form-control field-value" id="doTuoi" name="doTuoi" value="${phimModel.doTuoi}" <c:if test="${not isEditable}">readonly</c:if>>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="daoDien">Đạo Diễn</label>
                        <input type="text" class="form-control field-value" id="daoDien" name="daoDien" value="${phimModel.daoDien}" <c:if test="${not isEditable}">readonly</c:if>>
                    </div>
                    <div class="form-group">
                        <label for="dvChinh">Diễn Viên Chính</label>
                        <div class="tag-container field-value" id="dvChinhContainer" <c:if test="${not isEditable}">style="display: none;"</c:if>>
                            <c:forEach var="dv" items="${phimModel.dienViens}">
                                <span class="tag">${dv.hoTen} <span class="remove-tag" onclick="removeTag(this)">×</span></span>
                            </c:forEach>
                            <input type="text" class="form-control tag-input" id="dvChinhInput" list="dvChinhList" placeholder="Thêm diễn viên..." autocomplete="off">
                        </div>
                        <div class="tag-display" id="dvChinhDisplay" <c:if test="${not isEditable}">style="display: block;"</c:if>>
                            <c:choose>
                                <c:when test="${not empty phimModel.dienViens}">
                                    <c:forEach var="dv" items="${phimModel.dienViens}" varStatus="loop">
                                        ${dv.hoTen}${loop.last ? '' : ', '}
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>Chưa có diễn viên</c:otherwise>
                            </c:choose>
                        </div>
                        <datalist id="dvChinhList">
                            <c:forEach var="dv" items="${dienVienList}">
                                <option value="${dv.hoTen}"></option>
                            </c:forEach>
                        </datalist>
                        <input type="hidden" name="dvChinh" id="dvChinhHidden" value="${dvChinhString}">
                    </div>
                    <div class="form-group">
                        <label for="ngayKhoiChieu">Ngày Khởi Chiếu</label>
                        <input type="date" class="form-control field-value" id="ngayKhoiChieu" name="ngayKhoiChieu"
                            value="<fmt:formatDate value='${phimModel.ngayKhoiChieu}' pattern='yyyy-MM-dd'/>" <c:if test="${not isEditable}">readonly</c:if>>
                    </div>
                    <div class="form-group">
                        <label for="thoiLuong">Thời Lượng (phút)</label>
                        <input type="number" class="form-control field-value" id="thoiLuong" name="thoiLuong" value="${phimModel.thoiLuong}" <c:if test="${not isEditable}">readonly</c:if>>
                    </div>
                    <div class="form-group">
                        <label for="poster">Poster</label>
                        <div class="poster-view">
                            <c:if test="${not empty phimModel.urlPoster}">
                                <p>Tên file: ${phimModel.urlPoster}</p>
                                <img src="${pageContext.request.contextPath}/resources/images/${phimModel.urlPoster}" alt="Poster" style="max-width: 100px; max-height: 100px;"
                                    onerror="this.src='${pageContext.request.contextPath}/resources/images/default-poster.jpg';">
                            </c:if>
                            <c:if test="${empty phimModel.urlPoster}">
                                <p>Chưa có poster.</p>
                            </c:if>
                        </div>
                        <div class="poster-edit" <c:if test="${not isEditable}">style="display: none;"</c:if>>
                            <input type="file" class="form-control" id="poster" name="poster" accept="image/jpeg,image/png" onchange="validateFile(this)">
                            <small class="form-text text-muted">Chọn file hình ảnh mới (jpg, png, tối đa 5MB). Để trống để giữ poster hiện tại.</small>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="urlTrailer">URL Trailer</label>
                        <div class="url-trailer-view field-value">
                            <c:choose>
                                <c:when test="${not empty phimModel.urlTrailer}">
                                    <a href="${phimModel.urlTrailer}" target="_blank" rel="noopener noreferrer">${phimModel.urlTrailer}</a>
                                </c:when>
                                <c:otherwise>
                                    <p>Chưa có URL trailer.</p>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <input type="url" class="form-control field-value url-trailer-edit" id="urlTrailer" name="urlTrailer" value="${phimModel.urlTrailer}" <c:if test="${not isEditable}">style="display: none;" readonly</c:if>>
                    </div>
                    <div class="form-group">
                        <label for="giaVe">Giá Vé</label>
                        <input type="text" class="form-control field-value" id="giaVe" name="giaVe"
                            value="<fmt:formatNumber value='${phimModel.giaVe.longValue()}' type='number' groupingUsed='true' minFractionDigits='0' maxFractionDigits='0'/>" <c:if test="${not isEditable}">readonly</c:if>>
                    </div>
                    <div class="form-group">
                        <label for="moTa">Mô Tả</label>
                        <textarea class="form-control field-value" id="moTa" name="moTa" rows="4" <c:if test="${not isEditable}">readonly</c:if>>${phimModel.moTa}</textarea>
                    </div>
                </div>
            </div>
            <div class="modal-actions">
                <c:if test="${isEditable}">
                    <button type="button" class="custom-btn mt-2 mr-1 view-mode-btn" id="editMovieBtn" onclick="enableEditMode()">Sửa</button>
                    <button type="submit" class="custom-btn mt-2 mr-1 edit-mode-btn" id="saveMovieBtn" style="display: none;">Lưu</button>
                    <button type="button" class="custom-btn mt-2 mr-1 edit-mode-btn" id="cancelEditBtn" style="display: none;" onclick="disableEditMode()">Hủy</button>
                </c:if>
                <c:if test="${isDeletable}">
                    <button type="button" class="custom-btn mt-2 mr-1" id="deleteMovieBtn" onclick="deleteMovie('${phimModel.maPhim}')">Xóa</button>
                </c:if>
                <button type="button" class="custom-btn mt-2 mr-1" id="closeModalBtn" onclick="closeDetailModal()">Đóng</button>
            </div>
        </form>
    </div>
</div>

<script
	src="${pageContext.request.contextPath}/resources/admin/js/number-utils.js"></script>
<script
	src="${pageContext.request.contextPath}/resources/admin/js/currency-utils.js"></script>

<script>
// File validation
function validateFile(input) {
    if (!input) {
        console.error('validateFile: Input is null');
        return false;
    }
    const file = input.files[0];
    if (file) {
        const validTypes = ['image/jpeg', 'image/png'];
        if (!validTypes.includes(file.type)) {
            alert('Vui lòng chọn file hình ảnh (jpg hoặc png)!');
            input.value = '';
            return false;
        } else if (file.size > 5 * 1024 * 1024) { // 5MB limit
            alert('Kích thước file không được vượt quá 5MB!');
            input.value = '';
            return false;
        }
    }
    return true;
}

// Tag handling
function removeTag(element) {
    if (!element || !element.parentElement) {
        console.error('removeTag: Element or parentElement is null');
        return;
    }
    const parent = element.parentElement;
    if (parent && parent.classList.contains('tag')) {
        parent.remove();
        const container = element.closest('.tag-container');
        if (container) {
            updateHiddenInput(container.id);
        } else {
            console.error('removeTag: Container with class .tag-container not found');
        }
    }
}

function addTag(inputId, containerId) {
    const input = document.getElementById(inputId);
    const container = document.getElementById(containerId);

    if (!input || !container) {
        console.error(`addTag: Input (${inputId}) or container (${containerId}) not found`);
        return;
    }

    const value = input.value.trim();
    if (value) {
        const existingTags = Array.from(container.getElementsByClassName('tag') || [])
            .map(tag => tag.textContent.replace('×', '').trim());
        if (!existingTags.includes(value)) {
            const newTag = document.createElement('span');
            newTag.className = 'tag';
            newTag.textContent = value;
            const removeButton = document.createElement('span');
            removeButton.className = 'remove-tag';
            removeButton.textContent = '×';
            removeButton.onclick = function() { removeTag(this); };
            newTag.appendChild(removeButton);

            const inputElement = container.querySelector('input.tag-input');
            if (inputElement) {
                container.insertBefore(newTag, inputElement);
            } else {
                container.appendChild(newTag);
            }
            updateHiddenInput(containerId);
        }
        input.value = '';
    }
}

function updateHiddenInput(containerId) {
    const container = document.getElementById(containerId);
    if (!container) {
        console.error(`updateHiddenInput: Container (${containerId}) not found`);
        return;
    }

    const hiddenId = containerId === 'addTagContainer' ? 'addTheLoaiHidden' :
                    containerId === 'addDinhDangContainer' ? 'addDinhDangHidden' :
                    containerId === 'addActorContainer' ? 'addDvChinhHidden' :
                    containerId === 'theLoaiContainer' ? 'theLoaiHidden' :
                    containerId === 'dinhDangContainer' ? 'dinhDangHidden' :
                    'dvChinhHidden';
    const hiddenInput = document.getElementById(hiddenId);

    if (!hiddenInput) {
        console.error(`updateHiddenInput: Hidden input (${hiddenId}) not found`);
        return;
    }

    const tags = Array.from(container.getElementsByClassName('tag') || [])
        .map(tag => tag ? tag.textContent.replace('×', '').trim() : '')
        .filter(tag => tag !== '');
    hiddenInput.value = tags.join(',');
    console.log('Updated hidden input:', hiddenId, 'Value:', hiddenInput.value);
}

function initTagHandlers() {
    const inputIds = ['addTheLoaiInput', 'addDinhDangInput', 'addDvChinhInput', 'theLoaiInput', 'dinhDangInput', 'dvChinhInput'];
    inputIds.forEach(inputId => {
        const input = document.getElementById(inputId);
        if (input) {
            let timeout;
            input.addEventListener('input', (event) => {
                clearTimeout(timeout);
                timeout = setTimeout(() => {
                    const container = event.target.closest('.tag-container');
                    if (container) {
                        addTag(inputId, container.id);
                    } else {
                        console.error(`initTagHandlers: Container for input (${inputId}) not found`);
                    }
                }, 500);
            });
            input.addEventListener('change', (event) => {
                const container = event.target.closest('.tag-container');
                if (container) {
                    addTag(inputId, container.id);
                } else {
                    console.error(`initTagHandlers: Container for input (${inputId}) not found`);
                }
            });
            input.addEventListener('keypress', (event) => {
                if (event.key === 'Enter') {
                    event.preventDefault();
                    const container = event.target.closest('.tag-container');
                    if (container) {
                        addTag(inputId, container.id);
                    } else {
                        console.error(`initTagHandlers: Container for input (${inputId}) not found`);
                    }
                }
            });
        } else {
            console.error(`initTagHandlers: Input (${inputId}) not found`);
        }
    });
}

// Modal controls
function showAddModal() {
    const addModal = document.getElementById('addModal');
    if (addModal) {
        addModal.style.display = 'flex';
        // Reset form
        const addForm = document.getElementById('addMovieForm');
        if (addForm) {
            addForm.reset();
            document.getElementById('addQuocGia').value = '';
            document.getElementById('addDoTuoi').value = ''; // Đảm bảo reset về rỗng
        }
        // Reset tag containers nhưng giữ cấu trúc input và liên kết với datalist
        ['addTagContainer', 'addDinhDangContainer', 'addActorContainer'].forEach(containerId => {
            const container = document.getElementById(containerId);
            if (container) {
                // Xóa các thẻ tag hiện có
                const tags = container.getElementsByClassName('tag');
                Array.from(tags).forEach(tag => tag.remove());
                // Đảm bảo có input và liên kết với datalist
                let input = container.querySelector('input.tag-input');
                if (!input) {
                    input = document.createElement('input');
                    input.type = 'text';
                    input.className = 'form-control tag-input';
                    input.id = containerId.replace('Container', 'Input');
                    input.setAttribute('list', containerId.replace('Container', 'List'));
                    input.placeholder = 'Thêm...';
                    input.setAttribute('autocomplete', 'off');
                    container.appendChild(input);
                }
                updateHiddenInput(containerId); // Cập nhật hidden input
            }
        });
        initTagHandlers(); // Đảm bảo khởi tạo lại sự kiện cho input
    }
}

function closeAddModal() {
    const addModal = document.getElementById('addModal');
    if (addModal) {
        addModal.style.display = 'none';
    } else {
        console.error('closeAddModal: addModal not found');
    }
}

function closeDetailModal() {
    const movieDetailModal = document.getElementById('movieDetailModal');
    if (movieDetailModal) {
        movieDetailModal.style.display = 'none';
        window.history.pushState({}, document.title, "${pageContext.request.contextPath}/admin/movies?sort=${sort}&theLoai=${theLoai}&dinhDang=${dinhDang}&doTuoi=${doTuoi}&quocGia=${quocGia}");
    } else {
        console.error('closeDetailModal: movieDetailModal not found');
    }
}

function enableEditMode() {
    const inputs = document.querySelectorAll('#movieDetailForm input:not([name="maPhim"]):not([data-non-editable="true"]), #movieDetailForm textarea');
    inputs.forEach(input => input.removeAttribute('readonly'));

    const theLoaiContainer = document.getElementById('theLoaiContainer');
    const theLoaiDisplay = document.getElementById('theLoaiDisplay');
    const dinhDangContainer = document.getElementById('dinhDangContainer');
    const dinhDangDisplay = document.getElementById('dinhDangDisplay');
    const dvChinhContainer = document.getElementById('dvChinhContainer');
    const dvChinhDisplay = document.getElementById('dvChinhDisplay');

    if (theLoaiContainer && theLoaiDisplay) {
        theLoaiContainer.style.display = 'block';
        theLoaiDisplay.style.display = 'none';
    }
    if (dinhDangContainer && dinhDangDisplay) {
        dinhDangContainer.style.display = 'block';
        dinhDangDisplay.style.display = 'none';
    }
    if (dvChinhContainer && dvChinhDisplay) {
        dvChinhContainer.style.display = 'block';
        dvChinhDisplay.style.display = 'none';
    }

    const posterView = document.querySelector('.poster-view');
    const posterEdit = document.querySelector('.poster-edit');
    if (posterView && posterEdit) {
        posterView.style.display = 'none';
        posterEdit.style.display = 'block';
    }

    const urlTrailerView = document.querySelector('.url-trailer-view');
    const urlTrailerEdit = document.querySelector('.url-trailer-edit');
    if (urlTrailerView && urlTrailerEdit) {
        urlTrailerView.style.display = 'none';
        urlTrailerEdit.style.display = 'block';
    }

    const viewModeButtons = document.querySelectorAll('.view-mode-btn');
    const editModeButtons = document.querySelectorAll('.edit-mode-btn');
    if (viewModeButtons.length > 0 && editModeButtons.length > 0) {
        viewModeButtons.forEach(btn => btn.style.display = 'none');
        editModeButtons.forEach(btn => btn.style.display = 'inline-block');
    }

    initTagHandlers();
}

function disableEditMode() {
    const inputs = document.querySelectorAll('#movieDetailForm input:not([name="maPhim"]), #movieDetailForm textarea');
    inputs.forEach(input => input.setAttribute('readonly', 'true'));

    const theLoaiContainer = document.getElementById('theLoaiContainer');
    const theLoaiDisplay = document.getElementById('theLoaiDisplay');
    const dinhDangContainer = document.getElementById('dinhDangContainer');
    const dinhDangDisplay = document.getElementById('dinhDangDisplay');
    const dvChinhContainer = document.getElementById('dvChinhContainer');
    const dvChinhDisplay = document.getElementById('dvChinhDisplay');

    if (theLoaiContainer && theLoaiDisplay) {
        theLoaiContainer.style.display = 'none';
        theLoaiDisplay.style.display = 'block';
    }
    if (dinhDangContainer && dinhDangDisplay) {
        dinhDangContainer.style.display = 'none';
        dinhDangDisplay.style.display = 'block';
    }
    if (dvChinhContainer && dvChinhDisplay) {
        dvChinhContainer.style.display = 'none';
        dvChinhDisplay.style.display = 'block';
    }

    const posterView = document.querySelector('.poster-view');
    const posterEdit = document.querySelector('.poster-edit');
    if (posterView && posterEdit) {
        posterView.style.display = 'block';
        posterEdit.style.display = 'none';
    }

    const urlTrailerView = document.querySelector('.url-trailer-view');
    const urlTrailerEdit = document.querySelector('.url-trailer-edit');
    if (urlTrailerView && urlTrailerEdit) {
        urlTrailerView.style.display = 'block';
        urlTrailerEdit.style.display = 'none';
    }

    const viewModeButtons = document.querySelectorAll('.view-mode-btn');
    const editModeButtons = document.querySelectorAll('.edit-mode-btn');
    if (viewModeButtons.length > 0 && editModeButtons.length > 0) {
        viewModeButtons.forEach(btn => btn.style.display = 'inline-block');
        editModeButtons.forEach(btn => btn.style.display = 'none');
    }
}

function deleteMovie(maPhim) {
    if (confirm('Bạn có chắc muốn xóa phim ' + maPhim + ' không?')) {
        window.location.href = "${pageContext.request.contextPath}/admin/movies/delete/" + maPhim + "?sort=${sort}&theLoai=${theLoai}&dinhDang=${dinhDang}&doTuoi=${doTuoi}&quocGia=${quocGia}";
    }
}

function validateAddForm(form) {
    const errors = [];

    // Lấy giá trị các trường
    const ngayKhoiChieu = document.getElementById('addNgayKhoiChieu')?.value || '';
    const tenPhim = document.getElementById('addTenPhim')?.value.trim() || '';
    const nhaSanXuat = document.getElementById('addNhaSanXuat')?.value.trim() || '';
    const quocGia = document.getElementById('addQuocGia')?.value.trim() || '';
    const daoDien = document.getElementById('addDaoDien')?.value.trim() || '';
    const thoiLuong = document.getElementById('addThoiLuong')?.value.trim() || '';
    const urlTrailer = document.getElementById('addUrlTrailer')?.value.trim() || '';
    const giaVe = document.getElementById('addGiaVe')?.value.trim() || '';
    const theLoaiHidden = document.getElementById('addTheLoaiHidden')?.value.trim() || '';
    const dinhDangHidden = document.getElementById('addDinhDangHidden')?.value.trim() || '';
    const dvChinhHidden = document.getElementById('addDvChinhHidden')?.value.trim() || '';

    // Kiểm tra ngày khởi chiếu
    if (!ngayKhoiChieu) {
        errors.push("Ngày khởi chiếu không được để trống!");
    } else {
        const startDate = new Date(ngayKhoiChieu);
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        if (startDate < today) {
            errors.push("Ngày khởi chiếu không được là ngày trong quá khứ!");
        }
    }

    // Kiểm tra các trường khác (unchanged)
    if (!tenPhim) errors.push("Tên phim không được để trống.");
    if (!nhaSanXuat) errors.push("Nhà sản xuất không được để trống.");
    if (!quocGia) errors.push("Quốc gia không được để trống.");
    if (!daoDien) errors.push("Đạo diễn không được để trống.");
    if (!thoiLuong || parseInt(thoiLuong) <= 0) errors.push("Thời lượng phải là số dương.");
    if (!urlTrailer) errors.push("URL trailer không được để trống.");
    if (!giaVe || parseFloat(giaVe.replace(/[^0-9.]/g, '')) <= 0) errors.push("Giá vé phải là số dương.");
    if (!theLoaiHidden) errors.push("Thể loại không được để trống.");
    if (!dinhDangHidden) errors.push("Định dạng không được để trống.");
    if (!dvChinhHidden) errors.push("Diễn viên chính không được để trống.");

    if (errors.length > 0) {
        alert(errors.join("\n"));
        return false;
    }
    return true;
}

function validateEditForm(form) {
    const errors = [];

    const tenPhim = form.querySelector('#tenPhim').value.trim();
    const nhaSanXuat = form.querySelector('#nhaSanXuat').value.trim();
    const quocGia = form.querySelector('#quocGia').value.trim();
    const doTuoi = form.querySelector('#doTuoi').value.trim();
    const daoDien = form.querySelector('#daoDien').value.trim();
    const ngayKhoiChieu = form.querySelector('#ngayKhoiChieu').value.trim();
    const thoiLuong = form.querySelector('#thoiLuong').value.trim();
    const urlTrailer = form.querySelector('#urlTrailer').value.trim();
    const giaVe = form.querySelector('#giaVe').value.trim();
    const theLoaiHidden = form.querySelector('#theLoaiHidden').value.trim();
    const dinhDangHidden = form.querySelector('#dinhDangHidden').value.trim();
    const dvChinhHidden = form.querySelector('#dvChinhHidden').value.trim();

    // Kiểm tra ngày khởi chiếu
    if (!ngayKhoiChieu) {
        errors.push("Ngày khởi chiếu không được để trống!");
    } else {
        const startDate = new Date(ngayKhoiChieu);
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        if (startDate < today) {
            errors.push("Ngày khởi chiếu không được là ngày trong quá khứ!");
        }
    }

    // Kiểm tra các trường khác (unchanged)
    if (!tenPhim) errors.push("Tên phim không được để trống.");
    if (!nhaSanXuat) errors.push("Nhà sản xuất không được để trống.");
    if (!quocGia) errors.push("Quốc gia không được để trống.");
    if (!daoDien) errors.push("Đạo diễn không được để trống.");
    if (!thoiLuong || parseInt(thoiLuong) <= 0) errors.push("Thời lượng phải là số dương.");
    if (!urlTrailer) errors.push("URL trailer không được để trống.");
    if (!giaVe || parseFloat(giaVe.replace(/[^0-9.]/g, '')) <= 0) errors.push("Giá vé phải là số dương.");
    if (!theLoaiHidden) errors.push("Thể loại không được để trống.");
    if (!dinhDangHidden) errors.push("Định dạng không được để trống.");
    if (!dvChinhHidden) errors.push("Diễn viên chính không được để trống.");

    if (errors.length > 0) {
        alert(errors.join("\n"));
        return false;
    }
    return true;
}

document.addEventListener('DOMContentLoaded', () => {
    // Xử lý input giá vé cho form thêm phim
    const addGiaVeInput = document.getElementById('addGiaVe');
    if (addGiaVeInput) {
        addGiaVeInput.addEventListener('input', function (e) {
            let rawValue = e.target.value;
            let cleaned = rawValue.replace(/[^0-9.]/g, '').replace(/(\..*)\./g, '$1');
            if (cleaned) {
                const parsed = parseFloat(cleaned);
                if (!isNaN(parsed) && parsed >= 0) {
                    const formatted = formatCurrencyWithDecimal(parsed);
                    e.target.value = formatted;
                } else {
                    e.target.value = '';
                }
            } else {
                e.target.value = '';
            }
        });
    }

    // Xử lý input giá vé cho form chỉnh sửa
    const editGiaVeInput = document.getElementById('giaVe');
    if (editGiaVeInput) {
        editGiaVeInput.addEventListener('input', function (e) {
            let rawValue = e.target.value;
            let cleaned = rawValue.replace(/[^0-9.]/g, '').replace(/(\..*)\./g, '$1');
            if (cleaned) {
                const parsed = parseFloat(cleaned);
                if (!isNaN(parsed) && parsed >= 0) {
                    const formatted = formatCurrencyWithDecimal(parsed);
                    e.target.value = formatted;
                } else {
                    e.target.value = '';
                }
            } else {
                e.target.value = '';
            }
        });
    }

    // Xử lý form thêm phim
    const addMovieForm = document.getElementById('addMovieForm');
    if (addMovieForm) {
        addMovieForm.addEventListener('submit', function (e) {
            e.preventDefault();

            // Cập nhật hidden input
            ['addTagContainer', 'addDinhDangContainer', 'addActorContainer'].forEach(containerId => {
                updateHiddenInput(containerId);
            });

            if (!validateAddForm(this)) {
                return;
            }

            this.submit();
        });
    }

    // Xử lý form chỉnh sửa
    const movieDetailForm = document.getElementById('movieDetailForm');
    if (movieDetailForm) {
        movieDetailForm.addEventListener('submit', function (e) {
            e.preventDefault();
            ['theLoaiContainer', 'dinhDangContainer', 'dvChinhContainer'].forEach(containerId => {
                updateHiddenInput(containerId);
            });

            if (!validateEditForm(this)) {
                return;
            }

            const formData = new FormData(this);
            console.log('Form data to be sent:');
            for (let [key, value] of formData.entries()) {
                console.log(`${key}: ${value}`);
            }
            this.submit();
        });
    }

    // Hiển thị modal chi tiết nếu có
    <c:if test="${showDetailModal}">
        const movieDetailModal = document.getElementById('movieDetailModal');
        if (movieDetailModal) {
            movieDetailModal.style.display = 'flex';
            disableEditMode();
            initTagHandlers();
        }
    </c:if>

    // Ẩn thông báo lỗi sau 5 giây
    const errorMessage = document.getElementById('errorMessage');
    if (errorMessage) {
        setTimeout(() => {
            errorMessage.style.display = 'none';
        }, 5000);
    }

    // Ẩn thông báo thành công sau 5 giây
    const successMessage = document.getElementById('successMessage');
    if (successMessage) {
        setTimeout(() => {
            successMessage.style.display = 'none';
        }, 5000);
    }
});
</script>