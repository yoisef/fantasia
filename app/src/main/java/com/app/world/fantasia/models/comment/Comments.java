package com.app.world.fantasia.models.comment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.app.world.fantasia.models.post.Content;

public class Comments implements Parcelable {
    @SerializedName("id")
    private Double mID;
    @SerializedName("parent")
    private Double mParent;
    @SerializedName("author_name")
    private String mAuthorName;
    @SerializedName("content")
    private Content mContent = new Content();
    @SerializedName("author_avatar_urls")
    private AuthorAvatar mAuthorAvatarUrl = new AuthorAvatar();

    public Double getParent() {
        return mParent;
    }

    public void setParent(Double mParent) {
        this.mParent = mParent;
    }

    public String getAuthorName() {
        return mAuthorName;
    }

    public Content getContent() {
        return mContent;
    }

    public void setContent(Content mContent) {
        this.mContent = mContent;
    }

    public AuthorAvatar getAuthorAvatarUrl() {
        return mAuthorAvatarUrl;
    }

    public static Creator<Comments> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(mID);
        dest.writeDouble(mParent);
        dest.writeString(mAuthorName);
        dest.writeParcelable(mContent, flags);
        dest.writeParcelable(mAuthorAvatarUrl, flags);
    }

    protected Comments(Parcel in) {
        mID = in.readDouble();
        mParent = in.readDouble();
        mAuthorName = in.readString();
        mContent = in.readParcelable(Content.class.getClassLoader());
        mAuthorAvatarUrl = in.readParcelable(AuthorAvatar.class.getClassLoader());
    }

    public static final Creator<Comments> CREATOR = new Creator<Comments>() {
        @Override
        public Comments createFromParcel(Parcel source) {
            return new Comments(source);
        }

        @Override
        public Comments[] newArray(int size) {
            return new Comments[size];
        }
    };

}
