package com.zp.chat.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import com.zp.chat.Connection;
import com.zp.chat.R;
import com.zp.chat.bean.Contacts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ContactsActivity extends Activity implements OnClickListener {

	List<Contacts> conList = new ArrayList<Contacts>();
	public XMPPTCPConnection con;

	private ImageView ivSearch;

	private ListView listView;
	private PeopleAdapter peopleAdapter;

	Handler mHandle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			System.out.println("进入了handleMessage");
			if (msg.what == 1) {

				initFriends();

				peopleAdapter.notifyDataSetChanged();
				listView.setSelection(conList.size());
				System.out.println("msg.what == 1 更新了界面");
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fra_contact);

		con = Connection.getCon();
		initFriends();

		ivSearch = (ImageView) findViewById(R.id.bar_add_friend);
		ivSearch.setOnClickListener(this);

		peopleAdapter = new PeopleAdapter(this, R.layout.contacts_adapter,
				conList);
		listView = (ListView) findViewById(R.id.contact_list_view);
		listView.setAdapter(peopleAdapter);
		listView.setOnItemClickListener(new MyItemClickListener());
	}

	@Override
	public void onClick(View v) {
		if (v == ivSearch) {
			Connection.setCon(con);
			Intent intent = new Intent(ContactsActivity.this,
					SearchFriendActivity.class);

			startActivityForResult(intent, 1);
		}
	}

	public void initFriends() {
		if (conList != null) {
			conList.clear();
		}
		Collection<RosterEntry> rosters = Roster.getInstanceFor(con)
				.getEntries();
		// System.out.println("ContactsActivity我的好友列表：=======================");
		for (RosterEntry rosterEntry : rosters) {
			System.out.print("name: " + rosterEntry.getName() + ",jid: "
					+ rosterEntry.getUser());
			Contacts contacts = new Contacts(rosterEntry.getName(),
					rosterEntry.getUser());
			conList.add(contacts);
		}
	}

	class MyItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent intent = new Intent(ContactsActivity.this,
					ChatActivity.class);
			intent.putExtra("user", conList.get(position).getUser());
			startActivity(intent);
		}

	}

	class PeopleAdapter extends ArrayAdapter<Contacts> {

		private int resourceId;

		public PeopleAdapter(Context context, int resource,
				List<Contacts> objects) {
			super(context, resource, objects);
			resourceId = resource;
		}

		@Override
		public int getCount() {
			return conList.size();
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Contacts contacts = getItem(position);
			View view = LayoutInflater.from(getContext()).inflate(resourceId,
					null);
			TextView tvName = (TextView) view.findViewById(R.id.tv_name);
			TextView tvUser = (TextView) view.findViewById(R.id.tv_user);
			tvName.setText(contacts.getName());
			tvUser.setText(contacts.getUser());
			return view;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Message msg = Message.obtain();
		msg.what = 1;
		mHandle.sendEmptyMessage(msg.what);
		System.out.println("进入了onActivityResult");
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		con.disconnect();
		finish();
	}
}
