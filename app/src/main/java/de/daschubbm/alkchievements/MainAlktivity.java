package de.daschubbm.alkchievements;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainAlktivity extends AppCompatActivity {

    private ListView list;
    Alkdapter adapter;

    private Context context;
    private Database database;
    private String name;

    private DatabaseReference myDrinks;

    private Map<String, Float> drinks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_alktivity);

        context = this;

        drinks = new HashMap<>();

        setupDatabase();
        setupFirebase();
    }

    private void setupDatabase() {
        database = new Database(context);
        database.open();

        if (!database.getStatus()) {
            Intent hansl = new Intent(context, LoginAlktivity.class);
            startActivity(hansl);
        } else {
            name = database.getItem(0)[1];
        }
    }

    private void setupFirebase() {
        final DatabaseReference beverages = FirebaseDatabase.getInstance().getReference("beverages");
        beverages.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    drinks.put(child.getKey(), Float.parseFloat(String.valueOf(child.getValue())));
                }

                setupViews();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        myDrinks = FirebaseDatabase.getInstance().getReference("people/" + name);
        myDrinks.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Toast.makeText(context, "Do, " + name + ", dei "
                        + String.valueOf(dataSnapshot.getValue()) + ". "
                        + dataSnapshot.getKey() + "!", Toast.LENGTH_SHORT).show();

                if (adapter != null) {
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setupViews() {
        list = (ListView) findViewById(R.id.alkList);
        ArrayList<String[]> beverages = new ArrayList<>(drinks.size());

        for (Map.Entry<String, Float> drink : drinks.entrySet()) {
            beverages.add(new String[]{String.valueOf(drink.getValue()), "0", drink.getKey()});
        }

        adapter = new Alkdapter(this, R.layout.alk_item, beverages);
        list.setAdapter(adapter);
    }
}
