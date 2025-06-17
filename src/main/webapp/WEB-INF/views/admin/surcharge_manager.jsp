<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!-- Danh Sách Phụ Thu -->
<div class="header">
    <h2>Danh Sách Phụ Thu</h2>
    <div class="add-btn-container">
        <button class="custom-btn" onclick="showAddModal()">Thêm Phụ Thu</button>
    </div>
</div>

<!-- Search Form -->
<div class="search-form-container mb-3">
    <form action="${pageContext.request.contextPath}/admin/surcharges" method="get" class="form-inline">
        <div class="input-group">
            <input type="text" name="search" id="searchInput" class="form-control" placeholder="Tìm kiếm theo tên phụ thu..." value="${search}">
            <div class="input-group-append">
                <button type="submit" class="custom-btn">Tìm kiếm</button>
                <button type="button" class="custom-btn" onclick="clearSearch()">Xóa</button>
            </div>
        </div>
    </form>
</div>

<!-- Error and Success Messages -->
<c:if test="${not empty error}">
    <div class="alert alert-danger" id="errorMessage">${error}</div>
</c:if>
<c:if test="${not empty success}">
    <div class="alert alert-success" id="successMessage">${success}</div>
</c:if>
<div class="table-responsive">
    <table class="table table-bordered table-striped">
        <thead>
            <tr>
                <th>Mã Phụ Thu</th>
                <th>Tên Phụ Thu</th>
                <th>Giá (VNĐ)</th>
                <th>Hành Động</th>
            </tr>
        </thead>
        <tbody id="surchargeList">
            <c:choose>
                <c:when test="${empty phuThuList}">
                    <tr class="no-data">
                        <td colspan="4" class="no-data">Không có dữ liệu</td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <c:forEach var="pt" items="${phuThuList}">
                        <tr data-surcharge-id="${pt.maPhuThu}">
                            <td data-field="maPhuThu">${pt.maPhuThu}</td>
                            <td data-field="tenPhuThu">${pt.tenPhuThu}</td>
                            <td data-field="gia" class="currency"><fmt:formatNumber value="${pt.gia}" type="number" groupingUsed="true" minFractionDigits="0" maxFractionDigits="0"/>đ</td>
                            <td>
                                <button class="custom-btn btn-sm mr-1" onclick="showEditModal(this)">Sửa</button>
                                <a href="${pageContext.request.contextPath}/admin/surcharges/delete/${pt.maPhuThu}" 
                                   class="custom-btn btn-sm" 
                                   onclick="return confirm('Bạn có chắc muốn xóa phụ thu ${pt.maPhuThu} không?');">Xóa</a>
                            </td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </tbody>
    </table>
</div>

<!-- Modal Thêm Phụ Thu -->
<div class="modal" id="addModal" style="display: none;">
    <div class="modal-content">
        <h3>Thêm Phụ Thu Mới</h3>
        <form id="addForm" action="${pageContext.request.contextPath}/admin/surcharges/add" method="post">
            <div class="detail-field">
                <label for="addMaPhuThu">Mã Phụ Thu</label>
                <input type="text" id="addMaPhuThu" name="maPhuThu" class="form-control" value="${addMaPhuThu != null ? addMaPhuThu : newMaPhuThu}" readonly>
            </div>
            <div class="detail-field">
                <label for="addTenPhuThu">Tên Phụ Thu</label>
                <input type="text" id="addTenPhuThu" name="tenPhuThu" class="form-control" placeholder="VD: Phụ thu cuối tuần" value="${addTenPhuThu}">
            </div>
            <div class="detail-field">
                <label for="addGia">Giá (VNĐ)</label>
                <input type="text" id="addGia" name="gia" class="form-control" placeholder="VD: 15,000" value="${addGia}">
            </div>
            <div class="modal-actions">
                <button type="submit" class="custom-btn">Thêm</button>
                <button type="button" class="custom-btn" onclick="closeAddModal()">Hủy</button>
            </div>
        </form>
    </div>
</div>

<!-- Modal Sửa Phụ Thu -->
<div class="modal" id="editModal" style="display: none;">
    <div class="modal-content">
        <h3>Sửa Thông Tin Phụ Thu</h3>
        <form id="editForm" action="${pageContext.request.contextPath}/admin/surcharges/update" method="post">
            <div class="detail-field">
                <label for="editMaPhuThu">Mã Phụ Thu</label>
                <input type="text" id="editMaPhuThu" name="maPhuThu" class="form-control" readonly>
            </div>
            <div class="detail-field">
                <label for="editTenPhuThu">Tên Phụ Thu</label>
                <input type="text" id="editTenPhuThu" name="tenPhuThu" class="form-control">
            </div>
            <div class="detail-field">
                <label for="editGia">Giá (VNĐ)</label>
                <input type="text" id="editGia" name="gia" class="form-control">
            </div>
            <div class="modal-actions">
                <button type="submit" class="custom-btn">Lưu</button>
                <button type="button" class="custom-btn" onclick="closeModal()">Hủy</button>
            </div>
        </form>
    </div>
</div>

<!-- JavaScript -->
<script src="${pageContext.request.contextPath}/resources/admin/js/number-utils.js"></script>
<script src="${pageContext.request.contextPath}/resources/admin/js/currency-utils.js"></script>
<script>
    function showEditModal(button) {
        const row = button.closest('tr');
        document.getElementById('editMaPhuThu').value = row.cells[0].textContent;
        document.getElementById('editTenPhuThu').value = row.cells[1].textContent;
        const rawPrice = row.cells[2].textContent.replace(/[^0-9]/g, '');
        document.getElementById('editGia').value = formatCurrencyWithDecimal(rawPrice);
        document.getElementById('editModal').style.display = 'flex';
    }

    function closeModal() {
        document.getElementById('editModal').style.display = 'none';
    }

    function showAddModal() {
        document.getElementById('addForm').reset();
        document.getElementById('addMaPhuThu').value = '${newMaPhuThu}';
        document.getElementById('addModal').style.display = 'flex';
    }

    function closeAddModal() {
        document.getElementById('addModal').style.display = 'none';
    }

    function clearSearch() {
        document.getElementById('searchInput').value = '';
        window.location.href = '${pageContext.request.contextPath}/admin/surcharges';
    }

    function validateAddForm(form) {
        const errors = [];
        const ten = form.querySelector('#addTenPhuThu').value.trim();
        const gia = form.querySelector('#addGia').value.trim();

        if (!ten) errors.push("Tên phụ thu không được để trống.");
        if (!gia || parseCurrencyInput(gia) <= 0) errors.push("Giá phải là số dương.");

        if (errors.length > 0) {
            alert(errors.join("\n"));
            return false;
        }
        return true;
    }

    function validateEditForm(form) {
        const errors = [];
        const ten = form.querySelector('#editTenPhuThu').value.trim();
        const gia = form.querySelector('#editGia').value.trim();

        if (!ten) errors.push("Tên phụ thu không được để trống.");
        if (!gia || parseCurrencyInput(gia) <= 0) errors.push("Giá phải là số dương.");

        if (errors.length > 0) {
            alert(errors.join("\n"));
            return false;
        }
        return true;
    }

    document.addEventListener("DOMContentLoaded", function () {
        // Áp dụng restrictToIntegerInput cho các input giá
        const addGiaInput = document.getElementById('addGia');
        const editGiaInput = document.getElementById('editGia');

        if (addGiaInput) {
            restrictToIntegerInput(addGiaInput);
            addGiaInput.addEventListener('input', function (e) {
                let rawValue = e.target.value;
                let cleaned = rawValue.replace(/[^0-9]/g, '');
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

        if (editGiaInput) {
            restrictToIntegerInput(editGiaInput);
            editGiaInput.addEventListener('input', function (e) {
                let rawValue = e.target.value;
                let cleaned = rawValue.replace(/[^0-9]/g, '');
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

        // Xử lý submit form thêm
        const addForm = document.getElementById('addForm');
        if (addForm) {
            addForm.addEventListener('submit', function (e) {
                e.preventDefault();
                if (!validateAddForm(this)) {
                    return;
                }
                // Chuyển đổi giá về dạng số trước khi gửi
                const giaInput = this.querySelector('#addGia');
                const cleanedGia = parseCurrencyInput(giaInput.value);
                giaInput.value = cleanedGia !== null ? cleanedGia.toString() : '';
                this.submit();
            });
        }

        // Xử lý submit form sửa
        const editForm = document.getElementById('editForm');
        if (editForm) {
            editForm.addEventListener('submit', function (e) {
                e.preventDefault();
                if (!validateEditForm(this)) {
                    return;
                }
                // Chuyển đổi giá về dạng số trước khi gửi
                const giaInput = this.querySelector('#editGia');
                const cleanedGia = parseCurrencyInput(giaInput.value);
                giaInput.value = cleanedGia !== null ? cleanedGia.toString() : '';
                this.submit();
            });
        }

        // Hiển thị modal thêm nếu có lỗi từ server
        <c:if test="${addTenPhuThu != null || addGia != null}">
            document.getElementById('addModal').style.display = 'flex';
            document.getElementById('addMaPhuThu').value = '${addMaPhuThu != null ? addMaPhuThu : newMaPhuThu}';
            document.getElementById('addTenPhuThu').value = '${addTenPhuThu}';
            document.getElementById('addGia').value = ${addGia != null ? 'formatCurrencyWithDecimal(' + addGia + ')' : "''"};
        </c:if>

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
    });
</script>