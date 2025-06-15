<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!-- Error and Success Messages -->
<c:if test="${not empty error}">
	<div class="alert alert-danger" id="errorMessage">${error}</div>
</c:if>
<c:if test="${not empty success}">
	<div class="alert alert-success" id="successMessage">${success}</div>
</c:if>

<!-- Modal Thêm Khuyến Mãi -->
<div class="modal" id="addModal" style="display: none;">
	<div class="modal-content">
		<h3>Thêm Khuyến Mãi Mới</h3>
		<form id="addForm"
			action="${pageContext.request.contextPath}/admin/promotions/add"
			method="post" onsubmit="return validateAddForm(this)">
			<div class="row">
				<div class="col-md-6">
					<div class="form-group">
						<label for="addMaKM">Mã KM</label> <input type="text"
							class="form-control" id="addMaKM" name="maKhuyenMai"
							value="${newMaKhuyenMai}" readonly>
					</div>
				</div>
				<div class="col-md-6">
					<div class="form-group">
						<label for="addMaCode">Mã Code</label> <input type="text"
							class="form-control" id="addMaCode" name="maCode"
							placeholder="VD: CODE003"
							value="${addFormData != null ? addFormData.maCode : ''}">
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12">
					<div class="form-group">
						<label for="addMoTa">Mô Tả</label>
						<textarea class="form-control" id="addMoTa" name="moTa" rows="3"
							placeholder="VD: Giảm giá 20% cho vé xem phim">${addFormData != null ? addFormData.moTa : ''}</textarea>
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-md-6">
					<div class="form-group">
						<label for="addLoaiGiamGia">Loại Giảm Giá</label> <select
							class="form-control" id="addLoaiGiamGia" name="loaiGiamGia">
							<option value="Phần trăm"
								${addFormData != null && addFormData.loaiGiamGia == 'Phần trăm' ? 'selected' : ''}>Phần
								trăm</option>
							<option value="Số tiền cố định"
								${addFormData != null && addFormData.loaiGiamGia == 'Số tiền cố định' ? 'selected' : ''}>Số
								tiền cố định</option>
						</select>
					</div>
				</div>
				<div class="col-md-6">
					<div class="form-group">
						<label for="addGiaTriGiam">Giá Trị Giảm</label> <input
							type="number" class="form-control" id="addGiaTriGiam"
							name="giaTriGiam" step="0.01" min="0" placeholder="VD: 20.00"
							value="${addFormData != null ? addFormData.giaTriGiam : ''}">
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-md-6">
					<div class="form-group">
						<label for="addNgayBatDau">Ngày Bắt Đầu</label> <input type="date"
							class="form-control" id="addNgayBatDau" name="ngayBatDau"
							<c:if test="${addFormData != null && addFormData.ngayBatDau != null}">value="<fmt:formatDate value='${addFormData.ngayBatDau}' pattern='yyyy-MM-dd'/>"</c:if>>
					</div>
				</div>
				<div class="col-md-6">
					<div class="form-group">
						<label for="addNgayKetThuc">Ngày Kết Thúc</label> <input
							type="date" class="form-control" id="addNgayKetThuc"
							name="ngayKetThuc"
							<c:if test="${addFormData != null && addFormData.ngayKetThuc != null}">value="<fmt:formatDate value='${addFormData.ngayKetThuc}' pattern='yyyy-MM-dd'/>"</c:if>>
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
<div class="header">
	<h2>Danh Sách Khuyến Mãi</h2>
	<div class="add-btn-container">
		<button class="custom-btn" onclick="showAddModal()">Thêm
			Khuyến Mãi</button>
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
				<th>Hành Động</th>
			</tr>
		</thead>
		<tbody id="promotionList">
			<c:choose>
				<c:when test="${empty khuyenMaiList}">
					<tr class="no-data">
						<td colspan="8" nowrap="true">Không có dữ liệu</td>
					</tr>
				</c:when>
				<c:otherwise>
					<c:forEach var="km" items="${khuyenMaiList}">
						<tr data-promotion-id="${km.maKhuyenMai}">
							<td data-field="maKM">${km.maKhuyenMai}</td>
							<td data-field="maCode">${km.maCode}</td>
							<td data-field="moTa"><span class="description-short"></span>
								<span class="description-full" style="display: none;">${km.moTa}</span>
								<c:if test="${fn:length(km.moTa) > 50}">
									<span class="view-more" onclick="showDescriptionModal(this)">Xem
										thêm</span>
								</c:if></td>
							<td data-field="loaiGiamGia">${km.loaiGiamGia}</td>
							<td data-field="giaTriGiam">${km.giaTriGiam}</td>
							<td data-field="ngayBatDau"
								data-value="<fmt:formatDate value='${km.ngayBatDau}' pattern='yyyy-MM-dd'/>">
								<fmt:formatDate value="${km.ngayBatDau}" pattern="dd/MM/yyyy" />
							</td>
							<td data-field="ngayKetThuc"
								data-value="<fmt:formatDate value='${km.ngayKetThuc}' pattern='yyyy-MM-dd'/>">
								<fmt:formatDate value="${km.ngayKetThuc}" pattern="dd/MM/yyyy" />
							</td>
							<td>
								<button class="custom-btn btn-sm mr-1"
									onclick="showEditModal(this)">Sửa</button> <a
								href="${pageContext.request.contextPath}/admin/promotions/delete/${km.maKhuyenMai}"
								class="custom-btn btn-sm"
								onclick="return confirm('Bạn có chắc chắn muốn xóa khuyến mãi ${km.maCode} không?');">Xóa</a>
							</td>
						</tr>
					</c:forEach>
				</c:otherwise>
			</c:choose>
		</tbody>
	</table>
</div>

<!-- Pagination -->
<div class="pagination">
	<c:if test="${totalPages > 1}">
		<c:if test="${currentPage > 1}">
			<li class="page-item"><a class="page-link"
				href="?page=${currentPage - 1}">Trước</a></li>
		</c:if>
		<c:forEach var="i" items="${pageRange}">
			<li class="page-item ${i == currentPage ? 'active' : ''}"><a
				class="page-link" href="?page=${i}">${i}</a></li>
		</c:forEach>
		<c:if test="${currentPage < totalPages}">
			<li class="page-item"><a class="page-link"
				href="?page=${currentPage + 1}">Sau</a></li>
		</c:if>
	</c:if>
</div>

<!-- Modal Sửa -->
<div class="modal" id="editModal" style="display: none;">
	<div class="modal-content">
		<h3>Sửa Thông Tin Khuyến Mãi</h3>
		<form id="editForm"
			action="${pageContext.request.contextPath}/admin/promotions/update"
			method="post" onsubmit="return validateEditForm(this)">
			<div class="detail-field">
				<label for="editMaKM">Mã KM</label> <input type="text" id="editMaKM"
					name="maKhuyenMai" class="form-control" readonly>
			</div>
			<div class="detail-field">
				<label for="editMaCode">Mã Code</label> <input type="text"
					id="editMaCode" name="maCode" class="form-control">
			</div>
			<div class="detail-field">
				<label for="editMoTa">Mô Tả</label>
				<textarea id="editMoTa" name="moTa" class="form-control" rows="3"></textarea>
			</div>
			<div class="detail-field">
				<label for="editLoaiGiamGia">Loại Giảm Giá</label> <select
					id="editLoaiGiamGia" name="loaiGiamGia" class="form-control">
					<option value="Phần trăm">Phần trăm</option>
					<option value="Số tiền cố định">Số tiền cố định</option>
				</select>
			</div>
			<div class="detail-field">
				<label for="editGiaTriGiam">Giá Trị Giảm</label> <input
					type="number" id="editGiaTriGiam" name="giaTriGiam"
					class="form-control" step="0.01" min="0">
			</div>
			<div class="detail-field">
				<label for="editNgayBatDau">Ngày Bắt Đầu</label> <input type="date"
					id="editNgayBatDau" name="ngayBatDau" class="form-control">
			</div>
			<div class="detail-field">
				<label for="editNgayKetThuc">Ngày Kết Thúc</label> <input
					type="date" id="editNgayKetThuc" name="ngayKetThuc"
					class="form-control">
			</div>
			<div class="modal-actions">
				<button type="submit" class="custom-btn">Lưu</button>
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
    document.addEventListener('DOMContentLoaded', function() {
        updatePromotionList();

        // Ẩn thông báo sau 5 giây
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

        // Hiển thị modal thêm nếu có lỗi từ server
        <c:if test="${not empty addFormData}">
            document.getElementById('addModal').style.display = 'flex';
            document.getElementById('addMaKM').value = '${newMaKhuyenMai}';
            document.getElementById('addMaCode').value = '${addFormData.maCode}';
            document.getElementById('addMoTa').value = '${addFormData.moTa}';
            document.getElementById('addLoaiGiamGia').value = '${addFormData.loaiGiamGia}';
            document.getElementById('addGiaTriGiam').value = '${addFormData.giaTriGiam}';
            <c:if test="${addFormData.ngayBatDau != null}">
                document.getElementById('addNgayBatDau').value = '<fmt:formatDate value="${addFormData.ngayBatDau}" pattern="yyyy-MM-dd"/>';
            </c:if>
            <c:if test="${addFormData.ngayKetThuc != null}">
                document.getElementById('addNgayKetThuc').value = '<fmt:formatDate value="${addFormData.ngayKetThuc}" pattern="yyyy-MM-dd"/>';
            </c:if>
        </c:if>

        // Hiển thị modal sửa nếu có lỗi từ server
        <c:if test="${not empty editFormData}">
        document.getElementById('editMaKM').value = '${editFormData.maKhuyenMai}';
        document.getElementById('editMaCode').value = '${editFormData.maCode}';
        document.getElementById('editMoTa').value = '${editFormData.moTa}';
        document.getElementById('editLoaiGiamGia').value = '${editFormData.loaiGiamGia}';
        <c:choose>
            <c:when test="${editFormData.giaTriGiam != null}">
                document.getElementById('editGiaTriGiam').value = '${editFormData.giaTriGiam}';
            </c:when>
            <c:otherwise>
                document.getElementById('editGiaTriGiam').value = '';
            </c:otherwise>
        </c:choose>
        <c:if test="${editFormData.ngayBatDau != null}">
            document.getElementById('editNgayBatDau').value = '<fmt:formatDate value="${editFormData.ngayBatDau}" pattern="yyyy-MM-dd"/>';
        </c:if>
        <c:if test="${editFormData.ngayKetThuc != null}">
            document.getElementById('editNgayKetThuc').value = '<fmt:formatDate value="${editFormData.ngayKetThuc}" pattern="yyyy-MM-dd"/>';
        </c:if>
    </c:if>
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
            newRow.innerHTML = `<td colspan="8" nowrap="true">Không có dữ liệu</td>`;
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
        console.log("Row data:", {
            maKM: row.cells[0].textContent,
            maCode: row.cells[1].textContent,
            moTa: row.cells[2].querySelector('.description-full').textContent,
            loaiGiamGia: row.cells[3].textContent,
            giaTriGiam: row.cells[4].textContent,
            ngayBatDau: row.cells[5].getAttribute('data-value'),
            ngayKetThuc: row.cells[6].getAttribute('data-value')
        });

        document.getElementById('editMaKM').value = row.cells[0].textContent;
        document.getElementById('editMaCode').value = row.cells[1].textContent;
        document.getElementById('editMoTa').value = row.cells[2].querySelector('.description-full').textContent;
        document.getElementById('editLoaiGiamGia').value = row.cells[3].textContent;

        const giaTriGiamText = row.cells[4].textContent.trim();
        const giaTriGiam = parseFloat(giaTriGiamText);
        if (!isNaN(giaTriGiam) && giaTriGiamText !== '') {
            document.getElementById('editGiaTriGiam').value = giaTriGiam;
        } else {
            console.error("Invalid giaTriGiam value: ", giaTriGiamText);
            document.getElementById('editGiaTriGiam').value = '';
            alert("Giá trị giảm trong bảng không hợp lệ. Vui lòng kiểm tra dữ liệu.");
        }

        document.getElementById('editNgayBatDau').value = row.cells[5].getAttribute('data-value') || '';
        document.getElementById('editNgayKetThuc').value = row.cells[6].getAttribute('data-value') || '';

        document.getElementById('editModal').style.display = 'flex';
    }

    function closeModal() {
        document.getElementById('editModal').style.display = 'none';
    }

    function showAddModal() {
        document.getElementById('addForm').reset();
        document.getElementById('addMaKM').value = '${newMaKhuyenMai}';
        document.getElementById('addModal').style.display = 'flex';
    }

    function closeAddModal() {
        document.getElementById('addModal').style.display = 'none';
    }

    function validateAddForm(form) {
        const errors = [];
        const maCode = form.querySelector('#addMaCode').value.trim();
        const moTa = form.querySelector('#addMoTa').value.trim();
        const giaTriGiam = parseFloat(form.querySelector('#addGiaTriGiam').value);
        const ngayBatDau = form.querySelector('#addNgayBatDau').value;
        const ngayKetThuc = form.querySelector('#addNgayKetThuc').value;

        if (!maCode) {
            errors.push("Mã code không được để trống!");
        }
        if (!moTa) {
            errors.push("Mô tả không được để trống!");
        }
        if (isNaN(giaTriGiam) || giaTriGiam <= 0) {
            errors.push("Giá trị giảm phải là số dương!");
        }
        if (giaTriGiam > 100 && form.querySelector('#addLoaiGiamGia').value === 'Phần trăm') {
            errors.push("Giá trị phần trăm giảm không được lớn hơn 100!");
        }
        if (!ngayBatDau) {
            errors.push("Ngày bắt đầu không được để trống!");
        }
        if (!ngayKetThuc) {
            errors.push("Ngày kết thúc không được để trống!");
        }
        if (ngayBatDau && ngayKetThuc) {
            const startDate = new Date(ngayBatDau);
            const endDate = new Date(ngayKetThuc);
            const today = new Date();
            today.setHours(0, 0, 0, 0);
            if (startDate.getTime() === endDate.getTime()) {
                errors.push("Ngày bắt đầu không được trùng ngày kết thúc!");
            }
            if (startDate < today) {
                errors.push("Ngày bắt đầu không được là quá khứ!");
            }
            if (endDate < startDate) {
                errors.push("Ngày kết thúc phải sau ngày bắt đầu!");
            }
        }

        if (errors.length > 0) {
            alert(errors.join("\n"));
            return false;
        }
        return true;
    }

    function validateEditForm(form) {
        const errors = [];
        const maCode = form.querySelector('#editMaCode').value.trim();
        const moTa = form.querySelector('#editMoTa').value.trim();
        const giaTriGiam = parseFloat(form.querySelector('#editGiaTriGiam').value);
        const ngayBatDau = form.querySelector('#editNgayBatDau').value;
        const ngayKetThuc = form.querySelector('#editNgayKetThuc').value;

        if (!maCode) {
            errors.push("Mã code không được để trống!");
        }
        if (!moTa) {
            errors.push("Mô tả không được để trống!");
        }
        if (isNaN(giaTriGiam) || giaTriGiam <= 0) {
            errors.push("Giá trị giảm phải là số dương!");
        }
        if (giaTriGiam > 100 && form.querySelector('#editLoaiGiamGia').value === 'Phần trăm') {
            errors.push("Giá trị phần trăm giảm không được lớn hơn 100!");
        }
        if (!ngayBatDau) {
            errors.push("Ngày bắt đầu không được để trống!");
        }
        if (!ngayKetThuc) {
            errors.push("Ngày kết thúc không được để trống!");
        }
        if (ngayBatDau && ngayKetThuc) {
            const startDate = new Date(ngayBatDau);
            const endDate = new Date(ngayKetThuc);
            const today = new Date();
            today.setHours(0, 0, 0, 0);
            if (startDate.getTime() === endDate.getTime()) {
                errors.push("Ngày bắt đầu không được trùng ngày kết thúc!");
            }
            if (endDate < startDate) {
                errors.push("Ngày kết thúc phải sau ngày bắt đầu!");
            }
            if (startDate < today) {
                errors.push("Ngày bắt đầu không được là quá khứ!");
            }
        }

        if (errors.length > 0) {
            alert(errors.join("\n"));
            return false;
        }
        return true;
    }
</script>