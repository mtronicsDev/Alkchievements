package de.daschubbm.alkchievements;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import de.daschubbm.alkchievements.util.DataManager;

import static de.daschubbm.alkchievements.util.ConnectivityChecker.checkConnectivity;

public class LoadingAlktivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_alktivity);

        //noinspection ConstantConditions
        getSupportActionBar().hide();

        checkConnectivity(this); //No connection --> NoConnectionAlktivity

        if (DataManager.defaultStorage.getString("name", null) == null) {
            Intent hansl = new Intent(this, LoginAlktivity.class);
            startActivity(hansl);
        } else {
            Intent hansl = new Intent(this, MainAlktivity.class);
            startActivity(hansl);
        }
    }
}
