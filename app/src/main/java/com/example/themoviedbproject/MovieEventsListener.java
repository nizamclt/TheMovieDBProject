package com.example.themoviedbproject;

import android.support.annotation.Nullable;

import java.util.ArrayList;

/**
 * Created by Nisam on 4/25/2017.
 */

public interface MovieEventsListener<T>{

    void onPreExecute();
    void onPostExecute(T result, @Nullable Exception exception);
    String getAPIKey();
    String getDBURL();
    int getSortCondition();
}
