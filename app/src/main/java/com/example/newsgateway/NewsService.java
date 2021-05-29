package com.example.newsgateway;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;

public class NewsService extends Service{

    private boolean running = true;
    private final ArrayList<News> newsList = new ArrayList<>();
    private int count = 1;
    private String tempnews = "";
    private String temp;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        String temp;
        if (intent.hasExtra("story")) {
            temp = (String)intent.getSerializableExtra("story");
        }
        //Creating new thread for my service
        //ALWAYS write your long running tasks
        // in a separate thread, to avoid an ANR

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    sendNews(temp);
                }
                sendMessage("Service Thread Stopped");
            }
        }).start();

        return Service.START_NOT_STICKY;
    }

    private void sendNews(String newsToSend) {
        Intent intent = new Intent();
        intent.setAction(MainActivity.NEWS_FROM_SERVICE);
        intent.putExtra(MainActivity.NEWS_DATA, newsToSend);
        sendBroadcast(intent);
    }

    private void sendMessage(String msg) {
        Intent intent = new Intent();
        intent.setAction(MainActivity.MESSAGE_FROM_SERVICE);
        intent.putExtra(MainActivity.MESSAGE_DATA, msg);
        sendBroadcast(intent);
    }
    @Override
    public void onDestroy() {
        sendMessage("Service Destroyed");
        running = false;
        super.onDestroy();
    }


}
