package com.mymarket.gcm.julien.DAO.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mymarket.gcm.DAO.IDAO;
import com.mymarket.gcm.julien.modeles.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements IDAO<User, Long>, DAOConstants {

    public final String[] ALL = new String[]{_ID_USER,NOM_COLONNE_TOKENDEVISE_USER,NOM_COLONNE_USER_DATE};
    private SQLiteDatabase db;
    private SQLiteOpenHelper dbHelper;
    private Context context;

    public UserDAO(Context context) {
        this.context = context;
        this.dbHelper = new UserHelper(context);
    }

    @Override
    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    @Override
    public void close() {
        dbHelper.close();
    }

    @Override
    public void create(User user) throws Exception {
        ContentValues values = new ContentValues();
        values.put(NOM_COLONNE_TOKENDEVISE_USER,user.getTeokendevise());
        values.put(NOM_COLONNE_USER_DATE,user.getDate());
        db.insert(TABLE_USERS, null, values);
    }

    @Override
    public User retrieve(Long aLong) throws Exception {
        Cursor cursor = db.query(TABLE_USERS, ALL, _ID_USER + " = " + aLong, null, null, null, null);
        cursor.moveToFirst();
        String date = cursor.getString(cursor.getColumnIndex(NOM_COLONNE_USER_DATE));
        String tokenDevise = cursor.getString(cursor.getColumnIndex(NOM_COLONNE_TOKENDEVISE_USER));
        cursor.close();
        return new User(tokenDevise,date);
    }

    @Override
    public User retrieveByName(String name) throws Exception {
        Cursor cursor = db.query(TABLE_USERS, ALL, NOM_COLONNE_TOKENDEVISE_USER + " = " + name, null, null, null, null);
        cursor.moveToFirst();
        String date = cursor.getString(cursor.getColumnIndex(NOM_COLONNE_USER_DATE));
        String tokenDevise = cursor.getString(cursor.getColumnIndex(NOM_COLONNE_TOKENDEVISE_USER));
        cursor.close();
        return new User(tokenDevise,date);
    }

    @Override
    public List<User> findAll() throws Exception {
        List<User> users = new ArrayList<User>();
        Cursor cursor = db.query(TABLE_USERS, ALL, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            String date = cursor.getString(cursor.getColumnIndex(NOM_COLONNE_USER_DATE));
            String tokenDevise = cursor.getString(cursor.getColumnIndex(NOM_COLONNE_TOKENDEVISE_USER));

            User u = new User(tokenDevise,date);
            users.add(u);
            cursor.moveToNext();
        }
        cursor.close();
        return users;
    }

    @Override
    public void update(User user) throws Exception {
        ContentValues values = new ContentValues();
        values.put(NOM_COLONNE_TOKENDEVISE_USER,user.getTeokendevise());
        values.put(NOM_COLONNE_USER_DATE,user.getDate());
        db.update(TABLE_USERS, values, _ID_USER + " = " + user.getId(), null);
    }

    @Override
    public void delete(Long aLong) throws Exception {
        db.delete(TABLE_USERS, _ID_USER + " = " + aLong,null);
    }
}
