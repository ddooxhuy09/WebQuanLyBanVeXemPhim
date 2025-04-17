<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản Lý Đơn Hàng</title>
    <!-- CSS đã được tích hợp trong layout.jsp -->
</head>
<body>
    <div class="order-management">
        <h2>Danh Sách Đơn Hàng</h2>

        <!-- Sort Section -->
        <div class="sort-section">
            <label for="sortOptions">Sắp xếp theo:</label>
            <select id="sortOptions" onchange="sortOrders(this.value)">
                <option value="date-desc" ${sortBy == 'date-desc' ? 'selected' : ''}>Ngày đặt mới nhất</option>
                <option value="date-asc" ${sortBy == 'date-asc' ? 'selected' : ''}>Ngày đặt cũ nhất</option>
                <option value="price-desc" ${sortBy == 'price-desc' ? 'selected' : ''}>Giá cao đến thấp</option>
                <option value="price-asc" ${sortBy == 'price-asc' ? 'selected' : ''}>Giá thấp đến cao</option>
                <option value="order-id-asc" ${sortBy == 'order-id-asc' ? 'selected' : ''}>Mã đơn hàng A-Z</option>
                <option value="order-id-desc" ${sortBy == 'order-id-desc' ? 'selected' : ''}>Mã đơn hàng Z-A</option>
                <option value="customer-asc" ${sortBy == 'customer-asc' ? 'selected' : ''}>Tên khách hàng A-Z</option>
                <option value="customer-desc" ${sortBy == 'customer-desc' ? 'selected' : ''}>Tên khách hàng Z-A</option>
            </select>
        </div>

        <!-- Error Message -->
        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <!-- Order List -->
        <div class="table-responsive">
            <table class="table table-bordered">
                <thead>
                    <tr>
                        <th>Mã Đơn Hàng</th>
                        <th>Khách Hàng</th>
                        <th>Tổng Tiền</th>
                        <th>Ngày Đặt</th>
                        <th>Trạng Thái</th>
                        <th>Chi Tiết</th>
                    </tr>
                </thead>
                <tbody id="orderList">
                    <c:choose>
                        <c:when test="${empty donHangList}">
                            <tr><td colspan="6" class="no-data">Không có dữ liệu đơn hàng</td></tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="donHang" items="${donHangList}">
                                <tr data-order-id="${donHang.maDonHang}">
                                    <td>${donHang.maDonHang}</td>
                                    <td>${donHang.khachHang.tenKhachHang}</td>
                                    <td><fmt:formatNumber value="${donHang.tongTien}" type="currency" currencyCode="VND" /></td>
                                    <td><fmt:formatDate value="${donHang.ngayDat}" pattern="dd-MM-yyyy" /></td>
                                    <td>${donHang.datHang ? 'Đã xác nhận' : 'Chưa xác nhận'}</td>
                                    <td>
                                        <button class="custom-btn btn-sm" onclick="showOrderDetail('${donHang.maDonHang}')">Chi Tiết</button>
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
                        <a class="page-link" href="?page=${currentPage - 1}&sort=${sortBy}">Trước</a>
                    </li>
                </c:if>
                <c:forEach begin="1" end="${totalPages}" var="i">
                    <li class="page-item ${i == currentPage ? 'active' : ''}">
                        <a class="page-link" href="?page=${i}&sort=${sortBy}">${i}</a>
                    </li>
                </c:forEach>
                <c:if test="${currentPage < totalPages}">
                    <li class="page-item">
                        <a class="page-link" href="?page=${currentPage + 1}&sort=${sortBy}">Sau</a>
                    </li>
                </c:if>
            </c:if>
        </div>
    </div>

    <!-- Order Details Modal -->
    <div class="modal" id="orderDetailModal" style="display: none;">
        <div class="modal-content">
            <h2>Thông Tin Đơn Hàng</h2>

            <!-- Order Information Section -->
            <div class="info-section">
                <div class="row">
                    <div class="col-4">Mã đơn hàng:</div>
                    <div class="col-8" id="modalMaDonHang"></div>
                </div>
                <div class="row">
                    <div class="col-4">Phim:</div>
                    <div class="col-8" id="modalTenPhim"></div>
                </div>
                <div class="row">
                    <div class="col-4">Giờ chiếu:</div>
                    <div class="col-8 highlight" id="modalGioChieu"></div>
                </div>
                <div class="row">
                    <div class="col-4">Ngày chiếu:</div>
                    <div class="col-8" id="modalNgayChieu"></div>
                </div>
                <div class="row">
                    <div class="col-4">Phòng chiếu:</div>
                    <div class="col-8" id="modalPhongChieu"></div>
                </div>
                <div class="row">
                    <div class="col-4">Rạp chiếu:</div>
                    <div class="col-8" id="modalRapChieu"></div>
                </div>
                <div class="row">
                    <div class="col-4">Ngày đặt:</div>
                    <div class="col-8" id="modalNgayDat"></div>
                </div>
            </div>

            <!-- Customer Information Section -->
            <div class="customer-section">
                <h3>Thông Tin Khách Hàng</h3>
                <div class="row">
                    <div class="col-4">Khách hàng:</div>
                    <div class="col-8" id="modalTenKhachHang"></div>
                </div>
                <div class="row">
                    <div class="col-4">Điện thoại:</div>
                    <div class="col-8" id="modalDienThoai"></div>
                </div>
                <div class="row">
                    <div class="col-4">Email:</div>
                    <div class="col-8" id="modalEmail"></div>
                </div>
            </div>

            <!-- Invoice Information Section -->
            <div class="invoice-section">
                <h3>Thông Tin Hóa Đơn</h3>
                <div class="row">
                    <div class="col-4">Trạng thái:</div>
                    <div class="col-8" id="modalTrangThai"></div>
                </div>
                <div class="row">
                    <div class="col-4">Mã giảm giá:</div>
                    <div class="col-8" id="modalMaGiamGia"></div>
                </div>
                <div class="row">
                    <div class="col-4">Giảm giá:</div>
                    <div class="col-8" id="modalGiamGia"></div>
                </div>
                <div class="row">
                    <div class="col-4">Phụ thu:</div>
                    <div class="col-8" id="modalPhuThu"></div>
                </div>
                <div class="row">
                    <div class="col-4">Thành tiền:</div>
                    <div class="col-8" id="modalThanhTien"></div>
                </div>
                <div class="row">
                    <div class="col-4">Tổng tiền:</div>
                    <div class="col-8" id="modalTongTien"></div>
                </div>
            </div>

            <!-- Tickets Section -->
            <div class="tickets-section">
                <h3>Ghế & Dịch Vụ</h3>
                <table class="table table-bordered">
                    <thead>
                        <tr>
                            <th>Thông tin ghế</th>
                            <th>Loại ghế</th>
                            <th>Giá tiền</th>
                        </tr>
                    </thead>
                    <tbody id="modalTicketList"></tbody>
                </table>
            </div>

            <!-- Combos Section -->
            <div class="combos-section">
                <table class="table table-bordered">
                    <thead>
                        <tr>
                            <th>Tên dịch vụ</th>
                            <th>Số lượng</th>
                            <th>Đơn giá</th>
                            <th>Tổng tiền</th>
                        </tr>
                    </thead>
                    <tbody id="modalComboList"></tbody>
                </table>
                <div class="total" id="modalComboTotal"></div>
            </div>

            <div class="modal-actions">
                <button class="custom-btn" onclick="closeOrderDetailModal()">Đóng</button>
            </div>
        </div>
    </div>

    <script>
        // Function to handle sorting
        function sortOrders(criteria) {
            window.location.href = "${pageContext.request.contextPath}/admin/orders?page=1&sort=" + criteria;
        }

        // Function to show order details in modal
        function showOrderDetail(maDonHang) {
            fetch("${pageContext.request.contextPath}/admin/orders/detail/" + maDonHang)
                .then(response => response.json())
                .then(order => {
                    if (!order) return;

                    // Populate order information
                    document.getElementById('modalMaDonHang').textContent = order.maDonHang;
                    document.getElementById('modalTenPhim').textContent = order.tenPhim || "N/A";
                    document.getElementById('modalGioChieu').textContent = order.gioChieu || "N/A";
                    document.getElementById('modalNgayChieu').textContent = order.ngayChieu || "N/A";
                    document.getElementById('modalPhongChieu').textContent = order.phongChieu || "N/A";
                    document.getElementById('modalRapChieu').textContent = order.rapChieu || "N/A";
                    document.getElementById('modalNgayDat').textContent = order.ngayDat;

                    // Populate customer information
                    document.getElementById('modalTenKhachHang').textContent = order.tenKhachHang;
                    document.getElementById('modalDienThoai').textContent = order.dienThoai || "N/A";
                    document.getElementById('modalEmail').textContent = order.email || "N/A";

                    // Populate invoice information
                    document.getElementById('modalTrangThai').textContent = order.trangThai;
                    document.getElementById('modalMaGiamGia').textContent = order.maKhuyenMai || "Không có";
                    document.getElementById('modalGiamGia').textContent = order.giamGia;
                    document.getElementById('modalPhuThu').textContent = order.phuThu;
                    document.getElementById('modalThanhTien').textContent = order.thanhTien;
                    document.getElementById('modalTongTien').textContent = order.tongTien;

                    // Populate tickets
                    const ticketList = document.getElementById('modalTicketList');
                    ticketList.innerHTML = '';
                    order.tickets.forEach(ticket => {
                        const row = document.createElement('tr');
                        row.innerHTML = `
                            <td>${ticket.thongTinGhe}</td>
                            <td>${ticket.loaiGhe}</td>
                            <td>${ticket.giaTien}</td>`;
                        ticketList.appendChild(row);
                    });

                    // Populate combos
                    const comboList = document.getElementById('modalComboList');
                    comboList.innerHTML = '';
                    order.combos.forEach(combo => {
                        const row = document.createElement('tr');
                        row.innerHTML = `
                            <td>${combo.tenDichVu}</td>
                            <td>${combo.soLuong}</td>
                            <td>${combo.donGia}</td>
                            <td>${combo.tongTien}</td>`;
                        comboList.appendChild(row);
                    });

                    // Display combo total
                    document.getElementById('modalComboTotal').textContent = `Tổng tiền: ${order.comboTotal}`;

                    // Show modal
                    document.getElementById('orderDetailModal').style.display = 'flex';
                })
                .catch(error => {
                    console.error('Error fetching order details:', error);
                    alert('Lỗi khi lấy chi tiết đơn hàng');
                });
        }

        // Function to close order detail modal
        function closeOrderDetailModal() {
            document.getElementById('orderDetailModal').style.display = 'none';
        }
    </script>
</body>
</html>