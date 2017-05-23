package com.example.themoviedbproject;

/**
 * Created by Nisam on 4/20/2017.
 */

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;


public class MovieInfo implements Parcelable {

    public String movieID;
    public String movieTitle;
    public String movieVoteAverage;
    public String moviePosterPath;
    public String movieOverView;
    public String movieReleaseDate;
    public String moviePopularity;
    public String movieVoteCount;
    public String movieRuntime;
    public List<MovieTrailer> mMovieTrailers = new ArrayList<MovieTrailer>();
    public List<MovieReview> mMovieReviews = new ArrayList<MovieReview>();


    MovieInfo(){

    }

    MovieInfo(Parcel src){
        movieID = src.readString();
        movieTitle = src.readString();
        movieVoteAverage = src.readString();
        moviePosterPath = src.readString();
        movieOverView = src.readString();
        movieReleaseDate = src.readString();
        moviePopularity = src.readString();
        movieVoteCount = src.readString();
        movieRuntime = src.readString();

        if(null == mMovieTrailers){
            mMovieTrailers = new ArrayList<MovieTrailer>();
        }
        src.readTypedList(mMovieTrailers, MovieTrailer.CREATOR);

        if(null == mMovieReviews){
            mMovieReviews = new ArrayList<MovieReview>();
        }
        src.readTypedList(mMovieReviews, MovieReview.CREATOR);

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
        dest.writeString(movieVoteCount);
        dest.writeString(movieRuntime);
        dest.writeTypedList(mMovieTrailers);
        dest.writeTypedList(mMovieReviews);

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

    public static class MovieTrailer implements  Parcelable{

        public String mSize;
        public String mKey;
        public String mSite;
        public String mType;

        MovieTrailer(){}

        protected MovieTrailer(Parcel in) {
            mSize = in.readString();
            mKey = in.readString();
            mSite = in.readString();
            mType = in.readString();
        }

        public static final Creator<MovieTrailer> CREATOR = new Creator<MovieTrailer>() {
            @Override
            public MovieTrailer createFromParcel(Parcel in) {
                return new MovieTrailer(in);
            }

            @Override
            public MovieTrailer[] newArray(int size) {
                return new MovieTrailer[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(mSize);
            dest.writeString(mKey);
            dest.writeString(mSite);
            dest.writeString(mType);
        }
    }


    static class MovieReview implements Parcelable {

        public String mAuthor;
        public String mContent;
        public String mUrl;

        MovieReview(){}

        MovieReview(Parcel src){
            mAuthor = src.readString();
            mContent = src.readString();
            mUrl = src.readString();
        }

        public static final Creator<MovieReview> CREATOR = new Creator<MovieReview>() {
            @Override
            public MovieReview[] newArray(int size) {
                return new MovieReview[size];
            }

            @Override
            public MovieReview createFromParcel(Parcel source) {
                return new MovieReview(source);
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(mAuthor);
            dest.writeString(mContent);
            dest.writeString(mUrl);
        }
    }
}