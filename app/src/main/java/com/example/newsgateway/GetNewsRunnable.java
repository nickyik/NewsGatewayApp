package com.example.newsgateway;


import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class GetNewsRunnable implements Runnable {
    private static final String TAG = "GetNewsSources";
    private MainActivity mainActivity;
    private String input;

    private static final String DATA_URL = "https://newsapi.org/v2/sources?language=en&country=us&category=";
    private static final String API_KEY = "";

    GetNewsRunnable(MainActivity mainActivity, String input) {
        this.mainActivity = mainActivity;
        this.input = input;
    }

    @Override
    public void run() {
        final String URL = DATA_URL + input + "&apiKey=" + API_KEY;

        Uri dataUri = Uri.parse(URL);
        String urlToUse = dataUri.toString();
        Log.d(TAG, "run: urlToUse" + urlToUse);
        StringBuilder sb = new StringBuilder();
        try {
            java.net.URL url = new URL(urlToUse);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.addRequestProperty("User-Agent","");
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return;
            }

            InputStream is = connection.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            ArrayList<News> news = parseJSON(sb.toString());
            handleResults("", news);
        } catch (Exception e) {
            handleResults(null, null);
        }
    }

    private void handleResults(String s, ArrayList<News> of) {

        if(s != null){
            Log.d(TAG, "HIIIIIIIIIIIIIIIIIIII");
            mainActivity.runOnUiThread(()->mainActivity.receivedNews(of));
        }

    }

    private ArrayList<News> parseJSON(String s) {

        ArrayList<News> news = new ArrayList<News>();
        try {
            JSONObject wholeThing = new JSONObject(s);
            JSONArray block = wholeThing.getJSONArray("sources");

            String id;
            String name;
            String category;
            for (int i = 0; i < block.length(); i++) {
                JSONObject part = (JSONObject) block.get(i);
                id = part.getString("id");
                name = part.getString("name");
                category = part.getString("category");
                news.add(new News(id, name, category));

            }
            return news;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return news;
    }


}
