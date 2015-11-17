package com.zp.chat.fragment;

import java.io.IOException;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import com.zp.chat.Connection;
import com.zp.chat.R;
import com.zp.chat.activity.HomeActivity;
import com.zp.chat.base.BaseFragment;
import com.zp.chat.utils.ToastUtil;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class SignInFrag extends BaseFragment implements OnClickListener {

	private EditText etAccount;
	private EditText etPwd;
	private Button btnSignIn;

	public XMPPTCPConnection con;

	private String account = "";
	private String password = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fra_sign_in, container, false);
		initView(view);
		initEvent();

		return view;
	}

	private void initView(View view) {
		etAccount = (EditText) view.findViewById(R.id.et_number);
		etPwd = (EditText) view.findViewById(R.id.et_pwd);
		btnSignIn = (Button) view.findViewById(R.id.btn_sign_in);
	}

	private void initEvent() {
		btnSignIn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_sign_in:
			if (con == null) {
				con = Connection.getCon();
			} 
			account = etAccount.getText().toString().trim();
			password = etPwd.getText().toString().trim();
			if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)) {
				ToastUtil.show(getActivity(), "用户名或密码不能为空");
				return;
			} else {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							con.login(account, password);
							Connection.setAccount(account);
							if (con.isAuthenticated()) {
								Connection.isConnected = true;

								Intent intent = new Intent(getActivity(),
										HomeActivity.class);
								startActivity(intent);
								getActivity().finish();
							} else {
								return;
							}
						} catch (XMPPException | SmackException
								| IOException e) {
							e.printStackTrace();
							return;
						}
					}
				}).start();
			}
			break;

		default:
			break;
		}
	}
}
