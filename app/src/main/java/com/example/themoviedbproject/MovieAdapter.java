package com.example.themoviedbproject;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.example.themoviedbproject.MovieSortCondition;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Nisam on 4/20/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdaptorHolder>  {


    //Private constants loaded from resource file.
    private static String MOVIE_IMAGE_URL;
    private static String CLASS_TAG;
    static boolean bInitStatus = false;

    private ArrayList<MovieInfo> mMovieArrayList;
    private static MovieSortCondition mMovieSortCondition = MovieSortCondition.MOVIE_SORT_CONDITION_MOST_POPULAR;

    @Override
    public MovieAdapter.MovieAdaptorHolder onCreateViewHolder(ViewGroup group, int vewType){

        LayoutInflater layoutInflater = LayoutInflater.from(group.getContext());

        View view = layoutInflater.inflate(R.layout.movie_adapter_view, group, false);
        Init(view);
        return new MovieAdaptorHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdaptorHolder movieAdaptorHolder, int position){

        if(null == mMovieArrayList || mMovieArrayList.size() < position ){
            Log.e(CLASS_TAG, "Invalid position found = " + Integer.toString(position));
            return;
        }

        String strPosterPath = MOVIE_IMAGE_URL;
        strPosterPath += mMovieArrayList.get(position).moviePosterPath;
        movieAdaptorHolder.LoadImage(strPosterPath);

        Log.d(CLASS_TAG, "View Holder Position = " + Integer.toString(position) +
                         " Poster path = " + strPosterPath);

    }//onBindViewHolder

    @Override
    public int getItemCount(){

        if(null != mMovieArrayList){
            return mMovieArrayList.size();
        }
        return 0;
    }

    public static String getMovieImageUrl(){
        return MOVIE_IMAGE_URL;
    }

    public void addMovieInfo(MovieInfo movieInfo){

        if(null == mMovieArrayList){
            mMovieArrayList = new ArrayList<MovieInfo>();
        }
        mMovieArrayList.add( movieInfo);
        Collections.sort(mMovieArrayList, new MovieComparator());
        notifyDataSetChanged();
    }


    public void setMovieInfo(ArrayList<MovieInfo> movieInfos ){

        if(null != movieInfos){
            mMovieArrayList = movieInfos;

            //Sort based on popularity.
            Collections.sort(mMovieArrayList, new MovieComparator());
            notifyDataSetChanged();
        }
    }

    public static void setSortCondition( MovieSortCondition movieSortCondition){

        mMovieSortCondition = movieSortCondition;
    }

    public static MovieSortCondition getSortCondition(){
        return  mMovieSortCondition;
    }

    private void Init(View view){
       if(!bInitStatus){
           CLASS_TAG = getClass().getSimpleName();
           MOVIE_IMAGE_URL = view.getResources().getString(R.string.url_movie_image);
           bInitStatus = true;
       }
    }

    public class MovieAdaptorHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ProgressBar mProgressBar;
        public ImageView mImageView;


        public MovieAdaptorHolder(View view){

            super(view);
            view.setOnClickListener(this);
            mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
            mImageView = (ImageView) view.findViewById(R.id.imageView);
        }

        public void LoadImage(String stringImagePath){

            mProgressBar.setVisibility(View.VISIBLE);
            Picasso.with(itemView.getContext())
                   .load(stringImagePath)
                   .placeholder(R.drawable.loading)
                   .error(R.drawable.error_loading)
                   .into(mImageView);
            mProgressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onClick(View view) {

            int adapterPosition = getAdapterPosition();

            MovieInfo movieInfo = mMovieArrayList.get(adapterPosition);

            Log.d(CLASS_TAG, "Click Position = " + Integer.toString(adapterPosition) +
                             " Poster path = " + movieInfo.moviePosterPath);

            Class destinationClass = MovieDetailsActivity.class;
            Intent movieDetailIntent = new Intent(view.getContext(), destinationClass);

            movieDetailIntent.putExtra(Intent.EXTRA_TEXT, movieInfo);
            view.getContext().startActivity(movieDetailIntent);
        }//onClick
    }//Class MovieAdaptorHolder
}//Class MovieAdapter
