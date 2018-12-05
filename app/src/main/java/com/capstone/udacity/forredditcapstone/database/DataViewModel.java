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
    private LiveData<List<Favorite>> mAllFavorites;

    public DataViewModel(@NonNull Application application) {
        super(application);
        mRepository = new DatabaseRepository(application);
        mAllComments = mRepository.getAllComments();
        mAllPosts = mRepository.getAllPosts();
        mAllFavorites = mRepository.getAllFavorites();
    }

    public LiveData<List<Comment>> getAllComments() {
        return mAllComments;
    }

    public LiveData<List<Post>> getAllPosts() {
        return mAllPosts;
    }

    public LiveData<List<Favorite>> getAllFavorites() { return mAllFavorites; }

    public void deleteAllComments(){
        mRepository.deleteAllComments();
    }

    public void deleteAllPosts(){
        mRepository.deleteAllPosts();
    }

    public void deleteAllFavorites() { mRepository.deleteAllFavorites(); }

    public void insert(Comment comment){
        mRepository.insert(comment);
    }

    public void insert(Post post){
        mRepository.insert(post);
    }

    public void insert(Favorite favorite) { mRepository.insert(favorite); }

    public void insertAll(List<Post> posts){
        mRepository.insertPosts(posts);
    }

    public void insertAllFavorites(List<Favorite> favorites) { mRepository.insertFavorites(favorites); }

    public Favorite retrieveFavoriteByName(String name) {
        return mRepository.retriveFavoriteByName(name);
    }

    public void deletePostByName(String name){
        mRepository.deletePostByName(name);
    }

    public void deleteFavoriteByName(String name) { mRepository.deleteFavoriteByName(name); }
}
