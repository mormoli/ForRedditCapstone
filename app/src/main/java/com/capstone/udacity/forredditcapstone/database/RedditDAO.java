package com.capstone.udacity.forredditcapstone.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;
import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

import java.util.List;
// https://codelabs.developers.google.com/codelabs/android-room-with-a-view/#6
@Dao
public interface RedditDAO {
    @Insert(onConflict = IGNORE)
    void insert(Comment comment);
    @Insert(onConflict = IGNORE)
    void insert(Post post);
    @Insert(onConflict = REPLACE)
    void insert(Favorite favorite);
    @Insert(onConflict = REPLACE)
    void insertPosts(List<Post> posts);
    @Insert(onConflict = REPLACE)
    void insertFavorites(List<Favorite> favorites);
    @Query("DELETE FROM comment_table")
    void deleteAllComments();
    @Query("DELETE FROM post_table")
    void deleteAllPosts();
    @Query("DELETE FROM favorites_table")
    void deleteAllFavorites();
    @Query("SELECT * FROM comment_table WHERE name == :name")
    Comment retrieveCommentByName(String name);
    @Query("SELECT * from comment_table ORDER BY name ASC")
    LiveData<List<Comment>> getAllComments();
    @Query("SELECT * FROM post_table WHERE name == :name")
    Post retrievePostByName(String name);
    @Query("SELECT * FROM favorites_table WHERE name = :name")
    Favorite retriveFavoriteByName(String name);
    @Query("SELECT * from post_table")
    LiveData<List<Post>> getAllPosts();
    @Query("SELECT * FROM favorites_table")
    LiveData<List<Favorite>> getAllFavorites();
    @Query("DELETE FROM post_table WHERE name == :name")
    void deletePostByName(final String name);
    @Query("DELETE FROM favorites_table WHERE name == :name")
    void deleteFavoriteByName(final String name);
}
