package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import java.lang.invoke.ConstantCallSite;
import java.util.List;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {
    Context context;
    List<Tweet> tweets;

    //Pass in the context and list of tweets
    public TweetsAdapter(Context context, List<Tweet> tweets){
        this.context = context;
        this.tweets = tweets;
    }

    // For each row, inflate the layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    // Bind values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data at position
        Tweet tweet = tweets.get(position);
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public  void clear(){
        tweets.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Tweet> tweetList){
        tweets.addAll(tweetList);
        notifyDataSetChanged();
    }

    // Define a viewholder
    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView tProfile;
        TextView tBody;
        TextView tName;
        TextView tUsername;
        TextView tTime;
        ImageView tweetImage;
        TextView twtHeart;
        TextView twtRetweet;
        TextView twtShare;
        TextView twtComment;
        RelativeLayout container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tName = itemView.findViewById(R.id.tweetName);
            tProfile = itemView.findViewById(R.id.tweetProfile);
            tBody = itemView.findViewById(R.id.tweetBody);
            container = itemView.findViewById(R.id.container);
            tTime = itemView.findViewById(R.id.tweetTime);
            tUsername = itemView.findViewById(R.id.tweetUsername);
            tweetImage = itemView.findViewById(R.id.tweetImage);
            twtRetweet = itemView.findViewById(R.id.twtRetweet);
            twtHeart = itemView.findViewById(R.id.twtHeart);
            twtShare = itemView.findViewById(R.id.twtShare);
            twtComment = itemView.findViewById(R.id.twtComment);
        }

        public void bind(Tweet tweet) {
            tBody.setText(tweet.body);
            tName.setText(tweet.user.name);
            tUsername.setText("@" + tweet.user.screenName);
            twtRetweet.setText(tweet.retweet);
            twtHeart.setText(tweet.favorite);
            tTime.setText(tweet.createdAt);

            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, TweetActivity.class);
                    intent.putExtra("Tweet", Parcels.wrap(tweet));
                    context.startActivity(intent);
                }
            });

            Glide.with(context)
                    .load(tweet.user.profileImageUrl)
                    .transform(new CircleCrop())
                    .placeholder(R.drawable.load)
                    .into(tProfile);

            if(!tweet.entities.mediaUrl.isEmpty()) {
                tweetImage.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(tweet.entities.mediaUrl)
                        .transform(new RoundedCorners(35))
                        .placeholder(R.drawable.loading)
                        .into(tweetImage);
            }

            twtHeart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int likes =  Integer.parseInt(tweet.favorite);
                    if(!tweet.favoriteBtn){
                        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_heart2);
                        drawable.setBounds(0,0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        twtHeart.setCompoundDrawables(drawable, null, null, null);

                        likes += 1;
                        tweet.favorite = String.valueOf(likes);
                        twtHeart.setText(tweet.favorite);
                        tweet.favoriteBtn = true;
                    }else {
                        likes -= 1;
                        tweet.favorite = String.valueOf(likes);
                        twtHeart.setText(tweet.favorite);
                        tweet.favoriteBtn = false;

                        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_heart);
                        drawable.setBounds(0,0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        twtHeart.setCompoundDrawables(drawable, null, null, null);
                    }

                }
            });

            if(tweet.favoriteBtn){
                Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_heart2);
                drawable.setBounds(0,0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                twtHeart.setCompoundDrawables(drawable, null, null, null);
            }


            twtComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("tweets", Parcels.wrap(tweet));
                    bundle.putParcelable("profile", Parcels.wrap(TimelineActivity.user));

                    FragmentManager fm = ((FragmentActivity)context).getSupportFragmentManager();
                    ReplyDialogFragment editNameDialogFragment = ReplyDialogFragment.newInstance("Some Title");
                    editNameDialogFragment.setArguments(bundle);

                    editNameDialogFragment.show(fm, "fragment_edit_name");
                }
            });
        }
    }
}
