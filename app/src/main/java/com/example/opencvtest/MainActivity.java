package com.example.opencvtest;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    Fragment mainFrag;
    ImageButton graphButton;
    ImageButton exerciseButton;
    ImageButton accountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
