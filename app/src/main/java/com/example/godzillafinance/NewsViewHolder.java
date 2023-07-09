package com.example.godzillafinance;


import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NewsViewHolder extends RecyclerView.ViewHolder {
    public TextView titleTV;
    public TextView descriptionTV;

    public NewsViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
        super(itemView);
        this.titleTV = itemView.findViewById(R.id.title);
        this.descriptionTV = itemView.findViewById(R.id.description);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerViewInterface != null) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        recyclerViewInterface.onItemClick(pos);
                    }
                }
            }
        });
    }
    public void bindThisData(NewsLink theNewsToBind) {
        titleTV.setText(theNewsToBind.getTitle());
        descriptionTV.setText(theNewsToBind.getDescription());
    }
}
