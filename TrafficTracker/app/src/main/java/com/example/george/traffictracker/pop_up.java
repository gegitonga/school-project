package com.example.george.traffictracker;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.widget.TextView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.io.IOException;

/**
 * Created by george on 11/20/17.
 */

public class pop_up extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_up);

        TextView mytextprogress = (TextView) findViewById(R.id.myTextProgress);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width*.8),(int)(height*.4));
       CircularProgressBar circularProgressBar = (CircularProgressBar) findViewById(R.id.circularProgressbar);
        circularProgressBar.setProgressWithAnimation(65);
        int idspeed = getIntent().getIntExtra("Avg",0);





        if (idspeed >=0 && idspeed <=10){

            String MyColor = "#ff0000";
            circularProgressBar.setColor(Color.parseColor(MyColor));
            circularProgressBar.setBackgroundColor(adjustAlpha(Color.parseColor(MyColor), 0.3f));
            //mytextprogress.setText(idspeed);
        }else if (idspeed >10 && idspeed <=35){
            String MyColor = "#ff4300";
            circularProgressBar.setColor(Color.parseColor(MyColor));
            circularProgressBar.setBackgroundColor(adjustAlpha(Color.parseColor(MyColor), 0.3f));
           // mytextprogress.setText(idspeed);
        }else if (idspeed>35  && idspeed<=55){
            String MyColor = "#ffd400";
            circularProgressBar.setColor(Color.parseColor(MyColor));
            circularProgressBar.setBackgroundColor(adjustAlpha(Color.parseColor(MyColor), 0.3f));
           // mytextprogress.setText(idspeed);

        }else if (idspeed>55){
            String MyColor = "#00ff04";
            circularProgressBar.setColor(Color.parseColor(MyColor));
            circularProgressBar.setBackgroundColor(adjustAlpha(Color.parseColor(MyColor), 0.3f));
           // mytextprogress.setText(idspeed);
        }else{
            String MyColor = "#0000ff";
            circularProgressBar.setColor(Color.parseColor(MyColor));
            circularProgressBar.setBackgroundColor(adjustAlpha(Color.parseColor(MyColor), 0.3f));
          //  mytextprogress.setText(idspeed);
        }

        }

    private int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }


}
