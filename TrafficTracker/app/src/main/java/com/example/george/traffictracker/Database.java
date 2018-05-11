package com.example.george.traffictracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Database {

	public static final String TAG = "Traffic Update";
	public static final String FD_TABLE_NAME = "notification_locations";
	public static final String FD_Location = "locations";
	public static final String FD_ID = "_id";




	
	public static String getDatabasePath(Context context) {
		DBHelper dbhelper = new DBHelper(context);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		String path = db.getPath();
		db.close();
		dbhelper.close();
		return path;
	}


}
