package com.example.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by karthik on 30/10/17.
 */

public class FavMoviesContract {
    public static final String CONTENT_AUTHORITY = "com.example.popularmovies.app";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final class FavMoviesEntry implements BaseColumns {
        // table name
        public static final String TABLE_FAV_MOVIES = "favmovies";
        // columns
        public static final String _ID = "_id";
        public static final String MOVIE_ID = "movie_id";
        public static final String VOTE_AVERAGE = "vote_average";
        public static final String POSTER_PATH = "poster_path";
        public static final String ORIGINAL_TITLE = "original_title";
        public static final String RELEASE_DATE = "release_date";
        public static final String OVERVIEW = "overview";

        // create content uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_FAV_MOVIES).build();
        // create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_FAV_MOVIES;
        // create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +"/" + CONTENT_AUTHORITY + "/" + TABLE_FAV_MOVIES;

        // for building URIs on insertion
        public static Uri buildFavMoviessUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}