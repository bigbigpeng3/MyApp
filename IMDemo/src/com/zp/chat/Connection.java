package com.zp.chat;

import java.io.IOException;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import com.zp.chat.global.GlobalContants;

import android.os.AsyncTask;
import android.util.Log;

public class Connection {

	private static XMPPTCPConnectionConfiguration.Builder config;
	public static XMPPTCPConnection con = null; // 优化使用单例模式
	public static MyConnectionListener myConnectionListener;

	public static boolean isConnected = false;

	public static String HOST = GlobalContants.SERVER_URL;// 10.0.2.2 ....
	// none1201.oicp.net:19488
	public static int PORT = 5222;// 5222 19488
	public static String account = "";
	public static String password = "";

	public static void initConnection() {
		// if (con != null) {
		// con.disconnect();
		// }

		AsyncTask<Void, Void, Void> connectionThread = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				if (config == null) {
					config = XMPPTCPConnectionConfiguration.builder();
					config.setSecurityMode(
							ConnectionConfiguration.SecurityMode.disabled);
				}
				config.setSendPresence(true);
				config.setServiceName("none1-pc");
				config.setHost(HOST);
				config.setPort(PORT);
				config.setDebuggerEnabled(true);
				if (con == null) {
					con = Connection.getCon();
					con.setPacketReplyTimeout(10000);
				}
				if (myConnectionListener == null) {
					myConnectionListener = new MyConnectionListener();
				}
				con.addConnectionListener(myConnectionListener);
				conntect();
				return null;
			}

		};

		connectionThread.execute();

	}
	
	

	public static void closeConnect() {
		// 关闭con连接
		if (con != null) {
			con.disconnect();
			con = null;
		}
	}

	public static void conntect() {
		try {
			con.connect();
		} catch (SmackException | IOException | XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	public static void initFriends() {
//		Collection<RosterEntry> rosters = Roster.getInstanceFor(con)
//				.getEntries();
//		for (RosterEntry rosterEntry : rosters) {
//			System.out.print("name: " + rosterEntry.getName() + ",jid: "
//					+ rosterEntry.getUser());
//		}
//	}

	static class MyConnectionListener implements ConnectionListener {

		@Override
		public void authenticated(XMPPConnection arg0, boolean arg1) {
			System.out.println("Connection authenticated");
			//initFriends();
		}

		@Override
		public void connected(XMPPConnection arg0) {
			System.out.println("Connection connected...");
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

	public static void setCon(XMPPTCPConnection con) {
		Connection.con = con;
	}

	public static synchronized XMPPTCPConnection getCon() {

		if (con == null) {
			con = new XMPPTCPConnection(config.build());
		}

		return con;
	}

	public static String getAccount() {
		return account;
	}

	public static void setAccount(String account) {
		Connection.account = account;
	}

	public static String getPassword() {
		return password;
	}

	public static void setPassword(String password) {
		Connection.password = password;
	}

}
