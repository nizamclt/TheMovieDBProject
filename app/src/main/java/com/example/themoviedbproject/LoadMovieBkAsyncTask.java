package com.example.themoviedbproject;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

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

/**
 * Created by Nisam on 4/25/2017.
 */

public class LoadMovieBkAsyncTask extends AsyncTask<Void, Void, ArrayList<MovieInfo>>{

    private static String CLASS_TAG;
    private Exception mMovieException;
    MovieEventsListener mMovieEventsListener;

    LoadMovieBkAsyncTask(@NonNull MovieEventsListener movieEventsListener){
        mMovieEventsListener = movieEventsListener;
        CLASS_TAG = getClass().getSimpleName();
    }

    @Override
    public ArrayList<MovieInfo> doInBackground(Void... prams){

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
    public void onPostExecute(ArrayList<MovieInfo> result){
        mMovieEventsListener.onPostExecute(result, mMovieException);
    }

    @Override
    public void onPreExecute() {
        mMovieEventsListener.onPreExecute();
    }

    private ArrayList<MovieInfo> LoadMovieInfo() throws Exception{

        ArrayList<MovieInfo> movieResult = null;

        try{
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(mMovieEventsListener.getDBURL());

            if(mMovieEventsListener.getSortCondition() == R.id.sort_by_most_popular){
                stringBuilder.append(MovieSortCondition.MOVIE_SORT_CONDITION_MOST_POPULAR);
                MovieAdapter.setSortCondition(MovieSortCondition.MOVIE_SORT_CONDITION_MOST_POPULAR);
            }else{
                stringBuilder.append(MovieSortCondition.MOVIE_SORT_CONDITION_TOP_RATED);
                MovieAdapter.setSortCondition(MovieSortCondition.MOVIE_SORT_CONDITION_TOP_RATED);
            }
            stringBuilder.append("?api_key=" + mMovieEventsListener.getAPIKey());

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
