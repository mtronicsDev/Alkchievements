package de.daschubbm.alkchievements.firebase;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Maxi on 12.10.2016.
 */
public final class FirebaseActions {
    private FirebaseActions() {

    }

    public static void getAdminPassword(@NonNull final Callback<Integer> callback) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("adminPassword");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                callback.onCallback(Integer.valueOf(String.valueOf(dataSnapshot.getValue())));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static DatabaseReference getDrinks(@NonNull final Callback<Map<String, ValuePair[]>> callback) {
        DatabaseReference drinks = FirebaseDatabase.getInstance().getReference("drinks");
        drinks.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, ValuePair[]> drinks = new HashMap<>((int) dataSnapshot.getChildrenCount());

                for (DataSnapshot drink : dataSnapshot.getChildren()) {
                    ValuePair[] drinkValues = new ValuePair[(int) drink.getChildrenCount()];

                    int i = 0;
                    for (DataSnapshot drinkValue : drink.getChildren()) {
                        drinkValues[i] = new ValuePair(drinkValue.getKey(), drinkValue.getValue());
                        i++;
                    }

                    drinks.put(drink.getKey(), drinkValues);
                }

                callback.onCallback(drinks);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return drinks;
    }

    public static DatabaseReference getPerson(@NonNull String person, final Callback<Map<String, ValuePair[]>> callback) {
        DatabaseReference myDrinks = FirebaseDatabase.getInstance().getReference("people/" + person);
        myDrinks.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, ValuePair[]> re = new HashMap<>((int) dataSnapshot.getChildrenCount());

                for (DataSnapshot valueList : dataSnapshot.getChildren()) {
                    ValuePair[] values = new ValuePair[(int) valueList.getChildrenCount()];

                    int j = 0;
                    for (DataSnapshot value : valueList.getChildren()) {
                        values[j] = new ValuePair(value.getKey(), value.getValue());
                        j++;
                    }

                    re.put(valueList.getKey(), values);
                }

                callback.onCallback(re);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return myDrinks;
    }
}
