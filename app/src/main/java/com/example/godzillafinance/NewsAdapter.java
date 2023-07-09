package com.example.godzillafinance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsViewHolder> {
    private List<NewsLink> newsLinkList;
    private Context context;
    private final RecyclerViewInterface recyclerViewInterface;

    public NewsAdapter(List<NewsLink> newsLinkList, Context context,
                       RecyclerViewInterface recyclerViewInterface) {
        this.newsLinkList = newsLinkList;
        this.context = context;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewsViewHolder(LayoutInflater.from(context).inflate(R.layout.item_news_layout, null),
                recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        holder.bindThisData(newsLinkList.get(position));
    }

    @Override
    public int getItemCount() {
        return newsLinkList.size();
    }
}
