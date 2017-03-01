package de.daschubbm.alkchievements;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import de.daschubbm.alkchievements.util.ConnectivityChecker;
import de.daschubbm.alkchievements.util.DataManager;

public class LoginAlktivity extends AppCompatActivity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_alktivity);

        context = this;

        ConnectivityChecker.checkConnectivity(context);
    }

    public void login(View view) {
        EditText nameText = (EditText) findViewById(R.id.name);

        final String name = nameText.getText().toString();
        DataManager.write("name", name);

        Intent hansl = new Intent(context, MainAlktivity.class);
        hansl.putExtra("LOGIN", true);
        startActivity(hansl);
    }
}
