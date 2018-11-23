package com.capstone.udacity.forredditcapstone.model.search;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data {
    @SerializedName("modhash")
    private String modhash;
    @SerializedName("dist")
    private int dist;
    @SerializedName("children")
    private List<Child> children;

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

    public List<Child> getChildren() {
        return children;
    }

    public void setChildren(List<Child> children) {
        this.children = children;
    }
}
