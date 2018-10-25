package com.example.android.seekbarverticle;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SeekBar simpleSeekBar = (SeekBar) findViewById(R.id.simpleSeekBar);
        int maxValue=simpleSeekBar.getMax();
    }


}
