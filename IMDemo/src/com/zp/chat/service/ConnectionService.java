package com.zp.chat.service;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import com.zp.chat.Connection;
import com.zp.chat.R;
import com.zp.chat.activity.ChatActivity;
import com.zp.chat.bean.Contacts;
import com.zp.chat.bean.Msg;
import com.zp.chat.db.MyOpenhelper;
import com.zp.chat.fragment.ContactFrag;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.text.TextUtils;

public class ConnectionService extends Service {

	public XMPPTCPConnection con;
	private ChatManager cm;

	Context context;
	public static int count = 0;
	
	private MyOpenhelper mOpenhelper;
	private String from;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();

		if (con == null) {
			con = Connection.getCon();
		}

		cm = ChatManager.getInstanceFor(con);

		cm.addChatListener(new ChatManagerListener() {
			@Override
			public void chatCreated(Chat chat, boolean b) {
				chat.addMessageListener(new ChatMessageListener() {

					

					@SuppressWarnings("deprecation")
					@Override
					public void processMessage(Chat chat,
							org.jivesoftware.smack.packet.Message message) {

						if (message.getType()
								.equals(org.jivesoftware.smack.packet.Message.Type.chat)
								|| message.getType().equals(
										org.jivesoftware.smack.packet.Message.Type.normal)) {

							if (!TextUtils.isEmpty(message.getBody())) {

								String receviedMsg = message.getBody();

								from = message.getFrom().split("@")[0];

								Intent intent = new Intent(context,
										ChatActivity.class);
								intent.putExtra("user", from);
								intent.putExtra("newMsg", receviedMsg);

								PendingIntent pendingIntent = PendingIntent
										.getActivity(context, 0, intent,
												PendingIntent.FLAG_CANCEL_CURRENT);
								NotificationManager manager = (NotificationManager) getSystemService(
										Context.NOTIFICATION_SERVICE);

								
								Notification notification = new Notification(
										R.drawable.user_account_icon, "你有一条新消息",
										System.currentTimeMillis());
								notification.flags |= Notification.FLAG_AUTO_CANCEL ;
								notification.setLatestEventInfo(
										getBaseContext(), "你有一条新消息来自" + from,
										receviedMsg, pendingIntent);

								manager.notify(100, notification);

								++count;
								Msg msg =new Msg(receviedMsg, Msg.TYPE_RECEIVED);
								if (msg != null) {
									addMessage(msg);
								}
								
								setUnread(from);
//								System.out.println("I'm Service :" + from + ":"
//										+ receviedMsg);
							}
						}
					}
				});
			}

		});
		
		
		
		mOpenhelper = MyOpenhelper.getInstance(getApplicationContext());

	}
	
	
	public void setUnread(String from){
		
		for (Contacts Contacts : ContactFrag.conList) {
			if (Contacts.getUser().equals(from)) {
				int unread = Contacts.getUnread();
				Contacts.setUnread(unread+1);
			}
		}
		ContactFrag.refreshListView();
	}
	
	public void addMessage(Msg msg) {

		SQLiteDatabase db = mOpenhelper.getWritableDatabase();

		ContentValues values = new ContentValues();

		// 开始组装第一条数据
		values.put("user", from);
		values.put("content", msg.getContent());
		values.put("type", msg.getType());
		db.insert("message", null, values); // 插入第一条数据
		values.clear();
		db.close();
	}
	

}
