package com.capstone.udacity.forredditcapstone.model;

import com.google.gson.annotations.SerializedName;

public class CommentList {
    @SerializedName("kind")
    private String kind;
    @SerializedName("data")
    private CommentMainData data;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public CommentMainData getData() {
        return data;
    }

    public void setData(CommentMainData data) {
        this.data = data;
    }
}
