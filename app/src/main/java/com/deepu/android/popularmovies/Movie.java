package com.deepu.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {
    private String title;
    private String imageURL;
    private String synopsis;
    private Double voteAverage;
    private String releaseDate;

    private Movie(Parcel parcel){
        title = parcel.readString();
        imageURL = parcel.readString();
        synopsis = parcel.readString();
        voteAverage = (Double) parcel.readValue(Double.class.getClassLoader());
        releaseDate = parcel.readString();
    }

    public Movie(){}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageURL() {
        final String TMDB_POSTER_BASE_URL = "https://image.tmdb.org/t/p/w185";

        return TMDB_POSTER_BASE_URL+ imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public String getRating(){
        return String.valueOf(getVoteAverage()) + "/10";
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(imageURL);
        parcel.writeString(synopsis);
        parcel.writeValue(voteAverage);
        parcel.writeString(releaseDate);
    }
    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }
    };
}
