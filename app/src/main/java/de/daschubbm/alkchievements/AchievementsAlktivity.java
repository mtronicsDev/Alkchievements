package de.daschubbm.alkchievements;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class AchievementsAlktivity extends AppCompatActivity {

    private ListView list;
    private AlkchievementsAlkdapder adapter;
    private ArrayList<String[]> alkchievements = new ArrayList<String[]>();

    private AlkchievementsDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements_alktivity);
        getSupportActionBar().setTitle("Errungen safteln");
        database = new AlkchievementsDatabase(this);
        database.open();
        list = (ListView) findViewById(R.id.achievements_list);

        setNewList();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                database.changeStatusForItem(position, "true");
                setNewList();
            }
        });
    }

    private void setNewList() {
        if (database.getStatus()) {
            alkchievements = database.getItems();
        }
        adapter = new AlkchievementsAlkdapder(this, R.layout.alkchievement_item, alkchievements);
        list.setAdapter(adapter);
    }
}
