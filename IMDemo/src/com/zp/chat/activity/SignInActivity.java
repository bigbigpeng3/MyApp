package com.zp.chat.activity;

import java.io.IOException;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import com.zp.chat.Connection;
import com.zp.chat.R;
import com.zp.chat.utils.ToastUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class SignInActivity extends Activity implements OnClickListener {

	private EditText etAccount;
	private EditText etPwd;
	private Button btnSignIn;
	
	public XMPPTCPConnection con;

	private String account = "";
	private String password = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fra_sign_in);

		etAccount = (EditText) findViewById(R.id.et_number);
		etPwd = (EditText) findViewById(R.id.et_pwd);
		btnSignIn = (Button) findViewById(R.id.btn_sign_in);
		btnSignIn.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_sign_in:
			if (con == null) {
				con = Connection.getCon();
				account = etAccount.getText().toString().trim();
				password = etPwd.getText().toString().trim();
				if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)) {
					ToastUtil.show(this, "用户名或密码不能为空");
					return;
				} else {
						new Thread(new Runnable() {
							@Override
							public void run() {
								try {
									con.login(account, password);
									if (con.isAuthenticated()) {
										Connection.isConnected = true ;
										Intent intent = new Intent(SignInActivity.this,
												ContactsActivity.class);
										startActivity(intent);
										finish();
									} else {
										return;
									}
								} catch (XMPPException | SmackException | IOException e) {
									e.printStackTrace();
									return;
								}
							}
						}).start();
				}
	
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		disConnect();
	}

	public void disConnect() {
		Connection.closeConnect();
		finish();
	}
}
