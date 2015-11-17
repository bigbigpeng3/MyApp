package com.zp.chat.activity;

import com.zp.chat.R;
import com.zp.chat.base.BaseActivity;
import com.zp.chat.db.MyOpenhelper;
import com.zp.chat.fragment.LogoFrag;
import com.zp.chat.fragment.SignInFrag;
import com.zp.chat.fragment.SignUpFrag;
import com.zp.chat.receiver.InternetConnectedReceiver;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;

public class LoginActivity extends BaseActivity {

	public static final String TAG_LOGO = "logo";
	public static final String TAG_SIGN_IN = "sign_in";
	public static final String TAG_SIGN_UP = "sign_up";

	// Fragment声明
	private Fragment currentFra;
	private FragmentManager fm;

	// SQLite的openhelper
	private MyOpenhelper myHelper;

	// 关于广播接收的声明
	private IntentFilter intentFiler;
	private InternetConnectedReceiver myNetReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		fm = getSupportFragmentManager();
		initView();
		creatDB();

		// 广播相关
		intentFiler = new IntentFilter();
		intentFiler.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		myNetReceiver = new InternetConnectedReceiver();
		registerReceiver(myNetReceiver, intentFiler);

	}

	// 创建聊天记录的数据库
	private void creatDB() {

		myHelper = MyOpenhelper.getInstance(LoginActivity.this);
		myHelper.getWritableDatabase();
	}

	public void initView() {
		setContentView(R.layout.act_login);
		currentFra = new LogoFrag();
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.replace(R.id.contanier_login, currentFra, TAG_LOGO);
		// transaction.addToBackStack(currentTag);
		transaction.commit();
	}

	public void goToSignIn() {
		currentFra = new SignInFrag();
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.replace(R.id.contanier_login, currentFra, TAG_SIGN_IN);
		transaction.addToBackStack(TAG_SIGN_IN);
		transaction.commit();
	}

	public void goToSigUp() {
		currentFra = new SignUpFrag();
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.replace(R.id.contanier_login, currentFra, TAG_SIGN_UP);
		transaction.addToBackStack(TAG_SIGN_UP);
		transaction.commit();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(myNetReceiver);
	}

}
