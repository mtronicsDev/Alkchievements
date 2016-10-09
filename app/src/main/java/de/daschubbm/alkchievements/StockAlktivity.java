package de.daschubbm.alkchievements;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class StockAlktivity extends AppCompatActivity {

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
                    button_stock.setText("HINZUFÜGEN");
                    visibility_header.setVisibility(View.VISIBLE);
                    adapter = new StockAlkdapter(true, context, R.layout.stock_item, stock);
                    stock_list.setAdapter(adapter);
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
            if (string.matches("[0-9]")) {
                addStock(i, Integer.parseInt(string));
            }
        }
    }
}
