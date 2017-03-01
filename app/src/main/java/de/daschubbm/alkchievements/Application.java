package de.daschubbm.alkchievements;

import android.content.Context;

/**
 * Created by Maxi on 17.10.2016.
 */
public class Application extends android.app.Application {
    private static Application context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        try {
            Class.forName("de.daschubbm.alkchievements.firebase.FirebaseManager");
            Class.forName("de.daschubbm.alkchievements.util.DataManager");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
