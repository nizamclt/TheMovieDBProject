package com.example.themoviedbproject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.annotation.Nullable;
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

    private static String LAYOUT_BUNDLE_NAME;
    private static String URL_MOVIE_DB;
    private static String API_KAY;

    @BindView(R.id.ErrorLayout) RelativeLayout mLayoutError;
    @BindView(R.id.recyclerview_movie) RecyclerView mRecyclerView;
    @BindView(R.id.Refresh) Button mButtonRefresh;
    @BindView(R.id.progress_bar) ProgressBar mProgressBar;

    private MainActivity mMainActivity;
    private MovieAdapter mMovieAdapter;
    private int mSortSelection = R.id.sort_by_most_popular; //Default sorting


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivity = this;
        setContentView(R.layout.activity_main);
        setTitle(R.string.popular_movies);
        ButterKnife.bind(this);
        InitActivity();

        mButtonRefresh.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new LoadMovieBkAsyncTask(new FetchMovieEventsListener()).execute();
            }
        });

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(GRID_LAYOUT_COLUMNS, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter();

        mRecyclerView.setAdapter(mMovieAdapter);

        if(CheckOnline()){
            new LoadMovieBkAsyncTask(new FetchMovieEventsListener()).execute();
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
    }

    @Override
    protected  void onRestoreInstanceState(Bundle bundle){

        super.onRestoreInstanceState(bundle);

        if(bundle != null)
        {
            Parcelable savedRecyclerLayoutState = bundle.getParcelable(LAYOUT_BUNDLE_NAME);
            mRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
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

        if(mSortSelection == R.id.sort_by_most_popular){
            setTitle(R.string.popular_movies);
        }
        else{
            setTitle(R.string.top_rated_movies);
        }

        new LoadMovieBkAsyncTask(new FetchMovieEventsListener()).execute();
        return true;
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

    private class FetchMovieEventsListener implements MovieEventsListener<ArrayList<MovieInfo>>{

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
            mMovieAdapter.setMovieInfo(result);
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
    }

    private void InitActivity(){

        LAYOUT_BUNDLE_NAME = getResources().getString(R.string.class_bundle_main_activity);
        URL_MOVIE_DB = getResources().getString(R.string.url_movie_db);
        API_KAY = getResources().getString(R.string.api_key_movie_db);
        CLASS_TAG = getClass().getSimpleName();
    }
}//MainActivity
