package com.android;

/**
 * Represents and holds everything a post contains
 */
public class PostModel {
    private String username, time;
    private int likes, comments;

//    WILL PROBABLY NEED TO REPLACE THE TYPE FOR IMAGE AT ONE POINT, PERHAPS A REFERENCE TO WHERE IT IS STORED?
    private int image;

    public PostModel(String username, String time, int likes, int comments, int image) {
        this.username = username;
        this.time = time;
        this.likes = likes;
        this.comments = comments;
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public String getTime() {
        return time;
    }

    public int getLikes() {
        return likes;
    }

    public int getComments() {
        return comments;
    }

    public int getImage() {
        return image;
    }
}
