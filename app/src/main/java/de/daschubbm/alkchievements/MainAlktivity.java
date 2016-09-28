package de.daschubbm.alkchievements;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainAlktivity extends AppCompatActivity {

    private ImageView beer;
    private ImageView wheat;
    private ImageView almi;
    private ImageView biker;
    private ImageView spezi;
    private ImageView water;
    private ImageView shot;

    private int num_beer = 0;
    private int num_wheat = 0;
    private int num_almi = 0;
    private int num_biker = 0;
    private int num_spezi = 0;
    private int num_water = 0;
    private int num_shot = 0;

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
        /*beer = (ImageView) findViewById(R.id.add_beer);
        wheat = (ImageView) findViewById(R.id.add_wheat);
        almi = (ImageView) findViewById(R.id.add_almi);
        biker = (ImageView) findViewById(R.id.add_biker);
        spezi = (ImageView) findViewById(R.id.add_spezi);
        water = (ImageView) findViewById(R.id.add_water);
        shot = (ImageView) findViewById(R.id.add_shot);

        beer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num_beer++;
                Toast toast = Toast.makeText(context, "Prost auf dein " + String.valueOf(num_beer) + ". Bier heute Abend!", Toast.LENGTH_LONG);
                toast.show();
            }
        });

        wheat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num_wheat++;
                Toast toast = Toast.makeText(context, "Prost auf dein " + String.valueOf(num_wheat) + ". Weizen heute Abend!", Toast.LENGTH_LONG);
                toast.show();
            }
        });

        almi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num_almi++;
                Toast toast = Toast.makeText(context, "Es dudle dein " + String.valueOf(num_almi) + ". Almi heute!", Toast.LENGTH_LONG);
                toast.show();
            }
        });

        biker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num_biker++;
                Toast toast = Toast.makeText(context, "Auch das " + String.valueOf(num_biker) + ". Radler ist noch kein Alkohol!", Toast.LENGTH_LONG);
                toast.show();
            }
        });

        spezi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num_spezi++;
                Toast toast = Toast.makeText(context, "Du hast heute Abend schon " + String.valueOf(num_spezi) + " neue Spezis gefunden!", Toast.LENGTH_LONG);
                toast.show();
            }
        });

        water.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num_water++;
                Toast toast = Toast.makeText(context, "Es gibt nur " + String.valueOf(num_water) + " Wasser, Wasser, Wasser überall!", Toast.LENGTH_LONG);
                toast.show();
            }
        });

        shot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num_shot++;
                Toast toast = Toast.makeText(context, "Hau weg! Der " + String.valueOf(num_shot) + ". Shot ballert!", Toast.LENGTH_LONG);
                toast.show();
            }
        });*/
    }
}
