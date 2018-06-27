package com.johnbalderson.popularmoviesstage2.data_models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *  conversion to Parcelable at recommendation of Stage 1 reviewer
 *  https://guides.codepath.com/android/using-parcelable
 *
 *   */

// build data layout for movie

public class Movie implements Parcelable {

    private final String originalTitle;
    private final String posterPath;
    private final String plotSynopsis;
    private final double userRating;
    private final String releaseDate;
    private final String movieId;



    // booleans for favorite display

    /**
     * set a flag for favorite movie so that it can be turned off later if user wants to remove movie later
     */


    public boolean isMovieFavorite() {
        return isMovieFavorite;
    }
    public void setMovieFavorite(boolean favorite) {
        isMovieFavorite = favorite;
    }
    private boolean isMovieFavorite;

    public Movie(String movieId, String originalTitle, String plotSynopsis,
                     String posterPath, String releaseDate, double userRating, boolean isMovieFavorite) {
        this.originalTitle = originalTitle;
        this.plotSynopsis = plotSynopsis;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.userRating = userRating;
        this.movieId = movieId;
        this.isMovieFavorite = isMovieFavorite;
    }

    private Movie(Parcel in) {
        originalTitle = in.readString();
        plotSynopsis = in.readString();
        posterPath = in.readString();
        releaseDate = in.readString();
        userRating = in.readDouble();
        movieId= in.readString();
        isMovieFavorite = in.readByte() != 0;
    }

    @SuppressWarnings("unused")
    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };


    // get/set items

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getPlotSynopsis() {
        return plotSynopsis;
    }

    public double getUserRating() {
        return userRating;
    }

    public String getReleaseDate() { return releaseDate; }

    public String getMovieId() {
        return movieId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(originalTitle);
        dest.writeString(plotSynopsis);
        dest.writeString(posterPath);
        dest.writeString(releaseDate);
        dest.writeDouble(userRating);
        dest.writeString(movieId);
        dest.writeByte((byte) (isMovieFavorite ? 1 : 0));
    }
}
