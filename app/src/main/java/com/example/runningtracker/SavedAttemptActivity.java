package com.example.runningtracker;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

/**
 * This class displays saved runs and select specific saves
 * These saves shows distance ran, time taken and name of the run
 * Also, user can edit, check records or view the saves on google maps
 * */
public class SavedAttemptActivity extends AppCompatActivity {
    public static String URIKey = "com.example.runningtracker.RunnerContent";
    public static Uri URI = Uri.parse("content://"+ URIKey +"");
    public static Uri URIRun = Uri.parse("content://"+ URIKey +"/runner");

    public static String runnerNotes = "note";
    public static String runnerTitle = "name";
    public static String runnerTimeTaken = "duration";
    public static String runnerDistance = "distance";

    private TextView viewDistanceRan;
    private TextView viewTimeTaken;
    private TextView viewNotes;
    private TextView viewTitle;

    private Handler handler = new Handler();

    private long runnerID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_attempt);

        getSupportActionBar().setTitle("Saved Attempt");
        initViews();
        addToViews();
        getContentResolver().registerContentObserver(
                URI, true, new MyObserver(handler));
    }

    /***
     * Listens to changes in variables and updates the change
     * The change is update is on the view components
     */
    protected class MyObserver extends ContentObserver {

        public MyObserver(Handler handler) {
            super(handler);
        }
        @Override
        public void onChange(boolean selfChange) {
            this.onChange(selfChange, null);
        }
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            addToViews();
        }
    }
    // initialises views
    private void initViews() {
        viewDistanceRan = findViewById(R.id.routeCoveredRecord);
        viewTimeTaken = findViewById(R.id.timeData);
        viewNotes = findViewById(R.id.ViewSingleJourney_commentText);
        viewTitle = findViewById(R.id.txtTitle);
        runnerID = getIntent().getExtras().getLong("runnerID");
    }

    /**
     * This method creates intent which passes bundle data to swithc to EditSavesActivity
     * */
    public void onClickEdit(View v) {
        Intent editActivity = new Intent(SavedAttemptActivity.this, EditSavesActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong("runnerID", runnerID);
        editActivity.putExtras(bundle);
        startActivity(editActivity);
    }
    /**
     * This method creates intent which passes bundle data to switch to MapsActivity
     * */
    public void onClickMap(View v) {
        Intent map = new Intent(SavedAttemptActivity.this, MapsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong("runnerID", runnerID);
        map.putExtras(bundle);
        startActivity(map);
    }

    /**
     * Method to convert variables of duration (time taken)
     * */
    private String convertDuration(long time) {
        long hours = time / 3600;
        long minutes = (time % 3600) / 60;
        long seconds = time % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    /**
     * This method retrieves indexes in the cursor, which is error checked against -1 to check if it is valid.
     * If vaid, retrieves index value and sets it to view
     */
    private void addToViews() {
        Cursor c = getContentResolver().query(Uri.withAppendedPath(URIRun,
                runnerID + ""), null, null, null, null);

        if(c.moveToFirst()) {
            int distanceIndex = c.getColumnIndex(runnerDistance);
            int timeIndex = c.getColumnIndex(runnerTimeTaken);
            int notesIndex = c.getColumnIndex(runnerNotes);
            int nameIndex = c.getColumnIndex(runnerTitle);


            if (distanceIndex != -1) {
                double distance = c.getDouble(distanceIndex);
                viewDistanceRan.setText(String.format("%.2f KM", distance));
            }
            if (timeIndex != -1) {
                long time       = c.getLong(timeIndex);
                String duration = convertDuration(time);
                viewTimeTaken.setText(duration);
            }
            if (notesIndex != -1) {
                viewNotes.setText(c.getString(notesIndex));
            }
            if (nameIndex != -1) {
                viewTitle.setText(c.getString(nameIndex));
            }
        }
    }


}
