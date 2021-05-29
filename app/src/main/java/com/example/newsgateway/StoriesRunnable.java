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

public class StoriesRunnable implements Runnable{
    private static final String TAG = "GetNewsSources";
    private NewsReceiver newsReceiver;
    private String input;

    private static final String DATA_URL = "https://newsapi.org/v2/top-headlines?sources=";
    private static final String API_KEY = "";

    StoriesRunnable(NewsReceiver newsReceiver, String input) {
        this.newsReceiver = newsReceiver;
        this.input = input;
    }

    @Override
    public void run() {
        final String URL = DATA_URL + input + "&language=en&apiKey=" + API_KEY;

        Uri dataUri = Uri.parse(URL);
        String urlToUse = dataUri.toString();
        Log.d(TAG, "run: urlToUse " + urlToUse);
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
            ArrayList<Stories> stories = parseJSON(sb.toString());
            handleResults("", stories);
        } catch (Exception e) {
            handleResults(null, null);
        }
    }

    private void handleResults(String s, ArrayList<Stories> of) {
        if(s != null){
            newsReceiver.returnResults(of);
        }
    }

    private ArrayList<Stories> parseJSON(String s) {
        ArrayList<Stories> stories = new ArrayList<>();

        try {
            JSONObject jObjMain = new JSONObject(s);
            JSONArray jArray =  jObjMain.getJSONArray("articles");
            for(int i=0; i < jArray.length(); i++){
                String author = "";
                String title = "";
                String description = "";
                String url = "";
                String urlToImage = "";
                String publishedAt = "";
                JSONObject normal = (JSONObject)jArray.get(i);

                if(normal.has("author"))
                    author = normal.getString("author");
                if(normal.has("title"))
                    title = normal.getString("title");
                if(normal.has("description"))
                    description = normal.getString("description");
                if(normal.has("url"))
                    url = normal.getString("url");
                if(normal.has("urlToImage"))
                    urlToImage = normal.getString("urlToImage");
                if(normal.has("publishedAt"))
                    publishedAt = normal.getString("publishedAt");
                stories.add(new Stories(author, title, description, url, urlToImage, publishedAt));
            }
            return stories;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



}
