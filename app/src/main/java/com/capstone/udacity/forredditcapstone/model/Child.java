package com.capstone.udacity.forredditcapstone.model;

import com.google.gson.annotations.SerializedName;

public class Child {
    @SerializedName("kind")
    private String kind;
    @SerializedName("data")
    private PostData data;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public PostData getData() {
        return data;
    }

    public void setData(PostData data) {
        this.data = data;
    }
}
