package com.capstone.udacity.forredditcapstone.model.favorites;

import com.google.gson.annotations.SerializedName;

public class Favorites {
    @SerializedName("kind")
    private String kind;
    @SerializedName("data")
    private FavoritesChild data;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public FavoritesChild getData() {
        return data;
    }

    public void setData(FavoritesChild data) {
        this.data = data;
    }
}
