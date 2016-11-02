package de.daschubbm.alkchievements;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.DrawableRes;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;

/**
 * Created by Maxi on 25.10.2016.
 */
final class Roast {
    private Roast() {
    }

    static void showToast(final Activity activity, @DrawableRes int icon, String heading, String message) {
        RemoteViews view = new RemoteViews(activity.getPackageName(), R.layout.roast);
        view.setImageViewResource(R.id.icon, icon);
        view.setTextViewText(R.id.heading, heading);
        view.setTextViewText(R.id.message, message);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(activity.getApplicationContext());
        builder.setContent(view)
                .setCustomHeadsUpContentView(view)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setVibrate(new long[0])
                .setPriority(NotificationCompat.PRIORITY_MAX);

        ((NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE)).notify(1, builder.build());

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                ((NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(1);
                return null;
            }
        }.execute();
    }
}
