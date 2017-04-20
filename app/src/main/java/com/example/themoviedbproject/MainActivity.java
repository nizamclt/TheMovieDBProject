package com.example.themoviedbproject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

    private static String LAYOUT_BUNDLE_NAME;
    private static String URL_MOVIE_DB_SEARCH;
    private static String API_KAY;

    private RelativeLayout mLayoutError;
    private RecyclerView mRecyclerView;
    private MainActivity mMainActivity;
    private Exception mMovieException;
    private Button mButtonRefresh;
    int mSortSelection = R.id.sort_by_most_popular;

    //Constructor for initialization purpose.
    public void InitActivity(){

        LAYOUT_BUNDLE_NAME = getResources().getString(R.string.class_bundle_main_activity);
        URL_MOVIE_DB_SEARCH = getResources().getString(R.string.url_movie_db_search);
        API_KAY = getResources().getString(R.string.api_key_movie_db);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivity = this;
        setContentView(R.layout.activity_main);
        InitActivity();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movie);
        mLayoutError = (RelativeLayout) findViewById(R.id.ErrorLayout);
        mButtonRefresh = (Button) mLayoutError.findViewById(R.id.Refresh);

        mButtonRefresh.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new LoadMovieBkAsyncTask().execute();
            }
        });

        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);


        if(CheckOnline()){
            new LoadMovieBkAsyncTask().execute();
        }else{
            mLayoutError.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
        }
    }//onCreate

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
        }

        @Override
        protected void onPostExecute(ArrayList<MovieInfo> result){

            if(mMovieException != null ){
                mMainActivity.ShowError(mMovieException);
                return;
            }
        }

        private ArrayList<MovieInfo> LoadMovieInfo() throws Exception{

            ArrayList<MovieInfo> movieResult = null;

            try{

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(URL_MOVIE_DB_SEARCH);
                stringBuilder.append("?api_key=" + API_KAY);
                stringBuilder.append("&query=" + "/popular");//TODO make it based on menu selection.
                URL url = new URL(stringBuilder.toString());


                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(10000 /* milliseconds */);
                httpURLConnection.setConnectTimeout(15000 /* milliseconds */);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.addRequestProperty("Accept", "application/json");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                int responseCode = httpURLConnection.getResponseCode();
                Log.d("DEBUG_TAG", "The response code is: " + responseCode + " " + httpURLConnection.getResponseMessage());

                InputStream stream = new BufferedInputStream(httpURLConnection.getInputStream());
                movieResult = parseMovieResult(stringify(stream));
                httpURLConnection.disconnect();


            }catch (Exception exception){

                throw exception;
            }
            return movieResult;
        }

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

                    movieArrayList.add(movieInfo);//TODO dynamically add items to the adaptor, instead of adding everything.


                }
            } catch (JSONException jsonexception) {
                jsonexception.printStackTrace();
                throw  jsonexception;
            }
            return movieArrayList;
        }//parseMovieResult
    }//LoadMovieBkAsyncTask
}//MainActivity
