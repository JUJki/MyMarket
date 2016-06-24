package com.mymarket.gcm.julien.DAO.impl;

import android.net.Uri;

/**
 * Created by julien on 24/06/2016.
 */
public interface DAOConstants {
    public static final String TABLE_USERS = "users";
    public static final String _ID_USER = "_iduser";
    public static final String NOM_COLONNE_TOKENDEVISE_USER = "tokendevise";
    public static final String NOM_COLONNE_USER_DATE = "dateuser";

    public static final String TABLE_COURSES = "courses";
    public static final String _ID_COURSE = "_idcourse";
    public static final String NOM_COLONNE_COURSE_NAME = "namecourse";
    public static final String NOM_COLONNE_COURSE_DATE = "datecourse";


    public static final String CONTENT_PROVIDER_MIME_USER = "vnd.android.cursor.item/vnd.com.mymarket.gcm.julien.modeles.user";
    public static final Uri CONTENT_URI_USER = Uri.parse("content://com.mymarket.gcm.DAO.impl.user");

    public static final String CONTENT_PROVIDER_MIME_COURSE = "vnd.android.cursor.item/vnd.com.mymarket.gcm.julien.modeles.course";
    public static final Uri CONTENT_URI_COURSE = Uri.parse("content://com.mymarket.gcm.DAO.impl.course");
}
