import { MovieView } from '../views/MovieView.js';

export class MovieController {
    constructor() {
        this.view = new MovieView();

        // Bind event handlers
        this.view.bindBookTicket(this.handleBookTicket.bind(this));
        this.view.bindTrailerView(this.handleTrailerView.bind(this));

        // Initial render
        this.view.render();
    }

    handleBookTicket(maPhim) {
        console.log('Booking ticket for maPhim: ' + maPhim);
        // Không cần redirect, để form trong JSP xử lý
    }

    handleTrailerView(movieTitle) {
        if (movieTitle) {
            alert(`Opening trailer for ${movieTitle}`);
        } else {
            console.error('Movie title not found');
        }
    }
}