package com.example.themoviedbproject;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.lang.reflect.Array;
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
        String stringUrl = "<a href=\'";
        stringUrl +=  movieTrailer.mKey + "\'>" + stringId + "</a>";

        Spanned htmlResult;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            htmlResult = Html.fromHtml(stringUrl, Html.FROM_HTML_MODE_LEGACY);
        }else{
            htmlResult = Html.fromHtml(stringUrl);
        }

        movieReviewHolder.mTextTrailerId.setText(htmlResult);

        Log.d("", stringId);
    }

    @Override
    public int getItemCount() {

        if(null == mMovieTrailers) {
            return 0;
        }
        return  mMovieTrailers.size();
    }

    public class MovieTrailerHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/{

        ImageButton mImageButton;
        TextView mTextTrailerId;

        public MovieTrailerHolder(View itemView) {
            super(itemView);
            mTextTrailerId = (TextView) itemView.findViewById(R.id.trailerID);
            mImageButton = (ImageButton) itemView.findViewById(R.id.imageButtonPlay);
            mTextTrailerId.setClickable(true);
            mTextTrailerId.setMovementMethod(LinkMovementMethod.getInstance());

            mImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    View viewParent = (View)v.getParent();
                    if(viewParent == null){
                        return;
                    }
                    
                    //Find the hyper link text view.
                    TextView textView = (TextView) viewParent.findViewById(R.id.trailerID);
                    URLSpan[] stringLink = textView.getUrls();
                    if(Array.getLength(stringLink) <=0 ){
                        return;
                    }
                    String stringURL = stringLink[0].getURL();

                    Intent videoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(stringURL));
                    v.getContext().startActivity(videoIntent);
                }
            });

        }
/*
        @Override
        public void onClick(View v) {

        }*/
    }
}
