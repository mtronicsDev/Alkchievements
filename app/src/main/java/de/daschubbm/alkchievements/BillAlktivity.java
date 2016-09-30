package de.daschubbm.alkchievements;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static de.daschubbm.alkchievements.NumberFormatter.formatPrice;

public class BillAlktivity extends AppCompatActivity {

    private ListView list;
    private BillAlkdapter adapter;
    private ArrayList<String[]> debtors = new ArrayList<>();

    private Map<String, Float> beverages = new HashMap<>();

    private AlkchievementsDatabase alkchievementsDatabase;

    private Context context;
    private String name = "Hansl";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_alktivity);

        getSupportActionBar().setTitle(getString(R.string.rechnung_title));

        context = this;
        Log.d("ALKI", "Started bill");

        alkchievementsDatabase = new AlkchievementsDatabase(this);
        alkchievementsDatabase.open();
        name = getIntent().getStringExtra("NAME");

        final DatabaseReference all = FirebaseDatabase.getInstance().getReference();
        all.child("beverages").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("ALKI", "Starting beverage loading");
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    beverages.put(child.getKey(),
                            Float.parseFloat(String.valueOf(child.getValue())));
                }

                Log.d("ALKI", "Stopped beverage loading");

                all.child("people").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot person : dataSnapshot.getChildren()) {
                            String name = person.getKey();
                            float moneyToPay = 0;

                            for (DataSnapshot drink : person.getChildren()) {
                                moneyToPay += Integer.parseInt(String.valueOf(drink.getValue()))
                                        * beverages.get(drink.getKey());
                            }

                            debtors.add(new String[]{name, formatPrice(moneyToPay)});
                        }

                        Log.d("ALKI", "Done with people");

                        list = (ListView) findViewById(R.id.bill);

                        adapter = new BillAlkdapter(context, R.layout.bill_item, debtors);
                        list.setAdapter(adapter);

                        checkPrize();

                        findViewById(R.id.loading).setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkPrize() {
        float prize = 0;
        for (int i = 0; i < debtors.size(); i++) {
            if (debtors.get(i)[0].equals(name)) {
                prize = Float.parseFloat(debtors.get(i)[1]);
            }
        }
        String state = alkchievementsDatabase.getItems().get(0)[2];
        if (prize > 5 && state.equals("false")) {
            alkchievementsDatabase.changeStatusForItem(0, "1");
            Toast.makeText(context, "Alkchievement erhalten!", Toast.LENGTH_SHORT).show();
            }
        if (prize > 10 && (state.equals("false") || state.equals("1"))) {
            alkchievementsDatabase.changeStatusForItem(0, "2");
            alkchievementsDatabase.changeDescriptionForItem(0, "Erhalte eine Rechnung von über 10€!");
            Toast.makeText(context, "Alkchievement erhalten!", Toast.LENGTH_SHORT).show();
        }
        if (prize > 20 && (state.equals("false") || state.equals("1") || state.equals("2"))) {
            alkchievementsDatabase.changeStatusForItem(0, "3");
            alkchievementsDatabase.changeDescriptionForItem(0, "Erhalte eine Rechnung von über 20€!");
            Toast.makeText(context, "Alkchievement erhalten!", Toast.LENGTH_SHORT).show();
        }
    }
}
