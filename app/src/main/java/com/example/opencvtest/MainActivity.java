package com.example.opencvtest;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

// Socket programming
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

// IO
import java.io.IOException;

// utilities and widget
import android.util.Base64;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;

import android.support.v4.app.ActivityCompat;
import android.Manifest;

// json
import org.json.JSONException;
import org.json.JSONObject;

// opencv
import org.opencv.android.OpenCVLoader;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

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

    graphFragment graphFragment;

    Mat received;
    static {
        if (!OpenCVLoader.initDebug()){
            Log.e("opencv", "err");
        }
       // System.loadLibrary("opencv_java");
    }

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

                graphFragment = new graphFragment();
                mainFrag = graphFragment;
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
                    byte[] buffer = new byte[10485760];
                    int numRead, sumRead, numToRead;
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
                                numRead = 0;

                                sumRead = 0;
                                while(numToRead!=0){ //여기서 numToRead가 한 삼십만바이트면 십만바이트만 읽고 반환하기도 함.
                                    numRead = is.read(buffer, numRead, numToRead); //numRead
                                    numToRead -= numRead;
                                    sumRead += numRead;
                                    if (numRead < 0)
                                        break;
                                }

                                // Fetching buffer into string buffer
                                dataString.delete(0, dataString.length());
                                for (int i = 0 ; i < sumRead; i++){
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
                    JSONObject data = null;
                    while(connection){
                        ch = (char) is.read();
                        while(ch!='\n'){
                            lenString.append(ch);
                            ch = (char) is.read();
                        }

                        //Log.d("LENG", lenString.toString());
                        numToRead = Integer.parseInt(lenString.toString());
                        numRead = 0;
                        lenString.delete(0, lenString.length());

                        sumRead = 0;
                        while(numToRead!=0){ //여기서 numToRead가 한 삼십만바이트면 십만바이트만 읽고 반환하기도 함.
                            numRead = is.read(buffer, numRead, numToRead); //numRead
                            numToRead -= numRead;
                            sumRead += numRead;
                            if (numRead < 0)
                                break;
                        }

                        // Fetching buffer into string buffer
                        dataString.delete(0, dataString.length());
                        for (int i = 0 ; i < sumRead; i++){
                            dataString.append((char)buffer[i]);
                        }
                        //Log.d("DATA", dataString.toString());

                        // json paring
                        try {
                            data = new JSONObject(dataString.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // draw frame --> inside graphFragment
                        if(graphFragment.isCVReady()) {
                            byte[] bitmapdata = new byte[0];
                            try {
                                bitmapdata = Base64.decode(data.getString("img"), Base64.DEFAULT);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            //here the data coming from server is assumed in Base64
                            //if you are sending bytes in plain string you can directly convert it to byte array
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);

                            //android OpenCv function
                            if (bitmap == null) {
                                Log.e("opencv", "Couldn't decode bitmap.");
                            }

                            //received = new Mat(bitmap.getWidth(), bitmap.getHeight(), CvType.CV_8UC3);
                            //org.opencv.android.Utils.bitmapToMat(bitmap, received);

                            //graphFragment.updateGraph(received);
                            graphFragment.updateGraph(bitmap);
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

    public boolean toggleConnection(){
        connection = !connection;
        return connection;
    }


}
