package at.ac.fhcampuswien.model;

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

