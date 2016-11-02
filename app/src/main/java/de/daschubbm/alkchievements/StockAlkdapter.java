package de.daschubbm.alkchievements;

import android.content.Context;
import android.support.annotation.NonNull;
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

class StockAlkdapter extends ArrayAdapter<String[]> {

    private final Context context;
    private final int layoutResourceId;
    private final boolean admin;
    private ArrayList<String[]> stock = new ArrayList<>();
    private ViewHolder viewHolder;


    StockAlkdapter(boolean admin, Context context, ArrayList<String[]> data) {

        super(context, R.layout.stock_item, data);

        this.admin = admin;
        this.layoutResourceId = R.layout.stock_item;
        this.context = context;
        stock = data;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(layoutResourceId, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) v.findViewById(R.id.stock_drink);
            viewHolder.num = (TextView) v.findViewById(R.id.stock_number);
            viewHolder.add = (EditText) v.findViewById(R.id.stock_add);
        }

        final String[] item = stock.get(position);

        if (item != null) {

            viewHolder.name = (TextView) v.findViewById(R.id.stock_drink);
            viewHolder.num = (TextView) v.findViewById(R.id.stock_number);
            viewHolder.add = (EditText) v.findViewById(R.id.stock_add);

            viewHolder.name.setText(item[0]);
            viewHolder.num.setText(item[1]);
            viewHolder.add.setVisibility(View.INVISIBLE);
            if (admin) {
                viewHolder.add.setVisibility(View.VISIBLE);
            }
            v.setTag(item[0]);
        }
        return v;
    }

    private class ViewHolder {
        TextView name;
        TextView num;
        EditText add;
    }
}
