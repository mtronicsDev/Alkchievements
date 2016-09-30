package de.daschubbm.alkchievements;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    private Alkdapter adapter;

    private Context context;
    private Database database;
    private String name;

    private AlkchievementsDatabase alkchievementsDatabase;

    private DatabaseReference myDrinks;

    private Map<String, Float> drinks;
    private Map<String, Integer> numDrinks;

    private boolean drinksLoaded = false, numsLoaded = false;

    private String[] alkchievements= {"Armer Schlucker/Erhalte eine Rechnung von über 5€!",
            "Bierkenner/Trinke 2 Bier an einem Abend!",
            "Stammgast/Beschließe 3 Tage in Folge eine Transaktion im Schubbm!",
            "Kegelsportverein/Trinke 5 Radler an einemAbend!",
            "0,0/Trinke 5 antialkoholische Getränke an einem Abend!",
            "Blau wie das Meer/Trinke 5 Shots an einem Abend!",
            "Kasten leer/Trinke insgesamt 20 Bier",
            "Schuldner Nr. 1/Erreiche die höchste Summe auf der gesamten Rechnung!",
            "Hobbylos/Drücke 100 mal auf einen Kasten!",
            "Sparfuchs/Bleibe bei 10 Getränken bei unter 7 €!",
            "Wurschtfinger/Storniere 5 Getränkbestellungen!"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_alktivity);

        context = this;

        drinks = new HashMap<>();
        numDrinks = new HashMap<>();

        setupDatabase();
        setupAlkchivements();
        setupFirebase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater flatuleur = getMenuInflater();
        flatuleur.inflate(R.menu.menu_main, menu);

        return true;
    }

    private void setupAlkchivements() {
        alkchievementsDatabase = new AlkchievementsDatabase(this);
        alkchievementsDatabase.open();
        if (!alkchievementsDatabase.getStatus()) {
            ArrayList<String[]> alkis = new ArrayList<String[]>();
            for (int i = 0; i < alkchievements.length; i++) {
                alkis.add(alkchievements[i].split("/"));
            }
            for (int i = 0; i < alkis.size(); i++) {
                alkchievementsDatabase.insertItemIntoDataBase(alkis.get(i)[0], alkis.get(i)[1], "false");
            }
        }
    }

    private void setupDatabase() {
        database = new Database(context);
        database.open();

        if (!database.getStatus()) {
            Intent hansl = new Intent(context, LoginAlktivity.class);
            startActivity(hansl);
        } else {
            name = database.getItem(0)[1];
            getSupportActionBar().setTitle(name);
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

                drinksLoaded = true;
                if (numsLoaded) setupViews();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        myDrinks = FirebaseDatabase.getInstance().getReference("people/" + name);
        myDrinks.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    numDrinks.put(child.getKey(), Integer.parseInt(String.valueOf(child.getValue())));
                }

                numsLoaded = true;
                if (drinksLoaded) setupViews();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        myDrinks.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                numDrinks.put(dataSnapshot.getKey(),
                        Integer.parseInt(String.valueOf(dataSnapshot.getValue())));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Toast.makeText(context, "Do, " + name + ", dei "
                        + String.valueOf(dataSnapshot.getValue()) + ". "
                        + dataSnapshot.getKey() + "!", Toast.LENGTH_SHORT).show();
                numDrinks.put(dataSnapshot.getKey(),
                        Integer.parseInt(String.valueOf(dataSnapshot.getValue())));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                numDrinks.remove(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updateDrink(String drink, int count) {
        myDrinks.child(drink).setValue(count);
    }

    private void setupViews() {
        list = (ListView) findViewById(R.id.alkList);
        ArrayList<String[]> beverages = new ArrayList<>(drinks.size());

        for (Map.Entry<String, Float> drink : drinks.entrySet()) {
            String price = String.valueOf(drink.getValue());
            String name = drink.getKey();
            String count = String.valueOf(numDrinks.get(drink.getKey()));

            if (count.equals("null")) count = "0";

            beverages.add(new String[]{price, count, name});
        }

        adapter = new Alkdapter(this, R.layout.alk_item, beverages);
        list.setAdapter(adapter);
    }

    public void launchBilling(MenuItem item) {
        Intent hansl = new Intent(context, BillAlktivity.class);
        startActivity(hansl);
    }

    public void launchAchievements(MenuItem item) {
        Intent hansl = new Intent(context, AchievementsAlktivity.class);
        startActivity(hansl);
    }

    public void launchSettings(MenuItem item) {
        Intent hansl = new Intent(context, SettingsAlktivity.class);
        startActivity(hansl);
    }
}
