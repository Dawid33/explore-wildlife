package com.android;

/**
 * Represents and holds everything a post contains
 */
public class PostModel {
    private String username, time;
    private int likes;

    private String content;

    private String postID;

//    WILL PROBABLY NEED TO REPLACE THE TYPE FOR IMAGE AT ONE POINT, PERHAPS A REFERENCE TO WHERE IT IS STORED?
    private int image;
    private int avatar;

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    private boolean isLiked = false;

    public PostModel(String postID, String username, String time, int likes, boolean isLiked, String content) {
        this.postID = postID;
        this.username = username;
        this.content = content;
        this.time = time;
        this.likes = likes;
        this.isLiked = isLiked;
    }

    public PostModel(String username, String time, int likes) {
        this.username = username;
        this.time = time;
        this.likes = likes;
        this.image = image;
    }
    public PostModel(String username, String time, int likes, int avatar, int image) {
        this.username = username;
        this.time = time;
        this.likes = likes;
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public String getPostTime() {
        return time;
    }

    public int getPostLikes() {
        return likes;
    }

    public String getPostID() {
        return postID;
    }

    public int getPostAvatar() {
        return image;
    }

    public int getPostImage() {
        return image;
    }
}
