package com.example.godzillafinance;

import static com.example.godzillafinance.ProfileActivity.i1;
import static com.example.godzillafinance.ProfileActivity.i2;
import static com.example.godzillafinance.ProfileActivity.i3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity {

    private CardView budgetCardView;
    private CardView todayCardView;
    private CardView weeklyCardView;
    private CardView MonthlyCardView;
    private CardView FetchCardView;
    private CardView PlayCardView;
    private CardView Learning;

    private TextView Budget_Tv;
    private TextView Today_Tv;
    private TextView Week_Tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        budgetCardView = (CardView) findViewById(R.id.budgetCardView);
        todayCardView = (CardView) findViewById(R.id.todayCardView);
        weeklyCardView = (CardView) findViewById(R.id.WeekCardView);
        MonthlyCardView = (CardView) findViewById(R.id.MonthlyCardView);
        FetchCardView = (CardView) findViewById(R.id.FetchCardView);
        PlayCardView = (CardView) findViewById(R.id.GamesCardView);

        Budget_Tv = findViewById(R.id.Budget_Tv);
        Budget_Tv.setText("$:- "+i1);

        Today_Tv = findViewById(R.id.Today_Tv);
        Today_Tv.setText("$:- "+i2);

        Week_Tv = findViewById(R.id.Week_Tv);
        Week_Tv.setText("$:- "+i3);

        budgetCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity1();
            }
        });
        todayCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity2();
            }
        });
        weeklyCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {gotoActivity3();}
        });
        MonthlyCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {gotoActivity4();}
        });
        FetchCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {gotoActivity5();}
        });
        PlayCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {gotoActivity6();}
        });
    }
    public void gotoActivity1(){
        Intent i = new Intent(this,BudgetActivity.class);
        startActivity(i);
    }
    public void gotoActivity2(){
        Intent i = new Intent(this,TodaySpendingActivity.class);
        startActivity(i);
    }
    public void gotoActivity3(){
        Intent i = new Intent(this,WeeklySpending.class);
        startActivity(i);
    }
    public void gotoActivity4(){
        Intent i = new Intent(this,MonthlyActivity.class);
        startActivity(i);
    }
    public void gotoActivity5(){
        Intent i = new Intent(this,NewsFeed.class);
        startActivity(i);
    }
    public void gotoActivity6(){
        Intent i = new Intent(this,PlayActivity.class);
        startActivity(i);
    }
}