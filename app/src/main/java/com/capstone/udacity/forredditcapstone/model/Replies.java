package com.capstone.udacity.forredditcapstone.model;

import java.util.List;

public class Replies {
    private List<CommentList> commentList;

    public List<CommentList> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<CommentList> commentList) {
        this.commentList = commentList;
    }
}
