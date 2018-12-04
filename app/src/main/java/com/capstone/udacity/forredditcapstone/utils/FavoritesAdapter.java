package com.capstone.udacity.forredditcapstone.utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.capstone.udacity.forredditcapstone.R;
import com.capstone.udacity.forredditcapstone.database.Favorite;
import com.capstone.udacity.forredditcapstone.model.favorites.FavoritesData;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoritesHolder>{
    private List<FavoritesData> favoritesData;
    private List<Favorite> favorites;

    public FavoritesAdapter(List<FavoritesData> favoritesData){
        this.favoritesData = favoritesData;
    }

    @NonNull
    @Override
    public FavoritesHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.favorites_list_item, viewGroup, false);
        return new FavoritesHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        if(favorites != null && favorites.size() > 0) return favorites.size();
        else return favoritesData.size();
    }

    public class FavoritesHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.favorite_card_header_text)
        TextView headerText;
        @BindView(R.id.favorite_card_body_text)
        TextView bodyText;
        public FavoritesHolder(View view){
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
