package com.example.george.traffictracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

	public static final int DB_VERSION = 2; 
	public static final String DB_NAME = "george.db";
   
	public DBHelper(Context ctx) {
		super(ctx, DB_NAME, null, DB_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
          createTables(db);
	} 

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

	}
	
	public void createTables(SQLiteDatabase database) {
		String fd_table_sql = "create table " + Database.FD_TABLE_NAME + " ( " +
				Database.FD_ID 	+ " integer  primary key autoincrement, " +
				Database.FD_Location + " TEXT,"
				;


		
        try {

		   database.execSQL(fd_table_sql);



		   Log.d(Database.TAG,"Tables created!");
        }
        catch(Exception ex) {
        	Log.d(Database.TAG, "Error in DBHelper.onCreate() : " + ex.getMessage());
        }
	}

}
