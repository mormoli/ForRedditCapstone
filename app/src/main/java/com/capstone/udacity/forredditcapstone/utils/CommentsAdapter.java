package com.capstone.udacity.forredditcapstone.utils;

import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.capstone.udacity.forredditcapstone.R;
import com.capstone.udacity.forredditcapstone.model.CommentData;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsHolder> {
    private List<CommentData> commentLists;
    private CommentListener commentListener;
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public CommentsAdapter(List<CommentData> commentLists, CommentListener commentListener){
        this.commentLists = commentLists;
        this.commentListener = commentListener;
    }

    @NonNull
    @Override
    public CommentsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_item_card, viewGroup, false);
        return new CommentsHolder(itemView);
    }

    public static String getTimeAgo(long time){
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

    public String numberFormat(int number){
        String strNumber;
        if(Math.abs(number / 1000000) > 1) strNumber = (number / 1000000) + "m";
        else if(Math.abs(number / 1000) > 1) strNumber = (number / 1000) + "k";
        else strNumber = "" + number;
        return strNumber;
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentsHolder commentsHolder, int position) {
        String authorHeader = commentLists.get(position).getAuthor() + " " + numberFormat(commentLists.get(position).getScore()) + "points . " + getTimeAgo(commentLists.get(position).getCreatedUTC());
        commentsHolder.commentHeader.setText(authorHeader);
        commentsHolder.commentBody.setText(commentLists.get(position).getBody());
        //setting listener on save button and to comment layout
        commentsHolder.commentSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentListener.onSaveButtonClicked(v, commentsHolder.getAdapterPosition());
            }
        });
        //on layout click open reply window get necessary information to send data to server
        commentsHolder.commentCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentListener.onLayoutClicked(commentsHolder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentLists.size();
    }

    public class CommentsHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.comment_card_view)
        MaterialCardView commentCardView;
        @BindView(R.id.comment_header_text)
        TextView commentHeader;
        @BindView(R.id.comment_body)
        TextView commentBody;
        @BindView(R.id.comment_save_button)
        Button commentSaveButton;

        public CommentsHolder(View view){
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface CommentListener{
        void onSaveButtonClicked(View view, int position);
        void onLayoutClicked(int position);
    }
}
