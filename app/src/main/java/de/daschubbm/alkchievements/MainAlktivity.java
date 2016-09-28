package de.daschubbm.alkchievements;

import android.content.Context;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_alktivity);

        context = this;

        setupFirebase();
        setupViews();
    }

    private void setupFirebase() {
        DatabaseReference people = FirebaseDatabase.getInstance().getReference("people/Maxl/");
        people.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String beverage = dataSnapshot.getKey();
                int amount = Integer.parseInt(String.valueOf(dataSnapshot.getValue()));

                switch (beverage) {
                    case "Bier":
                        num_beer = amount;
                        break;
                    case "Radler":
                        num_biker = amount;
                        break;
                    case "Spezi":
                        num_spezi = amount;
                        break;
                }

                Toast.makeText(context, "Das ist dein " + amount + ". " + beverage + " heute!", Toast.LENGTH_SHORT).show();
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
        beer = (ImageView) findViewById(R.id.add_beer);
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
        });
    }
}
