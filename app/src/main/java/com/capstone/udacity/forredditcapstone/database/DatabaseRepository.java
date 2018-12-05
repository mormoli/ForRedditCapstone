package com.capstone.udacity.forredditcapstone.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class DatabaseRepository {
    private RedditDAO mRedditDao;
    private LiveData<List<Comment>> mAllComments;
    private LiveData<List<Post>> mAllPosts;
    private LiveData<List<Favorite>> mAllFavorites;

    DatabaseRepository(Application application){
        RedditDatabase db = RedditDatabase.getDatabase(application);
        mRedditDao = db.redditDAO();
        mAllComments = mRedditDao.getAllComments();
        mAllPosts = mRedditDao.getAllPosts();
        mAllFavorites = mRedditDao.getAllFavorites();
    }

    LiveData<List<Comment>> getAllComments(){
        return mAllComments;
    }

    LiveData<List<Post>> getAllPosts(){
        return mAllPosts;
    }

    LiveData<List<Favorite>> getAllFavorites(){ return mAllFavorites; }

    public void insert(Comment comment){
        new insertCommentAsyncTask(mRedditDao).execute(comment);
    }

    public void insert(Post post){
        new insertPostAsyncTask(mRedditDao).execute(post);
    }

    public void insert(Favorite favorite){ new insertFavoriteAsyncTask(mRedditDao).execute(favorite); }

    @SuppressWarnings("unchecked")
    public void insertPosts(List<Post> posts){
        new insertAllPosts(mRedditDao).execute(posts);
    }

    @SuppressWarnings("unchecked")
    public void insertFavorites(List<Favorite> favorites){ new insertAllFavorites(mRedditDao).execute(favorites); }

    public void deleteAllComments(){
        new deleteAllCommentsAsyncTask(mRedditDao).execute();
    }

    public void deleteAllPosts(){
        new deleteAllPostsAsyncTask(mRedditDao).execute();
    }

    public void deleteAllFavorites() { new deleteAllFavoritesAsyncTask(mRedditDao).execute(); }

    public Favorite retriveFavoriteByName(String name) {
        return mRedditDao.retriveFavoriteByName(name);
    }

    public void deletePostByName(String name){
        new deletePostByName(mRedditDao).execute(name);
    }

    public void deleteFavoriteByName(String name){ new deleteFavoriteByName(mRedditDao).execute(name); }

    private static class insertAllFavorites extends AsyncTask<List<Favorite>, Void, Void>{
        private RedditDAO mAsyncTaskDao;

        insertAllFavorites(RedditDAO dao){ mAsyncTaskDao = dao; }

        @SafeVarargs
        @Override
        protected final Void doInBackground(List<Favorite>... lists) {
            mAsyncTaskDao.insertFavorites(lists[0]);
            return null;
        }
    }

    private static class insertAllPosts extends AsyncTask<List<Post>, Void, Void>{
        private RedditDAO mAsyncTaskDao;

        insertAllPosts(RedditDAO dao){
            mAsyncTaskDao = dao;
        }

        @SafeVarargs
        @Override
        protected final Void doInBackground(List<Post>... lists) {
            mAsyncTaskDao.insertPosts(lists[0]);
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

    private static class insertFavoriteAsyncTask extends AsyncTask<Favorite, Void, Void>{
        private RedditDAO mAsyncTaskDao;

        insertFavoriteAsyncTask(RedditDAO dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Favorite... favorites) {
            mAsyncTaskDao.insert(favorites[0]);
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

    private static class deleteAllFavoritesAsyncTask extends AsyncTask<Void, Void, Void>{
        private RedditDAO mAsyncTaskDao;

        deleteAllFavoritesAsyncTask(RedditDAO redditDAO){
            mAsyncTaskDao = redditDAO;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncTaskDao.deleteAllFavorites();
            return null;
        }
    }

    /*private static class retriveFavoriteByNameTask extends AsyncTask<String, Void, Favorite>{
        private RedditDAO mAsyncTaskDao;

        retriveFavoriteByNameTask(RedditDAO redditDAO){
            mAsyncTaskDao = redditDAO;
        }

        @Override
        protected Favorite doInBackground(String... strings) {
            Favorite favorite = mAsyncTaskDao.retriveFavoriteByName(strings[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Favorite favorite) {
            super.onPostExecute(favorite);
        }
    }*/

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

    private static class deleteFavoriteByName extends AsyncTask<String , Void, Void>{
        private RedditDAO mAsyncTaskDao;

        deleteFavoriteByName(RedditDAO redditDAO){
            mAsyncTaskDao = redditDAO;
        }

        @Override
        protected Void doInBackground(String... strings) {
            mAsyncTaskDao.deleteFavoriteByName(strings[0]);
            return null;
        }
    }
}
