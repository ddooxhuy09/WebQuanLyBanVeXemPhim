export class MovieView {
  constructor() {
    this.movieContainer = document.querySelector(".movie-container");
    this.carouselInner = document.querySelector(".carousel-inner");
    this.leftBtn = document.querySelector(".left-btn");
    this.rightBtn = document.querySelector(".right-btn");
    this.currentIndex = 0; // Index dùng để tính toán transform
    this.imageCount = 0; // Số lượng ảnh gốc
    this.totalImages = 0; // Tổng số ảnh (gốc + clones)
    this.images = null;
    this.autoScrollInterval = null;
    this.isTransitioning = false; // Cờ đánh dấu đang có hiệu ứng chuyển slide

    this.setupCarousel();
  }

  setupCarousel() {
    const originalImages = Array.from(
      this.carouselInner.querySelectorAll("img")
    );
    if (originalImages.length === 0) {
      console.warn("No images found in carousel");
      return;
    }

    this.imageCount = originalImages.length;

    // Tạo bản sao trước và sau set ảnh gốc để tạo vòng lặp mượt
    // Cấu trúc: [clones-before] | [originals] | [clones-after]
    const clonesBefore = originalImages.map((img) => img.cloneNode(true));
    const clonesAfter = originalImages.map((img) => img.cloneNode(true));

    this.carouselInner.innerHTML = ""; // Xóa ảnh hiện có (nếu có)
    clonesBefore.forEach((clone) => this.carouselInner.appendChild(clone));
    originalImages.forEach((original) =>
      this.carouselInner.appendChild(original)
    );
    clonesAfter.forEach((clone) => this.carouselInner.appendChild(clone));

    this.images = this.carouselInner.querySelectorAll("img");
    this.totalImages = this.images.length;

    if (this.totalImages !== this.imageCount * 3) {
      console.error(
        "Image cloning setup failed. Expected:",
        this.imageCount * 3,
        "images, but found:",
        this.totalImages
      );
      return;
    }

    // Đặt chiều rộng cho carousel-inner để chứa tất cả ảnh
    // Mỗi ảnh sẽ chiếm 100% chiều rộng của viewport của carousel
    this.carouselInner.style.width = `${this.totalImages * 100}%`;

    // Bắt đầu từ ảnh đầu tiên của set "original" (set ở giữa)
    this.currentIndex = this.imageCount;
    this.updateCarousel(false); // Cập nhật vị trí ban đầu không có hiệu ứng

    this.leftBtn.addEventListener("click", () =>
      this.handleUserInteraction(() => this.showPreviousImage())
    );
    this.rightBtn.addEventListener("click", () =>
      this.handleUserInteraction(() => this.showNextImage())
    );

    this.startAutoScroll();
    this.carouselInner.addEventListener("mouseenter", () =>
      this.stopAutoScroll()
    );
    this.carouselInner.addEventListener("mouseleave", () =>
      this.startAutoScroll()
    );

    // Lắng nghe sự kiện transitionend để xử lý vòng lặp
    this.carouselInner.addEventListener("transitionend", () => {
      if (this.isTransitioning) {
        // Chỉ xử lý nếu transition này là do chúng ta chủ động gọi
        this.handleLoop();
        this.isTransitioning = false; // Reset cờ sau khi xử lý xong
      }
    });

    this.preloadImages(originalImages);
  }

  preloadImages(images) {
    images.forEach((img) => {
      const preloadImg = new Image();
      preloadImg.src = img.src;
      // preloadImg.onload = () => console.log(`Preloaded: ${img.src}`);
      // preloadImg.onerror = () => console.error(`Failed to preload: ${img.src}`);
    });
  }

  startAutoScroll() {
    if (!this.autoScrollInterval) {
      // Đảm bảo không có transition đang diễn ra trước khi bắt đầu auto scroll mới
      if (this.isTransitioning) return;
      this.autoScrollInterval = setInterval(() => this.showNextImage(), 3000); // Slide mỗi 3 giây
    }
  }

  stopAutoScroll() {
    if (this.autoScrollInterval) {
      clearInterval(this.autoScrollInterval);
      this.autoScrollInterval = null;
    }
  }

  handleUserInteraction(callback) {
    this.stopAutoScroll();
    callback(); // Thực hiện hành động (next/prev)
    // Chỉ khởi động lại auto-scroll sau một khoảng trễ nếu nó đã từng chạy
    // Cân nhắc không tự động start lại nếu người dùng vừa tương tác mạnh
    setTimeout(() => {
      // Kiểm tra lại, nếu người dùng không hover vào thì mới start
      if (!this.carouselInner.matches(":hover")) {
        this.startAutoScroll();
      }
    }, 5000); // Khởi động lại sau 5 giây
  }

  updateCarousel(useTransition = true) {
    // Tính toán vị trí offset dựa trên tổng số ảnh
    const offset = -(this.currentIndex * (100 / this.totalImages));

    if (useTransition) {
      this.carouselInner.style.transition = "transform 0.5s ease-in-out";
    } else {
      this.carouselInner.style.transition = "none";
    }
    this.carouselInner.style.transform = `translateX(${offset}%)`;

    if (!useTransition) {
      // Kỹ thuật "force reflow": Đảm bảo trình duyệt áp dụng thay đổi transform
      // ngay lập tức khi không có transition, tránh bị "nháy" hoặc "giật cục".
      // Đọc một thuộc tính layout như offsetHeight sẽ buộc trình duyệt tính toán lại layout.
      void this.carouselInner.offsetHeight;
    }
  }

  showPreviousImage() {
    if (this.isTransitioning) return;
    this.isTransitioning = true;
    this.currentIndex--;
    this.updateCarousel(); // Áp dụng hiệu ứng trượt
    // Xử lý vòng lặp sẽ diễn ra trong 'transitionend' event
  }

  showNextImage() {
    if (this.isTransitioning) return;
    this.isTransitioning = true;
    this.currentIndex++;
    this.updateCarousel(); // Áp dụng hiệu ứng trượt
    // Xử lý vòng lặp sẽ diễn ra trong 'transitionend' event
  }

  handleLoop() {
    let needsReset = false;
    // Xử lý vòng lặp tiến (khi xem hết ảnh gốc, chuyển sang bản sao phía sau)
    // Ví dụ: imageCount = 3. currentIndex bắt đầu từ 3 (ảnh gốc đầu tiên).
    // Chạy tới 3, 4, 5. Khi currentIndex = 6 (ảnh đầu tiên của set clone-after)
    if (this.currentIndex >= this.imageCount * 2) {
      this.currentIndex -= this.imageCount; // Đưa currentIndex về vị trí tương ứng trong set ảnh gốc
      // Ví dụ: 6 -> 3, 7 -> 4
      needsReset = true;
    }
    // Xử lý vòng lặp lùi (tương tự nếu bạn muốn hỗ trợ lùi vô tận)
    else if (this.currentIndex < this.imageCount) {
      this.currentIndex += this.imageCount; // Đưa currentIndex về vị trí tương ứng trong set ảnh gốc (ở cuối)
      // Ví dụ: 2 -> 5, 1 -> 4
      needsReset = true;
    }

    if (needsReset) {
      // Cập nhật vị trí ngay lập tức, không có hiệu ứng chuyển động
      this.updateCarousel(false);
    }
  }

  bindBookTicket(handler) {
    this.movieContainer.addEventListener("click", (event) => {
      if (
        event.target.classList.contains("btn") &&
        !event.target.classList.contains("trailer-btn")
      ) {
        const form = event.target.closest("form");
        if (form) {
          const maPhimInput = form.querySelector('input[name="id"]');
          if (maPhimInput) {
            const maPhim = maPhimInput.value;
            handler(maPhim);
            form.submit();
          } else {
            console.warn("Input maPhim not found in form.");
          }
        }
      }
    });
  }

  bindTrailerView(handler) {
    this.movieContainer.addEventListener("click", (event) => {
      if (event.target.classList.contains("trailer-btn")) {
        const movieItem = event.target.closest(".movie-item");
        if (movieItem) {
          const movieTitleElement = movieItem.querySelector("h3");
          if (movieTitleElement) {
            const movieTitle = movieTitleElement.textContent;
            handler(movieTitle);
          } else {
            console.warn("Movie title element (h3) not found.");
          }
        } else {
          console.warn(
            "Movie item (.movie-item) not found for trailer button."
          );
        }
      }
    });
  }

  render() {
    // Không cần render vì JSP đã render danh sách phim
  }
}
