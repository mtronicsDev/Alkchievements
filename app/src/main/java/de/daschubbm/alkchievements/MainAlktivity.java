package de.daschubbm.alkchievements;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.daschubbm.alkchievements.firebase.ChangeType;
import de.daschubbm.alkchievements.firebase.FirebaseManager;
import de.daschubbm.alkchievements.firebase.ValueChangedCallback;
import de.daschubbm.alkchievements.firebase.ValuePair;
import de.daschubbm.alkchievements.firebase.ValueReadCallback;
import de.daschubbm.alkchievements.util.ConnectivityChecker;

import static de.daschubbm.alkchievements.NumberFormatter.formatPrice;
import static de.daschubbm.alkchievements.util.DataManager.defaultStorage;
import static de.daschubbm.alkchievements.util.DataManager.write;
import static java.lang.Integer.parseInt;

public class MainAlktivity extends AppCompatActivity {

    private static final int[] BUILD_NUMBER = {1, 3, 0, 4};
    private final Map<String, Integer> stock = new HashMap<>();
    private int kastenClicks = 0;
    private Context context;
    private AlkchievementsDatabase alkchievementsDatabase;
    private TimeDatabase timeDatabase;
    private LastPrizesDatabase prizesDatabase;
    private Map<String, Float> drinks;
    private Map<String, Integer> numDrinks;
    //just for shitty explosion
    private MediaPlayer mp;
    private ImageView fassl;
    private ImageView explosion;

    private MainAlkdapter adapter;

    private boolean drinksLoaded = false, numsLoaded = false;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_alktivity);
        getSupportActionBar().setTitle("");

        context = this;

        init();
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        Log.d("HANSL", "dddddd");
        init();
    }*/

    private void init() {
        ConnectivityChecker.checkConnectivity(context);

        drinks = new HashMap<>();
        numDrinks = new HashMap<>();

        setupUserData();

        checkForUpdates();

        setupAlkchivements();
        setupTimeDatabase();
        setupPrizesDatabase();
        setupFirebase();

        setupLayout();

        setupFassl();
        setupKastenKlicks();
    }

    private void setupLayout() {
        ListView listView = (ListView) findViewById(R.id.alkList);

        LayoutInflater flatuleur = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View head = flatuleur.inflate(R.layout.alk_header, null);

        listView.addHeaderView(head);
    }

    private void setupUserData() {
        name = defaultStorage.getString("name", "FOISCH!");

        if (getIntent().getBooleanExtra("LOGIN", false))
            Toast.makeText(context, "Habedere "
                    + name + ", du bist ja aa do!", Toast.LENGTH_LONG).show();
    }

    private void setupPrizesDatabase() {
        prizesDatabase = new LastPrizesDatabase(this);
        prizesDatabase.open();
    }

    private void setupKastenKlicks() {
        int previousKastenKlicks = defaultStorage.getInt("alkchievement.hobbylos.counter", -1);
        if (previousKastenKlicks == -1) {
            write("alkchievement.hobbylos.counter", 0);
            kastenClicks = 0;
        } else kastenClicks = previousKastenKlicks;
    }

    private void checkForUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
                && UpdateDialogProcedure.DOWNLOAD_FILE.exists())
            //noinspection ResultOfMethodCallIgnored
            UpdateDialogProcedure.DOWNLOAD_FILE.delete();

        FirebaseManager.registerCurrentVersionCallback(new ValueReadCallback<ValuePair[]>() {
            @Override
            public void onCallback(ValuePair[] data) {
                checkForUpdates(data);
            }
        }, new ValueChangedCallback() {
            @Override
            public void onCallback(DataSnapshot changedNode, ChangeType changeType) {
                ValuePair[] data = new ValuePair[(int) changedNode.getChildrenCount()];

                int i = 0;
                for (DataSnapshot child : changedNode.getChildren()) {
                    data[i] = new ValuePair(child.getKey(), child.getValue());
                    i++;
                }

                checkForUpdates(data);
            }
        });
    }

    private void checkForUpdates(ValuePair[] values) {
        String buildNumber = "";
        String changelog = null;
        String downloadURL = null;

        for (ValuePair pair : values) {
            switch (pair.key) {
                case "build":
                    buildNumber = String.valueOf(pair.value);
                    break;
                case "changelog":
                    changelog = String.valueOf(pair.value);
                    break;
                case "downloadURL":
                    downloadURL = String.valueOf(pair.value);
                    break;
            }

            if (buildNumber.matches("([0-9]+\\.){3}[0-9]+")
                    && changelog != null
                    && downloadURL != null) {
                Log.d("UPDATE", "Build-nr. valid: " + buildNumber);

                String[] split = buildNumber.split("\\.");

                int[] newestBuild = new int[4];

                for (int i = 0; i < 4; i++) {
                    newestBuild[i] = parseInt(split[i]);
                }

                Log.d("UPDATE", "Integerized build nr.: "
                        + newestBuild[0] + "."
                        + newestBuild[1] + "."
                        + newestBuild[2] + "."
                        + newestBuild[3]);

                if (isLocalBuildOutOfDate(newestBuild)) {
                    Log.d("UPDATE", "New update available");
                    UpdateDialogProcedure.showUpdateDialog(context, buildNumber, changelog, downloadURL);
                }
            }
        }
    }

    private boolean isLocalBuildOutOfDate(int[] newestBuild) {
        return newestBuild[0] > MainAlktivity.BUILD_NUMBER[0]
                || newestBuild[1] > MainAlktivity.BUILD_NUMBER[1]
                || newestBuild[2] > MainAlktivity.BUILD_NUMBER[2]
                || newestBuild[3] > MainAlktivity.BUILD_NUMBER[3];
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater flatuleur = getMenuInflater();
        flatuleur.inflate(R.menu.menu_main, menu);

        return true;
    }

    private void setupAlkchivements() {
        alkchievementsDatabase = new AlkchievementsDatabase(name);
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

    private void setupFirebase() {
        FirebaseManager.registerDrinksCallback(new ValueReadCallback<Map<String, ValuePair[]>>() {
            @Override
            public void onCallback(Map<String, ValuePair[]> data) {
                Log.d("ALKDEB", "Entered drinks callback");
                for (Map.Entry<String, ValuePair[]> child : data.entrySet()) {
                    for (ValuePair pair : child.getValue()) {
                        switch (pair.key) {
                            case "price":
                                drinks.put(child.getKey(), Float.valueOf(String.valueOf(pair.value)));
                                Log.d("OUT", child.getKey());
                                Log.d("OUT", String.valueOf(pair.value));
                                break;
                            case "stock":
                                stock.put(child.getKey(), Integer.valueOf(String.valueOf(pair.value)));
                                break;
                        }
                    }
                }

                drinksLoaded = true;
                if (numsLoaded) setupViews();
            }
        }, new ValueChangedCallback() {
            @Override
            public void onCallback(DataSnapshot changedNode, ChangeType changeType) {
                switch (changeType) {
                    case ADDED:
                    case CHANGED:
                        if (changedNode.child("stock").getValue() != null) {
                            stock.put(changedNode.getKey(),
                                    Integer.valueOf(String.valueOf(changedNode.child("stock").getValue())));

                            if (!drinks.containsKey(changedNode.getKey())) {
                                drinks.put(changedNode.getKey(),
                                        Float.valueOf(String.valueOf(changedNode.child("price").getValue())));
                                setupViews();
                            }
                        }

                        break;
                    case REMOVED:
                        stock.remove(changedNode.getKey());
                        drinks.remove(changedNode.getKey());
                        setupViews();
                        break;
                }

                adapter.updateDrink(changedNode);
            }
        });

        FirebaseManager.registerPersonCallback(new ValueReadCallback<Map<String, ValuePair[]>>() {
            @Override
            public void onCallback(Map<String, ValuePair[]> data) {
                Log.d("ALKDEB", "Person callback basst");

                if (data.get("drinks") == null) {
                    data.put("drinks", new ValuePair[0]);
                    FirebaseDatabase.getInstance()
                            .getReference("people/" + name + "/drinks/Bier").setValue(0);
                }
                Log.d("BUU", "ois guad");

                //de schleife rennt ned durch...
                for (ValuePair pair : data.get("drinks")) {
                    Log.d("BUU", "warum ned?");
                    numDrinks.put(pair.key, Integer.valueOf(String.valueOf(pair.value)));
                    Log.d("OUT2", pair.key);
                    Log.d("OUT2", String.valueOf(pair.value));
                }

                numsLoaded = true;
                if (drinksLoaded) setupViews();

                Log.d("ALKDEB", "Drinks: " + drinksLoaded + " Nums: " + numsLoaded);
            }
        }, new ValueChangedCallback() {
            @Override
            public void onCallback(DataSnapshot changedNode, ChangeType changeType) {
                Log.d("ALKDEB", "Hn<lslsds " + changedNode.getRef().getKey());
                if (changedNode.getRef().getKey().equals("drinks")) {
                    Log.d("ALKDEB", "Hn<fffffff");
                    for (DataSnapshot child : changedNode.getChildren())
                    numDrinks.put(child.getKey(),(int)((long)child.getValue()));
                }
            }
        });

        FirebaseDatabase.getInstance().getReference("people/" + name + "/drinks")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        numDrinks.put(dataSnapshot.getKey(),
                                parseInt(String.valueOf(dataSnapshot.getValue())));
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        numDrinks.put(dataSnapshot.getKey(),
                                parseInt(String.valueOf(dataSnapshot.getValue())));
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

        Log.d("ALKDEB", "Supply wird nun aufgerufen");
        FirebaseManager.supplyPersonName(name);
        Log.d("ALKDEB", "Supply ist abgeschlossen");

        FirebaseManager.writeValue("people/" + name + "/appVersion", assembleBuildNumber());

        FirebaseManager.databaseHandshake();
    }

    private String assembleBuildNumber() {
        return BUILD_NUMBER[0]
                + "." + BUILD_NUMBER[1]
                + "." + BUILD_NUMBER[2]
                + "." + BUILD_NUMBER[3];
    }

    public void checkSum(float prize) {
        synchronized (prizesDatabase) {

            prizesDatabase.newPrize(prize);
            if (prizesDatabase.getStatusFull()) {
                float sum = prizesDatabase.getSum();

                prizesDatabase.notify();
                synchronized (alkchievementsDatabase) {
                    if (sum < 7 && alkchievementsDatabase.getState("sparfuchs") == 0) {
                        alkchievementsDatabase.setState("sparfuchs", 1);
                        Roast.showToast(this, R.drawable.sparfuchs, "Alkchievement erhalten!", "Sparfuchs");
                    }

                    alkchievementsDatabase.notify();
                }
            }
        }
    }

    public void addSessionBeer(boolean add) {
        synchronized (alkchievementsDatabase) {
            if (!(alkchievementsDatabase.getState("bierkenner") == 3)) {
                @SuppressLint("SimpleDateFormat")
                DateFormat df = new SimpleDateFormat("yyyy/MM/dd/HH/mm");
                String date = df.format(Calendar.getInstance().getTime());

                synchronized (timeDatabase) {
                    if (timeDatabase.getItem(0)[1].equals("0")) {
                        timeDatabase.updateValue(0, date);
                        alkchievementsDatabase.addToState("bierkenner", 1);
                        return;
                    }

                    boolean isSession = timeDatabase.getItem(0)[1].split("/")[2].equals(date.split("/")[2]) && parseInt(date.split("/")[3]) - parseInt(timeDatabase.getItem(0)[1].split("/")[3]) < 2;
                    if (!isSession) {
                        boolean newDay = parseInt(date.split("/")[2]) - parseInt(timeDatabase.getItem(0)[1].split("/")[2]) == 1 || date.split("/")[2].equals("01");
                        if (newDay) {
                            if (parseInt(date.split("/")[3]) < 2) {
                                isSession = true;
                            }
                        }
                    }

                    if (isSession && add) {
                        timeDatabase.updateValue(0, date);

                        int state = alkchievementsDatabase.getState("bierkenner");

                        if (state == 0) {
                            alkchievementsDatabase.setState("bierkenner", 1);
                            Roast.showToast(this, R.drawable.bierkenner, "Alkchievement Stufe 1/3 erhalten!",
                                    alkchievementsDatabase.getName("bierkenner"));
                        } else if (state == 1) {
                            alkchievementsDatabase.setState("bierkenner", 2);
                            Roast.showToast(this, R.drawable.bierkenner, "Alkchievement Stufe 2/3 erhalten!",
                                    alkchievementsDatabase.getName("bierkenner"));
                        } else if (state == 2) {
                            alkchievementsDatabase.setState("bierkenner", 3);
                            Roast.showToast(this, R.drawable.bierkenner, "Alkchievement Stufe 3/3 erhalten!",
                                    alkchievementsDatabase.getName("bierkenner"));
                        }
                    }

                    if (isSession && !add) {
                        alkchievementsDatabase.addToState("bierkenner", -1);
                    }

                    if (!isSession && add) {
                        timeDatabase.updateValue(0, date);
                        alkchievementsDatabase.setState("bierkenner", 1);
                    }

                    if (!isSession && !add) {
                        alkchievementsDatabase.setState("bierkenner", 0);
                    }

                    timeDatabase.notify();
                }
            }

            alkchievementsDatabase.notify();
        }
    }

    public void addFollowDay() {
        synchronized (alkchievementsDatabase) {
            if (!(alkchievementsDatabase.getState("stammgast") == 3)) {
                @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy/MM/dd/HH/mm");
                String date = df.format(Calendar.getInstance().getTime());
                String previousDate = defaultStorage.getString("alkchievement.stammgast.lastDate", null);

                int dayStreak = 0;

                if (previousDate == null) {
                    write("alkchievement.stammgast.lastDate", date);
                    write("alkchievement.stammgast.dayStreak", dayStreak = 1);
                } else {
                    int previousStreak = defaultStorage.getInt("alkchievement.stammgast.dayStreak", -1);
                    if (previousStreak == -1) throw new ApplicationFuckedUpError("Foisch!");

                    String[] splitDate = date.split("/");
                    String[] splitPrevDate = previousDate.split("/");

                    int year = parseInt(splitDate[0]);
                    int oldYear = parseInt(splitPrevDate[0]);
                    int month = parseInt(splitDate[1]);
                    int oldMonth = parseInt(splitPrevDate[1]);
                    int day = parseInt(splitDate[2]);
                    int oldDay = parseInt(splitPrevDate[2]);

                    //Folgt der aktuelle auf den letztgespeicherten Tag?
                    if ((year == oldYear && month == oldMonth && day == oldDay + 1)
                            || (year == oldYear && month == oldMonth + 1 && day == 1
                            && lastDayOfMonth(oldDay, oldMonth, oldYear))
                            || (year == oldYear + 1 && oldMonth == 12 && month == 1 && oldDay == 31
                            && day == 1)) {
                        write("alkchievement.stammgast.lastDate", date);
                        write("alkchievement.stammgast.dayStreak", dayStreak = (previousStreak + 1));
                    }
                }


                int state = alkchievementsDatabase.getState("stammgast");
                if (dayStreak >= 3 && state == 0) {
                    alkchievementsDatabase.setState("stammgast", 1);
                    Roast.showToast(this, R.drawable.stammgast, "Alkchievement Stufe 1/3 erhalten!",
                            alkchievementsDatabase.getName("stammgast"));
                } else if (dayStreak >= 5 && state == 1) {
                    alkchievementsDatabase.setState("stammgast", 2);
                    Roast.showToast(this, R.drawable.stammgast, "Alkchievement Stufe 2/3 erhalten!",
                            alkchievementsDatabase.getName("stammgast"));
                } else if (dayStreak >= 7 && state == 2) {
                    alkchievementsDatabase.setState("stammgast", 3);
                    Roast.showToast(this, R.drawable.stammgast, "Alkchievement Stufe 3/3 erhalten!",
                            alkchievementsDatabase.getName("stammgast"));
                }
            }

            alkchievementsDatabase.notify();
        }
    }

    private boolean lastDayOfMonth(int day, int month, int year) {
        if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
            return day == 31;
        } else if (month == 2) {
            if (year % 4 == 0) return day == 29;
            else return day == 28;
        } else return day == 30;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 69:
                UpdateDialogProcedure.checkPermissionsAndDownload(this);
        }
    }

    private boolean sameSession(String date, String lastDate) {
        String[] splitDate = date.split("/");
        String[] splitPrevDate = lastDate.split("/");

        int year = parseInt(splitDate[0]);
        int oldYear = parseInt(splitPrevDate[0]);
        int month = parseInt(splitDate[1]);
        int oldMonth = parseInt(splitPrevDate[1]);
        int day = parseInt(splitDate[2]);
        int oldDay = parseInt(splitPrevDate[2]);
        int hour = parseInt(splitDate[3]);
        int oldHour = parseInt(splitPrevDate[3]);

        return (year == oldYear && month == oldMonth && day == oldDay && hour - oldHour <= 10)
                || (((year == oldYear && month == oldMonth && day == oldDay + 1)
                || (year == oldYear && month == oldMonth + 1 && day == 1
                && lastDayOfMonth(oldDay, oldMonth, oldYear))
                || (year == oldYear + 1 && oldMonth == 12 && month == 1 && oldDay == 31
                && day == 1))
                && (hour + 24 - oldHour <= 10));
    }

    public void addSessionRadler(boolean add) {
        synchronized (alkchievementsDatabase) {
            if (!(alkchievementsDatabase.getState("kegelsportverein") == 1)) {
                @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy/MM/dd/HH");
                String date = df.format(Calendar.getInstance().getTime());
                String lastDate = defaultStorage.getString("alkchievement.kegelsportverein.lastDate", null);

                int streak = defaultStorage.getInt("alkchievement.kegelsportverein.streak", -1);

                if (lastDate == null || !sameSession(date, lastDate)) {
                    write("alkchievement.kegelsportverein.lastDate", date);
                    write("alkchievement.kegelsportverein.streak", streak = (add ? 1 : 0));
                } else {
                    if (streak == -1) throw new ApplicationFuckedUpError("Gäd ned!");

                    if (add) streak++;
                    else streak--;

                    write("alkchievement.kegelsportverein.streak", streak);
                }

                if (streak == 3 && alkchievementsDatabase.getState("kegelsportverein") == 0) {
                    alkchievementsDatabase.setState("kegelsportverein", 1);
                    Roast.showToast(this, R.drawable.kegelsportverein, "Alkchievement erhalten!",
                            alkchievementsDatabase.getName("kegelsportverein"));
                }
            }

            alkchievementsDatabase.notify();
        }
    }

    public void addSessionNonAlk(boolean add) {
        synchronized (alkchievementsDatabase) {
            if (!(alkchievementsDatabase.getState("nullKommaNull") == 1)) {
                @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy/MM/dd/HH");
                String date = df.format(Calendar.getInstance().getTime());
                String lastDate = defaultStorage.getString("alkchievement.nullKommaNull.lastDate", null);

                int streak = defaultStorage.getInt("alkchievement.nullKommaNull.streak", -1);

                if (lastDate == null || !sameSession(date, lastDate)) {
                    write("alkchievement.nullKommaNull.lastDate", date);
                    write("alkchievement.nullKommaNull.streak", streak = (add ? 1 : 0));
                } else {
                    if (streak == -1) throw new ApplicationFuckedUpError("Gäd ned!");

                    if (add) streak++;
                    else streak--;

                    write("alkchievement.nullKommaNull.streak", streak);
                }

                if (streak == 3 && alkchievementsDatabase.getState("nullKommaNull") == 0) {
                    alkchievementsDatabase.setState("nullKommaNull", 1);
                    Roast.showToast(this, R.drawable.null_komma_null, "Alkchievement erhalten!",
                            alkchievementsDatabase.getName("nullKommaNull"));
                }
            }

            alkchievementsDatabase.notify();
        }
    }

    public void addSessionShot(boolean add) {
        synchronized (alkchievementsDatabase) {
            if (!(alkchievementsDatabase.getState("blauWieDasMeer") == 1)) {
                @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy/MM/dd/HH");
                String date = df.format(Calendar.getInstance().getTime());
                String lastDate = defaultStorage.getString("alkchievement.blauWieDasMeer.lastDate", null);

                int streak = defaultStorage.getInt("alkchievement.blauWieDasMeer.streak", -1);

                if (lastDate == null || !sameSession(date, lastDate)) {
                    write("alkchievement.blauWieDasMeer.lastDate", date);
                    write("alkchievement.blauWieDasMeer.streak", streak = (add ? 1 : 0));
                } else {
                    if (streak == -1) throw new ApplicationFuckedUpError("Gäd ned!");

                    if (add) streak++;
                    else streak--;

                    write("alkchievement.blauWieDasMeer.streak", streak);
                }

                if (streak == 3 && alkchievementsDatabase.getState("blauWieDasMeer") == 0) {
                    alkchievementsDatabase.setState("blauWieDasMeer", 1);
                    Roast.showToast(this, R.drawable.blau_wie_das_meer, "Alkchievement erhalten!",
                            alkchievementsDatabase.getName("blauWieDasMeer"));
                }
            }

            alkchievementsDatabase.notify();
        }
    }

    public void addEverBeer(boolean add) {
        synchronized (alkchievementsDatabase) {
            int beerStreak = defaultStorage.getInt("alkchievement.kastenLeer.streak", 0);

            if (add) {
                write("alkchievement.kastenLeer.streak", beerStreak + 1);
                if (beerStreak >= 20 && alkchievementsDatabase.getState("kastenLeer") == 0) {
                    alkchievementsDatabase.setState("kastenLeer", 1);
                    Roast.showToast(this, R.drawable.kasten_leer, "Alkchievement erhalten!",
                            alkchievementsDatabase.getName("kastenLeer"));
                }
            }
            if (!add) {
                write("alkchievement.kastenLeer.streak", beerStreak - 1);
            }

            alkchievementsDatabase.notify();
        }
    }

    public void addStorno() {
        synchronized (alkchievementsDatabase) {
            int wurschtStreak = defaultStorage.getInt("alkchievement.wurstfinger.streak", 0);

            write("alkchievement.wurstfinger.streak", wurschtStreak + 1);
            if (wurschtStreak >= 20 && alkchievementsDatabase.getState("wurstfinger") == 0) {
                alkchievementsDatabase.setState("wurstfinger", 1);
                Roast.showToast(this, R.drawable.wurstfinger, "Alkchievement erhalten!",
                        alkchievementsDatabase.getName("wurstfinger"));
            }

            alkchievementsDatabase.notify();
        }

    }

    public void addClickKasten() {
        synchronized (alkchievementsDatabase) {
            if (kastenClicks < 100 && alkchievementsDatabase.getState("hobbylos") == 0) {
                kastenClicks += 1;

                write("alkchievement.hobbylos.counter", kastenClicks);
                if (kastenClicks >= 100) {
                    alkchievementsDatabase.setState("hobbylos", 1);
                    Roast.showToast(this, R.drawable.hobbylos, "Alkchievement erhalten!",
                            alkchievementsDatabase.getName("hobbylos"));
                }
            }

            alkchievementsDatabase.notify();
        }
    }

    public void updateDrink(String drink, int count) {
        FirebaseManager.writeValue("people/" + name + "/drinks/" + drink, count);
    }

    private void setupViews() {
        Log.d("ALKVIEW", "BAsst");

        List<String[]> drinksData = new ArrayList<>(drinks.size());

        for (String drinkId : drinks.keySet()) {
            Float price = drinks.get(drinkId);

            if (price == null)
                throw new ApplicationFuckedUpError("Preis eines Getränks existiert nicht!");

            if (price < 1000) {
                Integer gschwoabt = numDrinks.get(drinkId);
                Integer stock = this.stock.get(drinkId);

                if (gschwoabt == null) gschwoabt = 0;
                if (stock == null) stock = 0;

                drinksData.add(new String[]{drinkId, formatPrice(price),
                        String.valueOf(gschwoabt), String.valueOf(stock)});
            }
        }

        ListView list = (ListView) findViewById(R.id.alkList);
        adapter = new MainAlkdapter(this, drinksData);
        list.setAdapter(adapter);

        findViewById(R.id.loading).setVisibility(View.GONE);
    }

    public void launchBilling(@SuppressWarnings("UnusedParameters") MenuItem item) {
        Intent hansl = new Intent(context, BillAlktivity.class);
        startActivity(hansl);
    }

    public void launchAchievements(@SuppressWarnings("UnusedParameters") MenuItem item) {
        Intent hansl = new Intent(context, AchievementsAlktivity.class);
        startActivity(hansl);
    }

    public void launchSettings(@SuppressWarnings("UnusedParameters") MenuItem item) {
        PasswordChecker.checkPassword(context, new PasswordChecker.PasswordCheckCallback() {
            @Override
            public void onCallback() {
                Intent hansl = new Intent(context, SettingsAlktivity.class);
                startActivity(hansl);
            }
        });
    }

    public void showFassl() {
        Random random = new Random();

        int layoutWidth = findViewById(R.id.activity_main_alktivity).getMeasuredWidth();
        int layoutHeight = findViewById(R.id.activity_main_alktivity).getMeasuredHeight();
        int fasslWidth = fassl.getMeasuredWidth();
        int fasslHeight = fassl.getMeasuredHeight();

        fassl.setX(random.nextInt(layoutWidth - fasslWidth));
        fassl.setY(random.nextInt(layoutHeight - fasslHeight));
        fassl.setVisibility(View.VISIBLE);
        fassl.bringToFront();
    }

    private void explodeFassl(int posX, int posY) {
        explosion.setImageResource(R.drawable.ex1);
        explosion.setVisibility(View.VISIBLE);
        explosion.bringToFront();

        float explosionWidth = explosion.getMeasuredWidth() - fassl.getMeasuredWidth();
        float explosionHeight = explosion.getMeasuredHeight() - fassl.getMeasuredHeight();
        explosion.setX(posX - explosionWidth / 2);
        explosion.setY(posY - explosionHeight / 2);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        explosion.setImageResource(R.drawable.ex2);
                    }
                },
                100);
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        explosion.setImageResource(R.drawable.ex3);
                    }
                },
                200);
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        explosion.setImageResource(R.drawable.ex4);
                    }
                },
                300);
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        explosion.setVisibility(View.GONE);
                    }
                },
                400);

        if (alkchievementsDatabase.getState("sprengmeister") == 0) {
            alkchievementsDatabase.setState("sprengmeister", 1);
            Roast.showToast(this, R.drawable.sprengmeister, "Alkchievement erhalten!",
                    alkchievementsDatabase.getName("sprengmeister"));
        }
    }

    private void setupFassl() {
        mp = MediaPlayer.create(context, R.raw.explosion);
        fassl = (ImageView) findViewById(R.id.fassl);
        explosion = (ImageView) findViewById(R.id.explosion);
        fassl.setVisibility(View.INVISIBLE);
        explosion.setVisibility(View.INVISIBLE);

        fassl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                explodeFassl((int) fassl.getX(), (int) fassl.getY());
                fassl.setVisibility(View.GONE);
            }
        });
    }
}
