package de.daschubbm.alkchievements;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

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
    private Map<String, Integer> stock;
    private MainAlktivity main;

    private Map<String, Integer> images = new HashMap<>();

    public Alkdapter(MainAlktivity alk, int layoutResourceId, ArrayList<String[]> data, Map<String, Integer> stock) {

        super(alk, layoutResourceId, data);

        main = alk;
        this.layoutResourceId = layoutResourceId;
        this.context = alk;
        alks = data;
        this.stock = stock;

        images.put("Bier", R.drawable.kasten_bier);
        images.put("Radler", R.drawable.kasten_radler);
        images.put("Weizen", R.drawable.kasten_weizen);
        images.put("Almdudler", R.drawable.kasten_almdudler);
        images.put("Apfelschorle", R.drawable.kasten_apfelschorle);
        images.put("Spezi", R.drawable.kasten_spezi);
        images.put("Wasser", R.drawable.kasten_wasser);
        images.put("Schnaps", R.drawable.kasten_schnaps);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(layoutResourceId, null);
        }

        final String[] alk = alks.get(position);

        if (alk != null) {
            TextView preis = (TextView) v.findViewById(R.id.preis);
            final TextView num_beer = (TextView) v.findViewById(R.id.anzahl);
            ImageView kasten = (ImageView) v.findViewById(R.id.kasten);
            TextView name = (TextView) v.findViewById(R.id.name);
            name.setText(alk[2]);

            if (images.containsKey(alk[2])) kasten.setImageResource(images.get(alk[2]));
            else kasten.setImageResource(R.drawable.kasten_sonstige);

            final ImageView add_flasche = (ImageView) v.findViewById(R.id.add_flasche);

            add_flasche.setTag(alk[2]);
            preis.setText("Preis: " + alk[0] + " €");
            num_beer.setText("G'schwoabt: " + alk[1]);

            add_flasche.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int added = Integer.parseInt(alk[1]) + 1;
                    alk[1] = String.valueOf(added);
                    num_beer.setText("G'schwoabt: " + alk[1]);

                    FirebaseDatabase.getInstance().getReference("drinks/" + alk[2] + "/stock")
                            .setValue(stock.get(alk[2]) - 1);

                    main.updateDrink((String) add_flasche.getTag(), added);
                    main.addFollowDay();
                    main.checkSum(Float.parseFloat(alk[0]));
                    if (alk[2].equals("Bier")) {
                        main.addEverBeer(true);
                        main.addSessionBeer(true);
                    }
                    if (alk[2].equals("Weizen")) {
                        main.addSessionBeer(true);
                    }
                    if (alk[2].equals("Radler")) {
                        main.addSessionRadler(true);
                        main.addSessionNonAlk(true);
                    }
                    if (alk[2].equals("Wasser") || alk[2].equals("Almdudler") || alk[2].equals("Spezi")) {
                        main.addSessionNonAlk(true);
                    }
                    if (alk[2].equals("Schnaps")) {
                        main.addSessionShot(true);
                    }
                }
            });

            add_flasche.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (Integer.parseInt(alk[1]) > 0) {
                        int taken = Integer.parseInt(alk[1]) - 1;
                        alk[1] = String.valueOf(taken);
                        num_beer.setText("G'schwoabt: " + alk[1]);

                        FirebaseDatabase.getInstance().getReference("drinks/" + alk[2] + "/stock")
                                .setValue(stock.get(alk[2]) + 1);

                        main.updateDrink((String) add_flasche.getTag(), taken);
                        main.addStorno();
                        Toast.makeText(context, "Storniert \ud83d\ude12 Fettfinger!", Toast.LENGTH_SHORT).show();
                        if (alk[2].equals("Bier")) {
                            main.addEverBeer(false);
                            main.addSessionBeer(false);
                        }
                        if (alk[2].equals("Weizen")) {
                            main.addSessionBeer(false);
                        }
                        if (alk[2].equals("Radler")) {
                            main.addSessionRadler(false);
                            main.addSessionNonAlk(false);
                        }
                        if (alk[2].equals("Wasser") || alk[2].equals("Almdudler") || alk[2].equals("Spezi")) {
                            main.addSessionNonAlk(false);
                        }
                        if (alk[2].equals("Schnaps")) {
                            main.addSessionShot(false);
                        }
                    }
                    return true;
                }
            });

            kasten.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    main.addClickKasten();
                }
            });
        }

        return v;
    }
}
