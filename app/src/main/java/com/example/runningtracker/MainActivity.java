package com.example.runningtracker;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

/**
 * This is the main activity and the origin starting point of the app
 */
public class MainActivity extends AppCompatActivity {
    private ImageView homeImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Home");

        homeImg = findViewById(R.id.imageView);
        homeImg.setImageResource(R.drawable.running);
    }

    public void onClickTrackActivity(View v) {
        Intent journey = new Intent(MainActivity.this, TrackRunActivity.class);
        startActivity(journey);
    }

    public void onClickSavedRuns(View v) {
        Intent view = new Intent(MainActivity.this, RecentSavesActivity.class);
        startActivity(view);
    }

}
