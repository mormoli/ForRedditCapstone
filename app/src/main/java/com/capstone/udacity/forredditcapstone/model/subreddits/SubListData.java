package com.capstone.udacity.forredditcapstone.model.subreddits;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class SubListData implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public SubListData createFromParcel(Parcel in){
            return new SubListData(in);
        }

        public SubListData[] newArray(int size){
            return new SubListData[size];
        }
    };
    @SerializedName("community_icon")
    private String communityIcon;
    @SerializedName("description")
    private String description;
    @SerializedName("display_name")
    private String displayName;
    @SerializedName("header_img")
    private String headerImage;
    @SerializedName("display_name_prefixed")
    private String displayNamePrefixed;
    @SerializedName("subscribers")
    private int subscribers;
    @SerializedName("name")
    private String fullname;
    @SerializedName("title")
    private String title;
    @SerializedName("created_utc")
    private int createdUTC;
    @SerializedName("public_description")
    private String publicDescription;

    public String getCommunityIcon() {
        return communityIcon;
    }

    public void setCommunityIcon(String communityIcon) {
        this.communityIcon = communityIcon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getHeaderImage() {
        return headerImage;
    }

    public void setHeaderImage(String headerImage) {
        this.headerImage = headerImage;
    }

    public String getDisplayNamePrefixed() {
        return displayNamePrefixed;
    }

    public void setDisplayNamePrefixed(String displayNamePrefixed) {
        this.displayNamePrefixed = displayNamePrefixed;
    }

    public int getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(int subscribers) {
        this.subscribers = subscribers;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCreatedUTC() {
        return createdUTC;
    }

    public void setCreatedUTC(int createdUTC) {
        this.createdUTC = createdUTC;
    }

    public String getPublicDescription() {
        return publicDescription;
    }

    public void setPublicDescription(String publicDescription) {
        this.publicDescription = publicDescription;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private SubListData(Parcel in){
        this.communityIcon = in.readString();
        this.description = in.readString();
        this.displayName = in.readString();
        this.headerImage = in.readString();
        this.displayNamePrefixed = in.readString();
        this.subscribers = in.readInt();
        this.fullname = in.readString();
        this.title = in.readString();
        this.createdUTC = in.readInt();
        this.publicDescription = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.communityIcon);
        dest.writeString(this.description);
        dest.writeString(this.displayName);
        dest.writeString(this.headerImage);
        dest.writeString(this.displayNamePrefixed);
        dest.writeInt(this.subscribers);
        dest.writeString(this.fullname);
        dest.writeString(this.title);
        dest.writeInt(this.createdUTC);
        dest.writeString(this.publicDescription);
    }
}
