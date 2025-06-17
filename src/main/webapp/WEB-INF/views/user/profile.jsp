<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/user/css/styles.css?v=1.2" />
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
    <title>Thông Tin Khách Hàng - Galaxy Cinema</title>
    <style>
      .container { max-width: 1200px; margin: 2rem auto; padding: 0 1.5rem; }
      .error-message, .success-message { text-align: center; margin: 20px 0; padding: 10px; border-radius: 5px; font-size: 16px; }
      .error-message { color: #d32f2f; background-color: #ffe6e6; }
      .success-message { color: #2e7d32; background-color: #e8f5e9; }
      .order-history table { width: 100%; border-collapse: collapse; margin-top: 1rem; }
      .order-history th, .order-history td { padding: 12px 15px; border: 1px solid #ddd; text-align: left; font-size: 15px; }
      .order-history th { background-color: #ff5722; color: black; font-weight: 600; }
      .order-history td { background-color: #fff; transition: background-color 0.2s; cursor: pointer; }
      .order-history tr:hover td { background-color: #fff3e0; }
      .order-history .no-orders { text-align: center; padding: 20px; font-size: 16px; color: #666; }
      .modal { display: none; position: fixed; z-index: 50; left: 0; top: 0; width: 100%; height: 100%; overflow: auto; background-color: rgba(0,0,0,0.4); }
      .modal-content { background-color: #fff; margin: 5% auto; padding: 20px; border: 1px solid #888; width: 90%; max-width: 800px; border-radius: 10px; }
      .close { color: #aaa; float: right; font-size: 28px; font-weight: bold; cursor: pointer; }
      .close:hover, .close:focus { color: black; text-decoration: none; cursor: pointer; }
      .info__money__rating { position: relative; }
      .Rating_progress__session__difgK { height: 4px; background-color: #F58020; position: absolute; bottom: 0; transition: width 0.3s; }
      .Rating_steps__QaDG4 { position: absolute; bottom: 0; transform: translateX(-50%); }
      .Rating_index__teps__NlmMM { width: 4px; height: 4px; background-color: #034EA2; border-radius: 50%; position: absolute; bottom: 0; transform: translateX(-50%); }
      .modal-content th { background-color: #ff5722; color: black; }
    </style>
  </head>
  <body>
    <nav class="navbar">
        <div class="container-nav">
            <div class="navbar-brand">
                <a href="${pageContext.request.contextPath}/home/" class="logo">Galaxy Cinema</a>
            </div>
            <button class="navbar-toggle" aria-label="Toggle navigation">
                <span></span><span></span><span></span>
            </button>
            <ul class="nav-links">
                <li><a href="${pageContext.request.contextPath}/home/">Phim</a></li>
                <c:choose>
                    <c:when test="${not empty sessionScope.loggedInUser}">
                        <li><a href="${pageContext.request.contextPath}/user/profile">Xin chào, ${sessionScope.loggedInUser.tenKhachHang}</a></li>
                        <li><a href="${pageContext.request.contextPath}/auth/logout" class="login-btn">Đăng Xuất</a></li>
                    </c:when>
                    <c:otherwise>
                        <li><a href="${pageContext.request.contextPath}/auth/login" class="login-btn">Đăng Nhập</a></li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </div>
    </nav>

    <div class="container">
      <c:if test="${not empty error}">
        <div class="error-message">${error}</div>
      </c:if>
      <c:if test="${not empty success}">
        <div class="success-message">${success}</div>
      </c:if>
      <div class="flex flex-col xl:flex-row xl:space-x-6">
        <!-- Left Section: Total Spending and Order History -->
        <div class="xl:w-2/3">
          <div class="info__money__rating py-6 xl:border-b border-[#ECECEC]">
            <div class="flex justify-between items-center">
              <p class="md:text-base xl:text-lg font-bold not-italic relative">
                Tổng chi tiêu 2025
                <span class="absolute w-[14px] h-[14px] rounded-full border border-[#034EA2] -right-[9%] top-[0%] leading-[10px] lg:leading-[8px] text-center">
                  <svg aria-hidden="true" focusable="false" data-prefix="fas" data-icon="info" class="svg-inline--fa fa-info text-[#034EA2] text-[10px]" role="img" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 192 512">
                    <path fill="currentColor" d="M48 80a48 48 0 1 1 96 0A48 48 0 1 1 48 80zM0 224c0-17.7 14.3-32 32-32H96c17.7 0 32 14.3 32 32V448h32c17.7 0 32 14.3 32 32s-14.3 32-32 32H32c-17.7 0-32-14.3-32-32s14.3-32 32-32H64V256H32c-17.7 0-32-14.3-32-32z"></path>
                  </svg>
                </span>
              </p>
              <span class="md:text-base xl:text-lg font-bold not-italic text-[#F58020]">
                <fmt:formatNumber value="${totalSpending}" pattern="#,###₫" />
              </span>
            </div>
          </div>

          <div class="order-history mt-6">
            <h2 class="text-2xl font-bold text-[#ff5722] mb-4 border-b-2 border-[#ff5722] pb-2">Lịch Sử Đơn Hàng</h2>
            <c:choose>
              <c:when test="${not empty donHangList}">
                <table>
                  <thead>
                    <tr>
                      <th>Mã Đơn Hàng</th>
                      <th>Ngày Đặt</th>
                      <th>Tổng Tiền</th>
                      <th>Trạng Thái</th>
                    </tr>
                  </thead>
                  <tbody>
                    <c:forEach var="donHang" items="${donHangList}">
                      <c:if test="${donHang.datHang}">
                        <tr class="order-row" data-ma-don-hang="${donHang.maDonHang}">
                          <td>${donHang.maDonHang}</td>
                          <td><fmt:formatDate value="${donHang.ngayDat}" pattern="dd/MM/yyyy" /></td>
                          <td><fmt:formatNumber value="${donHang.tongTien}" pattern="#,###₫" /></td>
                          <td>Đã đặt</td>
                        </tr>
                      </c:if>
                    </c:forEach>
                  </tbody>
                </table>
              </c:when>
              <c:otherwise>
                <p class="no-orders">Chưa có đơn hàng nào.</p>
              </c:otherwise>
            </c:choose>
          </div>
        </div>

        <!-- Right Section: Customer Information -->
        <div class="xl:w-1/3 mt-6 xl:mt-0">
          <div class="card p-6 shadow-lg rounded-lg bg-white">
            <h2 class="text-2xl font-bold text-[#ff5722] mb-4 border-b-2 border-[#ff5722] pb-2">Thông Tin Khách Hàng</h2>
            <div class="space-y-3">
              <p><strong class="text-[#ff5722]">Họ:</strong> ${user.hoKhachHang}</p>
              <p><strong class="text-[#ff5722]">Tên:</strong> ${user.tenKhachHang}</p>
              <p><strong class="text-[#ff5722]">Số điện thoại:</strong> ${user.soDienThoai}</p>
              <p><strong class="text-[#ff5722]">Email:</strong> ${user.email}</p>
              <p><strong class="text-[#ff5722]">Ngày đăng ký:</strong> <fmt:formatDate value="${user.ngayDangKy}" pattern="dd/MM/yyyy" /></p>
              <p>
                <strong class="text-[#ff5722]">Mật khẩu:</strong> *******
                <button id="changePasswordBtn" class="ml-2 text-[#ff5722] underline hover:text-[#e64a19]">(Thay đổi)</button>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Change Password Modal -->
    <div id="changePasswordModal" class="modal">
      <div class="modal-content">
        <span class="close">×</span>
        <h2 class="text-2xl font-bold text-[#ff5722] mb-4">Đổi Mật Khẩu</h2>
        <form action="${pageContext.request.contextPath}/user/change-password" method="post" class="space-y-4">
          <div>
            <label for="currentPassword" class="block text-sm font-medium text-gray-700">Mật khẩu hiện tại:</label>
            <input type="password" id="currentPassword" name="currentPassword" required class="mt-1 block w-full p-2 border border-gray-300 rounded-md">
          </div>
          <div>
            <label for="newPassword" class="block text-sm font-medium text-gray-700">Mật khẩu mới:</label>
            <input type="password" id="newPassword" name="newPassword" required class="mt-1 block w-full p-2 border border-gray-300 rounded-md">
          </div>
          <div>
            <label for="confirmPassword" class="block text-sm font-medium text-gray-700">Xác nhận mật khẩu mới:</label>
            <input type="password" id="confirmPassword" name="confirmPassword" required class="mt-1 block w-full p-2 border border-gray-300 rounded-md">
          </div>
          <button type="submit" class="w-full bg-[#ff5722] text-black p-2 rounded-md hover:bg-[#e64a19] font-bold">Đổi Mật Khẩu</button>
        </form>
      </div>
    </div>

    <!-- Order Details Modal -->
    <div id="orderDetailsModal" class="modal">
      <div class="modal-content">
        <span class="close">×</span>
        <h2 class="text-2xl font-bold text-[#ff5722] mb-4">Chi Tiết Đơn Hàng</h2>
        <div id="orderDetailsContent">
          <p class="text-center">Đang tải chi tiết đơn hàng...</p>
        </div>
      </div>
    </div>

    <footer class="footer">
        <div class="footer-content">
            <div class="footer-section">
                <h3>About Galaxy Cinema</h3>
                <p>Your premier destination for the latest movies and entertainment experiences.</p>
            </div>
            <div class="footer-section">
                <h3>Quick Links</h3>
                <ul class="footer-links">
                    <li><a href="#">Now Showing</a></li>
                    <li><a href="#">Coming Soon</a></li>
                    <li><a href="#">Promotions</a></li>
                    <li><a href="#">Gift Cards</a></li>
                </ul>
            </div>
            <div class="footer-section">
                <h3>Connect With Us</h3>
                <div class="social-links">
                    <a href="#">Facebook</a>
                    <a href="#">Twitter</a>
                    <a href="#">Instagram</a>
                    <a href="#">YouTube</a>
                </div>
            </div>
            <div class="footer-section">
                <h3>Newsletter</h3>
                <p>Subscribe for updates and exclusive offers</p>
                <form class="newsletter-form">
                    <input type="email" placeholder="Enter your email" />
                    <button type="submit">Subscribe</button>
                </form>
            </div>
        </div>
        <div class="footer-bottom">
            <p>© 2024 Galaxy Cinema. All rights reserved.</p>
        </div>
    </footer>

    <script>
      document.addEventListener('DOMContentLoaded', () => {
        // Navbar toggle
        const toggleButton = document.querySelector('.navbar-toggle');
        const navLinks = document.querySelector('.nav-links');
        const navbar = document.querySelector('.navbar');
        if (toggleButton && navLinks && navbar) {
          toggleButton.addEventListener('click', (e) => {
            e.stopPropagation();
            navLinks.classList.toggle('active');
            toggleButton.classList.toggle('open');
          });
          document.addEventListener('click', (e) => {
            if (!navbar.contains(e.target) && navLinks.classList.contains('active')) {
              navLinks.classList.remove('active');
              toggleButton.classList.remove('open');
            }
          });
          navLinks.addEventListener('click', (e) => {
            e.stopPropagation();
          });
        } else {
          console.error('Navbar toggle, nav-links, or navbar not found');
        }

        // Change Password Modal control
        const changePasswordModal = document.getElementById('changePasswordModal');
        const changePasswordBtn = document.getElementById('changePasswordBtn');
        const changePasswordClose = changePasswordModal.getElementsByClassName('close')[0];
        changePasswordBtn.onclick = () => changePasswordModal.style.display = 'block';
        changePasswordClose.onclick = () => changePasswordModal.style.display = 'none';
        window.onclick = (event) => {
          if (event.target == changePasswordModal) {
            changePasswordModal.style.display = 'none';
          } else if (event.target == orderDetailsModal) {
            orderDetailsModal.style.display = 'none';
          }
        };

        // Order Details Modal control
        const orderDetailsModal = document.getElementById('orderDetailsModal');
        const orderDetailsClose = orderDetailsModal.getElementsByClassName('close')[0];
        orderDetailsClose.onclick = () => orderDetailsModal.style.display = 'none';

        // Order row click event
        const orderRows = document.querySelectorAll('.order-row');
        orderRows.forEach(row => {
          row.addEventListener('click', () => {
            const maDonHang = row.getAttribute('data-ma-don-hang');
            console.log('Fetching details for maDonHang:', maDonHang);
            fetchOrderDetails(maDonHang);
            orderDetailsModal.style.display = 'block';
          });
        });

        function fetchOrderDetails(maDonHang) {
          const contentDiv = document.getElementById('orderDetailsContent');
          contentDiv.innerHTML = '<p class="text-center">Đang tải chi tiết đơn hàng...</p>';

          fetch('${pageContext.request.contextPath}/user/order-details?maDonHang=' + encodeURIComponent(maDonHang))
            .then(response => {
              console.log('Response status:', response.status);
              if (!response.ok) {
                throw new Error('HTTP error! Status: ' + response.status);
              }
              return response.json();
            })
            .then(data => {
              console.log('Response data:', data);
              if (data.error) {
                contentDiv.innerHTML = '<p class="text-red-500 text-center">' + data.error + '</p>';
                return;
              }

              let html = '<div class="space-y-4">';
              
              // Ve
              if (data.veList && data.veList.length > 0) {
                html += '<div>' +
                  '<h3 class="text-lg font-bold text-[#ff5722]">Vé</h3>' +
                  '<table class="w-full border-collapse">' +
                    '<thead>' +
                      '<tr>' +
                        '<th class="border p-2 bg-[#ff5722]">Loại Ghế</th>' +
                        '<th class="border p-2 bg-[#ff5722]">Giá Vé</th>' +
                      '</tr>' +
                    '</thead>' +
                    '<tbody>';
                data.veList.forEach(ve => {
                  html += '<tr>' +
                    '<td class="border p-2">' + (ve.tenLoaiGhe || '') + '</td>' +
                    '<td class="border p-2">' + (parseFloat(ve.giaVe || 0).toLocaleString('vi-VN')) + ' ₫</td>' +
                    '</tr>';
                });
                html += '</tbody></table></div>';
              }

              // Combo
              if (data.comboList && data.comboList.length > 0) {
                html += '<div>' +
                  '<h3 class="text-lg font-bold text-[#ff5722]">Combo</h3>' +
                  '<table class="w-full border-collapse">' +
                    '<thead>' +
                      '<tr>' +
                        '<th class="border p-2 bg-[#ff5722]">Tên Combo</th>' +
                        '<th class="border p-2 bg-[#ff5722]">Số Lượng</th>' +
                        '<th class="border p-2 bg-[#ff5722]">Giá Combo</th>' +
                      '</tr>' +
                    '</thead>' +
                    '<tbody>';
                data.comboList.forEach(combo => {
                  html += '<tr>' +
                    '<td class="border p-2">' + (combo.tenCombo || '') + '</td>' +
                    '<td class="border p-2">' + (combo.soLuong || 0) + '</td>' +
                    '<td class="border p-2">' + (parseFloat(combo.giaCombo || 0).toLocaleString('vi-VN')) + ' ₫</td>' +
                    '</tr>';
                });
                html += '</tbody></table></div>';
              }

              // Bap Nuoc
              if (data.bapNuocList && data.bapNuocList.length > 0) {
                html += '<div>' +
                  '<h3 class="text-lg font-bold text-[#ff5722]">Bắp Nước</h3>' +
                  '<table class="w-full border-collapse">' +
                    '<thead>' +
                      '<tr>' +
                        '<th class="border p-2 bg-[#ff5722]">Tên Bắp Nước</th>' +
                        '<th class="border p-2 bg-[#ff5722]">Số Lượng</th>' +
                        '<th class="border p-2 bg-[#ff5722]">Giá Bắp Nước</th>' +
                      '</tr>' +
                    '</thead>' +
                    '<tbody>';
                data.bapNuocList.forEach(bapNuoc => {
                  html += '<tr>' +
                    '<td class="border p-2">' + (bapNuoc.tenBapNuoc || '') + '</td>' +
                    '<td class="border p-2">' + (bapNuoc.soLuong || 0) + '</td>' +
                    '<td class="border p-2">' + (parseFloat(bapNuoc.giaBapNuoc || 0).toLocaleString('vi-VN')) + ' ₫</td>' +
                    '</tr>';
                });
                html += '</tbody></table></div>';
              }

              html += '</div>';
              contentDiv.innerHTML = html;
            })
            .catch(error => {
              console.error('Fetch error:', error);
              contentDiv.innerHTML = '<p class="text-red-500 text-center">Lỗi khi tải chi tiết đơn hàng: ' + error.message + '</p>';
            });
        }
      });
    </script>
  </body>
</html>