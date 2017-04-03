package de.daschubbm.alkchievements;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import de.daschubbm.alkchievements.util.ConnectivityChecker;
import de.daschubbm.alkchievements.util.DataManager;

import static java.lang.Math.sqrt;

public class LoginAlktivity extends AppCompatActivity {

    private Context context;
    private Uri picUri = null;

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

    public void choosePicture(View view) {
        Intent hansl = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(hansl, "Profüibuidl auswöhn!"), 42);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("HANSL", "1");
        if (resultCode == RESULT_OK && requestCode == 42) {
            Log.d("HANSL", "2");
            try {
                Log.d("HANSL", "3");
                Bitmap shitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());

                Log.d("HANSL", "4");
                int h = shitmap.getHeight(), w = shitmap.getWidth();
                int smallDim = h < w ? h : w;

                Log.d("HANSL", "5");

                Bitmap bytemap = Bitmap.createBitmap(shitmap,
                        (w - smallDim) / 2, (h - smallDim) / 2,smallDim, smallDim);

                int center = smallDim / 2;

                Log.d("HANSL", "6");
                for (int x = 0; x < w; x++) {
                    for (int y = 0; y < h; y++) {
                        if (sqrt((x-center)*(x-center)+(y-center)*(y-center)) >= center) {
                            bytemap.setPixel(x, y, 0);
                        }
                    }
                }

                Log.d("HANSL", "7");
                File filet = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/hansl", "profile.png");
                if (!filet.exists()) filet.mkdirs();

                Log.d("HANSL", "8");
                FileOutputStream fileOutputCream = new FileOutputStream(filet);
                bytemap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputCream);
                fileOutputCream.close();
                picUri = Uri.fromFile(filet);

                Log.d("HANSL", "9");
                ((ImageView) findViewById(R.id.profilePicture)).setImageURI(picUri);
                Log.d("HANSL", "10");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
