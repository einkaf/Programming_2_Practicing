package at.ac.fhcampuswien.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Movie {
    private UUID id;
    private String title;
    private String genre;
    private int releaseYear;

    public Movie() {
        this.id = UUID.randomUUID();
    }

    public Movie(String title, String genre, int releaseYear) {
        this();
        this.title = title;
        this.genre = genre;
        this.releaseYear = releaseYear;
    }

    public static List<Movie> generateDummyMovies() {
        List<Movie> movies = new ArrayList<>();
        Random random = new Random();
        String[] titles = {
                "Shadow War", "Lost Horizon", "Final Escape", "Dark Future",
                "Golden Empire", "Silent Night", "Broken Dreams", "Last Hero",
                "Hidden Truth", "Endless Journey", "Crimson Sky", "Frozen Time",
                "Burning City", "Secret Code", "Phantom Strike", "Neon Lights",
                "Midnight Run", "Fallen Kingdom", "Iron Will", "Wild Chase"
        };

        String[] genres = {
                "Action", "Drama", "Comedy", "Horror", "Sci-Fi", "Thriller"
        };

        for (int i = 0; i < 20; i++) {
            String title = titles[random.nextInt(titles.length)];
            String genre = genres[random.nextInt(genres.length)];
            int releaseYear = 1980 + random.nextInt(45);

            Movie newMovie = new Movie(title, genre, releaseYear);

            movies.add(newMovie);
        }
        return movies;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", genre='" + genre + '\'' +
                ", releaseYear=" + releaseYear +
                '}';
    }

    public UUID getId() {
        return this.id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }
}

