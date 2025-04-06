<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<div class="container">
    <div class="movie-list">
        <h2>Danh Sách Phim</h2>
        <button class="custom-btn mb-3" onclick="window.location.href='${pageContext.request.contextPath}/admin/movies'">Thêm Phim</button>
        <div class="table-responsive">
            <table class="table table-bordered table-striped">
                <thead>
                    <tr>
                        <th>Mã Phim</th>
                        <th>Tên Phim</th>
                        <th>Nhà Sản Xuất</th>
                        <th>Quốc Gia</th>
                        <th>Thể Loại</th>
                        <th>Định Dạng</th>
                        <th>Độ Tuổi</th>
                        <th>Đạo Diễn</th>
                        <th>Diễn Viên Chính</th>
                        <th>Ngày Khởi Chiếu</th>
                        <th>Thời Lượng</th>
                        <th>URL Poster</th>
                        <th>URL Trailer</th>
                        <th>Giá Vé</th>
                        <th>Hành Động</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="phim" items="${phimList}">
                        <tr>
                            <td>${phim.maPhim}</td>
                            <td>${phim.tenPhim}</td>
                            <td>${phim.nhaSanXuat}</td>
                            <td>${phim.quocGia}</td>
                            <td>${phim.maTheLoais}</td>
                            <td>${phim.dinhDang}</td>
                            <td>${phim.doTuoi}</td>
                            <td>${phim.daoDien}</td>
                            <td>${phim.maDienViens}</td>
                            <td><fmt:formatDate value="${phim.ngayKhoiChieu}" pattern="yyyy-MM-dd"/></td>
                            <td>${phim.thoiLuong}</td>
                            <td><a href="${phim.urlPoster}" target="_blank">Xem</a></td>
                            <td><a href="${phim.urlTrailer}" target="_blank">Xem</a></td>
                            <td>${phim.giaVe}</td>
                            <td>
                                <button class="custom-btn btn-sm mr-1" onclick="editMovie('${phim.maPhim}')">Sửa</button>
                                <button class="custom-btn btn-sm" onclick="confirmDeleteMovie('${phim.maPhim}', '${phim.tenPhim}')">Xóa</button>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>

    <div class="movie-form mt-5" id="movieForm" style="display:block">
        <h2>${isEdit ? 'Cập Nhật Phim' : 'Thêm Phim Mới'}</h2>
        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>
        <form action="${pageContext.request.contextPath}/admin/movies/${isEdit ? 'update' : 'add'}" method="post">
            <div class="row">
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="maPhim">Mã Phim</label>
                        <input type="text" class="form-control" id="maPhim" name="maPhim" value="${phimModel.maPhim}" readonly>
                    </div>
                    <div class="form-group">
                        <label for="tenPhim">Tên Phim</label>
                        <input type="text" class="form-control" id="tenPhim" name="tenPhim" value="${phimModel.tenPhim}" required>
                    </div>
                    <div class="form-group">
                        <label for="nhaSX">Nhà Sản Xuất</label>
                        <input type="text" class="form-control" id="nhaSX" name="nhaSX" value="${phimModel.nhaSanXuat}" required>
                    </div>
                    <div class="form-group">
                        <label for="quocGia">Quốc Gia</label>
                        <input type="text" class="form-control" id="quocGia" name="quocGia" value="${phimModel.quocGia}" required>
                    </div>
                    <div class="form-group">
                        <label for="theLoai">Thể Loại</label>
                        <div class="tag-container" id="tagContainer">
                            <c:if test="${isEdit && not empty phimModel.maTheLoais}">
                                <c:forEach var="theLoai" items="${phimModel.maTheLoais}">
                                    <span class="tag">${theLoai} <span class="remove-tag" onclick="removeTag(this, 'theLoai')">×</span></span>
                                </c:forEach>
                            </c:if>
                        </div>
                        <input type="text" class="form-control tag-input" id="theLoaiInput" list="theLoaiList" placeholder="Thêm thể loại..." onchange="addTag('theLoaiInput', 'tagContainer', 'theLoai')">
                        <datalist id="theLoaiList">
                            <c:forEach var="theLoai" items="${theLoaiList}">
                                <option value="${theLoai.tenTheLoai}">
                            </c:forEach>
                        </datalist>
                        <input type="hidden" name="theLoai" id="theLoaiHidden" value="${isEdit ? theLoaiString : ''}">
                    </div>
                    <div class="form-group">
                        <label for="dinhDang">Định Dạng</label>
                        <select class="form-control" id="dinhDang" name="dinhDang">
                            <option value="2D" ${phimModel.dinhDang == '2D' ? 'selected' : ''}>2D</option>
                            <option value="3D" ${phimModel.dinhDang == '3D' ? 'selected' : ''}>3D</option>
                            <option value="IMAX" ${phimModel.dinhDang == 'IMAX' ? 'selected' : ''}>IMAX</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="doTuoi">Độ Tuổi</label>
                        <input type="number" class="form-control" id="doTuoi" name="doTuoi" value="${phimModel.doTuoi}" required>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="daoDien">Đạo Diễn</label>
                        <input type="text" class="form-control" id="daoDien" name="daoDien" value="${phimModel.daoDien}" required>
                    </div>
                    <div class="form-group">
                        <label for="dvChinh">Diễn Viên Chính</label>
                        <div class="tag-container" id="actorContainer">
                            <c:if test="${isEdit && not empty phimModel.maDienViens}">
                                <c:forEach var="dienVien" items="${phimModel.maDienViens}">
                                    <span class="tag">${dienVien} <span class="remove-tag" onclick="removeTag(this, 'dvChinh')">×</span></span>
                                </c:forEach>
                            </c:if>
                        </div>
                        <input type="text" class="form-control tag-input" id="dvChinhInput" list="dvChinhList" placeholder="Thêm diễn viên..." onchange="addTag('dvChinhInput', 'actorContainer', 'dvChinh')">
                        <datalist id="dvChinhList">
                            <c:forEach var="dienVien" items="${dienVienList}">
                                <option value="${dienVien.hoTen}">
                            </c:forEach>
                        </datalist>
                        <input type="hidden" name="dvChinh" id="dvChinhHidden" value="${isEdit ? dvChinhString : ''}">
                    </div>
                    <div class="form-group">
                        <label for="ngayKhoiChieu">Ngày Khởi Chiếu</label>
                        <input type="date" class="form-control" id="ngayKhoiChieu" name="ngayKhoiChieu" 
                            value="<fmt:formatDate value="${phimModel.ngayKhoiChieu}" pattern="yyyy-MM-dd"/>" required>
                    </div>
                    <div class="form-group">
                        <label for="thoiLuong">Thời Lượng (phút)</label>
                        <input type="number" class="form-control" id="thoiLuong" name="thoiLuong" value="${phimModel.thoiLuong}" required>
                    </div>
                    <div class="form-group">
                        <label for="urlPoster">URL Poster</label>
                        <input type="text" class="form-control" id="urlPoster" name="urlPoster" value="${phimModel.urlPoster}" required>
                    </div>
                    <div class="form-group">
                        <label for="urlTrailer">URL Trailer</label>
                        <input type="text" class="form-control" id="urlTrailer" name="urlTrailer" value="${phimModel.urlTrailer}" required>
                    </div>
                    <div class="form-group">
                        <label for="giaVe">Giá Vé</label>
                        <input type="number" step="0.01" class="form-control" id="giaVe" name="giaVe" value="${phimModel.giaVe}" required>
                    </div>
                </div>
            </div>
            <button type="submit" class="custom-btn">${isEdit ? 'Cập Nhật Phim' : 'Thêm Phim'}</button>
            <button type="button" class="custom-btn" onclick="window.location.href='${pageContext.request.contextPath}/admin/movies'">Hủy</button>
        </form>
    </div>
</div>

<style>
    .tag { display: inline-block; background: #e9ecef; padding: 5px 10px; margin: 2px; border-radius: 5px; }
    .remove-tag { cursor: pointer; color: red; margin-left: 5px; }
    .tag-input { display: inline-block; width: auto; }
    .custom-btn { background: #007bff; color: white; padding: 10px 20px; border: none; border-radius: 5px; }
    .btn-sm { padding: 5px 10px; }
</style>

<script>
    function editMovie(maPhim) {
        window.location.href = "${pageContext.request.contextPath}/admin/movies/edit/" + maPhim;
    }

    function confirmDeleteMovie(maPhim, tenPhim) {
        if (confirm("Bạn có chắc chắn muốn xóa phim '" + tenPhim + "'?")) {
            window.location.href = "${pageContext.request.contextPath}/admin/movies/delete/" + maPhim;
        }
    }

    function removeTag(element, type) {
        element.parentElement.remove();
        updateHiddenInput(type);
    }

    function updateHiddenInput(type) {
        const containerId = type === 'theLoai' ? 'tagContainer' : 'actorContainer';
        const hiddenId = type === 'theLoai' ? 'theLoaiHidden' : 'dvChinhHidden';
        const tags = Array.from(document.querySelectorAll(`#${containerId} .tag`))
            .map(tag => tag.textContent.replace('×', '').trim());
        document.getElementById(hiddenId).value = tags.join(',');
    }

    function addTag(inputId, containerId, type) {
        const input = document.getElementById(inputId);
        const value = input.value.trim();
        if (value) {
            const tagContainer = document.getElementById(containerId);
            const existingTags = Array.from(tagContainer.querySelectorAll('.tag'))
                .map(tag => tag.textContent.replace('×', '').trim());

            if (!existingTags.includes(value)) {
                const newTag = document.createElement('span');
                newTag.className = 'tag';
                newTag.innerHTML = `${value} <span class="remove-tag" onclick="removeTag(this, '${type}')">×</span>`;
               	tagContainer.appendChild(newTag);
            }
            input.value = '';  // Clear the input
            updateHiddenInput(type);
        }
    }

    window.onload = function() {
        updateHiddenInput('theLoai');
        updateHiddenInput('dvChinh');
    };
</script>