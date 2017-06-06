package com.example.themoviedbproject.data;

import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Nisam on 6/4/2017.
 */

public class MovieContract {

    public static final String AUTHORITY = "com.example.themoviedbproject";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_MOVIES = "movies";

    public static final class MovieEntry {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "movies";

        //List column names.
        public static final String COLUMN_MOVIEID = "movieId";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTERBLOB = "posterBlob";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_MINUTES = "minutes";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_VOTECOUNT = "voteCount";
        public static final String COLUMN_VOTEAVERAGE = "voteAverage";
    }
}
