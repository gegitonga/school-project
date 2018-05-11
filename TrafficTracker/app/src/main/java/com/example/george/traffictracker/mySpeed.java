package com.example.george.traffictracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.Manifest;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.BIND_AUTO_CREATE;
import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by george on 6/28/17.
 */

public class mySpeed extends Fragment {

    LocationService myService;

    static boolean status;
    LocationManager locationManager;
    static TextView dist;
    public static TextView time;
    static TextView speed, gpsNull;
    Button start, pause, stop;
    static long startTime, endTime;
    ImageView image;
    static ProgressDialog locate;
    static int p = 0;

    String tim="30 sec";

    TextView mText;
    GPS_Service gps;
    Runnable runnable;

    //DatabaseReference mDatabaseLocationDetails;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseRef = database.getReference("location_details");


    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
            myService = binder.getService();
            status = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            status = false;

        }
    };

    void bindService() {
        if (status == true) {
            return;
        }

        Intent i = new Intent(getActivity().getApplicationContext(), LocationService.class);
        getActivity().bindService(i, sc, BIND_AUTO_CREATE);
        status = true;
        startTime = System.currentTimeMillis();
    }


    void unbindService(){
        if (status == false){
            return;
        }


        Intent i =new Intent(getActivity().getApplicationContext(), LocationService.class);

        getActivity().unbindService(sc);
        status = false;
        return;
    }

    @Override
    public void onResume(){
        super.onResume();
    }
    @Override
    public void onStart(){
        super.onStart();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        if (status == false){


           // super.onBackPressed();
        }else{
           // moveTaskToBack(true);
        }
    }


    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.myspeed, container, false);

        dist = (TextView)view.findViewById(R.id.distancetext);
        time = (TextView) view.findViewById(R.id.timetext);
        speed = (TextView) view.findViewById(R.id.speedtext);
        gpsNull= (TextView) view.findViewById(R.id.gpsNull);
        start = (Button) view.findViewById(R.id.start);
        pause = (Button) view.findViewById(R.id.pause);
        stop = (Button) view.findViewById(R.id.stop);
        image = (ImageView) view.findViewById(R.id.image);

        //permission check
        getActivity().startService(new Intent(getActivity(), GPS_Service.class));
        if (!runtime_permission()){

            runtime_permission();
        }



        start.setOnClickListener(new View.OnClickListener(){
                                     @Override
                                     public void onClick(View v){
                                         //method checks if location is enabled if not to enable the gps
                                         checkGps();
                                         locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
                                         if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                                             return;
                                         }

                                         if (status == false){
                                             //location Service gets bound and the GPS speedometer
                                             bindService();
                                             locate = new ProgressDialog(getContext());
                                             locate.setIndeterminate(true);
                                             locate.setCancelable(false);
                                             locate.setMessage("Getting Location....");
                                             locate.show();
                                             start.setVisibility(View.GONE);
                                             pause.setVisibility(View.VISIBLE);
                                             pause.setText("pause");
                                             stop.setVisibility(View.VISIBLE);




                                         }


                                     }
                                 }



        );


        pause.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (pause.getText().toString().equalsIgnoreCase("Resume")){
                    checkGps();
                    locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                        //Toast.makeText(this,"GPS is Enabled in your device",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    pause.setText("pause");
                    p = 0;
                }
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status == false){
                    unbindService();
                    start.setVisibility(View.VISIBLE);
                    pause.setText("Pause");
                    pause.setVisibility(View.GONE);
                    stop.setVisibility(View.GONE);
                    p = 0;
                }
            }
        });


        return view;
    }




    //method leads to the alert dialog box
    void checkGps(){
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            showGPSDisabledAlertToUser();
        }

    }

    void checkGps2(){
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
          gpsNull.setText("Enable GPS for maximum results");
        }

    }
    //method configures alert dialog box
    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getActivity());
        alertDialogBuilder.setMessage("Enabled GPS to use application").setCancelable(false).setPositiveButton("Enable GPS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(callGPSSettingIntent);

            }
        });
        alertDialogBuilder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();

    }





    private boolean runtime_permission() {
        if(Build.VERSION.SDK_INT>=23 && ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED&& ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},123);
            return true;
        }
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==123){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]== PackageManager.PERMISSION_GRANTED){

            }else{
                runtime_permission();
            }
        }
    }

    public void StartTwo(){
        //method checks if location is enabled if not to enable the gps
        checkGps2();
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            return;
        }

        if (status == false){
            //location Service gets bound and the GPS speedometer
            bindService();


        }

    }
}






