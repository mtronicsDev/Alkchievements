package de.daschubbm.alkchievements;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AchievementsAlktivity extends AppCompatActivity {

    private final ArrayList<String[]> alkchievements = new ArrayList<>();
    private ListView list;
    private AlkchievementsDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements_alktivity);
        //noinspection ConstantConditions
        getSupportActionBar().setTitle("Errungen safteln");

        ConnectivityChecker.checkConnectivity(this);

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

        AlkchievementsAlkdapder adapter = new AlkchievementsAlkdapder(this, alkchievements);
        list.setAdapter(adapter);
    }
}
