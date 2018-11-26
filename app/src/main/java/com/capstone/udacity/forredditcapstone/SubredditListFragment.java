package com.capstone.udacity.forredditcapstone;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.capstone.udacity.forredditcapstone.model.subreddits.SubListData;
import com.capstone.udacity.forredditcapstone.utils.SubListFragmentAdapter;

import java.util.ArrayList;
import java.util.List;

public class SubredditListFragment extends Fragment {
    private static final String TAG = SubredditListFragment.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<SubListData> subListData = new ArrayList<>();
    private SubListFragmentAdapter subListFragmentAdapter;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SubredditListFragment(){}



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null){
            subListData = getArguments().getParcelableArrayList("listData");
        }
        //setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subreddit_list, container, false);
        recyclerView = view.findViewById(R.id.subreddit_list_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        subListFragmentAdapter = new SubListFragmentAdapter(subListData);
        recyclerView.setAdapter(subListFragmentAdapter);
        if(getActivity() != null)
            recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        return view;
    }
    // This event is triggered soon after onCreateView().
    // onViewCreated() is only called if the view returned from onCreateView() is non-null.
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        subListFragmentAdapter.setOnClick(new SubListFragmentAdapter.OnItemClicked() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d(TAG, " button clicked: " + ((Button) view).getText().toString());
                Log.d(TAG, " subreddit name : " + subListData.get(position).getDisplayNamePrefixed());
                Log.d(TAG, " fullname : " + subListData.get(position).getFullname());
            }

            @Override
            public void onLayoutClick(int position) {

            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(subListData != null) subListData.clear();
    }
}
