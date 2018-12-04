package com.capstone.udacity.forredditcapstone.model.favorites;

import com.google.gson.annotations.SerializedName;

public class FavoritesList {
    @SerializedName("kind")
    private String kind;
    @SerializedName("data")
    private FavoritesData data;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public FavoritesData getData() {
        return data;
    }

    public void setData(FavoritesData data) {
        this.data = data;
    }
}
