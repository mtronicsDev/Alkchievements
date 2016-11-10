package de.daschubbm.alkchievements;

import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import de.daschubbm.alkchievements.firebase.FirebaseManager;

public class MessageEditorAlktivity extends AppCompatActivity {

    private Uri currentImage = null;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_editor_alktivity);

        context = this;

        //noinspection ConstantConditions
        getSupportActionBar().setTitle("Neue Nachricht");
    }

    public void checkAndSendMessage(View view) {
        TextView titleView = (TextView) findViewById(R.id.title_text);
        TextView messageView = (TextView) findViewById(R.id.message_text);

        String title = titleView.getText().toString();
        String message = messageView.getText().toString();

        if (!title.matches("[ \t]*") && !message.matches("[ \t\n]*")) {
            sendMessage(title, message, currentImage);
        }
    }

    private void sendMessage(final String title, final String message, Uri image) {
        final long timestamp = new Date().getTime();

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.loading);
        progressBar.setVisibility(View.VISIBLE);
        findViewById(R.id.content_layout).setVisibility(View.GONE);

        if (image != null)
            FirebaseStorage.getInstance().getReference("messages/" + timestamp + ".jpg")
                    .putFile(image)
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            progressBar
                                    .setProgress((int) ((double) taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount()));
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            FirebaseManager.writeValue("messages/" + timestamp + "/title", title);
                            FirebaseManager.writeValue("messages/" + timestamp + "/message", message);
                            FirebaseManager.writeValue("messages/" + timestamp + "/hasImage", "true");
                            Intent hansl = new Intent(context, MainAlktivity.class);
                            startActivity(hansl);
                            finish();
                        }
                    });
        else {
            FirebaseManager.writeValue("messages/" + timestamp + "/title", title);
            FirebaseManager.writeValue("messages/" + timestamp + "/message", message);
            FirebaseManager.writeValue("messages/" + timestamp + "/hasImage", "false");
            Intent hansl = new Intent(context, MainAlktivity.class);
            startActivity(hansl);
            finish();
        }

    }

    public void addImage(View view) {
        Intent hansl = new Intent();
        hansl.setType("image/*");
        hansl.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(hansl, "Buidl auswöön!"), 37);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("NOTIFICATION_EDITOR", "Retrieved image");
        if (resultCode == RESULT_OK && requestCode == 37) {
            Uri selectedUri = data.getData();

            Log.d("NOTIFICATION_EDITOR", "ResultCode OK");
            if (selectedUri == null) return;
            Log.d("NOTIFICATION_EDITOR", "URI not null");

            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = new CursorLoader(this, selectedUri, projection, null, null, null)
                    .loadInBackground();

            if (cursor == null) return;
            Log.d("NOTIFICATION_EDITOR", "Cursor not null");

            int index = cursor.getColumnIndex(projection[0]);
            cursor.moveToFirst();
            String path = cursor.getString(index);
            cursor.close();

            Bitmap bitmap = BitmapFactory.decodeFile(path);
            ImageView imageView = (ImageView) findViewById(R.id.message_image);

            if (bitmap != null) {
                Log.d("NOTIFICATION_EDITOR", "Bitmap not null");

                try {
                    File tmp =
                            new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                                    + "/Alkchievements/upload.jpg");
                    tmp.mkdirs();
                    tmp.delete();

                    currentImage = Uri.fromFile(tmp);

                    FileOutputStream os = new FileOutputStream(tmp);
                    if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 60, os)) {
                        Toast.makeText(this, "Problem! Bild konnte nicht komprimiert werden!",
                                Toast.LENGTH_SHORT).show();
                        Log.d("NOTIFICATION_EDITOR", "Compression failed");
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("NOTIFICATION_EDITOR", "File operation failed");
                    return;
                }

                Log.d("NOTIFICATION_EDITOR", "Decoding succeeded");
                imageView.setImageBitmap(bitmap);
                imageView.invalidate();
                Log.d("NOTIFICATION_EDITOR", "Everything went to plan");
            }
        }
    }
}
