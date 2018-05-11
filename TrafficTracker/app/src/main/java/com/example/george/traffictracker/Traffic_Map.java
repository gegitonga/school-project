package com.example.george.traffictracker;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;


import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.ui.IconGenerator;






public class Traffic_Map extends Fragment implements OnMapReadyCallback,GoogleApiClient.OnConnectionFailedListener {

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private GoogleMap mMap;
    ArrayList markerPoints = new ArrayList();
    AutoCompleteTextView input;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136)
    );
    private static final String TAG = "Traffic Map";
    InfoWindowData info;
    Marker mapmarker;
    private boolean isInfoWindowShown = false;
    SearchView searchView;
    MenuItem myActionMenuItem;
    SearchView.SearchAutoComplete searchAutoComplete;
    List<Address> list2 = new ArrayList<>();
    SupportMapFragment mapFragment;





    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseRef2 = database.getReference().child("Messages");


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.traffic_map, null, false);
        setHasOptionsMenu(true);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
         mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        databaseRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()){

                    String placemessage = s.child("messagePlace").getValue(String.class);
                    String textmessage = s.child("messageText").getValue(String.class);

                    Geocoder geocoder = new Geocoder(getActivity());
                    try{
                        list2 = geocoder.getFromLocationName(placemessage, 1);
                    }catch (IOException e){
                        Log.e(TAG, "geolocation: IOException" + e.getMessage());
                    }
                    if (list2.size() > 0){
                        Address address = list2.get(0);

                        LatLng location = new LatLng(address.getLatitude(),address.getLongitude());


                        Log.d(TAG, "geolocation: found a location" + address.toString());
                    }


                }



            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });









        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);

        // Get the search menu.
        myActionMenuItem = menu.findItem(R.id.action_search);
        //Get SearchView object
        final SearchView searchViewAndroidActionBar = (SearchView) MenuItemCompat.getActionView(myActionMenuItem);

        //Get SearchView autocomplete object.
         searchAutoComplete = (SearchView.SearchAutoComplete) searchViewAndroidActionBar.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        ArrayAdapter<Address> adapter = new ArrayAdapter<Address>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, list2);
        searchAutoComplete.setAdapter(adapter);




        searchViewAndroidActionBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchViewAndroidActionBar.clearFocus();
                Toast.makeText(getActivity(), query, Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {


        }
        return true;
    }
    @Override    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void updateMe(String updateString) {
        input.setText(updateString);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        //enable Traffic
        googleMap.setTrafficEnabled(true);

        //enable zoom contols
        mMap.getUiSettings().setZoomControlsEnabled(true);


        //define pointer to center on
        LatLng nairobi = new LatLng(-1.2921, 36.8219);
        CameraUpdate panToOrigin = CameraUpdateFactory.newLatLng(nairobi);
        mMap.moveCamera(panToOrigin);
        //set zoom level with animation
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11), 400, null);




        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                if (markerPoints.size() > 1) {
                    markerPoints.clear();
                    mMap.clear();
                }

                // Adding new item to the ArrayList
                markerPoints.add(latLng);

                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(latLng);
                databaseRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot s : dataSnapshot.getChildren()){

                            String placemessage = s.child("messagePlace").getValue(String.class);
                            String textmessage = s.child("messageText").getValue(String.class);
                            String messageUser = s.child("messageUser").getValue(String.class);
                            //Toast.makeText(getActivity(), location_left, Toast.LENGTH_SHORT).show();

                            Geocoder geocoder = new Geocoder(getActivity());
                            List<Address> list = new ArrayList<>();
                            try{
                                list = geocoder.getFromLocationName(placemessage, 1);
                            }catch (IOException e){
                                Log.e(TAG, "geolocation: IOException" + e.getMessage());
                            }
                            if (list.size() > 0){
                                Address address = list.get(0);


                                LatLng location = new LatLng(address.getLatitude(),address.getLongitude());

                                IconGenerator iconGenerator = new IconGenerator(getActivity());

                                info = new InfoWindowData();
                                info.setImage("snowqualmie");
                                info.setLocation(placemessage);


                                CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(getActivity());
                                mMap.setInfoWindowAdapter(customInfoWindow);

                                Bitmap iconBitmap = iconGenerator.makeIcon("#"+placemessage);
                                mapmarker =    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(iconBitmap))
                                        .anchor(0.5f, 0.6f)
                                        .title(messageUser)
                                        .position(location)
                                        .snippet(textmessage));

                                mapmarker.setTag(info);



                                list.clear();



                                Log.d(TAG, "geolocation: found a location" + address.toString());
                            }


                        }



                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        throw databaseError.toException();
                    }
                });


                mMap.addMarker(options);



            }

        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //int position = (int)(marker.getTag());
                if (isInfoWindowShown) {
                    mapmarker.hideInfoWindow();


                }




                return false;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                if (!isInfoWindowShown) {

                    mapmarker.showInfoWindow();
                    LatLng position =  marker.getPosition();
                    float zoomLevel = 19.0f; //This goes up to 21
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, zoomLevel));

                    isInfoWindowShown = true;
                } else {

                    mapmarker.hideInfoWindow();
                    isInfoWindowShown = false;
                }
            }
        });


    }

    public boolean checkLocationPermission() {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = getActivity().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {

            case 0: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "Permission granted", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }




    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();


            parserTask.execute(result);

        }
    }


    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);

            }

// Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                mMap.addPolyline(lineOptions);
                Polyline polyline = mMap.addPolyline(lineOptions);
                polyline.setClickable(true);
            } else {
                Log.d("onPostExecute", "without polylines draw");
            }


        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }




}














