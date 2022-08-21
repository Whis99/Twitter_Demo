package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class TweetActivity extends AppCompatActivity {

    ImageView profile;
    TextView name;
    TextView userName;
    TextView tweet;
    TextView favorite;
    TextView tweetTime;
    ImageView image;
    TextView icLike;
    TextView icRetweet;
    TextView icShare;
    TextView icComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow);
        getSupportActionBar().setLogo(R.drawable.twitter);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("   Tweet");


        profile = findViewById(R.id.twProfile2);
        tweet = findViewById(R.id.tbody2);
        name = findViewById(R.id.twname2);
        userName = findViewById(R.id.twUsername);
        icComment = findViewById(R.id.icComment);
        icShare = findViewById(R.id.icShare);
        icRetweet = findViewById(R.id.icRetweet);
        icLike = findViewById(R.id.icLike);
        tweetTime = findViewById(R.id.twTime);
        image = findViewById(R.id.twImage);

        Tweet tweet2 = Parcels.unwrap(getIntent().getParcelableExtra("Tweet"));

        tweet.setText(tweet2.body);
        name.setText(tweet2.user.name);
        userName.setText("@" + tweet2.user.screenName);
        icRetweet.setText(tweet2.retweet);
        icLike.setText(tweet2.favorite);
        userName.setText(tweet2.user.screenName);
        tweetTime.setText(tweet2.createdAt);


        Glide.with(this)
                .load(tweet2.user.profileImageUrl)
                .transform(new CircleCrop())
                .placeholder(R.drawable.load)
                .into(profile);

        if(!tweet2.entities.mediaUrl.isEmpty()) {
            image.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(tweet2.entities.mediaUrl)
                    .transform(new RoundedCorners(35))
                    .placeholder(R.drawable.loading)
                    .into(image);
        }

        if(tweet2.favoriteBtn){
            Drawable drawable = ContextCompat.getDrawable(TweetActivity.this, R.drawable.ic_heart2);
            drawable.setBounds(0,0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            icLike.setCompoundDrawables(drawable, null, null, null);
        }

        icLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int likes =  Integer.parseInt(tweet2.favorite);
                if(!tweet2.favoriteBtn){
                    Drawable drawable = ContextCompat.getDrawable(TweetActivity.this, R.drawable.ic_heart2);
                    drawable.setBounds(0,0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    icLike.setCompoundDrawables(drawable, null, null, null);

                    likes++;
                    tweet2.favorite = String.valueOf(likes);
                    icLike.setText(tweet2.favorite);
                    tweet2.favoriteBtn = true;
                }else {
                    likes -= 1;
                    tweet2.favorite = String.valueOf(likes);
                    icLike.setText(tweet2.favorite);
                    tweet2.favoriteBtn = false;

                    Drawable drawable = ContextCompat.getDrawable(TweetActivity.this, R.drawable.ic_heart);
                    drawable.setBounds(0,0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    icLike.setCompoundDrawables(drawable, null, null, null);
                }

            }
        });
    }
    public boolean onOptionsItemSelected(MenuItem menuItem){
        Intent intent = new Intent(TweetActivity.this, TimelineActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivityIfNeeded(intent, 0);
        return true;
    }
}