package com.example.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by karthik on 30/10/17.
 */

public class MoviesDBHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = MoviesDBHelper.class.getSimpleName();

    //name & version
    private static final String DATABASE_NAME = "flavors.db";
    private static final int DATABASE_VERSION = 12;

    public MoviesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create the database
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                FavMoviesContract.FavMoviesEntry.TABLE_FAV_MOVIES + "(" + FavMoviesContract.FavMoviesEntry._ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FavMoviesContract.FavMoviesEntry.MOVIE_ID + " TEXT NOT NULL, " +
                FavMoviesContract.FavMoviesEntry.VOTE_AVERAGE +
                " TEXT NULL, " +
                FavMoviesContract.FavMoviesEntry.POSTER_PATH +
                " TEXT NULL, " +
                FavMoviesContract.FavMoviesEntry.ORIGINAL_TITLE +
                " TEXT NULL, " +
                FavMoviesContract.FavMoviesEntry.RELEASE_DATE +
                " TEXT NULL, " +
                FavMoviesContract.FavMoviesEntry.OVERVIEW +
                " TEXT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    // Upgrade database when version is changed.
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to " +
                newVersion + ". OLD DATA WILL BE DESTROYED");
        // Drop the table
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavMoviesContract.FavMoviesEntry.TABLE_FAV_MOVIES);
        sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                FavMoviesContract.FavMoviesEntry.TABLE_FAV_MOVIES + "'");

        // re-create database
        onCreate(sqLiteDatabase);
    }
}
