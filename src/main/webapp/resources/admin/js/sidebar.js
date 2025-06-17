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
            const contextPath = window.contextPath || '';
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
            navLinks.forEach(nav => nav.classList.remove('active'));
            this.classList.add('active');
            if (sidebar) {
                sessionStorage.setItem('sidebarScrollPosition', sidebar.scrollTop);
            }
            if (sidebar && mainContent) {
                sidebar.classList.add('collapsed');
                mainContent.classList.add('expanded');
            }
        });
    });
});

// Hàm toggleSidebar cho nút toggle
function toggleSidebar() {
    console.log("toggleSidebar called");
    const sidebar = document.querySelector('.sidebar');
    const mainContent = document.querySelector('.main-content');
    if (sidebar && mainContent) {
        sidebar.classList.toggle('collapsed');
        mainContent.classList.toggle('expanded');
        console.log("Sidebar collapsed: ", sidebar.classList.contains('collapsed'));
    } else {
        console.error("Sidebar or main-content not found");
    }
}