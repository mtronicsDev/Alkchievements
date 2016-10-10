package de.daschubbm.alkchievements;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class StockAlktivity extends AppCompatActivity {

    private static int UNIMPORTANT_VARIABLE = 9318;

    /*
    ToDo
    * Download the password form firebase
    * Download the stock from firebase and replace the dummy ArrayList
    * Update firebase when drinks are added to the stock
    * Reduce stock of drink if one is ordered (other Alktivities)
    * */

    private ListView stock_list;
    private Button button_stock;
    private TextView visibility_header;

    private ArrayList<String[]> stock = new ArrayList<String[]>();
    private StockAlkdapter adapter;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_alktivity);
        getSupportActionBar().setTitle("Bestand");
        context = this;

        stock_list = (ListView) findViewById(R.id.stock_list);
        button_stock = (Button) findViewById(R.id.button_add_stock);
        visibility_header = (TextView) findViewById(R.id.stock_add_header);
        visibility_header.setVisibility(View.INVISIBLE);

        String[] uno = {"Bier", "20"};
        String[] due = {"Wein", "10"};
        stock.add(uno);
        stock.add(due);

        adapter = new StockAlkdapter(false, context, R.layout.stock_item, stock);
        stock_list.setAdapter(adapter);

        button_stock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (button_stock.getText().equals("BESTAND AUFSTOCKEN")) {
                    launchPasswordCheck();
                    return;
                }

                if (button_stock.getText().equals("HINZUFÜGEN")) {
                    updateStock();
                    button_stock.setText("BESTAND AUFSTOCKEN");
                    visibility_header.setVisibility(View.INVISIBLE);
                    adapter = new StockAlkdapter(false, context, R.layout.stock_item, stock);
                    stock_list.setAdapter(adapter);
                }
            }
        });
    }

    private void doAdmin() {
        button_stock.setText("HINZUFÜGEN");
        visibility_header.setVisibility(View.VISIBLE);
        adapter = new StockAlkdapter(true, context, R.layout.stock_item, stock);
        stock_list.setAdapter(adapter);
    }

    private void addStock(int pos, int num) {
        int newNum = Integer.parseInt(stock.get(pos)[1]) + num;
        ArrayList<String[]> stockNow = new ArrayList<String[]>();
        for (int i = 0; i < stock.size(); i++) {
            if (i != pos) {
                stockNow.add(stock.get(i));
            }
            if (i == pos) {
                String[] dat = {stock.get(i)[0], String.valueOf(newNum)};
                stockNow.add(dat);
            }
        }
        stock = stockNow;
    }

    private void updateStock() {
        for (int i = 0; i < stock.size(); i++) {
            View view = stock_list.getChildAt(i);
            EditText editText = (EditText) view.findViewById(R.id.stock_add);
            String string = editText.getText().toString();
            if (string.matches("[0-9][0-9]") || string.matches("[0-9]")) {
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
                    Toast.makeText(getApplication(), "Subba Hansl!", Toast.LENGTH_SHORT).show();

                    doAdmin();
                }
            }
        });

        dialog.show();
    }
}
