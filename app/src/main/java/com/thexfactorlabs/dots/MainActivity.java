package com.thexfactorlabs.dots;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        Draw draw = new Draw(this);
        setContentView(draw);
        //ActionBar titlebar = getActionBar();
        //if(titlebar != null){
        //    titlebar.hide();
        //}
    }
}
