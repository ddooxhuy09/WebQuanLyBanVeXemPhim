<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!-- Modal Thêm Khuyến Mãi -->
<div class="modal" id="addModal" style="display: none;">
    <div class="modal-content">
        <h3>Thêm Khuyến Mãi Mới</h3>
        <form action="${pageContext.request.contextPath}/admin/promotions/add" method="post">
            <div class="row">
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="addMaKM">Mã KM</label>
                        <input type="text" class="form-control" id="addMaKM" name="maKhuyenMai" value="${newMaKhuyenMai}" readonly>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="addMaCode">Mã Code</label>
                        <input type="text" class="form-control" id="addMaCode" name="maCode" placeholder="VD: CODE003">
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-md-12">
                    <div class="form-group">
                        <label for="addMoTa">Mô Tả</label>
                        <textarea class="form-control" id="addMoTa" name="moTa" rows="3" placeholder="VD: Giảm giá 20% cho vé xem phim"></textarea>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="addLoaiGiamGia">Loại Giảm Giá</label>
                        <select class="form-control" id="addLoaiGiamGia" name="loaiGiamGia">
                            <option value="Phần trăm">Phần trăm</option>
                            <option value="Số tiền cố định">Số tiền cố định</option>
                        </select>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="addGiaTriGiam">Giá Trị Giảm</label>
                        <input type="number" class="form-control" id="addGiaTriGiam" name="giaTriGiam" step="0.01" min="0" placeholder="VD: 20.00">
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="addNgayBatDau">Ngày Bắt Đầu</label>
                        <input type="date" class="form-control" id="addNgayBatDau" name="ngayBatDau">
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="addNgayKetThuc">Ngày Kết Thúc</label>
                        <input type="date" class="form-control" id="addNgayKetThuc" name="ngayKetThuc">
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="addApDungCho">Áp Dụng Cho</label>
                        <select class="form-control" id="addApDungCho" name="apDungCho">
                            <option value="Tất cả khách hàng">Tất cả khách hàng</option>
                            <option value="Khách hàng VIP">Khách hàng VIP</option>
                            <option value="Khách hàng mới">Khách hàng mới</option>
                        </select>
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

<!-- Danh Sách Khuyến Mãi -->
<div class="promotion-list mb-4">
    <div class="promotion-header">
        <h2>Danh Sách Khuyến Mãi</h2>
        <div class="add-btn-container">
            <button class="custom-btn" onclick="showAddModal()">Thêm Khuyến Mãi</button>
        </div>
    </div>
    <div class="table-responsive">
        <table class="table table-bordered table-striped">
            <thead>
                <tr>
                    <th>Mã KM</th>
                    <th>Mã Code</th>
                    <th>Mô Tả</th>
                    <th>Loại Giảm Giá</th>
                    <th>Giá Trị Giảm</th>
                    <th>Ngày Bắt Đầu</th>
                    <th>Ngày Kết Thúc</th>
                    <th>Áp Dụng Cho</th>
                    <th>Hành Động</th>
                </tr>
            </thead>
            <tbody id="promotionList">
                <c:choose>
                    <c:when test="${empty khuyenMaiList}">
                        <tr class="no-data">
                            <td colspan="9" class="no-data">Không có dữ liệu</td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="km" items="${khuyenMaiList}">
                            <tr data-promotion-id="${km.maKhuyenMai}">
                                <td data-field="maKM">${km.maKhuyenMai}</td>
                                <td data-field="maCode">${km.maCode}</td>
                                <<td data-field="moTa">
								    <c:set var="moTa" value="${km.moTa}"/>
								    <c:set var="shortMoTa" value="${moTa.length() > 50 ? fn:substring(moTa, 0, 50) + '...' : moTa}"/>
								    <span class="description-short">${shortMoTa}</span>
								    <span class="description-full" style="display: none;">${moTa}</span>
								    <c:if test="${moTa.length() > 50}">
								        <span class="view-more" onclick="showDescriptionModal(this)">Xem thêm</span>
								    </c:if>
								</td>
                                <td data-field="loaiGiamGia">${km.loaiGiamGia}</td>
                                <td data-field="giaTriGiam">${km.giaTriGiam}</td>
                                <td data-field="ngayBatDau"><fmt:formatDate value="${km.ngayBatDau}" pattern="yyyy-MM-dd"/></td>
                                <td data-field="ngayKetThuc"><fmt:formatDate value="${km.ngayKetThuc}" pattern="yyyy-MM-dd"/></td>
                                <td data-field="apDungCho">${km.apDungCho}</td>
                                <td>
                                    <button class="custom-btn btn-sm mr-1" onclick="showEditModal(this)">Sửa</button>
                                    <a href="${pageContext.request.contextPath}/admin/promotions/delete/${km.maKhuyenMai}" class="custom-btn btn-sm" onclick="return confirm('Bạn có chắc muốn xóa khuyến mãi ${km.maKhuyenMai} không?');">Xóa</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>
</div>

<!-- Modal Sửa -->
<div class="modal" id="editModal" style="display: none;">
    <div class="modal-content">
        <h3>Sửa Thông Tin Khuyến Mãi</h3>
        <form action="${pageContext.request.contextPath}/admin/promotions/update" method="post">
            <div class="detail-field">
                <label for="editMaKM">Mã KM</label>
                <input type="text" id="editMaKM" name="maKhuyenMai" class="form-control" readonly>
            </div>
            <div class="detail-field">
                <label for="editMaCode">Mã Code</label>
                <input type="text" id="editMaCode" name="maCode" class="form-control">
            </div>
            <div class="detail-field">
                <label for="editMoTa">Mô Tả</label>
                <textarea id="editMoTa" name="moTa" class="form-control" rows="3"></textarea>
            </div>
            <div class="detail-field">
                <label for="editLoaiGiamGia">Loại Giảm Giá</label>
                <select id="editLoaiGiamGia" name="loaiGiamGia" class="form-control">
                    <option value="Phần trăm">Phần trăm</option>
                    <option value="Số tiền cố định">Số tiền cố định</option>
                </select>
            </div>
            <div class="detail-field">
                <label for="editGiaTriGiam">Giá Trị Giảm</label>
                <input type="number" id="editGiaTriGiam" name="giaTriGiam" class="form-control" step="0.01" min="0">
            </div>
            <div class="detail-field">
                <label for="editNgayBatDau">Ngày Bắt Đầu</label>
                <input type="date" id="editNgayBatDau" name="ngayBatDau" class="form-control">
            </div>
            <div class="detail-field">
                <label for="editNgayKetThuc">Ngày Kết Thúc</label>
                <input type="date" id="editNgayKetThuc" name="ngayKetThuc" class="form-control">
            </div>
            <div class="detail-field">
                <label for="editApDungCho">Áp Dụng Cho</label>
                <select id="editApDungCho" name="apDungCho" class="form-control">
                    <option value="Tất cả khách hàng">Tất cả khách hàng</option>
                    <option value="Khách hàng VIP">Khách hàng VIP</option>
                    <option value="Khách hàng mới">Khách hàng mới</option>
                </select>
            </div>
            <div class="modal-actions">
                <button type="submit" class="customरी-btn">Lưu</button>
                <button type="button" class="custom-btn" onclick="closeModal()">Hủy</button>
            </div>
        </form>
    </div>
</div>

<!-- Modal Hiển Thị Toàn Bộ Mô Tả -->
<div class="modal" id="descriptionModal" style="display: none;">
    <div class="modal-content">
        <h3>Mô Tả Khuyến Mãi</h3>
        <p id="fullDescription"></p>
        <div class="modal-actions">
            <button class="custom-btn" onclick="closeDescriptionModal()">Đóng</button>
        </div>
    </div>
</div>

<!-- JavaScript -->
<script>
    document.addEventListener('DOMContentLoaded', function () {
        updatePromotionList();
    });

    function updatePromotionList() {
        const maxLength = 50;
        const promotionList = document.getElementById('promotionList');
        const rows = promotionList.querySelectorAll('tr:not(.no-data)');

        const noDataRow = promotionList.querySelector('.no-data');
        if (noDataRow && rows.length > 0) {
            noDataRow.remove();
        } else if (!noDataRow && rows.length === 0) {
            const newRow = document.createElement('tr');
            newRow.classList.add('no-data');
            newRow.innerHTML = `<td colspan="9" class="no-data">Không có dữ liệu</td>`;
            promotionList.appendChild(newRow);
        }

        document.querySelectorAll('td[data-field="moTa"]').forEach(cell => {
            const fullDescription = cell.querySelector('.description-full').textContent;
            const shortDescriptionSpan = cell.querySelector('.description-short');
            const viewMore = cell.querySelector('.view-more');
            if (fullDescription.length > maxLength) {
                shortDescriptionSpan.textContent = fullDescription.substring(0, maxLength) + '...';
                if (viewMore) viewMore.style.display = 'inline';
            } else {
                shortDescriptionSpan.textContent = fullDescription;
                if (viewMore) viewMore.style.display = 'none';
            }
        });
    }

    function showDescriptionModal(element) {
        const fullDescription = element.parentElement.querySelector('.description-full').textContent;
        document.getElementById('fullDescription').textContent = fullDescription;
        document.getElementById('descriptionModal').style.display = 'flex';
    }

    function closeDescriptionModal() {
        document.getElementById('descriptionModal').style.display = 'none';
    }

    function showEditModal(button) {
        const row = button.closest('tr');
        const maKM = row.cells[0].textContent;
        const maCode = row.cells[1].textContent;
        const moTa = row.cells[2].querySelector('.description-full').textContent;
        const loaiGiamGia = row.cells[3].textContent;
        const giaTriGiam = row.cells[4].textContent;
        const ngayBatDau = row.cells[5].textContent;
        const ngayKetThuc = row.cells[6].textContent;
        const apDungCho = row.cells[7].textContent;

        document.getElementById('editMaKM').value = maKM;
        document.getElementById('editMaCode').value = maCode;
        document.getElementById('editMoTa').value = moTa;
        document.getElementById('editLoaiGiamGia').value = loaiGiamGia;
        document.getElementById('editGiaTriGiam').value = giaTriGiam;
        document.getElementById('editNgayBatDau').value = ngayBatDau;
        document.getElementById('editNgayKetThuc').value = ngayKetThuc;
        document.getElementById('editApDungCho').value = apDungCho;

        document.getElementById('editModal').style.display = 'flex';
    }

    function closeModal() {
        document.getElementById('editModal').style.display = 'none';
    }

    function showAddModal() {
        document.getElementById('addModal').style.display = 'flex';
    }

    function closeAddModal() {
        document.getElementById('addModal').style.display = 'none';
    }
</script>