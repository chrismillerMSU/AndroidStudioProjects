package com.example.android.seekbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button submitButton;
    SeekBar verticalSeekBar;
    SeekBar simpleSeekBar;

    int progressChangedValue = 0;
    int verticalChangedValue = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // initiate  views
        simpleSeekBar=(SeekBar)findViewById(R.id.simpleSeekBar);
        verticalSeekBar=(SeekBar)findViewById(R.id.verticalSeekBar);
        verticalSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                verticalChangedValue = progress - 100;
                displayMessage(verticalChangedValue, (TextView) findViewById(R.id.verticalProgress), "Speed: ");
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setProgress(100);
            }

        });
        // perform seek bar change listener event used for getting the progress value
        simpleSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress - 100;
               /* Toast.makeText(MainActivity.this, "Seek bar progress is :" + progressChangedValue,
                        Toast.LENGTH_SHORT).show();*/
               displayMessage(progressChangedValue, (TextView) findViewById(R.id.progress), "Turn angle: ");
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(MainActivity.this, "Seek bar progress is :" + progressChangedValue,
                        //Toast.LENGTH_SHORT).show();
                seekBar.setProgress(100);

            }
        });

    }
    private void displayMessage(int value, TextView progressTextView, String text){
        //TextView progressTextView = (TextView) findViewById(R.id.progress);
        progressTextView.setText(text + (value));

    }


}