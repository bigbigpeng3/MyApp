package com.zp.chat.fragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import com.zp.chat.Connection;
import com.zp.chat.R;
import com.zp.chat.activity.ChatActivity;
import com.zp.chat.activity.SearchFriendActivity;
import com.zp.chat.base.BaseFragment;
import com.zp.chat.bean.Contacts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ContactFrag extends BaseFragment implements OnClickListener {

	
	//public static Set<Contacts> conList1 = new HashSet<Contacts>();
	public static List<Contacts> conList = new ArrayList<Contacts>();
	public XMPPTCPConnection con;

	private ImageView ivSearch;

	private static ListView listView;
	private static PeopleAdapter peopleAdapter;

	static Handler mHandle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// System.out.println("进入了handleMessage");
			if (msg.what == 1) {
				peopleAdapter.notifyDataSetChanged();
				listView.setSelection(conList.size());
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		con = Connection.getCon();
		initFriends();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fra_contact, container, false);

		ivSearch = (ImageView) view.findViewById(R.id.bar_add_friend);
		ivSearch.setOnClickListener(this);

		peopleAdapter = new PeopleAdapter(getActivity(),
				R.layout.contacts_adapter, conList);
		listView = (ListView) view.findViewById(R.id.contact_list_view);
		listView.setAdapter(peopleAdapter);
		listView.setOnItemClickListener(new MyItemClickListener());

		refreshListView();
		return view;
	}

	@Override
	public void onClick(View v) {
		if (v == ivSearch) {
			Connection.setCon(con);
			Intent intent = new Intent(getActivity(),
					SearchFriendActivity.class);

			startActivityForResult(intent, 1);
		}
	}

	public void initFriends() {
		if (conList!=null) {
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		initFriends();
		refreshListView();
	}

	public static void refreshListView() {
		Message msg = Message.obtain();
		msg.what = 1;
		mHandle.sendEmptyMessage(msg.what);
	}

	class MyItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent intent = new Intent(getActivity(), ChatActivity.class);
			conList.get(position).setUnread(0);
			intent.putExtra("user", conList.get(position).getUser());
			// intent.putExtra("newMsg", "");
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
			TextView tv_unread = (TextView) view.findViewById(R.id.tv_unread);
			tvName.setText(contacts.getName());
			tvUser.setText(contacts.getUser());

			// contacts.setUnread(1);
			// tv_unread.setText("" + contacts.getUnread());
			// tv_unread.setVisibility(View.VISIBLE);
			if (contacts.getUnread() <= 0) {
				tv_unread.setVisibility(View.GONE);
				tv_unread.setText("");
			} else {
				if (contacts.getUnread() >= 99) {
					tv_unread.setText("99+");
				} else {
					tv_unread.setText("" + contacts.getUnread());
				}
				tv_unread.setVisibility(View.VISIBLE);
			}
			return view;
		}

	}

	@Override
	public void onResume() {
		refreshListView();
		super.onResume();
	}

}
