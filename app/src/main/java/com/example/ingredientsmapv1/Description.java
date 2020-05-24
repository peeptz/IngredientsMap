package com.example.ingredientsmapv1;

import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            name = extras.getString("EXTRA_RECIPE_DETAIL_NAME");
        }
        getDescription();
    }

    // Getting the steps of how to make the selected recipe
    public void getDescription() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Descriptions/" + name);
        Query query = reference.orderByChild(name);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    prep = prep + child.getValue();
                }
                setContentView(R.layout.preparation);
                TextView preparation = (TextView) findViewById (R.id.textView);
                TextView recipeName = (TextView) findViewById (R.id.textView2);
                recipeName.setText(name);
                preparation.setText(prep.replaceAll("[\\[\\](){}]",""));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }
}
