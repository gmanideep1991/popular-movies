package com.deepu.android.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Trailer implements Parcelable {

    private String trailerKey;

    private Trailer(Parcel in) {
        this.trailerKey = in.readString();
    }

    public Trailer() {
    }

    public String getTrailerKey() {
        return trailerKey;
    }

    public void setTrailerKey(String trailerKey) {
        this.trailerKey = trailerKey;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(trailerKey);
    }

    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };
}
