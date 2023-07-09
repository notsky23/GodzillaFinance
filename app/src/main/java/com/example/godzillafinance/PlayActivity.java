package com.example.godzillafinance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class PlayActivity extends AppCompatActivity {

    // Displays the game start.
    private TextView Start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        Start = findViewById(R.id.play);
        Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity_playGame();
            }

            private void gotoActivity_playGame() {
                Intent i = new Intent(PlayActivity.this,GameActivity.class);
                startActivity(i);
            }
        });

    }
}