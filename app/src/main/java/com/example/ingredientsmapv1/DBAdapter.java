package com.example.ingredientsmapv1;

import android.content.Context;
import android.database.*;
import android.database.sqlite.*;
import android.util.Log;

class DBAdapter{

    private static DBAdapter sInstance;  //singleton instance

    // ADAPTER STATE
    private SQLiteDatabase db; // reference to the DB
    private DBOpenHelper dbHelper; // reference to the OpenHelper

    public static synchronized DBAdapter getInstance(Context context) {
        if (sInstance == null)
            sInstance = new DBAdapter(context.getApplicationContext());

        return sInstance;
    }

    private DBAdapter(Context context) {
        this.dbHelper = DBOpenHelper.getInstance(context);
    }


    public void open() throws SQLException {
        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLiteException e) {
            // the tag used in LogCat messages
            String TAG = "DBAdapter";
            Log.e(TAG, "Couldn't load adapter");
            throw e;
        }
    }
    /**
     * Close the DB.
     */

    public void close() {
        db.close();
    }

    public boolean deleteTodoItem(long idx) {
        return db.delete(DBContract.Recipe.TABLE_NAME, DBContract.Recipe._ID + "=" + idx, null) == 1;
    }


    public Cursor getAllEntries() {
        return db.query(DBContract.Recipe.TABLE_NAME, null, null, null, null, null, null);
    }


    public void deleteRecipeTable() {
        db.delete(DBContract.Recipe.TABLE_NAME, null, null);
        db.execSQL("delete from sqlite_sequence where name='" + DBContract.Recipe.TABLE_NAME + "';");
    }

}
