<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!-- Danh Sách Phụ Thu -->
<div class="surcharge-list mb-4">
    <div class="promotion-header">
        <h2>Danh Sách Phụ Thu</h2>
        <div class="add-btn-container">
            <button class="custom-btn" onclick="showAddModal()">Thêm Phụ Thu</button>
        </div>
    </div>
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
                                <td data-field="gia"><fmt:formatNumber value="${pt.gia}" type="currency" currencySymbol="₫" /></td>
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
</div>

<!-- Modal Thêm Phụ Thu -->
<div class="modal" id="addModal" style="display: none;">
    <div class="modal-content">
        <h3>Thêm Phụ Thu Mới</h3>
        <form action="${pageContext.request.contextPath}/admin/surcharges/add" method="post">
            <div class="detail-field">
                <label for="addMaPhuThu">Mã Phụ Thu</label>
                <input type="text" id="addMaPhuThu" name="maPhuThu" class="form-control" value="${newMaPhuThu}" readonly>
            </div>
            <div class="detail-field">
                <label for="addTenPhuThu">Tên Phụ Thu</label>
                <input type="text" id="addTenPhuThu" name="tenPhuThu" class="form-control" placeholder="VD: Phụ thu cuối tuần">
            </div>
            <div class="detail-field">
                <label for="addGia">Giá (VNĐ)</label>
                <input type="number" id="addGia" name="gia" class="form-control" step="1000" min="0" placeholder="VD: 15000">
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
        <form action="${pageContext.request.contextPath}/admin/surcharges/update" method="post">
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
                <input type="number" id="editGia" name="gia" class="form-control" step="1000" min="0">
            </div>
            <div class="modal-actions">
                <button type="submit" class="custom-btn">Lưu</button>
                <button type="button" class="custom-btn" onclick="closeModal()">Hủy</button>
            </div>
        </form>
    </div>
</div>

<!-- JavaScript -->
<script>
    function showEditModal(button) {
        const row = button.closest('tr');
        document.getElementById('editMaPhuThu').value = row.cells[0].textContent;
        document.getElementById('editTenPhuThu').value = row.cells[1].textContent;
        document.getElementById('editGia').value = row.cells[2].textContent.replace(/[^0-9]/g, '');
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