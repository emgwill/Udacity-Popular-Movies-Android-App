package com.udacity.caraher.emma.popularmovies;

import java.io.Serializable;
import java.util.List;

/**
 * This class stores all of the information about the movies passed to the details activity.
 */
public class MovieClass implements Serializable {

    private String originalTitle;
    private String plotSynopsis;
    private double userRating;
    private String releaseDate;
    private String posterPath;
    private String movieId;
    private TrailerClass[] trailers;

    public MovieClass(String title, String id, String plot, double rating, String date, String path) {
        originalTitle = title;
        plotSynopsis = plot;
        userRating = rating;
        releaseDate = date;
        posterPath = path;
        movieId = id;
    }

    public void setTrailers(TrailerClass[] trailerList) {
        trailers = trailerList;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getPlotSynopsis() {
        return plotSynopsis;
    }

    public double getUserRating() {
        return userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public TrailerClass[] getTrailers() {
        return trailers;
    }

    public String getId() {
        return movieId;
    }
}
