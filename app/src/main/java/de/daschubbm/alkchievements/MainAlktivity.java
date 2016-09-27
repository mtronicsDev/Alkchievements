package de.daschubbm.alkchievements;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

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
        setupViews();
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
