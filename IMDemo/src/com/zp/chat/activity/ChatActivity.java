package com.zp.chat.activity;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import com.zp.chat.Connection;
import com.zp.chat.R;
import com.zp.chat.adapter.MsgAdapter;
import com.zp.chat.bean.Msg;
import com.zp.chat.db.MyOpenhelper;
import com.zp.chat.service.ConnectionService;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ChatActivity extends Activity implements OnClickListener {

	public XMPPTCPConnection con;
	private Chat ct;
	private ChatManager cm;

	private ListView messageListView;
	private Button btnSend;
	private EditText etContent;
	private String user;
	private String msgFromNew;

	private String receviedMsg;

	private List<Msg> msgList = new ArrayList<Msg>();
	private MsgAdapter msgAdapter;

	private MyOpenhelper mOpenhelper;

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				msgAdapter.notifyDataSetChanged();
				messageListView.setSelection(msgList.size());
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_message);

		// 设置helper
		mOpenhelper = MyOpenhelper.getInstance(this);

		// 服务阻止了这个Activity的监听。需要把服务停止？但是会产生问题，就是服务不能接收到其他发来的消息了
		// 不能这样,不然就很不人性化了.暂时先停止服务把。
		// Intent stopServiceIntent = new Intent(this, ConnectionService.class);
		// stopService(stopServiceIntent);

		Intent intent = getIntent();
		user = intent.getStringExtra("user");
		msgFromNew = intent.getStringExtra("newMsg");
		if (!TextUtils.isEmpty(msgFromNew)) {
			receviedMessage(user + ":" + msgFromNew);
		}
		// 读取数据库里面的内容
		readMessage();

		messageListView = (ListView) findViewById(R.id.message_list_view);
		btnSend = (Button) findViewById(R.id.message_btn_send);
		etContent = (EditText) findViewById(R.id.message_et_content);

		// 连接服务器
		con = Connection.getCon();
		cm = ChatManager.getInstanceFor(con);
		ct = cm.createChat(user + "@none1-pc");
		cm.addChatListener(new ChatManagerListener() {
			@Override
			public void chatCreated(Chat chat, boolean b) {
				chat.addMessageListener(new ChatMessageListener() {

					@Override
					public void processMessage(Chat chat,
							org.jivesoftware.smack.packet.Message message) {
						if (message.getType()
								.equals(org.jivesoftware.smack.packet.Message.Type.chat)
								|| message.getType().equals(
										org.jivesoftware.smack.packet.Message.Type.normal)) {

							if (!TextUtils.isEmpty(message.getBody())) {
								receviedMsg = message.getBody();
								String from = message.getFrom().split("@")[0];
								receviedMessage(receviedMsg);
								System.out.println("ChatActivity" + from + ":"
										+ receviedMsg);
							}
						}
					}
				});
			}

		});

		msgAdapter = new MsgAdapter(ChatActivity.this, R.layout.msg_item,
				msgList);
		messageListView.setAdapter(msgAdapter);
		btnSend.setOnClickListener(this);
	}

	// 遍历表里面的数据并添加到listView中
	public void readMessage() {
		SQLiteDatabase readDB = mOpenhelper.getWritableDatabase();
		String args[] = { user };

		Cursor cursor = readDB.query("message", null, "user=?", args, null,
				null, null);
		System.out.println("cursor: " + cursor);
		if (cursor.moveToFirst()) {
			do {
				// 遍历Cursor 对象，取出数据并打印
				String content = cursor
						.getString(cursor.getColumnIndex("content"));
				int type = cursor.getInt(cursor.getColumnIndex("type"));
				//System.out.println("DB信息：" + user + content + ":" + type);
				Msg msg = new Msg(content, type);
				msgList.add(msg);

			} while (cursor.moveToNext());
		}
		cursor.close();

		refreshList();
	}

	// public void initListView() {
	//
	// }

	// 保存到数据库
	public void addMessage(Msg msg) {

		SQLiteDatabase db = mOpenhelper.getWritableDatabase();

		ContentValues values = new ContentValues();

		// 开始组装第一条数据
		values.put("user", user);
		values.put("content", msg.getContent());
		values.put("type", msg.getType());
		db.insert("message", null, values); // 插入第一条数据
		values.clear();
		db.close();
	}

	@Override
	public void onClick(View v) {
		if (v == btnSend) {
			Msg msg = null;
			String content = etContent.getText().toString().trim();
			if (!TextUtils.isEmpty(content)) {
				msg = new Msg(content, Msg.TYPE_SENT);
				msgList.add(msg);
				etContent.setText("");
				sendMessage(content);

				refreshList();

			} else {
				Toast.makeText(this, "信息为空", Toast.LENGTH_SHORT).show();
			}

			// 给数据库添加数据，定义为发送的数据
			if (msg != null) {
				addMessage(msg);
			}

		}
	}

	public void receviedMessage(String msgs) {
		Msg msg = new Msg(msgs, Msg.TYPE_RECEIVED);
		msgList.add(msg);

		refreshList();
		// 给数据库添加数据，定义为接收的的数据
	}

	public void refreshList() {
		Message messages = Message.obtain();
		messages.what = 1;
		mHandler.sendEmptyMessage(messages.what);
	}

	public void sendMessage(String text) {
		try {
			ct.sendMessage(text);
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.out.println("ChatActivity onDestroy");
		user = "";
		// ConnectionService.count = 0;
		// Intent startServiceIntent = new Intent(this,
		// ConnectionService.class);
		// startService(startServiceIntent);

	}

}
