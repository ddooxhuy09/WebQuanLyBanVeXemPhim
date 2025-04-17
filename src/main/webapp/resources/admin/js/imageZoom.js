// imageZoom.js

// Hàm mở modal phóng to hình ảnh
function openImageZoomModal(imageSrc) {
    const modal = document.getElementById('imageZoomModal');
    const zoomedImage = document.getElementById('zoomedImage');
    if (modal && zoomedImage) {
        zoomedImage.src = imageSrc;

        // Tạo một hình ảnh tạm để lấy kích thước thực tế
        const tempImage = new Image();
        tempImage.src = imageSrc;
        tempImage.onload = function () {
            const naturalWidth = tempImage.naturalWidth;
            const naturalHeight = tempImage.naturalHeight;
            const aspectRatio = naturalWidth / naturalHeight;

            // Tính toán kích thước tối đa dựa trên màn hình
            const maxWidth = window.innerWidth * 0.9;
            const maxHeight = window.innerHeight * 0.7;

            let newWidth, newHeight;

            if (aspectRatio > 1) {
                newWidth = Math.min(naturalWidth, maxWidth);
                newHeight = newWidth / aspectRatio;
                if (newHeight > maxHeight) {
                    newHeight = maxHeight;
                    newWidth = newHeight * aspectRatio;
                }
            } else {
                newHeight = Math.min(naturalHeight, maxHeight);
                newWidth = newHeight * aspectRatio;
                if (newWidth > maxWidth) {
                    newWidth = maxWidth;
                    newHeight = newWidth / aspectRatio;
                }
            }

            zoomedImage.style.width = `${newWidth}px`;
            zoomedImage.style.height = `${newHeight}px`;

            modal.style.display = 'flex';
        };
    }
}

// Hàm đóng modal phóng to hình ảnh
function closeImageZoomModal() {
    const modal = document.getElementById('imageZoomModal');
    if (modal) {
        modal.style.display = 'none';
    }
}

// Gắn sự kiện cho tất cả button chứa hình ảnh
function initImageZoom() {
    document.querySelectorAll('.image-zoom-btn').forEach(button => {
        button.addEventListener('click', () => {
            const image = button.querySelector('img');
            if (image) {
                openImageZoomModal(image.src);
            }
        });
    });
}

// Gọi hàm khởi tạo khi DOM đã tải xong
document.addEventListener('DOMContentLoaded', initImageZoom);

// Xuất các hàm để có thể sử dụng ở nơi khác nếu cần
window.openImageZoomModal = openImageZoomModal;
window.closeImageZoomModal = closeImageZoomModal;
window.initImageZoom = initImageZoom;