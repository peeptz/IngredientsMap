package com.example.ingredientsmapv1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("ON CREATE", "onCreate: onCreate called");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView =(ListView) findViewById(R.id.listview);
        final ArrayList<String> arrayList = new ArrayList<>();

        arrayList.add("Pasta Carbonara");
        arrayList.add("Pizza Quattro Stagioni");
        arrayList.add("Tortino al cioccolato");

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

                //startActivity(intent);
            }
        });
    }
}
