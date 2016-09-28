package de.daschubbm.alkchievements;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;

public class LoginAlktivity extends AppCompatActivity {

    private EditText nameTextField;
    private Button goButton;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_alktivity);

        context = this;

        nameTextField = (EditText) findViewById(R.id.name_text_field);
        goButton = (Button) findViewById(R.id.go_button);

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameTextField.getText().toString();

                if (!"".equals(name) && name.matches("[A-Z][a-z]+")) {
                    //TODO: Rei ind Datenbank!
                } else {
                    Toast.makeText(context,
                            "Gib g'fälligst dein Namen ein du Schelm!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
