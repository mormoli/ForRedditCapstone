package com.capstone.udacity.forredditcapstone.utils;

import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.capstone.udacity.forredditcapstone.R;
import com.capstone.udacity.forredditcapstone.model.Child;
import com.capstone.udacity.forredditcapstone.model.PostData;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomePageAdapter extends RecyclerView.Adapter<HomePageAdapter.HomePageHolder> {
    private List<Child> childList;
    private ButtonsListener buttonsListener;
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    public HomePageAdapter(List<Child> childList, ButtonsListener buttonsListener){
        this.childList = childList;
        this.buttonsListener = buttonsListener;
    }

    public class HomePageHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.card_view)
        MaterialCardView materialCardView;
        @BindView(R.id.card_header_text)
        TextView headerText;
        @BindView(R.id.post_title_text)
        TextView postTitle;
        @BindView(R.id.thumbnail_image_view)
        ImageView postImage;
        //@BindView(R.id.card_subtitle_text)
        //TextView postSubtitle;
        @BindView(R.id.points_text_view)
        TextView points;
        @BindView(R.id.comments_text_view)
        TextView comments;
        @BindView(R.id.hide_button)
        ImageButton hideButton;
        @BindView(R.id.save_button)
        ImageButton saveButton;
        public HomePageHolder(View view){
            super(view);
            ButterKnife.bind(this, view);
        }
    }
    //@see 'https://stackoverflow.com/questions/13018550/time-since-ago-library-for-android-java'
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

    @NonNull
    @Override
    public HomePageHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_card, viewGroup, false);
        return new HomePageHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final HomePageHolder holder, int position) {
        PostData data = childList.get(position).getData();
        //Header of the card
        String header = data.getSubredditNamePrefixed() + " . posted bu u/"+ data.getAuthor() +" "+ getTimeAgo(data.getCreatedUTC());
        holder.headerText.setText(header);
        //post title
        if(!TextUtils.isEmpty(data.getTitle())) holder.postTitle.setText(data.getTitle());
        else holder.postTitle.setVisibility(View.GONE);
        //image if exist
        if(!TextUtils.isEmpty(data.getThumbnail())){
            //holder.postImage.setLayoutParams(new RelativeLayout.LayoutParams(100,80));
            Picasso.get()
                    .load(data.getThumbnail())
                    .resize(144, 108)
                    .onlyScaleDown() //// the image will only be resized if it's bigger than 120x120 pixels.
                    .into(holder.postImage);
        } else {
            holder.postImage.setVisibility(View.GONE);
        }
        //set sub text if exist or visibility gone
        //if(!TextUtils.isEmpty(data.getSelftext())) holder.postSubtitle.setText(data.getSelftext());
        //else holder.postSubtitle.setVisibility(View.GONE);
        //Footer items
        //format numbers for ups and comments
        //@see 'https://stackoverflow.com/questions/4753251/how-to-go-about-formatting-1200-to-1-2k-in-java'
        String ups = numberFormat(data.getUps()) + " points";
        holder.points.setText(ups);
        String comments = numberFormat(data.getNumComments()) + " comments";
        holder.comments.setText(comments);
        //BUTTONS setting click listeners
        holder.hideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonsListener.onHideButtonClick(v, holder.getAdapterPosition());
            }
        });

        holder.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonsListener.onSaveButtonClick(v, holder.getAdapterPosition());
            }
        });
        //Setting listener to the view
        holder.materialCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonsListener.onLayoutClicked(holder.getAdapterPosition());
            }
        });
    }

    public String numberFormat(int number){
        String strNumber;
        if(Math.abs(number / 1000000) > 1) strNumber = (number / 1000000) + "m";
        else if(Math.abs(number / 1000) > 1) strNumber = (number / 1000) + "k";
        else strNumber = "" + number;
        return strNumber;
    }

    @Override
    public int getItemCount() {
        return childList.size();
    }

    public interface ButtonsListener{
        void onHideButtonClick(View view, int position);
        void onSaveButtonClick(View view, int position);
        void onLayoutClicked(int position);
    }
}
