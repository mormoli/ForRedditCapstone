package com.capstone.udacity.forredditcapstone.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "comment_table")
public class Comment {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "name")
    private String fullname;
    private String author;
    private String body;
    private int score;
    private int createdUTC;

    public Comment(@NonNull String fullname, String author, String body, int score, int createdUTC){
        this.fullname = fullname;
        this.author = author;
        this.body = body;
        this.score = score;
        this.createdUTC = createdUTC;
    }
    //Every field that's stored in the database needs to be either public or have a "getter" method.
    @NonNull
    public String getFullname() {
        return fullname;
    }

    public String getAuthor() {
        return author;
    }

    public String getBody() {
        return body;
    }

    public int getScore() {
        return score;
    }

    public int getCreatedUTC() {
        return createdUTC;
    }
}
