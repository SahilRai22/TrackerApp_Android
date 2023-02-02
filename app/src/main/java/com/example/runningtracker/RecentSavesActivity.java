package com.example.runningtracker;

import android.net.Uri;
import android.widget.TextView;
import android.widget.ListView;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;


/**
 * This class shows recent saved runs, which can be selected individually.
 * These saved runs are provided in a list and can be scrolled throguh
 * */
public class RecentSavesActivity extends  ListActivity  {
    public static String URIKey = "com.example.runningtracker.RunnerContent";
    public static Uri URIRun = Uri.parse("content://"+ URIKey +"/runner");

    public static String runnerTitle = "name";
    public static String runnerID = "runnerID";


    private ListView savesList;
    private JourneyAdapter adapter;
    private ArrayList<runSaveObjects> savesName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_saves);
        savesList = getListView();
        savesName = new ArrayList<runSaveObjects>();
        adapter = new JourneyAdapter(this, R.layout.recentsaves, savesName);
        setListAdapter(adapter);

        getSavedRuns();
        savesList.setClickable(true);
        savesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                runSaveObjects obj = (runSaveObjects) savesList.getItemAtPosition(position);
                long runnerID = obj.getSavesID();

                Bundle bundle = new Bundle();
                bundle.putLong("runnerID", runnerID);
                Intent savedAttempt = new Intent(RecentSavesActivity.this, SavedAttemptActivity.class);
                savedAttempt.putExtras(bundle);
                startActivity(savedAttempt);
            }
        });
    }

    private class runSaveObjects {
        private long savesID;
        private String name;

        public String getSavesName() {
            return name;
        }
        public long getSavesID() {
            return savesID;
        }
        public void setSavesId(long ID) {
            this.savesID = ID;
        }
        public void setSavesName(String name) {
            this.name = name;
        }
    }

    /**
     * This method displays saved run objects as a listview, which is the saved name of the attempts
     */
    private class JourneyAdapter extends ArrayAdapter<runSaveObjects> {
        private ArrayList<runSaveObjects> objects;

        public JourneyAdapter(Context context, int textViewResourceId, ArrayList<runSaveObjects> obj) {
            super(context, textViewResourceId, obj);
            this.objects = obj;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = vi.inflate(R.layout.recentsaves, null);
            }
            // checks for viability, then sets onbject to view
            runSaveObjects obj = objects.get(position);
            if (obj != null) {
                TextView text = view.findViewById(R.id.savedAttemtps);
                if (text != null) {
                    text.setText(obj.getSavesName());
                }
            }
            return view;
        }
    }

    public void onClickPersonalRecord(View v) {
        Intent stats = new Intent(RecentSavesActivity.this, PersonalRecordActivity.class);
        startActivity(stats);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * Retrieves saved runs from database using cursor, iterating through each saved instances of the data
     * after each iteration new object is saved to the array list
     * */
    private void getSavedRuns() {
        Cursor c = getContentResolver().query(URIRun,
                new String[] {runnerID + " _id", runnerTitle}, null, null, runnerTitle + " ASC");

        Log.d("mdp", "Journeys Loaded: " + c.getCount());

        savesName = new ArrayList<runSaveObjects>();
        adapter.clear();

        try {
            while(c.moveToNext()) {
                runSaveObjects i = new runSaveObjects();
                int nameIndex = c.getColumnIndex(runnerTitle);
                int idIndex = c.getColumnIndex("_id");
                if (nameIndex != -1) {
                    i.setSavesName(c.getString(nameIndex));
                }
                if (idIndex != -1) {
                    i.setSavesId(c.getLong(idIndex));
                }
                savesName.add(i);
            }
            adapter.addAll(savesName);
        } finally {
            c.close();
        }
    }

}
