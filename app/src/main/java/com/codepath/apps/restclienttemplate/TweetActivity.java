package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class TweetActivity extends AppCompatActivity {

    public static final String TAG = "TweetActivity";
    public static final int MAX_TWEET = 280;
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
    EditText tweetEditText;
    Button tweetBtn;
    Context context;
    TwitterClient client;

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
        tweetBtn = findViewById(R.id.twButton);
        tweetEditText = findViewById(R.id.twBox);

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

        tweetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetContent = tweetEditText.getText().toString();
                if(tweetContent.isEmpty()){
                    Toast.makeText(context, "Sorry, your reply cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }
                if(tweetContent.length() > MAX_TWEET){
                    Toast.makeText(context, "Reply is too long", Toast.LENGTH_LONG).show();
                    return;
                }

                // Make an API call to Twitter to publish the tweet
                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "on Success to publish tweet");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG, "published tweet is : " + tweet.body);
                            Intent intent = new Intent();
                            intent.putExtra("tweet", Parcels.wrap(tweet));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "on Failure to publish tweet", throwable);
                    }
                });
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