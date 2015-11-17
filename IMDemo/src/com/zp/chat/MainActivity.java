package com.zp.chat;

import java.io.IOException;
import java.util.Collection;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

public class MainActivity extends Activity
		implements OnClickListener, ConnectionListener {

//	private EditText etNumber;
//	private EditText etPwd;
//	private Button btnSignin;
//	private Button btnSendMsg;
//	private Button btnDiscon;

	private XMPPTCPConnectionConfiguration.Builder config;
	// private AbstractXMPPConnection mConnection;

	public static XMPPTCPConnection con;
	private Chat ct;
	private ChatManager cm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fra_sign_in);
		
		
//		etNumber = (EditText) findViewById(R.id.et_number);
//		etPwd = (EditText) findViewById(R.id.et_pwd);
//		btnSignin = (Button) findViewById(R.id.btn_sign_in);
//		btnSendMsg = (Button) findViewById(R.id.btn_sendmsg);
//		btnDiscon = (Button) findViewById(R.id.btn_discon);
//		btnSendMsg.setOnClickListener(this);
//		btnSignin.setOnClickListener(this);
//		btnDiscon.setOnClickListener(this);

		// 使用XMPPTCPConnectionConfiguration.Builder config
		config = XMPPTCPConnectionConfiguration.builder();
		config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
		config.setUsernameAndPassword("test" + "@" + "10.0.2.2", "test");
		config.setServiceName("none1-pc");
		config.setHost("10.0.2.2");
		config.setPort(5222);
		config.setDebuggerEnabled(true);
		con = new XMPPTCPConnection(config.build());
		con.setPacketReplyTimeout(5000);
		con.addConnectionListener(MainActivity.this);

		cm = ChatManager.getInstanceFor(con);
		ct = cm.createChat("test2@none1-pc");
		cm.addChatListener(new ChatManagerListener() {
			@Override
			public void chatCreated(Chat chat, boolean b) {
				chat.addMessageListener(new ChatMessageListener() {
					@Override
					public void processMessage(Chat chat, Message message) {
						if (message.getType().equals(Message.Type.chat)
								|| message.getType()
										.equals(Message.Type.normal)) {
							if (!TextUtils.isEmpty(message.getFrom())
									&& !TextUtils.isEmpty(message.getBody())) {
								System.out.println("message.getFrom()"+ message.getFrom());
								System.out.println("message.getBody()"+ message.getBody());
							}
						}
					}
				});
			}
		});

	}

	public void getPeople() {
		Collection<RosterEntry> rosters = Roster.getInstanceFor(con)
				.getEntries();
		System.out.println("我的好友列表：=======================");
		for (RosterEntry rosterEntry : rosters) {
			System.out.print("name: " + rosterEntry.getName() + ",jid: "
					+ rosterEntry.getUser());
			System.out.println("");
		}
		System.out.println("我的好友列表：=======================");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_sign_in:
			System.out.println("点击了登陆按钮-zp");
			new Thread(new Runnable() {
				@Override
				public void run() {
					System.out.println("子线程启动了");
					try {
						con.connect();
					} catch (SmackException | IOException | XMPPException e) {
						e.printStackTrace();
					}

				}
			}).start();
			break;
//		case R.id.btn_sendmsg:
//			System.out.println("点击了发送按钮-zp");
//			sendMessage();// 发送消息
//			getPeople();// 获取自己的好友
//			break;
//
//		case R.id.btn_discon:
//			System.out.println("点击了取消连接按钮-zp");
//			if (con != null) {
//				con.disconnect();
//			}
//			break;
		default:
			break;
		}

	}

	@Override
	public void authenticated(XMPPConnection arg0, boolean arg1) {
		Log.e("ZP-XMPP", "authenticated");
	}

	@Override
	public void connected(XMPPConnection arg0) {
		Log.e("ZP-XMPP", "connected");
		try {
			con.login("test", "adsfasdf");

		} catch (XMPPException | SmackException | IOException e) {
			e.printStackTrace();
		}
	}

	public void sendMessage() {
		try {
			ct.sendMessage("i am test !");
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void connectionClosed() {
		Log.e("ZP-XMPP", "connectionClosed");
	}

	@Override
	public void connectionClosedOnError(Exception arg0) {
		Log.e("ZP-XMPP", "connectionClosedOnError");
	}

	@Override
	public void reconnectingIn(int arg0) {
		Log.e("ZP-XMPP", "reconnectingIn");
	}

	@Override
	public void reconnectionFailed(Exception arg0) {

	}

	@Override
	public void reconnectionSuccessful() {

	}

}