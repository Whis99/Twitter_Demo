package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeDialogFragment extends DialogFragment {
    public static final String TAG = "ComposeDialogFragment";
    public static final int MAX_TWEET = 280;
    EditText composeEditText;
    Button composeBtn;
    ImageView composeImg;
    TextView composeUsrname, composeClose;
    TwitterClient client;
    Context context;


    public ComposeDialogFragment() {
        // Empty constructor is required for DialogFragment

    }



    public static ComposeDialogFragment newInstance(String title) {
        ComposeDialogFragment frag = new ComposeDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.compose_dialog, container);

    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        User thisUser = Parcels.unwrap(bundle.getParcelable("profile"));


        // Get field from view
        client = TwitterApp.getRestClient(getContext());
        composeEditText = view.findViewById(R.id.composeBox);
        composeBtn = view.findViewById(R.id.composeBtn);
        composeImg = view.findViewById(R.id.composeProfile);
        composeUsrname = view.findViewById(R.id.composeUsername);
        composeClose = view.findViewById(R.id.composeClose);

        composeUsrname.setText("@" + thisUser.screenName);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String draft = pref.getString("draft", "");

        if (!draft.isEmpty()){
            composeEditText.setText(draft);
        }

        Glide.with(getContext())
                .load(thisUser.profileImageUrl)
                .transform(new CircleCrop())
                .into(composeImg);

        composeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetContent = composeEditText.getText().toString();
                if(tweetContent.isEmpty()){
                    Toast.makeText(context, "Sorry, your tweet cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }
                if(tweetContent.length() > MAX_TWEET){
                    Toast.makeText(context, "Tweet is too long", Toast.LENGTH_LONG).show();
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

        composeClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open();
            }
        });




        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field
        composeEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }


    public void open(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage("Save draft?");
        alertDialogBuilder.setPositiveButton("Save",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        save();
                        dismiss();
                    }
                });

        alertDialogBuilder.setNegativeButton("Delete",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    public void save(){
        String tweetContent = composeEditText.getText().toString();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("draft", tweetContent);
        edit.commit();
    }

}

