package com.example.runningtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.widget.TextView;
import android.os.Bundle;
import android.os.Handler;
import android.database.Cursor;

/**
 * This class shows personal records of user, which is the furthest attempt in one go and total distance covered
 * */
public class PersonalRecordActivity extends AppCompatActivity {
    public static String URIKey = "com.example.runningtracker.RunnerContent";
    public static Uri URIRun = Uri.parse("content://"+ URIKey +"/runner");
    private TextView recordDistance;
    private TextView distanceAllTime;

    public static final String runnerDistance = "distance";


    private Handler postBack = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_record);
        recordDistance  = findViewById(R.id.routeCoveredRecord);
        distanceAllTime = findViewById(R.id.totalkmRecord);
        getSupportActionBar().setTitle("Personal Record");
        showRecordBoard();
    }
    /**
     * This method retrieves record distance and all time record covered
     * */
    private void showRecordBoard() {
        new Thread(() -> {
            double totalDistanceKM = 0;
            double recordDistanceKM = 0;
            try (Cursor c = getContentResolver().query(URIRun,
                    null, null, null, null)) {
                while (c.moveToNext()) {
                    int distanceIndex = c.getColumnIndexOrThrow(runnerDistance);
                    double distance = c.getDouble(distanceIndex);
                    if (recordDistanceKM < distance) {
                        recordDistanceKM = distance;
                    }
                    totalDistanceKM += distance;
                }
            }
            double finalRecordDistanceKM = recordDistanceKM;
            double finalTotalDistanceKM = totalDistanceKM;

            postBack.post(() -> {
                recordDistance.setText(String.format("%.2f km", finalRecordDistanceKM));
                distanceAllTime.setText(String.format("%.2f km", finalTotalDistanceKM));
            });
        }).start();
    }

}