package com.mymarket.gcm.julien;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by julien on 23/06/2016.
 */
public class Sender {

    public static final String API_KEY = "AIzaSyCU5atEe-Hgd-hU_ZH_Zw10XX447eBdtYA";
    private String messageSend;


    public static void send(String message, String abonne) {
        String messageSend = message;

        try {
            // Prepare JSON containing the GCM message content. What to send and where to send.
            JSONObject jGcmData = new JSONObject();
            JSONObject jData = new JSONObject();
            try {
                jData.put( "message" , messageSend);
                jData.put( "title"		, "This is a title. title");
                jData.put( "subtitle"	, "This is a subtitle. subtitle");
                jData.put( "tickerText"	, "Ticker text here...Ticker text here...Ticker text here");
                jData.put( "vibrate"	, 1);
                jData.put( "sound"		, 1);
                jData.put( "largeIcon"	, "large_icon");
                jData.put( "smallIcon"	, "small_icon");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                jGcmData.put("data", jData);
                //jGcmData.put("to", "evvzg0qh4Rc:APA91bHxG63qiDpPe-gbFpedFBVZkfBN-ZHZahnFmpPMsh7FeMfAsicd44J5dGw-WPacv8Za4yyWliIchwYulh8YdWha-7ij3h2KVR1nZuwKpkM2LFo3fUUS42YAeh1efCFyWciNqscR");
                jGcmData.put("to", abonne);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            DataOutputStream printout;
            DataInputStream input;
            // Create connection to send GCM Message request.
            URL url = new URL("https://android.googleapis.com/gcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "key=" + API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput (true);

            // Send GCM message content.
           /* OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jGcmData.toString().getBytes());*/

            printout = new DataOutputStream(conn.getOutputStream ());
            printout.write(jGcmData.toString().getBytes());
            // Read GCM response.
            InputStream inputStream = conn.getInputStream();
            //String resp = IOUtils.toString(inputStream);
            System.out.println(inputStream);

            System.out.println("Unabl1e to send GCM message.");

        } catch (IOException e) {
            System.out.println("Unable to send GCM message.");
            System.out.println("Please ensure that API_KEY has been replaced by the server " +
                    "API key, and that the device's registration token is correct (if specified).");
            e.printStackTrace();
        }
    }

}
