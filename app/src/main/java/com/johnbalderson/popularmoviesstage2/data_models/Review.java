package com.johnbalderson.popularmoviesstage2.data_models;

import android.os.Parcel;
import android.os.Parcelable;



/**
 *  conversion to Parcelable at recommendation of Stage 1 reviewer
 *  https://guides.codepath.com/android/using-parcelable
 */

// build data layout for review

public class Review implements Parcelable {
    private final String author;
    private final String content;

    public Review(String author, String content) {
        this.author = author;
        this.content = content;
    }

    private Review(Parcel in) {
        this.author = in.readString();
        this.content = in.readString();

    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(content);
    }
}


