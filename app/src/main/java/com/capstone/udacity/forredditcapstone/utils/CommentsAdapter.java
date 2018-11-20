package com.capstone.udacity.forredditcapstone.utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.capstone.udacity.forredditcapstone.model.CommentList;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsHolder> {
    List<CommentList> commentLists;

    public CommentsAdapter(List<CommentList> commentLists){
        this.commentLists = commentLists;
    }

    @NonNull
    @Override
    public CommentsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsHolder commentsHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class CommentsHolder extends RecyclerView.ViewHolder{
        public CommentsHolder(View view){
            super(view);
        }
    }


}
