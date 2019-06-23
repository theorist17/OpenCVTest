package com.example.opencvtest;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Realtime extends AppCompatActivity {
    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
            Log.e("opencv", "OpenCV load not successfully");
        } else {
            // System.loadLibrary("openCVLibrary346");
            Log.d("opencv", "OpenCV load successfully");
        }
    }

    ImageView ivGraph;

    private Socket client;
    private static final String serverName = "175.113.152.102";
    private static final int serverPort = 17171;
    private InputStream is;
    private boolean connection;
    private TextView tvTest;
    private Button connect;
    private Activity thisActivity;
    ArrayList<Bitmap> bmplist;
    int downloded = 0;

    Mat received;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime);
        ivGraph = findViewById(R.id.realtimeGraph);

        Intent intent = new Intent(this.getIntent());
        int one = intent.getIntExtra("qq", -1);

        // CV Loaded Test
//        Mat test = Mat.zeros(100,400, CvType.CV_8UC3);
//        Imgproc.putText(test, "hi there ;)", new Point(30,80), Core.FONT_HERSHEY_SCRIPT_SIMPLEX, 2.2, new Scalar(200,200,0),2);
//        updateGraph(test);

        bmplist = new ArrayList<>();
        ActivityCompat.requestPermissions(Realtime.this, new String[]{Manifest.permission.INTERNET},1);
        connect();
//        Drawable myDrawable = getResources().getDrawable(R.drawable.airplane);
//        Bitmap bitmap      = ((BitmapDrawable) myDrawable).getBitmap();
//
//        ivGraph.setImageBitmap(bitmap);

//        Drawable myDrawable = getResources().getDrawable(R.drawable.airplane);
//        bitmap      = ((BitmapDrawable) myDrawable).getBitmap();
        //ivGraph.setImageBitmap(bitmap);


    }

    @Override
    public void onResume() {;
        super.onResume();
        connection = true;
        while(downloded != 1);
        for(int i = 0 ; i <1 ;i++) {
            updateGraph(bmplist.get(i));
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        connection = false;
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
//                        Realtime.this.runOnUiThread(new Runnable() {
//                            public void run() {
//                                //Do your UI operations like dialog opening or Toast here
//                                tvTest.setText("Connected");
//                                Toast.makeText(getApplicationContext(), "Messege send", Toast.LENGTH_SHORT).show();
//                            }
//                        });
                    }

                    // Receive from server
                    // Entering in the middle of stream
                    char ch;
                    byte[] buffer = new byte[10485760];
                    int numRead, sumRead, numToRead;
                    StringBuilder lenString = new StringBuilder();
                    StringBuilder dataString = new StringBuilder();
                    dataString.setLength(1000000);

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
                                    numRead = is.read(buffer, sumRead, numToRead); //numRead
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
                            numRead = is.read(buffer, sumRead, numToRead); //numRead
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

                        // draw frame
                        byte[] bitmapdata = new byte[0];
                        try {
                            bitmapdata = Base64.decode(data.getString("img"), Base64.DEFAULT);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //here the data coming from server is assumed in Base64
                        //if you are sending bytes in plain string you can directly convert it to byte array
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);

                        //faster code?
//                        InputStream inputStream  = new ByteArrayInputStream(bitmapdata);
//                        Bitmap bitmap  = BitmapFactory.decodeStream(inputStream);

                        //android OpenCv function
                        if (bitmap == null) {
                            Log.e("opencv", "Couldn't decode bitmap.");
                        }

//                        received = new Mat(bitmap.getWidth(), bitmap.getHeight(), CvType.CV_8UC3);
//                        org.opencv.android.Utils.bitmapToMat(bitmap, received);
//
//                        updateGraph(received);
                        bmplist.add(bitmap);
                        downloded++;
                        if (downloded==1)
                            break;
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
//                    Realtime.this.runOnUiThread(new Runnable() {
//                        public void run() {
//                            //Do your UI operations like dialog opening or Toast here
//                            Toast.makeText(getApplicationContext(), "Unknown host please make sure IP address", Toast.LENGTH_SHORT).show();
//                        }
//                    });

                }
                catch (IOException e) {
                    Log.d("socket", "IOException");
                    e.printStackTrace();
//                    Realtime.this.runOnUiThread(new Runnable() {
//                        public void run() {
//                            //Do your UI operations like dialog opening or Toast here
//                            Toast.makeText(getApplicationContext(), "Error Occured"+ "  " + serverName, Toast.LENGTH_SHORT).show();
//                        }
//                    });
                }
            }
        }).start();
    }

    public boolean updateGraph(Mat src){
        if (ivGraph == null)
            return false;

        Bitmap bmp = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(src, bmp);
        ivGraph.setImageBitmap(bmp);

        return true;
    }

    public boolean updateGraph(Bitmap bitmap){
        if (ivGraph == null)
            return false;

        //Drawable myDrawable = getResources().getDrawable(R.drawable.airplane);
        //bitmap      = ((BitmapDrawable) myDrawable).getBitmap();

        ivGraph.setImageBitmap(bitmap);


//        // Define an offset value between canvas and bitmap
//        int offset = 0;
//
//        // Initialize a new Bitmap to hold the source bitmap
//        Bitmap dstBitmap = Bitmap.createBitmap(
//                bitmap.getWidth() + offset * 2, // Width
//                bitmap.getHeight() + offset * 2, // Height
//                Bitmap.Config.ARGB_8888 // Config
//        );
//
//        // Initialize a new Canvas instance
//        Canvas canvas = new Canvas(dstBitmap);
//
//        // Draw a solid color on the canvas as background
//        canvas.drawColor(Color.LTGRAY);
//
//                /*
//                    public void drawBitmap (Bitmap bitmap, float left, float top, Paint paint)
//                        Draw the specified bitmap, with its top/left corner at (x,y), using the
//                        specified paint, transformed by the current matrix.
//
//                        Note: if the paint contains a maskfilter that generates a mask which extends
//                        beyond the bitmap's original width/height (e.g. BlurMaskFilter), then the
//                        bitmap will be drawn as if it were in a Shader with CLAMP mode. Thus the
//                        color outside of the original width/height will be the edge color replicated.
//
//                        If the bitmap and canvas have different densities, this function will take
//                        care of automatically scaling the bitmap to draw at the same density
//                        as the canvas.
//
//                    Parameters
//                        bitmap : The bitmap to be drawn
//                        left : The position of the left side of the bitmap being drawn
//                        top : The position of the top side of the bitmap being drawn
//                        paint : The paint used to draw the bitmap (may be null)
//                */
//
//        //Finally, Draw the source bitmap on the canvas
//        canvas.drawBitmap(
//                bitmap, // Bitmap
//                offset, // Left
//                offset, // Top
//                null // Paint
//        );
//
//        // Display the newly created bitmap on app interface
//        ivGraph.setImageBitmap(dstBitmap);

        return true;
    }

}
