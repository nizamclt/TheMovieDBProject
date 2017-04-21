package com.example.themoviedbproject;

/**
 * Created by Nisam on 4/21/2017.
 */

public enum MovieSortCondition{

    MOVIE_SORT_CONDITION_MOST_POPULAR("popular"),
    MOVIE_SORT_CONDITION_TOP_RATED("top_rated");

    private final String enumText;

    private MovieSortCondition(final String string) {
        this.enumText = string;
    }

    @Override
    public String toString() {
        return enumText;
    }
}
