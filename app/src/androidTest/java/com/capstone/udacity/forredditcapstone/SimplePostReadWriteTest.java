package com.capstone.udacity.forredditcapstone;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.capstone.udacity.forredditcapstone.database.Post;
import com.capstone.udacity.forredditcapstone.database.RedditDAO;
import com.capstone.udacity.forredditcapstone.database.RedditDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertEquals;
/*
* Room Database write and read test
* @see 'https://developer.android.com/training/data-storage/room/testing-db#java'
* */
@RunWith(AndroidJUnit4.class)
public class SimplePostReadWriteTest {
    private RedditDAO mRedditDao;
    private RedditDatabase mDb;

    @Before
    public void createDb(){
        Context context = InstrumentationRegistry.getTargetContext();
        mDb = Room.inMemoryDatabaseBuilder(context, RedditDatabase.class).build();
        mRedditDao = mDb.redditDAO();
    }

    @After
    public void closeDb(){
        mDb.close();
    }

    @Test
    public void writePostAndReadInTest() throws Exception {
        Post post = new Post("test", "", "", "", "", "", "", "", "", "");
        mRedditDao.insert(post);
        Post postByName = mRedditDao.retrievePostByName("test");
        assertEquals(postByName.getFullname(),"test");
    }
}
