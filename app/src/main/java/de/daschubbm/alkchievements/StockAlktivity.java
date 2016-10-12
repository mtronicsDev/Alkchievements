package de.daschubbm.alkchievements;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.daschubbm.alkchievements.firebase.Callback;
import de.daschubbm.alkchievements.firebase.FirebaseActions;
import de.daschubbm.alkchievements.firebase.ValuePair;

public class StockAlktivity extends AppCompatActivity {

    private static int UNIMPORTANT_VARIABLE = -1;

    private ListView stock_list;
    private Button button_stock;
    private TextView visibility_header;

    private ArrayList<String[]> stock;
    private StockAlkdapter adapter;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_alktivity);
        getSupportActionBar().setTitle("Bestand");
        context = this;

        retrieveAdminPassword();
        retrieveStock();

        stock_list = (ListView) findViewById(R.id.stock_list);
        button_stock = (Button) findViewById(R.id.button_add_stock);
        visibility_header = (TextView) findViewById(R.id.stock_add_header);
        visibility_header.setVisibility(View.INVISIBLE);

        button_stock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (button_stock.getText().equals("Bestand aufstocken")) {
                    launchPasswordCheck();
                    return;
                }

                if (button_stock.getText().equals("Hinzufügen")) {
                    updateStock();
                    button_stock.setText("Bestand aufstocken");
                    visibility_header.setVisibility(View.INVISIBLE);
                    adapter = new StockAlkdapter(false, context, R.layout.stock_item, stock);
                    stock_list.setAdapter(adapter);
                }
            }
        });
    }

    private void retrieveStock() {
        FirebaseActions.getDrinks(new Callback<Map<String, ValuePair[]>>() {
            @Override
            public void onCallback(Map<String, ValuePair[]> data) {
                stock = new ArrayList<>(data.size());
                for (Map.Entry<String, ValuePair[]> drink : data.entrySet()) {
                    String drinkStock = "Wenn du das siehst haben wir Scheiße gebaut...";

                    for (ValuePair valuePair : drink.getValue()) {
                        if (valuePair.key.equals("stock")) {
                            drinkStock = String.valueOf(valuePair.value);
                        }
                    }

                    stock.add(new String[]{drink.getKey(), drinkStock});
                }

                adapter = new StockAlkdapter(false, context, R.layout.stock_item, stock);
                stock_list.setAdapter(adapter);

                findViewById(R.id.loading).setVisibility(View.GONE);
                findViewById(R.id.table_header).setVisibility(View.VISIBLE);
            }
        }).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for (String[] drink : stock) {
                    if (drink[0].equals(dataSnapshot.getKey())) {
                        stock.set(stock.indexOf(drink),
                                new String[]{dataSnapshot.getKey(),
                                        String.valueOf(dataSnapshot.child("stock").getValue())});
                        break;
                    }
                }

                adapter = new StockAlkdapter(false, context, R.layout.stock_item, stock);
                stock_list.setAdapter(adapter);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                for (String[] drink : stock) {
                    if (drink[0].equals(dataSnapshot.getKey())) {
                        stock.set(stock.indexOf(drink),
                                new String[]{dataSnapshot.getKey(),
                                        String.valueOf(dataSnapshot.child("stock").getValue())});
                        break;
                    }
                }

                adapter = new StockAlkdapter(false, context, R.layout.stock_item, stock);
                stock_list.setAdapter(adapter);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                for (String[] drink : stock) {
                    if (drink[0].equals(dataSnapshot.getKey())) {
                        stock.remove(drink);
                        break;
                    }
                }

                adapter = new StockAlkdapter(false, context, R.layout.stock_item, stock);
                stock_list.setAdapter(adapter);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void retrieveAdminPassword() {
        FirebaseActions.getAdminPassword(new Callback<Integer>() {
            @Override
            public void onCallback(Integer data) {
                if (data != null) UNIMPORTANT_VARIABLE = data;
            }
        });
    }

    //don't mind this method or any of the sources mentioned here... there's nothing to see
    private void nothingSpecial() {
        final ImageView dontMindMe = (ImageView) findViewById(R.id.olaf);
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.olaf_hugs);

        int layoutWidth = findViewById(R.id.activity_stock_alktivity).getMeasuredWidth();
        int olafWidth = dontMindMe.getMeasuredWidth();

        TranslateAnimation animation = new TranslateAnimation(-olafWidth, layoutWidth, 0, 0);
        animation.setDuration(4500);
        animation.setFillAfter(false);

        dontMindMe.bringToFront();
        dontMindMe.setVisibility(View.VISIBLE);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        dontMindMe.setVisibility(View.INVISIBLE); //Wichtig, da nur mit GONE das Bild noch einmal über den Bildschirm flackert
                        dontMindMe.setVisibility(View.GONE);
                    }
                },
                4500);

        dontMindMe.startAnimation(animation);
        mp.start();
    }
    //as I said... NOTHING!!!

    private void doAdmin() {
        button_stock.setText("Hinzufügen");
        visibility_header.setVisibility(View.VISIBLE);
        adapter = new StockAlkdapter(true, context, R.layout.stock_item, stock);
        stock_list.setAdapter(adapter);
    }

    private void addStock(int pos, int num) {
        int newNum = Integer.parseInt(stock.get(pos)[1]) + num;
        ArrayList<String[]> stockNow = new ArrayList<>();
        for (int i = 0; i < stock.size(); i++) {
            if (i != pos) {
                stockNow.add(stock.get(i));
            } else {
                String[] dat = {stock.get(i)[0], String.valueOf(newNum)};
                stockNow.add(dat);
            }
        }

        FirebaseDatabase.getInstance().getReference("drinks/" + stock.get(pos)[0] + "/stock").setValue(newNum);
        stock = stockNow;
    }

    private void updateStock() {
        for (int i = 0; i < stock.size(); i++) {
            View view = stock_list.getChildAt(i);
            EditText editText = (EditText) view.findViewById(R.id.stock_add);
            String string = editText.getText().toString();
            if (string.matches("[0-9]+")) {
                addStock(i, Integer.parseInt(string));
            }
        }
    }

    private void launchPasswordCheck() {
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
                    nothingSpecial();

                    doAdmin();
                }
            }
        });

        dialog.show();
    }
}
