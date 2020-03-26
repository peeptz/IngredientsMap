package com.example.ingredientsmapv1;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class RecipeDetailActivity extends AppCompatActivity {

    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("ON CREATE", "onCreate: onCreate called for Activity RecipeDetail");
        super.onCreate(savedInstanceState);

        String name = "Undefined";

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // Intent mIntent = getIntent();
            // int intValue = mIntent.getIntExtra("intVariableName", 0);
            int position = extras.getInt("EXTRA_RECIPE_DETAIL_POSITION");
            name = extras.getString("EXTRA_RECIPE_DETAIL_NAME");
            Log.d("Recived extra", "Argument recieved is " + name + " in position " + position);
        }

        setContentView(R.layout.detail_recipe);
        TextView recipeName = (TextView) findViewById (R.id.recipeName);
        recipeName.setText(name);

        listView =(ListView) findViewById(R.id.listviewIngredients);
        final ArrayList<String> arrayList = new ArrayList<>();

        arrayList.add("Ingrediente 1");
        arrayList.add("Ingrediente 2");
        arrayList.add("Ingrediente 3");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);
    }
}
