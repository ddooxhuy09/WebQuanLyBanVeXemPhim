export class MovieView {
    constructor() {
        this.movieContainer = document.querySelector('.movie-container');
        this.carouselInner = document.querySelector('.carousel-inner');
        this.leftBtn = document.querySelector('.left-btn');
        this.rightBtn = document.querySelector('.right-btn');
        this.currentIndex = 0;
        this.setupCarousel();
    }

    setupCarousel() {
        const images = this.carouselInner.querySelectorAll('img');
        if (images.length === 0) {
            console.warn('No images found in carousel');
            return;
        }
        this.carouselInner.style.transform = `translateX(0%)`;
        this.leftBtn.addEventListener('click', () => this.showPreviousImage(images));
        this.rightBtn.addEventListener('click', () => this.showNextImage(images));
    }

    showPreviousImage(images) {
        this.currentIndex = (this.currentIndex - 1 + images.length) % images.length;
        this.carouselInner.style.transform = `translateX(-${this.currentIndex * 100}%)`;
    }

    showNextImage(images) {
        this.currentIndex = (this.currentIndex + 1) % images.length;
        this.carouselInner.style.transform = `translateX(-${this.currentIndex * 100}%)`;
    }

    bindBookTicket(handler) {
        this.movieContainer.addEventListener('click', (event) => {
            if (event.target.classList.contains('btn') && !event.target.classList.contains('trailer-btn')) {
                const form = event.target.closest('form');
                if (form) {
                    const maPhim = form.querySelector('input[name="id"]').value;
                    handler(maPhim);
                    form.submit(); // Gửi form đến /movie-detail
                }
            }
        });
    }

    bindTrailerView(handler) {
        this.movieContainer.addEventListener('click', (event) => {
            if (event.target.classList.contains('trailer-btn')) {
                const movieItem = event.target.closest('.movie-item');
                const movieTitle = movieItem.querySelector('h3').textContent;
                handler(movieTitle);
            }
        });
    }

    render() {
        // Không cần render vì JSP đã render danh sách phim
    }
}