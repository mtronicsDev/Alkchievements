package de.daschubbm.alkchievements;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jonathan on 28.09.2016.
 */

public class Alkdapter extends ArrayAdapter<String[]> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<String[]> alks = new ArrayList<>();

    private int[] images = {R.mipmap.kasten, R.mipmap.kasten};

    public Alkdapter(Context context, int layoutResourceId, ArrayList<String[]> data) {

        super(context, layoutResourceId, data);

        this.layoutResourceId = layoutResourceId;
        this.context = context;
        alks = data;
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
            TextView num_beer = (TextView) v.findViewById(R.id.num_beer);
            ImageView kasten = (ImageView) v.findViewById(R.id.kasten);
            TextView name = (TextView) v.findViewById(R.id.name);
            name.setText(alk[2]);
            kasten.setVisibility(View.INVISIBLE);
            if(position < images.length) {
                kasten.setImageResource(images[position]);
                kasten.setVisibility(View.VISIBLE);
            }

            ImageView add_flasche = (ImageView) v.findViewById(R.id.add_flasche);

            add_flasche.setTag(alk[2]);
            preis.setText(alk[0] + " €");
            num_beer.setText(alk[1]);

            add_flasche.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        return v;
    }
}
