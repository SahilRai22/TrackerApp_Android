package com.example.runningtracker;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * This class monitor's user's running activities, using the service LocationService.
 * The user can start tracking and then save their runs which prompts a dialog fragment to notify the user
 * that their data has been saved to the database.
 * */
public class TrackRunActivity extends AppCompatActivity {
    private RunningLocationService.LocationServiceBinder locationService;
    private TextView txtTimeTaken;
    private TextView txtDistanceRan;
    private Button btnPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_run);
        getSupportActionBar().setTitle("Track your run");

        txtDistanceRan = findViewById(R.id.ranDistance);
        txtTimeTaken = findViewById(R.id.txtTime);
        btnPlay = findViewById(R.id.btnStart);

        startAndBindService();
    }


    private Handler postBack = new Handler();
    /**
     * This Method connects to service and takes binder object as locationService
     * Thread initialises, retrieving distance ran and time taken from the service
     * These values are then posted onto main thread, which can is set for view
     * */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            locationService = (RunningLocationService.LocationServiceBinder) iBinder;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (locationService != null) {
                        // get the distance and duration from the surface
                        float distanceRan = locationService.getDistanceRan();
                        float d = (float) locationService.getTimeTaken();
                        long duration = (long) d;  // in seconds

                        long hours = duration / 3600;
                        long minutes = (duration % 3600) / 60;
                        long seconds = duration % 60;


                        String setDistanceRan = String.format("%.2f KM", distanceRan);
                        String setTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);

                        postBack.post(new Runnable() {
                            @Override
                            public void run() {
                                txtTimeTaken.setText(setTime);
                                txtDistanceRan.setText(setDistanceRan);
                            }
                        });

                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            locationService = null;
            Log.d("TrackRunActivity", "Service disconnected");
        }
    };

    /**
     * This method connects TrackRunActivity and the service
     * */
    private void startAndBindService() {
        startService(new Intent(TrackRunActivity.this, RunningLocationService.class));
        bindService(
                new Intent(TrackRunActivity.this, RunningLocationService.class), serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // unbind to the service (if we are the only binding activity then the service will be destroyed)
        if(serviceConnection != null) {
            unbindService(serviceConnection);
            serviceConnection = null;
        }
    }

    // starts service and tracking run
    public void onClickPlay(View view) {
        locationService.startTracking();
        btnPlay.setEnabled(false);
    }

   // Stops and Saves the run
    public void onClickSave(View view) {
        locationService.saveRun();
        btnPlay.setEnabled(false);
        DialogFragment modal = ReadDialog.newInstance();
        modal.show(getSupportFragmentManager(), "Finished");
    }

    /**
     * This method pops up notification to user once the tracking has been saved successfully
     * */
    public static class ReadDialog extends DialogFragment {
        public static ReadDialog newInstance() {
            Bundle savedInstanceState = new Bundle();
            ReadDialog fragment = new ReadDialog();
            fragment.setArguments(savedInstanceState);
            return fragment;
        }

        // once fragment created, alert message pops up as notificaiton
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return createSuccessDialog();
        }

        private AlertDialog createSuccessDialog() {
            AlertDialog.Builder alertMsg = new AlertDialog.Builder(getActivity());
            alertMsg.setMessage(" Updating... Run saved!")
                    .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            getActivity().finish();
                        }
                    });
            return alertMsg.create();
        }
    }
}
