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

public class RecipeDetailActivity extends AppCompatActivity {

    DBOpenHelper myDB;
    ListView listView;
    final ArrayList<String> arrayList = new ArrayList<>();
    int r;
    String name = " ";
    int LAUNCH_SECOND_ACTIVITY = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("ON CREATE", "onCreate: onCreate called for Activity RecipeDetail");
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int position = extras.getInt("EXTRA_RECIPE_DETAIL_POSITION");
            name = extras.getString("EXTRA_RECIPE_DETAIL_NAME");
            r = position;
        }

        setContentView(R.layout.detail_recipe);
        TextView recipeName = (TextView) findViewById (R.id.recipeName);
        recipeName.setText(name);
        myDB = new DBOpenHelper(this);
        listView =(ListView) findViewById(R.id.listviewIngredients);

        getAll(name);

        switch(name) {
            case "Carbonara":
                new DownloadImageTask((ImageView) findViewById(R.id.imageView))
                        .execute("https://static01.nyt.com/images/2018/08/10/dining/carbonara-horizontal/carbonara-horizontal-articleLarge.jpg");
                break;
            case "Tortino al Cioccolato":
                new DownloadImageTask((ImageView) findViewById(R.id.imageView))
                        .execute("https://www.cucchiaio.it/content/cucchiaio/it/ricette/2016/02/tortino-al-cioccolato-con-cuore-morbido/jcr:content/header-par/image-single.img10.jpg/1456741969164.jpg");
                break;
            case "Pizza Quattro Formaggi":
                new DownloadImageTask((ImageView) findViewById(R.id.imageView))
                        .execute("https://i2.wp.com/www.piccolericette.net/piccolericette/wp-content/uploads/2017/06/3234_Pizza.jpg");
                break;
            default:
                Log.d("NO_IMAGE", "onCreate() returned: ");
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);
    }

    public void getAll(String name) {
        Log.d("GET ALL", "getAll: getting all " +  name);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ingredients/" + name);
        Log.d("NAME", "getAll: " + name);
        Query query = reference.orderByChild(name);
        Log.d("Query", "getAll:" +  query);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("RESULT", "onDataChange: " + dataSnapshot.getValue());
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String ingredient = child.getValue().toString();
                    arrayList.add(ingredient);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void foodPosition (View v) {
        Intent intent = new Intent(RecipeDetailActivity.this, MapsActivity.class);
        intent.putExtra("EXTRA_RECIPE_DETAIL_NAME", name);
        intent.putExtra("EXTRA_RECIPE_INGREDIENTS", (Serializable)arrayList);
        startActivityForResult(intent, LAUNCH_SECOND_ACTIVITY);
    }

    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
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

    public void goToSteps(View v) {
        Intent intent = new Intent(RecipeDetailActivity.this, Description.class);
        intent.putExtra("EXTRA_RECIPE_DETAIL_NAME", name);
        Log.d("INTENT", "Intent created");
        startActivity(intent);
    }
}
