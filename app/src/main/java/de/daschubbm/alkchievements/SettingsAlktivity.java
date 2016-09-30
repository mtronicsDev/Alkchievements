package de.daschubbm.alkchievements;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SettingsAlktivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_alktivity);
        getSupportActionBar().setTitle("Einstellungen");
    }
}
