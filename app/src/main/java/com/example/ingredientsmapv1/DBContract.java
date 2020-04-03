package com.example.ingredientsmapv1;
import android.provider.BaseColumns;

/* DB CONTRACT */
public class DBContract {

    // DATABASE
    static final String DATABASE_NAME = "recipeApp.db";
    static final int DATABASE_VERSION = 1;


    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private DBContract() {
    }

    /* Inner class that defines the table contents */
    static abstract class Recipe implements BaseColumns {

        static final String TABLE_NAME = "recipe_table";
        public static final String COL_ID = "id";
        public static final String COL_NAME = "name";
    }
}

