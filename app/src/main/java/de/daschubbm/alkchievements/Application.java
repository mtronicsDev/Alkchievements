package de.daschubbm.alkchievements;

import android.content.Context;

/**
 * Created by Maxi on 17.10.2016.
 */
public class Application extends android.app.Application {
    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        try {
            Class.forName("de.daschubbm.alkchievements.firebase.FirebaseManager");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
