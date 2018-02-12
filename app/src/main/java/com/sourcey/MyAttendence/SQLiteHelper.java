package com.sourcey.MyAttendence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 212634269 on 09-Feb-18.
 */

public class SQLiteHelper extends SQLiteOpenHelper {
    public String TABLE_NAME = "ATTENDANCE";
    public  String COLUMN_ID = "ID";
    public  String COLUMN_FIRST = "ENDPOINT";
    public  String COLUMN_SECOND = "IMAGE";
    public  String COLUMN_THIRD = "USERID";
    public  String COLUMN_FOURTH = "COORDINATES";

    private SQLiteDatabase database;

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SQLiteDatabase.db";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " ( " + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_FIRST + " VARCHAR, " + COLUMN_SECOND + " VARCHAR, "+ COLUMN_THIRD + " VARCHAR, " + COLUMN_FOURTH + " VARCHAR);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertRecord(String endpoint, String image, String userid, String coordinates) {
        database = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_FIRST, endpoint);
        contentValues.put(COLUMN_SECOND, image);
        contentValues.put(COLUMN_THIRD, userid);
        contentValues.put(COLUMN_FOURTH, coordinates);
        database.insert(TABLE_NAME, null, contentValues);
        database.close();
    }

    public void deleteRecord(String[] id) {
        database = this.getReadableDatabase();
        database.delete(TABLE_NAME, COLUMN_ID + " = ?", id);
        database.close();
    }

    public Cursor getAllRecords() {
        database = this.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);
        return cursor;
    }
}