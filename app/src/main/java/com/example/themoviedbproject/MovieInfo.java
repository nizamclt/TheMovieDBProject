package com.example.themoviedbproject;

/**
 * Created by Nisam on 4/20/2017.
 */

import android.os.Parcel;
import android.os.Parcelable;


public class MovieInfo implements Parcelable {

    public String movieID;
    public String movieTitle;
    public String movieVoteAverage;
    public String moviePosterPath;
    public String movieOverView;
    public String movieReleaseDate;
    public String moviePopularity;


    MovieInfo(){

    }

    MovieInfo( Parcel src){
        movieID = src.readString();
        movieTitle = src.readString();
        movieVoteAverage = src.readString();
        moviePosterPath = src.readString();
        movieOverView = src.readString();
        movieReleaseDate = src.readString();
        moviePopularity = src.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(movieID);
        dest.writeString(movieTitle);
        dest.writeString(movieVoteAverage);
        dest.writeString(moviePosterPath);
        dest.writeString(movieOverView);
        dest.writeString(movieReleaseDate);
        dest.writeString(moviePopularity);
    }

    public static final Creator<MovieInfo> CREATOR = new Creator<MovieInfo>() {
        @Override
        public MovieInfo[] newArray(int size) {
            return new MovieInfo[size];
        }

        @Override
        public MovieInfo createFromParcel(Parcel source) {
            return new MovieInfo(source);
        }
    };
}