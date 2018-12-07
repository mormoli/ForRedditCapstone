package com.capstone.udacity.forredditcapstone.utils;

import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.card.MaterialCardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.capstone.udacity.forredditcapstone.R;
import com.capstone.udacity.forredditcapstone.model.subreddits.SubListData;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SubListFragmentAdapter extends RecyclerView.Adapter<SubListFragmentAdapter.SubListFragmentHolder>{
    private List<SubListData> subListData;
    private OnItemClicked onItemClicked;

    public SubListFragmentAdapter(List<SubListData> subListData){
        this.subListData = subListData;
    }
    @NonNull
    @Override
    public SubListFragmentHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.subreddit_list_item, viewGroup, false);
        return new SubListFragmentHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final SubListFragmentHolder subListFragmentHolder, int position) {
        String header = subListData.get(position).getDisplayNamePrefixed() + " : " + subListData.get(position).getTitle();
        subListFragmentHolder.headerText.setText(header);
        if(!TextUtils.isEmpty(subListData.get(position).getPublicDescription()))
            subListFragmentHolder.bodyText.setText(subListData.get(position).getPublicDescription());
        else subListFragmentHolder.bodyText.setVisibility(View.GONE);
        String subscribers = numberFormat(subListData.get(position).getSubscribers()) + " Subscribers";
        subListFragmentHolder.subscribersText.setText(subscribers);

        subListFragmentHolder.confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClicked.onItemClick(v, subListFragmentHolder.getAdapterPosition());
            }
        });

        subListFragmentHolder.subsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClicked.onLayoutClick(subListFragmentHolder.getAdapterPosition());
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
        return subListData.size();
    }

    public class SubListFragmentHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.subreddit_list_item_layout)
        MaterialCardView subsCardView;
        @BindView(R.id.search_card_header_text)
        TextView headerText;
        @BindView(R.id.search_card_body_text)
        TextView bodyText;
        @BindView(R.id.search_card_subscribers_text)
        TextView subscribersText;
        @BindView(R.id.subscribe_button)
        MaterialButton confirmButton;

        public SubListFragmentHolder(View view){
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void setSubListData(List<SubListData> subListData){
        this.subListData = subListData;
        notifyDataSetChanged();
    }

    public interface OnItemClicked{
        void onItemClick(View view, int position);
        void onLayoutClick(int position);
    }

    public void setOnClick(OnItemClicked onItemClicked){
        this.onItemClicked = onItemClicked;
    }
}
