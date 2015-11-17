package com.zp.chat.activity;

import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import com.zp.chat.Connection;
import com.zp.chat.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class SearchFriendActivity extends Activity implements OnClickListener {

	public XMPPTCPConnection con;

	public EditText etSearch;
	public Button btnSearch;

	private String user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_friend_add);
		con = Connection.getCon();

		etSearch = (EditText) findViewById(R.id.friend_add_et_search);
		btnSearch = (Button) findViewById(R.id.btn_search);
		btnSearch.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		if (v == btnSearch) {
			user = etSearch.getText().toString().trim();
			if (con == null || TextUtils.isEmpty(user)){
				return;
			}
			try {
				Roster roster = Roster.getInstanceFor(con);
				roster.createEntry(user, user, null);
				if (!TextUtils.isEmpty(user)) {
					Intent intent = new Intent();
					setResult(RESULT_OK, intent);
				}
				return;
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}

		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		finish();
	}
}
