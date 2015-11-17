package com.zp.chat.db;

public class MyDB {
	

	public interface Message {
		
		//String TABLE_NAME = "message";
		
		// int TYPE_TEXT = 0;
		// int TYPE_IMAGE = 1;

		/// String COLUMN_ID = "_id";
		// String COLUMN_OWNER = "owner";
		// String COLUMN_ACCOUNT = "account";// 接收者或发送者
		// String COLUMN_DIRECTION = "direct";// 0:发送 1:接收
		// String COLUMN_TYPE = "type";
		// String COLUMN_CONTENT = "content";
		// String COLUMN_URL = "url";
		//String COLUMN_STATE = "state";// 发送状态: 1.正在发送 2.已经成功发送 3.发送失败
		//String COLUMN_READ = "read";// 0:未读 1:已读
		//String COLUMN_CREATE_TIME = "create_time";

//		String SQL_CREATE_TABLE = "create table " + TABLE_NAME + " ("
//				+ COLUMN_ID + " integer primary key autoincrement, "
//				+ COLUMN_OWNER + " text," + COLUMN_ACCOUNT + " text,"
//				+ COLUMN_DIRECTION + " integer," + COLUMN_TYPE + " integer,"
//				+ COLUMN_CONTENT + " text," + COLUMN_URL + " text,"
//				+ COLUMN_STATE + " integer," + COLUMN_READ + " integer,"
//				+ COLUMN_CREATE_TIME + " integer" + ")";
	}
}
