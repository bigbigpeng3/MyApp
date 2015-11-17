package com.zp.mobliesafe.activity;

import com.zp.mobliesafe.R;
import com.zp.mobliesafe.utils.ToastUtils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Setup3Activity extends BaseSetupActivity {

	private EditText number;

	private Button choose;

	private String userNumber, userName;

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_setup3);

		number = (EditText) findViewById(R.id.et_num);
		choose = (Button) findViewById(R.id.btn_choose);
		
		String phone = mPref.getString("safe_phone", "");
		number.setText(phone);

		choose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), 0);
			}
		});

	}

	/**
	 * 从系统联系人中获取号码
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {
			// ContentProvider展示数据类似一个单个数据库表
			// ContentResolver实例带的方法可实现找到指定的ContentProvider并获取到ContentProvider的数据
			ContentResolver reContentResolver = getContentResolver();
			// URI,每个ContentProvider定义一个唯一的公开的URI,用于指定到它的数据集
			Uri contactData = data.getData();
			// 查询就是输入URI等参数,其中URI是必须的,其他是可选的,如果系统能找到URI对应的ContentProvider将返回一个Cursor对象.
			Cursor cursor = managedQuery(contactData, null, null, null, null);
			cursor.moveToFirst();
			// 获得DATA表中的名字
			userName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			// 条件为联系人ID
			String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
			// 获得DATA表中的电话号码，条件为联系人ID,因为手机号码可能会有多个
			Cursor phone = reContentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
			while (phone.moveToNext()) {
				userNumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				number.setText(userNumber);
				// +"("+userName+")"
			}
		}
	}

	// button的点击事件，跳转到下一页
	public void next(View view) {

		showNextPage();
	}

	public void previous(View view) {

		showPreviousPage();
	}

	@Override
	public void showPreviousPage() {

		startActivity(new Intent(this, Setup2Activity.class));
		finish();
		overridePendingTransition(R.anim.tran_previous_in, R.anim.tran_previous_out);// 进入和退出

	}

	@Override
	public void showNextPage() {
		String phone = number.getText().toString().trim();

		if (TextUtils.isEmpty(phone)) {
			ToastUtils.ShowToast(this, "安全号码不能为空");
			return;
		}
		
		mPref.edit().putString("safe_phone", phone).commit();
		
		startActivity(new Intent(this, Setup4Activity.class));
		finish();
		// 两个页面的转换动画
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);// 进入和退出
	}

}
