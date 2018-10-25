package com.example.android.tenniscounter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    int pointsA = 0;
    int pointsB = 0;
    int gamesA = 0;
    int gamesB = 0;
    int setsA = 0;
    int setsB = 0;

    public void addPointForA(View v) {
        if (pointsA == 30) {
            pointsA += 10;
        } else if (pointsA == 40) {
            pointsA = 0;
            addGameForA(v);
        } else {
            pointsA += 15;
        }
        display(pointsA, R.id.pointsA);
    }

    public void addGameForA(View v) {
        resetPoints();
        if (gamesA >= 5) {
            if (gamesB == 5 && gamesA == 5) {
                gamesA += 1;

            } else if (gamesA == 6) {
                addSetForA(v);
            } else {
                gamesA = 0;
                addSetForA(v);
            }
        } else {
            gamesA += 1;
        }
        display(gamesA, R.id.gameScoreA);
    }

    public void addSetForA(View v) {
        resetGames();
        if (setsA >= 2) {
            TextView scoreView = (TextView) findViewById(R.id.pointsA);
            scoreView.setText(String.valueOf("Win"));
            setsA = 3;
        } else {
            setsA += 1;
        }
        display(setsA, R.id.setScoreA);
    }

    public void addPointForB(View v) {
        if (pointsB == 30) {
            pointsB += 10;
        } else if (pointsB == 40) {
            pointsB = 0;
            addGameForB(v);
        } else {
            pointsB += 15;
        }
        display(pointsB, R.id.pointsB);
    }

    public void addGameForB(View v) {
        resetPoints();
        if (gamesB >= 5) {
            if (gamesA == 5 && gamesB == 5) {
                gamesB += 1;

            } else if (gamesB == 6) {
                addSetForB(v);
            } else {
                gamesB = 0;
                addSetForB(v);
            }
        } else {
            gamesB += 1;
        }
        display(gamesB, R.id.gameScoreB);
    }

    public void addSetForB(View v) {
        resetGames();
        if (setsB >= 2) {
            TextView scoreView = (TextView) findViewById(R.id.pointsB);
            scoreView.setText(String.valueOf("Win"));
            setsB = 3;
        } else {
            setsB += 1;
        }
        display(setsB, R.id.setScoreB);
    }

    public void reset(View v) {
        resetPoints();
        resetGames();
        setsA = 0;
        setsB = 0;
        display(setsA, R.id.setScoreA);
        display(setsB, R.id.setScoreB);
    }

    public void resetPoints() {
        pointsA = 0;
        pointsB = 0;
        display(pointsA, R.id.pointsA);
        display(pointsB, R.id.pointsB);
    }

    public void resetGames() {
        gamesA = 0;
        gamesB = 0;
        display(gamesA, R.id.gameScoreA);
        display(gamesB, R.id.gameScoreB);
    }

    public void display(int score, int scoreType) {
        TextView scoreView = (TextView) findViewById(scoreType);
        scoreView.setText(String.valueOf(score));
    }

}
