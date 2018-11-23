package com.capstone.udacity.forredditcapstone.model.search;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class SearchData implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public SearchData createFromParcel(Parcel in){
            return new SearchData(in);
        }

        public SearchData[] newArray(int size){
            return new SearchData[size];
        }
    };
    @SerializedName("banner_img")
    private String bannerImage;
    @SerializedName("public_description")
    private String publicDescription;
    @SerializedName("community_icon")
    private String communityIcon;
    @SerializedName("display_name_prefixed")
    private String displayNamePrefixed;
    @SerializedName("display_name")
    private String displayName;
    @SerializedName("title")
    private String title;
    @SerializedName("id")
    private String id;
    @SerializedName("description")
    private String description;
    @SerializedName("submit_text")
    private String submitText;
    @SerializedName("subscribers")
    private int subscribers;
    @SerializedName("created_utc")
    private int createdUTC;
    @SerializedName("user_is_subscriber")
    private boolean userIsSubscriber;

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public String getPublicDescription() {
        return publicDescription;
    }

    public void setPublicDescription(String publicDescription) {
        this.publicDescription = publicDescription;
    }

    public String getCommunityIcon() {
        return communityIcon;
    }

    public void setCommunityIcon(String communityIcon) {
        this.communityIcon = communityIcon;
    }

    public String getDisplayNamePrefixed() {
        return displayNamePrefixed;
    }

    public void setDisplayNamePrefixed(String displayNamePrefixed) {
        this.displayNamePrefixed = displayNamePrefixed;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubmitText() {
        return submitText;
    }

    public void setSubmitText(String submitText) {
        this.submitText = submitText;
    }

    public int getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(int subscribers) {
        this.subscribers = subscribers;
    }

    public int getCreatedUTC() {
        return createdUTC;
    }

    public void setCreatedUTC(int createdUTC) {
        this.createdUTC = createdUTC;
    }

    public boolean isUserIsSubscriber() {
        return userIsSubscriber;
    }

    public void setUserIsSubscriber(boolean userIsSubscriber) {
        this.userIsSubscriber = userIsSubscriber;
    }

    private SearchData(Parcel in){
        this.bannerImage = in.readString();
        this.publicDescription = in.readString();
        this.communityIcon = in.readString();
        this.displayNamePrefixed = in.readString();
        this.displayName = in.readString();
        this.title = in.readString();
        this.id = in.readString();
        this.description = in.readString();
        this.submitText = in.readString();
        this.subscribers = in.readInt();
        this.createdUTC = in.readInt();
        this.userIsSubscriber = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.bannerImage);
        dest.writeString(this.publicDescription);
        dest.writeString(this.communityIcon);
        dest.writeString(this.displayNamePrefixed);
        dest.writeString(this.displayName);
        dest.writeString(this.title);
        dest.writeString(this.id);
        dest.writeString(this.description);
        dest.writeString(this.submitText);
        dest.writeInt(this.subscribers);
        dest.writeInt(this.createdUTC);
        dest.writeByte((byte) (this.userIsSubscriber ? 1 : 0));
    }

    @Override
    public String toString() {
        return "SearchData{" +
                "publicDescription='" + publicDescription + '\'' +
                ", displayNamePrefixed='" + displayNamePrefixed + '\'' +
                ", title='" + title + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
