package com.capstone.udacity.forredditcapstone.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SubredditData{
    @SerializedName("modhash")
    private String modhash;
    @SerializedName("dist")
    private int dist;
    @SerializedName("children")
    private List<Child> children;
    @SerializedName("after")
    private String after;
    @SerializedName("before")
    private String before;

    public SubredditData(){}

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
