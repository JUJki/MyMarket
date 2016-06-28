package com.mymarket.gcm.julien;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.mymarket.gcm.julien.DAO.impl.DAOConstants;
import com.mymarket.gcm.julien.DAO.impl.UserDAO;
import com.mymarket.gcm.julien.modeles.User;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class RegistrationIntentService extends IntentService implements DAOConstants {
    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};
    private UserDAO userDAO;

    public RegistrationIntentService() {
        super(TAG);
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.i(TAG, "GCM Registration Token: " + token);
            sendRegistrationToServer(token);
            subscribeTopics(token);
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }
        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(String token) {
        User user = new User(token);
        ServerRequests serverRequest = new ServerRequests(this);
        serverRequest.storeUserDataInBackground(user);
        //this.insertSqLITE(token);
    }

    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }

    public void insertProvider(String input){
        if(!TextUtils.isEmpty(input)){
            try {
                ContentValues user = new ContentValues();
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String formattedDate = df.format(c.getTime());
                user.put(NOM_COLONNE_TOKENDEVISE_USER, input);
                getContentResolver().insert(CONTENT_URI_USER, user);
                Log.i(TAG, "OK INSERT: ");
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG, "ERROR insert 1: ");
            }
        }
        else{
            Log.i(TAG, "ERROR insert 2: ");
        }
    }

    public void insertSqLITE(String input ){
        if(!TextUtils.isEmpty(input)){
            User p = new User(input);
            this.userDAO = new UserDAO(this);
            try {
                this.userDAO.open();
                this. userDAO.create(p);
                this. userDAO.close();
                Log.i(TAG, "OK INSERT: ");

            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG, "ERROR insert 1: ");
            }
        }
        else{
            Log.i(TAG, "ERROR insert 2: ");
        }
    }

}
