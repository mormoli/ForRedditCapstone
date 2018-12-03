package com.capstone.udacity.forredditcapstone.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

@Database(entities = {Comment.class, Post.class}, version = 1, exportSchema = false)
public abstract class RedditDatabase extends RoomDatabase {
    public abstract RedditDAO redditDAO();
    private static volatile RedditDatabase INSTANCE;

    public static RedditDatabase getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (RedditDatabase.class){
                if (INSTANCE == null){
                    //create database
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            RedditDatabase.class, "favorite_database").build();
                }
            }
        }
        return INSTANCE;
    }
}
