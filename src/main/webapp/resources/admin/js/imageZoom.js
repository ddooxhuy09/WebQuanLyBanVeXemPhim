function openImageZoomModal(imageSrc) {
    try {
        const modal = document.getElementById('imageZoomModal');
        const zoomedImage = document.getElementById('zoomedImage');
        if (!modal || !zoomedImage) {
            console.error('Image zoom modal or image element not found');
            return;
        }
        zoomedImage.src = imageSrc;

        const tempImage = new Image();
        tempImage.src = imageSrc;
        tempImage.onload = function () {
            const naturalWidth = tempImage.naturalWidth;
            const naturalHeight = tempImage.naturalHeight;
            const aspectRatio = naturalWidth / naturalHeight;

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
        tempImage.onerror = () => console.error(`Failed to load image: ${imageSrc}`);
    } catch (error) {
        console.error('Error opening image zoom modal:', error);
    }
}

function closeImageZoomModal() {
    try {
        const modal = document.getElementById('imageZoomModal');
        if (modal) {
            modal.style.display = 'none';
        }
    } catch (error) {
        console.error('Error closing image zoom modal:', error);
    }
}

function initImageZoom() {
    try {
        document.querySelectorAll('.image-zoom-btn').forEach(button => {
            button.addEventListener('click', () => {
                const image = button.querySelector('img');
                if (image) {
                    openImageZoomModal(image.src);
                }
            });
        });
    } catch (error) {
        console.error('Error initializing image zoom:', error);
    }
}

document.addEventListener('DOMContentLoaded', initImageZoom);
window.openImageZoomModal = openImageZoomModal;
window.closeImageZoomModal = closeImageZoomModal;
window.initImageZoom = initImageZoom;