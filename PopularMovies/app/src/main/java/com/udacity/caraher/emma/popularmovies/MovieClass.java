package com.udacity.caraher.emma.popularmovies;

import java.io.Serializable;

/**
 * This class stores all of the information about the movies passed to the details activity.
 */
public class MovieClass implements Serializable {

    public String originalTitle;
    public String plotSynopsis;
    public double userRating;
    public String releaseDate;
    public String posterPath;

    public MovieClass(String title, String plot, double rating, String date, String path) {
        originalTitle = title;
        plotSynopsis = plot;
        userRating = rating;
        releaseDate = date;
        posterPath = path;
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
}
