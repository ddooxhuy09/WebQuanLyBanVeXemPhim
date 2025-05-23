<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Admin - <tiles:getAsString name="title" /></title>
<!-- lấy title của các trang con được nhúng vào layout -->
<link rel="stylesheet"
	href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css"
	integrity="sha384-JcKb8q3iqJ61gNV9KGb8thSsNjpSL0n8PARn9HuZOnIxN0hoP+VmmDGMN5t9UJ0Z"
	crossorigin="anonymous">
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/cleave.js/1.6.0/cleave.min.js"></script>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css"
	integrity="sha512-Fo3rlrZj/k7ujTnHg4CGR2D7kSs0v4LLanw2qksYuRlEzO+tcaEPQogQ0KaoGN26/zrn20ImR1DfuLWnOo7aBA=="
	crossorigin="anonymous" referrerpolicy="no-referrer" />
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/admin/css/reset.css?v=1.3">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/admin/css/global.css?v=1.3">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/admin/css/sidebar.css?v=1.3">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/admin/css/home.css?v=1.3">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/admin/css/movies_manager.css?v=1.3">



<!-- Thêm mã JavaScript để khôi phục vị trí cuộn ngay lập tức -->
<script>
        // Khôi phục vị trí cuộn của sidebar ngay khi trang bắt đầu tải
        (function() {
            const scrollPosition = sessionStorage.getItem('sidebarScrollPosition');
            if (scrollPosition) {
                document.addEventListener('DOMContentLoaded', function() {
                    const sidebar = document.querySelector('.sidebar');
                    if (sidebar) {
                        sidebar.scrollTop = parseInt(scrollPosition);
                        sessionStorage.removeItem('sidebarScrollPosition');
                    }
                });
            }
        })();
    </script>
</head>
<body>
	<div class="container-wrapper d-flex">
		<!-- Sidebar với trạng thái thu nhỏ mặc định -->
		<div class="sidebar d-flex flex-column p-3 collapsed">
			<div
				class="sidebar-header d-flex justify-content-between align-items-center mb-4">
				<h2 class="text-center font-size-sm">Admin Panel</h2>
				<button class="toggle-btn" onclick="toggleSidebar()">
					<i class="fas fa-bars"></i>
				</button>
			</div>
			<!-- User Info -->
			<div class="user-info mb-4">
				<i class="fas fa-user-circle"></i> <span class="font-size-sm">Xin
					chào, Admin</span>
			</div>

			<!-- Quản Lý Chung -->
			<a href="${pageContext.request.contextPath}/admin/dashboard"
				class="nav-link font-size-sm" title="Trang Chủ"> <i
				class="fas fa-home"></i> <span>Trang Chủ</span>
			</a>

			<!-- Quản Lý Rạp & Chiếu Phim -->
			<div class="nav-section-title font-size-sm">Quản Lý Rạp & Chiếu
				Phim</div>
			<a href="${pageContext.request.contextPath}/admin/movies"
				class="nav-link font-size-sm" title="Quản Lý Phim"> <i
				class="fas fa-film"></i> <span>Quản Lý Phim</span>
			</a> 
			<a href="${pageContext.request.contextPath}/admin/showtimes"
				class="nav-link font-size-sm" title="Quản Lý Suất Chiếu"> <i
				class="fas fa-calendar-alt"></i> <span>Quản Lý Suất Chiếu</span>
			</a> 
			<a href="${pageContext.request.contextPath}/admin/theater-rooms"
				class="nav-link font-size-sm" title="Quản Lý Phòng Chiếu"> <i
				class="fas fa-theater-masks"></i> <span>Quản Lý Phòng Chiếu</span>
			</a> 
			<a href="${pageContext.request.contextPath}/admin/theaters"
				class="nav-link font-size-sm" title="Quản Lý Rạp Chiếu"> <i
				class="fas fa-building"></i> <span>Quản Lý Rạp Chiếu</span>
			</a>

			<!-- Quản Lý Vé & Giao Dịch -->
			<a href="${pageContext.request.contextPath}/admin/orders"
				class="nav-link font-size-sm" title="Quản Lý Đơn Hàng"> <i
				class="fas fa-shopping-cart"></i> <span>Quản Lý Đơn Hàng</span>
			</a> 
			<a href="${pageContext.request.contextPath}/admin/payment"
				class="nav-link font-size-sm" title="Quản Lý Thanh Toán"> <i
				class="fas fa-money-bill"></i> <span>Quản Lý Thanh Toán</span>
			</a>

			<!-- Quản Lý Thực Phẩm -->
			<div class="nav-section-title font-size-sm">Quản Lý Thực Phẩm</div>
			<a href="${pageContext.request.contextPath}/admin/food-combo"
				class="nav-link font-size-sm" title="Quản Lý Bắp Nước và Combo">
				<i class="fas fa-box"></i> <span>Quản Lý Bắp Nước và Combo</span>
			</a>

			<!-- Quản Lý Khách Hàng & Ưu Đãi -->
			<div class="nav-section-title font-size-sm">Quản Lý Khách Hàng
				& Ưu Đãi</div>
			<a href="${pageContext.request.contextPath}/admin/customers"
				class="nav-link font-size-sm" title="Quản Lý Khách Hàng"> <i
				class="fas fa-users"></i> <span>Quản Lý Khách Hàng</span>
			</a> 
			<a href="${pageContext.request.contextPath}/admin/promotions"
				class="nav-link font-size-sm" title="Quản Lý Khuyến Mãi"> <i
				class="fas fa-gift"></i> <span>Quản Lý Khuyến Mãi</span>
			</a> 
			<a href="${pageContext.request.contextPath}/admin/point-redemptions"
				class="nav-link font-size-sm" title="Quản Lý Quy Đổi Điểm"> <i
				class="fas fa-exchange-alt"></i> <span>Quản Lý Quy Đổi Điểm</span>
			</a> 
			<a href="${pageContext.request.contextPath}/admin/surcharges"
				class="nav-link font-size-sm" title="Quản Lý Phụ Thu"> <i
				class="fas fa-plus-circle"></i> <span>Quản Lý Phụ Thu</span>
			</a>

			<!-- Hệ Thống -->
			<div class="nav-section-title font-size-sm">Hệ Thống</div>
			<a href="${pageContext.request.contextPath}/admin/auth/logout"
				class="nav-link font-size-sm" title="Đăng Xuất"> <i
				class="fas fa-sign-out-alt"></i> <span>Đăng Xuất</span>
			</a>
		</div>

		<!-- Main Content với Bootstrap Grid -->
		<div class="main-content flex-grow-1 expanded">
			<!-- Thanh tìm kiếm -->
			<!-- <div class="search-bar mb-4">
				<form class="form-inline justify-content-center">
					<div class="input-group col-12 col-sm-8 col-md-6 col-lg-4">
						<input type="text" class="form-control" placeholder="Tìm kiếm..."
							aria-label="Search">
						<div class="input-group-append">
							<button class="btn btn-outline" type="button">
								<i class="fas fa-search"></i>
							</button>
						</div>
					</div>
				</form>
			</div> -->

			<!-- Nội dung động -->
			<div id="content">
				<tiles:insertAttribute name="body" />
			</div>
		</div>
	</div>

	<!-- Nhúng Bootstrap JS và Popper.js -->
	<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"
		integrity="sha384-DfXdz2htPH0lsSSs5nCTpuj/zy4C+OGpamoFVy38MVBnE+IbbVYUew+OrCXaRkfj"
		crossorigin="anonymous"></script>
	<script
		src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.5.4/dist/umd/popper.min.js"
		integrity="sha384-q2kxQ16AaE6UbzuKqyBE9/u/KzioAlnx2maXQHiDX9d4/zp8Ok3f+M7DPm+Ib6IU"
		crossorigin="anonymous"></script>
	<script
		src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"
		integrity="sha384-B4gt1jrGC7Jh4AgTPSdUtOBvfO8shuf57BaghqFfPlYxofvL8/KUEfYiJOMMV+rV"
		crossorigin="anonymous"></script>
	<script
		src="${pageContext.request.contextPath}/resources/admin/js/sidebar.js"></script>
	<script>
        document.addEventListener('DOMContentLoaded', function () {
            const navLinks = document.querySelectorAll('.nav-link');
            const sidebar = document.querySelector('.sidebar');
            const mainContent = document.querySelector('.main-content');

            // Function to set the active link based on the current URL
            function setActiveLink() {
                const currentPath = window.location.pathname;

                navLinks.forEach(nav => nav.classList.remove('active'));

                navLinks.forEach(link => {
                    const linkPath = link.getAttribute('href');
                    const contextPath = "${pageContext.request.contextPath}";
                    const fullLinkPath = linkPath.startsWith(contextPath) ? linkPath : contextPath + linkPath;

                    if (currentPath === fullLinkPath || currentPath === linkPath) {
                        link.classList.add('active');
                    }
                });
            }

            // Restore scroll position of the sidebar on page load
            function restoreSidebarScrollPosition() {
                const scrollPosition = sessionStorage.getItem('sidebarScrollPosition');
                if (scrollPosition && sidebar) {
                    sidebar.scrollTop = parseInt(scrollPosition);
                    sessionStorage.removeItem('sidebarScrollPosition');
                }
            }

            // Set active link and restore sidebar scroll position on page load
            setActiveLink();
            restoreSidebarScrollPosition();

            // Add click event listeners to update the active link and save sidebar scroll position
            navLinks.forEach(link => {
                link.addEventListener('click', function (e) {
                    // Remove active class from all links
                    navLinks.forEach(nav => nav.classList.remove('active'));

                    // Add active class to the clicked link
                    this.classList.add('active');

                    // Save current sidebar scroll position before navigation
                    if (sidebar) {
                        sessionStorage.setItem('sidebarScrollPosition', sidebar.scrollTop);
                    }

                    // Ensure sidebar remains collapsed after navigation
                    sidebar.classList.add('collapsed');
                    mainContent.classList.add('expanded');
                });
            });
        });

        function toggleSidebar() {
            const sidebar = document.querySelector('.sidebar');
            const mainContent = document.querySelector('.main-content');
            sidebar.classList.toggle('collapsed');
            mainContent.classList.toggle('expanded');
        }
    </script>

	<style>
.view-mode {
	display: block;
}

.edit-mode {
	display: none;
}

.detail-field img, .detail-field video {
	margin-top: 5px;
	border: 1px solid #ddd;
	border-radius: 4px;
}

.form-text {
	font-size: 0.85em;
	color: #6c757d;
}
</style>

</body>
</html>