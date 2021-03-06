package com.example.themoviedbproject;

import android.support.annotation.Nullable;

import java.util.ArrayList;

/**
 * Created by Nisam on 4/25/2017.
 */

public interface MovieEventsListener<Progress, Result>{

    void onPreExecute();
    void onPostExecute(Result result, @Nullable Exception exception);
    void onProgressUpdate(Progress progress);
    String getAPIKey();
    String getDBURL();
    String getNodeReviews();
    String getNodeVideos();
    String getNodeVideoLink();
    int getSortCondition();
}
