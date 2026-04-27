package at.ac.fhcampuswien.services;

import at.ac.fhcampuswien.model.Movie;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MovieService {
    private final List<Movie> movies;

    public MovieService(List<Movie> movies) {
        this.movies = movies;
    }

    public List<Movie> getAllMovies() {
        return movies;
    }

    public boolean addMovie(Movie newMovie) {
        boolean exists = movies.stream().anyMatch(movie ->
                movie.getTitle().equalsIgnoreCase(newMovie.getTitle()) &&
                        movie.getGenre().equalsIgnoreCase(newMovie.getGenre()) &&
                        movie.getReleaseYear() == newMovie.getReleaseYear()
        );

        if (exists) {
            return false;
        }

        movies.add(newMovie);
        return true;
    }

    public boolean deleteMovie(Movie movieToDelete) {
        return movies.removeIf(movie ->
                movie.getTitle().equalsIgnoreCase(movieToDelete.getTitle()) &&
                        movie.getGenre().equalsIgnoreCase(movieToDelete.getGenre()) &&
                        movie.getReleaseYear() == movieToDelete.getReleaseYear()
        );
    }

    public Optional<Movie> updateMovie(String id, Movie updatedMovie) {
        return movies.stream()
                .filter(movie -> movie.getId().toString().equalsIgnoreCase(id))
                .findFirst()
                .map(movie -> {
                    movie.setTitle(updatedMovie.getTitle());
                    movie.setGenre(updatedMovie.getGenre());
                    movie.setReleaseYear(updatedMovie.getReleaseYear());
                    return movie;
                });
    }

    public List<Movie> searchMovies(Map<String, String> params) {
        return movies.stream()
                .filter(movie -> !params.containsKey("title") ||
                        movie.getTitle().toLowerCase().contains(params.get("title").toLowerCase()))
                .filter(movie -> !params.containsKey("genre") ||
                        movie.getGenre().toLowerCase().contains(params.get("genre").toLowerCase()))
                .filter(movie -> !params.containsKey("releaseYear") ||
                        String.valueOf(movie.getReleaseYear()).equals(params.get("releaseYear")))
                .toList();
    }
}