package com.example.barta_a_messenger_app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.barta_a_messenger_app.R;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start login activity
                startActivity(new Intent(SplashScreenActivity.this, LoginPageActivity.class));
                finish(); // Close the splash activity
            }
        }, 1500);
    }
}