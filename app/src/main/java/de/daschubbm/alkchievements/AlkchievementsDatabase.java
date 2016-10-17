package de.daschubbm.alkchievements;

import android.database.SQLException;

import java.util.ArrayList;

/**
 * Created by Maxi on 17.10.2016.
 */
public interface AlkchievementsDatabase {
    void open() throws SQLException;

    boolean getStatus();

    long insertItemIntoDataBase(String name, String des, String state);

    void removeAllItemsFromDatabase();

    ArrayList<String[]> getItems();

    void changeStatusForItem(int id, String status);

    void changeDescriptionForItem(int id, String status);
}
