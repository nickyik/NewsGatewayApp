package com.example.newsgateway;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFragment extends Fragment {
    private ImageView imageView;

    public NewsFragment(){
        // Required empty public constructor
    }

    static NewsFragment newInstance(Stories stories, int index, int max)
    {
        NewsFragment f = new NewsFragment();
        Bundle bdl = new Bundle(1);
        bdl.putSerializable("STORIES_DATA", stories);
        bdl.putSerializable("INDEX", index);
        bdl.putSerializable("TOTAL_COUNT", max);
        f.setArguments(bdl);
        return f;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment_layout = inflater.inflate(R.layout.fragment_layout, container, false);

        Bundle args = getArguments();
        if (args != null) {
            final Stories currentStory = (Stories) args.getSerializable("STORIES_DATA");
            if (currentStory == null) {
                return null;
            }
            int index = args.getInt("INDEX");
            int total = args.getInt("TOTAL_COUNT");

            //display title
            TextView title = fragment_layout.findViewById(R.id.fragmentTitle);
            if(!currentStory.getTitle().equals("null")){
                title.setText(currentStory.getTitle());
                title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        Bundle args = getArguments();
                        final Stories currentStory = (Stories) args.getSerializable("STORIES_DATA");

                        i.setData(Uri.parse(currentStory.getUrl()));
                        startActivity(i);
                    }
                });
            }else{
                title.setText("");
            }
            //display author
            TextView author = fragment_layout.findViewById(R.id.fragmentAuthor);
            if(!currentStory.getAuthor().equals("null")){
                author.setText(currentStory.getAuthor());
            }else{
                author.setText("");
            }

            //display date aka published at
            TextView publishedAt = fragment_layout.findViewById(R.id.fragmentDate);
            String formattedDate = "";
            String fPattern = "yyyy-MM-dd'T'HH:mm:ss'Z'";
            String sPattern = "yyyy-MM-dd'T'HH:mm:ss+hh:mm";
            String tPattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
            if(!currentStory.getPublishedAt().equals("null")) {
                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(fPattern);
                    formattedDate = new SimpleDateFormat("MMM d, YYYY HH:mm")
                            .format(Objects.requireNonNull(simpleDateFormat.parse(currentStory.getPublishedAt())));
                } catch (ParseException e) {
                    try {
                        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat(sPattern);
                        formattedDate = new SimpleDateFormat("MMM d, YYYY HH:mm")
                                .format(Objects.requireNonNull(simpleDateFormat1.parse(currentStory.getPublishedAt())));
                    } catch (Exception err) {
                        try {
                            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(tPattern);
                            formattedDate = new SimpleDateFormat("MMM d, YYYY HH:mm")
                                    .format(Objects.requireNonNull(simpleDateFormat2.parse(currentStory.getPublishedAt())));
                        } catch (Exception er) {
                            publishedAt.setText(currentStory.getPublishedAt());
                        }
                    }
                }
                publishedAt.setText(formattedDate);
            } else{
                publishedAt.setText("");
            }

            //display text aka description
            TextView bodyText = fragment_layout.findViewById(R.id.fragmentText);
            if(!currentStory.getDescription().equals("null")){
                bodyText.setText(currentStory.getDescription());
                bodyText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        Bundle args = getArguments();
                        final Stories currentStory = (Stories) args.getSerializable("STORIES_DATA");

                        i.setData(Uri.parse(currentStory.getUrl()));
                        startActivity(i);
                    }
                });
            }else{
                bodyText.setText("");
            }
            //get image
            imageView = fragment_layout.findViewById(R.id.fragmentImage);
            if(!currentStory.getUrlToImage().equals("null")) {
                loadRemoteImage(currentStory.getUrlToImage());
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        Bundle args = getArguments();
                        final Stories currentStory = (Stories) args.getSerializable("STORIES_DATA");

                        i.setData(Uri.parse(currentStory.getUrl()));
                        startActivity(i);
                    }
                });

            }else{
                imageView.setImageResource(R.drawable.brokenimage);
                //loadRemoteImage(currentStory.getUrlToImage());
            }

            TextView pageNum = fragment_layout.findViewById(R.id.page_num);
            pageNum.setText(String.format(Locale.US, "%d of %d", index, total));

            return fragment_layout;
        } else {
            return null;
        }
    }

    private void loadRemoteImage(final String imageURL) {
        Picasso.get().load(imageURL)
                .error(R.drawable.brokenimage)
                .placeholder(R.drawable.placeholder)
                .into(imageView);
    }

}
