package com.mymarket.gcm.julien.DAO.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mymarket.gcm.DAO.IDAO;
import com.mymarket.gcm.julien.modeles.Course;
import com.mymarket.gcm.julien.modeles.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class CourseDAO implements IDAO<Course, Long>, DAOConstants {

    public final String[] ALL = new String[]{_ID_COURSE,NOM_COLONNE_COURSE_NAME,NOM_COLONNE_COURSE_DATE};
    private SQLiteDatabase db;
    private SQLiteOpenHelper dbHelper;
    private Context context;

    public CourseDAO(Context context) {
        this.context = context;
        this.dbHelper = new CourseHelper(context);
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
        ContentValues values = new ContentValues();
        values.put(NOM_COLONNE_COURSE_NAME,course.getName());
        values.put(NOM_COLONNE_COURSE_DATE,course.getDate());
        db.insert(TABLE_COURSES, null, values);
    }

    @Override
    public Course retrieve(Long aLong) throws Exception {
        Cursor cursor = db.query(TABLE_COURSES, ALL, _ID_COURSE + " = " + aLong, null, null, null, null);
        cursor.moveToFirst();
        String date = cursor.getString(cursor.getColumnIndex(NOM_COLONNE_COURSE_DATE));
        String name = cursor.getString(cursor.getColumnIndex(NOM_COLONNE_COURSE_NAME));
        cursor.close();
        return new Course(name,date);
    }

    @Override
    public Course retrieveByName(String name) throws Exception {
        Cursor cursor = db.query(TABLE_COURSES, ALL, NOM_COLONNE_COURSE_NAME + " = " + name, null, null, null, null);
        cursor.moveToFirst();
        String date = cursor.getString(cursor.getColumnIndex(NOM_COLONNE_COURSE_DATE));
        String namecourse = cursor.getString(cursor.getColumnIndex(NOM_COLONNE_COURSE_NAME));
        cursor.close();
        return new Course(namecourse,date);
    }

    @Override
    public List<Course> findAll() throws Exception {
        List<Course> courses = new ArrayList<Course>();
        Cursor cursor = db.query(TABLE_COURSES, ALL, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            String date = cursor.getString(cursor.getColumnIndex(NOM_COLONNE_COURSE_DATE));
            String namecourse = cursor.getString(cursor.getColumnIndex(NOM_COLONNE_COURSE_NAME));

            Course c = new Course(namecourse,date);
            courses.add(c);
            cursor.moveToNext();
        }
        cursor.close();
        return courses;
    }

    @Override
    public void update(Course course) throws Exception {
        ContentValues values = new ContentValues();
        values.put(NOM_COLONNE_COURSE_NAME,course.getName());
        values.put(NOM_COLONNE_COURSE_DATE,course.getDate());
        db.update(TABLE_COURSES, values, _ID_COURSE + " = " + course.getId(), null);
    }

    @Override
    public void delete(Long aLong) throws Exception {
        db.delete(TABLE_COURSES, _ID_COURSE + " = " + aLong,null);
    }
}
