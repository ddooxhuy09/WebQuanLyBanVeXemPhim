import { MovieDetailView } from '../views/MovieDetailView.js';

export class MovieDetailController {
    constructor() {
        this.view = new MovieDetailView();
        this.initialize();
    }

    initialize() {
        try {
            // Get movie ID from URL parameters (not used in JSP context)
            var urlParams = new URLSearchParams(window.location.search);
            var movieId = urlParams.get('id');
            console.log('Initializing movie details for movieId: ' + movieId);

            // Movie details are already rendered by JSP, no need to fetch again
            var movie = {
                title: getTextContent('movie-title', 'N/A'),
                image: getImageSource('movie-poster', ''),
                rating: getTextContent('movie-rating', 'N/A'),
                ageRestriction: getTextContent('movie-age', 'N/A'),
                duration: getTextContent('movie-duration', 'N/A'),
                synopsis: getTextContent('movie-synopsis', 'Không có mô tả'),
                director: getTextContent('movie-director', 'N/A'),
                cast: getTextContent('movie-cast', 'N/A'),
                genre: getTextContent('movie-genre', 'N/A'),
                releaseDate: getTextContent('movie-release', 'N/A'),
                theaters: this.getTheatersFromPage()
            };

            function getTextContent(elementId, defaultValue) {
                var element = document.getElementById(elementId);
                return element && element.textContent ? element.textContent.trim() : defaultValue;
            }

            function getImageSource(elementId, defaultValue) {
                var element = document.getElementById(elementId);
                return element && element.src ? element.src : defaultValue;
            }

            if (!movie.title || movie.title === 'N/A') {
                console.error('Movie data not found on page for movieId: ' + movieId);
                this.view.displayMovieDetails(null);
                return;
            }

            // Display movie details (optional, JSP already handles this)
            this.view.displayMovieDetails(movie);
            
            // Display showtimes (not needed, JSP handles)
            this.view.displayShowtimes(movie.theaters);
            
            // Display available dates
            this.view.displayDates();

            // Bind event handlers
            this.view.bindDateSelection(this.handleDateSelection.bind(this));
            this.view.bindTimeSelection(this.handleTimeSelection.bind(this));

        } catch (error) {
            console.error("Error initializing movie details:", error);
        }
    }

    getTheatersFromPage() {
        try {
            var theaterElements = document.querySelectorAll('.theater');
            var theaters = [];
            for (var i = 0; i < theaterElements.length; i++) {
                var theater = theaterElements[i];
                var nameElement = theater.querySelector('.theater-name');
                var name = nameElement && nameElement.textContent ? nameElement.textContent.trim() : 'Unknown Theater';
                var timeSlots = theater.querySelectorAll('.time-slot');
                var showtimes = [];
                for (var j = 0; j < timeSlots.length; j++) {
                    showtimes.push(timeSlots[j].textContent.trim());
                }
                theaters.push({ name: name, showtimes: showtimes });
            }
            return theaters;
        } catch (error) {
            console.error("Error retrieving theater data:", error);
            return [];
        }
    }

    handleDateSelection(date) {
        console.log('Selected date: ' + date);
        // Filter showtimes based on date (optional enhancement)
    }

    handleTimeSelection(time) {
        console.log('Selected time: ' + time);
        const timeSlot = Array.from(document.querySelectorAll('.time-slot')).find(
            el => el.textContent.trim() === time.trim()
        );
        if (timeSlot) {
            const form = timeSlot.closest('form');
            if (form) {
                form.submit(); // Gửi form đến /book-ticket
            }
        }
    }
}