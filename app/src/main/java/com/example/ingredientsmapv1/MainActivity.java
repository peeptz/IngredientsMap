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
    ListView listView;
    final ArrayList<String> arrayList = new ArrayList<>();
    final ArrayList<String> r = new ArrayList<>();
    Map<String, List<String>> hm = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("ON CREATE", "onCreate: onCreate called");
        Log.d("ONLINE?", "onCreate: " + isOnline());
        if (isOnline()) {
            final DatabaseReference recipeRef = FirebaseDatabase.getInstance().getReference("ingredients");
            recipeRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("TEST", "onDataChange() returned: " + dataSnapshot.getValue());
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        arrayList.add(child.getKey());
                        ArrayList r = (ArrayList) child.getValue();
                        hm.put(child.getKey(), r);
                    }
                    Log.d(TAG, "onDataChange: " + hm);
                    fillList();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }

        super.onCreate(savedInstanceState);
//            DBAdapter dbAdapter = DBAdapter.getInstance(this);
        setContentView(R.layout.activity_main);
//            myDB = new DBOpenHelper(this);
        listView =(ListView) findViewById(R.id.listview);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        getSupportActionBar().setLogo(R.mipmap.ic_launcher_round);
//        getSupportActionBar().setDisplayUseLogoEnabled(true);




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

//        getAll();
//        arrayList.add("Pasta Carbonara");
//        arrayList.add("Pizza Quattro Stagioni");
//        arrayList.add("Tortino al cioccolato");
//        arrayList.add("DIOPORCO");
    }

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

    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            Toast.makeText(MainActivity.this, "No Internet connection!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
