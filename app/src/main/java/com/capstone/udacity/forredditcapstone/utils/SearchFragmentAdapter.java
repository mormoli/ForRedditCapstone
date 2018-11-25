package com.capstone.udacity.forredditcapstone.utils;

import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.capstone.udacity.forredditcapstone.R;
import com.capstone.udacity.forredditcapstone.model.search.SearchData;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchFragmentAdapter extends RecyclerView.Adapter<SearchFragmentAdapter.SearchFragmentHolder> {
    private List<SearchData> searchData;
    private OnItemClicked onItemClicked;

    public SearchFragmentAdapter(List<SearchData> searchData){
        this.searchData = searchData;
    }

    @NonNull
    @Override
    public SearchFragmentHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_list_item, viewGroup, false);
        return new SearchFragmentHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final SearchFragmentHolder searchFragmentHolder, int position) {
        String header = searchData.get(position).getDisplayNamePrefixed() + " : " + searchData.get(position).getTitle();
        searchFragmentHolder.headerText.setText(header);
        if(!TextUtils.isEmpty(searchData.get(position).getPublicDescription()))
            searchFragmentHolder.bodyText.setText(searchData.get(position).getPublicDescription());
        else searchFragmentHolder.bodyText.setVisibility(View.GONE);
        String subscribers = numberFormat(searchData.get(position).getSubscribers()) + " Subscribers";
        searchFragmentHolder.subscribersText.setText(subscribers);
        if(searchData.get(position).isUserIsSubscriber()){
            //searchFragmentHolder.confirmButton.setBackgroundColor(ContextCompat.getColor(searchFragmentHolder.confirmButton.getContext(), R.color.colorAccent));
            searchFragmentHolder.confirmButton.setBackgroundTintList(ContextCompat.getColorStateList(searchFragmentHolder.confirmButton.getContext(), R.color.colorAccent));
            searchFragmentHolder.confirmButton.setText(R.string.unsubscribe_button);
        }

        searchFragmentHolder.confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClicked.onItemClick(v, searchFragmentHolder.getAdapterPosition());
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
        return searchData.size();
    }

    public class SearchFragmentHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.search_card_header_text)
        TextView headerText;
        @BindView(R.id.search_card_body_text)
        TextView bodyText;
        @BindView(R.id.search_card_subscribers_text)
        TextView subscribersText;
        @BindView(R.id.subscribe_button)
        MaterialButton confirmButton;

        public SearchFragmentHolder(View view){
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface OnItemClicked{
        void onItemClick(View view, int position);
    }

    public void setOnClick(OnItemClicked onItemClicked){
        this.onItemClicked = onItemClicked;
    }
}
