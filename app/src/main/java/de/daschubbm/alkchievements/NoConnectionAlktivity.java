package de.daschubbm.alkchievements;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class NoConnectionAlktivity extends AppCompatActivity {

    Class<Activity> origin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_connection_alktivity);

        getSupportActionBar().hide();

        try {
            //noinspection unchecked
            origin = (Class<Activity>) Class.forName(getIntent().getStringExtra("SOURCE_CONTEXT"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (ConnectivityChecker.isConnected(this)) {
            Intent hansl = new Intent(this, origin);
            startActivity(hansl);
        }
    }
}
