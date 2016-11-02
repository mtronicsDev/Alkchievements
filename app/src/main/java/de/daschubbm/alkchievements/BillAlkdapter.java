package de.daschubbm.alkchievements;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jonathan on 28.09.2016.
 */

class BillAlkdapter extends ArrayAdapter<String[]> {

    private final Context context;
    private final int layoutResourceId;
    private ArrayList<String[]> debtors = new ArrayList<>();


    BillAlkdapter(Context context, ArrayList<String[]> data) {

        super(context, R.layout.bill_item, data);

        this.layoutResourceId = R.layout.bill_item;
        this.context = context;
        debtors = data;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(layoutResourceId, null);
        }

        final String[] debtor = debtors.get(position);

        if (debtor != null) {
            TextView name = (TextView) v.findViewById(R.id.schuldner);
            TextView debt = (TextView) v.findViewById(R.id.zu_zahlen);

            name.setText(debtor[0]);
            if ("-1".equals(debtor[1])) {
                debt.setVisibility(View.GONE);

                v.findViewById(R.id.geld).setVisibility(View.GONE);
            } else debt.setText(debtor[1]);
        }
        return v;
    }
}
