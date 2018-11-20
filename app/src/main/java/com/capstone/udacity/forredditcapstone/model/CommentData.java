package com.capstone.udacity.forredditcapstone.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CommentData implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public CommentData createFromParcel(Parcel in){
            return new CommentData(in);
        }

        public CommentData[] newArray(int size){
            return new CommentData[size];
        }
    };
    @SerializedName("author")
    private String author;
    @SerializedName("score")
    private int score;
    @SerializedName("body")
    private String body;
    @SerializedName("created_utc")
    private int createdUTC;
    @SerializedName("replies")
    private List<Replies> replies;

    public CommentData(){}

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getCreatedUTC() {
        return createdUTC;
    }

    public void setCreatedUTC(int createdUTC) {
        this.createdUTC = createdUTC;
    }

    public List<Replies> getReplies() {
        return replies;
    }

    public void setReplies(List<Replies> replies) {
        this.replies = replies;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private CommentData(Parcel in){
        this.author = in.readString();
        this.score = in.readInt();
        this.body = in.readString();
        this.createdUTC = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.author);
        dest.writeInt(this.score);
        dest.writeString(this.body);
        dest.writeInt(this.createdUTC);
    }
}
