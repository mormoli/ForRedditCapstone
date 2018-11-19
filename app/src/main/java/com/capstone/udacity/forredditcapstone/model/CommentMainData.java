package com.capstone.udacity.forredditcapstone.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CommentMainData {
    @SerializedName("modhash")
    private String modhash;
    @SerializedName("dist")
    private int dist;
    @SerializedName("children")
    private List<CommentChild> children;
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

    public List<CommentChild> getChildren() {
        return children;
    }

    public void setChildren(List<CommentChild> children) {
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
