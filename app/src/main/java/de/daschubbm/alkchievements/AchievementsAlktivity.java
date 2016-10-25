package de.daschubbm.alkchievements;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AchievementsAlktivity extends AppCompatActivity {

    private ListView list;
    private AlkchievementsAlkdapder adapter;
    private ArrayList<String[]> alkchievements = new ArrayList<>();

    private AlkchievementsDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements_alktivity);
        getSupportActionBar().setTitle("Errungen safteln");
        database = AlkchievementsDatabase.getInstance();

        list = (ListView) findViewById(R.id.achievements_list);

        setNewList();
    }

    private void setNewList() {
        while (!database.isReady())
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        Map<String, String[]> alkchievementTable = new HashMap<>();

        for (Map.Entry<String, String[]> alkchievementDesc
                : database.getAlkchievementDescriptions().entrySet()) {
            String[] nameAndDesc = database.getNameAndDescription(alkchievementDesc.getKey());
            alkchievementTable.put(alkchievementDesc.getKey(), new String[]{
                    nameAndDesc[0],
                    nameAndDesc[1],
                    null});
        }

        for (Map.Entry<String, Integer> alkchievementState
                : database.getAlkchievementStates().entrySet()) {
            alkchievementTable.get(alkchievementState.getKey())[2] = String.valueOf(alkchievementState.getValue());
        }

        for (String[] alkchievement : alkchievementTable.values()) {
            alkchievements.add(alkchievement);
        }

        alkchievementTable.clear();

        adapter = new AlkchievementsAlkdapder(this, R.layout.alkchievement_item, alkchievements);
        list.setAdapter(adapter);
    }
}
