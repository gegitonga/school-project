package com.example.george.traffictracker;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
//import android.support.v7.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
//import android.widget.SearchView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class chat extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private static final int SIGN_IN_REQUEST_CODE = 200;

    private FirebaseListAdapter<ChatMessage> adapter;
    private String loggedInUserName = "";

    ListView listView;

    AutoCompleteTextView input;

    TextView prefix;

    ImageButton IBmap;

    ImageButton like_btn, unlike_btn;

    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;

    private GoogleApiClient mGoogleApiClient;

    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136)
    );

    private static final String TAG = "chat";

    private ViewPager viewPager;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseRef2 = database.getReference();

    private boolean mProcessLike = false;

    private boolean mProcessUnlike = false;

    private DatabaseReference mDatabase = database.getReference();

    private DatabaseReference mDatabaseLike = database.getReference().child("Likes");
    private DatabaseReference mDatabaseUnLike = database.getReference().child("Unlikes");

    private DatabaseReference mDatabaseUsers = database.getReference();






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.chat, container, false);
        viewPager = (ViewPager) getActivity().findViewById(R.id.container);
        setHasOptionsMenu(true);
        input = (AutoCompleteTextView) rootView.findViewById(R.id.input);
        listView = (ListView) rootView.findViewById(R.id.list);
        prefix = (TextView) rootView.findViewById(R.id.prefix);
        IBmap = (ImageButton) rootView.findViewById(R.id.IBmap);


        //enables filtering for the contents of the given ListView
        listView.setTextFilterEnabled(true);


        mDatabaseUsers.keepSynced(true);
        mDatabaseLike.keepSynced(true);
        mDatabaseUnLike.keepSynced(true);
        mDatabase.keepSynced(true);

        mGoogleApiClient = new GoogleApiClient
                .Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getActivity(), this)
                .build();

        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(getActivity(), mGoogleApiClient,
                LAT_LNG_BOUNDS, null);
        input.setAdapter(mPlaceAutocompleteAdapter);


         final ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
                = new ResultCallback<PlaceBuffer>() {
            @Override
            public void onResult(PlaceBuffer places) {
                if (!places.getStatus().isSuccess()) {
                    Log.e(TAG, "Place query did not complete. Error: " +
                            places.getStatus().toString());
                    return;
                }
                // Selecting the first object buffer.
                final Place place = places.get(0);
                CharSequence attributions = places.getAttributions();

                prefix.setText(Html.fromHtml(place.getAddress() + ""));


            }
        };


        AdapterView.OnItemClickListener mAutocompleteClickListener
                = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

               //final PlaceAutocompleteAdapter.PlaceAutocomplete item = mPlaceAutocompleteAdapter.getItem(position);

                final AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(position);
                if (item != null) {
                    final String placeId = item.getPlaceId();
                    final CharSequence primaryText = item.getPrimaryText(null);
                    PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                            .getPlaceById(mGoogleApiClient, placeId);
                    placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
                }

            }
        };
        input.setOnItemClickListener(mAutocompleteClickListener);


        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Start sign in/sign up activity
            startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .build(), SIGN_IN_REQUEST_CODE);
        } else {
            // User is already signed in, show list of messages
            showAllOldMessages();
        }

        IBmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefix.setText("");
                input.setText("");
            }
        });

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (input.getText().toString().trim().equals("")) {
                    Toast.makeText(getActivity(), "Please enter some texts!", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseDatabase.getInstance()
                            .getReference().child("Messages")
                            .push()
                            .setValue(new ChatMessage(input.getText().toString(),
                                    FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                                    FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                    prefix.getText().toString())
                            );

                    input.setText("");
                    prefix.setText("");

                }
            }
        });

       // input.setThreshold(1);


        return rootView;
    }
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);



    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getActivity(), "Signed in successful!", Toast.LENGTH_LONG).show();
                showAllOldMessages();
            } else {
                Toast.makeText(getActivity(), "Sign in failed, please try again later", Toast.LENGTH_LONG).show();
                getActivity().finish();
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_sign_out) {
            AuthUI.getInstance().signOut(getActivity())
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            Toast.makeText(getActivity(), "You have logged out!", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        }
                    });
        }
        return true;
    }


    private void showAllOldMessages() {

        loggedInUserName = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("Main", "user id: " + loggedInUserName);

        // adapter = new MessageAdapter(getActivity(), ChatMessage.class, R.layout.item_in_message,
        //FirebaseDatabase.getInstance().getReference());

        adapter = new FirebaseListAdapter<ChatMessage>(getActivity(), ChatMessage.class, R.layout.item_in_message,
                FirebaseDatabase.getInstance().getReference().child("Messages")) {
            @Override
            protected void populateView(View v, final ChatMessage model, int position) {

                final String post_key = getRef(position).getKey();

                // Get references to the views of message.xml
                TextView messageText = (TextView) v.findViewById(R.id.message_text);
                TextView messageUser = (TextView) v.findViewById(R.id.message_user);
                TextView messageTime = (TextView) v.findViewById(R.id.message_time);
                final TextView messagePlace = (TextView) v.findViewById(R.id.message_place);
                TextView thumbup = (TextView) v.findViewById(R.id.thumbup);
                TextView thumbdown = (TextView) v.findViewById(R.id.thumbdown);
                 like_btn = (ImageButton) v.findViewById(R.id.thumb_up);
                unlike_btn = (ImageButton) v.findViewById(R.id.thumb_down);


                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());
                messagePlace.setText(model.getMessagePlace());

                setLikeBtn(post_key);

                setUnlikeBtn(post_key);


                ImageButton imagePlace = (ImageButton) v.findViewById(R.id.imageplace);

                imagePlace.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        RelativeLayout rl = (RelativeLayout)v.getParent();
                        TextView tv = (TextView)rl.findViewById(R.id.message_place);
                        String text = tv.getText().toString();
                        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
                       // viewPager.setCurrentItem(0);


                    }
                });


                // Format the date before showing it
                messageTime.setText(DateFormat.format("dd-MM (HH:mm)",
                        model.getMessageTime()));

                like_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mProcessLike = true;



                            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (mProcessLike) {

                                        if (dataSnapshot.child(post_key).hasChild(loggedInUserName)) {


                                            mDatabaseLike.child(post_key).child(loggedInUserName).removeValue();

                                            mProcessLike = false;



                                        } else {
                                            mDatabaseLike.child(post_key).child(loggedInUserName).setValue("RandomValue");
                                            mDatabaseUnLike.child(post_key).child(loggedInUserName).removeValue();
                                            unlike_btn.setImageResource(R.mipmap.thumbs_down);

                                            mProcessLike = false;

                                        }
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                    }
                });

                unlike_btn.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(View v) {
                                                      mProcessUnlike = true;



                                                      mDatabaseUnLike.addValueEventListener(new ValueEventListener() {
                                                          @Override
                                                          public void onDataChange(DataSnapshot dataSnapshot) {
                                                              if (mProcessUnlike) {

                                                                  if (dataSnapshot.child(post_key).hasChild(loggedInUserName)) {


                                                                      mDatabaseUnLike.child(post_key).child(loggedInUserName).removeValue();

                                                                      mProcessUnlike = false;



                                                                  } else {
                                                                      mDatabaseUnLike.child(post_key).child(loggedInUserName).setValue("RandomValue");
                                                                      mDatabaseLike.child(post_key).child(loggedInUserName).removeValue();
                                                                      like_btn.setImageResource(R.drawable.thumbs_up);
                                                                      mProcessUnlike = false;

                                                                  }
                                                              }

                                                          }

                                                          @Override
                                                          public void onCancelled(DatabaseError databaseError) {

                                                          }
                                                      });


                                                  }
                                              }

                );

            }
        };

        listView.setAdapter(adapter);

    }

    public void setLikeBtn(final String post_key){
        mDatabaseLike.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(post_key).hasChild(loggedInUserName)){

                    like_btn.setImageResource(R.mipmap.thumb_red);
                }else {
                    like_btn.setImageResource(R.drawable.thumbs_up);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void setUnlikeBtn(final String post_key){
        mDatabaseUnLike.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(post_key).hasChild(loggedInUserName)){

                    unlike_btn.setImageResource(R.mipmap.thumb_down_red);
                }else {
                    unlike_btn.setImageResource(R.mipmap.thumbs_down);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public String getLoggedInUserName() {
        return loggedInUserName;
    }

    /* Activity transitions */

    protected void slideInTransition() {
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.fade_back);
    }

    protected void slideOutTransition() {
        getActivity().overridePendingTransition(R.anim.fade_forward, R.anim.slide_out_right);
    }





}




