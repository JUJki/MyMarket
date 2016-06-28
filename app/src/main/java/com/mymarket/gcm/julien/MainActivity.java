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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.mymarket.gcm.julien.DAO.impl.CourseDAO;
import com.mymarket.gcm.julien.modeles.Course;
import com.mymarket.gcm.julien.modeles.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ProgressBar mRegistrationProgressBar;
    private TextView mInformationTextView;
    private ListView listviemCourse;
    private boolean isReceiverRegistered;
    ServerRequests serverRequest;
    ArrayList courseList = new ArrayList();
    private EditText et;
    private Sender sender;
    private CourseDAO courseDao;

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
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
        this.et = (EditText) findViewById(R.id.messageId);
        this.listviemCourse = (ListView)findViewById(R.id.listView);
        et.requestFocus();
        this.sender = new Sender();
        this.serverRequest = new ServerRequests(MainActivity.this);
        serverRequest.fetchAllUserDataAsyncTask();
        serverRequest.fetchAllCourseDataAsyncTask();
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

    public void onClickList(View v){
        GetListTask getListTask = new GetListTask();
        getListTask.execute();
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
        private List<String> abonne;
        private ArrayList<User> arrayUser;
        private List<String> arrayListToken;
        private Course itemCourse;
        public SendMessageToCloudTask(String message) {
            this.message = message;
            this.itemCourse = new Course(message);
            serverRequest.fetchAllUserDataAsyncTask();
            //this.arrayUser = serverRequest.getTokenUser();
            this.arrayListToken = serverRequest.getTokenUser();
            Log.i(TAG, "user"+arrayListToken.size());
            //this.abonne = "evvzg0qh4Rc:APA91bHxG63qiDpPe-gbFpedFBVZkfBN-ZHZahnFmpPMsh7FeMfAsicd44J5dGw-WPacv8Za4yyWliIchwYulh8YdWha-7ij3h2KVR1nZuwKpkM2LFo3fUUS42YAeh1efCFyWciNqscR";
            this.abonne = this.arrayListToken;
        }
        protected Exception doInBackground(String... params) {
            try {
                if(internet() && abonne.size()>=0) {
                    sender.send(this.message,abonne);
                    serverRequest.storeCourseDataAsyncTask(itemCourse);
                    insertSqLITECourse(this.message);
                }
            } catch (Exception e) {
                e.printStackTrace();
                cause = e;
            }
            return cause;
        }
        @Override
        protected void onPostExecute(Exception e){
            Toast toast = Toast.makeText(MainActivity.this,"Ajout effectué",Toast.LENGTH_LONG);
            toast.show();
            et.setText("");

        }
    }
    private class GetListTask extends AsyncTask<Void, Void, ArrayList<Course>> {
        private ArrayList<Course> arrayCourseAsync = new ArrayList<Course>();
        @Override
        protected void onPostExecute(ArrayList<Course> arraycourse) {
            super.onPostExecute(arraycourse);
            courseList = arraycourse;
            CourseAdapter adapterCourse = new CourseAdapter(courseList, MainActivity.this);
            listviemCourse.setAdapter(adapterCourse);
            Log.i(TAG, courseList.toString());
        }
        @Override
        protected ArrayList<Course> doInBackground(Void... params) {
            serverRequest.fetchAllCourseDataAsyncTask();
            arrayCourseAsync = serverRequest.getAllCourse();
            return arrayCourseAsync;
        }
    }

    public void insertSqLITECourse(String input ){
        if(!TextUtils.isEmpty(input)){
            Course c = new Course (input);
            this.courseDao = new CourseDAO(this);
            try {
                this.courseDao.open();
                this. courseDao.create(c);
                this. courseDao.close();
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
