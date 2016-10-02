package de.daschubbm.alkchievements;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    private TimeDatabase timeDatabase;
    private LastPrizesDatabase prizesDatabase;

    private DatabaseReference myDrinks;

    private Map<String, Float> drinks;
    private Map<String, Integer> numDrinks;

    private boolean drinksLoaded = false, numsLoaded = false;

    //Variables for the Alkchievements
    /**
     * Reihenfolge im Array wie die unten Stehenden Integers
     *
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

        setupTimeDatabase();
        prizesDatabase = new LastPrizesDatabase(this);
        prizesDatabase.open();

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

    private void setupTimeDatabase() {
        timeDatabase = new TimeDatabase(this);
        timeDatabase.open();
        if (!timeDatabase.getStatus()) {
            timeDatabase.insertItemIntoDataBase("num_beer_session", "0");
            timeDatabase.insertItemIntoDataBase("num_radler_session", "0");
            timeDatabase.insertItemIntoDataBase("num_nonalk_session", "0");
            timeDatabase.insertItemIntoDataBase("num_shots_session", "0");
            timeDatabase.insertItemIntoDataBase("last_buy", "0");
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

    public void checkSum(float prize) {
        prizesDatabase.newPrize(prize);
        if(prizesDatabase.getStatusFull()) {
            float sum = prizesDatabase.getSum();
            if (sum < 7 && alkchievementsDatabase.getItems().get(9)[2].equals("false")) {
                alkchievementsDatabase.changeStatusForItem(9, "true");
                Toast.makeText(context, "Alkchievement erhalten!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void addSessionBeer(boolean add) {
        if (!alkchievementsDatabase.getItems().get(1)[2].equals("3")) {
            DateFormat df = new SimpleDateFormat("yyyy/MM/dd/HH/mm");
            String date = df.format(Calendar.getInstance().getTime());

            if (timeDatabase.getItem(0)[1].equals("0")) {
                timeDatabase.updateValue(0, date);
                varchievements[0] = varchievements[0] + 1;
                database.updateValue(1, varchievements[0]);
                return;
            }

            boolean isSession = timeDatabase.getItem(0)[1].split("/")[2].equals(date.split("/")[2]) && Integer.parseInt(date.split("/")[3]) - Integer.parseInt(timeDatabase.getItem(0)[1].split("/")[3]) < 2;
            if (!isSession) {
                boolean newDay = Integer.parseInt(date.split("/")[2]) - Integer.parseInt(timeDatabase.getItem(0)[1].split("/")[2]) == 1 || date.split("/")[2].equals("01");
                if (newDay) {
                    if (Integer.parseInt(date.split("/")[3]) < 2) {
                        isSession = true;
                    }
                }
            }

            if (isSession && add) {
                timeDatabase.updateValue(0, date);
                varchievements[0] = varchievements[0] + 1;
                database.updateValue(1, varchievements[0]);

                if (varchievements[0] == 2 && alkchievementsDatabase.getItems().get(1)[2].equals("false")) {
                    alkchievementsDatabase.changeStatusForItem(1, "1");
                    Toast.makeText(context, "Alkchievement erhalten!", Toast.LENGTH_SHORT).show();
                }
                if (varchievements[0] == 4 && alkchievementsDatabase.getItems().get(1)[2].equals("1")) {
                    alkchievementsDatabase.changeStatusForItem(1, "2");
                    Toast.makeText(context, "Alkchievement erhalten!", Toast.LENGTH_SHORT).show();
                    alkchievementsDatabase.changeDescriptionForItem(1, "Trinke 4 Bier an einem Abend!");
                }
                if (varchievements[0] == 6 && alkchievementsDatabase.getItems().get(1)[2].equals("2")) {
                    alkchievementsDatabase.changeStatusForItem(1, "3");
                    Toast.makeText(context, "Alkchievement erhalten!", Toast.LENGTH_SHORT).show();
                    alkchievementsDatabase.changeDescriptionForItem(1, "Trinke 6 Bier an einem Abend!");
                }
            }

            if (isSession && !add) {
                varchievements[0] = varchievements[0] - 1;
                database.updateValue(1, varchievements[0]);
            }

            if (!isSession && add) {
                timeDatabase.updateValue(0, date);
                varchievements[0] = 1;
                database.updateValue(1, varchievements[0]);
            }

            if (!isSession && !add) {
                varchievements[0] = 0;
                database.updateValue(1, varchievements[0]);
            }
        }
    }

    public void addFollowDay() {
        if (!alkchievementsDatabase.getItems().get(2)[2].equals("3")) {
            DateFormat df = new SimpleDateFormat("yyyy/MM/dd/HH/mm");
            String date = df.format(Calendar.getInstance().getTime());

            if (timeDatabase.getItem(4)[1].equals("0")) {
                timeDatabase.updateValue(4, date);
                varchievements[1] = varchievements[1] + 1;
                database.updateValue(2, varchievements[1]);
                return;
            }

            boolean nextDay = Integer.parseInt(date.split("/")[2]) - Integer.parseInt(timeDatabase.getItem(4)[1].split("/")[2]) == 1;
            if (!nextDay) {
                String last = timeDatabase.getItem(4)[1].split("/")[2];
                boolean newMonth = date.split("/")[2].equals("01") && (last.equals("28") || last.equals("29") || last.equals("30") || last.equals("31"));
                if (newMonth) {
                    nextDay = true;
                }
            }

            if (nextDay) {
                timeDatabase.updateValue(4, date);
                varchievements[1] = varchievements[1] + 1;
                database.updateValue(2, varchievements[1]);

                if (varchievements[1] == 3 && alkchievementsDatabase.getItems().get(2)[2].equals("false")) {
                    alkchievementsDatabase.changeStatusForItem(2, "1");
                    Toast.makeText(context, "Alkchievement erhalten!", Toast.LENGTH_SHORT).show();
                }
                if (varchievements[1] == 5 && alkchievementsDatabase.getItems().get(2)[2].equals("1")) {
                    alkchievementsDatabase.changeStatusForItem(2, "2");
                    Toast.makeText(context, "Alkchievement erhalten!", Toast.LENGTH_SHORT).show();
                    alkchievementsDatabase.changeDescriptionForItem(1, "Beschließe 5 Tage in Folge eine Transaktion im Schubbm!");
                }
                if (varchievements[1] == 7 && alkchievementsDatabase.getItems().get(2)[2].equals("2")) {
                    alkchievementsDatabase.changeStatusForItem(2, "3");
                    Toast.makeText(context, "Alkchievement erhalten!", Toast.LENGTH_SHORT).show();
                    alkchievementsDatabase.changeDescriptionForItem(1, "Beschließe 7 Tage in Folge eine Transaktion im Schubbm!");
                }
            }
        }
    }

    public void addSessionRadler(boolean add) {
        if (!alkchievementsDatabase.getItems().get(3)[2].equals("true")) {
            DateFormat df = new SimpleDateFormat("yyyy/MM/dd/HH/mm");
            String date = df.format(Calendar.getInstance().getTime());

            if (timeDatabase.getItem(1)[1].equals("0")) {
                timeDatabase.updateValue(1, date);
                varchievements[2] = varchievements[2] + 1;
                database.updateValue(3, varchievements[2]);
                return;
            }

            boolean isSession = timeDatabase.getItem(1)[1].split("/")[2].equals(date.split("/")[2]) && Integer.parseInt(date.split("/")[3]) - Integer.parseInt(timeDatabase.getItem(1)[1].split("/")[3]) < 2;
            if (!isSession) {
                boolean newDay = Integer.parseInt(date.split("/")[2]) - Integer.parseInt(timeDatabase.getItem(1)[1].split("/")[2]) == 1 || date.split("/")[2].equals("01");
                if (newDay) {
                    if (Integer.parseInt(date.split("/")[3]) < 2) {
                        isSession = true;
                    }
                }
            }

            if (isSession && add) {
                timeDatabase.updateValue(1, date);
                varchievements[2] = varchievements[2] + 1;
                database.updateValue(3, varchievements[2]);

                if (varchievements[2] == 5 && alkchievementsDatabase.getItems().get(3)[2].equals("false")) {
                    alkchievementsDatabase.changeStatusForItem(3, "true");
                    Toast.makeText(context, "Alkchievement erhalten!", Toast.LENGTH_SHORT).show();
                }
            }

            if (isSession && !add) {
                varchievements[2] = varchievements[2] - 1;
                database.updateValue(3, varchievements[2]);
            }

            if (!isSession && add) {
                timeDatabase.updateValue(1, date);
                varchievements[2] = 1;
                database.updateValue(3, varchievements[2]);
            }

            if (!isSession && !add) {
                varchievements[2] = 0;
                database.updateValue(3, varchievements[2]);
            }
        }
    }

    public void addSessionNonAlk(boolean add) {
        if (!alkchievementsDatabase.getItems().get(4)[2].equals("true")) {
            DateFormat df = new SimpleDateFormat("yyyy/MM/dd/HH/mm");
            String date = df.format(Calendar.getInstance().getTime());

            if (timeDatabase.getItem(2)[1].equals("0")) {
                timeDatabase.updateValue(2, date);
                varchievements[3] = varchievements[3] + 1;
                database.updateValue(4, varchievements[3]);
                return;
            }

            boolean isSession = timeDatabase.getItem(2)[1].split("/")[2].equals(date.split("/")[2]) && Integer.parseInt(date.split("/")[3]) - Integer.parseInt(timeDatabase.getItem(2)[1].split("/")[3]) < 2;
            if (!isSession) {
                boolean newDay = Integer.parseInt(date.split("/")[2]) - Integer.parseInt(timeDatabase.getItem(2)[1].split("/")[2]) == 1 || date.split("/")[2].equals("01");
                if (newDay) {
                    if (Integer.parseInt(date.split("/")[3]) < 2) {
                        isSession = true;
                    }
                }
            }

            if (isSession && add) {
                timeDatabase.updateValue(2, date);
                varchievements[3] = varchievements[3] + 1;
                database.updateValue(4, varchievements[3]);

                if (varchievements[3] == 5 && alkchievementsDatabase.getItems().get(4)[2].equals("false")) {
                    alkchievementsDatabase.changeStatusForItem(4, "true");
                    Toast.makeText(context, "Alkchievement erhalten!", Toast.LENGTH_SHORT).show();
                }
            }

            if (isSession && !add) {
                varchievements[3] = varchievements[3] - 1;
                database.updateValue(4, varchievements[3]);
            }

            if (!isSession && add) {
                timeDatabase.updateValue(2, date);
                varchievements[3] = 1;
                database.updateValue(4, varchievements[3]);
            }

            if (!isSession && !add) {
                varchievements[3] = 0;
                database.updateValue(4, varchievements[3]);
            }
        }
    }

    public void addSessionShot(boolean add) {
        if (!alkchievementsDatabase.getItems().get(5)[2].equals("true")) {
            DateFormat df = new SimpleDateFormat("yyyy/MM/dd/HH/mm");
            String date = df.format(Calendar.getInstance().getTime());

            if (timeDatabase.getItem(3)[1].equals("0")) {
                timeDatabase.updateValue(3, date);
                varchievements[4] = varchievements[4] + 1;
                database.updateValue(5, varchievements[4]);
                return;
            }

            boolean isSession = timeDatabase.getItem(3)[1].split("/")[2].equals(date.split("/")[2]) && Integer.parseInt(date.split("/")[3]) - Integer.parseInt(timeDatabase.getItem(3)[1].split("/")[3]) < 2;
            if (!isSession) {
                boolean newDay = Integer.parseInt(date.split("/")[2]) - Integer.parseInt(timeDatabase.getItem(3)[1].split("/")[2]) == 1 || date.split("/")[2].equals("01");
                if (newDay) {
                    if (Integer.parseInt(date.split("/")[3]) < 2) {
                        isSession = true;
                    }
                }
            }

            if (isSession && add) {
                timeDatabase.updateValue(3, date);
                varchievements[4] = varchievements[4] + 1;
                database.updateValue(5, varchievements[4]);

                if (varchievements[4] == 5 && alkchievementsDatabase.getItems().get(5)[2].equals("false")) {
                    alkchievementsDatabase.changeStatusForItem(5, "true");
                    Toast.makeText(context, "Alkchievement erhalten!", Toast.LENGTH_SHORT).show();
                }
            }

            if (isSession && !add) {
                varchievements[4] = varchievements[4] - 1;
                database.updateValue(5, varchievements[4]);
            }

            if (!isSession && add) {
                timeDatabase.updateValue(3, date);
                varchievements[4] = 1;
                database.updateValue(5, varchievements[4]);
            }

            if (!isSession && !add) {
                varchievements[4] = 0;
                database.updateValue(5, varchievements[4]);
            }
        }
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

        findViewById(R.id.loading).setVisibility(View.GONE);
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
