package com.johnbalderson.popularmoviesstage2.db_utils;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * This is a layout for the DB with the fields needed
 */

public final class MovieDBLayout {

    private MovieDBLayout() {
    }

    public static final String AUTHORITY = "com.johnbalderson.popularmoviesstage2";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_ENTRY = "entry";

    /* Define table and content */
    public static class MovieTable implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ENTRY).build();

        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_ORIGINAL_TITLE = "originalTitle";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_POSTER_PATH = "posterPath";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_VOTE_AVERAGE = "voteAverage";

    }
}
