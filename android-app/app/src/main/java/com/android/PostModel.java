package com.android;

import android.graphics.Bitmap;

/**
 * Represents and holds everything a post contains
 */
public class PostModel {
    private String username, time;
    private int likes;

//    WILL PROBABLY NEED TO REPLACE THE TYPE FOR IMAGE AT ONE POINT, PERHAPS A REFERENCE TO WHERE IT IS STORED?
    private int image;
    private int avatar;

    private Bitmap post_image;
    private Bitmap avatar_image;

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

    public int getPostAvatar() {
        return image;
    }

    public int getPostImage() {
        return image;
    }
}
