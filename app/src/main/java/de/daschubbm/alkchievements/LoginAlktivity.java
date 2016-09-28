package de.daschubbm.alkchievements;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginAlktivity extends AppCompatActivity {

    private EditText nameTextField;
    private Button goButton;

    private Context context;
    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_alktivity);

        context = this;

        nameTextField = (EditText) findViewById(R.id.name_text_field);
        goButton = (Button) findViewById(R.id.go_button);

        database = new Database(this);
        database.open();

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameTextField.getText().toString();

                if (!"".equals(name) && name.matches("[A-Z][a-z]+")) {
                    DatabaseReference people = FirebaseDatabase.getInstance().getReference("people");

                    database.insertItemIntoDataBase("name", name);

                    Toast.makeText(context, "Habedere " + database.getItem(0)[1] + ", du bist ja aa do!", Toast.LENGTH_SHORT).show();

                    Intent hansl = new Intent(context, MainAlktivity.class);
                    startActivity(hansl);
                } else {
                    Toast.makeText(context,
                            "Gib g'fälligst dein Namen ein du Schelm!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
