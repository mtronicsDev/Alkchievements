package de.daschubbm.alkchievements.firebase;

import com.google.firebase.database.DataSnapshot;

/**
 * Created by Maxi on 17.10.2016.
 */
@FunctionalInterface
public interface ValueChangedCallback {
    void onCallback(DataSnapshot changedNode, ChangeType changeType);
}
