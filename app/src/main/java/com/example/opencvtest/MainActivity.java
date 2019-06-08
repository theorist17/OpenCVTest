package com.example.opencvtest;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

// Socket programming
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

// IO
import java.io.PrintWriter;
import java.io.IOException;

// utilities and widget
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;

import android.support.v4.app.ActivityCompat;
import android.Manifest;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private Socket client;
    private static final String serverName = "175.113.152.102";
    private static final int serverPort = 17171;
    private InputStream is;
    private boolean connection;
    private TextView tvTest;
    private Button connect;
    private Activity thisActivity;

    Fragment mainFrag;
    ImageButton graphButton;
    ImageButton exerciseButton;
    ImageButton accountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        thisActivity = this;
        tvTest = findViewById(R.id.tvTest);
        connect = findViewById(R.id.button);

        connect.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                ActivityCompat.requestPermissions(thisActivity,new String[]{Manifest.permission.INTERNET},1);
                tvTest.setText("Clicked");
                connect();
            }
        });

        graphButton = findViewById(R.id.graphButton);
        exerciseButton = findViewById(R.id.exercizeButton);
        accountButton = findViewById(R.id.accountButton);

        mainFrag = new MainFragment();


        graphButton.setOnClickListener(new ImageButton.OnClickListener(){
            @Override
            public void onClick(View view){

                mainFrag = new graphFragment();

                switchFrag();
            }
        });

        exerciseButton.setOnClickListener(new ImageButton.OnClickListener(){
            @Override
            public void onClick(View view) {
                mainFrag = new exerciseFragment();

                switchFrag();
            }
        });

        accountButton.setOnClickListener(new ImageButton.OnClickListener(){
            @Override
            public void onClick(View view) {
                mainFrag = new accountFragment();

                switchFrag();
            }
        });
    }

    public void connect() {
        new Thread(new Runnable(){

            @Override
            public void run() {
                try {
                    Log.d("socket", "connecting " + serverName + ' ' + serverPort);
                    client = new Socket(serverName, serverPort);
                    is = client.getInputStream();

                    // Connected
                    connection = true;
                    Log.d("socket", "connected " + connection);

                    // Toast in background becauase Toast cannnot be in main thread you have to create runOnuithread.
                    // this is run on ui thread where dialogs and all other GUI will run.
                    if (client.isConnected()) {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                //Do your UI operations like dialog opening or Toast here
                                tvTest.setText("Connected");
                                Toast.makeText(getApplicationContext(), "Messege send", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    // Receive from server
                    // Entering in the middle of stream
                    char ch;
                    byte[] buffer = new byte[1024];
                    int numRead, numToRead;
                    StringBuilder lenString = new StringBuilder();
                    StringBuilder dataString = new StringBuilder();

                    // Entering in the middle of stream
                    try {
                        while(true) {
                            ch = (char) is.read();
                            while ('0' <= ch && ch <= '9') { // digit sequence
                                lenString.append(ch);
                                ch = (char) is.read();
                            }
                            if(ch=='\n'){//ending with line feed
                                //Log.d("LENG", lenString.toString());
                                numToRead = Integer.parseInt(lenString.toString());
                                numRead = is.read(buffer, 0, numToRead);

                                if(numRead!= numToRead){
                                    Log.d("socket", "Failed to locate first position to read.");
                                    try {
                                        client.close();
                                        connection = false;
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                                // Fetching buffer into string buffer
                                dataString.delete(0, dataString.length());
                                for (int i = 0 ; i < numRead; i++){
                                    dataString.append((char)buffer[i]);
                                }
                                //Log.d("DATA", dataString.toString());

                                break;
                            } else {
                                lenString.delete(0, lenString.length());
                            }
                        }
                    } catch (IOException e){
                        Log.d("socket", "An input stream error on first read.");
                    }

                    // Parse json data
                    lenString.delete(0, lenString.length());
                    JSONObject data;
                    while(connection){
                        ch = (char) is.read();
                        while(ch!='\n'){
                            lenString.append(ch);
                            ch = (char) is.read();
                        }

                        //Log.d("LENG", lenString.toString());
                        numToRead = Integer.parseInt(lenString.toString());
                        numRead = is.read(buffer, 0, numToRead);
                        lenString.delete(0, lenString.length());

                        if(numRead!= numToRead){
                            Log.d("socket", "The parser of JSON didn't read enough bytes.");
                            try {
                                client.close();
                                connection = false;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        // Fetching buffer into string buffer
                        dataString.delete(0, dataString.length());
                        for (int i = 0 ; i < numRead; i++){
                            dataString.append((char)buffer[i]);
                        }
                        //Log.d("DATA", dataString.toString());

                        // json paring
                        try {
                            data = new JSONObject(dataString.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    // closing connection
                    try {
                        client.close();
                        connection = false;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                catch (UnknownHostException e2){
                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            //Do your UI operations like dialog opening or Toast here
                            Toast.makeText(getApplicationContext(), "Unknown host please make sure IP address", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                catch (IOException e) {
                    Log.d("socket", "IOException");
                    e.printStackTrace();
                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            //Do your UI operations like dialog opening or Toast here
                            Toast.makeText(getApplicationContext(), "Error Occured"+ "  " + serverName, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        }).start();
    }

    public void switchFrag(){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment, mainFrag);
        fragmentTransaction.commit();
    }

    public void runningFrag(){
        mainFrag = new runningFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment, mainFrag);
        fragmentTransaction.commit();
    }
}
