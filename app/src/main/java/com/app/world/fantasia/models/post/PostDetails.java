package com.app.world.fantasia.models.post;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.app.world.fantasia.utility.DateUtilities;

public class PostDetails implements Parcelable {

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
    @SerializedName("content")
    private Content mContent = new Content();
    @SerializedName("link")
    private String mPostUrl;
    @SerializedName("sticky")
    private boolean mIsSticky;
    @SerializedName("_links")
    private Links mLinks = new Links();
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

    public Content getContent() {
        return mContent;
    }

    public void setContent(Content mContent) {
        this.mContent = mContent;
    }

    public String getPostUrl() {
        return mPostUrl;
    }

    public Links getLinks() {
        return mLinks;
    }

    public static Creator<PostDetails> getCREATOR() {
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
        dest.writeParcelable(mContent, flags);
        dest.writeString(mPostUrl);
        dest.writeParcelable(mLinks, flags);
        dest.writeInt(mIsSticky ? 1 : 0);
        dest.writeInt(mIsBookmark ? 1 : 0);
    }

    protected PostDetails(Parcel in) {
        mID = in.readDouble();
        mTitle = in.readParcelable(Title.class.getClassLoader());
        mEmbedded = in.readParcelable(Embedded.class.getClassLoader());
        mOldDate = in.readString();
        mFormattedDate = in.readString();
        mExcerpt = in.readParcelable(Excerpt.class.getClassLoader());
        mContent = in.readParcelable(Excerpt.class.getClassLoader());
        mLinks = in.readParcelable(Excerpt.class.getClassLoader());
        mPostUrl = in.readString();
        mIsSticky = in.readInt() != 0;
        mIsBookmark = in.readInt() != 0;
    }

    public static final Creator<PostDetails> CREATOR = new Creator<PostDetails>() {
        @Override
        public PostDetails createFromParcel(Parcel source) {
            return new PostDetails(source);
        }

        @Override
        public PostDetails[] newArray(int size) {
            return new PostDetails[size];
        }
    };

    public String getFormattedDate() {
        return DateUtilities.getFormattedDate(mOldDate);
    }
}
