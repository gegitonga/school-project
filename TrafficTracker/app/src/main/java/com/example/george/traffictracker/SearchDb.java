package com.example.george.traffictracker;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static io.fabric.sdk.android.Fabric.TAG;

/**
 * Created by george on 9/27/17.
 */

public class SearchDb extends Activity{
    SearchView searchView;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.searchdb);
            final TextView tv = (TextView) findViewById(R.id.textView);

            searchView=(SearchView) findViewById(R.id.searchView);
            searchView.setQueryHint("Search View");

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {


                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {

                    return false;
                }
            });

        }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);







        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        slideOutTransition();
    }
    protected void slideOutTransition() {
        overridePendingTransition(R.anim.fade_forward, R.anim.slide_out_right);
    }


    }

