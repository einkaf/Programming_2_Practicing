package at.ac.fhcampuswien.services;

import at.ac.fhcampuswien.model.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class MovieServiceTest {

    private MovieService movieService;
    private List<Movie> movies;
    private Movie movie1;
    private Movie movie2;

    @BeforeEach
    void setUp() {
        movie1 = new Movie("Inception", "Sci-Fi", 2010);
        movie2 = new Movie("Titanic", "Drama", 1997);

        movies = new ArrayList<>();
        movies.add(movie1);
        movies.add(movie2);

        movieService = new MovieService(movies);
    }

    @Test
    void givenMovies_whenGetAllMovies_thenReturnAllMovies() {
        List<Movie> result = movieService.getAllMovies();

        assertEquals(2, result.size());
        assertTrue(result.contains(movie1));
        assertTrue(result.contains(movie2));
    }

    @Test
    void givenNewMovie_whenAddMovie_thenMovieIsAdded() {
        Movie newMovie = new Movie("Avatar", "Sci-Fi", 2009);

        boolean result = movieService.addMovie(newMovie);

        assertTrue(result);
        assertEquals(3, movies.size());
        assertTrue(movies.contains(newMovie));
    }

    @Test
    void givenExistingMovie_whenAddMovie_thenReturnFalseAndDoNotAdd() {
        Movie duplicate = new Movie("Inception", "Sci-Fi", 2010);

        boolean result = movieService.addMovie(duplicate);

        assertFalse(result);
        assertEquals(2, movies.size());
    }

    @Test
    void givenExistingMovie_whenDeleteMovie_thenMovieIsDeleted() {
        Movie movieToDelete = new Movie("Inception", "Sci-Fi", 2010);

        boolean result = movieService.deleteMovie(movieToDelete);

        assertTrue(result);
        assertEquals(1, movies.size());
        assertFalse(movies.contains(movie1));
    }

    @Test
    void givenNonExistingMovie_whenDeleteMovie_thenReturnFalse() {
        Movie movieToDelete = new Movie("Avatar", "Sci-Fi", 2009);

        boolean result = movieService.deleteMovie(movieToDelete);

        assertFalse(result);
        assertEquals(2, movies.size());
    }

    @Test
    void givenExistingMovieId_whenUpdateMovie_thenMovieIsUpdated() {
        Movie updatedMovie = new Movie("Inception Updated", "Action", 2011);

        Optional<Movie> result = movieService.updateMovie(
                movie1.getId().toString(),
                updatedMovie
        );

        assertTrue(result.isPresent());
        assertEquals("Inception Updated", movie1.getTitle());
        assertEquals("Action", movie1.getGenre());
        assertEquals(2011, movie1.getReleaseYear());
    }

    @Test
    void givenInvalidMovieId_whenUpdateMovie_thenReturnEmptyOptional() {
        Movie updatedMovie = new Movie("Unknown", "Action", 2020);

        Optional<Movie> result = movieService.updateMovie(
                UUID.randomUUID().toString(),
                updatedMovie
        );

        assertTrue(result.isEmpty());
        assertEquals("Inception", movie1.getTitle());
    }

    @Test
    void givenPartialTitle_whenSearchMovies_thenReturnMatchingMovie() {
        Map<String, String> params = new HashMap<>();
        params.put("title", "cep");

        List<Movie> result = movieService.searchMovies(params);

        assertEquals(1, result.size());
        assertEquals("Inception", result.get(0).getTitle());
    }

    @Test
    void givenTitleIgnoringCase_whenSearchMovies_thenReturnMatchingMovie() {
        Map<String, String> params = new HashMap<>();
        params.put("title", "inception");

        List<Movie> result = movieService.searchMovies(params);

        assertEquals(1, result.size());
        assertEquals("Inception", result.get(0).getTitle());
    }

    @Test
    void givenGenreIgnoringCase_whenSearchMovies_thenReturnMatchingMovie() {
        Map<String, String> params = new HashMap<>();
        params.put("genre", "drama");

        List<Movie> result = movieService.searchMovies(params);

        assertEquals(1, result.size());
        assertEquals("Titanic", result.get(0).getTitle());
    }

    @Test
    void givenReleaseYear_whenSearchMovies_thenReturnMatchingMovie() {
        Map<String, String> params = new HashMap<>();
        params.put("releaseYear", "2010");

        List<Movie> result = movieService.searchMovies(params);

        assertEquals(1, result.size());
        assertEquals("Inception", result.get(0).getTitle());
    }

    @Test
    void givenMultipleParams_whenSearchMovies_thenReturnMatchingMovie() {
        Map<String, String> params = new HashMap<>();
        params.put("title", "tit");
        params.put("genre", "Drama");
        params.put("releaseYear", "1997");

        List<Movie> result = movieService.searchMovies(params);

        assertEquals(1, result.size());
        assertEquals("Titanic", result.get(0).getTitle());
    }

    @Test
    void givenNoParams_whenSearchMovies_thenReturnAllMovies() {
        Map<String, String> params = new HashMap<>();

        List<Movie> result = movieService.searchMovies(params);

        assertEquals(2, result.size());
    }

    @Test
    void givenNoMatchingParams_whenSearchMovies_thenReturnEmptyList() {
        Map<String, String> params = new HashMap<>();
        params.put("title", "Avatar");

        List<Movie> result = movieService.searchMovies(params);

        assertTrue(result.isEmpty());
    }
}