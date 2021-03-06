package de.daschubbm.alkchievements.firebase;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.daschubbm.alkchievements.util.DataManager;

/**
 * Created by Maxi on 17.10.2016.
 */
public final class FirebaseManager {
    private static final List<ValueChangedCallback> drinksChangedCallbacks;
    private static final List<ValueChangedCallback> currentVersionChangedCallbacks;
    private static final List<ValueChangedCallback> personChangedCallbacks;
    private static final List<ValueChangedCallback> adminPasswordChangedCallbacks;
    private static Map<String, ValuePair[]> drinks = null;
    private static ValuePair[] currentVersion = null;
    private static Map<String, ValuePair[]> person = null;
    private static int adminPassword = -1;
    private static List<ValueReadCallback<Map<String, ValuePair[]>>> drinksCallbacks;
    private static List<ValueReadCallback<ValuePair[]>> currentVersionCallbacks;
    private static List<ValueReadCallback<Map<String, ValuePair[]>>> personCallbacks;
    private static List<ValueReadCallback<Integer>> adminPasswordCallbacks;

    static {
        drinksCallbacks = new LinkedList<>();
        currentVersionCallbacks = new LinkedList<>();
        personCallbacks = new LinkedList<>();
        adminPasswordCallbacks = new LinkedList<>();

        drinksChangedCallbacks = new LinkedList<>();
        currentVersionChangedCallbacks = new LinkedList<>();
        personChangedCallbacks = new LinkedList<>();
        adminPasswordChangedCallbacks = new LinkedList<>();
    }

    private FirebaseManager() {
    }

    public static void databaseHandshake() {
        DatabaseReference drinksRef = getDrinks(new ValueReadCallback<Map<String, ValuePair[]>>() {
            @Override
            public void onCallback(Map<String, ValuePair[]> data) {
                drinks = data;

                if (drinksCallbacks != null) {
                    notifyAllReadCallbacks(drinksCallbacks, drinks);
                }
                drinksCallbacks = null;
            }
        });

        drinksRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                addDrink(dataSnapshot);
                notifyAllChangedCallbacks(drinksChangedCallbacks, dataSnapshot, ChangeType.ADDED);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                addDrink(dataSnapshot);
                notifyAllChangedCallbacks(drinksChangedCallbacks, dataSnapshot, ChangeType.CHANGED);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                drinks.remove(dataSnapshot.getKey());
                notifyAllChangedCallbacks(drinksChangedCallbacks, dataSnapshot, ChangeType.REMOVED);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference currentVersionRef = getCurrentVersion(new ValueReadCallback<ValuePair[]>() {
            @Override
            public void onCallback(ValuePair[] data) {
                currentVersion = data;

                if (currentVersionCallbacks != null) {
                    notifyAllReadCallbacks(currentVersionCallbacks, currentVersion);
                }
                currentVersionCallbacks = null;
            }
        });

        currentVersionRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                notifyAllChangedCallbacks(currentVersionChangedCallbacks, dataSnapshot, ChangeType.ADDED);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                notifyAllChangedCallbacks(currentVersionChangedCallbacks, dataSnapshot, ChangeType.CHANGED);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                notifyAllChangedCallbacks(currentVersionChangedCallbacks, dataSnapshot, ChangeType.REMOVED);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        String name = DataManager.defaultStorage.getString("name", null);

        if (name != null) loadPersonInfo(name);

        final DatabaseReference adminPasswordRef = getAdminPassword(new ValueReadCallback<Integer>() {
            @Override
            public void onCallback(Integer data) {
                if (data != null) adminPassword = data;

                if (adminPasswordCallbacks != null) {
                    notifyAllReadCallbacks(adminPasswordCallbacks, adminPassword);
                }
                adminPasswordCallbacks = null;
            }
        });

        adminPasswordRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                notifyAllChangedCallbacks(adminPasswordChangedCallbacks, dataSnapshot, ChangeType.ADDED);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                notifyAllChangedCallbacks(adminPasswordChangedCallbacks, dataSnapshot, ChangeType.CHANGED);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                notifyAllChangedCallbacks(adminPasswordChangedCallbacks, dataSnapshot, ChangeType.REMOVED);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private static void addDrink(DataSnapshot snapshot) {
        if (drinks == null) return;

        ValuePair[] values = new ValuePair[(int) snapshot.getChildrenCount()];

        int i = 0;
        for (DataSnapshot child : snapshot.getChildren()) {
            values[i] = new ValuePair(child.getKey(), child.getValue());
            i++;
        }

        drinks.put(snapshot.getKey(), values);
    }

    private static void loadPersonInfo(String name) {
        Log.d("ALKDEB", "LOAD ENTERED");
        DatabaseReference personRef = getPerson(name, new ValueReadCallback<Map<String, ValuePair[]>>() {
            @Override
            public void onCallback(Map<String, ValuePair[]> data) {
                Log.d("ALKDEB", "CALLBACK ENTERED");

                person = data;

                if (personCallbacks != null) {
                    notifyAllReadCallbacks(personCallbacks, person);
                }
                personCallbacks = null;
            }
        });

        personRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                notifyAllChangedCallbacks(personChangedCallbacks, dataSnapshot, ChangeType.ADDED);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                notifyAllChangedCallbacks(personChangedCallbacks, dataSnapshot, ChangeType.CHANGED);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                notifyAllChangedCallbacks(personChangedCallbacks, dataSnapshot, ChangeType.REMOVED);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private static <D> void notifyAllReadCallbacks(List<ValueReadCallback<D>> callbacks, D data) {
        for (ValueReadCallback<D> callback : callbacks) {
            callback.onCallback(data);
        }
    }

    private static void notifyAllChangedCallbacks(List<ValueChangedCallback> callbacks, DataSnapshot data, ChangeType changeType) {
        for (ValueChangedCallback callback : callbacks) {
            callback.onCallback(data, changeType);
        }
    }

    public static void writeValue(String path, Object value) {
        FirebaseDatabase.getInstance().getReference(path).setValue(value);
    }

    public static void supplyPersonName(String name) {
        loadPersonInfo(name);
    }

    public static void registerDrinksCallback(ValueReadCallback<Map<String, ValuePair[]>> callback,
                                              ValueChangedCallback changedCallback) {
        if (drinks == null) drinksCallbacks.add(callback);
        else callback.onCallback(drinks);

        if (changedCallback != null) drinksChangedCallbacks.add(changedCallback);
    }

    public static void registerCurrentVersionCallback(ValueReadCallback<ValuePair[]> callback,
                                                      ValueChangedCallback changedCallback) {
        if (currentVersion == null) currentVersionCallbacks.add(callback);
        else callback.onCallback(currentVersion);

        if (changedCallback != null) currentVersionChangedCallbacks.add(changedCallback);
    }

    public static void registerPersonCallback(ValueReadCallback<Map<String, ValuePair[]>> callback,
                                              @SuppressWarnings("SameParameterValue") ValueChangedCallback changedCallback) {
        if (person == null) personCallbacks.add(callback);
        else callback.onCallback(person);

        if (changedCallback != null) personChangedCallbacks.add(changedCallback);
    }

    public static void registerAdminPasswordCallback(ValueReadCallback<Integer> callback,
                                                     ValueChangedCallback changedCallback) {
        if (adminPassword == -1) adminPasswordCallbacks.add(callback);
        else callback.onCallback(adminPassword);

        if (changedCallback != null) adminPasswordChangedCallbacks.add(changedCallback);
    }

    private static DatabaseReference getAdminPassword(@NonNull final ValueReadCallback<Integer> callback) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("adminPassword");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String pw = String.valueOf(dataSnapshot.getValue());
                if (pw == null || pw.equals("null")) pw = "0000";
                callback.onCallback(Integer.valueOf(pw));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return reference;
    }

    private static DatabaseReference getDrinks(@NonNull final ValueReadCallback<Map<String, ValuePair[]>> callback) {
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

    private static DatabaseReference getCurrentVersion(final ValueReadCallback<ValuePair[]> callback) {
        DatabaseReference currentVersion = FirebaseDatabase.getInstance().getReference("currentVersion");
        currentVersion.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ValuePair[] re = new ValuePair[(int) dataSnapshot.getChildrenCount()];

                int i = 0;
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    re[i] = new ValuePair(child.getKey(), child.getValue());
                    i++;
                }

                callback.onCallback(re);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return currentVersion;
    }

    private static DatabaseReference getPerson(@NonNull String person, final ValueReadCallback<Map<String, ValuePair[]>> callback) {
        Log.d("ALKDEB", "GET ENTERED with name: " + person);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("people/" + person);
        Log.d("ALKDEB", reference.toString());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("ALKDEB", "DATA CHANGE ENTERED");
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
                Log.e("ALKDEB", "Name fucked up!");
            }
        });

        return reference;
    }
}
