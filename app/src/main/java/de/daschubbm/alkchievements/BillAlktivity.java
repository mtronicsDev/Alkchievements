package de.daschubbm.alkchievements;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class BillAlktivity extends AppCompatActivity {

    private ListView list;
    private BillAlkdapter adapter;
    private ArrayList<String[]> debtors = new ArrayList<String[]>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_alktivity);

        list = (ListView) findViewById(R.id.bill);

        String[] uno = {"Friedhelmut", "35,70"};
        String[] due = {"Maxl", "11,20"};
        debtors.add(uno);
        debtors.add(due);
        adapter = new BillAlkdapter(this, R.layout.bill_item, debtors);
        list.setAdapter(adapter);
    }
}
