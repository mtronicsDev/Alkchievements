package de.daschubbm.alkchievements;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class BullshitActivity extends AppCompatActivity {

    private LinearLayout mainLayout;

    private ArrayList<LinearLayout> layouts = new ArrayList<>();
    private ArrayList<EditText> edits = new ArrayList<>();
    private ArrayList<String> bullshits = new ArrayList<>();

    private int rows;
    private int width;
    private int height;

    private int actionHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bullshit);
        getSupportActionBar().setTitle("Bullshit Bingo");

        mainLayout = (LinearLayout) findViewById(R.id.main_layout);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;

        setupEdits(3);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater flatuleur = getMenuInflater();
        flatuleur.inflate(R.menu.menu_bullshit, menu);

        return true;
    }

    public void setupEdits(int rows) {
        mainLayout.removeAllViews();
        edits = new ArrayList<>();
        layouts = new ArrayList<>();
        this.rows = rows;
        int count = 0;
        for (int i = 0; i < rows; i++) {
            final LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            mainLayout.addView(rowLayout);
            layouts.add(rowLayout);
            for (int o = 0; o < rows; o++) {
                final EditText rowEdit = new EditText(this);

                rowEdit.setHint("Bullshit eingeben!");
                rowEdit.setTextSize(65/rows);
                rowEdit.setTag(String.valueOf(count));
                rowEdit.setWidth(width/rows);
                rowEdit.setHeight(height/rows - height/23);
                rowEdit.setPadding(2,5,2,5);
                rowEdit.setGravity(Gravity.START);
                rowEdit.setBackground(getResources().getDrawable(R.drawable.back_1));
                if (count%2 == 0) {
                    rowEdit.setBackground(getResources().getDrawable(R.drawable.back_2));
                }
                count ++;

                rowLayout.addView(rowEdit);
                edits.add(rowEdit);
            }
        }
    }

    private void getBullshits(boolean shuffle) {
        bullshits = new ArrayList<>();
        if (layouts.size() < 3) {
            Toast.makeText(this, "Entscheid di erst amoi für a Format!", Toast.LENGTH_SHORT).show();
            return;
        }
        for (int i =0; i < edits.size(); i++) {
            String tag = (String) edits.get(i).getTag();
            if (tag.equals(String.valueOf(i))) {
                if (!edits.get(i).getText().toString().equals("")) {
                    bullshits.add(edits.get(i).getText().toString());
                } else {
                    Toast.makeText(this, "Du muasst scho alle Felder ausfüllen, du Experte!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
        if (shuffle) {
            Collections.shuffle(bullshits);
        }
        Intent hansl = new Intent(this, BullshitBingoActivity.class);
        hansl.putExtra("ROWS", rows);
        hansl.putExtra("BULLSHITS", bullshits);
        startActivity(hansl);
    }

    public void launchBullshit(MenuItem item) {
        getBullshits(false);
    }

    public void launchBullshitShuffle (MenuItem item) {
        getBullshits(true);
    }
}
