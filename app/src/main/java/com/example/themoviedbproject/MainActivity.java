package com.example.themoviedbproject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class MainActivity extends AppCompatActivity {

    private static String CLASS_TAG;
    private static final int GRID_LAYOUT_COLUMNS = 2;

    private static String LAYOUT_BUNDLE_NAME;
    private static String URL_MOVIE_DB;
    private static String API_KAY;

    private RelativeLayout mLayoutError;
    private RecyclerView mRecyclerView;
    private MainActivity mMainActivity;
    private Exception mMovieException;
    private Button mButtonRefresh;
    MovieAdapter mMovieAdapter;
    ProgressBar mProgressBar;
    int mSortSelection = R.id.sort_by_most_popular;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivity = this;
        setContentView(R.layout.activity_main);
        InitActivity();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movie);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mLayoutError = (RelativeLayout) findViewById(R.id.ErrorLayout);
        mButtonRefresh = (Button) mLayoutError.findViewById(R.id.Refresh);

        mButtonRefresh.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new LoadMovieBkAsyncTask().execute();
            }
        });

        GridLayoutManager layoutManager = new GridLayoutManager(this, GRID_LAYOUT_COLUMNS);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter();

        mRecyclerView.setAdapter(mMovieAdapter);

        if(CheckOnline()){
            new LoadMovieBkAsyncTask().execute();
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
        new LoadMovieBkAsyncTask().execute();
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

    private void InitActivity(){

        LAYOUT_BUNDLE_NAME = getResources().getString(R.string.class_bundle_main_activity);
        URL_MOVIE_DB = getResources().getString(R.string.url_movie_db);
        API_KAY = getResources().getString(R.string.api_key_movie_db);
        CLASS_TAG = getClass().getSimpleName();
    }

    private  class LoadMovieBkAsyncTask extends AsyncTask<Void, Void, ArrayList<MovieInfo>> {

        @Override
        protected ArrayList<MovieInfo> doInBackground(Void... prams){

            ArrayList<MovieInfo> movieResult = null;
            mMovieException = null;
            try{
                movieResult = LoadMovieInfo();

            }catch (Exception exception){
                exception.printStackTrace();
                mMovieException = exception;
            }
            return movieResult;
        }

        @Override
        protected  void onPreExecute(){

            mMainActivity.HideError();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(ArrayList<MovieInfo> result){
            mProgressBar.setVisibility(View.INVISIBLE);

            if(mMovieException != null ){
                mMainActivity.ShowError(mMovieException);
                return;
            }
            mMovieAdapter.setMovieInfo(result);
        }

        private ArrayList<MovieInfo> LoadMovieInfo() throws Exception{

            ArrayList<MovieInfo> movieResult = null;

            try{
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(URL_MOVIE_DB);

                if(mSortSelection == R.id.sort_by_most_popular){
                    stringBuilder.append(MovieSortCondition.MOVIE_SORT_CONDITION_MOST_POPULAR);
                    mMovieAdapter.setSortCondition(MovieSortCondition.MOVIE_SORT_CONDITION_MOST_POPULAR);
                }else{
                    stringBuilder.append(MovieSortCondition.MOVIE_SORT_CONDITION_TOP_RATED);
                    mMovieAdapter.setSortCondition(MovieSortCondition.MOVIE_SORT_CONDITION_TOP_RATED);
                }
                stringBuilder.append("?api_key=" + API_KAY);

                URL url = new URL(stringBuilder.toString());

                Log.d(getClass().getSimpleName(), url.toString());

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(10000 /* milli seconds */);
                httpURLConnection.setConnectTimeout(15000 /* milli seconds */);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.addRequestProperty("Accept", "application/json");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                int responseCode = httpURLConnection.getResponseCode();
                Log.d(CLASS_TAG, "The response code is: " + responseCode + " " + httpURLConnection.getResponseMessage());

                InputStream stream = new BufferedInputStream(httpURLConnection.getInputStream());
                movieResult = parseMovieResult(stringify(stream));
                httpURLConnection.disconnect();


            }catch (Exception exception){

                throw exception;
            }
            return movieResult;
        }//LoadMovieInfo

        public String stringify(InputStream stream) throws IOException, UnsupportedEncodingException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(reader);
            return bufferedReader.readLine();
        }

        private ArrayList<MovieInfo> parseMovieResult(String result) throws JSONException {
            String streamAsString = result;

            ArrayList<MovieInfo> movieArrayList = new ArrayList<MovieInfo>();

            try {
                JSONObject jsonObject = new JSONObject(streamAsString);
                String string;
                JSONArray array = (JSONArray) jsonObject.get("results");
                for (int i = 0; i < array.length(); i++) {

                    JSONObject jsonMovieObject = array.getJSONObject(i);
                    string = jsonMovieObject.getString("poster_path");
                    if(string.isEmpty() || string.contains("null")){
                        continue;
                    }

                    MovieInfo movieInfo = new MovieInfo();

                    string = jsonMovieObject.getString("id");
                    movieInfo.movieID = string;
                    string = jsonMovieObject.getString("title");
                    movieInfo.movieTitle = string;
                    string = jsonMovieObject.getString("vote_average");//sort key
                    movieInfo.movieVoteAverage = string;
                    string = jsonMovieObject.getString("poster_path");
                    movieInfo.moviePosterPath = string;
                    string = jsonMovieObject.getString("overview");
                    movieInfo.movieOverView = string;
                    string = jsonMovieObject.getString("release_date");
                    movieInfo.movieReleaseDate = string;
                    string = jsonMovieObject.getString("popularity");//sort key
                    movieInfo.moviePopularity = string;

//                    string = jsonMovieObject.getString("backdrop_path");
//                    string = jsonMovieObject.getString("original_title");
//                    string = jsonMovieObject.getString("vote_count");

                    movieArrayList.add(movieInfo);//Dynamically adding items to the adaptor possible?

                }
            } catch (JSONException jsonexception) {
                throw  jsonexception;
            }
            return movieArrayList;
        }//parseMovieResult
    }//LoadMovieBkAsyncTask
}//MainActivity
