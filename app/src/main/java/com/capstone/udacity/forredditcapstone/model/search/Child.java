package com.capstone.udacity.forredditcapstone.model.search;

import com.google.gson.annotations.SerializedName;

public class Child {
    @SerializedName("kind")
    private String kind;
    @SerializedName("data")
    private SearchData searchData;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public SearchData getSearchData() {
        return searchData;
    }

    public void setSearchData(SearchData searchData) {
        this.searchData = searchData;
    }
}
