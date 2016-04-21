package com.example.mridul_xpetize.test;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLlite {

    public static final String KEY_ROWID = "_id";
    public static final String KEY_EVENT = "input_event";
    public static final String KEY_TIME = "input_time";
    public static final String KEY_DATA = "input_data";
    private static final String DATABASE_NAME = "TestDB3";
    private static final String DATABASE_TABLE = "TestDbTable3";
    private static final int DATABASE_VERSION = 1;
    private DbHelper ourHelper;
    private final Context ourContext;
    private SQLiteDatabase ourDatabase;

    private static class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub
            db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" +
                            KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            KEY_EVENT + " TEXT NOT NULL, " +
                            KEY_TIME + " TEXT NOT NULL, " +
                            KEY_DATA + " TEXT NOT NULL);"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }

    public SQLlite(Context c) {
        ourContext = c;
    }

    public SQLlite open() throws SQLException {
        ourHelper = new DbHelper(ourContext);
        ourDatabase = ourHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        ourHelper.close();
    }

    public long createEntry(String event, String time, String data) {
        // TODO Auto-generated method stub
        ContentValues cv = new ContentValues();
        cv.put(KEY_EVENT, event);
        cv.put(KEY_TIME, time);
        cv.put(KEY_DATA, data);
        return ourDatabase.insert(DATABASE_TABLE, null, cv);
    }

    public String getCount() throws SQLException {
        // TODO Auto-generated method stub
        String[] columns = new String[]{KEY_ROWID, KEY_EVENT, KEY_TIME, KEY_DATA};
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);
        if (c != null) {
            String count = String.valueOf(c.getCount());
            return count;
        }
        return null;
    }


    public void deleteFirstRow() {

        String sql = "SELECT * FROM " + DATABASE_TABLE + " ORDER BY " + KEY_ROWID + " DESC LIMIT 1";
        Cursor c = ourDatabase.rawQuery(sql, null);
        if (c.moveToFirst()) {
            String rowId = c.getString(c.getColumnIndex(KEY_ROWID));
            ourDatabase.delete(DATABASE_TABLE, KEY_ROWID + "=?", new String[]{rowId});
        }

    }

    public String[] getFirstRow() {
        // TODO Auto-generated method stub
        String sql = "SELECT * FROM " + DATABASE_TABLE + " ORDER BY " + KEY_ROWID + " DESC LIMIT 1";
        String[] columns = new String[]{KEY_ROWID, KEY_EVENT, KEY_TIME, KEY_DATA};
        Cursor c = ourDatabase.rawQuery(sql, null);
        int iRow = c.getColumnIndex(KEY_ROWID);
        int iEvent = c.getColumnIndex(KEY_EVENT);
        int iTime = c.getColumnIndex(KEY_TIME);
        int iData = c.getColumnIndex(KEY_DATA);
        String arrData[] = null;
        if (c != null) {
            if (c.moveToFirst()) {
                arrData = new String[c.getColumnCount()];
                arrData[0] = c.getString(iRow);
                arrData[1] = c.getString(iEvent);
                arrData[2] = c.getString(iTime);
                arrData[3] = c.getString(iData);

            }
        }
        return arrData;

    }
}