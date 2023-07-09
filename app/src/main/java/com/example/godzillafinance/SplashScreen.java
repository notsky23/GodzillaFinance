package com.example.godzillafinance;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    private static  int SPLASH_timer = 3000;
    Animation animation_val;
    ImageView SplashScreenImageView;
    TextView ApplicationName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        // creating animations here.
        animation_val = AnimationUtils.loadAnimation(this, R.anim.animation);

        // Adding animations values to iamge and text views.
        SplashScreenImageView = findViewById(R.id.Splash_Screen_ImageView);
        ApplicationName = findViewById(R.id.Application_Name);
        SplashScreenImageView.setAnimation(animation_val);
        ApplicationName.setAnimation(animation_val);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this,LoginActivity.class);
                startActivity(i);
                finish();
            }
        },SPLASH_timer);

         }
}