package com.capstone.udacity.forredditcapstone.utils;

import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.capstone.udacity.forredditcapstone.R;
import com.capstone.udacity.forredditcapstone.database.Favorite;
import com.capstone.udacity.forredditcapstone.model.favorites.FavoritesData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoritesHolder>{
    private List<FavoritesData> favoritesData;
    private List<Favorite> favorites = new ArrayList<>();
    private LayoutClickListener layoutClickListener;
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public FavoritesAdapter(List<FavoritesData> favoritesData, LayoutClickListener layoutClickListener){
        this.favoritesData = favoritesData;
        this.layoutClickListener = layoutClickListener;
    }

    @NonNull
    @Override
    public FavoritesHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.favorites_list_item, viewGroup, false);
        return new FavoritesHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final FavoritesHolder holder, int position) {
        if(favorites != null && favorites.size() > 0){ // database list item check
            if(!TextUtils.isEmpty(favorites.get(position).getBody())){//body exist so the data is t1 : comment
                String header = favorites.get(position).getAuthor() + " " + getTimeAgo(favorites.get(position).getCreatedUTC());
                holder.headerText.setText(header);
                holder.bodyText.setText(Html.fromHtml(favorites.get(position).getBody()));
            } else { // body not exist so the data is t3 : post
                String header = favorites.get(position).getTitle() + " ( " + favorites.get(position).getDomain() + ")";
                holder.headerText.setText(header);
                String body = "submitted " + getTimeAgo(favorites.get(position).getCreatedUTC()) + " * by "
                        + favorites.get(position).getAuthor() + " " + favorites.get(position).getSubredditNamePrefixed();
                holder.bodyText.setText(body);
            }
        } else {
            if(!TextUtils.isEmpty(favoritesData.get(position).getBody())){//body exist so the data is t1 : comment
                String header = favoritesData.get(position).getAuthor() + " " + getTimeAgo(favoritesData.get(position).getCreatedUTC());
                holder.headerText.setText(header);
                holder.bodyText.setText(Html.fromHtml(favoritesData.get(position).getBody()));
            } else { // body not exist so the data is t3 : post
                String header = favoritesData.get(position).getTitle() + " ( " + favoritesData.get(position).getDomain() + ")";
                holder.headerText.setText(header);
                String body = "submitted " + getTimeAgo(favoritesData.get(position).getCreatedUTC()) + " * by "
                        + favoritesData.get(position).getAuthor() + " " + favoritesData.get(position).getSubredditNamePrefixed();
                holder.bodyText.setText(body);
            }
        }
        //User layout click handled here.
        holder.materialCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutClickListener.OnLayoutClicked(holder.getAdapterPosition());
            }
        });
    }

    public void setFavorites(List<Favorite> favorites) {
        this.favorites = favorites;
        //notifyDataSetChanged();
        notifyItemRangeChanged(0, favorites.size());
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

    @Override
    public int getItemCount() {
        if(favorites != null && favorites.size() > 0) return favorites.size();
        else return favoritesData.size();
    }

    public class FavoritesHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.favorite_list_item_layout)
        MaterialCardView materialCardView;
        @BindView(R.id.favorite_card_header_text)
        TextView headerText;
        @BindView(R.id.favorite_card_body_text)
        TextView bodyText;
        public FavoritesHolder(View view){
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface LayoutClickListener{
        void OnLayoutClicked(int position);
    }

    /*public void setLayoutClickListener(LayoutClickListener layoutClicked){
        this.layoutClickListener = layoutClicked;
    }*/
}
