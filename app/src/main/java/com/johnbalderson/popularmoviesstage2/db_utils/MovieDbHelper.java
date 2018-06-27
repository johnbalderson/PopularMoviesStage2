package com.johnbalderson.popularmoviesstage2.db_utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// DB functions for movie DB (table create/drop)


/**
 * This performs the DB functions using the MovieContract
 *
 * Development using lessons from GWG Part 1 and
 * Understanding Android - A Developer's Guide (2009) ISBN 978-1-933988-67-2
 */

class MovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 2;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                MovieDBLayout.MovieTable.TABLE_NAME + " (" +
                MovieDBLayout.MovieTable.COLUMN_ID + " TEXT NOT NULL PRIMARY KEY, "  +
                MovieDBLayout.MovieTable.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MovieDBLayout.MovieTable.COLUMN_SYNOPSIS + " TEXT NOT NULL, " +
                MovieDBLayout.MovieTable.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MovieDBLayout.MovieTable.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieDBLayout.MovieTable.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL " +
                ");";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + MovieDBLayout.MovieTable.TABLE_NAME);
        onCreate(db);

    }
}
