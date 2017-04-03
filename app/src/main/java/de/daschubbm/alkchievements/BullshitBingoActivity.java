package de.daschubbm.alkchievements;

import android.content.Intent;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class BullshitBingoActivity extends AppCompatActivity {

    private LinearLayout bullshitLayout;
    private ImageView bingo;
    private Button playOn;
    private Button back;

    private MediaPlayer mp;

    private ArrayList<LinearLayout> layouts = new ArrayList<>();
    private ArrayList<TextView> views = new ArrayList<>();
    private ArrayList<String> bullshits = new ArrayList<>();

    private int rows;
    private int width;
    private int height;

    private boolean[] checked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bullshit_bingo);
        getSupportActionBar().setTitle("Bullshit Bingo");
        Intent hansl = getIntent();
        rows = hansl.getIntExtra("ROWS", 0);
        bullshits = hansl.getStringArrayListExtra("BULLSHITS");

        bullshitLayout = (LinearLayout) findViewById(R.id.bullshit_layout);
        bingo = (ImageView) findViewById(R.id.bingo);
        playOn = (Button) findViewById(R.id.button_bullshit_play_on);
        back = (Button) findViewById(R.id.button_bullshit_back);
        checked = new boolean[rows*rows];
        for (int i = 0; i < checked.length; i++) {
            checked[i] = false;
        }
        mp = MediaPlayer.create(this, R.raw.olaf_hugs);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        setupViews();
    }

    public void setupViews() {
        int count = 0;
        for (int i = 0; i < rows; i++) {
            final LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            bullshitLayout.addView(rowLayout);
            layouts.add(rowLayout);
            for (int o = 0; o < rows; o++) {
                final TextView rowText = new TextView(this);

                rowText.setText(bullshits.get(count));
                rowText.setWidth(width/rows);
                rowText.setTag(String.valueOf(count));
                rowText.setTextSize(65/rows);
                rowText.setPadding(2,5,2,5);
                rowText.setHeight(width/rows);
                rowText.setGravity(Gravity.START);
                rowText.setBackground(getResources().getDrawable(R.drawable.back_1));
                if (count%2 == 0) {
                    rowText.setBackground(getResources().getDrawable(R.drawable.back_2));
                }
                count ++;
                rowText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setBackground(getResources().getDrawable(R.drawable.back_accent));
                        String tag = (String) v.getTag();
                        checked[Integer.parseInt(tag)] = true;
                        checkBingo();
                    }
                });

                rowText.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        String tag = (String) v.getTag();
                        int count = Integer.parseInt(tag);
                        rowText.setBackground(getResources().getDrawable(R.drawable.back_1));
                        if (count%2 == 0) {
                            rowText.setBackground(getResources().getDrawable(R.drawable.back_2));
                        }
                        checked[count] = false;
                        return false;
                    }
                });

                rowLayout.addView(rowText);
                views.add(rowText);
            }
        }
    }

    private void checkBingo() {
        if (rows == 3) {
            if (checked[0] && checked[1] && checked[2]) {
                celebrateBingo();
            }
            if (checked[3] && checked[4] && checked[5]) {
                celebrateBingo();
            }
            if (checked[6] && checked[7] && checked[8]) {
                celebrateBingo();
            }
            if (checked[0] && checked[3] && checked[6]) {
                celebrateBingo();
            }
            if (checked[1] && checked[4] && checked[7]) {
                celebrateBingo();
            }
            if (checked[2] && checked[5] && checked[8]) {
                celebrateBingo();
            }
            if (checked[0] && checked[4] && checked[8]) {
                celebrateBingo();
            }
            if (checked[2] && checked[4] && checked[6]) {
                celebrateBingo();
            }
        }
        if (rows == 4) {
            if (checked[0] && checked[1] && checked[2] && checked[3]) {
                celebrateBingo();
            }
            if (checked[4] && checked[5] && checked[6] && checked[7]) {
                celebrateBingo();
            }
            if (checked[8] && checked[9] && checked[10] && checked[11]) {
                celebrateBingo();
            }
            if (checked[12] && checked[13] && checked[14] && checked[15]) {
                celebrateBingo();
            }
            if (checked[0] && checked[4] && checked[8] && checked[12]) {
                celebrateBingo();
            }
            if (checked[1] && checked[5] && checked[9] && checked[13]) {
                celebrateBingo();
            }
            if (checked[2] && checked[6] && checked[10] && checked[14]) {
                celebrateBingo();
            }
            if (checked[3] && checked[7] && checked[11] && checked[15]) {
                celebrateBingo();
            }
            if (checked[0] && checked[5] && checked[10] && checked[15]) {
                celebrateBingo();
            }
            if (checked[3] && checked[6] && checked[9] && checked[12]) {
                celebrateBingo();
            }
        }
        if (rows == 5) {
            if (checked[0] && checked[1] && checked[2] && checked[3] && checked[4]) {
                celebrateBingo();
            }
            if (checked[5] && checked[6] && checked[7] && checked[8] && checked[9]) {
                celebrateBingo();
            }
            if (checked[10] && checked[11] && checked[12] && checked[13] && checked[14]) {
                celebrateBingo();
            }
            if (checked[15] && checked[16] && checked[17] && checked[18] && checked[19]) {
                celebrateBingo();
            }
            if (checked[20] && checked[21] && checked[22] && checked[23] && checked[24]) {
                celebrateBingo();
            }
            if (checked[0] && checked[5] && checked[10] && checked[15] && checked[20]) {
                celebrateBingo();
            }
            if (checked[1] && checked[6] && checked[11] && checked[16] && checked[21]) {
                celebrateBingo();
            }
            if (checked[2] && checked[7] && checked[12] && checked[17] && checked[22]) {
                celebrateBingo();
            }
            if (checked[3] && checked[8] && checked[13] && checked[18] && checked[23]) {
                celebrateBingo();
            }
            if (checked[4] && checked[9] && checked[14] && checked[19] && checked[24]) {
                celebrateBingo();
            }
            if (checked[0] && checked[6] && checked[12] && checked[18] && checked[24]) {
                celebrateBingo();
            }
            if (checked[4] && checked[8] && checked[12] && checked[16] && checked[20]) {
                celebrateBingo();
            }
        }
    }

    private void celebrateBingo() {
        nothingSpecial();
        deleteListeners();
    }

    private void deleteListeners() {
        for (int i =0; i <views.size(); i++) {
            views.get(i).setOnClickListener(null);
            views.get(i).setOnLongClickListener(null);
        }
    }

    private void nothingSpecial() {
        int bingoWidth = bingo.getMeasuredWidth();

        TranslateAnimation animation = new TranslateAnimation(-2*bingoWidth, width, 0, 0);
        animation.setDuration(4500);
        animation.setFillAfter(false);

        bingo.setY(height/2);
        bingo.bringToFront();
        bingo.setVisibility(View.VISIBLE);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        bingo.setVisibility(View.INVISIBLE); //Wichtig, da nur mit GONE das Bild noch einmal über den Bildschirm flackert
                        bingo.setVisibility(View.GONE);
                        playOn.setVisibility(View.VISIBLE);
                        back.setVisibility(View.VISIBLE);
                        back.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        });
                        playOn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                weidaspuin();
                                back.setVisibility(View.INVISIBLE);
                                playOn.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                },
                4500);

        bingo.startAnimation(animation);
        mp.start();
    }

    private void weidaspuin() {
        for (int i =0; i <views.size(); i++) {
            views.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setBackground(getResources().getDrawable(R.drawable.back_accent));
                }
            });
            views.get(i).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    String tag = (String) v.getTag();
                    int count = Integer.parseInt(tag);
                    v.setBackground(getResources().getDrawable(R.drawable.back_1));
                    if (count%2 == 0) {
                        v.setBackground(getResources().getDrawable(R.drawable.back_2));
                    }
                    return false;
                }
            });
        }
    }
}
