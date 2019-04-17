package com.app.world.fantasia.models.post;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Embedded implements Parcelable {

    @SerializedName("wp:featuredmedia")
    private List<WpFeaturedMedia> mWpFeaturedMedias = new ArrayList<>();

    public Embedded() {
    }

    public List<WpFeaturedMedia> getWpFeaturedMedias() {
        return mWpFeaturedMedias;
    }

    public static Creator<Embedded> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(mWpFeaturedMedias);
    }

    protected Embedded(Parcel in) {
        in.readList(mWpFeaturedMedias, WpFeaturedMedia.class.getClassLoader());
    }

    public static final Creator<Embedded> CREATOR = new Creator<Embedded>() {
        @Override
        public Embedded createFromParcel(Parcel source) {
            return new Embedded(source);
        }

        @Override
        public Embedded[] newArray(int size) {
            return new Embedded[size];
        }
    };

}
