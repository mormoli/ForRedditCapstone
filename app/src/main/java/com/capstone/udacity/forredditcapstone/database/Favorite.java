package com.capstone.udacity.forredditcapstone.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "favorites_table")
public class Favorite {
    private String title;
    private String subreddit;
    private String domain;
    private String subredditNamePrefixed;
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "name")
    private String fullname;
    private String author;
    private String permalink;
    private int createdUTC;
    private String body;
    private String linkTitle;

    public Favorite(String title, String subreddit, String domain, String subredditNamePrefixed, @NonNull String fullname, String author, String permalink, int createdUTC, String body, String linkTitle) {
        this.title = title;
        this.subreddit = subreddit;
        this.domain = domain;
        this.subredditNamePrefixed = subredditNamePrefixed;
        this.fullname = fullname;
        this.author = author;
        this.permalink = permalink;
        this.createdUTC = createdUTC;
        this.body = body;
        this.linkTitle = linkTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubreddit() {
        return subreddit;
    }

    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getSubredditNamePrefixed() {
        return subredditNamePrefixed;
    }

    public void setSubredditNamePrefixed(String subredditNamePrefixed) {
        this.subredditNamePrefixed = subredditNamePrefixed;
    }

    @NonNull
    public String getFullname() {
        return fullname;
    }

    public void setFullname(@NonNull String fullname) {
        this.fullname = fullname;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public int getCreatedUTC() {
        return createdUTC;
    }

    public void setCreatedUTC(int createdUTC) {
        this.createdUTC = createdUTC;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getLinkTitle() {
        return linkTitle;
    }

    public void setLinkTitle(String linkTitle) {
        this.linkTitle = linkTitle;
    }
}
