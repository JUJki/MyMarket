package com.mymarket.gcm.julien.DAO.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class UserHelper extends SQLiteOpenHelper implements DAOConstants {

    private static final String DATABASE_NAME = "gcmjulienuser.db";
    private static final int DATABASE_VERSION = 1;

    public UserHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    private static final String CREATION_BD = "CREATE TABLE "+ TABLE_USERS+ " (" +
            _ID_USER + " integer primary key autoincrement, " +
            NOM_COLONNE_USER_DATE + " date, " +
            NOM_COLONNE_TOKENDEVISE_USER + " text not null);";


    private static final String DELETE_DB = "DROP TABLE IF EXISTS "+TABLE_USERS;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DELETE_DB);
        db.execSQL(CREATION_BD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_DB);
        onCreate(db);
    }
}
