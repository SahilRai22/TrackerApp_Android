package com.example.runningtracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDataBase extends SQLiteOpenHelper {
    public MyDataBase(Context context) {
        super(context, "mySqlDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE runner (" +
                "runnerID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "duration BIGINT NOT NULL," +
                "distance REAL NOT NULL," +
                "name varchar(256) NOT NULL DEFAULT 'Your saved run'," +
                "note varchar(256) NOT NULL DEFAULT '');");


        db.execSQL("CREATE TABLE location (" +
                " locationID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                " runnerID INTEGER NOT NULL," +
                " latitude REAL NOT NULL," +
                " longitude REAL NOT NULL," +
                " altitude REAL NOT NULL," +

                " CONSTRAINT fk1 FOREIGN KEY (runnerID) REFERENCES runner (runnerID) ON DELETE CASCADE);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
