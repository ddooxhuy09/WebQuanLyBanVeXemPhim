<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<!-- Include currency-utils.js -->
<script src="${pageContext.request.contextPath}/resources/admin/js/currency-utils.js"></script>

<!-- Header -->
<div class="header">
    <h2>Quản Lý Thanh Toán</h2>
</div>

<!-- Filter and Sort Section -->
<div class="filter-section mb-3">
    <form method="get" action="${pageContext.request.contextPath}/admin/payment" class="form-inline">
        <!-- Lọc theo mã đơn hàng -->
        <div class="form-group mr-3">
            <label for="filterMaDonHang" class="mr-2">Mã Đơn Hàng:</label>
            <input type="text" class="form-control" id="filterMaDonHang" name="filterMaDonHang" 
                   value="${fn:escapeXml(filterMaDonHang)}" placeholder="Nhập mã đơn hàng">
        </div>

        <!-- Lọc theo phương thức thanh toán -->
        <div class="form-group mr-3">
            <label for="filterPhuongThuc" class="mr-2">Phương Thức:</label>
            <select class="form-control" id="filterPhuongThuc" name="filterPhuongThuc">
                <option value="all" ${filterPhuongThuc == null || filterPhuongThuc == 'all' ? 'selected' : ''}>Tất cả</option>
                <option value="zalopay" ${filterPhuongThuc == 'zalopay' ? 'selected' : ''}>ZaloPay</option>
                <option value="vnpay" ${filterPhuongThuc == 'vnpay' ? 'selected' : ''}>VNPay</option>
            </select>
        </div>

        <!-- Sắp xếp -->
        <div class="form-group mr-3">
            <label for="sortBy" class="mr-2">Sắp xếp theo:</label>
            <select class="form-control" id="sortBy" name="sortBy" onchange="this.form.submit()">
                <option value="ngayThanhToan" ${sortBy == 'ngayThanhToan' || sortBy == null ? 'selected' : ''}>Ngày mới nhất</option>
                <option value="soTien" ${sortBy == 'soTien' ? 'selected' : ''}>Số tiền cao nhất</option>
            </select>
        </div>

        <!-- Nút submit với class custom-btn -->
        <button type="submit" class="custom-btn">Áp dụng</button>
        <input type="hidden" name="page" value="${currentPage}">
    </form>
</div>


<!-- Danh Sách Thanh Toán -->
<div class="table-responsive">
    <table class="table table-bordered table-striped">
        <thead>
            <tr>
                <th>Mã Thanh Toán</th>
                <th>Mã Đơn Hàng</th>
                <th>Phương Thức</th>
                <th>Số Tiền</th>
                <th>Ngày Thanh Toán</th>
                <th>Trạng Thái</th>
            </tr>
        </thead>
        <tbody id="thanhToanList">
            <c:choose>
                <c:when test="${empty thanhToanList}">
                    <tr class="no-data">
                        <td colspan="6" class="no-data">Không có dữ liệu</td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <c:forEach var="thanhToan" items="${thanhToanList}">
                        <tr data-thanh-toan-id="${thanhToan.maThanhToan}">
                            <td data-field="maThanhToan">${thanhToan.maThanhToan}</td>
                            <td data-field="maDonHang">${thanhToan.maDonHang}</td>
                            <td data-field="phuongThuc">${thanhToan.phuongThuc}</td>
                            <td data-field="soTien">
                                <script>document.write(formatCurrencyWithDecimal(${thanhToan.soTien}));</script>
                            </td>
                            <td data-field="ngayThanhToan"><fmt:formatDate value="${thanhToan.ngayThanhToan}" pattern="dd/MM/yyyy"/></td>
                            <td data-field="trangThai">${thanhToan.trangThai}</td>
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
            <li class="page-item">
                <a class="page-link" href="?page=${currentPage - 1}&sortBy=${sortBy}&filterMaDonHang=${fn:escapeXml(filterMaDonHang)}&filterPhuongThuc=${filterPhuongThuc}">Trước</a>
            </li>
        </c:if>
        <c:forEach var="i" items="${pageRange}">
            <li class="page-item ${i == currentPage ? 'active' : ''}">
                <a class="page-link" href="?page=${i}&sortBy=${sortBy}&filterMaDonHang=${fn:escapeXml(filterMaDonHang)}&filterPhuongThuc=${filterPhuongThuc}">${i}</a>
            </li>
        </c:forEach>
        <c:if test="${currentPage < totalPages}">
            <li class="page-item">
                <a class="page-link" href="?page=${currentPage + 1}&sortBy=${sortBy}&filterMaDonHang=${fn:escapeXml(filterMaDonHang)}&filterPhuongThuc=${filterPhuongThuc}">Sau</a>
            </li>
        </c:if>
    </c:if>
</div>