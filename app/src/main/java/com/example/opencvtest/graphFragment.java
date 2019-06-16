package com.example.opencvtest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;

import org.opencv.core.Mat;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

public class graphFragment extends android.support.v4.app.Fragment {

    private RecyclerView mRecyclerView;
    private ListAdapter mListadapter;
    private TextView mTextViewEmpty;
    private ProgressBar mProgressBarLoading;
    private ImageView mImageViewEmpty;
    private TextView title;



    ImageView ivGraph;
    boolean cvLoaded = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_graph, container, false);
        //ivGraph = view.findViewById(R.id.realtimeGraph);

        title = (TextView)view.findViewById(R.id.titleText);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mTextViewEmpty = (TextView)view.findViewById(R.id.textViewEmpty);
        mImageViewEmpty = (ImageView)view.findViewById(R.id.imageViewEmpty);
        mProgressBarLoading = (ProgressBar)view.findViewById(R.id.progressBarLoading);

        title.setBackgroundColor(Color.parseColor("#EEEEEE"));


        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        ArrayList data = new ArrayList<DataNote>();
        for (int i = 0; i < DataNoteInformation.id.length; i++)
        {
            data.add(
                    new DataNote
                            (
                                    DataNoteInformation.id[i],
                                    DataNoteInformation.textArray[i],
                                    DataNoteInformation.dateArray[i],
                                    DataNoteInformation.imgArray[i]
                            ));
        }

        mListadapter = new ListAdapter(data);
        mRecyclerView.setAdapter(mListadapter);

        return view;
    }

    public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
        private ArrayList<DataNote> dataList;

        public ListAdapter(ArrayList<DataNote> data) {
            this.dataList = data;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView textViewText;
            TextView textViewComment;
            TextView textViewDate;
            ImageView cardImage;

            public ViewHolder(View itemView) {
                super(itemView);

                this.cardImage = (ImageView) itemView.findViewById(R.id.cardImage);
                this.textViewText = (TextView) itemView.findViewById(R.id.text);
                this.textViewComment = (TextView) itemView.findViewById(R.id.comment);
                this.textViewDate = (TextView) itemView.findViewById(R.id.date);
            }
        }

        @Override
        public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);

            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ListAdapter.ViewHolder holder, final int position) {

            holder.cardImage.setImageResource(dataList.get(position).getImg());
            holder.textViewText.setText(dataList.get(position).getText());
            holder.textViewComment.setText(dataList.get(position).getComment());
            holder.textViewDate.setText(dataList.get(position).getDate());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent =  new Intent(getActivity(), Realtime.class);
                    intent.putExtra("qq",1);
                    startActivity(intent);

                }
            });
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }
    }

//    @Override
//    public void onResume() {;
//        super.onResume();
//        if(OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this.getActivity(), mOpenCVLoaderCallBack))
//            return;
//        else
//            return;
//    }

//    private BaseLoaderCallback mOpenCVLoaderCallBack = new BaseLoaderCallback(this.getActivity()) {
//        @Override
//        public void onManagerConnected(int status) {
//            switch (status) {
//                case LoaderCallbackInterface.SUCCESS:
//                {
//                    //System.loadLibrary("opencv_java");
//                    Log.i("opencv", "OpenCV loaded successfully");
//
//                    Mat test = Mat.zeros(100,400, CvType.CV_8UC3);
//                    Imgproc.putText(test, "hi there ;)", new Point(30,80), Core.FONT_HERSHEY_SCRIPT_SIMPLEX, 2.2, new Scalar(200,200,0),2);
//                    updateGraph(test);
//
//                    cvLoaded = true;
//                } break;
//                default:
//                {
//                    super.onManagerConnected(status);
//                } break;
//
//            }
//        }
//    };

//    @Override
//    public void onPause() {;
//        super.onPause();
//        ((MainActivity)getActivity()).toggleConnection();
//        cvLoaded = false;
//    }

//    public boolean isCVReady(){
//        return cvLoaded;
//    }


}
