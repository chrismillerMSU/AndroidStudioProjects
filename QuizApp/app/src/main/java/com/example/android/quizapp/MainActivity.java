package com.example.android.quizapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getQuestions();
    }

    private String[] questions = new String[20];
    public void getQuestions(){
        questions[0] = getString(R.string.question1);
        questions[1] = getString(R.string.question2);
        questions[2] = getString(R.string.question3);
        /*questions[3] = getString(R.string.question4);
        questions[4] = getString(R.string.question5);
        questions[5] = getString(R.string.question6);*/
    }


    public void clickAnswer1(View v){
        TextView quantityTextView = (TextView) findViewById(R.id.button1);
        String ans = quantityTextView.getText().toString();
    }
}
