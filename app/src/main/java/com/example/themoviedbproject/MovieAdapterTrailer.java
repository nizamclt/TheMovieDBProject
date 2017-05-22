package com.example.themoviedbproject;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Nisam on 5/22/2017.
 */

public class MovieAdapterTrailer extends RecyclerView.Adapter<MovieAdapterTrailer.MovieTrailerHolder> {

    List<MovieInfo.MovieTrailer> mMovieTrailers = null;


    public void setTrailerInfo(List<MovieInfo.MovieTrailer> movieTrailers){
        mMovieTrailers = movieTrailers;
    }

    @Override
    public MovieTrailerHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.movie_trailer_view, parent, false);
        return new MovieTrailerHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieTrailerHolder movieReviewHolder, int position) {

        if(null == mMovieTrailers){
            return;
        }

        MovieInfo.MovieTrailer movieTrailer = mMovieTrailers.get(position);

        String stringId = "Trailer " + Integer.toString(position + 1);
        String stringUrl = "<a href=\"";
        stringUrl +=  movieTrailer.mKey + ">" + stringId + "</a>";

        movieReviewHolder.mTextTrailerId.setText(stringUrl);
        Log.d("", stringUrl);
    }

    @Override
    public int getItemCount() {

        if(null == mMovieTrailers) {
            return 0;
        }
        return  mMovieTrailers.size();
    }

    public class MovieTrailerHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/{

        TextView mTextTrailerId;

        public MovieTrailerHolder(View itemView) {
            super(itemView);
            mTextTrailerId = (TextView) itemView.findViewById(R.id.trailerID);
            //mTextTrailerId.setMovementMethod(LinkMovementMethod.getInstance());
        }
/*
        @Override
        public void onClick(View v) {

        }*/
    }
}
