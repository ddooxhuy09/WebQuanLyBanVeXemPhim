export class MovieDetailView {
    constructor() {
        this.movieTitle = document.getElementById('movie-title');
        this.moviePoster = document.getElementById('movie-poster');
        this.movieRating = document.getElementById('movie-rating');
        this.movieAge = document.getElementById('movie-age');
        this.movieDuration = document.getElementById('movie-duration');
        this.movieSynopsis = document.getElementById('movie-synopsis');
        this.movieDirector = document.getElementById('movie-director');
        this.movieCast = document.getElementById('movie-cast');
        this.movieGenre = document.getElementById('movie-genre');
        this.movieRelease = document.getElementById('movie-release');
        this.datesContainer = document.querySelector('.dates');
        this.theaterList = document.querySelector('.theater-list');
    }

    displayMovieDetails(movie) {
        if (!movie) {
            this.movieTitle.textContent = 'Phim không tồn tại';
            return;
        }
        // Không cần cập nhật vì JSP đã render
        console.log('Movie details loaded from JSP:', movie);
    }

    displayDates() {
        const today = new Date();
        this.datesContainer.innerHTML = '';
        for (let i = 0; i < 7; i++) {
            const date = new Date(today);
            date.setDate(today.getDate() + i);
            const dateBtn = document.createElement('button');
            dateBtn.classList.add('date-btn');
            dateBtn.textContent = date.toLocaleDateString('vi-VN', { day: 'numeric', month: 'numeric' });
            if (i === 0) dateBtn.classList.add('active');
            this.datesContainer.appendChild(dateBtn);
        }
    }

    displayShowtimes(theaters) {
        // Không ghi đè, JSP đã render sẵn
        console.log('Showtimes loaded from JSP:', theaters);
    }

    bindDateSelection(handler) {
        this.datesContainer.addEventListener('click', (event) => {
            if (event.target.classList.contains('date-btn')) {
                const buttons = this.datesContainer.querySelectorAll('.date-btn');
                buttons.forEach(btn => btn.classList.remove('active'));
                event.target.classList.add('active');
                handler(event.target.textContent);
            }
        });
    }

    bindTimeSelection(handler) {
        this.theaterList.addEventListener('click', (event) => {
            if (event.target.classList.contains('time-slot')) {
                handler(event.target.textContent);
            }
        });
    }
}