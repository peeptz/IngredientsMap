package com.example.ingredientsmapv1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "FIREBASE";
    DBOpenHelper myDB;
    private ListView listView;
    private final ArrayList<String> arrayList = new ArrayList<>();
    Map<String, List<String>> hm = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Checking if the device is connected to the internet
        if (isOnline()) {
            // Getting recipies through Firebase
            final DatabaseReference recipeRef = FirebaseDatabase.getInstance().getReference("ingredients");
            recipeRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        arrayList.add(child.getKey());
                        ArrayList r = (ArrayList) child.getValue();
                        hm.put(child.getKey(), r);
                    }
                    fillList();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView =(ListView) findViewById(R.id.listview);
    }

    // Filling list with realuts of the Firebase Query
    public void fillList() {
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, RecipeDetailActivity.class);
                intent.putExtra("EXTRA_RECIPE_DETAIL_POSITION", position);
                intent.putExtra("EXTRA_RECIPE_DETAIL_NAME", arrayList.get(position));
                Log.d("INTENT", "Intent created");
                startActivity(intent);
            }
        });
    }

    // Old method used with with SQLite before switching to Firebase
    public void getAll() {
        Cursor res = myDB.getAllData();
        if (res.getCount() == 0) {
            Log.e("NO_ELEMENT", "NO ELEMENTS IN THE DB");
        }
        while (res.moveToNext()) {
            arrayList.add(res.getString(1));
        }
        Log.d(TAG, "getAll() returned: " + arrayList);
    }

    // Method to check is connection is on and working
    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
    // In case it's not notify it through a toast
        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            Toast.makeText(MainActivity.this, "No Internet connection!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
