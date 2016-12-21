package com.udacity.caraher.emma.popularmovies;

/**
 * Created by Jemma on 12/19/2016.
 */

public class TrailerClass {
    public String trailerId;
    public String trailerKey;
    public String trailerName;

    public TrailerClass(String id, String key, String name) {
        trailerId = id;
        trailerKey = key;
        trailerName = name;
    }

    public String getTrailerId() {
        return trailerId;
    }

    public String getTrailerKey() {
        return trailerKey;
    }

    public String getTrailerName() {
        return trailerName;
    }
}
