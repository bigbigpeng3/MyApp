package com.zp.chat.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyOpenhelper extends SQLiteOpenHelper{
	
	
	private static String DBName = "MyMessage.db";
	private static int DBversion = 1 ;
	
	//private Context mContext ;
	
	public static final String CREATE_MESSAGE = "create table message ("
			+ "id integer primary key autoincrement, "
			+ "user text, "
			+ "content text, "
			+ "type integer) ";
	
	
	private static MyOpenhelper instance;
	public static MyOpenhelper getInstance(Context context) {
		if (instance == null) {
			synchronized (MyOpenhelper.class) {
				if (instance == null) {
					instance = new MyOpenhelper(context);
				}
			}
		}
		return instance;
	}
	
	
	public MyOpenhelper(Context context) {
		super(context, DBName, null, DBversion);
		//mContext = context ;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_MESSAGE);
		//ToastUtil.show(mContext, "数据库创建成功");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}
