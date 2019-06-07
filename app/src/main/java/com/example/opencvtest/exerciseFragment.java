package com.example.opencvtest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class exerciseFragment extends android.support.v4.app.Fragment {
    boolean check_touch = false;

    public exerciseFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_exercise,null);

        String[] LIST_MENU = {"CORE", "UPPER BODY", "LOWER BODY"} ;

        ListView listview = (ListView)view.findViewById(R.id.exerList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, LIST_MENU);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Toast.makeText(getActivity(), "touch", Toast.LENGTH_SHORT).show();
            }
        });

        if (check_touch)
            view = inflater.inflate(R.layout.activity_running,null);

        return view;

    }

}
