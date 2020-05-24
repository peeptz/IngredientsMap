package com.example.ingredientsmapv1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class RecipeDetailActivity extends AppCompatActivity {

    ListView listView;
    private ArrayList<String> arrayList = new ArrayList<>();
    int r;
    private String name = " ";
    int LAUNCH_SECOND_ACTIVITY = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("ON CREATE", "onCreate: onCreate called for Activity RecipeDetail");
        super.onCreate(savedInstanceState);
        // Getting information from previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int position = extras.getInt("EXTRA_RECIPE_DETAIL_POSITION");
            name = extras.getString("EXTRA_RECIPE_DETAIL_NAME");
            r = position;
        }

        setContentView(R.layout.detail_recipe);
        TextView recipeName = (TextView) findViewById (R.id.recipeName);
        recipeName.setText(name);
        // used before switching to Firebase
        // myDB = new DBOpenHelper(this);
        listView =(ListView) findViewById(R.id.listviewIngredients);
        getAll(name);
        getImages(name);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);
    }

    // Getting from Firebase all ingredients of a given recipe
    public void getAll(String name) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ingredients/" + name);
        Query query = reference.orderByChild(name);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String ingredient = child.getValue().toString();
                    arrayList.add(ingredient);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    // Getting image for selected recipe
    public void getImages(String name) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("images/" + name);
        Query query = reference.orderByChild(name);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("RESULT IMAGES", "onDataChange: " + dataSnapshot.getValue());
                String imageUrl;
                if (dataSnapshot.getValue() != null) {
                    imageUrl = dataSnapshot.getValue().toString();
                } else {
                    // Image saying that the other wasn't found
                    imageUrl = "https://www.genesismobile.it/wp-content/themes/genesismobile/images/no-image/No-Image-Found-400x264.png";
                }
                new DownloadImageTask((ImageView) findViewById(R.id.imageView))
                        .execute(imageUrl);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    // This allows to download images
    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    // Action triggered when 'Order' button is clicked
    public void foodPosition (View v) {
        Intent intent = new Intent(RecipeDetailActivity.this, MapsActivity.class);
        intent.putExtra("EXTRA_RECIPE_DETAIL_NAME", name);
        intent.putExtra("EXTRA_RECIPE_INGREDIENTS", (Serializable)arrayList);
        startActivityForResult(intent, LAUNCH_SECOND_ACTIVITY);
    }

    // Action triggered when 'Steps' button is clicked
    public void goToSteps(View v) {
        Intent intent = new Intent(RecipeDetailActivity.this, Description.class);
        intent.putExtra("EXTRA_RECIPE_DETAIL_NAME", name);
        Log.d("INTENT", "Intent created");
        startActivity(intent);
    }
}
