package com.app.world.fantasia.models.post;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Excerpt implements Parcelable {
    @SerializedName("rendered")
    private String mRendered;

    public Excerpt() {

    }

    public String getRendered() {
        return mRendered;
    }

    public static Creator<Excerpt> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mRendered);
    }

    protected Excerpt(Parcel in) {
        mRendered = in.readString();
    }

    public static final Creator<Excerpt> CREATOR = new Creator<Excerpt>() {
        @Override
        public Excerpt createFromParcel(Parcel source) {
            return new Excerpt(source);
        }

        @Override
        public Excerpt[] newArray(int size) {
            return new Excerpt[size];
        }
    };

}