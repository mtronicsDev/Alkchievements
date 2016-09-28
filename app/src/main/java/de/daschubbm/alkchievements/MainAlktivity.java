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

import java.util.ArrayList;

public class MainAlktivity extends AppCompatActivity {

    private ListView list;

    private Context context;
    private Database database;
    private String name;

    private DatabaseReference myDrinks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_alktivity);

        context = this;

        setupDatabase();
        setupFirebase();
        setupViews();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    myDrinks.child("Spezi").setValue(i + 1);
                }
            }
        }).start();
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
        DatabaseReference beverages = FirebaseDatabase.getInstance().getReference("beverages");

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
        ArrayList<String[]> dummy = new ArrayList<String[]>();
        String[] uno = {"1,00" , "2", "Bier"};
        String[] due = {"0,90" , "1", "Radler"};
        dummy.add(uno);
        dummy.add(due);
        Alkdapter adapter = new Alkdapter(this, R.layout.alk_item, dummy);
        list.setAdapter(adapter);
    }
}
