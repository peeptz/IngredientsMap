package com.example.ingredientsmapv1;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
// import android.database.sqlite;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    DBOpenHelper myDB;
    ListView listView;
    final ArrayList<String> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("ON CREATE", "onCreate: onCreate called");

        super.onCreate(savedInstanceState);
        DBAdapter dbAdapter = DBAdapter.getInstance(this);
        setContentView(R.layout.activity_main);
        myDB = new DBOpenHelper(this);
        listView =(ListView) findViewById(R.id.listview);


        // DELETE EVERYTHING FROM DB
        // myDB.deleteAll(DBOpenHelper.TABLE_NAME);

         // POPULATE DB with recipes
//         myDB.InsertData(1, "Pasta Carbonara");
//         myDB.InsertData(2, "Tortino al cioccolato");
//         myDB.InsertData(3, "Pizza Quattro formaggi");

         // POPULATE DB with Ingredients
//         myDB.InsertIngData(1, "Pasta", 350);
//         myDB.InsertIngData(1, "Uova", 2);
//         myDB.InsertIngData(2, "Farina", 2);
//         myDB.InsertIngData(2, "Cioccolato", 2);
//         myDB.InsertIngData(2, "Burro", 2);
//         myDB.InsertIngData(3, "Farina", 2);
//         myDB.InsertIngData(3, "Formaggio", 2);
//         myDB.InsertIngData(3, "Sale", 2);

        getAll();
//        arrayList.add("Pasta Carbonara");
//        arrayList.add("Pizza Quattro Stagioni");
//        arrayList.add("Tortino al cioccolato");
//        arrayList.add("DIOPORCO");


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("CLICKED ELEMENT", "onItemClick() returned: " + position);
                Intent intent = new Intent(MainActivity.this, RecipeDetailActivity.class);
                intent.putExtra("EXTRA_RECIPE_DETAIL_POSITION", position);
                intent.putExtra("EXTRA_RECIPE_DETAIL_NAME", arrayList.get(position));
                Log.d("INTENT", "Intent created");
                startActivity(intent);
            }
        });
    }

    public void getAll() {
        Cursor res = myDB.getAllData();
        if (res.getCount() == 0) {
            Log.e("NO_ELEMENT", "NO ELEMENTS IN THE DB");
        }
        while (res.moveToNext()) {
            arrayList.add(res.getString(1));
        }
    }
}
