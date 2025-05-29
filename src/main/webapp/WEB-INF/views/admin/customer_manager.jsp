<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

    <h2>Danh Sách Khách Hàng</h2>
    <c:if test="${not empty error}">
        <div class="alert alert-danger">${fn:escapeXml(error)}</div>
    </c:if>
    <div class="row">
        <div class="col-md-12">
            <div class="table-responsive">
                <table class="table">
                    <thead>
                        <tr>
                            <th>Mã KH</th>
                            <th>Họ</th>
                            <th>Tên</th>
                            <th>SĐT</th>
                            <th>Email</th>
                            <th>Ngày Sinh</th>
                            <th>Ngày ĐK</th>
                            <th>Tổng Điểm</th>
                            <th>Hành Động</th>
                        </tr>
                    </thead>
                    <tbody id="customerList">
                        <c:choose>
                            <c:when test="${empty customerList}">
                                <tr>
                                    <td colspan="9" class="no-data">Không có khách hàng nào</td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="customer" items="${customerList}">
                                    <tr data-customer-id="${fn:escapeXml(customer.maKhachHang)}">
                                        <td>${fn:escapeXml(customer.maKhachHang)}</td>
                                        <td>${fn:escapeXml(customer.hoKhachHang)}</td>
                                        <td>${fn:escapeXml(customer.tenKhachHang)}</td>
                                        <td>${fn:escapeXml(customer.soDienThoai)}</td>
                                        <td>${fn:escapeXml(customer.email)}</td>
                                        <td>
                                            <fmt:formatDate value="${customer.ngaySinh}" pattern="dd-MM-yyyy"/>
                                        </td>
                                        <td>
                                            <fmt:formatDate value="${customer.ngayDangKy}" pattern="dd-MM-yyyy"/>
                                        </td>
                                        <td>${customer.tongDiem}</td>
                                        <td>
                                            <button class="custom-btn btn-sm" onclick="showOrderDetail('${fn:escapeXml(customer.maKhachHang)}')">Xem Đơn Hàng</button>
                                            <a href="${pageContext.request.contextPath}/admin/customers/delete/${fn:escapeXml(customer.maKhachHang)}" class="custom-btn btn-sm" onclick="return confirm('Bạn có chắc muốn xóa khách hàng này không?')">Xóa</a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </div>


<!-- Pagination -->
<div class="pagination">
    <c:if test="${totalPages > 1}">
        <c:if test="${currentPage > 1}">
            <li class="page-item">
                <a class="page-link" href="?page=${currentPage - 1}">Trước</a>
            </li>
        </c:if>
        <c:forEach var="i" items="${pageRange}">
            <li class="page-item ${i == currentPage ? 'active' : ''}">
                <a class="page-link" href="?page=${i}">${i}</a>
            </li>
        </c:forEach>
        <c:if test="${currentPage < totalPages}">
            <li class="page-item">
                <a class="page-link" href="?page=${currentPage + 1}">Sau</a>
            </li>
        </c:if>
    </c:if>
</div>

<!-- Modal hiển thị chi tiết đơn hàng -->
<div class="modal" id="orderDetailModal" style="display: none;">
    <div class="modal-content large-modal">
        <h3>Chi Tiết Đơn Hàng</h3>
        <div class="table-responsive">
            <table class="table">
                <thead>
                    <tr>
                        <th>Mã Đơn Hàng</th>
                        <th>Mã Khuyến Mãi</th>
                        <th>Mã Quy Đổi</th>
                        <th>Tổng Tiền</th>
                        <th>Đặt Hàng</th>
                        <th>Ngày Đặt</th>
                        <th>Điểm Sử Dụng</th>
                    </tr>
                </thead>
                <tbody id="orderDetails">
                    <!-- Dữ liệu sẽ được thêm bằng JavaScript -->
                </tbody>
            </table>
        </div>
        <div class="modal-actions">
            <button class="custom-btn" onclick="closeOrderDetailModal()">Đóng</button>
        </div>
    </div>
</div>

<script>
    function showOrderDetail(maKhachHang) {
        const orderDetails = document.getElementById('orderDetails');
        orderDetails.innerHTML = ''; // Xóa dữ liệu cũ

        // Lấy danh sách đơn hàng từ customerOrdersMap
        const orders = window.customerOrders[maKhachHang] || [];
        if (orders.length === 0) {
            orderDetails.innerHTML = '<tr><td colspan="7" class="no-data">Không có đơn hàng nào</td></tr>';
        } else {
            orders.forEach(order => {
                const row = document.createElement('tr');
                // Format ngày đặt
                const ngayDat = new Date(Number(order.ngayDat));
                const ngayDatStr = ngayDat.toLocaleDateString('vi-VN', {
                    day: '2-digit',
                    month: '2-digit',
                    year: 'numeric'
                });
                row.innerHTML = `
                    <td>\${order.maDonHang}</td>
                    <td>\${order.maKhuyenMai || 'N/A'}</td>
                    <td>\${order.maQuyDoi || 'N/A'}</td>
                    <td>\${Number(order.tongTien).toLocaleString('vi-VN', { style: 'currency', currency: 'VND' })}</td>
                    <td>\${order.datHang ? 'Online' : 'Tại quầy'}</td>
                    <td>\${ngayDatStr}</td>
                    <td>\${order.diemSuDung || 0}</td>`;
                orderDetails.appendChild(row);
            });
        }

        document.getElementById('orderDetailModal').style.display = 'flex';
    }

    function closeOrderDetailModal() {
        document.getElementById('orderDetailModal').style.display = 'none';
    }

    // Chuyển customerOrdersMap từ JSP sang JavaScript
    window.customerOrders = {
        <c:forEach var="entry" items="${customerOrdersMap}" varStatus="status">
            "${fn:escapeXml(entry.key)}": [
                <c:forEach var="order" items="${entry.value}" varStatus="orderStatus">
                    {
                        maDonHang: "${fn:escapeXml(order.maDonHang)}",
                        maKhuyenMai: "${fn:escapeXml(order.maKhuyenMai != null ? order.maKhuyenMai : '')}",
                        maQuyDoi: "${fn:escapeXml(order.maQuyDoi != null ? order.maQuyDoi : '')}",
                        tongTien: ${order.tongTien},
                        datHang: ${order.datHang},
                        ngayDat: "${order.ngayDat.time}",
                        diemSuDung: ${order.diemSuDung != null ? order.diemSuDung : 0}
                    }${orderStatus.last ? '' : ','}
                </c:forEach>
            ]${status.last ? '' : ','}
        </c:forEach>
    };
</script>