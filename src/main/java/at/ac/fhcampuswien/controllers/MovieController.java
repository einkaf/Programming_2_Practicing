package at.ac.fhcampuswien.controllers;

import at.ac.fhcampuswien.ApiUtils;
import at.ac.fhcampuswien.model.Movie;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class MovieController implements HttpHandler {
    public List<Movie> movies = Movie.generateDummyMovies();
    final String BASE = "/api/movies/";

    @Override

    public void handle(HttpExchange exchange) throws IOException {
//        Get Http method (GET, POST, ...)
        String method = exchange.getRequestMethod();

//        Get request path
        String path = exchange.getRequestURI().getPath();

        switch (path) {
            case BASE + "getAll" -> handleBaseRequest(method, exchange);
            case BASE + "add" -> handleAddRequest(method, exchange);
            case BASE + "delete" -> handleDeleteRequest(method, exchange);
            case BASE + "update" -> handleUpdateRequest(method, exchange);

            default -> {
                // Path not found
                String response = "{ \"error\": \"Path not found\" }";
                ApiUtils.sendResponse(exchange, 404, response);
            }
        }
    }

    private void handleBaseRequest(String method, HttpExchange exchange) throws IOException {
        if (method.equals("GET")) {// Convert all movies in the list to JSON manually
            String json = movies.stream()
                    .map(m -> "{ \"id\": \"" + m.getId() + "\", " +
                            "\"title\": \"" + m.getTitle() + "\", " +
                            "\"genre\": \"" + m.getGenre() + "\", " +
                            "\"releaseYear\": " + m.getReleaseYear() + " }")
                    .collect(Collectors.joining(", ", "[", "]"));
            ApiUtils.sendResponse(exchange, 200, json);
        } else {
            String response = "{ \"error\": \"Method not allowed\" }";
            ApiUtils.sendResponse(exchange, 405, response);
        }
    }

    private void handleAddRequest(String method, HttpExchange exchange) throws IOException {
        if (!method.equals("POST")) {
            String response = "{ \"error\": \"Method not allowed\" }";
            ApiUtils.sendResponse(exchange, 405, response);
            return;
        }

        try {
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            if (!requestBody.contains("title") || !requestBody.contains("genre") || !requestBody.contains("releaseYear")) {
                String response = "{ \"error\": \"Invalid movie data\" }";
                ApiUtils.sendResponse(exchange, 400, response);
                return;
            }

            String title = extractValue(requestBody, "title");
            String genre = extractValue(requestBody, "genre");
            int releaseYear = Integer.parseInt(extractValue(requestBody, "releaseYear"));

            for (Movie movie : movies) {
                if (title.equalsIgnoreCase(movie.getTitle()) && genre.equalsIgnoreCase(movie.getGenre())
                        && releaseYear == movie.getReleaseYear()) {
                    String response = "{ \"error\": \"Movie already exists\"}";
                    ApiUtils.sendResponse(exchange, 400, response);
                    return;
                }
            }

            movies.add(new Movie(title, genre, releaseYear));
            String response = "{ \"message\":  \"Movie added successfully\" }";
            ApiUtils.sendResponse(exchange, 201, response);
        } catch (Exception e) {
            String response = "{ \"error\": \"Invalid movie data\" }";
            ApiUtils.sendResponse(exchange, 400, response);
        }
    }

    private void handleDeleteRequest(String method, HttpExchange exchange) throws IOException {
        if (!method.equals("DELETE")) {
            String response = "{ \"error\": \"Method not allowed\" }";
            ApiUtils.sendResponse(exchange, 405, response);
            return;
        }
        try {
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            if (!requestBody.contains("title") || !requestBody.contains("genre") || !requestBody.contains("releaseYear")) {
                String response = "{ \"error\": \"Invalid movie data\" }";
                ApiUtils.sendResponse(exchange, 400, response);
                return;
            }

            String title = extractValue(requestBody, "title");
            String genre = extractValue(requestBody, "genre");
            int releaseYear = Integer.parseInt(extractValue(requestBody, "releaseYear"));

            for (Movie movie : movies) {
                if (title.equalsIgnoreCase(movie.getTitle()) && genre.equalsIgnoreCase(movie.getGenre())
                        && releaseYear == movie.getReleaseYear()) {
                    movies.remove(movie);
                    String response = "{ \"message\": \"Movie deleted successfully\"}";
                    ApiUtils.sendResponse(exchange, 200, response);
                    return;
                }
            }
            String response = "{ \"error\": \"Movie not found\"}";
            ApiUtils.sendResponse(exchange, 404, response);
        } catch (Exception e) {
            String response = "{ \"error\": \"Invalid movie data\" }";
            ApiUtils.sendResponse(exchange, 400, response);
        }
    }

    private void handleUpdateRequest(String method, HttpExchange exchange) throws IOException {
        if (!method.equals("PUT")) {
            String response = "{ \"error\": \"Method not allowed\" }";
            ApiUtils.sendResponse(exchange, 405, response);
            return;
        }

        try {
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            if (!requestBody.contains("id") || !requestBody.contains("title") || !requestBody.contains("genre") || !requestBody.contains("releaseYear")) {
                String response = "{ \"error\": \"Invalid movie data\" }";
                ApiUtils.sendResponse(exchange, 400, response);
                return;
            }

            String id = extractValue(requestBody, "id");
            String title = extractValue(requestBody, "title");
            String genre = extractValue(requestBody, "genre");
            int releaseYear = Integer.parseInt(extractValue(requestBody, "releaseYear"));

            for (Movie movie : movies) {
                if (id.equalsIgnoreCase(movie.getId().toString())) {
                    movie.setTitle(title);
                    movie.setGenre(genre);
                    movie.setReleaseYear(releaseYear);
                    String response = "[" + "{ \"message\": \"Movie updated successfully\"}" +", "+"{ \"id\": \"" + movie.getId() + "\", " +
                            "\"title\": \"" + movie.getTitle() + "\", " +
                            "\"genre\": \"" + movie.getGenre() + "\", " +
                            "\"releaseYear\": " + movie.getReleaseYear() + " }" + "]";
                    ApiUtils.sendResponse(exchange, 200, response);
                    return;
                }
            }

            String response = "{ \"error\": \"Movie not found\"}";
            ApiUtils.sendResponse(exchange, 404, response);

        } catch (Exception e) {
            String response = "{ \"error\": \"Invalid movie data\" }";
            ApiUtils.sendResponse(exchange, 400, response);
        }
    }
    private String extractValue(String json, String key) {
        String value = json.split("\"" + key + "\"\\s*:\\s*")[1];

        if (value.startsWith("\"")) {
            return value.split("\"")[1];
        } else {
            return value.split("[,}]")[0].trim();
        }
    }
}