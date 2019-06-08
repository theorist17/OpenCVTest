package com.example.opencvtest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class graphFragment extends android.support.v4.app.Fragment {
/*
    Text graphText;

    public graphFragment(){

    }*/

    ImageView ivGraph;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ivGraph = getView().findViewById(R.id.realtimeGraph);

        return inflater.inflate(R.layout.activity_graph, container, false);
        //return super.onCreateView(inflater, container, savedInstanceState);
    }

    public boolean updateGraphFrame(){
        if (ivGraph == null)
            return false;


        return true;
    }
}
