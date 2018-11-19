package com.capstone.udacity.forredditcapstone.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class PostData implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public PostData createFromParcel(Parcel in){
            return new PostData(in);
        }

        public PostData[] newArray(int size){
            return new PostData[size];
        }
    };

    public PostData(){}

    @SerializedName("subreddit")
    private String subredditName;
    @SerializedName("id")
    private String id;
    @SerializedName("subreddit_name_prefixed")
    private String subredditNamePrefixed;
    @SerializedName("author")
    private String author;
    @SerializedName("created_utc")
    private int createdUTC;
    @SerializedName("title")
    private String title;
    @SerializedName("selftext")
    private String selftext;
    @SerializedName("thumbnail")
    private String thumbnail;
    @SerializedName("ups")
    private int ups;
    @SerializedName("num_comments")
    private int numComments;
    @SerializedName("over_18")
    private boolean over18;
    @SerializedName("permalink")
    private String permalink;

    public String getSubredditName() {
        return subredditName;
    }

    public void setSubredditName(String subredditName) {
        this.subredditName = subredditName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubredditNamePrefixed() {
        return subredditNamePrefixed;
    }

    public void setSubredditNamePrefixed(String subredditNamePrefixed) {
        this.subredditNamePrefixed = subredditNamePrefixed;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getCreatedUTC() {
        return createdUTC;
    }

    public void setCreatedUTC(int createdUTC) {
        this.createdUTC = createdUTC;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSelftext() {
        return selftext;
    }

    public void setSelftext(String selftext) {
        this.selftext = selftext;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getUps() {
        return ups;
    }

    public void setUps(int ups) {
        this.ups = ups;
    }

    public int getNumComments() {
        return numComments;
    }

    public void setNumComments(int numComments) {
        this.numComments = numComments;
    }

    public boolean isOver18() {
        return over18;
    }

    public void setOver18(boolean over18) {
        this.over18 = over18;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    private PostData(Parcel in){
        this.subredditName = in.readString();
        this.id = in.readString();
        this.subredditNamePrefixed = in.readString();
        this.author = in.readString();
        this.createdUTC = in.readInt();
        this.title = in.readString();
        this.selftext = in.readString();
        this.thumbnail = in.readString();
        this.ups = in.readInt();
        this.numComments = in.readInt();
        this.over18 = in.readByte() != 0;
        this.permalink = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.subredditName);
        dest.writeString(this.id);
        dest.writeString(this.subredditNamePrefixed);
        dest.writeString(this.author);
        dest.writeInt(this.createdUTC);
        dest.writeString(this.title);
        dest.writeString(this.selftext);
        dest.writeString(this.thumbnail);
        dest.writeInt(this.ups);
        dest.writeInt(this.numComments);
        dest.writeByte((byte) (this.over18 ? 1 : 0));
        dest.writeString(this.permalink);
    }

    @NonNull
    @Override
    public String toString() {
        return "PostData{" +
                "id='" + id + '\'' +
                ", subredditNamePrefixed='" + subredditNamePrefixed + '\'' +
                ", author='" + author + '\'' +
                ", createdUTC=" + createdUTC +
                ", title='" + title + '\'' +
                ", selftext='" + selftext + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", ups=" + ups +
                ", numComments=" + numComments +
                ", over18=" + over18 +
                '}';
    }
}
