package com.capstone.udacity.forredditcapstone.model.search;

import com.google.gson.annotations.SerializedName;

public class SearchList {
    @SerializedName("kind")
    private String kind;
    @SerializedName("data")
    private Data data;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
