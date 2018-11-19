package com.capstone.udacity.forredditcapstone.model;

import com.google.gson.annotations.SerializedName;

public class CommentChild {
    @SerializedName("kind")
    private String kind;
    @SerializedName("data")
    private CommentData data;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public CommentData getData() {
        return data;
    }

    public void setData(CommentData data) {
        this.data = data;
    }
}
