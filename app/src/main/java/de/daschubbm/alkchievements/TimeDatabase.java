package de.daschubbm.alkchievements;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Jonathan on 02.10.2016.
 */

class TimeDatabase {
    private static final String DATABASE_NAME = "time.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_TABLE = "time";

    private static final String KEY_ID = "_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_DATE = "date";

    private final TimeDatabase.ToDoDBOpenHelper dbHelper;
    private SQLiteDatabase db;

    TimeDatabase(Context context) {
        dbHelper = new ToDoDBOpenHelper(context
        );
    }

    public void open() throws SQLException {
        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLException e) {
            db = dbHelper.getReadableDatabase();
        }
    }

    boolean getStatus() {
        Cursor results = getCursorForAllItemsFromDatabase();
        return results.moveToFirst();
    }

    void insertItemIntoDataBase(String name, String state) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_DATE, state);
        db.insert(DATABASE_TABLE, null, values);
    }

    private void removeAllItemsFromDatabase() {
        db.delete(DATABASE_TABLE, null, null);
    }

    private Cursor getCursorForAllItemsFromDatabase() {
        return db.query(DATABASE_TABLE, new String[]{KEY_ID, KEY_NAME, KEY_DATE}, null, null, null, null, null);
    }

    String[] getItem(int pos) {
        String[] item = new String[2];
        Cursor results = getCursorForAllItemsFromDatabase();

        int iName = results.getColumnIndex(KEY_NAME);
        int iState = results.getColumnIndex(KEY_DATE);

        if (results.moveToPosition(pos)) {
            item[0] = results.getString(iName);
            item[1] = results.getString(iState);
        }

        return item;
    }

    private ArrayList<String[]> getItems() {
        ArrayList<String[]> items = new ArrayList<>();
        String[] item;
        Cursor results = getCursorForAllItemsFromDatabase();

        int iName = results.getColumnIndex(KEY_NAME);
        int iState = results.getColumnIndex(KEY_DATE);

        if (results.moveToFirst()) do {
            item = new String[2];
            item[0] = results.getString(iName);
            item[1] = results.getString(iState);
            items.add(item);
        } while (results.moveToNext());

        return items;
    }

    void updateValue(int id, String value) {
        ArrayList<String[]> tempo = getItems();
        removeAllItemsFromDatabase();
        for (int i = 0; i < tempo.size(); i++) {
            if (i != id) {
                insertItemIntoDataBase(tempo.get(i)[0], tempo.get(i)[1]);
            }
            if (i == id) {
                insertItemIntoDataBase(tempo.get(i)[0], value);
            }
        }
    }

    private class ToDoDBOpenHelper extends SQLiteOpenHelper {
        private static final String DATABASE_CREATE = "create table "
                + DATABASE_TABLE + " (" + KEY_ID
                + " integer primary key autoincrement, " + KEY_NAME
                + " text not null, " + KEY_DATE + " text);";

        ToDoDBOpenHelper(Context c) {
            super(c, TimeDatabase.DATABASE_NAME, null, TimeDatabase.DATABASE_VERSION);
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
