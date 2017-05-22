package com.example.themoviedbproject;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

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

public class LoadMovieBkAsyncTask extends AsyncTask<Void, MovieInfo, ArrayList<MovieInfo>>{

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
    protected void onProgressUpdate(MovieInfo... values) {
        //super.onProgressUpdate(values);
        mMovieEventsListener.onProgressUpdate(values[0]);

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

            String stringURLResult = sendMovieRequest(stringBuilder.toString());

            movieResult = parseMovieResult(stringURLResult);

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

    private ArrayList<MovieInfo> parseMovieResult(String result) throws Exception {
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

                //Get movie Reviews and Trailers.
                getMovieReviews(movieInfo);
                getMovieTrailers(movieInfo);

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
                publishProgress(movieInfo);

            }
        } catch (JSONException jsonexception) {
            throw  jsonexception;
        }
        return movieArrayList;
    }//parseMovieResult

    private void getMovieReviews(MovieInfo movieInfo) throws  Exception{

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(mMovieEventsListener.getDBURL());
        stringBuilder.append(movieInfo.movieID + "/");
        stringBuilder.append(mMovieEventsListener.getNodeReviews());

        stringBuilder.append("?api_key=" + mMovieEventsListener.getAPIKey());

        String stringURLResult = sendMovieRequest(stringBuilder.toString());

        getReviewList(stringURLResult, movieInfo);

    }//GetMovieReviews

    private void getMovieTrailers(MovieInfo movieInfo) throws  Exception{


        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(mMovieEventsListener.getDBURL());
        stringBuilder.append(movieInfo.movieID + "/");
        stringBuilder.append(mMovieEventsListener.getNodeVideos());

        stringBuilder.append("?api_key=" + mMovieEventsListener.getAPIKey());

        String stringURLResult = sendMovieRequest(stringBuilder.toString());
        getTrailerList(stringURLResult, movieInfo);
    }

    private void getReviewList(String movieReviews, MovieInfo movieInfo) throws  Exception{

        JSONObject jsonObject = new JSONObject(movieReviews);
        String entry;
        String reviewResult;
        JSONArray array = (JSONArray) jsonObject.get("results");

        MovieInfo.MovieReview movieReview = new MovieInfo.MovieReview();
        for (int i = 0; i < array.length(); i++) {
            JSONObject jsonReviewObject = array.getJSONObject(i);
            entry = jsonReviewObject.getString("author");
            movieReview.mAuthor = entry;
            entry = jsonReviewObject.getString("content");
            movieReview.mContent = entry;
            entry = jsonReviewObject.getString("url");
            movieReview.mUrl = entry;
            movieInfo.mMovieReviews.add(movieReview);
        }
    }

    private void getTrailerList(String movieTrailers, MovieInfo movieInfo) throws  Exception{

        JSONObject jsonObject = new JSONObject(movieTrailers);
        String entry;
        String reviewResult;
        JSONArray array = (JSONArray) jsonObject.get("results");

        MovieInfo.MovieTrailer movieTrailer = new MovieInfo.MovieTrailer();
        for (int i = 0; i < array.length(); i++) {
            JSONObject jsonReviewObject = array.getJSONObject(i);
            entry = jsonReviewObject.getString("name");
            entry = jsonReviewObject.getString("size");
            movieTrailer.mSize = entry;
            entry = jsonReviewObject.getString("key");
            entry = mMovieEventsListener.getNodeVideoLink() + entry;
            movieTrailer.mKey = entry;
            Log.d(CLASS_TAG, entry);
            entry = jsonReviewObject.getString("type");
            movieTrailer.mType = entry;
            entry = jsonReviewObject.getString("site");
            movieTrailer.mSite = entry;
            movieInfo.mMovieTrailers.add(movieTrailer);
        }
    }

    private String sendMovieRequest(String stringURL) throws  Exception{

        String stringReturn = null;
        URL url = new URL(stringURL.toString());

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
        stringReturn = stringify(stream);
        httpURLConnection.disconnect();
        return  stringReturn;
    }

}//LoadMovieBkAsyncTask
