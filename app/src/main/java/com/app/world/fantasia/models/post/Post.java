package com.app.world.fantasia.models.post;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.app.world.fantasia.utility.DateUtilities;

public class Post implements Parcelable {

    @SerializedName("id")
    private Double mID;
    @SerializedName("title")
    private Title mTitle = new Title();
    @SerializedName("_embedded")
    private Embedded mEmbedded = new Embedded();
    @SerializedName("date")
    private String mOldDate;
    @SerializedName("excerpt")
    private Excerpt mExcerpt = new Excerpt();
    @SerializedName("link")
    private String mPostUrl;
    @SerializedName("sticky")
    private boolean mIsSticky;
    private String mFormattedDate;
    private boolean mIsBookmark;

    public Double getID() {
        return mID;
    }

    public Title getTitle() {
        return mTitle;
    }

    public Embedded getEmbedded() {
        return mEmbedded;
    }

    public Excerpt getExcerpt() {
        return mExcerpt;
    }

    public String getPostUrl() {
        return mPostUrl;
    }

    public boolean isIsSticky() {
        return mIsSticky;
    }

    public boolean isBookmark() {
        return mIsBookmark;
    }

    public void setBookmark(boolean mIsBookmark) {
        this.mIsBookmark = mIsBookmark;
    }

    public static Creator<Post> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(mID);
        dest.writeParcelable(mTitle, flags);
        dest.writeParcelable(mEmbedded, flags);
        dest.writeString(mOldDate);
        dest.writeString(mFormattedDate);
        dest.writeParcelable(mExcerpt, flags);
        dest.writeString(mPostUrl);
        dest.writeInt(mIsSticky ? 1 : 0);
        dest.writeInt(mIsBookmark ? 1 : 0);
    }

    protected Post(Parcel in) {
        mID = in.readDouble();
        mTitle = in.readParcelable(Title.class.getClassLoader());
        mEmbedded = in.readParcelable(Embedded.class.getClassLoader());
        mOldDate = in.readString();
        mFormattedDate = in.readString();
        mExcerpt = in.readParcelable(Excerpt.class.getClassLoader());
        mPostUrl = in.readString();
        mIsSticky = in.readInt() != 0;
        mIsBookmark = in.readInt() != 0;
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel source) {
            return new Post(source);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    public String getFormattedDate() {
        return DateUtilities.getFormattedDate(mOldDate);
    }
}
