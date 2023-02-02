package com.example.runningtracker;

import android.net.Uri;
import android.util.Log;
import android.app.Service;
import android.content.Context;
import android.content.ContentValues;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;

import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import java.util.ArrayList;

/**
 * This class is a service to track user's location using location manager.
 * Each GPS location call is listened to and stored into arraylist
 * */
public class RunningLocationService extends Service {
    private final IBinder binder = new LocationServiceBinder();
    private LocationManager locationManager;
    private MyLocationListener myLocationListener;

    private long startTime = 0;
    private long stopTime = 0;
    public static String runnerTimeTaken = "duration";
    public static String runnerDistance = "distance";

    public static String runnerID = "runnerID";
    public static String locationAltitude = "altitude";
    public static String locationLongitude = "longitude";
    public static String locationLatitude = "latitude";
    public static String URIKey = "com.example.runningtracker.RunnerContent";
    public static Uri URIRun = Uri.parse("content://"+ URIKey +"/runner");
    public static Uri URILocation = Uri.parse("content://"+ URIKey +"/location");

    // locationListener interface tracks the location of the runner
    public class MyLocationListener implements LocationListener {
        ArrayList<Location> listLocation;
        boolean recordLocations;

        public MyLocationListener() {
            emptyLocationList();
            recordLocations = false;
        }

        /**
         * When the location of user updates, value is picked up and added to arraylist
         * */
        @Override
        public void onLocationChanged(Location location) {
            if(recordLocations) {
                listLocation.add(location);
            }
        }

        /**
         * This method calculates the distance ran by user in km
         * */
        public float getDistanceRan() {
            if (listLocation.isEmpty()) {
                return 0;
            }
            float distance = 0;
            for (int i = 0; i < listLocation.size() - 1; i++) {
                distance += listLocation.get(i).distanceTo(listLocation.get(i + 1));
            }

            return distance / 1000;
        }


        public ArrayList<Location> getListLocation() {
            return listLocation;
        }

        public void emptyLocationList() {
            listLocation = new ArrayList<Location>();
        }
    }

    public class LocationServiceBinder extends Binder {
        public float getDistanceRan() {
            return RunningLocationService.this.getDistance();
        }

        public double getTimeTaken() {
            return RunningLocationService.this.getDuration();
        }

        public void startTracking() {
            RunningLocationService.this.startTrackingGPS();
        }

        public void saveRun() {
            RunningLocationService.this.saveLocation();
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("mdp", "Location Service created");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        myLocationListener = new MyLocationListener();
        myLocationListener.recordLocations = false;

        try {
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 5, 5, myLocationListener);
        } catch(SecurityException e) {
            // don't have the permission to access GPS
            Log.d("mdp", "No Permissions for GPS");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(myLocationListener);
        myLocationListener = null;
        locationManager = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent,  flags, startId);
        return START_NOT_STICKY;
    }

    protected float getDistance() {
        return myLocationListener.getDistanceRan();
    }


    protected void startTrackingGPS() {
        myLocationListener.emptyLocationList();
        myLocationListener.recordLocations = true;
        startTime = SystemClock.elapsedRealtime();
        stopTime = 0;
    }

    private double getTimeTaken(long startTime, long endTime) {
        return (endTime - startTime) / 1000.0;
    }

    /***
     * this method sets starting time as 0.0
     * has functions to check end time and start time with real time
     */
    protected double getDuration() {
        long endTime = SystemClock.elapsedRealtime();

        if(startTime==0){
            return 0.0;
        }
        if(stopTime != 0) {
            endTime = stopTime;
        }
        return getTimeTaken(startTime, endTime);
    }

    private void setRunningLocation(long runnerID, Location location) {
        ContentValues locationData = new ContentValues();
        getContentResolver().insert(URILocation, locationData);

        locationData.put(locationLatitude, location.getLatitude());
        locationData.put(locationAltitude, location.getAltitude());
        locationData.put(locationLongitude, location.getLongitude());
        locationData.put(RunningLocationService.runnerID, runnerID);
    }

    protected void saveLocation() {
        ContentValues saveData = new ContentValues();
        saveData.put(runnerDistance, getDistance());
        saveData.put(runnerTimeTaken, (long) getDuration());

        long runnerID = Long.parseLong(getContentResolver().insert(URIRun, saveData).getLastPathSegment());
        for(Location location : myLocationListener.getListLocation()) {
            setRunningLocation(runnerID, location);
        }

        myLocationListener.recordLocations = false;


        stopTime = SystemClock.elapsedRealtime();
        startTime = 0;
        myLocationListener.emptyLocationList();
        Log.d("RunningLocationService", "saveLocationData: " + runnerID);
    }

}
