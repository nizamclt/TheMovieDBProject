package com.example.themoviedbproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
    MovieInfo mMovieInfo;

    //ButterKnife is really a knife!
    @BindView(R.id.imageViewDetail) ImageView mImageView;
    @BindView(R.id.textTitle) TextView mTextTitle;
    @BindView(R.id.textOverview) TextView mTextOverView;
    @BindView(R.id.textRating) TextView mTextRating;
    @BindView(R.id.textReleaseDate) TextView mTextReleaseDate;
    @BindView(R.id.textMinutes) TextView mTextRuntime;
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

        mMovieInfo = movieInfo;
        Log.d(CLASS_TAG, movieInfo.moviePosterPath);

        //Load the image
        String url = MovieAdapter.getMovieImageUrl();
        url += movieInfo.moviePosterPath;

        Picasso.with(this).load(url).into(mImageView);

        mTextTitle.setText(movieInfo.movieTitle);
        mTextOverView.setText(movieInfo.movieOverView);
        mTextRating.setText(movieInfo.movieVoteAverage + "/" + movieInfo.movieVoteCount);

        String stringYear = movieInfo.movieReleaseDate;
        try {
            Date date = new SimpleDateFormat("YYYY-MM-DD", Locale.ENGLISH).parse(movieInfo.movieReleaseDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            stringYear = Integer.toString(year);
        }catch (Exception exception){
        }
        mTextReleaseDate.setText(stringYear);

        mTextRuntime.setText(movieInfo.movieRuntime + getResources().getString(R.string.movie_minutes));

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(mMovieInfo == null){
            return false;
        }

        //Check whether there exists any trailers to be shared.
        if(mMovieInfo.mMovieTrailers.size() > 0) {
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.movie_video_share_menu, menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(mMovieInfo == null){
            return false;
        }

        //Once again ensure that there exists at least one trailer.
        if(mMovieInfo.mMovieTrailers.size() <= 0){
            return false;
        }

        //Get the first movie trailer info.
        MovieInfo.MovieTrailer movieTrailer = mMovieInfo.mMovieTrailers.get(0);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, movieTrailer.mKey);
        startActivity(Intent.createChooser(intent, getResources().getText(R.string.share_trailer)));
        return true;
    }
}//MovieDetailsActivity