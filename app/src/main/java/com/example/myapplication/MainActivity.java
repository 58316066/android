package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "UsingThingspeakAPI";
    private static final String THINGSPEAK_CHANNEL_ID = "857378";
    private static final String THINGSPEAK_API_KEY = "MI0O9NCE9DFQ360A"; //GARBAGE KEY
    private static final String THINGSPEAK_API_KEY_STRING = "api_key";
    /* Be sure to use the correct fields for your own app*/
    private static final String THINGSPEAK_FIELD1 = "&field1=";
    private static final String THINGSPEAK_FIELD2 = "&field2=";
    private static final String THINGSPEAK_FIELD3 = "&field3=";
    private static final String THINGSPEAK_FIELD4 = "&field4=";
    private static final String VALUE = "5";
    private static final String THINGSPEAK_UPDATE_URL = "https://api.thingspeak.com/update?";
    private static final String THINGSPEAK_CHANNEL_URL = "https://api.thingspeak.com/channels/";
    private static final String THINGSPEAK_FEEDS_LAST = "/fields/1.json?";

    private static final String PHP_FILE = "http://bnmsgps.hostingerapp.com/thingspeak/thingspeak.php";

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                Log.i("Runnable", "Send Data to ThinkSpeak");
                try {
                    new FetchThingspeakTask().execute();
                } catch (Exception e) {
                    Log.e("ERROR", e.getMessage(), e);
                }
                handler.postDelayed(this, 300000); // 5 Sec.
            }
        };

        handler.postDelayed(r, 1000);
    }


    class FetchThingspeakTask extends AsyncTask<Void, Void, String> {
        protected void onPreExecute() {
            Toast.makeText(MainActivity.this, "Fetching Data from Server.Please Wait...", Toast.LENGTH_SHORT).show();
        }

        protected String doInBackground(Void... urls) {
            try {
//                URL url = new URL(THINGSPEAK_UPDATE_URL + THINGSPEAK_API_KEY_STRING + "=" +
//                        THINGSPEAK_API_KEY + THINGSPEAK_FIELD1 + VALUE + THINGSPEAK_FIELD2 + VALUE + THINGSPEAK_FIELD2 + VALUE + THINGSPEAK_FIELD4 + VALUE
//                );

                URL url = new URL(PHP_FILE);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                Log.i("url : ", url.toString());
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            Log.i("response", response);
            if (response == null) {
                Toast.makeText(MainActivity.this, "There was an error", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(MainActivity.this, "Send Data to ThinkSpeak Success", Toast.LENGTH_SHORT).show();

        }
    }
}