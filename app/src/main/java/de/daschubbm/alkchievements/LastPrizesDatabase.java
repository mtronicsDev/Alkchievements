package de.daschubbm.alkchievements;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jonathan on 02.10.2016.
 */

class LastPrizesDatabase {
    private static final String DATABASE_NAME = "prize.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_TABLE = "prize";

    private static final String KEY_ID = "_id";
    private static final String KEY_PRIZE = "name";

    private final LastPrizesDatabase.ToDoDBOpenHelper dbHelper;
    private SQLiteDatabase db;

    LastPrizesDatabase(Context context) {
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

    @SuppressWarnings("unused")
    public boolean getStatus() {
        Cursor results = getCursorForAllItemsFromDatabase();
        return results.moveToFirst();
    }

    boolean getStatusFull() {
        Cursor results = getCursorForAllItemsFromDatabase();
        results.moveToFirst();
        return results.moveToPosition(9);
    }

    private void insertItemIntoDataBase(float prize) {
        ContentValues values = new ContentValues();
        values.put(KEY_PRIZE, String.valueOf(prize));
        db.insert(DATABASE_TABLE, null, values);
    }

    void newPrize(float prize) {
        if (!getStatusFull()) {
            insertItemIntoDataBase(prize);
        }

        if (getStatusFull()) {
            float[] save = getItems();
            removeAllItemsFromDatabase();
            insertItemIntoDataBase(prize);
            for (int i = 0; i < 9; i++) {
                insertItemIntoDataBase(save[i]);
            }
        }
    }

    float getSum() {
        float[] save = getItems();
        float sum = 0;
        for (float aSave : save) {
            sum = sum + aSave;
        }
        return sum;
    }

    private void removeAllItemsFromDatabase() {
        db.delete(DATABASE_TABLE, null, null);
    }

    private Cursor getCursorForAllItemsFromDatabase() {
        return db.query(DATABASE_TABLE, new String[]{KEY_ID, KEY_PRIZE}, null, null, null, null, null);
    }

    private float[] getItems() {
        Cursor results = getCursorForAllItemsFromDatabase();

        int iName = results.getColumnIndex(KEY_PRIZE);
        int length = 0;

        if (results.moveToFirst()) {
            do {
                length++;
            } while (results.moveToNext());
        }

        float[] items = new float[length];
        results.moveToFirst();
        for (int i = 0; i < length; i++) {
            items[i] = Float.parseFloat(results.getString(iName));
            results.moveToNext();
        }
        return items;
    }

    private class ToDoDBOpenHelper extends SQLiteOpenHelper {
        private static final String DATABASE_CREATE = "create table "
                + DATABASE_TABLE + " (" + KEY_ID
                + " integer primary key autoincrement, " + KEY_PRIZE
                + " text not null);";

        ToDoDBOpenHelper(Context c) {
            super(c, LastPrizesDatabase.DATABASE_NAME, null, LastPrizesDatabase.DATABASE_VERSION);
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
