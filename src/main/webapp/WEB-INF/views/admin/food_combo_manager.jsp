<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
                        <label for="addLoai">Loại</label>
                        <select class="form-control" id="addLoai" name="loai" onchange="toggleAddFields()">
                            <option value="Bắp Nước" ${addFormData != null && addFormData.loai == 'Bắp Nước' ? 'selected' : ''}>Bắp Nước</option>
                            <option value="Combo" ${addFormData != null && addFormData.loai == 'Combo' ? 'selected' : ''}>Combo</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="addMa">Mã</label>
                        <input type="text" class="form-control" id="addMa" name="ma"
                            value="${fn:escapeXml(newMaMap[addFormData != null ? addFormData.loai : 'Bắp Nước'])}" readonly>
                    </div>
                    <div class="form-group">
                        <label for="addTen">Tên</label>
                        <input type="text" class="form-control" id="addTen" name="ten"
                            value="${fn:escapeXml(addFormData != null ? addFormData.ten : '')}"
                            placeholder="VD: Bắp Rang Lớn" required>
                    </div>
                    <div class="form-group">
                        <label for="addGia">Giá (VNĐ)</label>
                        <input type="text" class="form-control" id="addGia"
                            value="${addFormData != null ? addFormData.gia : ''}"
                            placeholder="VD: 50000" required>
                        <input type="hidden" id="addGiaRaw" name="gia">
                    </div>
                    <input type="hidden" id="bapNuocHidden" name="bapNuocHidden">
                </div>
                <div class="col-md-6">
                    <div class="form-group" id="addMoTaField" style="display: none;">
                        <label for="addMoTa">Mô Tả</label>
                        <textarea class="form-control" id="addMoTa" name="moTa" rows="3">${fn:escapeXml(addFormData != null ? addFormData.moTa : '')}</textarea>
                    </div>
                    <div class="form-group" id="addComboItemsField" style="display: none;">
                        <label>Danh sách Bắp Nước trong Combo</label>
                        <div id="addBapNuocContainer">
                            <c:forEach var="bapNuoc" items="${allBapNuocList}">
                                <div class="form-check">
                                    <input type="checkbox" class="form-check-input combo-item-checkbox">
                                        id="addCb_${bapNuoc.maBapNuoc}" name="bapNuocIds"
                                        value="${bapNuoc.maBapNuoc}"
                                        <c:if test="${addFormData != null && addFormData.bapNuocIds != null}">
                                            <c:forEach var="bnId" items="${addFormData.bapNuocIds}">
                                                <c:if test="${bnId == bapNuoc.maBapNuoc}">checked</c:if>
                                            </c:forEach>
                                        </c:if>>
                                    <label class="form-check-label" for="addCb_${bapNuoc.maBapNuoc}">
                                        ${bapNuoc.tenBapNuoc} (${bapNuoc.maBapNuoc})
                                    </label>
                                    <input type="number" class="form-control"
                                        name="soLuong_${bapNuoc.maBapNuoc}"
                                        value="${addFormData != null && addFormData.soLuongs != null ? addFormData.soLuongs[addFormData.bapNuocIds.indexOf(bapNuoc.maBapNuoc)] : '1'}"
                                        min="1" style="width: 60px; display: inline-block; margin-left: 10px;">
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="addHinhAnh">Hình Ảnh</label>
                        <input type="file" class="form-control" id="addHinhAnh" name="hinhAnh"
                            accept="image/jpeg,image/png" onchange="validateFile(this)">
                        <small class="form-text text-muted">Chọn file hình ảnh (jpg, png, tối đa 5MB).</small>
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

<!-- Modal Sửa Bắp Nước -->
<c:if test="${showEditModal && editLoai == 'Bắp Nước'}">
<div class="modal" id="editBapNuocModal" style="display: flex;">
    <div class="modal-content">
        <h3>Sửa Bắp Nước</h3>
        <form id="editBapNuocForm"
            action="${pageContext.request.contextPath}/admin/food-combo/edit"
            method="post" enctype="multipart/form-data">
            <div class="row">
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="editBapNuocMa">Mã</label>
                        <input type="text" class="form-control" id="editBapNuocMa"
                            value="${fn:escapeXml(editItem.maBapNuoc)}" readonly>
                        <input type="hidden" name="ma" value="${fn:escapeXml(editItem.maBapNuoc)}">
                        <input type="hidden" name="loai" value="Bắp Nước">
                    </div>
                    <div class="form-group">
                        <label for="editBapNuocTen">Tên</label>
                        <input type="text" class="form-control" id="editBapNuocTen" name="ten"
                            value="${fn:escapeXml(editItem.tenBapNuoc)}" required>
                    </div>
                    <div class="form-group">
                        <label for="editBapNuocGia">Giá (VNĐ)</label>
                        <input type="text" class="form-control" id="editBapNuocGia"
                            value="<fmt:formatNumber value='${editItem.giaBapNuoc}' type='number' groupingUsed='true' minFractionDigits='0' maxFractionDigits='0'/>"
                            required>
                        <input type="hidden" id="editBapNuocGiaRaw" name="gia">
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="editBapNuocHinhAnh">Hình Ảnh</label>
                        <div class="image-view">
                            <c:if test="${not empty editItem.urlHinhAnh}">
                                <c:set var="imagePath" value="${fn:replace(editItem.urlHinhAnh, 'resources/images/', '')}" />
                                <img id="editBapNuocHinhAnhPreview"
                                     src="${pageContext.request.contextPath}/resources/images/${imagePath}"
                                     alt="Hình ảnh hiện tại"
                                     style="max-width: 100px; max-height: 100px;">
                            </c:if>
                            <c:if test="${empty editItem.urlHinhAnh}">
                                <p>Chưa có hình ảnh.</p>
                            </c:if>
                        </div>
                        <div class="image-edit">
                            <input type="file" class="form-control" id="editBapNuocHinhAnh"
                                name="hinhAnh" accept="image/jpeg,image/png"
                                onchange="validateFile(this)">
                            <small class="form-text text-muted">Chọn file hình ảnh mới (jpg, png, tối đa 5MB).</small>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal-actions">
                <button type="submit" class="custom-btn">Lưu</button>
                <button type="button" class="custom-btn" onclick="closeEditBapNuocModal()">Hủy</button>
            </div>
        </form>
    </div>
</div>
</c:if>

<!-- Modal Sửa Combo -->
<c:if test="${showEditModal && editLoai == 'Combo'}">
<div class="modal" id="editComboModal" style="display: flex;">
    <div class="modal-content">
        <h3>Sửa Combo</h3>
        <form id="editComboForm"
            action="${pageContext.request.contextPath}/admin/food-combo/edit"
            method="post" enctype="multipart/form-data">
            <div class="row">
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="editComboMa">Mã</label>
                        <input type="text" class="form-control" id="editComboMa"
                            value="${fn:escapeXml(editItem.maCombo)}" readonly>
                        <input type="hidden" name="ma" value="${fn:escapeXml(editItem.maCombo)}">
                        <input type="hidden" name="loai" value="Combo">
                    </div>
                    <div class="form-group">
                        <label for="editComboTen">Tên</label>
                        <input type="text" class="form-control" id="editComboTen" name="ten"
                            value="${fn:escapeXml(editItem.tenCombo)}" required>
                    </div>
                    <div class="form-group">
                        <label for="editComboGia">Giá (VNĐ)</label>
                        <input type="text" class="form-control" id="editComboGia"
                            value="<fmt:formatNumber value='${editItem.giaCombo}' type='number' groupingUsed='true' minFractionDigits='0' maxFractionDigits='0'/>"
                            required>
                        <input type="hidden" id="editComboGiaRaw" name="gia">
                    </div>
                    <input type="hidden" id="editBapNuocHidden" name="bapNuocHidden" value="">
                </div>
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="editComboMoTa">Mô Tả</label>
                        <textarea class="form-control" id="editComboMoTa" name="moTa" rows="3">${fn:escapeXml(editItem.moTa)}</textarea>
                    </div>
                    <div class="form-group">
                        <label>Danh sách Bắp Nước trong Combo</label>
                        <div id="editBapNuocContainer">
                            <c:forEach var="bapNuoc" items="${allBapNuocList}">
                                <div class="form-check">
                                    <c:set var="isChecked" value="false" />
                                    <c:set var="soLuong" value="1" />
                                    <c:if test="${not empty editItem.chiTietCombos}">
                                        <c:forEach var="ct" items="${editItem.chiTietCombos}">
                                            <c:if test="${ct.maBapNuoc == bapNuoc.maBapNuoc}">
                                                <c:set var="isChecked" value="true" />
                                                <c:set var="soLuong" value="${ct.soLuong}" />
                                            </c:if>
                                        </c:forEach>
                                    </c:if>
                                    
                                    <input type="checkbox" 
                                           class="form-check-input combo-item-checkbox"
                                           id="editCb_${bapNuoc.maBapNuoc}" 
                                           name="bapNuocIds"
                                           value="${bapNuoc.maBapNuoc}" 
                                           ${isChecked ? 'checked' : ''} />
                                    <label class="form-check-label" for="editCb_${bapNuoc.maBapNuoc}">
                                        ${fn:escapeXml(bapNuoc.tenBapNuoc)} (${bapNuoc.maBapNuoc})
                                    </label>
                                    <input type="number" 
                                           class="form-control"
                                           name="soLuong_${bapNuoc.maBapNuoc}" 
                                           value="${soLuong}" 
                                           min="1"
                                           style="width: 60px; display: inline-block; margin-left: 10px;" />
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="editComboHinhAnh">Hình Ảnh</label>
                        <div class="image-view">
                            <c:if test="${not empty editItem.urlHinhAnh}">
                                <c:set var="imagePath" value="${fn:replace(editItem.urlHinhAnh, 'resources/images/', '')}" />
                                <img id="editComboHinhAnhPreview"
                                     src="${pageContext.request.contextPath}/resources/images/${imagePath}"
                                     alt="Hình ảnh hiện tại"
                                     style="max-width: 100px; max-height: 100px;">
                            </c:if>
                            <c:if test="${empty editItem.urlHinhAnh}">
                                <p>Chưa có hình ảnh.</p>
                            </c:if>
                        </div>
                        <div class="image-edit">
                            <input type="file" class="form-control" id="editComboHinhAnh"
                                name="hinhAnh" accept="image/jpeg,image/png"
                                onchange="validateFile(this)">
                            <small class="form-text text-muted">Chọn file hình ảnh mới (jpg, png, tối đa 5MB).</small>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal-actions">
                <button type="submit" class="custom-btn">Lưu</button>
                <button type="button" class="custom-btn" onclick="closeEditComboModal()">Hủy</button>
            </div>
        </form>
    </div>
</div>
</c:if>

<!-- Header và Filter -->
<div class="header">
    <h2>Quản Lý Bắp Nước & Combo</h2>
    <div class="add-btn-container">
        <button class="custom-btn" onclick="showAddModal()">Thêm Mới</button>
    </div>
</div>
<div class="flex-column col-2 filter-section mb-3">
    <div class="form-group">
        <label for="filterLoai">Lọc theo loại:</label>
        <select class="form-control" id="filterLoai" onchange="filterTable()">
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
                                    value="${item.giaBapNuoc}" type="number" groupingUsed='true'
                                    minFractionDigits="0" maxFractionDigits="0" />đ</td>
                            <td>Không có mô tả</td>
                            <td>
                                <c:if test="${not empty item.urlHinhAnh}">
                                    <c:set var="imagePath" value="${fn:replace(item.urlHinhAnh, 'resources/images/', '')}" />
                                    <img src="${pageContext.request.contextPath}/resources/images/${imagePath}"
                                         alt="${item.tenBapNuoc}"
                                         style="max-width: 50px; max-height: 50px;"
                                         onerror="this.src='${pageContext.request.contextPath}/resources/images/default-poster.jpg';" />
                                </c:if>
                                <c:if test="${empty item.urlHinhAnh}">
                                    Chưa có hình ảnh
                                </c:if>
                            </td>
                            <td>Bắp Nước</td>
                            <td>
                                <a href="${pageContext.request.contextPath}/admin/food-combo?editMa=${item.maBapNuoc}&editLoai=Bắp Nước"
                                   class="custom-btn btn-sm mr-1">Sửa</a>
                                <form action="${pageContext.request.contextPath}/admin/food-combo/delete"
                                      method="post" style="display: inline;">
                                    <input type="hidden" name="ma" value="${item.maBapNuoc}">
                                    <input type="hidden" name="loai" value="Bắp Nước">
                                    <button type="submit" class="custom-btn btn-sm"
                                            onclick="return confirm('Bạn có chắc muốn xóa mục này không?')">Xóa</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:forEach var="item" items="${comboList}">
                        <tr data-loai="Combo">
                            <td>${item.maCombo}</td>
                            <td>${item.tenCombo}</td>
                            <td class="currency"><fmt:formatNumber
                                    value="${item.giaCombo}" type="number" groupingUsed='true'
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
<script src="${pageContext.request.contextPath}/resources/admin/js/number-utils.js"></script>
<script src="${pageContext.request.contextPath}/resources/admin/js/currency-utils.js"></script>
<script>
// Hàm format số tiền
function formatCurrency(input) {
    let value = input.value.replace(/[^\d]/g, '');
    const rawInput = input.id.includes('add') ? document.getElementById('addGiaRaw') :
                    input.id.includes('BapNuoc') ? document.getElementById('editBapNuocGiaRaw') :
                    document.getElementById('editComboGiaRaw');
    if (rawInput) rawInput.value = value;
    if (value.length > 0) {
        value = parseInt(value).toLocaleString('vi-VN');
    }
    input.value = value;
}

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

// Hàm cập nhật sử dụng approach mới
function updateHiddenInput(containerId) {
    const container = document.getElementById(containerId);
    if (!container) {
        console.error('Container not found:', containerId);
        return;
    }

    const bapNuocData = [];
    const formChecks = container.querySelectorAll('.form-check');

    formChecks.forEach((formCheck) => {
        const checkbox = formCheck.querySelector('.form-check-input.combo-item-checkbox');
        const numberInput = formCheck.querySelector('input[type="number"]');

        if (!checkbox || !numberInput) {
            console.log('Missing checkbox or number input in form-check');
            return;
        }

        if (checkbox.checked) {
            const maBapNuoc = checkbox.value || checkbox.getAttribute('value');
            const soLuong = parseInt(numberInput.value) || 1;

            if (maBapNuoc && soLuong > 0) {
                bapNuocData.push(maBapNuoc + ':' + soLuong);
            } else {
                console.error('Invalid data: maBapNuoc=', maBapNuoc, 'soLuong=', soLuong);
            }
        }
    });

    const hiddenInputId = containerId === 'addBapNuocContainer' ? 'bapNuocHidden' : 'editBapNuocHidden';
    const hiddenInput = document.getElementById(hiddenInputId);
    if (hiddenInput) {
        hiddenInput.value = bapNuocData.join(',');
        console.log('Updated', hiddenInputId, 'to:', hiddenInput.value);
    } else {
        console.error('Hidden input not found:', hiddenInputId);
    }
}

function debugCheckboxValues() {
    const container = document.getElementById('editBapNuocContainer');
    if (!container) return;
    
    console.log('=== DEBUG CHECKBOX VALUES ===');
    const checkboxes = container.querySelectorAll('.combo-item-checkbox');
    
    checkboxes.forEach((cb, index) => {
        console.log(`Checkbox ${index}:`, {
            id: cb.id,
            value: cb.value,
            valueAttribute: cb.getAttribute('value'),
            checked: cb.checked,
            outerHTML: cb.outerHTML
        });
    });
}

function validateAddForm(form) {
    const errors = [];
    const loai = form.querySelector('#addLoai').value;
    const ten = form.querySelector('#addTen').value.trim();
    const giaRaw = form.querySelector('#addGiaRaw').value.trim();
    const hinhAnh = form.querySelector('#addHinhAnh').files[0];
    
    if (loai === 'Combo') {
        updateHiddenInput('addBapNuocContainer');
        const bapNuocHidden = form.querySelector('#bapNuocHidden')?.value.trim();
        if (!bapNuocHidden) {
            errors.push("Danh sách bắp nước không được để trống.");
        }
    }

    if (!ten) errors.push("Tên không được để trống.");
    if (!giaRaw) errors.push("Giá không được để trống.");
    if (!hinhAnh) errors.push("Hình ảnh không được để trống.");

    if (errors.length > 0) {
        alert(errors.join("\n"));
        return false;
    }
    return true;
}

function validateEditBapNuocForm(form) {
    const errors = [];
    const ma = form.querySelector('input[name="ma"]')?.value.trim() || '';
    const ten = form.querySelector('#editBapNuocTen')?.value.trim() || '';
    const giaRaw = form.querySelector('#editBapNuocGiaRaw')?.value.trim() || '';

    if (!ma) errors.push("Mã không được để trống.");
    if (!ten) errors.push("Tên không được để trống.");
    if (!giaRaw) errors.push("Giá không được để trống.");

    if (errors.length > 0) {
        alert(errors.join("\n"));
        return false;
    }
    return true;
}

function validateEditComboForm(form) {
    const ten = form.querySelector('#editComboTen').value.trim();
    const giaRaw = form.querySelector('#editComboGiaRaw').value;

    if (!ten) {
        alert('Tên combo không được để trống.');
        return false;
    }

    if (!giaRaw || parseInt(giaRaw) <= 0) {
        alert('Giá combo phải lớn hơn 0.');
        return false;
    }

    console.log('=== VALIDATE EDIT COMBO FORM ===');

    // Gọi updateHiddenInput để đảm bảo dữ liệu mới nhất
    updateHiddenInput('editBapNuocContainer');

    // Kiểm tra giá trị của editBapNuocHidden
    const bapNuocHidden = form.querySelector('#editBapNuocHidden').value;
    console.log('bapNuocHidden after update:', bapNuocHidden);

    if (!bapNuocHidden || bapNuocHidden.trim() === '') {
        alert('Vui lòng chọn ít nhất một bắp nước cho combo.');
        return false;
    }

    // Kiểm tra từng cặp maBapNuoc:soLuong
    const pairs = bapNuocHidden.split(',');
    for (let i = 0; i < pairs.length; i++) {
        const pair = pairs[i].trim();
        if (!pair) continue;
        const [maBapNuoc, soLuong] = pair.split(':');
        console.log(`Pair ${i}: ${pair}, maBapNuoc: ${maBapNuoc}, soLuong: ${soLuong}`);
        if (!maBapNuoc || maBapNuoc.trim() === '') {
            alert('Lỗi: Mã bắp nước không hợp lệ trong cặp: ' + pair);
            return false;
        }
        if (!soLuong || isNaN(parseInt(soLuong)) || parseInt(soLuong) <= 0) {
            alert('Lỗi: Số lượng không hợp lệ trong cặp: ' + pair);
            return false;
        }
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

function closeEditBapNuocModal() {
    document.getElementById('editBapNuocModal').style.display = 'none';
    window.history.pushState({}, document.title, "${pageContext.request.contextPath}/admin/food-combo");
}

function closeEditComboModal() {
    document.getElementById('editComboModal').style.display = 'none';
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

document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM loaded');
    const editComboModal = document.getElementById('editComboModal');
    if (editComboModal && editComboModal.style.display === 'flex') {
        console.log('Edit combo modal found, initializing...');
        setTimeout(() => {
            debugCheckboxValues();
            updateHiddenInput('editBapNuocContainer');

            const editCheckboxes = document.querySelectorAll('#editBapNuocContainer .combo-item-checkbox');
            const editNumberInputs = document.querySelectorAll('#editBapNuocContainer input[type="number"]');

            console.log('Adding event listeners to:', editCheckboxes.length, 'checkboxes and', editNumberInputs.length, 'number inputs');

            editCheckboxes.forEach(cb => {
                cb.removeAttribute('onchange');
                cb.addEventListener('change', function() {
                    console.log('Checkbox changed:', this.id, this.checked, 'value:', this.value);
                    updateHiddenInput('editBapNuocContainer');
                });
            });

            editNumberInputs.forEach(input => {
                input.removeAttribute('onchange');
                input.addEventListener('change', function() {
                    console.log('Number input changed:', this.name, this.value);
                    updateHiddenInput('editBapNuocContainer');
                });
            });
        }, 200);
    }

    // Xử lý format giá cho các input
    const giaInputs = [
        document.getElementById('addGia'),
        document.getElementById('editBapNuocGia'),
        document.getElementById('editComboGia')
    ].filter(input => input);
    
    giaInputs.forEach(input => {
        if (input) {
            let value = input.value.replace(/[^\d]/g, '');
            const rawInput = input.id.includes('add') ? document.getElementById('addGiaRaw') :
                            input.id.includes('BapNuoc') ? document.getElementById('editBapNuocGiaRaw') :
                            document.getElementById('editComboGiaRaw');
            if (rawInput) rawInput.value = value;
            if (value.length > 0) {
                value = parseInt(value).toLocaleString('vi-VN');
            }
            input.value = value;
            
            input.addEventListener('input', function() {
                formatCurrency(this);
            });
        }
    });

    // Xử lý sự kiện cho form thêm mới
    const addForm = document.getElementById('addForm');
    if (addForm) {
        addForm.addEventListener('submit', function(e) {
            e.preventDefault();
            if (validateAddForm(this)) {
                this.submit();
            }
        });

        // Thêm event listener cho checkbox và input số lượng trong form add
        const addCheckboxes = document.querySelectorAll('#addBapNuocContainer .combo-item-checkbox');
        addCheckboxes.forEach(cb => {
            cb.addEventListener('change', function() {
                updateHiddenInput('addBapNuocContainer');
            });
        });

        const addNumberInputs = document.querySelectorAll('#addBapNuocContainer input[type="number"]');
        addNumberInputs.forEach(input => {
            input.addEventListener('change', function() {
                updateHiddenInput('addBapNuocContainer');
            });
        });
    }

    // Xử lý sự kiện cho form chỉnh sửa Bắp Nước
    const editBapNuocForm = document.getElementById('editBapNuocForm');
    if (editBapNuocForm) {
        editBapNuocForm.addEventListener('submit', function(e) {
            e.preventDefault();
            if (validateEditBapNuocForm(this)) {
                this.submit();
            }
        });
    }

    // Xử lý sự kiện cho form chỉnh sửa Combo
    const editComboForm = document.getElementById('editComboForm');
if (editComboForm) {
    editComboForm.addEventListener('submit', function(e) {
        e.preventDefault();
        console.log('=== EDIT COMBO FORM SUBMIT ===');
        
        // Force update hidden input ngay trước khi submit
        updateHiddenInput('editBapNuocContainer');
        
        // Debug hidden input
        const hiddenInput = document.getElementById('editBapNuocHidden');
        console.log('Hidden input before submit:', hiddenInput);
        console.log('Hidden input value before submit:', hiddenInput ? hiddenInput.value : 'NOT FOUND');
        console.log('Hidden input name:', hiddenInput ? hiddenInput.name : 'NOT FOUND');
        
        if (validateEditComboForm(this)) {
            console.log('Validation passed, submitting form');
            
            // Debug lần cuối trước khi submit thật
            const finalValue = document.getElementById('editBapNuocHidden').value;
            console.log('FINAL HIDDEN VALUE BEFORE SUBMIT:', finalValue);
            
            this.submit();
        } else {
            console.log('Validation failed');
        }
    });
}
});
</script>