package com.zp.chat.fragment;

import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import com.zp.chat.Connection;
import com.zp.chat.R;
import com.zp.chat.activity.LoginActivity;
import com.zp.chat.base.BaseFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class LogoFrag extends BaseFragment implements OnClickListener {

	public XMPPTCPConnection con;

	private Button btnSignUp;
	private Button btnSignIn;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fra_logo, container, false);
		initView(view);
		initEvent();
		return view;
	}

	public void initView(View view) {
		btnSignIn = (Button) view.findViewById(R.id.btn_sign_in);
		btnSignUp = (Button) view.findViewById(R.id.btn_sign_up);
	}

	public void initEvent() {
		btnSignIn.setOnClickListener(this);
		btnSignUp.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_sign_in:
			connectServer();
			signIn();
			break;
		case R.id.btn_sign_up:
			connectServer();
			signUp();
			break;
		default:
			break;
		}
	}

	private void signIn() {
		FragmentActivity activity = getActivity();
		if (activity != null) {
			((LoginActivity) activity).goToSignIn();
		}
	}

	private void signUp() {
		FragmentActivity activity = getActivity();
		if (activity != null) {
			((LoginActivity) activity).goToSigUp();
		}
	}

	public void connectServer() {

		if (Connection.isConnected) {
			Connection.closeConnect();
			Connection.isConnected = false;
		}
		if (con == null) {
			Connection.initConnection();
		}

	}
}
