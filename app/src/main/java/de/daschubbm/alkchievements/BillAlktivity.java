package de.daschubbm.alkchievements;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

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

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_alktivity);

        getSupportActionBar().setTitle(getString(R.string.rechnung_title));

        context = this;
        Log.d("ALKI", "Started bill");

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
}
