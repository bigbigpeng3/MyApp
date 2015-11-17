package com.zp.mobliesafe.activity;

import com.zp.mobliesafe.R;
import com.zp.mobliesafe.db.dao.AddressDao;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 归属地查询page
 * @author none1
 *
 */

public class AddressActivity extends Activity {

	private EditText etNumber ;
	private TextView tvResult;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address);
		
		etNumber = (EditText) findViewById(R.id.et_number);
		tvResult = (TextView) findViewById(R.id.tv_result);
		
		//监听EditText的变化
		etNumber.addTextChangedListener(new TextWatcher() {
			
			//文字发生变化的回调
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
				String address = AddressDao.getAddress(s.toString());
				
				tvResult.setText(address);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
			//文字变化后的回调
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
	}
	
	
	public void query(View view){
		String number = etNumber.getText().toString().trim();
		if (!TextUtils.isEmpty(number)) {
			String address = AddressDao.getAddress(number);	
			tvResult.setText(address);
		}else {
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			etNumber.startAnimation(shake);
			vibrate();
		}
	}
	
	private void vibrate(){
		Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		//vibrator.vibrate(1500);
		vibrator.vibrate(new long[]{100,200,300,200}, -1);
		//参数1，100 先暂停0.1秒,震动0.2秒，等待0.3秒,再震动0.2秒。参数2，-1只执行一次，不循环
		
	}
	
}
