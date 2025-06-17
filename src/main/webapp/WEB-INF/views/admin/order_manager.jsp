<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!-- Include currency-utils.js -->
<script src="${pageContext.request.contextPath}/resources/admin/js/currency-utils.js"></script>

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
                    <th>Chi Tiết</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${empty donHangList}">
                        <tr><td colspan="5" class="no-data">Không có dữ liệu đơn hàng</td></tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="donHang" items="${donHangList}">
                            <tr data-order-id="${donHang.maDonHang}">
                                <td>${donHang.maDonHang}</td>
                                <td>${donHang.khachHang.getHoVaTen()}</td>
                                <td>
                                    <script>document.write(formatCurrencyWithDecimal(${donHang.tongTien != null ? donHang.tongTien : 0}));</script>
                                </td>
                                <td><fmt:formatDate value="${donHang.ngayDat}" pattern="dd/MM/yyyy" /></td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/orders/detail/${donHang.maDonHang}?page=${currentPage}&sort=${sortBy}" class="custom-btn btn-sm">Chi Tiết</a>
                                </td>
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
                    <a class="page-link" href="?page=${currentPage - 1}&sort=${sortBy}">Trước</a>
                </li>
            </c:if>
            <c:forEach var="i" items="${pageRange}">
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

    <!-- Order Details Modal -->
    <c:if test="${showDetail}">
        <div class="modal" style="display: flex;">
            <div class="modal-content">
                <h2>Thông Tin Đơn Hàng</h2>

                <!-- Order Information Section -->
                <div class="info-section">
                    <div class="row">
                        <div class="col-4">Mã đơn hàng:</div>
                        <div class="col-8">${maDonHang}</div>
                    </div>
                    <div class="row">
                        <div class="col-4">Phim:</div>
                        <div class="col-8">${tenPhim}</div>
                    </div>
                    <div class="row">
                        <div class="col-4">Giờ chiếu:</div>
                        <div class="col-8 highlight">${gioChieu}</div>
                    </div>
                    <div class="row">
                        <div class="col-4">Ngày chiếu:</div>
                        <div class="col-8">${ngayChieu}</div>
                    </div>
                    <div class="row">
                        <div class="col-4">Phòng chiếu:</div>
                        <div class="col-8">${phongChieu}</div>
                    </div>
                    <div class="row">
                        <div class="col-4">Rạp chiếu:</div>
                        <div class="col-8">${rapChieu}</div>
                    </div>
                    <div class="row">
                        <div class="col-4">Ngày đặt:</div>
                        <div class="col-8">${ngayDat}</div>
                    </div>
                </div>

                <!-- Customer Information Section -->
                <div class="customer-section">
                    <h3>Thông Tin Khách Hàng</h3>
                    <div class="row">
                        <div class="col-4">Khách hàng:</div>
                        <div class="col-8">${tenKhachHang}</div>
                    </div>
                    <div class="row">
                        <div class="col-4">Điện thoại:</div>
                        <div class="col-8">${dienThoai}</div>
                    </div>
                    <div class="row">
                        <div class="col-4">Email:</div>
                        <div class="col-8">${email}</div>
                    </div>
                </div>

                <!-- Invoice Information Section -->
                <div class="invoice-section">
                    <h3>Thông Tin Hóa Đơn</h3>
                    <div class="row">
                        <div class="col-4">Mã code:</div>
                        <div class="col-4">${maCode}</div>
                    </div>
                    <div class="row">
                        <div class="col-4">Điểm sử dụng:</div>
                        <div class="col-8">${diemSuDung != null ? diemSuDung : 0} điểm</div>
                    </div>
                    <div class="row">
                        <div class="col-4">Tổng tiền:</div>
                        <div class="col-8">
                            <script>document.write(formatCurrencyWithDecimal(${tongTien != null ? tongTien : 0}));</script>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-4">Phương thức thanh toán:</div>
                        <div class="col-8">${phuongThucThanhToan}</div>
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
                        <tbody>
                            <c:forEach var="ticket" items="${tickets}">
                                <tr>
                                    <td>${ticket.thongTinGhe}</td>
                                    <td>${ticket.loaiGhe}</td>
                                    <td>
                                        <script>document.write(formatCurrencyWithDecimal(${ticket.giaTien != null ? ticket.giaTien : 0}));</script>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
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
                        <tbody>
                            <c:forEach var="combo" items="${combos}">
                                <tr>
                                    <td>${combo.tenDichVu}</td>
                                    <td>${combo.soLuong}</td>
                                    <td>
                                        <script>document.write(formatCurrencyWithDecimal(${combo.donGia != null ? combo.donGia : 0}));</script>
                                    </td>
                                    <td>
                                        <script>document.write(formatCurrencyWithDecimal(${combo.tongTien != null ? combo.tongTien : 0}));</script>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                    <div class="total">
                        <script>document.write(formatCurrencyWithDecimal(${comboTotal != null ? comboTotal : 0}));</script>
                    </div>
                </div>

                <div class="modal-actions">
                    <a href="${pageContext.request.contextPath}/admin/orders?page=${currentPage}&sort=${sortBy}" class="custom-btn">Đóng</a>
                </div>
            </div>
        </div>
    </c:if>
</div>

<script>
    // Function to handle sorting
    function sortOrders(criteria) {
        window.location.href = "${pageContext.request.contextPath}/admin/orders?page=1&sort=" + criteria;
    }
</script>