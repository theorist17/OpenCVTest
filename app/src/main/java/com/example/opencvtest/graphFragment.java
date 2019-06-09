package com.example.opencvtest;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;

import org.opencv.core.Mat;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

public class graphFragment extends android.support.v4.app.Fragment {

    ImageView ivGraph;
    boolean cvLoaded = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_graph, container, false);
        ivGraph = view.findViewById(R.id.realtimeGraph);

        return view;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }

    public boolean updateGraph(Mat src){
        if (ivGraph == null)
            return false;

        Bitmap bmp = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(src, bmp);
        ivGraph.setImageBitmap(bmp);

        return true;
    }

    @Override
    public void onResume() {;
        super.onResume();
        if(OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this.getActivity(), mOpenCVLoaderCallBack))
            return;
        else
            return;
    }

    private BaseLoaderCallback mOpenCVLoaderCallBack = new BaseLoaderCallback(this.getActivity()) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    //System.loadLibrary("opencv_java");
                    Log.i("opencv", "OpenCV loaded successfully");

                    Mat test = Mat.zeros(100,400, CvType.CV_8UC3);
                    Imgproc.putText(test, "hi there ;)", new Point(30,80), Core.FONT_HERSHEY_SCRIPT_SIMPLEX, 2.2, new Scalar(200,200,0),2);
                    updateGraph(test);

                    cvLoaded = true;
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;

            }
        }
    };

    @Override
    public void onPause() {;
        super.onPause();
        ((MainActivity)getActivity()).toggleConnection();
        cvLoaded = false;
    }

    public boolean isCVReady(){
        return cvLoaded;
    }


}
