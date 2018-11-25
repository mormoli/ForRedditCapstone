package com.capstone.udacity.forredditcapstone;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.capstone.udacity.forredditcapstone.model.search.SearchData;
import com.capstone.udacity.forredditcapstone.utils.SearchFragmentAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchListFragment extends Fragment {
    private static final String TAG = SearchListFragment.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<SearchData> searchData = new ArrayList<>();
    private SearchFragmentAdapter searchFragmentAdapter;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SearchListFragment(){}

    public static SearchListFragment newInstance(){
        return new SearchListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null){
            searchData = getArguments().getParcelableArrayList("searchData");
        }
        //setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_list, container, false);
        recyclerView = view.findViewById(R.id.search_list_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        searchFragmentAdapter = new SearchFragmentAdapter(searchData);
        recyclerView.setAdapter(searchFragmentAdapter);
        return view;
    }
    // This event is triggered soon after onCreateView().
    // onViewCreated() is only called if the view returned from onCreateView() is non-null.
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchFragmentAdapter.setOnClick(new SearchFragmentAdapter.OnItemClicked() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d(TAG, " button clicked: " + ((Button) view).getText().toString());
                Log.d(TAG, " subreddit name : " + searchData.get(position).getDisplayNamePrefixed());
                //searchFragmentAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(searchData != null) searchData.clear();
    }
}
