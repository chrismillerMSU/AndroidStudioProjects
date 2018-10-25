package com.example.android.verticalsliderandroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class VerticalSliderActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView sliderText = findViewById(R.id.verticalSeekBarText);
        sliderText.setTextSize(48);
        VerticalSeekBar verticalSeekBar = findViewById(R.id.verticalSeekbar);
        verticalSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

            @Override


            public void onStopTrackingTouch(SeekBar seekBar) {


            }





            @Override


            public void onStartTrackingTouch(SeekBar seekBar) {


            }





            @Override


            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


                sliderText.setText(""+progress);


            }


        });


    }


}
