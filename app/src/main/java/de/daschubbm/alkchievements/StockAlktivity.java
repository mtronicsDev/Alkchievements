package de.daschubbm.alkchievements;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Map;

import de.daschubbm.alkchievements.firebase.ChangeType;
import de.daschubbm.alkchievements.firebase.FirebaseManager;
import de.daschubbm.alkchievements.firebase.ValueChangedCallback;
import de.daschubbm.alkchievements.firebase.ValuePair;
import de.daschubbm.alkchievements.firebase.ValueReadCallback;
import de.daschubbm.alkchievements.util.ConnectivityChecker;
import de.daschubbm.alkchievements.util.StockPloetAlkdapter;

public class StockAlktivity extends AppCompatActivity {

    private static int UNIMPORTANT_VARIABLE = 1111;

    private RecyclerView stock_list;
    private Button button_stock;
    private TextView visibility_header;

    private ArrayList<String[]> stock = new ArrayList<>();
    //private StockAlkdapter adapter;
    private StockPloetAlkdapter adapter;
    private Context context;

    private ImageView dontMindMe;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_alktivity);
        //noinspection ConstantConditions
        getSupportActionBar().setTitle("Bestand");
        context = this;

        ConnectivityChecker.checkConnectivity(context);

        stock_list = (RecyclerView) findViewById(R.id.rec_view);
        button_stock = (Button) findViewById(R.id.button_add_stock);
        visibility_header = (TextView) findViewById(R.id.stock_add_header);
        visibility_header.setVisibility(View.INVISIBLE);

        //not important
        dontMindMe = (ImageView) findViewById(R.id.olaf);
        mp = MediaPlayer.create(this, R.raw.olaf_hugs);

        retrieveAdminPassword();
        retrieveStock();
        /*String[] uno = {"Horst", "2"};
        String[] due = {"Bier", "1"};
        stock.add(uno);
        stock.add(due);*/

        updateListView(false);

        button_stock.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
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
                    //adapter = new StockAlkdapter(false, context, stock);
                    //adapter = new StockPloetAlkdapter(stock, context, false);
                    //stock_list.setAdapter(adapter);
                    updateListView(false);
                }
            }
        });
    }

    private void updateListView(boolean ad) {
        adapter = new StockPloetAlkdapter(stock, context, ad);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        stock_list.setLayoutManager(mLayoutManager);
        stock_list.setItemAnimator(new DefaultItemAnimator());
        stock_list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void retrieveStock() {
        FirebaseManager.registerDrinksCallback(new ValueReadCallback<Map<String, ValuePair[]>>() {
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

                //adapter = new StockAlkdapter(false, context, stock);
                //stock_list.setAdapter(adapter);
                updateListView(false);

                findViewById(R.id.loading).setVisibility(View.GONE);
                findViewById(R.id.table_header).setVisibility(View.VISIBLE);
            }
        }, new ValueChangedCallback() {
            @Override
            public void onCallback(DataSnapshot changedNode, ChangeType changeType) {
                switch (changeType) {
                    case ADDED:
                        for (String[] drink : stock) {
                            if (drink[0].equals(changedNode.getKey())) {
                                stock.set(stock.indexOf(drink),
                                        new String[]{changedNode.getKey(),
                                                String.valueOf(changedNode.child("stock").getValue())});
                                break;
                            }
                        }

                        //adapter = new StockAlkdapter(false, context, stock);
                        //stock_list.setAdapter(adapter);
                        updateListView(false);
                        break;
                    case CHANGED:
                        for (String[] drink : stock) {
                            if (drink[0].equals(changedNode.getKey())) {
                                stock.set(stock.indexOf(drink),
                                        new String[]{changedNode.getKey(),
                                                String.valueOf(changedNode.child("stock").getValue())});
                                break;
                            }
                        }

                        //adapter = new StockAlkdapter(false, context, stock);
                        //stock_list.setAdapter(adapter);
                        updateListView(false);
                        break;
                    case REMOVED:
                        for (String[] drink : stock) {
                            if (drink[0].equals(changedNode.getKey())) {
                                stock.remove(drink);
                                break;
                            }
                        }

                        //adapter = new StockAlkdapter(false, context, stock);
                        //stock_list.setAdapter(adapter);
                        updateListView(false);
                        break;
                }
            }
        });
    }

    private void retrieveAdminPassword() {
        FirebaseManager.registerAdminPasswordCallback(new ValueReadCallback<Integer>() {
            @Override
            public void onCallback(Integer data) {
                UNIMPORTANT_VARIABLE = data;
            }
        }, null);
    }

    //don't mind this method or any of the sources mentioned here... there's nothing to see
    private void nothingSpecial() {
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

    @SuppressLint("SetTextI18n")
    private void doAdmin() {
        button_stock.setText("Hinzufügen");
        visibility_header.setVisibility(View.VISIBLE);
        //adapter = new StockAlkdapter(true, context, stock);
        //stock_list.setAdapter(adapter);
        updateListView(true);
    }

    private void addStock(String tag, int num) {
        int newNum = 0;
        ArrayList<String[]> stockNow = new ArrayList<>();
        for (int i = 0; i < stock.size(); i++) {
            if (stock.get(i)[0].equals(tag)) {
                newNum = Integer.parseInt(stock.get(i)[1]) + num;
                String[] dat = {stock.get(i)[0], String.valueOf(newNum)};
                stockNow.add(dat);
            } else {
                stockNow.add(stock.get(i));
            }
        }

        if (tag != null) {
            FirebaseDatabase.getInstance().getReference("drinks/" + tag + "/stock").setValue(newNum);
        }
        stock = stockNow;
    }

    private void updateStock() {
        int count = 0;
        if (adapter != null) {
            count = adapter.getItemCount();
        }
        for (int i = 0; i < count; i++) {
            View view = stock_list.getChildAt(i);
            if (view != null) {
                EditText editText = (EditText) view.findViewById(R.id.stock_add);
                String string = editText.getText().toString();
                String tag = adapter.getItem(i)[0];

                if (string.matches("[0-9]+")) {
                    addStock(tag, Integer.parseInt(string));
                }
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

                    doAdmin();
                }
                if (((p1.getValue() * 1000)
                        + (p2.getValue() * 100)
                        + (p3.getValue() * 10)
                        + numberPicker.getValue()) == 2012) {
                    dialog.dismiss();
                    nothingSpecial();
                }
            }
        });

        dialog.show();
    }
}
