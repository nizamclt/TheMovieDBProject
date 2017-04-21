package com.example.themoviedbproject;

import java.util.Comparator;
import com.example.themoviedbproject.MovieSortCondition;

/**
 * Created by Nisam on 4/21/2017.
 */

public class MovieComparator implements Comparator<MovieInfo> {

    private static final String CLASS_TAG = "MovieComparator";

    @Override
    public int compare(MovieInfo objFirst, MovieInfo objSecond){
        int result = 0;
        switch ( MovieAdapter.getSortCondition()){

            case MOVIE_SORT_CONDITION_MOST_POPULAR:{

                //Log.d(CLASS_TAG, objFirst.moviePopularity);
                //Log.d(CLASS_TAG, objSecond.moviePopularity);

                float dPopularity1 = Float.valueOf(objFirst.moviePopularity);
                float dPopularity2 = Float.parseFloat(objSecond.moviePopularity);
                result = Float.compare(dPopularity2, dPopularity1); //Descending order of sort.
                break;
            }

            case MOVIE_SORT_CONDITION_TOP_RATED:{

                //Log.d(CLASS_TAG, objFirst.movieVoteAverage);
                //Log.d(CLASS_TAG, objSecond.movieVoteAverage);

                float nTopRating1 = Float.valueOf(objFirst.movieVoteAverage);
                float nTopRating2= Float.valueOf(objSecond.movieVoteAverage);
                result = Float.compare(nTopRating2, nTopRating1); //Descending order of sort.
                break;
            }
        }//Switch ends
        return result;
    }//compare
}//MovieComparator