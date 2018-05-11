package com.example.george.traffictracker;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


//to ensure the app does not become unresponsive when capturing the device location we use asynchronous process by implementing
// ConnectionCallback, OnConnectionFailedListener LocationListener
public class LocationService extends Service implements com.google.android.gms.location.LocationListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
{
    private static  final long INTERVAL = 1000*2;
    private static final long FASTEST_INTERVAL = 1000*1;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation, lStart, lEnd;
    protected String mLastUpdateTime;
    static double distance = 0;
    double speed, speedFormated;
    double lattitude;//Latitude
    double longgitude;//Longitude
    DecimalFormat dFormat = new DecimalFormat("#.#####");
    ArrayList<Long> location = new ArrayList<>();
    static boolean status;
    LocationManager locationManager;
    static long startTime;
    LocationService myService;



    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseRef = database.getReference("messages");




    private final IBinder mBinder = new LocalBinder();


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

public void onCreate(){


}


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        createLocationRequest();

        //In  we created an instance of the Google API Client using GoogleApiClient.Builder and added the LocationServices API to the client
        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();


        mGoogleApiClient.connect();


        return mBinder;
    }
    protected void createLocationRequest(){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }
    @Override
    public int onStartCommand(Intent intent,int flags, int startId ){
        return super.onStartCommand(intent, flags, startId);


    }

//The fused location provider invokes the LocationListener.onLocationChanged() callback method
// when a new location is detected by Google Play Services
    @Override
    public void onConnected(Bundle bundle) {
        try{
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        }catch (SecurityException e){}


    }
    protected void stopLocationUpdates(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        distance = 0;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

        @Override
        public void onLocationChanged(Location location) {
            mySpeed.locate.dismiss();
            mCurrentLocation = location;
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = new Date();
            mLastUpdateTime = dateFormat.format(date).toString();

            lattitude = location.getLatitude();
            longgitude = location.getLongitude();
            lattitude = Double.valueOf(dFormat.format(lattitude));
            longgitude = Double.valueOf(dFormat.format(longgitude));



            if (lStart == null) {
                lStart = mCurrentLocation;
                lEnd = mCurrentLocation;
            } else
                lEnd = mCurrentLocation;
            //calling the method below updates the lives values of distance and speed to  the text views
            //calculate the speed using getSpeed method m/s to kmph




            updateUI();
            updateDB();
            speed = location.getSpeed() * 18 / 5;


        }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {


    }
    public class LocalBinder extends Binder {

        public LocationService getService(){

            return LocationService.this;
        }
    }
    //the live feed of distance and speed are set in the method below
    private void updateUI(){
        if(mySpeed.p == 0) {
            distance = distance + (lStart.distanceTo(lEnd) / 1000.00);
            mySpeed.endTime = System.currentTimeMillis();
            long diff = mySpeed.endTime - mySpeed.startTime;
            diff = TimeUnit.MILLISECONDS.toMinutes(diff);
            mySpeed.time.setText("Total Time:" + diff + "minutes");
            if(speed > 0.0){



                mySpeed.speed.setText("current speed:" + new DecimalFormat("#.##").format(speed)+ "km/h");
                speedFormated = Double.parseDouble(new DecimalFormat("#.##").format(speed));

                /*
                Map mLocations = new HashMap();
                mLocations.put("timestamp", mySpeed.endTime);
                Map mCoordinate = new HashMap();
                mLocations.put("latitude", lattitude);
                mLocations.put("longitude", longgitude);


                mLocations.put("speed", speedFormated);
                databaseRef.push().child("Co-ord").setValue(mLocations);

*/


            }else{
                mySpeed.speed.setText("current speed:" + new DecimalFormat("#.##").format(speed)+ "km/h");

                //mySpeed.speed.setText("...." );


                mySpeed.dist.setText(new DecimalFormat("#.##").format(distance) + "ms.");

                lStart = lEnd;


            }



        }


    }
    public void updateDB(){

        if (isNetworkAvailable(this)) {

            if(speed > 0.0){



                speedFormated = Double.parseDouble(new DecimalFormat("#.##").format(speed));
                Map mLocations = new HashMap();
                mLocations.put("timestamp", mySpeed.endTime);
                Map mCoordinate = new HashMap();
                mLocations.put("latitude", lattitude);
                mLocations.put("longitude", longgitude);

                mLocations.put("speed", speedFormated);
                databaseRef.push().child("Co-ord").setValue(mLocations);




            }else{

                mySpeed.dist.setText(new DecimalFormat("#.##").format(distance) + "ms.");

                lStart = lEnd;
                Map mLocations = new HashMap();
                mLocations.put("timestamp", mySpeed.endTime);
                Map mCoordinate = new HashMap();
                mLocations.put("latitude", lattitude);
                mLocations.put("longitude", longgitude);

                mLocations.put("speed", speedFormated);
                databaseRef.push().child("Co-ord").setValue(mLocations);


            }





        } else {
            System.out.println(" not network connection");

        }


    }

    @Override
    public boolean onUnbind(Intent intent) {
        stopLocationUpdates();
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
        lStart = null;
        lEnd = null;
        distance = 0;
        return super.onUnbind(intent);

    }

    void bindService() {
        if (status == true) {
            return;
        }

        Intent i = new Intent(this, LocationService.class);
        bindService(i, sc, BIND_AUTO_CREATE);
        status = true;
        startTime = System.currentTimeMillis();
    }

    void checkGps(){
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            showGPSDisabledAlertToUser();
        }

    }
    //method configures alert dialog box
    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
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
    void unbindService(){
        if (status == false){
            return;
        }


        Intent i =new Intent(getApplicationContext(), LocationService.class);

        unbindService(sc);
        status = false;
        return;
    }
    public void StartThree(){
        //method checks if location is enabled if not to enable the gps
        checkGps();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            return;
        }

        if (status == false){
            //location Service gets bound and the GPS speedometer
            bindService();





        }

    }
    public boolean isNetworkAvailable(Context context) {
        //get connectivity manager object to check connection

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }

        }
        return false;
    }



}

