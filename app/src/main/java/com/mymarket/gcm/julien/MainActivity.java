package com.mymarket.gcm.julien;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ProgressBar mRegistrationProgressBar;
    private TextView mInformationTextView;
    private boolean isReceiverRegistered;



    // le message à envoyer
    private EditText et;
    // La liste des abonnés
    private GCMListRegIds listRegIds;
    // Envoi vers le cloud
    private Sender sender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRegistrationProgressBar = (ProgressBar) findViewById(R.id.registrationProgressBar);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    mInformationTextView.setText(getString(R.string.gcm_send_message));
                } else {
                    mInformationTextView.setText(getString(R.string.token_error_message));
                }
            }
        };
        mInformationTextView = (TextView) findViewById(R.id.informationTextView);

        // Registering BroadcastReceiver
        registerReceiver();

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        this.et = (EditText) findViewById(R.id.messageId);
        et.requestFocus();
        this.sender = new Sender();
        this.listRegIds = new GCMListRegIds(this, GCMListRegIds.LIST_NAME);
        Log.i(TAG, this.listRegIds.toString());

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    private void registerReceiver(){
        if(!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }
    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    public void onClickSend(View v) {
        String message = et.getText().toString();
        SendMessageToCloudTask task = new SendMessageToCloudTask(message);
        try {
            if (Build.VERSION.SDK_INT >= 11) {
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                task.execute();
            }
        } catch (Exception e) {
            e.printStackTrace(); //

        }
    }



    // -------------- Utilitaires d'accès -------------------

    public  boolean internet(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null || !(networkInfo.isConnected())) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Internet");
            alertDialog.setMessage("Vérifiez votre connexion !");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            MainActivity.this.finish();
                        }
                    });
            alertDialog.show();
            return false;
        }else
            return true;
    }




    private class SendMessageToCloudTask extends AsyncTask<String, Void, Exception> {
        private String message;
        private Exception cause;

        public SendMessageToCloudTask(String message) {
            this.message = message;
        }

        protected Exception doInBackground(String... params) {
            try {
                if(internet() /*&& abonnes.size()>=0*/) {
                    //MulticastResult result = sender.send(msg, abonnes, 15);
                    sender.send(this.message);
                }
            } catch (Exception e) {
                e.printStackTrace();
                cause = e;
            }
            return cause;
        }
        @Override
        protected void onPostExecute(Exception e){

        }
    }

    private class GetListRegIdsTask extends AsyncTask<Void, String, Void> {
        private String message;
        protected Void doInBackground(Void... params) {
            int number = listRegIds.size();
            String str = number + " subscriber" + (number>1?"s":"") + ", " + listRegIds.getName()+ "\n";

            for (String regId : listRegIds.regIds()) {  // tous les regids
                if(regId.startsWith("APA91")&&regId.length()>20)
                    str = str + regId.substring(0, 20) + "..." + regId.substring(regId.length()-5, regId.length()) +"\n";
                else
                    publishProgress("Regid non conforme ?!: " + regId);
            }
            publishProgress(str);
            return null;
        }
        @Override
        public void onProgressUpdate(String... values) {

        }

    }


}
