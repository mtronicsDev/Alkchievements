package de.daschubbm.alkchievements;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Jonathan on 30.09.2016.
 */

public class AlkchievementsLocalDatabase implements AlkchievementsDatabase {
    private static final String DATABASE_NAME = "alkchievements.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_TABLE = "alkchievements";

    private static final String KEY_ID = "_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_STATE = "state";

    private AlkchievementsLocalDatabase.ToDoDBOpenHelper dbHelper;
    private SQLiteDatabase db;

    public AlkchievementsLocalDatabase(Context context) {
        dbHelper = new ToDoDBOpenHelper(context, DATABASE_NAME, null,
                DATABASE_VERSION);
    }

    public void open() throws SQLException {
        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLException e) {
            db = dbHelper.getReadableDatabase();
        }
    }


    public boolean getStatus() {
        Cursor results = getCursorForAllItemsFromDatabase();
        return results.moveToFirst();
    }

    public long insertItemIntoDataBase(String name, String des, String state) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_DESCRIPTION, des);
        values.put(KEY_STATE, state);
        return db.insert(DATABASE_TABLE, null, values);
    }

    public void removeAllItemsFromDatabase() {
        db.delete(DATABASE_TABLE, null, null);
    }

    private Cursor getCursorForAllItemsFromDatabase() {
        return db.query(DATABASE_TABLE, new String[]{KEY_ID, KEY_NAME, KEY_DESCRIPTION, KEY_STATE}, null, null, null, null, null);
    }

    public ArrayList<String[]> getItems() {
        ArrayList<String[]> items = new ArrayList<String[]>();
        String[] item;
        Cursor results = getCursorForAllItemsFromDatabase();

        int iName = results.getColumnIndex(KEY_NAME);
        int iDes = results.getColumnIndex(KEY_DESCRIPTION);
        int iState = results.getColumnIndex(KEY_STATE);

        if (results.moveToFirst()) do {
            item = new String[3];
            item[0] = results.getString(iName);
            item[1] = results.getString(iDes);
            item[2] = results.getString(iState);
            items.add(item);
        } while (results.moveToNext());

        return items;
    }

    public void changeStatusForItem(int id, String status) {
        ArrayList<String[]> tempo = getItems();
        removeAllItemsFromDatabase();
        for (int i = 0; i < tempo.size(); i++) {
            if (i != id) {
                insertItemIntoDataBase(tempo.get(i)[0], tempo.get(i)[1], tempo.get(i)[2]);
            }
            if (i == id) {
                insertItemIntoDataBase(tempo.get(i)[0], tempo.get(i)[1], status);
            }
        }
    }

    public void changeDescriptionForItem(int id, String status) {
        ArrayList<String[]> tempo = getItems();
        removeAllItemsFromDatabase();
        for (int i = 0; i < tempo.size(); i++) {
            if (i != id) {
                insertItemIntoDataBase(tempo.get(i)[0], tempo.get(i)[1], tempo.get(i)[2]);
            }
            if (i == id) {
                insertItemIntoDataBase(tempo.get(i)[0], status, tempo.get(i)[2]);
            }
        }
    }


    private class ToDoDBOpenHelper extends SQLiteOpenHelper {
        private static final String DATABASE_CREATE = "create table "
                + DATABASE_TABLE + " (" + KEY_ID
                + " integer primary key autoincrement, " + KEY_NAME
                + " text not null, " + KEY_DESCRIPTION
                + " text not null, " + KEY_STATE + " text);";

        public ToDoDBOpenHelper(Context c, String dbname,
                                SQLiteDatabase.CursorFactory factory, int version) {
            super(c, dbname, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
