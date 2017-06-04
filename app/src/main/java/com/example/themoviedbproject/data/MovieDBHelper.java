package com.example.themoviedbproject.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Nisam on 6/4/2017.
 */

public class MovieDBHelper extends SQLiteOpenHelper {

    // The name of the database
    private static final String DATABASE_NAME = "movieDb.db";

    private static final int VERSION = 1;

    MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String CREATE_TABLE = "CREATE TABLE "  + MovieContract.MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry.COLUMN_MOVIEID     + " INTEGER PRIMARY KEY, " +
                MovieContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_POSTER    + " BLOB, " +
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE    + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_MINUTES    + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_RATING    + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_OVERVIEW    + " TEXT" +
        " );";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
