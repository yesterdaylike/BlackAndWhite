package com.yesterdaylike.blackandwhite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HistoryDB extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "history.db";  
	private static final int DATABASE_VERSION = 1; 
	private SQLiteDatabase db;

	public HistoryDB(Context context) {
		// TODO Auto-generated constructor stub
		super(context, DATABASE_NAME, null, DATABASE_VERSION);  
	}

	static String month = "month";
	static String day = "day";
	static String time = "time";
	static String date = "date";
	static String step = "step";
	static String score = "score";
	static String interval = "interval";
	static String type = "type";

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE IF NOT EXISTS history" +  
				"(_id INTEGER PRIMARY KEY AUTOINCREMENT, month TEXT, day TEXT, time TEXT, date TEXT, step TEXT, score TEXT, interval TEXT, type TEXT)");  
	}

	public void add(int month, int day, String time ,long date, int step, int score, int interval, int type){
		if( db == null ){
			db = getWritableDatabase();
		}
		try {
			db.execSQL("INSERT INTO history VALUES(null, ?, ?, ?, ?, ?, ?, ?, ?)", new Object[]{month, day, time, date, step, score, interval, type});
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public Cursor query(){
		if( db == null ){
			db = getWritableDatabase();
		}
		Cursor cursor = db.query("history", null, null, null, null, null, null); 
		cursor = db.query("history", null, null, null, null, null, null); 
		//db.close();
		return cursor;
	}
	
	public String queryBestScore(){
		if( db == null ){
			db = getWritableDatabase();
		}
		
		String best = "";
		Cursor cursor = db.query("history", new String[]{"score"}, null, null, null, null, "score"); 
		//db.close();
		if ( null!= cursor ){
			cursor.moveToFirst();
			best = cursor.getString(0);
			cursor.close();
		}
		return best;
	}
	
	

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}
}