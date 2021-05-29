package com.example.newsgateway;


import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static final String NEWS_FROM_SERVICE = "NEWS_FROM_SERVICE";
    static final String MESSAGE_FROM_SERVICE = "MESSAGE_FROM_SERVICE";
    static final String NEWS_DATA = "NEWS_DATA";
    static final String MESSAGE_DATA = "MESSAGE_DATA";

    private static final String TAG = "TAG";
    private ArrayList<String> subRegionDisplayed = new ArrayList<>();
    private HashMap<String, ArrayList<String>> regionData = new HashMap<>();
    private Menu opt_menu;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private List<Fragment> fragments;
    private MyPageAdapter pageAdapter;
    private ArrayAdapter<String> arrayAdapter;
    private ViewPager pager;
    private String currentSubRegion;
    public static int screenWidth, screenHeight;
    private ArrayList<News> newsList = new ArrayList<>();
    private NewsReceiver newsReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.drawer_list);

        // Set up the drawer item click callback method
        mDrawerList.setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String temp = null;
                        for(News n : newsList){
                            if(subRegionDisplayed.get(position).equals(n.getName())){
                                temp = n.getId();
                            }
                        }
                        Log.d(TAG, "Name: " + temp);
                        selectItem(temp);
                        mDrawerLayout.closeDrawer(mDrawerList);
                        changeTitle(view);
                    }
                }
        );

        // Create the drawer toggle
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        );

        fragments = new ArrayList<>();

        pageAdapter = new MyPageAdapter(getSupportFragmentManager());
        pager = findViewById(R.id.viewpager);
        pager.setAdapter(pageAdapter);

        arrayAdapter = new ArrayAdapter<>(this, R.layout.drawer_item, subRegionDisplayed);
        mDrawerList.setAdapter(arrayAdapter);

        GetNewsRunnable newsRunnable = new GetNewsRunnable(this, "");
        new Thread(newsRunnable).start();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    public void changeTitle(View view){
        getSupportActionBar().setTitle(((TextView) view).getText().toString());
    }

    private void selectItem(String id) {
        /// Start the service
        Intent intent = new Intent(MainActivity.this, NewsService.class);
        intent.putExtra("story", id);
        startService(intent);

        IntentFilter filter1 = new IntentFilter(NEWS_FROM_SERVICE);
        IntentFilter filter2 = new IntentFilter(MESSAGE_FROM_SERVICE);

        newsReceiver = new NewsReceiver(this);

        registerReceiver(newsReceiver, filter1);
        registerReceiver(newsReceiver, filter2);
    }

    private void stoppingService() {
        Intent intent = new Intent(this, NewsService.class);
        stopService(intent);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(newsReceiver);
        stoppingService();
        super.onDestroy();
    }

    public void sendBackStories(ArrayList<Stories> story){
        stoppingService();
        Log.d(TAG, "Author: " + story.get(0).getAuthor() + " Author2: " + story.get(1).getAuthor());
        for(int i = 0; i < pageAdapter.getCount(); i++){
            pageAdapter.notifyChangeInPosition(i);
        }
        fragments.clear();
        for (int i = 0; i < story.size(); i++){
            fragments.add(NewsFragment.newInstance( story.get(i), i+1, story.size() ));
        }
        pager.setBackground(null);
        pageAdapter.notifyDataSetChanged();
        pager.setCurrentItem(0);

//        Log.d(TAG, "sendBackStories: " + story.get(0).getTitle());
    }

    // You need the 2 below to make the drawer-toggle work properly:

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }



    // You need the below to open the drawer when the toggle is clicked
    // Same method is called when an options menu item is selected.

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //subRegionDisplayed.clear();
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "onOptionsItemSelected: mDrawerToggle " + item);
            return true;
        }
        subRegionDisplayed.clear();
        for(int i = 0; i < newsList.size(); i++){
            if(newsList.get(i).getCategory().equals(item.getTitle())){
                subRegionDisplayed.add(newsList.get(i).getName());
            }
        }
        arrayAdapter.notifyDataSetChanged();
        return super.onOptionsItemSelected(item);

    }

    // You need this to set up the options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        opt_menu = menu;
        return true;
    }

    public void receivedNews(ArrayList<News> news){
        subRegionDisplayed.clear();

        for (int i =0; i<news.size(); i++){
            subRegionDisplayed.add(news.get(i).getName());
        }
        Log.d(TAG, "SUB REGION" + Integer.toString(subRegionDisplayed.size()));
        newsList.addAll(news);
        ArrayList<String> temp = new ArrayList<>();
        for (News n : news){
            if(!temp.contains(n.getCategory())){
                temp.add(n.getCategory());
                opt_menu.add(n.getCategory());
            }
        }
//        arrayAdapter.notifyDataSetChanged();
        arrayAdapter = new ArrayAdapter<>(this, R.layout.drawer_item, subRegionDisplayed);
        mDrawerList.setAdapter(arrayAdapter);

    }

//////////////////////////////////////

    private class MyPageAdapter extends FragmentPagerAdapter {
        private long baseId = 0;

        MyPageAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public long getItemId(int position) {
            // give an ID different from position when position has been changed
            return baseId + position;
        }

        /**
         * Notify that the position of a fragment has been changed.
         * Create a new ID for each position to force recreation of the fragment
         * @param n number of items which have been changed
         */
        void notifyChangeInPosition(int n) {
            // shift the ID returned by getItemId outside the range of all previous fragments
            baseId += getCount() + n;
        }

    }
}
