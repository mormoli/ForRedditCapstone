package com.capstone.udacity.forredditcapstone.database;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class DataViewModel extends AndroidViewModel {
    private DatabaseRepository mRepository;
    private LiveData<List<Comment>> mAllComments;
    private LiveData<List<Post>> mAllPosts;

    public DataViewModel(@NonNull Application application) {
        super(application);
        mRepository = new DatabaseRepository(application);
        mAllComments = mRepository.getAllComments();
        mAllPosts = mRepository.getAllPosts();
    }

    public LiveData<List<Comment>> getAllComments() {
        return mAllComments;
    }

    public LiveData<List<Post>> getAllPosts() {
        return mAllPosts;
    }

    public void insert(Comment comment){
        mRepository.insert(comment);
    }

    public void insert(Post post){
        mRepository.insert(post);
    }
}
