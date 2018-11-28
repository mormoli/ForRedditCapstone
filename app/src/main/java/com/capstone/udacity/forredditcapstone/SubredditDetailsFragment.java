package com.capstone.udacity.forredditcapstone;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.capstone.udacity.forredditcapstone.model.PostData;
import com.capstone.udacity.forredditcapstone.utils.SubredditDetailsAdapter;

import java.util.ArrayList;
import java.util.List;

public class SubredditDetailsFragment extends Fragment {
    private static final String TAG = SubredditDetailsFragment.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<PostData> postData;
    private Parcelable recyclerViewState;
    /*
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SubredditDetailsFragment(){}

    public static SubredditDetailsFragment newInstance(List<PostData> postData){
        SubredditDetailsFragment fragment = new SubredditDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("postData", (ArrayList<? extends Parcelable>) postData);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null){
            postData = getArguments().getParcelableArrayList("postData");
        }
        //setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subreddit_details, container, false);
        recyclerView = view.findViewById(R.id.sub_detail_list_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SubredditDetailsAdapter subredditDetailsAdapter = new SubredditDetailsAdapter(postData, new SubredditDetailsAdapter.ButtonsListener() {
            @Override
            public void onHideButtonClick(View view, int position) {

            }

            @Override
            public void onSaveButtonClick(View view, int position) {

            }

            @Override
            public void onLayoutClicked(int position) {
                //open web view
                String urlToOpen = "https://www.reddit.com" + postData.get(position).getPermalink();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(urlToOpen));
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(subredditDetailsAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null){
            postData = bundle.getParcelableArrayList("listData");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("postData", (ArrayList<? extends Parcelable>) postData);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(postData != null) postData.clear();
    }
}
