package de.daschubbm.alkchievements;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.IOException;

public class NotificationService extends Service {
    private DatabaseReference reference;
    private Context context;

    private OnSuccessListener<FileDownloadTask.TaskSnapshot> successListener;

    private DataSnapshot currentDataSnapshot;
    private File currentFile;
    private Exception dummy = new Exception();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = this;

        initializeListeners();

        reference = FirebaseDatabase.getInstance().getReference("messages");
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (!dataSnapshot.child("hasImage").exists()
                        || !dataSnapshot.child("title").exists()
                        || !dataSnapshot.child("message").exists()) return;

                currentDataSnapshot = dataSnapshot;

                if ("true".equals(dataSnapshot.child("hasImage").getValue())) {
                    try {
                        currentFile = File.createTempFile("images", "jpg");
                        currentFile.delete();

                        FirebaseStorage.getInstance()
                                .getReference("messages/" + dataSnapshot.getKey() + ".jpg")
                                .getFile(currentFile)
                                .addOnSuccessListener(successListener);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (currentDataSnapshot == null) return;

                    String title = String.valueOf(currentDataSnapshot.child("title").getValue());
                    String message = String.valueOf(currentDataSnapshot.child("message").getValue());

                    Notification.Builder builder = new Notification.Builder(context);
                    builder.setContentTitle(title)
                            .setContentText(message)
                            .setSmallIcon(R.drawable.message)
                            .setVibrate(new long[0])
                            .setPriority(Notification.PRIORITY_MAX);

                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    manager.notify(3, builder.build());

                    currentDataSnapshot = null;
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return Service.START_STICKY;
    }

    private void initializeListeners() {
        successListener = new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                if (currentDataSnapshot == null || currentFile == null) return;

                String title = String.valueOf(currentDataSnapshot.child("title").getValue());
                String message = String.valueOf(currentDataSnapshot.child("message").getValue());

                Bitmap image = BitmapFactory.decodeFile(currentFile.getPath());

                if (image == null) {
                    Log.e("NOTIFICATION", "Retrieved image is null!");
                    return;
                }

                Notification.Builder builder = new Notification.Builder(context);
                builder.setContentTitle(title)
                        .setContentText(message)
                        .setSmallIcon(R.drawable.message)
                        .setLargeIcon(image)
                        .setStyle(new Notification.BigPictureStyle().bigPicture(image))
                        .setVibrate(new long[0])
                        .setPriority(Notification.PRIORITY_MAX);

                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                manager.notify(7, builder.build());

                currentDataSnapshot = null;
            }
        };
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
}
