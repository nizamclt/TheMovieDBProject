package com.example.themoviedbproject;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Nisam on 5/23/2017.
 */

public class MovieAdapterReview extends RecyclerView.Adapter<MovieAdapterReview.MovieReviewHolder> {

    private List<MovieInfo.MovieReview> mMovieReviews = null;

    public void setReviewInfo(List<MovieInfo.MovieReview> reviewInfo){
        mMovieReviews = reviewInfo;
    }

    @Override
    public MovieReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.movie_review_view, parent, false);
        return new MovieAdapterReview.MovieReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieReviewHolder movieReviewHolder, int position) {

        if(null == mMovieReviews){
            return;
        }

        Resources resources = movieReviewHolder.itemView.getContext().getResources();
        MovieInfo.MovieReview movieReview = mMovieReviews.get(position);

        String stringId = resources.getString(R.string.label_reviewId) + " " + Integer.toString(position + 1);
        String stringUrl = "<a href=\'";
        stringUrl +=  movieReview.mUrl + "\'>" + stringId + "</a>";

        Spanned htmlResult;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            htmlResult = Html.fromHtml(stringUrl, Html.FROM_HTML_MODE_LEGACY);
        }else{
            htmlResult = Html.fromHtml(stringUrl);
        }

        movieReviewHolder.mTextReviewID.setText(htmlResult);
        Log.d("", stringUrl);
    }

    @Override
    public int getItemCount() {
        if(null == mMovieReviews){
            return 0;
        }

        return mMovieReviews.size();
    }

    public class MovieReviewHolder extends RecyclerView.ViewHolder{

        TextView mTextReviewID;

        public MovieReviewHolder(View itemView) {
            super(itemView);
            mTextReviewID = (TextView) itemView.findViewById(R.id.reviewID);
            mTextReviewID.setClickable(true);
            mTextReviewID.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }
}
