package de.daschubbm.alkchievements;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jonathan on 28.09.2016.
 */

public class Database {

    private static final String DATABASE_NAME = "alk.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_TABLE = "alk";

    private static final String KEY_ID = "_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_STATE = "state";

    private  ToDoDBOpenHelper dbHelper;
    private SQLiteDatabase db;

    public Database(Context context) {
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

    public long insertItemIntoDataBase(String name, String state) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_STATE, state);
        return db.insert(DATABASE_TABLE, null, values);
    }

    public void removeAllItemsFromDatabase() {
        db.delete(DATABASE_TABLE, null, null);
    }

    private Cursor getCursorForAllItemsFromDatabase() {
        return db.query(DATABASE_TABLE, new String[]{KEY_ID, KEY_NAME, KEY_STATE}, null, null, null, null, null);
    }

    public String[] getItem(int pos) {
        String[] item = new String[2];
        Cursor results = getCursorForAllItemsFromDatabase();

        int iName = results.getColumnIndex(KEY_NAME);
        int iState = results.getColumnIndex(KEY_STATE);

        if(results.moveToPosition(pos)) {
            item[0] = results.getString(iName);
            item[1] = results.getString(iState);
        }

        return item;
    }

    private class ToDoDBOpenHelper extends SQLiteOpenHelper {
        private static final String DATABASE_CREATE = "create table "
                + DATABASE_TABLE + " (" + KEY_ID
                + " integer primary key autoincrement, " + KEY_NAME
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