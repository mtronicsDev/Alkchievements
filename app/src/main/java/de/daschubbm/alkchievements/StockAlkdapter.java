package de.daschubbm.alkchievements;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jonathan on 09.10.2016.
 */

public class StockAlkdapter extends ArrayAdapter<String[]> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<String[]> stock = new ArrayList<>();
    private boolean admin;


    public StockAlkdapter(boolean admin, Context context, int layoutResourceId, ArrayList<String[]> data) {

        super(context, layoutResourceId, data);

        this.admin = admin;
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        stock = data;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(layoutResourceId, null);
        }

        final String[] item = stock.get(position);

        if (item != null) {
            TextView name = (TextView) v.findViewById(R.id.stock_drink);
            TextView num = (TextView) v.findViewById(R.id.stock_number);
            EditText add = (EditText) v.findViewById(R.id.stock_add);

            name.setText(item[0]);
            num.setText(item[1]);
            add.setVisibility(View.INVISIBLE);
            if (admin) {
                add.setVisibility(View.VISIBLE);
            }
        }
        return v;
    }
}
