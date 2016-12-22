package com.udacity.caraher.emma.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.udacity.caraher.emma.popularmovies.data.MovieContract;
import com.udacity.caraher.emma.popularmovies.data.MovieDbHelper;

/**
 * Created by Jemma on 12/21/2016.
 */

public class Utility {

    public static void updateValuesInTable(Context context, String tableName,
                                           ContentValues values, long index) {
        MovieDbHelper dbHelper = new MovieDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String id = MovieContract.MovieEntry._ID;

        try {
            String query = "SELECT * FROM " + tableName + " WHERE " + id + " = ?";
            Cursor cursor = db.rawQuery(query, new String[]{Long.toString(index)});
            try {
                cursor.moveToFirst();
                db.update(tableName, values, id + " = ?",
                        new String[]{Long.toString(index)});
            } finally {
                cursor.close();
            }
        } finally {
            db.close();
        }
    }

    public static long insertValuesInTable(Context context, String tableName, ContentValues values) {
        long index;
        MovieDbHelper dbHelper = new MovieDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            index = db.insert(tableName, null, values);
        } finally {
            db.close();
        }
        return index;
    }

    public static ContentValues getMovieContentValues(Context context, String index) {
        ContentValues values;

        MovieDbHelper dbHelper = new MovieDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {
            String query = "SELECT * FROM " + MovieContract.MovieEntry.TABLE_NAME
                    + " WHERE " + MovieContract.MovieEntry.COLUMN_API_ID + " = ?";
            Cursor cursor = db.rawQuery(query, new String[]{index});

            cursor.moveToFirst();
            values = new ContentValues();

            String apiId = getStringFromCursor(cursor, MovieContract.MovieEntry.COLUMN_API_ID);
            String title = getStringFromCursor(cursor, MovieContract.MovieEntry.COLUMN_TITLE);
            String plot = getStringFromCursor(cursor, MovieContract.MovieEntry.COLUMN_PLOT);
            String rating = getStringFromCursor(cursor, MovieContract.MovieEntry.COLUMN_RATING);
            String date = getStringFromCursor(cursor, MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
            int favorite = getIntFromCursor(cursor, MovieContract.MovieEntry.COLUMN_FAVORITE);
            String path = getStringFromCursor(cursor, MovieContract.MovieEntry.COLUMN_POSTER_PATH);

            putInContentValue(values, MovieContract.MovieEntry.COLUMN_API_ID, apiId);
            putInContentValue(values, MovieContract.MovieEntry.COLUMN_TITLE, title);
            putInContentValue(values, MovieContract.MovieEntry.COLUMN_PLOT, plot);
            putInContentValue(values, MovieContract.MovieEntry.COLUMN_RATING, rating);
            putInContentValue(values, MovieContract.MovieEntry.COLUMN_RELEASE_DATE, date);
            putInContentValue(values, MovieContract.MovieEntry.COLUMN_FAVORITE, date);
            putInContentValue(values, MovieContract.MovieEntry.COLUMN_POSTER_PATH, path);
        } finally {
            db.close();
        }

        return values;
    }

    public static String getStringFromCursor(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        return cursor.getString(index);
    }

    public static int getIntFromCursor(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        return cursor.getInt(index);
    }


    public static void putInContentValue(ContentValues values,
                                         String columnName, String val) {

        values.put(columnName, val);
    }

    public static void putInContentValue(ContentValues values,
                                         String columnName, int val) {

        values.put(columnName, val);
    }

}
