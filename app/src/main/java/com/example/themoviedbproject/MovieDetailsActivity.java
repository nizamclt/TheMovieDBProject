package com.example.themoviedbproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Nisam on 4/20/2017.
 */

public class MovieDetailsActivity extends AppCompatActivity {

    private static String CLASS_TAG;
    ImageView mImageView;
    TextView mTextTitle;
    TextView mTextOverView;
    TextView mTextRating;
    TextView mTextReleaseDate;

    @Override
    protected void onCreate(Bundle savedInstance){

        super.onCreate(savedInstance);
        CLASS_TAG = getClass().getSimpleName();
        setContentView(R.layout.activity_movie_details);

        mImageView = (ImageView) findViewById(R.id.imageViewDetail);
        mTextTitle = (TextView) findViewById(R.id.textTitle);
        mTextOverView = (TextView) findViewById(R.id.textOverview);
        mTextRating = (TextView) findViewById(R.id.textRating);
        mTextReleaseDate = (TextView) findViewById(R.id.textReleaseDate);

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
        mTextRating.setText(movieInfo.moviePopularity);
        mTextReleaseDate.setText(movieInfo.movieReleaseDate);
    }//onCreate
}//MovieDetailsActivity