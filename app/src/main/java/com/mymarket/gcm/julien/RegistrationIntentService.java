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

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class RegistrationIntentService extends IntentService implements DAOConstants {
    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};
    private UserDAO userDAO;
    private AsyncTaskFindOne one;

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
            // [START get_token]
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // [END get_token]
            Log.i(TAG, "GCM Registration Token: " + token);

            // TODO: Implement this method to send any registration to your app's servers.
            sendRegistrationToServer(token);

            // Subscribe to topic channels
            subscribeTopics(token);

            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
            // [END register_for_gcm]
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }


    private void sendRegistrationToServer(String token) {
        User user = this.getOne(token);
        if(user != null){
            this.insertSqLITE(token);
        }
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
    /*public User getUserToken(String token ){
        if(!TextUtils.isEmpty(token)){
            this.userDAO = new UserDAO(this);
            try {
                this.userDAO.open();
                User user = this. userDAO.retrieveByName(token);
                this. userDAO.close();
                Log.i(TAG, "OK INSERT: ");
                return user;

            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG, "ERROR insert 1: ");
                return user;
            }
        }
        else{
            Log.i(TAG, "ERROR insert 2: ");
            return false;
        }
    }*/

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

    public User getOne(String token){
        try {
            this.userDAO.open();
            this.one = new AsyncTaskFindOne();
            this.one.execute(this.userDAO);
            User result = this.one.get();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private class AsyncTaskFindOne extends AsyncTask<UserDAO,Void,User> {
        private Throwable cause = null;
        @Override
        protected User doInBackground(UserDAO... params) {
            try{
                return params[0].retrieveByName("fdfdfdfd");
            }
            catch (Exception e){
                cause = e;
                return null;
            }
        }
    }
}
