package com.example.george.traffictracker;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by george on 12/7/17.
 */

public class notifyUpdate extends Activity {

    EditText editLocation;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifyupdate);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width*.8),(int)(height*.4));


            editLocation = (EditText) findViewById(R.id.editLocation);
        Button add = (Button) findViewById(R.id.btnAdd);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveToDB();
                editLocation.setText("");
            }
        });



    }
    private void saveToDB() {
        SQLiteDatabase database = new SampleSQLiteDBHelper(this).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SampleSQLiteDBHelper.PERSON_COLUMN_NAME, editLocation.getText().toString());
        long newRowId = database.insert(SampleSQLiteDBHelper.PERSON_TABLE_NAME, null, values);

        Toast.makeText(this, editLocation.getText()+" Route added to notification ", Toast.LENGTH_LONG).show();
    }






}
