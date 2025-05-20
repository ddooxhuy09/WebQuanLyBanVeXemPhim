<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<div class="customer-management mb-4">
    <h2>Danh Sách Khách Hàng</h2>
    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
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
                                    <tr data-customer-id="${customer.maKhachHang}">
                                        <td>${customer.maKhachHang}</td>
                                        <td>${customer.hoKhachHang}</td>
                                        <td>${customer.tenKhachHang}</td>
                                        <td>${customer.soDienThoai}</td>
                                        <td>${customer.email}</td>
                                        <td>
                                            <fmt:formatDate value="${customer.ngaySinh}" pattern="dd-MM-yyyy"/>
                                        </td>
                                        <td>
                                            <fmt:formatDate value="${customer.ngayDangKy}" pattern="dd-MM-yyyy"/>
                                        </td>
                                        <td>${customer.tongDiem}</td>
                                        <td>
                                            <button class="custom-btn btn-sm" onclick="showOrderDetail('${customer.maKhachHang}')">Xem Đơn Hàng</button>
                                            <a href="${pageContext.request.contextPath}/admin/customers/delete/${customer.maKhachHang}" class="custom-btn btn-sm" onclick="return confirm('Bạn có chắc muốn xóa khách hàng này không?')">Xóa</a>
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
                    <!-- Dữ liệu sẽ được thêm bằng JavaScript/AJAX -->
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
        fetch('${pageContext.request.contextPath}/admin/customers/orders/' + maKhachHang)
            .then(response => response.json())
            .then(orders => {
                const orderDetails = document.getElementById('orderDetails');
                orderDetails.innerHTML = ''; // Xóa dữ liệu cũ

                if (orders.length === 0) {
                    orderDetails.innerHTML = '<tr><td colspan="7" class="no-data">Không có đơn hàng nào</td></tr>';
                } else {
                    orders.forEach(order => {
                        const row = document.createElement('tr');
                        row.innerHTML = `
                            <td>\${order.maDonHang}</td>
                            <td>\${order.maKhuyenMai || 'N/A'}</td>
                            <td>\${order.maQuyDoi || 'N/A'}</td>
                            <td>\${Number(order.tongTien).toLocaleString('vi-VN', { style: 'currency', currency: 'VND' })}</td>
                            <td>\${order.datHang ? 'Online' : 'Tại quầy'}</td>
                            <td>\${new Date(order.ngayDat).toLocaleDateString('vi-VN')}</td>
                            <td>\${order.diemSuDung || 0}</td>`;
                        orderDetails.appendChild(row);
                    });
                }

                document.getElementById('orderDetailModal').style.display = 'flex';
            })
            .catch(error => {
                console.error('Lỗi khi lấy đơn hàng:', error);
                orderDetails.innerHTML = '<tr><td colspan="7" class="no-data">Lỗi khi tải đơn hàng</td></tr>';
            });
    }

    function closeOrderDetailModal() {
        document.getElementById('orderDetailModal').style.display = 'none';
    }
</script>