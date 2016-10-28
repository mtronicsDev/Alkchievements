package de.daschubbm.alkchievements;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Maxi on 28.10.2016.
 */

final class ConnectivityChecker {
    private ConnectivityChecker() {
    }

    static boolean isConnected(Context context) {
        ConnectivityManager manager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo info = manager.getActiveNetworkInfo();

        return info != null && info.isConnectedOrConnecting();
    }

    static void checkConnectivity(Context context) {
        if (!isConnected(context)) {
            Intent hansl = new Intent(context, NoConnectionAlktivity.class);
            hansl.putExtra("SOURCE_CONTEXT", context.getClass().getName());
            context.startActivity(hansl);
        }
    }
}
