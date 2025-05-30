<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!-- Modal Thêm Món Ăn -->
<div class="modal" id="addModal" style="display: none;">
	<div class="modal-content">
		<h3>Thêm Bắp Nước / Combo</h3>
		<form id="addForm"
			action="${pageContext.request.contextPath}/admin/food-combo/add"
			method="post" enctype="multipart/form-data">
			<div class="row">
				<div class="col-md-6">
					<div class="form-group">
						<label for="addLoai">Loại</label> <select class="form-control"
							id="addLoai" name="loai" onchange="toggleAddFields()">
							<option value="Bắp Nước"
								${addFormData != null && addFormData.loai == 'Bắp Nước' ? 'selected' : ''}>Bắp
								Nước</option>
							<option value="Combo"
								${addFormData != null && addFormData.loai == 'Combo' ? 'selected' : ''}>Combo</option>
						</select>
					</div>
					<div class="form-group">
						<label for="addMa">Mã</label> <input type="text"
							class="form-control" id="addMa" name="ma"
							value="${fn:escapeXml(newMaMap[addFormData != null ? addFormData.loai : 'Bắp Nước'])}"
							readonly>
					</div>
					<div class="form-group">
						<label for="addTen">Tên</label> <input type="text"
							class="form-control" id="addTen" name="ten"
							value="${fn:escapeXml(addFormData != null ? addFormData.ten : '')}"
							placeholder="VD: Bắp Rang Lớn" required>
					</div>
					<div class="form-group">
						<label for="addGia">Giá (VNĐ)</label> <input type="text"
							class="form-control" id="addGia" name="gia"
							value="${addFormData != null ? addFormData.gia : ''}"
							placeholder="VD: 50000" required>
					</div>
					<input type="hidden" id="bapNuocHidden" name="bapNuocHidden">
				</div>
				<div class="col-md-6">
					<div class="form-group" id="addMoTaField" style="display: none;">
						<label for="addMoTa">Mô Tả</label>
						<textarea class="form-control" id="addMoTa" name="moTa" rows="3">${fn:escapeXml(addFormData != null ? addFormData.moTa : '')}</textarea>
					</div>
					<div class="form-group" id="addComboItemsField"
						style="display: none;">
						<label>Danh sách Bắp Nước trong Combo</label>
						<div id="addBapNuocContainer">
							<c:forEach var="bapNuoc" items="${allBapNuocList}">
								<div class="form-check">
									<input type="checkbox"
										class="form-check-input combo-item-checkbox"
										id="addCb_${bapNuoc.maBapNuoc}" name="bapNuocIds"
										value="${bapNuoc.maBapNuoc}">
									<c:if
										test="${addFormData != null && addFormData.bapNuocIds != null}">
										<c:forEach var="bnId" items="${addFormData.bapNuocIds}">
											<c:if test="${bnId == bapNuoc.maBapNuoc}">checked</c:if>
										</c:forEach>
									</c:if>
									<label class="form-check-label"
										for="addCb_${bapNuoc.maBapNuoc}">
										${bapNuoc.tenBapNuoc} (${bapNuoc.maBapNuoc}) </label> <input
										type="number" class="form-control"
										name="soLuong_${bapNuoc.maBapNuoc}"
										value="${addFormData != null && addFormData.soLuongs != null ? addFormData.soLuongs[addFormData.bapNuocIds.indexOf(bapNuoc.maBapNuoc)] : '1'}"
										min="1"
										style="width: 60px; display: inline-block; margin-left: 10px;">
								</div>
							</c:forEach>
						</div>
					</div>
					<div class="form-group">
						<label for="addHinhAnh">Hình Ảnh</label> <input type="file"
							class="form-control" id="addHinhAnh" name="hinhAnh"
							accept="image/jpeg,image/png" onchange="validateFile(this)">
						<small class="form-text text-muted">Chọn file hình ảnh
							(jpg, png, tối đa 5MB).</small>
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

<!-- Modal Sửa Món Ăn -->
<div class="modal" id="editModal" style="display: none;">
	<div class="modal-content">
		<h3>Sửa Thông Tin</h3>
		<form id="editForm"
			action="${pageContext.request.contextPath}/admin/food-combo/edit"
			method="post" enctype="multipart/form-data">
			<div class="row">
				<div class="col-md-6">
					<div class="form-group">
						<label for="editMa">Mã</label> <input type="text"
							class="form-control" id="editMa"
							value="${fn:escapeXml(editLoai == 'Bắp Nước' ? editItem.maBapNuoc : editItem.maCombo)}"
							readonly> <input type="hidden" name="ma"
							value="${fn:escapeXml(editLoai == 'Bắp Nước' ? editItem.maBapNuoc : editItem.maCombo)}">
						<input type="hidden" name="loai" value="${fn:escapeXml(editLoai)}">
					</div>
					<div class="form-group">
						<label for="editTen">Tên</label> <input type="text"
							class="form-control" id="editTen" name="ten"
							value="${fn:escapeXml(editLoai == 'Bắp Nước' ? editItem.tenBapNuoc : editItem.tenCombo)}"
							required>
					</div>
					<div class="form-group">
						<label for="editGia">Giá (VNĐ)</label> <input type="text"
							class="form-control" id="editGia" name="gia"
							value="<fmt:formatNumber value='${editLoai == "Bắp Nước" ? editItem.giaBapNuoc : editItem.giaCombo}' type='number' groupingUsed='true' minFractionDigits='0' maxFractionDigits='0'/>"
							required> <input type="hidden" id="editGiaRaw" name="gia">
					</div>
					<input type="hidden" id="editBapNuocHidden" name="bapNuocHidden">
				</div>
				<div class="col-md-6">
					<c:if test="${editLoai == 'Combo'}">
						<div class="form-group" id="editMoTaField">
							<label for="editMoTa">Mô Tả</label>
							<textarea class="form-control" id="editMoTa" name="moTa" rows="3">${fn:escapeXml(editItem.moTa)}</textarea>
						</div>
						<div class="form-group" id="editComboItemsField">
							<label>Danh sách Bắp Nước trong Combo</label>
							<div id="editBapNuocContainer">
								<c:forEach var="bapNuoc" items="${allBapNuocList}">
									<div class="form-check">
										<input type="checkbox"
											class="form-check-input combo-item-checkbox"
											id="editCb_${bapNuoc.maBapNuoc}" name="bapNuocIds"
											value="${bapNuoc.maBapNuoc}">
										<c:if test="${not empty editItem.chiTietCombos}">
											<c:forEach var="ct" items="${editItem.chiTietCombos}">
												<c:if test="${ct.maBapNuoc == bapNuoc.maBapNuoc}">checked</c:if>
											</c:forEach>
										</c:if>
										<label class="form-check-label"
											for="editCb_${bapNuoc.maBapNuoc}">
											${bapNuoc.tenBapNuoc} (${bapNuoc.maBapNuoc}) </label> <input
											type="number" class="form-control"
											name="soLuong_${bapNuoc.maBapNuoc}"
											value="<c:if test='${not empty editItem.chiTietCombos}'><c:forEach var='ct' items='${editItem.chiTietCombos}'><c:if test='${ct.maBapNuoc == bapNuoc.maBapNuoc}'>${ct.soLuong}</c:if></c:forEach></c:if><c:if test='${empty editItem.chiTietCombos}'>1</c:if>"
											min="1"
											style="width: 60px; display: inline-block; margin-left: 10px;">
									</div>
								</c:forEach>
							</div>
						</div>
					</c:if>
					<div class="form-group">
						<label for="editHinhAnh">Hình Ảnh</label>
						<div class="image-view">
							<c:if test="${not empty editItem.urlHinhAnh}">
							    <c:set var="imagePath" value="${fn:replace(editItem.urlHinhAnh, 'resources/images/', '')}" />
							    <img id="editHinhAnhPreview"
							         src="${pageContext.request.contextPath}/resources/images/${imagePath}"
							         alt="Hình ảnh hiện tại"
							         style="max-width: 100px; max-height: 100px;">
							</c:if>
							<c:if test="${empty editItem.urlHinhAnh}">
								<p>Chưa có hình ảnh.</p>
							</c:if>
						</div>
						<div class="image-edit">
							<input type="file" class="form-control" id="editHinhAnh"
								name="hinhAnh" accept="image/jpeg,image/png"
								onchange="validateFile(this)"> <small
								class="form-text text-muted">Chọn file hình ảnh mới
								(jpg, png, tối đa 5MB). Để trống để giữ hình ảnh hiện tại.</small>
						</div>
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

<!-- Header và Filter -->
<div class="header">
	<h2>Quản Lý Bắp Nước & Combo</h2>
	<div class="add-btn-container">
		<button class="custom-btn" onclick="showAddModal()">Thêm Mới</button>
	</div>
</div>
<div class="flex-column col-2 filter-section mb-3">
	<div class="form-group">
		<label for="filterLoai">Lọc theo loại:</label> <select
			class="form-control" id="filterLoai" onchange="filterTable()">
			<option value="all">Tất cả</option>
			<option value="Bắp Nước">Bắp Nước</option>
			<option value="Combo">Combo</option>
		</select>
	</div>
</div>

<!-- Error and Success Messages -->
<c:if test="${not empty error}">
	<div class="alert alert-danger" id="errorMessage">${error}</div>
</c:if>
<c:if test="${not empty success}">
	<div class="alert alert-success" id="successMessage">${success}</div>
</c:if>

<!-- Bảng dữ liệu -->
<div class="table-responsive">
	<table class="table table-bordered table-striped">
		<thead>
			<tr>
				<th>Mã</th>
				<th>Tên</th>
				<th>Giá (VNĐ)</th>
				<th>Mô Tả</th>
				<th>Hình Ảnh</th>
				<th>Loại</th>
				<th>Hành Động</th>
			</tr>
		</thead>
		<tbody id="foodComboList">
			<c:choose>
				<c:when test="${empty bapNuocList and empty comboList}">
					<tr class="no-data">
						<td colspan="7" class="no-data text-center">Không có dữ liệu</td>
					</tr>
				</c:when>
				<c:otherwise>
					<c:forEach var="item" items="${bapNuocList}">
						<tr data-loai="Bắp Nước">
							<td>${item.maBapNuoc}</td>
							<td>${item.tenBapNuoc}</td>
							<td class="currency"><fmt:formatNumber
									value="${item.giaBapNuoc}" type="number" groupingUsed="true"
									minFractionDigits="0" maxFractionDigits="0" />đ</td>
							<td>Không có mô tả</td>
							<td>
							    <c:if test="${not empty item.urlHinhAnh}">
							        <c:set var="imagePath" value="${fn:replace(item.urlHinhAnh, 'resources/images/', '')}" />
							        <img src="${pageContext.request.contextPath}/resources/images/${imagePath}"
							             alt="${item.tenBapNuoc != null ? item.tenBapNuoc : item.tenCombo}"
							             style="max-width: 50px; max-height: 50px;"
							             onerror="this.src='${pageContext.request.contextPath}/resources/images/default-poster.jpg';" />
							    </c:if>
							    <c:if test="${empty item.urlHinhAnh}">
							        Chưa có hình ảnh
							    </c:if>
							</td>
							<td>Bắp Nước</td>
							<td><a
								href="${pageContext.request.contextPath}/admin/food-combo?editMa=${item.maBapNuoc}&editLoai=Bắp Nước"
								class="custom-btn btn-sm mr-1">Sửa</a>
								<form
									action="${pageContext.request.contextPath}/admin/food-combo/delete"
									method="post" style="display: inline;">
									<input type="hidden" name="ma" value="${item.maBapNuoc}">
									<input type="hidden" name="loai" value="Bắp Nước">
									<button type="submit" class="custom-btn btn-sm"
										onclick="return confirm('Bạn có chắc muốn xóa mục này không?')">Xóa</button>
								</form></td>
						</tr>
					</c:forEach>
					<c:forEach var="item" items="${comboList}">
					    <tr data-loai="Combo">
					        <td>${item.maCombo}</td>
					        <td>${item.tenCombo}</td>
					        <td class="currency"><fmt:formatNumber
					                value="${item.giaCombo}" type="number" groupingUsed="true"
					                minFractionDigits="0" maxFractionDigits="0" />đ</td>
					        <td>${item.moTa}</td>
					        <td>
					            <c:if test="${not empty item.urlHinhAnh}">
					                <c:set var="imagePath" value="${fn:replace(item.urlHinhAnh, 'resources/images/', '')}" />
					                <img src="${pageContext.request.contextPath}/resources/images/${imagePath}"
					                     alt="${item.tenCombo}"
					                     style="max-width: 50px; max-height: 50px;"
					                     onerror="this.src='${pageContext.request.contextPath}/resources/images/default-poster.jpg';" />
					            </c:if>
					            <c:if test="${empty item.urlHinhAnh}">
					                Chưa có hình ảnh
					            </c:if>
					        </td>
					        <td>Combo</td>
					        <td>
					            <a href="${pageContext.request.contextPath}/admin/food-combo?editMa=${item.maCombo}&editLoai=Combo"
					               class="custom-btn btn-sm mr-1">Sửa</a>
					            <form action="${pageContext.request.contextPath}/admin/food-combo/delete"
					                  method="post" style="display: inline;">
					                <input type="hidden" name="ma" value="${item.maCombo}">
					                <input type="hidden" name="loai" value="Combo">
					                <button type="submit" class="custom-btn btn-sm"
					                        onclick="return confirm('Bạn có chắc muốn xóa mục này không?')">Xóa</button>
					            </form>
					        </td>
					    </tr>
					</c:forEach>
				</c:otherwise>
			</c:choose>
		</tbody>
	</table>
</div>

<!-- JavaScript -->
<script
	src="${pageContext.request.contextPath}/resources/admin/js/number-utils.js"></script>
<script
	src="${pageContext.request.contextPath}/resources/admin/js/currency-utils.js"></script>
<script>
function isValidNumber(value) {
    return /^\d*\.?\d*$/.test(value.replace(/[^0-9.]/g, ''));
}

function validateFile(input) {
    if (!input || !input.files) {
        alert('Không thể truy cập file hình ảnh!');
        return false;
    }
    const file = input.files[0];
    if (file) {
        const validTypes = ['image/jpeg', 'image/png'];
        if (!validTypes.includes(file.type)) {
            alert('Vui lòng chọn file hình ảnh (jpg hoặc png)!');
            input.value = '';
            return false;
        } else if (file.size > 5 * 1024 * 1024) {
            alert('Kích thước file không được vượt quá 5MB!');
            input.value = '';
            return false;
        }
    }
    return true;
}

function toggleAddFields() {
    const loai = document.getElementById('addLoai').value;
    const isCombo = loai === 'Combo';
    document.getElementById('addMoTaField').style.display = isCombo ? 'block' : 'none';
    document.getElementById('addComboItemsField').style.display = isCombo ? 'block' : 'none';
    if (!isCombo) {
        document.querySelectorAll('#addComboItemsField .combo-item-checkbox').forEach(cb => {
            cb.checked = false;
        });
    }
    const newMaMap = {
        "Bắp Nước": "${fn:escapeXml(newMaMap['Bắp Nước'])}",
        "Combo": "${fn:escapeXml(newMaMap['Combo'])}"
    };
    document.getElementById('addMa').value = newMaMap[loai];
}

function updateHiddenInput(containerId) {
    const container = document.getElementById(containerId);
    if (!container) return;

    const checkboxes = container.querySelectorAll('.combo-item-checkbox:checked');
    const bapNuocData = [];
    checkboxes.forEach(cb => {
        const maBapNuoc = cb.value;
        const soLuongInput = container.querySelector(`input[name="soLuong_${maBapNuoc}"]`);
        const soLuong = soLuongInput ? parseInt(soLuongInput.value) : 1;
        bapNuocData.push(`${maBapNuoc}:${soLuong}`);
    });

    const hiddenInput = containerId === 'addBapNuocContainer' ? document.getElementById('bapNuocHidden') : document.getElementById('editBapNuocHidden');
    hiddenInput.value = bapNuocData.join(',');
}

function validateAddForm(form) {
    const errors = [];
    const loai = form.querySelector('#addLoai').value;
    const ten = form.querySelector('#addTen').value.trim();
    const giaRaw = form.querySelector('#addGiaRaw').value.trim();
    const hinhAnh = form.querySelector('#addHinhAnh').files[0];
    const bapNuocHidden = form.querySelector('#bapNuocHidden').value.trim();

    if (!ten) errors.push("Tên không được để trống.");
    if (!giaRaw) errors.push("Giá không được để trống.");
    if (!hinhAnh) errors.push("Hình ảnh không được để trống.");
    if (loai === 'Combo' && !bapNuocHidden) errors.push("Danh sách bắp nước không được để trống.");

    if (errors.length > 0) {
        alert(errors.join("\n"));
        return false;
    }
    return true;
}

function validateEditForm(form) {
    const errors = [];
    const loaiInput = form.querySelector('input[name="loai"]');
    let loai = loaiInput ? loaiInput.value.trim() : '';
    const maInput = form.querySelector('input[name="ma"]');
    const ma = maInput ? maInput.value.trim() : '';
    const ten = form.querySelector('#editTen').value.trim();
    const giaRaw = form.querySelector('#editGiaRaw').value.trim();
    const bapNuocHidden = form.querySelector('#editBapNuocHidden').value.trim();

    if (!ma) errors.push("Mã không được để trống.");
    if (!loai) {
        loai = document.getElementById('editModal').dataset.loai || '${fn:escapeXml(editLoai != null ? editLoai : "Bắp Nước")}';
        if (loaiInput) loaiInput.value = loai;
    }
    if (!ten) errors.push("Tên không được để trống.");
    if (!giaRaw) errors.push("Giá không được để trống.");
    if (loai === 'Combo' && !bapNuocHidden) errors.push("Danh sách bắp nước không được để trống.");

    if (errors.length > 0) {
        alert(errors.join("\n"));
        return false;
    }
    return true;
}

function showAddModal() {
    document.getElementById('addForm').reset();
    document.getElementById('addLoai').value = 'Bắp Nước';
    document.getElementById('addMa').value = '${fn:escapeXml(newMaMap["Bắp Nước"])}';
    toggleAddFields();
    document.getElementById('addModal').style.display = 'flex';
}

function closeAddModal() {
    document.getElementById('addModal').style.display = 'none';
}

function closeEditModal() {
    document.getElementById('editModal').style.display = 'none';
    window.history.pushState({}, document.title, "${pageContext.request.contextPath}/admin/food-combo");
}

function filterTable() {
    const filterValue = document.getElementById('filterLoai').value.toLowerCase();
    const rows = document.querySelectorAll('#foodComboList tr');
    let hasVisibleRows = false;
    const noDataRow = document.querySelector('#foodComboList .no-data');

    rows.forEach(row => {
        if (row.classList.contains('no-data')) return;

        const loai = (row.dataset.loai || '').toLowerCase();
        const shouldDisplay = filterValue === 'all' || loai === filterValue;
        row.style.display = shouldDisplay ? '' : 'none';
        if (shouldDisplay) hasVisibleRows = true;
    });

    if (noDataRow) {
        noDataRow.style.display = hasVisibleRows ? 'none' : '';
    }
}

document.addEventListener("DOMContentLoaded", function () {
    const addGiaInput = document.getElementById('addGia');
    if (addGiaInput) {
        addGiaInput.addEventListener('input', function (e) {
            let rawValue = e.target.value;
            let cleaned = rawValue.replace(/[^0-9.]/g, '').replace(/(\..*)\./g, '$1');
            if (cleaned && isValidNumber(cleaned)) {
                const parsed = parseFloat(cleaned);
                if (!isNaN(parsed) && parsed >= 0) {
                    const formatted = formatCurrencyWithDecimal(parsed);
                    e.target.value = formatted;
                    document.getElementById('addGiaRaw').value = parsed.toString(); // Cập nhật giá trị thô
                } else {
                    e.target.value = '';
                    document.getElementById('addGiaRaw').value = '';
                }
            } else {
                e.target.value = '';
                document.getElementById('addGiaRaw').value = '';
            }
        });
    }

    const editGiaInput = document.getElementById('editGia');
    if (editGiaInput) {
        editGiaInput.addEventListener('input', function (e) {
            let rawValue = e.target.value;
            let cleaned = rawValue.replace(/[^0-9.]/g, '').replace(/(\..*)\./g, '$1');
            if (cleaned && isValidNumber(cleaned)) {
                const parsed = parseFloat(cleaned);
                if (!isNaN(parsed) && parsed >= 0) {
                    const formatted = formatCurrencyWithDecimal(parsed);
                    e.target.value = formatted;
                    document.getElementById('editGiaRaw').value = parsed.toString(); // Cập nhật giá trị thô
                } else {
                    e.target.value = '';
                    document.getElementById('editGiaRaw').value = '';
                }
            } else {
                e.target.value = '';
                document.getElementById('editGiaRaw').value = '';
            }
        });

        // Khởi tạo giá trị ban đầu từ giá trị đã format
        let initialValue = editGiaInput.value.replace(/[^0-9.]/g, '');
        if (initialValue && isValidNumber(initialValue)) {
            const parsed = parseFloat(initialValue);
            if (!isNaN(parsed) && parsed >= 0) {
                editGiaInput.value = formatCurrencyWithDecimal(parsed);
                document.getElementById('editGiaRaw').value = parsed.toString();
            }
        }
    }

    const addForm = document.getElementById('addForm');
    if (addForm) {
        addForm.addEventListener('submit', function (e) {
            e.preventDefault();
            updateHiddenInput('addBapNuocContainer');
            if (validateAddForm(this)) {
                this.submit();
            }
        });
    }

    const editForm = document.getElementById('editForm');
    if (editForm) {
        editForm.addEventListener('submit', function (e) {
            e.preventDefault();
            updateHiddenInput('editBapNuocContainer');
            if (validateEditForm(this)) {
                this.submit();
            }
        });
    }

    filterTable();

    <c:if test="${showEditModal}">
        document.getElementById('editModal').style.display = 'flex';
        document.getElementById('editModal').dataset.loai = '${fn:escapeXml(editLoai)}';
        <c:if test="${editLoai == 'Combo'}">
            document.getElementById('editMoTaField').style.display = 'block';
            document.getElementById('editComboItemsField').style.display = 'block';
        </c:if>
    </c:if>

    <c:if test="${addFormData != null}">
        document.getElementById('addModal').style.display = 'flex';
        toggleAddFields();
    </c:if>

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
});
</script>