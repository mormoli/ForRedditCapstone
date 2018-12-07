package com.capstone.udacity.forredditcapstone.utils;

import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.capstone.udacity.forredditcapstone.R;
import com.capstone.udacity.forredditcapstone.database.Post;
import com.capstone.udacity.forredditcapstone.model.PostData;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomePageAdapter extends RecyclerView.Adapter<HomePageAdapter.HomePageHolder> {
    private List<PostData> childList;
    private List<Post> posts;
    private ButtonsListener buttonsListener;
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    public HomePageAdapter(List<PostData> childList, ButtonsListener buttonsListener){
        this.childList = childList;
        this.buttonsListener = buttonsListener;
    }

    public class HomePageHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.card_view)
        MaterialCardView materialCardView;
        @BindView(R.id.card_header_text)
        TextView headerText;
        @BindView(R.id.post_title_text)
        TextView postTitle;
        @BindView(R.id.thumbnail_image_view)
        ImageView postImage;
        //@BindView(R.id.card_subtitle_text)
        //TextView postSubtitle;
        @BindView(R.id.points_text_view)
        TextView points;
        @BindView(R.id.comments_text_view)
        TextView comments;
        @BindView(R.id.hide_button)
        ImageButton hideButton;
        @BindView(R.id.save_button)
        ImageButton saveButton;
        public HomePageHolder(View view){
            super(view);
            //setIsRecyclable(false);
            ButterKnife.bind(this, view);
        }

        /*public void insertPostsIntoDB(Post post){
            RedditDatabase.getDatabase(itemView.getContext()).redditDAO().insert(post);
        }*/
    }
    //@see 'https://stackoverflow.com/questions/13018550/time-since-ago-library-for-android-java'
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

    @NonNull
    @Override
    public HomePageHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_card, viewGroup, false);
        return new HomePageHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final HomePageHolder holder, int position) {
        final String ups, comments;
        if(posts == null) {
            //Header of the card
            String header = childList.get(position).getSubredditNamePrefixed() + " . posted bu u/" + childList.get(position).getAuthor() + " " + getTimeAgo(childList.get(position).getCreatedUTC());
            holder.headerText.setText(header);

            if (!TextUtils.isEmpty(childList.get(position).getThumbnail())) {
                Picasso.get()
                        .load(childList.get(position).getThumbnail())
                        .resizeDimen(R.dimen.thumb_image_width, R.dimen.thumb_image_height)
                        .into(holder.postImage);
            } else {
                holder.postImage.setVisibility(View.GONE);
            }
            //post title
            if (!TextUtils.isEmpty(childList.get(position).getTitle())) {
                holder.postTitle.setText(Html.fromHtml(childList.get(position).getTitle()));
            } else holder.postTitle.setVisibility(View.GONE);
            //Footer items
            //format numbers for ups and comments
            //@see 'https://stackoverflow.com/questions/4753251/how-to-go-about-formatting-1200-to-1-2k-in-java'
            ups = numberFormat(childList.get(position).getUps()) + " points";
            holder.points.setText(ups);
            comments = numberFormat(childList.get(position).getNumComments()) + " comments";
            holder.comments.setText(comments);
        } else { //populate ui from database
            holder.headerText.setText(posts.get(position).getHeader());
            if(!TextUtils.isEmpty(posts.get(position).getThumbnail())){
                Picasso.get()
                        .load(posts.get(position).getThumbnail())
                        .resizeDimen(R.dimen.thumb_image_width, R.dimen.thumb_image_height)
                        .into(holder.postImage);
            } else holder.postImage.setVisibility(View.GONE);
            if(!TextUtils.isEmpty(posts.get(position).getTitle())) holder.postTitle.setText(Html.fromHtml(posts.get(position).getTitle()));
            else holder.postTitle.setVisibility(View.GONE);
            holder.points.setText(posts.get(position).getUps());
            ups = posts.get(position).getUps();
            holder.comments.setText(posts.get(position).getComments());
            comments = posts.get(position).getComments();
        }
        //BUTTONS setting click listeners
        holder.hideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonsListener.onHideButtonClick(v, holder.getAdapterPosition());
            }
        });

        holder.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonsListener.onSaveButtonClick(v, holder.getAdapterPosition());
            }
        });
        //Setting listener to the view
        holder.materialCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostData postData = new PostData();
                postData.setSubredditNamePrefixed(holder.headerText.getText().toString());
                if(childList != null && childList.size() > 0) {
                    if (!TextUtils.isEmpty(childList.get(holder.getAdapterPosition()).getThumbnail()))
                        postData.setThumbnail(childList.get(holder.getAdapterPosition()).getThumbnail());
                    postData.setTitle(childList.get(holder.getAdapterPosition()).getTitle());
                    if (!TextUtils.isEmpty(childList.get(holder.getAdapterPosition()).getSelftext()))
                        postData.setSelftext(childList.get(holder.getAdapterPosition()).getSelftext());
                    if (!TextUtils.isEmpty(childList.get(holder.getAdapterPosition()).getImageDetailURL())) {
                        String ext = MimeTypeMap.getFileExtensionFromUrl(childList.get(holder.getAdapterPosition()).getImageDetailURL());
                        if (!TextUtils.isEmpty(ext) && (ext.equals("jpg") || ext.equals("png")))
                            postData.setImageDetailURL(childList.get(holder.getAdapterPosition()).getImageDetailURL());
                    }
                } else {
                    if (!TextUtils.isEmpty(posts.get(holder.getAdapterPosition()).getThumbnail()))
                        postData.setThumbnail(posts.get(holder.getAdapterPosition()).getThumbnail());
                    postData.setTitle(posts.get(holder.getAdapterPosition()).getTitle());
                    if (!TextUtils.isEmpty(posts.get(holder.getAdapterPosition()).getSelftext()))
                        postData.setSelftext(posts.get(holder.getAdapterPosition()).getSelftext());
                    if (!TextUtils.isEmpty(posts.get(holder.getAdapterPosition()).getImageDetailURL())) {
                        String ext = MimeTypeMap.getFileExtensionFromUrl(posts.get(holder.getAdapterPosition()).getImageDetailURL());
                        if (!TextUtils.isEmpty(ext) && (ext.equals("jpg") || ext.equals("png")))
                            postData.setImageDetailURL(posts.get(holder.getAdapterPosition()).getImageDetailURL());
                    }
                }
                buttonsListener.onLayoutClicked(holder.getAdapterPosition(), postData, ups, comments);
            }
        });
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
        notifyDataSetChanged();
        //notifyItemRangeChanged(0, posts.size());
    }

    public String numberFormat(int number){
        String strNumber;
        if(Math.abs(number / 1000000) > 1) strNumber = (number / 1000000) + "m";
        else if(Math.abs(number / 1000) > 1) strNumber = (number / 1000) + "k";
        else strNumber = "" + number;
        return strNumber;
    }

    @Override
    public int getItemCount() {
        if(posts != null && posts.size() > 0) return posts.size();
        else return childList.size();
    }

    public interface ButtonsListener{
        void onHideButtonClick(View view, int position);
        void onSaveButtonClick(View view, int position);
        void onLayoutClicked(int position, PostData postData, String ups, String comments);
    }
}
