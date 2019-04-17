package com.app.world.fantasia.models.bookmark;

public class BookmarkModel {
    private int id;
    private int postId;
    private String postImageUrl;
    private String postTitle;
    private String postExcerpt;
    private String postUrl;
    private String formattedDate;

    public BookmarkModel(int id, int postId, String postImageUrl, String postTitle, String postExcerpt, String postUrl, String formattedDate) {
        this.id = id;
        this.postId = postId;
        this.postImageUrl = postImageUrl;
        this.postTitle = postTitle;
        this.postExcerpt = postExcerpt;
        this.postUrl = postUrl;
        this.formattedDate = formattedDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPostId() {
        return postId;
    }

    public String getPostImageUrl() {
        return postImageUrl;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public String getPostExcerpt() {
        return postExcerpt;
    }

    public String getPostUrl() {
        return postUrl;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

}