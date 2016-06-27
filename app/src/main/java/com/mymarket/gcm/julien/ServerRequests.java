package com.mymarket.gcm.julien;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.mymarket.gcm.julien.modeles.Course;
import com.mymarket.gcm.julien.modeles.User;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by julien on 27/06/2016.
 */
public class ServerRequests {
    private static final String TAG = "ServerRequest";
    public static final int CONNECTION_TIMEOUT = 1000 * 15;
    public static final String SERVER_ADDRESS = "http://82.241.46.118:49200/mymarket/";
    public ArrayList<User> arrayUSer = new ArrayList<User>();


    public ServerRequests(Context context) {

    }

    public void storeUserDataInBackground(User user) {
        new StoreUserDataAsyncTask(user).execute();
    }

    public void fetchUserDataAsyncTask(User user) {
        new fetchUserDataAsyncTask(user).execute();
    }
    public  ArrayList<User> fetchAllUserDataAsyncTask() {
        fetchAllUserDataAsyncTask fetchUser = new fetchAllUserDataAsyncTask(arrayUSer);
        fetchUser.execute();
        return this.arrayUSer;
    }

    public void storeCourseDataAsyncTask(Course course) {
        new StoreCourseDataAsyncTask(course).execute();
    }

    public class StoreUserDataAsyncTask extends AsyncTask<Void, Void, Void> {

        User user;

        public StoreUserDataAsyncTask(User user) {
            this.user = user;
        }

        @Override
        protected Void doInBackground(Void... params) {
            //ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            List<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("token", user.getTeokendevise()));
            Log.i(TAG, "Insert User token: "+user.getTeokendevise());
            HttpParams httpRequestParams = getHttpRequestParams();
            Log.i(TAG, "Insert User: ");
            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS
                    + "Register.php");

            try {
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                HttpResponse httpResponse = client.execute(post);
                HttpEntity entity = httpResponse.getEntity();
                String result = EntityUtils.toString(entity);
                JSONObject jObject = new JSONObject(result);
                Log.i(TAG, jObject.toString());
                Log.i(TAG, "Insert User: OK ");
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        private HttpParams getHttpRequestParams() {
            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams,
                    CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams,
                    CONNECTION_TIMEOUT);
            return httpRequestParams;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    public class fetchUserDataAsyncTask extends AsyncTask<Void, Void, User> {
        User user;


        public fetchUserDataAsyncTask(User user) {
            this.user = user;
        }

        @Override
        protected User doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("token", user.getTeokendevise()));
            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams,
                    CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams,
                    CONNECTION_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS
                    + "FetchUserData.php");

            User returnedUser = null;



            try {
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                HttpResponse httpResponse = client.execute(post);

                HttpEntity entity = httpResponse.getEntity();
                String result = EntityUtils.toString(entity);
                JSONObject jObject = new JSONObject(result);

                if (jObject.length() != 0){
                    String token = jObject.getString("token");
                    String date = jObject.getString("createdAt");
                    returnedUser = new User(token, date);
                }

            } catch (Exception e) {

                e.printStackTrace();
            }

            return returnedUser;
        }

        @Override
        protected void onPostExecute(User returnedUser) {
            super.onPostExecute(returnedUser);
        }
    }

    public class fetchAllUserDataAsyncTask extends AsyncTask<ArrayList<User>, Void, ArrayList<User>> {
        ArrayList<User> arrayuser;

        public fetchAllUserDataAsyncTask(ArrayList<User> arrayListUser) {

            this.arrayuser = arrayListUser;
        }

        @Override
        protected ArrayList<User> doInBackground(ArrayList<User>... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams,
                    CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams,
                    CONNECTION_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS
                    + "FetchAllUserData.php");

            try {
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                HttpResponse httpResponse = client.execute(post);

                HttpEntity entity = httpResponse.getEntity();
                String result = EntityUtils.toString(entity);
                JSONArray jObject = new JSONArray(result);
                Log.i(TAG, result.toString());
                Log.i(TAG, "nb"+jObject.length());
                if (jObject.length() != 0){
                    for (int i=0; i<jObject.length(); i++) {
                        JSONObject value = jObject.getJSONObject(i);
                        String token = value.getString("token");
                        Log.i(TAG, "nb"+token);
                        arrayuser.add(new User(token));
                    }
                }

            } catch (Exception e) {

                e.printStackTrace();
            }
            return arrayuser;
        }

        @Override
        protected void onPostExecute(ArrayList<User> arrayUser) {
            super.onPostExecute(arrayUser);
            arrayUSer = arrayUser;
        }
    }

    public class StoreCourseDataAsyncTask extends AsyncTask<Void, Void, Void> {

        Course course;

        public StoreCourseDataAsyncTask(Course course) {
            this.course = course;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("name", course.getName()));
            HttpParams httpRequestParams = getHttpRequestParams();

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS
                    + "UpdaterCourse.php");

            try {
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                client.execute(post);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        private HttpParams getHttpRequestParams() {
            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams,
                    CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams,
                    CONNECTION_TIMEOUT);
            return httpRequestParams;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    public class fetchCourseUserDataAsyncTask extends AsyncTask<Void, Void, User> {
        ArrayList<Course> arrayCourse;
        @Override
        protected User doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("data", ""));
            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams,
                    CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams,
                    CONNECTION_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS
                    + "FetchCourseData.php");

            try {
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                HttpResponse httpResponse = client.execute(post);

                HttpEntity entity = httpResponse.getEntity();
                String result = EntityUtils.toString(entity);
                JSONObject jObject = new JSONObject(result);

                if (jObject.length() != 0){

                }

            } catch (Exception e) {

                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(User returnedUser) {
            super.onPostExecute(returnedUser);
        }
    }
}
