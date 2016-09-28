package de.daschubbm.alkchievements;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jonathan on 28.09.2016.
 */

public class Alkdapter extends ArrayAdapter<String[]> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<String[]> alks = new ArrayList<>();
    private MainAlktivity main;

    private Map<String, Integer> images = new HashMap<>();

    public Alkdapter(MainAlktivity alk, int layoutResourceId, ArrayList<String[]> data) {

        super(alk, layoutResourceId, data);

        main = alk;
        this.layoutResourceId = layoutResourceId;
        this.context = alk;
        alks = data;

        images.put("Radler", R.mipmap.kasten);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(layoutResourceId, null);
        }

        final String[] alk = alks.get(position);

        if(alk != null) {
            TextView preis = (TextView) v.findViewById(R.id.preis);
            final TextView num_beer = (TextView) v.findViewById(R.id.num_beer);
            ImageView kasten = (ImageView) v.findViewById(R.id.kasten);
            TextView name = (TextView) v.findViewById(R.id.name);
            name.setText(alk[2]);
            kasten.setVisibility(View.INVISIBLE);
            if(images.containsKey(alk[2])) {
                kasten.setImageResource(images.get(alk[2]));
                kasten.setVisibility(View.VISIBLE);
            }

            final ImageView add_flasche = (ImageView) v.findViewById(R.id.add_flasche);

            add_flasche.setTag(alk[2]);
            preis.setText(alk[0] + " €");
            num_beer.setText(alk[1]);

            add_flasche.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int added = Integer.parseInt(alk[1]) + 1;
                    alk[1] = String.valueOf(added);
                    num_beer.setText(alk[1]);
                    main.updateDrink((String) add_flasche.getTag(), added);
                }
            });
        }

        return v;
    }
}
