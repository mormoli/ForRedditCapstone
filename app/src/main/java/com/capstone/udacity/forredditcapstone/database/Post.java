package com.capstone.udacity.forredditcapstone.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "post_table")
public class Post {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "name")
    private String fullname;
    private String subredditName;
    private String id;
    private String header;
    private String thumbnail;
    private String author;
    private String title;
    private String selftext;
    private String permalink;
    private String ups;
    private String comments;
    private String createdUTC;
    private String imageDetailURL;

    public Post(@NonNull String fullname, String subredditName, String id,String header, String thumbnail, String author, String title, String selftext, String permalink, String ups, String comments, String createdUTC, String imageDetailURL) {
        this.fullname = fullname;
        this.subredditName = subredditName;
        this.id = id;
        this.header = header;
        this.thumbnail = thumbnail;
        this.author = author;
        this.title = title;
        this.selftext = selftext;
        this.permalink = permalink;
        this.ups = ups;
        this.comments = comments;
        this.createdUTC = createdUTC;
        this.imageDetailURL = imageDetailURL;
    }
    //Every field that's stored in the database needs to be either public or have a "getter" method.
    @NonNull
    public String getFullname() {
        return fullname;
    }

    public String getSubredditName() {
        return subredditName;
    }

    public String getId() {
        return id;
    }

    public String getHeader(){ return header; }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getSelftext() {
        return selftext;
    }

    public String getPermalink() {
        return permalink;
    }

    public String getUps() {
        return ups;
    }

    public String getComments() {
        return comments;
    }

    public String getCreatedUTC() {
        return createdUTC;
    }

    public String getImageDetailURL() {
        return imageDetailURL;
    }

    public void setFullname(@NonNull String fullname) {
        this.fullname = fullname;
    }

    public void setSubredditName(String subredditName) {
        this.subredditName = subredditName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSelftext(String selftext) {
        this.selftext = selftext;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public void setUps(String ups) {
        this.ups = ups;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setCreatedUTC(String createdUTC) {
        this.createdUTC = createdUTC;
    }

    public void setImageDetailURL(String imageDetailURL) {
        this.imageDetailURL = imageDetailURL;
    }
}
