package com.example.opencvtest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class graphFragment extends android.support.v4.app.Fragment {
/*
    Text graphText;

    public graphFragment(){

    }*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {


        return inflater.inflate(R.layout.activity_graph, container, false);
        //return super.onCreateView(inflater, container, savedInstanceState);
    }
}