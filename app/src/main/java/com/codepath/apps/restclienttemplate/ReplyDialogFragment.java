package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ReplyDialogFragment extends DialogFragment {

    public static final String TAG = "ReplyDialogFragment";
    public static final int MAX_TWEET = 280;
    EditText replyEditText;
    Button replyBtn;
    ImageView replyImg;
    TextView replyUsrname;
    TextView replyLabel;
    TwitterClient client;
    Context context;


    public ReplyDialogFragment() {
        // Empty constructor is required for DialogFragment

    }



    public static ReplyDialogFragment newInstance(String title) {
        ReplyDialogFragment frag = new ReplyDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reply, container);

    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        Tweet tweet = Parcels.unwrap(bundle.getParcelable("tweets"));
        User thisUser = Parcels.unwrap(bundle.getParcelable("profile"));

        // Get field from view
        client = TwitterApp.getRestClient(getContext());
        replyEditText = (EditText) view.findViewById(R.id.replyBox);
        replyBtn = view.findViewById(R.id.replyBtn);
        replyImg = view.findViewById(R.id.replyProfile);
        replyUsrname = view.findViewById(R.id.replyUsername);
        replyLabel = view.findViewById(R.id.replyLbl);

        replyEditText.setHint("Reply to " + tweet.user.screenName);
        replyUsrname.setText("@" + thisUser.screenName);
        replyLabel.setText("In reply to @" + tweet.user.screenName);

        Glide.with(getContext())
                .load(thisUser.profileImageUrl)
                .transform(new RoundedCorners(70))
                .into(replyImg);

        replyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetContent = replyEditText.getText().toString();
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
                dismiss();
            }
        });


        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field
        replyEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }


}
