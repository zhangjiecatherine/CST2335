package com.example.androidlabs;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        //view the Linear page
        //setContentView(R.layout.activity_main_linear);

        //view the grid page
        //setContentView(R.layout.activity_main_grid);

        //view the relative page
        setContentView(R.layout.activity_main_relative);
    }
}
