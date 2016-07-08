package com.udacity.caraher.emma.popularmovies;

import java.io.Serializable;

/**
 * Created by Jemma on 7/7/2016.
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
