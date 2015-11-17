package com.zp.chat.fragment;

import java.io.IOException;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.iqregister.AccountManager;

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

public class SignUpFrag extends BaseFragment implements OnClickListener {

	private EditText etAccount;
	private EditText etPwd;
	private Button btnSignUp;

	public XMPPTCPConnection con;

	public boolean createUser = false;
	private String username;
	private String password;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fra_sign_up, container, false);
		initView(view);
		initEvent();
		return view;
	}

	private void initView(View view) {
		etAccount = (EditText) view.findViewById(R.id.et_sign_up_account);
		etPwd = (EditText) view.findViewById(R.id.et_sign_up_pwd);
		btnSignUp = (Button) view.findViewById(R.id.btn_sign_up);
	}

	private void initEvent() {
		btnSignUp.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// 对服务器进行注册
				if (v == btnSignUp) {
					username = etAccount.getText().toString().trim();
					password = etPwd.getText().toString().trim();

					con = Connection.getCon();
					System.out.println("con = Connection.getCon() succeed ");
					if (TextUtils.isEmpty(password) || TextUtils.isEmpty(username)) {
						ToastUtil.show(getActivity(), "用户名或密码为空");
						return;
					}
					try {
						AccountManager.getInstance(con).createAccount(username,
								password);
						ToastUtil.show(getActivity(), "创建用户成功,正在登陆...");
						createUser = true;
					} catch (NoResponseException | XMPPErrorException
							| NotConnectedException e) {
						e.printStackTrace();
						ToastUtil.show(getActivity(), "创建用户发生错误,用户已存在或者网络错误");
						return;
					}

					if (createUser = true) {
						new Thread(new Runnable() {
							@Override
							public void run() {
								try {
									con.login(username, password);
									Connection.setAccount(username);
									if (con.isAuthenticated()) {
										Connection.isConnected = true ;
										Intent intent = new Intent(getActivity(),
												HomeActivity.class);
										startActivity(intent);
										getActivity().finish();
									}
								} catch (XMPPException | SmackException | IOException e) {
									e.printStackTrace();
								}
							}
						}).start();
					}
				}
	}

}
