package com.johnbalderson.popularmoviesstage2.db_utils;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

// content provider for movie DB


/**

 *        Developed using lessons from GWG Part 1 and
 *        Understanding Android - A Developer's Guide (2009) ISBN 978-1-933988-67-2
 *
 *        Some data may be null so @Nullable is allowed in the methods
 */

public class MovieContentProvider extends ContentProvider {

    private static final int MOVIES = 100;
    private static final int MOVIE_ID = 101;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private MovieDbHelper movieDbHelper;

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MovieDBLayout.AUTHORITY, MovieDBLayout.PATH_ENTRY, MOVIES);

        uriMatcher.addURI(MovieDBLayout.AUTHORITY, MovieDBLayout.PATH_ENTRY + "/*", MOVIE_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        movieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {

        final SQLiteDatabase db = movieDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            case MOVIES:
                retCursor = db.query(MovieDBLayout.MovieTable.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);


                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    // add/insert items to DB
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case MOVIES:
                long id = db.insert(MovieDBLayout.MovieTable.TABLE_NAME,
                        null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(MovieDBLayout.MovieTable.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI on insert:  " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    // remove items from DB
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int moviesDeleted; // starts as 0

        switch (match) {
            case MOVIE_ID:
                String id = uri.getPathSegments().get(1);
                moviesDeleted = db.delete(MovieDBLayout.MovieTable.TABLE_NAME,
                        "id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI on delete: " + uri);
        }

        if (moviesDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return moviesDeleted;

    }

    // update DB
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
