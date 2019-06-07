package com.example.opencvtest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainFragment extends android.support.v4.app.Fragment {

/*
    TextView text1;
    TextView text2;
    TextView text3;
*/


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
/*
       View view = inflater.inflate(R.layout.mainfragment,null);

        text1 = (TextView)view.findViewById(R.id.text1);
        text2 = (TextView)view.findViewById(R.id.text2);
        text3 = (TextView)view.findViewById(R.id.text3);*/
        return inflater.inflate(R.layout.mainfragment, container, false);

        //return super.onCreateView(inflater, container, savedInstanceState);

    }
}
