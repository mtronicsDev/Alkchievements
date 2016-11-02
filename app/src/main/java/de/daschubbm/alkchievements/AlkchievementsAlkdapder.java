package de.daschubbm.alkchievements;

import android.content.Context;
import android.support.annotation.NonNull;
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
 * Created by Jonathan on 30.09.2016.
 */

class AlkchievementsAlkdapder extends ArrayAdapter<String[]> {

    private final Context context;
    private final int layoutResourceId;
    private final Map<String, Integer> imageMap;
    private ArrayList<String[]> alkchievements = new ArrayList<>();


    AlkchievementsAlkdapder(Context context, ArrayList<String[]> data) {

        super(context, R.layout.alkchievement_item, data);

        this.layoutResourceId = R.layout.alkchievement_item;
        this.context = context;
        alkchievements = data;

        imageMap = new HashMap<>();
        imageMap.put("Wurschtfinger", R.drawable.wurstfinger);
        imageMap.put("Blau wie das Meer", R.drawable.blau_wie_das_meer);
        imageMap.put("Schuldner Nr. 1", R.drawable.schuldner_nr_1);
        imageMap.put("Kegelsportverein", R.drawable.kegelsportverein);
        imageMap.put("Bierkenner", R.drawable.bierkenner);
        imageMap.put("0,0", R.drawable.null_komma_null);
        imageMap.put("Kasten leer", R.drawable.kasten_leer);
        imageMap.put("Armer Schlucker", R.drawable.armer_schlucker);
        imageMap.put("Sparfuchs", R.drawable.sparfuchs);
        imageMap.put("Stammgast", R.drawable.stammgast);
        imageMap.put("Hobbylos", R.drawable.hobbylos);
        imageMap.put("Sprengmeister", R.drawable.sprengmeister);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(layoutResourceId, null);
        }

        final String[] goal = alkchievements.get(position);

        if (goal != null) {
            TextView title = (TextView) v.findViewById(R.id.title_alkchivement);
            TextView description = (TextView) v.findViewById(R.id.description_alkchivement);
            ImageView image = (ImageView) v.findViewById(R.id.image_alkchivement);

            title.setText(goal[0]);
            description.setText(goal[1]);
            description.setVisibility(View.INVISIBLE);

            if (goal[2].equals("true") || goal[2].equals("1") || goal[2].equals("2") || goal[2].equals("3")) {
                Integer res = imageMap.get(goal[0]);

                image.setImageResource(res == null ? R.drawable.freigeschaltet : res);
                description.setVisibility(View.VISIBLE);
            }

            if (goal[2].equals("0")) {
                image.setImageResource(R.drawable.nicht_freigeschaltet);
            }
        }
        return v;
    }
}
