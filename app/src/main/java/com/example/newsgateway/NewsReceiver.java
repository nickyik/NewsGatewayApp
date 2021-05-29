package com.example.newsgateway;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class NewsReceiver extends BroadcastReceiver {
    private static final String TAG = "tag";
    private MainActivity mainActivity;
    public NewsReceiver(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null)
            return;
        switch (action) {
            case MainActivity.NEWS_FROM_SERVICE:
                String temp = null;
                if (intent.hasExtra(MainActivity.NEWS_DATA))
                    temp = (String) intent.getSerializableExtra(MainActivity.NEWS_DATA);
                StoriesRunnable storiesRunnable = new StoriesRunnable(this, temp);
                new Thread(storiesRunnable).start();
                break;

            case MainActivity.MESSAGE_FROM_SERVICE:
                String data = "";
                if (intent.hasExtra(MainActivity.MESSAGE_DATA))
                    data = intent.getStringExtra(MainActivity.MESSAGE_DATA);
                break;

            default:
                Log.d(TAG, "onReceive: Unknown broadcast received");
        }
    }
    public void returnResults(final ArrayList<Stories> results){
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainActivity.sendBackStories(results);
            }
        });
    }
}
