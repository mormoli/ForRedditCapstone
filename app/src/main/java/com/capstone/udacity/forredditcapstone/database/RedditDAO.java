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
    void insertPosts(Post... posts);
    @Query("DELETE FROM comment_table")
    void deleteAllComments();
    @Query("DELETE FROM post_table")
    void deleteAllPosts();
    @Query("SELECT * FROM comment_table WHERE name == :name")
    Comment retrieveCommentByName(String name);
    @Query("SELECT * from comment_table ORDER BY name ASC")
    LiveData<List<Comment>> getAllComments();
    @Query("SELECT * FROM post_table WHERE name == :name")
    Post retrievePostByName(String name);
    @Query("SELECT * from post_table ORDER BY name ASC")
    LiveData<List<Post>> getAllPosts();
    @Query("DELETE FROM post_table WHERE name == :name")
    void deletePostByName(final String name);
}
