package com.example.godzillafinance;

import static java.lang.System.out;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import okhttp3.*;


public class NewsFeed extends AppCompatActivity implements RecyclerViewInterface{
    private RecyclerView recyclerView;
    private List<NewsLink> newsLinkList = new ArrayList<>();
    private String FinanceAPIUrl = "https://api.apilayer.com/financelayer/news?";
    private String APIKey = "TaRvTlocSJ6Pjjb8VxsQQah1Hc9ZMwqH";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed_2);

        // Setup filter buttons
        Button btn_daily = findViewById(R.id.btn_daily);
        btn_daily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String URL = FinanceAPIUrl + "date=today";
                newAPIRequest(URL);
                recyclerView.setAdapter(new NewsAdapter(newsLinkList, NewsFeed.this, NewsFeed.this));
            }
        });
        Button btn_monthly = findViewById(R.id.btn_monthly);
        btn_monthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String URL = FinanceAPIUrl + "date=thismonth";
                newAPIRequest(URL);
                recyclerView.setAdapter(new NewsAdapter(newsLinkList, NewsFeed.this, NewsFeed.this));
            }
        });
        Button btn_business = findViewById(R.id.btn_business);
        btn_business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String URL = FinanceAPIUrl + "date=thismonth&tags=Business";
                newAPIRequest(URL);
                recyclerView.setAdapter(new NewsAdapter(newsLinkList, NewsFeed.this, NewsFeed.this));
            }
        });
        Button btn_energy = findViewById(R.id.btn_energy);
        btn_energy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String URL = FinanceAPIUrl + "date=thismonth&tags=Energy";
                newAPIRequest(URL);
                recyclerView.setAdapter(new NewsAdapter(newsLinkList, NewsFeed.this, NewsFeed.this));
            }
        });
        Button btn_stock = findViewById(R.id.btn_stock);
        btn_stock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String URL = FinanceAPIUrl + "date=thismonth&tags=Stock";
                newAPIRequest(URL);
                recyclerView.setAdapter(new NewsAdapter(newsLinkList, NewsFeed.this, NewsFeed.this));
            }
        });
        Button btn_tech = findViewById(R.id.btn_tech);
        btn_tech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String URL = FinanceAPIUrl + "date=thismonth&tags=Technology";
                newAPIRequest(URL);
                recyclerView.setAdapter(new NewsAdapter(newsLinkList, NewsFeed.this, NewsFeed.this));
            }
        });


        recyclerView = findViewById(R.id.newsFeedRecView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.recyclerview_divider, null));
        recyclerView.addItemDecoration(dividerItemDecoration);
        //recyclerView.setAdapter(new NewsAdapter(newsLinkList, this, this));

        String URL = FinanceAPIUrl + "date=today";

        //API code--start thread
        if (savedInstanceState == null) {
            runnableThread runnableThread = new runnableThread(URL);
            Thread thread = new Thread(runnableThread);
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Show filled recyclerview
        recyclerView.setAdapter(new NewsAdapter(newsLinkList, this, this));

    }

    @Override
    public void onItemClick(int position) {
        String url = newsLinkList.get(position).getUrl();
        Uri uri = Uri.parse(url);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        for (int i = 0; i < newsLinkList.size(); i++) {
            NewsLink newsLink = newsLinkList.get(i);
            outState.putString("title" + i, newsLink.getTitle());
            outState.putString("description" + i, newsLink.getDescription());
            outState.putString("url" + i, newsLink.getUrl());
        }
        outState.putInt("size", newsLinkList.size());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        newsLinkList.clear();
        int size = savedInstanceState.getInt("size");
        for (int i = 0; i < size; i++) {
            newsLinkList.add(new NewsLink(savedInstanceState.getString("title"+i),
                    savedInstanceState.getString("description"+i),
                    savedInstanceState.getString("url"+i)));
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    protected void newAPIRequest(String URL) {
        runnableThread runnableThread = new runnableThread(URL);
        Thread thread = new Thread(runnableThread);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // thread class
    class runnableThread implements Runnable {
        //private ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        private Handler textHandler = new Handler();
        private String URL;


        public runnableThread(String URL) {
            this.URL = URL;
        }

        @Override
        public void run() {
//            textHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    progressBar.setVisibility(View.VISIBLE);
//                }
//            });
            newsLinkList.clear();
            JSONObject jObject;
            OkHttpClient client = new OkHttpClient().newBuilder().build();

            Request request = new Request.Builder()
                    .url(URL)
                    .addHeader("apikey", APIKey)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                jObject = new JSONObject(response.body().string());
                //Log.d("jObject_tag", jObject.toString());
                JSONArray jsonArray = (JSONArray) jObject.get("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject row = jsonArray.getJSONObject(i);
                    String title = row.getString("title");
                    String description = row.getString("description");
                    String url = row.getString("url");
                    newsLinkList.add(new NewsLink(title, description, url));
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
//            textHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    progressBar.setVisibility(View.INVISIBLE);
//                }
//            });
        }
    }
}