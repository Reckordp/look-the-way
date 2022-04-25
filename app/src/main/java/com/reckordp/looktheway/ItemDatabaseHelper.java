package com.reckordp.looktheway;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ItemDatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Item.db";
    public static final int DATABASE_VERSION = 1;
    private static final String SQL_CREATE_TABLE = "CREATE TABLE " + ItemDetail.TABLE_NAME +
            " (id INTEGER PRIMARY KEY AUTOINCREMENT,nama VARCHAR(255),tanda INT(1)," +
            "berkaitan INTEGER,FOREIGN KEY(berkaitan) REFERENCES " + ItemDetail.TABLE_NAME +
            "(id))";
    private static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + ItemDetail.TABLE_NAME;


    ItemDatabaseHelper(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
