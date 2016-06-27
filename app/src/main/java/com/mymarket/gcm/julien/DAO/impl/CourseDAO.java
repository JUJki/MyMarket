package com.mymarket.gcm.julien.DAO.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mymarket.gcm.DAO.IDAO;
import com.mymarket.gcm.julien.modeles.Course;
import com.mymarket.gcm.julien.modeles.User;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by julien on 24/06/2016.
 */
public class CourseDAO implements IDAO<Course, Long>, DAOConstants {

    public final String[] ALL = new String[]{_ID_COURSE,NOM_COLONNE_COURSE_NAME,NOM_COLONNE_COURSE_DATE};

    private SQLiteDatabase db;
    private SQLiteOpenHelper dbHelper;
    private Context context;

    public CourseDAO(Context context) {
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
    public void create(Course course) throws Exception {

    }

    @Override
    public Course retrieve(Long aLong) throws Exception {
        return null;
    }

    @Override
    public Course retrieveByName(String name) throws Exception {
        return null;
    }

    @Override
    public List<Course> findAll() throws Exception {
        return null;
    }

    @Override
    public void update(Course course) throws Exception {

    }

    @Override
    public void delete(Long aLong) throws Exception {

    }
}
