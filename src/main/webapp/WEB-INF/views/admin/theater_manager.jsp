<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!-- Modal Thêm Rạp Chiếu -->
<div class="modal" id="addModal" style="display: none;">
    <div class="modal-content">
        <h3>Thêm Rạp Chiếu Mới</h3>
        <form action="${pageContext.request.contextPath}/admin/theaters/add" method="post">
            <div class="form-group">
                <label for="addMaRap">Mã Rạp</label>
                <input type="text" class="form-control" id="addMaRap" name="maRapChieu" value="${newMaRapChieu}" readonly>
            </div>
            <div class="form-group">
                <label for="addTenRap">Tên Rạp</label>
                <input type="text" class="form-control" id="addTenRap" name="tenRapChieu" placeholder="VD: Rạp 1 - Quận 1" required value="${tenRapChieu != null ? tenRapChieu : ''}">
            </div>
            <div class="form-group">
                <label for="addDiaChi">Địa Chỉ</label>
                <input type="text" class="form-control" id="addDiaChi" name="diaChi" placeholder="VD: 123 Đường ABC, Quận 1" required value="${diaChi != null ? diaChi : ''}">
            </div>
            <div class="form-group">
                <label for="addSdtLienHe">SĐT Liên Hệ</label>
                <input type="text" class="form-control" id="addSdtLienHe" name="soDienThoaiLienHe" placeholder="VD: 0909123456" required pattern="\d{10}" title="Số điện thoại phải gồm đúng 10 chữ số" value="${soDienThoaiLienHe != null ? soDienThoaiLienHe : ''}">
            </div>
            <div class="modal-actions">
                <button type="submit" class="custom-btn">Thêm</button>
                <button type="button" class="custom-btn" onclick="closeAddModal()">Hủy</button>
            </div>
        </form>
    </div>
</div>

<!-- Modal Sửa Rạp Chiếu -->
<div class="modal" id="editModal" style="display: none;">
    <div class="modal-content">
        <h3>Sửa Thông Tin Rạp Chiếu</h3>
        <form action="${pageContext.request.contextPath}/admin/theaters/update" method="post">
            <div class="form-group">
                <label for="editMaRap">Mã Rạp</label>
                <input type="text" class="form-control" id="editMaRap" name="maRapChieu" readonly>
            </div>
            <div class="form-group">
                <label for="editTenRap">Tên Rạp</label>
                <input type="text" class="form-control" id="editTenRap" name="tenRapChieu" required value="${tenRapChieu_edit != null ? tenRapChieu_edit : ''}">
            </div>
            <div class="form-group">
                <label for="editDiaChi">Địa Chỉ</label>
                <input type="text" class="form-control" id="editDiaChi" name="diaChi" required value="${diaChi_edit != null ? diaChi_edit : ''}">
            </div>
            <div class="form-group">
                <label for="editSdtLienHe">SĐT Liên Hệ</label>
                <input type="text" class="form-control" id="editSdtLienHe" name="soDienThoaiLienHe" required pattern="\d{10}" title="Số điện thoại phải gồm đúng 10 chữ số" value="${soDienThoaiLienHe_edit != null ? soDienThoaiLienHe : ''}">
            </div>
            <div class="modal-actions">
                <button type="submit" class="custom-btn">Lưu</button>
                <button type="button" class="custom-btn" onclick="closeModal()">Hủy</button>
            </div>
        </form>
    </div>
</div>

<!-- Header and Filter Section -->
<div class="header">
    <h2>Quản Lý Rạp Chiếu</h2>
    <div class="add-btn-container">
        <button class="custom-btn" onclick="showAddModal()">Thêm Mới</button>
    </div>
</div>

<!-- Thông báo lỗi/thành công từ flash attributes -->
<c:if test="${not empty success}">
    <div class="alert alert-success" role="alert">
        ${fn:escapeXml(success)}
    </div>
</c:if>
<c:if test="${not empty error}">
    <div class="alert alert-danger" role="alert">
        ${fn:escapeXml(error)}
    </div>
</c:if>

<div class="flex-column col-2 filter-section mb-3">
    <div class="form-group">
        <label for="filterTenRap">Lọc theo tên rạp:</label>
        <input type="text" class="form-control" id="filterTenRap" placeholder="Nhập tên rạp để lọc">
    </div>
</div>

<!-- Danh Sách Rạp Chiếu -->
<div class="table-responsive">
    <table class="table table-bordered table-striped">
        <thead>
            <tr>
                <th>Mã Rạp</th>
                <th>Tên Rạp</th>
                <th>Địa Chỉ</th>
                <th>SĐT Liên Hệ</th>
                <th>Hành Động</th>
            </tr>
        </thead>
        <tbody id="theaterList">
            <c:choose>
                <c:when test="${empty rapChieuList}">
                    <tr class="no-data">
                        <td colspan="5" class="no-data">Không có dữ liệu</td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <c:forEach var="rap" items="${rapChieuList}">
                        <tr data-theater-id="${rap.maRapChieu}">
                            <td data-field="maRapChieu">${rap.maRapChieu}</td>
                            <td data-field="tenRapChieu">${rap.tenRapChieu}</td>
                            <td data-field="diaChi">${rap.diaChi}</td>
                            <td data-field="soDienThoaiLienHe">${rap.soDienThoaiLienHe}</td>
                            <td>
                                <button class="custom-btn btn-sm mr-1" onclick="showEditModal('${rap.maRapChieu}', '${fn:escapeXml(rap.tenRapChieu)}', '${fn:escapeXml(rap.diaChi)}', '${rap.soDienThoaiLienHe}')">Sửa</button>
                                <a href="${pageContext.request.contextPath}/admin/theaters/delete/${rap.maRapChieu}" class="custom-btn btn-sm" onclick="return confirm('Bạn có chắc muốn xóa rạp ${fn:escapeXml(rap.tenRapChieu)} không?');">Xóa</a>
                            </td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </tbody>
    </table>
</div>

<!-- JavaScript -->
<script>
document.addEventListener('DOMContentLoaded', function () {
    updateTheaterList();
    initFilter();

    // Giới hạn input số điện thoại
    const addSdtInput = document.getElementById("addSdtLienHe");
    if (addSdtInput) {
        addSdtInput.addEventListener("input", function () {
            this.value = this.value.replace(/[^0-9]/g, '').slice(0, 10);
        });
    }

    const editSdtInput = document.getElementById("editSdtLienHe");
    if (editSdtInput) {
        editSdtInput.addEventListener("input", function () {
            this.value = this.value.replace(/[^0-9]/g, '').slice(0, 10);
        });
    }

    // Hide error message after 5 seconds
    const errorMessage = document.querySelector('.alert-danger');
    if (errorMessage) {
        setTimeout(() => {
            errorMessage.style.display = 'none';
        }, 5000);
    }

    // Hide success message after 5 seconds
    const successMessage = document.querySelector('.alert-success');
    if (successMessage) {
        setTimeout(() => {
            successMessage.style.display = 'none';
        }, 5000);
    }
});

function updateTheaterList() {
    const theaterList = document.getElementById('theaterList');
    const rows = theaterList.querySelectorAll('tr:not(.no-data)');
    const noDataRow = theaterList.querySelector('.no-data');

    if (noDataRow && rows.length > 0) {
        noDataRow.remove();
    } else if (!noDataRow && rows.length === 0) {
        const newRow = document.createElement('tr');
        newRow.classList.add('no-data');
        newRow.innerHTML = `<td colspan="5" class="no-data">Không có dữ liệu</td>`;
        theaterList.appendChild(newRow);
    }
}

function initFilter() {
    const filterInput = document.getElementById('filterTenRap');
    filterInput.addEventListener('input', function () {
        const filterValue = this.value.toLowerCase();
        const rows = document.querySelectorAll('#theaterList tr:not(.no-data)');

        rows.forEach(row => {
            const tenRap = row.querySelector('td[data-field="tenRapChieu"]').textContent.toLowerCase();
            row.style.display = tenRap.includes(filterValue) ? '' : 'none';
        });
    });
}

function showAddModal() {
    document.getElementById('addMaRap').value = '${newMaRapChieu}';
    document.getElementById('addTenRap').value = '';
    document.getElementById('addDiaChi').value = '';
    document.getElementById('addSdtLienHe').value = '';
    document.getElementById('addModal').style.display = 'flex';
}

function closeAddModal() {
    document.getElementById('addModal').style.display = 'none';
    document.getElementById('addTenRap').value = '';
    document.getElementById('addDiaChi').value = '';
    document.getElementById('addSdtLienHe').value = '';
}

function showEditModal(maRap, tenRap, diaChi, sdtLienHe) {
    document.getElementById('editMaRap').value = maRap;
    document.getElementById('editTenRap').value = tenRap;
    document.getElementById('editDiaChi').value = diaChi;
    document.getElementById('editSdtLienHe').value = sdtLienHe;
    document.getElementById('editModal').style.display = 'flex';
}

function closeModal() {
    document.getElementById('editModal').style.display = 'none';
    document.getElementById('editTenRap').value = '';
    document.getElementById('editDiaChi').value = '';
    document.getElementById('editSdtLienHe').value = '';
}

</script>