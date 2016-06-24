package com.mymarket.gcm.julien.DAO.impl;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

public class UserProvider extends ContentProvider implements DAOConstants {
    private static final String CONTENT_PROVIDER_DB_NAME = "gcmjulien.db";
    private static final int CONTENT_PROVIDER_DB_VERSION = 1;
    private DatabaseHelper dbHelper;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, UserProvider.CONTENT_PROVIDER_DB_NAME, null,
                    UserProvider.CONTENT_PROVIDER_DB_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE "+ TABLE_USERS+ " (" +
                    _ID_USER + " integer primary key autoincrement, " +
                    NOM_COLONNE_USER_DATE + " date, " +
                    NOM_COLONNE_TOKENDEVISE_USER+ " text not null , ");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_USERS);
            onCreate(db);
        }
    }
    private long getId(Uri uri) {
        String lastPathSegment = uri.getLastPathSegment();
        if (lastPathSegment != null) {
            try {
                return Long.parseLong(lastPathSegment);
            } catch (NumberFormatException e) {
                Log.e("UserProvider", "Number Format Exception : " + e);
            }
        }
        return -1;
    }

    public UserProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        long id = getId(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            if (id < 0)
                return db.delete(
                        TABLE_USERS,
                        selection, selectionArgs);
            else
                return db.delete(
                        TABLE_USERS,
                        _ID_USER + "=" + id, selectionArgs);
        } finally {
            db.close();
        }
    }

    @Override
    public String getType(Uri uri) {
        return UserProvider.CONTENT_PROVIDER_MIME_USER;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            long id = db.insertOrThrow(
                    TABLE_USERS, null,
                    values);

            if (id == -1) {
                throw new RuntimeException(String.format(
                        "%s : Failed to insert [%s] for unknown reasons.",
                        "PersonProver", values, uri));
            } else {
                return ContentUris.withAppendedId(uri, id);
            }

        } finally {
            db.close();
        }

    }

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        long id = getId(uri);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (id < 0) {
            return db
                    .query(TABLE_USERS,
                            projection, selection, selectionArgs, null, null,
                            sortOrder);
        } else {
            return db.query(TABLE_USERS,
                    projection, _ID_USER + "=" + id, null, null, null,
                    null);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        long id = getId(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            if (id < 0)
                return db.update(
                        TABLE_USERS,
                        values, selection, selectionArgs);
            else
                return db.update(
                        TABLE_USERS,
                        values, _ID_USER + "=" + id, null);
        } finally {
            db.close();
        }
    }
}
