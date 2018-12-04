package com.capstone.udacity.forredditcapstone.model.favorites;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class FavoritesData implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public FavoritesData createFromParcel(Parcel in){
            return new FavoritesData(in);
        }

        public FavoritesData[] newArray(int size){
            return new FavoritesData[size];
        }
    };
    @SerializedName("title")
    private String title;
    @SerializedName("subreddit")
    private String subreddit;
    @SerializedName("domain")
    private String domain;
    @SerializedName("subreddit_name_prefixed")
    private String subredditNamePrefixed;
    @SerializedName("name")
    private String fullname;
    @SerializedName("author")
    private String author;
    @SerializedName("permalink")
    private String permalink;
    @SerializedName("created_utc")
    private int createdUTC;
    @SerializedName("body")
    private String body;
    @SerializedName("link_title")
    private String linkTitle;

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

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    private FavoritesData(Parcel in){
        this.title = in.readString();
        this.subreddit = in.readString();
        this.domain = in.readString();
        this.subredditNamePrefixed = in.readString();
        this.fullname = in.readString();
        this.author = in.readString();
        this.permalink = in.readString();
        this.createdUTC = in.readInt();
        this.body = in.readString();
        this.linkTitle = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.subreddit);
        dest.writeString(this.domain);
        dest.writeString(this.subredditNamePrefixed);
        dest.writeString(this.fullname);
        dest.writeString(this.author);
        dest.writeString(this.permalink);
        dest.writeInt(this.createdUTC);
        dest.writeString(this.body);
        dest.writeString(this.linkTitle);
    }
}
