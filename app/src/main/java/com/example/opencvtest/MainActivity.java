package com.example.opencvtest;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

// Socket programming
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


public class MainActivity extends AppCompatActivity {

    private Socket client;
    private static final String ipadres = "175.113.152.102";
    private static final int portnumber = 17171;
    private PrintWriter printwriter;
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
        //testing

        graphButton = (ImageButton)findViewById(R.id.graphButton);
        exerciseButton = (ImageButton)findViewById(R.id.exercizeButton);
        accountButton = (ImageButton)findViewById(R.id.accountButton);

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

                    client = new Socket(ipadres, portnumber);

                    printwriter = new PrintWriter(client.getOutputStream(), true);
                    printwriter.write("HI "); // write the message to output stream
                    printwriter.flush();
                    printwriter.close();
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
                }
                catch (UnknownHostException e2){
                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            //Do your UI operations like dialog opening or Toast here
                            Toast.makeText(getApplicationContext(), "Unknown host please make sure IP address", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                catch (IOException e1) {
                    Log.d("socket", "IOException");
                    e1.printStackTrace();
                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            //Do your UI operations like dialog opening or Toast here
                            Toast.makeText(getApplicationContext(), "Error Occured"+ "  " + ipadres, Toast.LENGTH_SHORT).show();
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
