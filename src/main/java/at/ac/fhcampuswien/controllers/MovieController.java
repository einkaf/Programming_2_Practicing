package at.ac.fhcampuswien.controllers;

import at.ac.fhcampuswien.ApiUtils;
import at.ac.fhcampuswien.model.Movie;
import at.ac.fhcampuswien.services.MovieService;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MovieController implements HttpHandler {

    private final MovieService movieService;
    private final Gson gson = new Gson();

    private static final String BASE = "/api/movies/";

    public MovieController() {
        List<Movie> movies = Movie.generateDummyMovies();
        this.movieService = new MovieService(movies);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        switch (path) {
            case BASE + "getAll" -> handleBaseRequest(method, exchange);
            case BASE + "add" -> handleAddRequest(method, exchange);
            case BASE + "delete" -> handleDeleteRequest(method, exchange);
            case BASE + "update" -> handleUpdateRequest(method, exchange);
            case BASE + "search" -> handleSearchRequest(method, exchange);
            default -> ApiUtils.sendResponse(exchange, 404, "{ \"error\": \"Path not found\" }");
        }
    }

    private void handleBaseRequest(String method, HttpExchange exchange) throws IOException {
        if (!method.equals("GET")) {
            ApiUtils.sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
            return;
        }

        List<Movie> movies = movieService.getAllMovies();
        String json = gson.toJson(movies);

        ApiUtils.sendResponse(exchange, 200, json);
    }

    private void handleAddRequest(String method, HttpExchange exchange) throws IOException {
        if (!method.equals("POST")) {
            ApiUtils.sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
            return;
        }

        try {
            String requestBody = readRequestBody(exchange);
            Movie movie = gson.fromJson(requestBody, Movie.class);

            if (!isValidMovie(movie)) {
                ApiUtils.sendResponse(exchange, 400, "{ \"error\": \"Invalid movie data\" }");
                return;
            }

            boolean added = movieService.addMovie(movie);

            if (!added) {
                ApiUtils.sendResponse(exchange, 400, "{ \"error\": \"Movie already exists\" }");
                return;
            }

            ApiUtils.sendResponse(exchange, 201, "{ \"message\": \"Movie added successfully\" }");

        } catch (Exception e) {
            ApiUtils.sendResponse(exchange, 400, "{ \"error\": \"Invalid movie data\" }");
        }
    }

    private void handleDeleteRequest(String method, HttpExchange exchange) throws IOException {
        if (!method.equals("DELETE")) {
            ApiUtils.sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
            return;
        }

        try {
            String requestBody = readRequestBody(exchange);
            Movie movie = gson.fromJson(requestBody, Movie.class);

            if (!isValidMovie(movie)) {
                ApiUtils.sendResponse(exchange, 400, "{ \"error\": \"Invalid movie data\" }");
                return;
            }

            boolean deleted = movieService.deleteMovie(movie);

            if (!deleted) {
                ApiUtils.sendResponse(exchange, 404, "{ \"error\": \"Movie not found\" }");
                return;
            }

            ApiUtils.sendResponse(exchange, 200, "{ \"message\": \"Movie deleted successfully\" }");

        } catch (Exception e) {
            ApiUtils.sendResponse(exchange, 400, "{ \"error\": \"Invalid movie data\" }");
        }
    }

    private void handleUpdateRequest(String method, HttpExchange exchange) throws IOException {
        if (!method.equals("PUT")) {
            ApiUtils.sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
            return;
        }

        try {
            String requestBody = readRequestBody(exchange);
            Movie updatedMovie = gson.fromJson(requestBody, Movie.class);

            if (updatedMovie == null ||
                    updatedMovie.getId() == null ||
                    !isValidMovie(updatedMovie)) {
                ApiUtils.sendResponse(exchange, 400, "{ \"error\": \"Invalid movie data\" }");
                return;
            }

            Optional<Movie> result = movieService.updateMovie(
                    updatedMovie.getId().toString(),
                    updatedMovie
            );

            if (result.isEmpty()) {
                ApiUtils.sendResponse(exchange, 404, "{ \"error\": \"Movie not found\" }");
                return;
            }

            ApiUtils.sendResponse(exchange, 200, gson.toJson(result.get()));

        } catch (Exception e) {
            ApiUtils.sendResponse(exchange, 400, "{ \"error\": \"Invalid movie data\" }");
        }
    }

    private void handleSearchRequest(String method, HttpExchange exchange) throws IOException {
        if (!method.equals("GET")) {
            ApiUtils.sendResponse(exchange, 405, "{ \"error\": \"Method not allowed\" }");
            return;
        }

        Map<String, String> params = ApiUtils.parseQueryParams(exchange.getRequestURI().getQuery());
        List<Movie> result = movieService.searchMovies(params);

        ApiUtils.sendResponse(exchange, 200, gson.toJson(result));
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    private boolean isValidMovie(Movie movie) {
        return movie != null
                && movie.getTitle() != null
                && movie.getGenre() != null
                && movie.getReleaseYear() != 0;
    }
}