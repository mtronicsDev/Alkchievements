package de.daschubbm.alkchievements;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Maxi on 30.09.2016.
 */
public class SettingsAlkdapter extends ArrayAdapter<String[]> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<String[]> drinks = new ArrayList<>();


    public SettingsAlkdapter(Context context, ArrayList<String[]> data) {

        super(context, R.layout.settings_drink_item, data);

        this.layoutResourceId = R.layout.settings_drink_item;
        this.context = context;
        drinks = data;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(layoutResourceId, null);
        }

        final String drinkName = drinks.get(position)[0];
        String drinkPrice = drinks.get(position)[1];

        if (drinkName != null && drinkPrice != null) {
            TextView name = (TextView) v.findViewById(R.id.drink_name);
            name.setText(drinkName);

            TextView price = (TextView) v.findViewById(R.id.drink_price);
            price.setText(drinkPrice);
        }
        return v;
    }
}
