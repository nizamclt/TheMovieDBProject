package com.example.themoviedbproject;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.themoviedbproject.data.MovieContract;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static String CLASS_TAG;
    private static final int GRID_LAYOUT_COLUMNS = 2;
    private static final int MOVIE_DB_LOADER_ID = 1;

    private static String LAYOUT_BUNDLE_NAME;
    private static String API_KAY;
    private static String URL_MOVIE_DB;
    private static String NODE_REVIEWS;
    private static String NODE_VIDEOS;
    private static String NODE_VIDEO_LINK;

    @BindView(R.id.ErrorLayout) RelativeLayout mLayoutError;
    @BindView(R.id.recyclerview_movie) RecyclerView mRecyclerView;
    @BindView(R.id.Refresh) Button mButtonRefresh;
    @BindView(R.id.progress_bar) ProgressBar mProgressBar;

    private MainActivity mMainActivity;
    private MovieAdapter mMovieAdapter;
    Parcelable mSavedRecyclerLayoutState;
    private int mSortSelection = R.id.sort_by_most_popular; //Default sorting
    private LoadFavouritesDB mLoadFavouritesDB = new LoadFavouritesDB();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivity = this;
        setContentView(R.layout.activity_main);
        setTitle(R.string.popular_movies);
        ButterKnife.bind(this);
        InitActivity();


        if(savedInstanceState != null){
            mSavedRecyclerLayoutState = savedInstanceState.getParcelable(LAYOUT_BUNDLE_NAME);
            mSortSelection = (int) savedInstanceState.getSerializable("mSortSelection");
        }

        mButtonRefresh.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                LoadMovieDetails(mSortSelection);
            }
        });

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(GRID_LAYOUT_COLUMNS, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter();

        mRecyclerView.setAdapter(mMovieAdapter);

        if(CheckOnline()){
            LoadMovieDetails(mSortSelection);
        }else{
            mLayoutError.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
        }


    }//onCreate


/*
It is noticed that when we go back to the main activity from the MovieDetailsActivity by using the back button (i.e. the <- button) on top of
the application, the onRestoreInstanceState() is not getting called. To mitigate this problem android:launchMode="singleTop" is set in the manifest.
*/

    @Override
    protected void onSaveInstanceState(Bundle bundle){

        super.onSaveInstanceState(bundle);
        bundle.putParcelable(LAYOUT_BUNDLE_NAME, mRecyclerView.getLayoutManager().onSaveInstanceState());
        bundle.putSerializable("mSortSelection", mSortSelection);
    }

    @Override
    protected  void onRestoreInstanceState(Bundle bundle){

        super.onRestoreInstanceState(bundle);

        if(bundle != null)
        {
            //Parcelable savedRecyclerLayoutState = bundle.getParcelable(LAYOUT_BUNDLE_NAME);
            //mRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);

            //Restoration of RecyclerView position will be done only after loading is completed.
            //Here, just save the parcelable instance.
            mSavedRecyclerLayoutState = bundle.getParcelable(LAYOUT_BUNDLE_NAME);
            mSortSelection = (int) bundle.getSerializable("mSortSelection");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.movie_sort_menu, menu);

        MenuItem currentOption = menu.findItem(mSortSelection);
        if( null != currentOption)
        {
            currentOption.setChecked(true);
        }
        return  true;
    }

    @Override
    public  boolean onOptionsItemSelected(MenuItem menuItem){

        menuItem.setChecked(true);
        mSortSelection = menuItem.getItemId();

        if(mLayoutError.getVisibility() == View.VISIBLE){
            return true;
        }
        LoadMovieDetails(mSortSelection);
        return true;
    }

    private void LoadMovieDetails(int sortSelection){

        switch (sortSelection){

            case R.id.sort_by_most_popular:
                setTitle(R.string.popular_movies);
                mMovieAdapter.Clear();
                new LoadMovieBkAsyncTask(new FetchMovieEventsListener()).execute();
                break;
            case R.id.sort_by_top_rating:
                setTitle(R.string.top_rated_movies);
                mMovieAdapter.Clear();
                new LoadMovieBkAsyncTask(new FetchMovieEventsListener()).execute();
                break;
            case R.id.sort_by_favourites:
                setTitle(R.string.sort_by_favourites);
                LoaderManager loaderManager = getSupportLoaderManager();
                Loader<Cursor> loaderDB = loaderManager.getLoader(MOVIE_DB_LOADER_ID);
                if(loaderDB == null) {
                    loaderManager.initLoader(MOVIE_DB_LOADER_ID, null, mLoadFavouritesDB);
                }else{
                    loaderManager.restartLoader(MOVIE_DB_LOADER_ID, null, mLoadFavouritesDB);
                }

                break;
        }
    }

    private boolean CheckOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public void HideError(){
        mLayoutError.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    public void ShowError(Exception exception){

        TextView errorText;

        errorText = (TextView) mLayoutError.findViewById(R.id.error_text);
        String strError = exception.getMessage();
        if(null == strError || strError.isEmpty()){
            strError = getResources().getString(R.string.error_unknown);
        }
        errorText.setText(strError);
        mLayoutError.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    private class FetchMovieEventsListener implements MovieEventsListener<MovieInfo, ArrayList<MovieInfo>>{

        @Override
        public void onPreExecute()
        {
            mMainActivity.HideError();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPostExecute(ArrayList<MovieInfo> result, Exception exception)
        {
            mProgressBar.setVisibility(View.INVISIBLE);

            if(exception != null ){
                mMainActivity.ShowError(exception);
                return;
            }

            //No need to set the result as we would have received all the movie details by the means of
            //onProgressUpdate.
            //mMovieAdapter.setMovieInfo(result);

            if(mSavedRecyclerLayoutState != null){
                mRecyclerView.getLayoutManager().onRestoreInstanceState(mSavedRecyclerLayoutState);
                mSavedRecyclerLayoutState = null;
            }
        }

        @Override
        public void onProgressUpdate(MovieInfo movieInfo) {

            //Add each movie as it comes.
            mMovieAdapter.addMovieInfo(movieInfo);
        }

        @Override
        public String getAPIKey()
        {
            return API_KAY;
        }

        @Override
        public String getDBURL()
        {
            return URL_MOVIE_DB;
        }

        @Override
        public int getSortCondition(){

            return mSortSelection;
        }

        @Override
        public String getNodeReviews() {
            return NODE_REVIEWS;
        }

        @Override
        public String getNodeVideos() {
            return NODE_VIDEOS;
        }

        @Override
        public String getNodeVideoLink() {
            return NODE_VIDEO_LINK;
        }
    }

    private void InitActivity(){

        LAYOUT_BUNDLE_NAME = getResources().getString(R.string.class_bundle_main_activity);
        API_KAY = getResources().getString(R.string.api_key_movie_db);
        URL_MOVIE_DB = getResources().getString(R.string.url_movie_db);
        NODE_REVIEWS = getResources().getString(R.string.url_node_reviews);
        NODE_VIDEOS = getResources().getString(R.string.url_node_videos);
        NODE_VIDEO_LINK = getResources().getString(R.string.youtube_link);
        CLASS_TAG = getClass().getSimpleName();
    }

    public class LoadFavouritesDB implements LoaderManager.LoaderCallbacks<Cursor>{

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            if(mSortSelection != R.id.sort_by_favourites ){
                return null;
            }
            return new AsyncTaskLoader<Cursor>(MainActivity.this) {

                @Override
                protected void onStartLoading() {
                    if(mSortSelection == R.id.sort_by_favourites ){
                        super.onStartLoading();
                        mMovieAdapter.Clear();
                        forceLoad();
                    }
                }

                @Override
                public Cursor loadInBackground() {

                    try {
                        return getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                                null,
                                null,
                                null,
                                null);

                    } catch (Exception e) {
                        Log.e(CLASS_TAG, "Failed to asynchronously load data.");
                        e.printStackTrace();
                        return null;
                    }
                }

                public void deliverResult(Cursor data) {
                    super.deliverResult(data);
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

            if(data == null){
                return;
            }

            try{
                //Load the data returned by the cursor.
                while(data.moveToNext()){

                    MovieInfo movieInfo = new MovieInfo();
                    movieInfo.movieID = Long.toString(data.getLong(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIEID)));
                    movieInfo.movieTitle = data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));

                    //Poster blob
                    movieInfo.bytePosterPixels = data.getBlob(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTERBLOB));

                    movieInfo.movieReleaseDate = data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE));
                    movieInfo.movieRuntime = data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MINUTES));
                    movieInfo.movieOverView = data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW));
                    movieInfo.moviePopularity = data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_POPULARITY));
                    movieInfo.movieVoteCount = data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTECOUNT));
                    movieInfo.movieVoteAverage = data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTEAVERAGE));

                    movieInfo.boolOffline = true;
                    mMovieAdapter.addMovieInfo(movieInfo);
                }

            }finally {
                data.close();
            }

            //Restore view state.
            if(mSavedRecyclerLayoutState != null){
                mRecyclerView.getLayoutManager().onRestoreInstanceState(mSavedRecyclerLayoutState);
                mSavedRecyclerLayoutState = null;
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }
}//MainActivity
