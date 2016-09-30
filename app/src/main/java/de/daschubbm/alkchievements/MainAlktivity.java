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

import static de.daschubbm.alkchievements.NumberFormatter.formatPrice;

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

    //Variables for the Alkchievements
    /**
     * Reihenfolge im Array wie die unten Stehenden Integers
    private int num_beer_session = 0;
    private int evenings_in_row = 0;

    private int num_radler_session = 0;
    private int num_nonalk_session = 0;
    private int num_shots_session = 0;
    private int num_beer_ever = 0;

    private int num_storno = 0;
    private int num_drinks = 0;
    private int num_kasten_clicked = 0;**/

    private int[] varchievements = {0,0,0,0,0,0,0,0,0};

    private String[] alkchievements= {"Armer Schlucker/Erhalte eine Rechnung von über 5€!",
            "Bierkenner/Trinke 2 Bier an einem Abend!",
            "Stammgast/Beschließe 3 Tage in Folge eine Transaktion im Schubbm!",

            "Kegelsportverein/Trinke 5 Radler an einem Abend!",
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
        if (database.getStatus() && !database.getStatusSecond()) {
            setupAlkchievementValues();
        }
        setupAlkchivements();
        loadAlkchievementValues();
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

    private void setupAlkchievementValues() {
        for (int i = 0; i < varchievements.length; i++) {
            database.insertItemIntoDataBase(String.valueOf(i), String.valueOf(varchievements[i]));
        }
    }

    private void loadAlkchievementValues() {
        ArrayList<String[]> varies = database.getItems();
        for(int i = 1; i <= varchievements.length; i++) {
            varchievements[i-1] = Integer.parseInt(varies.get(i)[1]);
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

    public void addEverBeer(boolean add) {
        if (add) {
            varchievements[5] = varchievements[5] + 1;
            database.updateValue(6, varchievements[5]);
            if (varchievements[5] == 20 && alkchievementsDatabase.getItems().get(6)[2].equals("false")) {
                alkchievementsDatabase.changeStatusForItem(6, "true");
                Toast.makeText(context, "Alkchievement erhalten!", Toast.LENGTH_SHORT).show();
            }
        }
        if (!add) {
            varchievements[5] = varchievements[5] - 1;
            database.updateValue(6, varchievements[5]);
        }
    }

    public void addStorno() {
        varchievements[6] = varchievements[6] + 1;
        database.updateValue(7, varchievements[6]);
        if (varchievements[6] == 5) {
            alkchievementsDatabase.changeStatusForItem(10, "true");
            Toast.makeText(context, "Alkchievement erhalten!", Toast.LENGTH_SHORT).show();
        }
    }

    public void addClickKasten() {
        varchievements[8] = varchievements[8] + 1;
        database.updateValue(9, varchievements[8]);
        if (varchievements[8] == 100) {
            alkchievementsDatabase.changeStatusForItem(8, "true");
            Toast.makeText(context, "Alkchievement erhalten!", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateDrink(String drink, int count) {
            myDrinks.child(drink).setValue(count);
    }

    private void setupViews() {
        list = (ListView) findViewById(R.id.alkList);
        ArrayList<String[]> beverages = new ArrayList<>(drinks.size());

        for (Map.Entry<String, Float> drink : drinks.entrySet()) {
            String price = formatPrice(String.valueOf(drink.getValue()));
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
        hansl.putExtra("NAME", name);
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
