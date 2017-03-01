package de.daschubbm.alkchievements;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

import de.daschubbm.alkchievements.firebase.FirebaseManager;
import de.daschubbm.alkchievements.firebase.ValueReadCallback;
import de.daschubbm.alkchievements.util.DataManager;

import static de.daschubbm.alkchievements.util.ConnectivityChecker.checkConnectivity;
import static de.daschubbm.alkchievements.util.DataManager.defaultStorage;
import static de.daschubbm.alkchievements.util.DataManager.write;

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
