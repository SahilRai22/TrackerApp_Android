package com.example.runningtracker;

import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.content.ContentUris;
import android.content.ContentProvider;
import android.content.ContentValues;
/**
 * This class provides CURD operations and is set for URI use
 * */
public class RunnerContent extends ContentProvider {
    public static String URIKey = "com.example.runningtracker.RunnerContent";
    MyDataBase myDb;
    SQLiteDatabase sqlDb;

    private static final UriMatcher matcher;

    static {
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(URIKey, "location/#", 000);
        matcher.addURI(URIKey, "location", 100);
        matcher.addURI(URIKey, "runner", 200);
        matcher.addURI(URIKey, "runner/#", 300);
    }

    @Override
    public boolean onCreate() {
        myDb = new MyDataBase(this.getContext());
        sqlDb = myDb.getWritableDatabase();
        return (sqlDb != null);
    }

    @Override
    public String getType(Uri uri) {
        if (uri.getLastPathSegment() == null) {
            return "vnd.android.cursor.dir/JourneyProvider.data.text";
        } else {
            return "vnd.android.cursor.item/JourneyProvider.data.text";
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[]
            selectionArgs, String sortOrder) {

        switch(matcher.match(uri)) {
            case 300:
                selection = "runnerID = " + uri.getLastPathSegment();
            case 200:
                return sqlDb.query("runner", projection, selection, selectionArgs, null, null, sortOrder);
            case 000:
                selection = "locationID = " + uri.getLastPathSegment();
            case 100:
                return sqlDb.query("location", projection, selection, selectionArgs, null, null, sortOrder);
            default:
                return null;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String table;

        switch(matcher.match(uri)) {
            case 200:
                table = "runner";
                break;
            case 100:
                table = "location";
                break;
            default:
                table = "";
        }

        long _id = sqlDb.insert(table, null, values);
        Uri newRowUri = ContentUris.withAppendedId(uri, _id);

        getContext().getContentResolver().notifyChange(newRowUri, null);
        return newRowUri;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String table;
        int counter;

        switch(matcher.match(uri)) {
            case 300:
                selection = "runnerID = " + uri.getLastPathSegment();
            case 200:
                table = "runner";
                counter = sqlDb.delete(table, selection, selectionArgs);
                break;
            case 000:
                selection = "locationID = " + uri.getLastPathSegment();
            case 100:
                table = "location";
                counter = sqlDb.delete(table, selection, selectionArgs);
                break;
            default:
                return 0;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return counter;
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection, String[]
            selectionArgs) {
        String table;
        int counter;

        switch(matcher.match(uri)) {
            case 300:
                selection = "runnerID = " + uri.getLastPathSegment();
            case 200:
                table = "runner";
                counter = sqlDb.update(table, values, selection, selectionArgs);
                break;
            case 000:
                selection = "locationID = " + uri.getLastPathSegment();
            case 100:
                table = "location";
                counter = sqlDb.update(table, values, selection, selectionArgs);
                break;
            default:
                return 0;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return counter;
    }

}
