package com.capstone.udacity.forredditcapstone.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class DatabaseRepository {
    private RedditDAO mRedditDao;
    private LiveData<List<Comment>> mAllComments;
    private LiveData<List<Post>> mAllPosts;

    DatabaseRepository(Application application){
        RedditDatabase db = RedditDatabase.getDatabase(application);
        mRedditDao = db.redditDAO();
        mAllComments = mRedditDao.getAllComments();
        mAllPosts = mRedditDao.getAllPosts();
    }

    LiveData<List<Comment>> getAllComments(){
        return mAllComments;
    }

    LiveData<List<Post>> getAllPosts(){
        return mAllPosts;
    }

    public void insert(Comment comment){
        new insertCommentAsyncTask(mRedditDao).execute(comment);
    }

    public void insert(Post post){
        new insertPostAsyncTask(mRedditDao).execute(post);
    }

    public void insertPosts(Post... posts){
        new insertAllPosts(mRedditDao).execute(posts);
    }

    public void deleteAllComments(){
        new deleteAllCommentsAsyncTask(mRedditDao).execute();
    }

    public void deleteAllPosts(){
        new deleteAllPostsAsyncTask(mRedditDao).execute();
    }

    public void deletePostByName(String name){
        new deletePostByName(mRedditDao).execute(name);
    }

    private static class insertAllPosts extends AsyncTask<Post, Void, Void>{
        private RedditDAO mAsyncTaskDao;

        insertAllPosts(RedditDAO dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Post... posts) {
            mAsyncTaskDao.insertPosts(posts[0]);
            return null;
        }
    }

    private static class insertCommentAsyncTask extends AsyncTask<Comment, Void, Void> {
        private RedditDAO mAsyncTaskDao;

        insertCommentAsyncTask(RedditDAO dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Comment... comments) {
            mAsyncTaskDao.insert(comments[0]);
            return null;
        }
    }

    private static class insertPostAsyncTask extends AsyncTask<Post, Void, Void> {
        private RedditDAO mAsyncTaskDao;

        insertPostAsyncTask(RedditDAO dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Post... posts) {
            mAsyncTaskDao.insert(posts[0]);
            return null;
        }
    }

    private static class deleteAllCommentsAsyncTask extends AsyncTask<Void, Void, Void>{
        private RedditDAO mAsyncTaskDao;

        deleteAllCommentsAsyncTask(RedditDAO redditDAO){
            mAsyncTaskDao = redditDAO;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncTaskDao.deleteAllComments();
            return null;
        }
    }

    private static class deleteAllPostsAsyncTask extends AsyncTask<Void, Void, Void>{
        private RedditDAO mAsyncTaskDao;

        deleteAllPostsAsyncTask(RedditDAO redditDAO){
            mAsyncTaskDao = redditDAO;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncTaskDao.deleteAllPosts();
            return null;
        }
    }

    private static class deletePostByName extends AsyncTask<String, Void, Void>{
        private RedditDAO mAsyncTaskDao;

        deletePostByName(RedditDAO redditDAO){
            mAsyncTaskDao = redditDAO;
        }

        @Override
        protected Void doInBackground(String... args) {
            mAsyncTaskDao.deletePostByName(args[0]);
            return null;
        }
    }
}
