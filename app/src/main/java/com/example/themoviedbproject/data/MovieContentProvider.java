package com.example.themoviedbproject.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.BoolRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Nisam on 6/4/2017.
 */

public class MovieContentProvider extends ContentProvider {


    //Define integer path constants.
    public static final int MOVIES        = 100; //All movies. Used for query of all movies.
    public static final int MOVIE_WITH_ID = 101; //A single movie with a given id. Used for insert/delete/query

    private static final UriMatcher sUriMatcher = buildMovieUriMatcher();

    public static UriMatcher buildMovieUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIES + "/#", MOVIE_WITH_ID);

        return uriMatcher;
    }

    private MovieDBHelper mMovieDBHelper;


    @Override
    public boolean onCreate() {

        Context context = getContext();
        mMovieDBHelper = new MovieDBHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        Cursor returnCursor = null;

        final SQLiteDatabase sqLiteDatabase = mMovieDBHelper.getReadableDatabase();

        //Look for a match for the given uri.
        int match = sUriMatcher.match(uri);

        switch(match){

            case MOVIES:
                returnCursor = sqLiteDatabase.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;

            case MOVIE_WITH_ID:
                String movieId = uri.getPathSegments().get(1);

                String localSelection = MovieContract.MovieEntry.COLUMN_MOVIEID + "=?";
                String[] localSelectionArgs = new String[]{movieId};

                returnCursor = sqLiteDatabase.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }

        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        Uri uriReturn = null;

        final SQLiteDatabase sqLiteDatabase = mMovieDBHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        switch (match){

            case MOVIES:
                long id =  sqLiteDatabase.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if(id > 0){

                    uriReturn = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, id);

                }else{

                    throw new SQLException("Failed to insert row into " + uri );
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }//switch..ends


        return uriReturn;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        int returnInt = 0;

        final SQLiteDatabase sqLiteDatabase = mMovieDBHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        switch (match){

            case MOVIE_WITH_ID:
                String movieId = uri.getPathSegments().get(1);
                String localSelection = MovieContract.MovieEntry.COLUMN_MOVIEID + "=?";
                String[] localSelectionArgs = new String[]{movieId};
                returnInt = sqLiteDatabase.delete(MovieContract.MovieEntry.TABLE_NAME, localSelection, localSelectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }

        if(returnInt > 0){

            getContext().getContentResolver().notifyChange(uri, null);
        }

        return returnInt;

    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
