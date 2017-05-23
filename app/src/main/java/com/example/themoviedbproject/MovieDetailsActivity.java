package com.example.themoviedbproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Nisam on 4/20/2017.
 */

public class MovieDetailsActivity extends AppCompatActivity {

    private static String CLASS_TAG;

    private MovieAdapterTrailer mMovieAdapterTrailer;
    private MovieAdapterReview mMovieAdapterReview;
    private static final int GRID_LAYOUT_COLUMNS = 1;

    //ButterKnife is really a knife!
    @BindView(R.id.imageViewDetail) ImageView mImageView;
    @BindView(R.id.textTitle) TextView mTextTitle;
    @BindView(R.id.textOverview) TextView mTextOverView;
    @BindView(R.id.textRating) TextView mTextRating;
    @BindView(R.id.textReleaseDate) TextView mTextReleaseDate;
    @BindView(R.id.recyclerview_trailer) RecyclerView mRecyclerViewTrailer;
    @BindView(R.id.recyclerview_review) RecyclerView mRecyclerViewReview;

    @Override
    protected void onCreate(Bundle savedInstance){

        super.onCreate(savedInstance);
        CLASS_TAG = getClass().getSimpleName();
        setContentView(R.layout.activity_movie_details);
        setTitle(R.string.movie_details);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        MovieInfo movieInfo = (MovieInfo) intent.getParcelableExtra(Intent.EXTRA_TEXT);

        if(movieInfo == null){
            return;
        }

        Log.d(CLASS_TAG, movieInfo.moviePosterPath);

        //Load the image
        String url = MovieAdapter.getMovieImageUrl();
        url += movieInfo.moviePosterPath;

        Picasso.with(this).load(url).into(mImageView);

        mTextTitle.setText(movieInfo.movieTitle);
        mTextOverView.setText(movieInfo.movieOverView);
        mTextRating.setText(movieInfo.movieVoteAverage);
        mTextReleaseDate.setText(movieInfo.movieReleaseDate);


        //Configure the Trailer views.
        StaggeredGridLayoutManager layoutManager1 = new StaggeredGridLayoutManager(GRID_LAYOUT_COLUMNS, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerViewTrailer.setLayoutManager(layoutManager1);
        mRecyclerViewTrailer.setHasFixedSize(true);

        mMovieAdapterTrailer = new MovieAdapterTrailer();
        mMovieAdapterTrailer.setTrailerInfo(movieInfo.mMovieTrailers);
        mRecyclerViewTrailer.setAdapter(mMovieAdapterTrailer);

        //Configure the Review views.
        StaggeredGridLayoutManager layoutManager2 = new StaggeredGridLayoutManager(GRID_LAYOUT_COLUMNS, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerViewReview.setLayoutManager(layoutManager2);
        mRecyclerViewReview.setHasFixedSize(true);

        mMovieAdapterReview = new MovieAdapterReview();
        mMovieAdapterReview.setReviewInfo(movieInfo.mMovieReviews);
        mRecyclerViewReview.setAdapter(mMovieAdapterReview);
    }//onCreate
}//MovieDetailsActivity