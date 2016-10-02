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
import java.util.Random;

/**
 * Created by Jonathan on 30.09.2016.
 */

public class AlkchievementsAlkdapder extends ArrayAdapter<String[]> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<String[]> alkchievements = new ArrayList<>();
    private Map<String, Integer> imageMap;


    public AlkchievementsAlkdapder(Context context, int layoutResourceId, ArrayList<String[]> data) {

        super(context, layoutResourceId, data);

        this.layoutResourceId = layoutResourceId;
        this.context = context;
        alkchievements = data;

        imageMap = new HashMap<>();
        imageMap.put("Wurschtfinger", R.drawable.wurstfinger);
        imageMap.put("Blau wie das Meer", R.drawable.blau_wie_das_meer);
        imageMap.put("Schuldner Nr. 1", R.drawable.schuldner_nr_1);
        imageMap.put("Kegelsportverein", R.drawable.kegelsportverein);
        imageMap.put("Bierkenner", R.drawable.bierkenner);
        imageMap.put("0,0", R.drawable.null_komma_null);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

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

                image.setImageResource(res == null ? R.mipmap.achievement_solved : res);
                description.setVisibility(View.VISIBLE);
            }
        }
        return v;
    }
}
