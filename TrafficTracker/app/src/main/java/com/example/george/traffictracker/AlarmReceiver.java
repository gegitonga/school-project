package com.example.george.traffictracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by george on 12/18/17.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        LocationService new1 = new LocationService();
        new1.updateDB();
        LocationService new2 = new LocationService();
        new2.StartThree();
    }
}
