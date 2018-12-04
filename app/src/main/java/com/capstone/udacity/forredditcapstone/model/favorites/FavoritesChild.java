package com.capstone.udacity.forredditcapstone.model.favorites;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FavoritesChild {
    @SerializedName("modhash")
    private String modhash;
    @SerializedName("dist")
    private int dist;
    @SerializedName("children")
    private List<FavoritesList> favoritesList;
    @SerializedName("after")
    private String after;
    @SerializedName("before")
    private String before;

    public String getModhash() {
        return modhash;
    }

    public void setModhash(String modhash) {
        this.modhash = modhash;
    }

    public int getDist() {
        return dist;
    }

    public void setDist(int dist) {
        this.dist = dist;
    }

    public List<FavoritesList> getFavoritesList() {
        return favoritesList;
    }

    public void setFavoritesData(List<FavoritesList> favoritesData) {
        this.favoritesList = favoritesData;
    }

    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }

    public String getBefore() {
        return before;
    }

    public void setBefore(String before) {
        this.before = before;
    }
}
