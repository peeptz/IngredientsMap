package com.example.ingredientsmapv1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class Description extends AppCompatActivity {

    String name = "";
    String prep = "";
    final ArrayList<String> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("ON CREATE", "onCreate: onCreate called for Activity Description");
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            name = extras.getString("EXTRA_RECIPE_DETAIL_NAME");
            Log.d("NAME", "onCreate: " + name);
        }
        getDescription();
    }

    public void getDescription() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Descriptions/" + name);
        Log.d("NAME", "getAll: " + name);
        Query query = reference.orderByChild(name);
        Log.d("Query", "getAll:" +  query);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("RESULT", "onDataChange: " + dataSnapshot.getValue());
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String ingredient = child.getValue().toString();
//                    arrayList.add(ingredient);
                    prep = prep + child.getValue();
                }
                Log.d("DESCRIPTION", "onDataChange: " + arrayList.toString());
                setContentView(R.layout.preparation);
                TextView preparation = (TextView) findViewById (R.id.textView);
                TextView recipeName = (TextView) findViewById (R.id.textView2);
                recipeName.setText(name);
                preparation.setText(prep.replaceAll("[\\[\\](){}]",""));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("PASSING BACK", "onBackPressed: PASSING DATA BACK" + name);
        Intent returnIntent = new Intent();
        returnIntent.putExtra("name", name);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }
}
