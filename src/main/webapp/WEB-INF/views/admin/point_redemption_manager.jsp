<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
    <div class="header">
        <h2>Quản Lý Quy Đổi Điểm</h2>
        <div class="add-btn-container">
            <button class="custom-btn" onclick="showAddModal()">Thêm Quy Đổi</button>
        </div>
    </div>

    <!-- Filter Section -->
    <div class="filter-section mb-3">
        <div class="form-group">
            <label for="sort">Sắp xếp theo:</label>
            <select class="form-control" id="sort" onchange="applyFiltersAndSort()">
                <option value="all" ${sortBy == 'all' ? 'selected' : ''}>Mặc định</option>
                <option value="sodiem_asc" ${sortBy == 'sodiem_asc' ? 'selected' : ''}>Số Điểm Cần (tăng dần)</option>
                <option value="sodiem_desc" ${sortBy == 'sodiem_desc' ? 'selected' : ''}>Số Điểm Cần (giảm dần)</option>
                <option value="giatri_asc" ${sortBy == 'giatri_asc' ? 'selected' : ''}>Giá trị Giảm (tăng dần)</option>
                <option value="giatri_desc" ${sortBy == 'giatri_desc' ? 'selected' : ''}>Giá trị Giảm (giảm dần)</option>
            </select>
        </div>
        <div class="form-group">
            <label for="filterLoaiUuDai">Loại Ưu Đãi:</label>
            <select class="form-control" id="filterLoaiUuDai" onchange="applyFiltersAndSort()">
                <option value="all" ${loaiUuDai == 'all' ? 'selected' : ''}>Tất cả</option>
                <option value="Giảm giá vé" ${loaiUuDai == 'Giảm giá vé' ? 'selected' : ''}>Giảm giá vé</option>
                <option value="Tặng đồ ăn" ${loaiUuDai == 'Tặng đồ ăn' ? 'selected' : ''}>Tặng đồ ăn</option>
                <option value="Tặng voucher" ${loaiUuDai == 'Tặng voucher' ? 'selected' : ''}>Tặng voucher</option>
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

    <!-- List Table -->
    <div class="table-responsive">
        <table class="table table-bordered table-striped" id="redemptionTable">
            <thead>
                <tr>
                    <th>Mã Quy Đổi</th>
                    <th>Tên Ưu Đãi</th>
                    <th>Số Điểm Cần</th>
                    <th>Loại Ưu Đãi</th>
                    <th>Giá Trị Giảm</th>
                    <th>Hành Động</th>
                </tr>
            </thead>
            <tbody id="redemptionList">
                <c:choose>
                    <c:when test="${empty quyDoiList}">
                        <tr><td colspan="6" class="no-data">Không có dữ liệu quy đổi điểm</td></tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="quyDoi" items="${quyDoiList}">
                            <tr data-redemption-id="${quyDoi.maQuyDoi}">
                                <td>${quyDoi.maQuyDoi}</td>
                                <td>${quyDoi.tenUuDai}</td>
                                <td>${quyDoi.soDiemCan}</td>
                                <td>${quyDoi.loaiUuDai}</td>
                                <td class="currency"><fmt:formatNumber value="${quyDoi.giaTriGiam}" type="number" groupingUsed="true" minFractionDigits="0" maxFractionDigits="0"/>đ</td>
                                <td>
                                    <button class="custom-btn btn-sm edit-btn" onclick="showEditModal(this)">Sửa</button>
                                    <a href="${pageContext.request.contextPath}/admin/point-redemptions/delete/${quyDoi.maQuyDoi}" 
                                       class="custom-btn btn-sm delete-btn" 
                                       onclick="return confirm('Bạn có chắc muốn xóa quy đổi ${quyDoi.maQuyDoi} không?')">Xóa</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>

    <!-- Pagination -->
    <div class="pagination" id="pagination">
        <c:if test="${totalPages > 1}">
            <c:if test="${currentPage > 1}">
                <li class="page-item">
                    <a class="page-link" href="?page=${currentPage - 1}&sort=${sortBy}&loai=${loaiUuDai}">Trước</a>
                </li>
            </c:if>
            <c:forEach begin="1" end="${totalPages}" var="i">
                <li class="page-item ${i == currentPage ? 'active' : ''}">
                    <a class="page-link" href="?page=${i}&sort=${sortBy}&loai=${loaiUuDai}">${i}</a>
                </li>
            </c:forEach>
            <c:if test="${currentPage < totalPages}">
                <li class="page-item">
                    <a class="page-link" href="?page=${currentPage + 1}&sort=${sortBy}&loai=${loaiUuDai}">Sau</a>
                </li>
            </c:if>
        </c:if>
    </div>

    <!-- Add Modal -->
    <div class="modal" id="addModal" style="display: none;">
        <div class="modal-content">
            <h3>Thêm Quy Đổi Điểm Mới</h3>
            <form id="addForm" action="${pageContext.request.contextPath}/admin/point-redemptions/add" method="post" onsubmit="return validateAddForm(this)">
                <div class="row">
                    <div class="col-md-6">
                        <div class="form-group">
                            <label for="addMaQuyDoi">Mã Quy Đổi</label>
                            <input type="text" class="form-control" id="addMaQuyDoi" name="maQuyDoi" value="${newMaQuyDoi}" readonly>
                        </div>
                        <div class="form-group">
                            <label for="addTenUuDai">Tên Ưu Đãi</label>
                            <input type="text" class="form-control" id="addTenUuDai" name="tenUuDai" placeholder="VD: Giảm giá vé 10,000đ" value="${addTenUuDai}">
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="form-group">
                            <label for="addSoDiemCan">Số Điểm Cần</label>
                            <input type="text" class="form-control" id="addSoDiemCan" name="soDiemCan" placeholder="VD: 100" value="${addSoDiemCan}">
                        </div>
                        <div class="form-group">
                            <label for="addLoaiUuDai">Loại Ưu Đãi</label>
                            <select class="form-control" id="addLoaiUuDai" name="loaiUuDai">
                                <option value="Giảm giá vé" ${addLoaiUuDai == 'Giảm giá vé' ? 'selected' : ''}>Giảm giá vé</option>
                                <option value="Tặng đồ ăn" ${addLoaiUuDai == 'Tặng đồ ăn' ? 'selected' : ''}>Tặng đồ ăn</option>
                                <option value="Tặng voucher" ${addLoaiUuDai == 'Tặng voucher' ? 'selected' : ''}>Tặng voucher</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="addGiaTriGiam">Giá Trị Giảm (VNĐ)</label>
                            <input type="text" class="form-control" id="addGiaTriGiam" name="giaTriGiam" placeholder="VD: 50,000" value="${addGiaTriGiam}">
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

    <!-- Edit Modal -->
    <div class="modal" id="editModal" style="display: none;">
        <div class="modal-content">
            <h3>Sửa Quy Đổi Điểm</h3>
            <form id="editForm" action="${pageContext.request.contextPath}/admin/point-redemptions/update" method="post" onsubmit="return validateEditForm(this)">
                <div class="row">
                    <div class="col-md-6">
                        <div class="form-group">
                            <label for="editMaQuyDoi">Mã Quy Đổi</label>
                            <input type="text" class="form-control" id="editMaQuyDoi" name="maQuyDoi" readonly>
                        </div>
                        <div class="form-group">
                            <label for="editTenUuDai">Tên Ưu Đãi</label>
                            <input type="text" class="form-control" id="editTenUuDai" name="tenUuDai">
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="form-group">
                            <label for="editSoDiemCan">Số Điểm Cần</label>
                            <input type="text" class="form-control" id="editSoDiemCan" name="soDiemCan">
                        </div>
                        <div class="form-group">
                            <label for="editLoaiUuDai">Loại Ưu Đãi</label>
                            <select class="form-control" id="editLoaiUuDai" name="loaiUuDai">
                                <option value="Giảm giá vé">Giảm giá vé</option>
                                <option value="Tặng đồ ăn">Tặng đồ ăn</option>
                                <option value="Tặng voucher">Tặng voucher</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="editGiaTriGiam">Giá Trị Giảm (VNĐ)</label>
                            <input type="text" class="form-control" id="editGiaTriGiam" name="giaTriGiam">
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

    <!-- JavaScript -->
    <script src="${pageContext.request.contextPath}/resources/admin/js/number-utils.js"></script>
    <script src="${pageContext.request.contextPath}/resources/admin/js/currency-utils.js"></script>
    <script>
        console.log("point_redemption_manager.jsp: Script loaded");

        // Áp dụng filter và sort
        function applyFiltersAndSort() {
            const sort = document.getElementById('sort').value;
            const loaiUuDai = document.getElementById('filterLoaiUuDai').value;
            window.location.href = "${pageContext.request.contextPath}/admin/point-redemptions?page=1&sort=" + sort + "&loai=" + loaiUuDai;
            console.log("Applied filters: sort=", sort, "loaiUuDai=", loaiUuDai);
        }

        // Hiển thị modal thêm
        function showAddModal() {
            document.getElementById('addForm').reset();
            document.getElementById('addMaQuyDoi').value = '${newMaQuyDoi}';
            document.getElementById('addModal').style.display = 'flex';
            console.log("Add modal shown with maQuyDoi:", '${newMaQuyDoi}');
        }

        // Đóng modal thêm
        function closeAddModal() {
            document.getElementById('addModal').style.display = 'none';
            console.log("Add modal closed");
        }

        // Hiển thị modal sửa
        function showEditModal(button) {
            const row = button.closest('tr');
            document.getElementById('editMaQuyDoi').value = row.cells[0].textContent;
            document.getElementById('editTenUuDai').value = row.cells[1].textContent;
            document.getElementById('editSoDiemCan').value = row.cells[2].textContent;
            document.getElementById('editLoaiUuDai').value = row.cells[3].textContent;
            const rawPrice = row.cells[4].textContent.replace(/[^0-9]/g, '');
            document.getElementById('editGiaTriGiam').value = typeof formatCurrencyWithDecimal === 'function' ? formatCurrencyWithDecimal(parseFloat(rawPrice)) : rawPrice;
            document.getElementById('editModal').style.display = 'flex';
            console.log("Edit modal shown for maQuyDoi:", row.cells[0].textContent);
        }

        // Đóng modal sửa
        function closeEditModal() {
            document.getElementById('editModal').style.display = 'none';
            console.log("Edit modal closed");
        }

        // Validate form thêm
        function validateAddForm(form) {
            const errors = [];
            const ten = form.querySelector('#addTenUuDai').value.trim();
            const soDiem = form.querySelector('#addSoDiemCan').value.trim();
            const gia = form.querySelector('#addGiaTriGiam').value.trim();

            if (!ten) errors.push("Tên ưu đãi không được để trống.");
            if (!soDiem || isNaN(parseInt(soDiem.replace(/[^0-9]/g, ''))) || parseInt(soDiem.replace(/[^0-9]/g, '')) <= 0) {
                errors.push("Số điểm cần phải là số nguyên dương.");
            }
            const giaValue = typeof parseCurrencyInput === 'function' ? parseCurrencyInput(gia) : parseFloat(gia.replace(/[^0-9]/g, ''));
            if (!gia || isNaN(giaValue) || giaValue <= 0) {
                errors.push("Giá trị giảm phải là số dương.");
            }

            if (errors.length > 0) {
                alert(errors.join("\n"));
                console.log("Add form validation failed:", errors);
                return false;
            }

            // Parse giá trị trước khi submit
            form.querySelector('#addSoDiemCan').value = parseInt(soDiem.replace(/[^0-9]/g, '')) || '';
            form.querySelector('#addGiaTriGiam').value = giaValue;
            console.log("Add form validated, submitting với soDiemCan:", form.querySelector('#addSoDiemCan').value, "giaTriGiam:", form.querySelector('#addGiaTriGiam').value);
            return true;
        }

        // Validate form sửa
        function validateEditForm(form) {
            const errors = [];
            const ten = form.querySelector('#editTenUuDai').value.trim();
            const soDiem = form.querySelector('#editSoDiemCan').value.trim();
            const gia = form.querySelector('#editGiaTriGiam').value.trim();

            if (!ten) errors.push("Tên ưu đãi không được để trống.");
            if (!soDiem || isNaN(parseInt(soDiem.replace(/[^0-9]/g, ''))) || parseInt(soDiem.replace(/[^0-9]/g, '')) <= 0) {
                errors.push("Số điểm cần phải là số nguyên dương.");
            }
            const giaValue = typeof parseCurrencyInput === 'function' ? parseCurrencyInput(gia) : parseFloat(gia.replace(/[^0-9]/g, ''));
            if (!gia || isNaN(giaValue) || giaValue <= 0) {
                errors.push("Giá trị giảm phải là số dương.");
            }

            if (errors.length > 0) {
                alert(errors.join("\n"));
                console.log("Edit form validation failed:", errors);
                return false;
            }

            // Parse giá trị trước khi submit
            form.querySelector('#editSoDiemCan').value = parseInt(soDiem.replace(/[^0-9]/g, '')) || '';
            form.querySelector('#editGiaTriGiam').value = giaValue;
            console.log("Edit form validated, submitting với soDiemCan:", form.querySelector('#editSoDiemCan').value, "giaTriGiam:", form.querySelector('#editGiaTriGiam').value);
            return true;
        }

        document.addEventListener("DOMContentLoaded", function () {
            console.log("point_redemption_manager.jsp: DOMContentLoaded event fired");

            try {
                // Áp dụng restrictToIntegerInput cho addSoDiemCan
                const addSoDiemCanInput = document.getElementById('addSoDiemCan');
                if (addSoDiemCanInput && typeof restrictToIntegerInput === 'function') {
                    restrictToIntegerInput(addSoDiemCanInput);
                    console.log("Restricted input với addSoDiemCan");
                } else if (!addSoDiemCanInput) {
                    console.error("addSoDiemCan input không tìm thấy");
                } else {
                    console.warn("restrictToIntegerInput không định nghĩa, kiểm tra number-utils.js");
                }

                // Áp dụng restrictToNumberInput và format cho addGiaTriGiam
                const addGiaTriGiamInput = document.getElementById('addGiaTriGiam');
                if (addGiaTriGiamInput && typeof restrictToIntegerInput === 'function' && typeof formatCurrencyWithDecimal === 'function') {
                    restrictToIntegerInput(addGiaTriGiamInput);
                    addGiaTriGiamInput.addEventListener('input', function (e) {
                        let rawValue = e.target.value;
                        let cleaned = rawValue.replace(/[^0-9]/g, '');
                        if (cleaned) {
                            const parsed = parseFloat(cleaned);
                            if (!isNaN(parsed) && parsed >= 0) {
                                e.target.value = formatCurrencyWithDecimal(parsed);
                                console.log("Formatted addGiaTriGiam:", e.target.value);
                            } else {
                                e.target.value = '';
                            }
                        } else {
                            e.target.value = '';
                        }
                    });
                } else {
                    console.warn("addGiaTriGiam input hoặc các hàm cần thiết không tồn tại");
                }

                // Áp dụng restrictToIntegerInput cho editSoDiemCan
                const editSoDiemCanInput = document.getElementById('editSoDiemCan');
                if (editSoDiemCanInput && typeof restrictToIntegerInput === 'function') {
                    restrictToIntegerInput(editSoDiemCanInput);
                    console.log("Restricted input với editSoDiemCan");
                }

                // Áp dụng restrictToNumberInput và format cho editGiaTriGiam
                const editGiaTriGiamInput = document.getElementById('editGiaTriGiam');
                if (editGiaTriGiamInput && typeof restrictToIntegerInput === 'function' && typeof formatCurrencyWithDecimal === 'function') {
                    restrictToIntegerInput(editGiaTriGiamInput);
                    editGiaTriGiamInput.addEventListener('input', function (e) {
                        let rawValue = e.target.value;
                        let cleaned = rawValue.replace(/[^0-9]/g, '');
                        if (cleaned) {
                            const parsed = parseFloat(cleaned);
                            if (!isNaN(parsed) && parsed >= 0) {
                                e.target.value = formatCurrencyWithDecimal(parsed);
                                console.log("Formatted editGiaTriGiam:", e.target.value);
                            } else {
                                e.target.value = '';
                            }
                        } else {
                            e.target.value = '';
                        }
                    });
                }

                // Hiển thị modal thêm nếu có lỗi từ server
                <c:if test="${not empty addTenUuDai || not empty addSoDiemCan || not empty addLoaiUuDai || not empty addGiaTriGiam}">
                    document.getElementById('addModal').style.display = 'flex';
                    document.getElementById('addMaQuyDoi').value = '${addMaQuyDoi != null ? addMaQuyDoi : newMaQuyDoi}';
                    document.getElementById('addTenUuDai').value = '${addTenUuDai}';
                    document.getElementById('addSoDiemCan').value = '${addSoDiemCan}';
                    document.getElementById('addLoaiUuDai').value = '${addLoaiUuDai}';
                    document.getElementById('addGiaTriGiam').value = <c:choose>
                        <c:when test="${not empty addGiaTriGiam}">
                            typeof formatCurrencyWithDecimal === 'function' ? formatCurrencyWithDecimal(parseFloat('${addGiaTriGiam}')) : '${addGiaTriGiam}'
                        </c:when>
                        <c:otherwise>''</c:otherwise>
                    </c:choose>;
                    console.log("Repopulated add modal with server data, giaTriGiam:", document.getElementById('addGiaTriGiam').value);
                </c:if>

                // Ẩn thông báo sau 5 giây
                const errorMessage = document.getElementById('errorMessage');
                if (errorMessage) {
                    setTimeout(() => {
                        errorMessage.style.display = 'none';
                        console.log("Hid error message");
                    }, 5000);
                }
                const successMessage = document.getElementById('successMessage');
                if (successMessage) {
                    setTimeout(() => {
                        successMessage.style.display = 'none';
                        console.log("Hid success message");
                    }, 5000);
                }
            } catch (err) {
                console.error("Error trong DOMContentLoaded:", err);
            }
        });
    </script>