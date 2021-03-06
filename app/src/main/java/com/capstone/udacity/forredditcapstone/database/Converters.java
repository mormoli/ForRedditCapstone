package com.capstone.udacity.forredditcapstone.database;

import com.capstone.udacity.forredditcapstone.model.PostData;
import com.capstone.udacity.forredditcapstone.model.favorites.FavoritesData;

public class Converters {
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    //Below methods used to convert retrofit pojo classes in to room database class
    //in order to save data to the database.
    public static Post fromRetrofitPojoToRoom(PostData postData){
        String createdUTC = getTimeAgo(postData.getCreatedUTC());
        String ups = numberFormat(postData.getUps()) + " points";
        String comments = numberFormat(postData.getNumComments()) + " comments";
        String header = postData.getSubredditNamePrefixed() + " . posted bu u/" + postData.getAuthor() + " " + createdUTC;

        return new Post(postData.getFullName(), postData.getSubredditName(), postData.getId(), header, postData.getThumbnail(), postData.getAuthor(),
                postData.getTitle(), postData.getSelftext(), postData.getPermalink(), ups, comments, createdUTC, postData.getImageDetailURL());
    }

    public static Favorite fromFavoritePojoToRoom(FavoritesData favoritesData){
        return new Favorite(favoritesData.getTitle(), favoritesData.getSubreddit(), favoritesData.getDomain(), favoritesData.getSubredditNamePrefixed(),
                favoritesData.getFullname(), favoritesData.getAuthor(), favoritesData.getPermalink(), favoritesData.getCreatedUTC(),
                favoritesData.getBody(), favoritesData.getLinkTitle());
    }

    public static Favorite fromPostDataToFavorites(PostData postData){
        return new Favorite(postData.getTitle(), postData.getSubredditName(), "", postData.getSubredditNamePrefixed(), postData.getFullName(),
                postData.getAuthor(), postData.getPermalink(), postData.getCreatedUTC(), "", "");
    }

    public static String getTimeAgo(long time){
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

    public static String numberFormat(int number){
        String strNumber;
        if(Math.abs(number / 1000000) > 1) strNumber = (number / 1000000) + "m";
        else if(Math.abs(number / 1000) > 1) strNumber = (number / 1000) + "k";
        else strNumber = "" + number;
        return strNumber;
    }
}
