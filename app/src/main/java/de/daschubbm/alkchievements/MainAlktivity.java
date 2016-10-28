package de.daschubbm.alkchievements;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.daschubbm.alkchievements.firebase.ChangeType;
import de.daschubbm.alkchievements.firebase.FirebaseManager;
import de.daschubbm.alkchievements.firebase.ValueChangedCallback;
import de.daschubbm.alkchievements.firebase.ValuePair;
import de.daschubbm.alkchievements.firebase.ValueReadCallback;

import static de.daschubbm.alkchievements.NumberFormatter.formatPrice;

public class MainAlktivity extends AppCompatActivity {

    private static final int[] BUILD_NUMBER = {1, 3, 0, 3};
    private static int UNIMPORTANT_VARIABLE = -1;
    int kastenClicks = 0;
    private ListView list;
    private Alkdapter adapter;
    private Context context;
    private Database database;
    private String name;
    private AlkchievementsDatabase alkchievementsDatabase;
    private TimeDatabase timeDatabase;
    private LastPrizesDatabase prizesDatabase;
    private Map<String, Float> drinks;
    private Map<String, Integer> numDrinks;
    private Map<String, Integer> stock = new HashMap<>();
    //just for shitty explosion
    private MediaPlayer mp;
    private ImageView fassl;
    private ImageView explosion;
    private boolean drinksLoaded = false, numsLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_alktivity);

        context = this;

        ConnectivityChecker.checkConnectivity(context);

        drinks = new HashMap<>();
        numDrinks = new HashMap<>();

        checkForUpdates();

        retrieveAdminPassword();

        if (!setupDatabase()) {
            setupAlkchivements();

            setupTimeDatabase();
            prizesDatabase = new LastPrizesDatabase(this);
            prizesDatabase.open();

            setupFirebase();
            performUpdateCleanup();

            setupExplosion();
            kastenClicks = Integer.parseInt(database.getItem(8)[1]);
        }
    }

    private void performUpdateCleanup() {
        FirebaseDatabase.getInstance().getReference("people/" + name)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean cleanedUp = false;

                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            String key = child.getKey();
                            if (!key.equals("drinks") && !key.equals("achievements") && !key.equals("appVersion")) {
                                //Needs cleanup
                                FirebaseManager.writeValue("people/" + name + "/drinks/" + key, child.getValue());
                                child.getRef().removeValue();

                                numDrinks.put(key, Integer.valueOf(String.valueOf(child.getValue())));
                                cleanedUp = true;
                            }
                        }

                        if (cleanedUp) setupViews();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void retrieveAdminPassword() {
        FirebaseManager.registerAdminPasswordCallback(new ValueReadCallback<Integer>() {
            @Override
            public void onCallback(Integer data) {
                UNIMPORTANT_VARIABLE = data;
            }
        }, new ValueChangedCallback() {
            @Override
            public void onCallback(DataSnapshot changedNode, ChangeType changeType) {
                Integer newPassword = Integer.valueOf(String.valueOf(changedNode.getValue()));
                if (newPassword != null) UNIMPORTANT_VARIABLE = newPassword;
            }
        });
    }

    private void checkForUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
                && UpdateDialogProcedure.DOWNLOAD_FILE.exists())
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
                    newestBuild[i] = Integer.parseInt(split[i]);
                }

                Log.d("UPDATE", "Integerized build nr.: "
                        + newestBuild[0] + "."
                        + newestBuild[1] + "."
                        + newestBuild[2] + "."
                        + newestBuild[3]);

                if (isLocalBuildOutOfDate(BUILD_NUMBER, newestBuild)) {
                    Log.d("UPDATE", "New update available");
                    UpdateDialogProcedure.showUpdateDialog(context, buildNumber, changelog, downloadURL);
                }
            }
        }
    }

    private boolean isLocalBuildOutOfDate(int[] localBuild, int[] newestBuild) {
        if (newestBuild[0] > localBuild[0]) return true;
        if (newestBuild[1] > localBuild[1]) return true;
        if (newestBuild[2] > localBuild[2]) return true;
        return newestBuild[3] > localBuild[3];
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater flatuleur = getMenuInflater();
        flatuleur.inflate(R.menu.menu_main, menu);

        return true;
    }

    private void setupAlkchivements() {
        alkchievementsDatabase = new AlkchievementsDatabase(name);

        database.insertItemIntoDataBase("dummy", "-1"); //1
        database.insertItemIntoDataBase("stammgast", "0"); //2
        database.insertItemIntoDataBase("kegelsportverein", "0"); //3
        database.insertItemIntoDataBase("nullKommaNull", "0"); //4
        database.insertItemIntoDataBase("blauWieDasMeer", "0"); //5
        database.insertItemIntoDataBase("kastenLeer", "0"); //6
        database.insertItemIntoDataBase("wurstfinger", "0"); //7
        database.insertItemIntoDataBase("hobbylos", "0"); //8
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

    private boolean setupDatabase() {
        database = new Database(context);
        database.open();

        if (!database.getStatus()) {
            Intent hansl = new Intent(context, LoginAlktivity.class);
            startActivity(hansl);
            finish();
            return true;
        } else {
            name = database.getItem(0)[1];
            getSupportActionBar().setTitle(name);
            return false;
        }
    }

    private void setupFirebase() {
        FirebaseManager.registerDrinksCallback(new ValueReadCallback<Map<String, ValuePair[]>>() {
            @Override
            public void onCallback(Map<String, ValuePair[]> data) {
                for (Map.Entry<String, ValuePair[]> child : data.entrySet()) {
                    for (ValuePair pair : child.getValue()) {
                        switch (pair.key) {
                            case "price":
                                drinks.put(child.getKey(), Float.valueOf(String.valueOf(pair.value)));
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
            }
        });

        FirebaseManager.registerPersonCallback(new ValueReadCallback<Map<String, ValuePair[]>>() {
            @Override
            public void onCallback(Map<String, ValuePair[]> data) {
                if (data.get("drinks") == null) {
                    data.put("drinks", new ValuePair[0]);
                    FirebaseDatabase.getInstance()
                            .getReference("people/" + name + "/drinks/Bier").setValue(0);
                }

                for (ValuePair pair : data.get("drinks")) {
                    numDrinks.put(pair.key, Integer.valueOf(String.valueOf(pair.value)));
                }

                numsLoaded = true;
                if (drinksLoaded) setupViews();
            }
        }, null);

        FirebaseDatabase.getInstance().getReference("people/" + name + "/drinks")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        numDrinks.put(dataSnapshot.getKey(),
                                Integer.parseInt(String.valueOf(dataSnapshot.getValue())));
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
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

        FirebaseManager.writeValue("people/" + name + "/appVersion", assembleBuildNumber());
    }

    private String assembleBuildNumber() {
        return BUILD_NUMBER[0]
                + "." + BUILD_NUMBER[1]
                + "." + BUILD_NUMBER[2]
                + "." + BUILD_NUMBER[3];
    }

    public void checkSum(float prize) {
        prizesDatabase.newPrize(prize);
        if (prizesDatabase.getStatusFull()) {
            float sum = prizesDatabase.getSum();
            if (sum < 7 && alkchievementsDatabase.getState("sparfuchs") == 0) {
                alkchievementsDatabase.setState("sparfuchs", 1);
                Roast.showToast(this, R.drawable.sparfuchs, "Alkchievement erhalten!", "Sparfuchs");
            }
        }
    }

    public void addSessionBeer(boolean add) {
        if (!(alkchievementsDatabase.getState("bierkenner") == 3)) {
            @SuppressLint("SimpleDateFormat")
            DateFormat df = new SimpleDateFormat("yyyy/MM/dd/HH/mm");
            String date = df.format(Calendar.getInstance().getTime());

            if (timeDatabase.getItem(0)[1].equals("0")) {
                timeDatabase.updateValue(0, date);
                alkchievementsDatabase.addToState("bierkenner", 1);
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
        }
    }

    public void addFollowDay() {
        if (!(alkchievementsDatabase.getState("stammgast") == 3)) {
            DateFormat df = new SimpleDateFormat("yyyy/MM/dd/HH/mm");
            String date = df.format(Calendar.getInstance().getTime());

            if (timeDatabase.getItem(4)[1].equals("0")) {
                timeDatabase.updateValue(4, date);
                database.updateValue(2, 1);
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
                int dayStreak = Integer.parseInt(database.getItem(2)[1]) + 1;
                database.updateValue(2, dayStreak);

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
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 69:
                UpdateDialogProcedure.checkPermissionsAndDownload(this);
        }
    }

    public void addSessionRadler(boolean add) {
        if (!(alkchievementsDatabase.getState("kegelsportverein") == 1)) {
            DateFormat df = new SimpleDateFormat("yyyy/MM/dd/HH/mm");
            String date = df.format(Calendar.getInstance().getTime());

            int radlerStreak = Integer.parseInt(database.getItem(3)[1]) + 1;

            if (timeDatabase.getItem(1)[1].equals("0")) {
                timeDatabase.updateValue(1, date);
                database.updateValue(3, radlerStreak);
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
                database.updateValue(3, radlerStreak);

                if (radlerStreak == 5 && alkchievementsDatabase.getState("kegelsportverein") == 0) {
                    alkchievementsDatabase.setState("kegelsportverein", 1);
                    Roast.showToast(this, R.drawable.kegelsportverein, "Alkchievement erhalten!",
                            alkchievementsDatabase.getName("kegelsportverein"));
                }
            }

            if (isSession && !add) {
                database.updateValue(3, radlerStreak - 2); //-2, da schon 1 hinzugefügt wurde
            }

            if (!isSession && add) {
                timeDatabase.updateValue(1, date);
                database.updateValue(3, 1);
            }

            if (!isSession && !add) {
                database.updateValue(3, 0);
            }
        }
    }

    public void addSessionNonAlk(boolean add) {
        if (!(alkchievementsDatabase.getState("nullKommaNull") == 1)) {
            DateFormat df = new SimpleDateFormat("yyyy/MM/dd/HH/mm");
            String date = df.format(Calendar.getInstance().getTime());

            int noAlkStreak = Integer.parseInt(database.getItem(4)[1]) + 1;

            if (timeDatabase.getItem(2)[1].equals("0")) {
                timeDatabase.updateValue(2, date);
                database.updateValue(4, noAlkStreak);
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
                database.updateValue(4, noAlkStreak);

                if (noAlkStreak == 5 && alkchievementsDatabase.getState("nullKommaNull") == 0) {
                    alkchievementsDatabase.setState("nullKommaNull", 1);
                    Roast.showToast(this, R.drawable.null_komma_null, "Alkchievement erhalten!",
                            alkchievementsDatabase.getName("nullKommaNull"));
                }
            }

            if (isSession && !add) {
                database.updateValue(4, noAlkStreak - 2);
            }

            if (!isSession && add) {
                timeDatabase.updateValue(2, date);
                database.updateValue(4, 1);
            }

            if (!isSession && !add) {
                database.updateValue(4, 0);
            }
        }
    }

    public void addSessionShot(boolean add) {
        if (!(alkchievementsDatabase.getState("blauWieDasMeer") == 1)) {
            DateFormat df = new SimpleDateFormat("yyyy/MM/dd/HH/mm");
            String date = df.format(Calendar.getInstance().getTime());

            int killStreak = Integer.parseInt(database.getItem(5)[1]) + 1;

            if (timeDatabase.getItem(3)[1].equals("0")) {
                timeDatabase.updateValue(3, date);
                database.updateValue(5, killStreak);
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
                database.updateValue(5, killStreak);

                if (killStreak >= 5 && alkchievementsDatabase.getState("blauWieDasMeer") == 0) {
                    alkchievementsDatabase.setState("blauWieDasMeer", 1);
                    Roast.showToast(this, R.drawable.blau_wie_das_meer, "Alkchievement erhalten!",
                            alkchievementsDatabase.getName("blauWieDasMeer"));
                }
            }

            if (isSession && !add) {
                database.updateValue(5, killStreak - 2);
            }

            if (!isSession && add) {
                timeDatabase.updateValue(3, date);
                database.updateValue(5, 1);
            }

            if (!isSession && !add) {
                database.updateValue(5, 0);
            }
        }
    }

    public void addEverBeer(boolean add) {
        int beerStreak = Integer.parseInt(database.getItem(6)[1]) + 1;

        if (add) {
            database.updateValue(6, beerStreak);
            if (beerStreak >= 20 && alkchievementsDatabase.getState("kastenLeer") == 0) {
                alkchievementsDatabase.setState("kastenLeer", 1);
                Roast.showToast(this, R.drawable.kasten_leer, "Alkchievement erhalten!",
                        alkchievementsDatabase.getName("kastenLeer"));
            }
        }
        if (!add) {
            database.updateValue(6, beerStreak - 2);
        }
    }

    public void addStorno() {
        int stornoCount = Integer.parseInt(database.getItem(7)[1]) + 1;
        database.updateValue(7, stornoCount);
        if (stornoCount >= 5 && alkchievementsDatabase.getState("wurstfinger") == 0) {
            alkchievementsDatabase.setState("wurstfinger", 1);
            Roast.showToast(this, R.drawable.wurstfinger, "Alkchievement erhalten!",
                    alkchievementsDatabase.getName("wurstfinger"));
        }
    }

    public void addClickKasten() {
        if (kastenClicks < 100 && alkchievementsDatabase.getState("hobbylos") == 0) {
            kastenClicks = kastenClicks + 1;
            database.updateValue(8, kastenClicks);
            if (kastenClicks >= 100 && alkchievementsDatabase.getState("hobbylos") == 0) {
                alkchievementsDatabase.setState("hobbylos", 1);
                Roast.showToast(this, R.drawable.hobbylos, "Alkchievement erhalten!",
                        alkchievementsDatabase.getName("hobbylos"));
            }
        }
    }

    public void updateDrink(String drink, int count) {
        FirebaseManager.writeValue("people/" + name + "/drinks/" + drink, count);
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

        adapter = new Alkdapter(this, R.layout.alk_item, beverages, stock);
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
        launchPasswordCheck();
    }

    public void launchPasswordCheck() {
        final Dialog dialog = new Dialog(this);
        dialog.setTitle("Passwort eingeben");
        dialog.setContentView(R.layout.dialog_password_checker);

        final NumberPicker p1 = (NumberPicker) dialog.findViewById(R.id.num_lock_1);
        p1.setMinValue(0);
        p1.setValue(1);
        p1.setMaxValue(9);
        p1.setWrapSelectorWheel(true);

        final NumberPicker p2 = (NumberPicker) dialog.findViewById(R.id.num_lock_2);
        p2.setMinValue(0);
        p2.setValue(1);
        p2.setMaxValue(9);
        p2.setWrapSelectorWheel(true);

        final NumberPicker p3 = (NumberPicker) dialog.findViewById(R.id.num_lock_3);
        p3.setMinValue(0);
        p3.setValue(1);
        p3.setMaxValue(9);
        p3.setWrapSelectorWheel(true);

        NumberPicker p4 = (NumberPicker) dialog.findViewById(R.id.num_lock_4);
        p4.setMinValue(0);
        p4.setValue(1);
        p4.setMaxValue(9);
        p4.setWrapSelectorWheel(true);
        p4.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                if (((p1.getValue() * 1000)
                        + (p2.getValue() * 100)
                        + (p3.getValue() * 10)
                        + numberPicker.getValue()) == UNIMPORTANT_VARIABLE) {
                    dialog.dismiss();

                    Intent hansl = new Intent(context, SettingsAlktivity.class);
                    startActivity(hansl);
                    finish();
                }
            }
        });

        dialog.show();
    }

    public void fassl() {
        Random random = new Random();

        int layoutWidth = findViewById(R.id.activity_main_alktivity).getMeasuredWidth();
        int layoutHeight = findViewById(R.id.activity_main_alktivity).getMeasuredHeight();
        int fasslWidth = fassl.getMeasuredWidth();
        int fasslHeight = fassl.getMeasuredHeight();

        fassl.setX(random.nextInt(layoutWidth - fasslWidth));
        fassl.setY(random.nextInt(layoutHeight - fasslHeight));
        fassl.setVisibility(View.VISIBLE);
        fassl.bringToFront();

        fassl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                explode((int) fassl.getX(), (int) fassl.getY());
                fassl.setVisibility(View.GONE);
            }
        });
    }

    public void explode(int posX, int posY) {
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

    private void setupExplosion() {
        mp = MediaPlayer.create(context, R.raw.explosion);
        fassl = (ImageView) findViewById(R.id.fassl);
        explosion = (ImageView) findViewById(R.id.explosion);
        fassl.setVisibility(View.INVISIBLE);
        explosion.setVisibility(View.INVISIBLE);
    }
}
