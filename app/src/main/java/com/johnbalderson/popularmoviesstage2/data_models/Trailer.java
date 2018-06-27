package com.johnbalderson.popularmoviesstage2.data_models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *  conversion to Parcelable at recommendation of Stage 1 reviewer
 *  https://guides.codepath.com/android/using-parcelable
 */

// build data layout for trailer

public class Trailer implements Parcelable {

    private final String id; //this is not a numerical id
    private final String key;

    public String getKey() {
        return key;
    }

    public Trailer(String id, String key) {
        this.id = id;
        this.key = key;
    }

    private Trailer(Parcel in) {
        id = in.readString();
        key = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(key);
    }

    @Override
    public int describeContents() {
        return 0;
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
