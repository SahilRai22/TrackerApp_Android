package com.example.runningtracker;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This class allows user to edit their chosen saved files
 * Saving new title and notes to the database
 * */
public class EditSavesActivity extends AppCompatActivity {
    public static String URIKey = "com.example.runningtracker.RunnerContent";
    public static Uri URIRun = Uri.parse("content://"+URIKey+"/runner");

    public static String runnerNotes = "note";
    public static String runnerTitle = "name";
    private EditText editSaveTitle;
    private EditText editNotes;

    private long runnerID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_saves);
        getSupportActionBar().setTitle("Edit attempt");

        Bundle bundle = getIntent().getExtras();
        runnerID = bundle.getLong("runnerID");
        editSaveTitle = findViewById(R.id.editTitle);
        editNotes = findViewById(R.id.editNote);
        fillText();
    }

    /**
     * recieves content values values and then saves to database
     * */
    public void onClickSave(View v) {

        Uri rowQueryUri = Uri.withAppendedPath(URIRun, "" + runnerID);

        ContentValues contentValues = new ContentValues();

        contentValues.put(runnerTitle, editSaveTitle.getText().toString());
        contentValues.put(runnerNotes, editNotes.getText().toString());

        getContentResolver().update(rowQueryUri, contentValues, null, null);
        finish();
    }

    /**
     * Receives pre-exisiting data and then sends data to cursor
     * */
    private void fillText() {
        Cursor c = getContentResolver().query(Uri.withAppendedPath(URIRun,
                runnerID + ""), null, null, null, null);

        if(c.moveToFirst()) {
            editSaveTitle.setText(c.getString(c.getColumnIndex(runnerTitle)));
            editNotes.setText(c.getString(c.getColumnIndex(runnerNotes)));
        }
    }

}
