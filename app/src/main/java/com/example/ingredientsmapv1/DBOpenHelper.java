package com.example.ingredientsmapv1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DBOpenHelper extends SQLiteOpenHelper {

    private static DBOpenHelper sInstance;

    public static final String DATABASE_NAME = "recipeApp.db";
    public static final String TABLE_NAME = "recipe_table";
    public static final String TABLE_ING_NAME = "recipe_ing_table";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_ID_INGREDIENT = "ingredient_id";
    public static final String COL_NAME_INGREDIENT = "ingredient";
    public static final String COL_NAME_QUANTITY = "quantity";

    static synchronized DBOpenHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DBOpenHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public DBOpenHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String queryRec = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_NAME + " Text);";

        String queryIngr = "CREATE TABLE IF NOT EXISTS " + TABLE_ING_NAME + "("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_ID_INGREDIENT + " INTEGER,"
                + COL_NAME_INGREDIENT + " Text,"
                + COL_NAME_QUANTITY + " INTEGER);";

//        db.execSQL("DELETE FROM " + TABLE_NAME);
//        db.execSQL("DELETE FROM " + TABLE_ING_NAME);
        db.execSQL(queryRec);
        db.execSQL(queryIngr);


//        db.execSQL("create table " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, _name TEXT)");
//        db.execSQL("insert into " + TABLE_NAME + (COL_ID, COL_NAME);
        Log.d("ON_CREATE", "onCreate: DATABASE CREATED");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("ON_UPDATE", "onUpgrade: CALLED ON_UPDATE");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean InsertData(Integer id, String name) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NAME, name);
        long result = db.insert(TABLE_NAME, null, contentValues);
        return result > 0;
    }

    public boolean InsertIngData(int id,  String name, int quantity) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ID_INGREDIENT, id);
        contentValues.put(COL_NAME_INGREDIENT, name);
        contentValues.put(COL_NAME_QUANTITY, quantity);
        long result = db.insert(TABLE_ING_NAME, null, contentValues);
        return result > 0;
    }

    public Cursor getAllData(){
        SQLiteDatabase db = getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return res;
    }

    public Cursor getIngData(int r){
        Log.d("CALLED", "getIngData: " + r);
        SQLiteDatabase db = getWritableDatabase();
        int ingredient_ID = r + 1;
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_ING_NAME + " WHERE ingredient_id = " + ingredient_ID,  null);
        return res;
    }

    // Method to delete every record in a table
    public void deleteAll(String table){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from "+ table);
    }
}
