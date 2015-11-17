package com.zp.mobliesafe.activity;

import com.zp.mobliesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 手机防盗页面
 * @author none1
 *
 */
public class LostFindActivity extends Activity{

	private SharedPreferences mPrefs;
	
	TextView tvSafePhone ;
	
	ImageView ivProtect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mPrefs = getSharedPreferences("config", MODE_PRIVATE);
		
		boolean configed = mPrefs.getBoolean("configed", false);
		
		
		
		if (configed) {
			setContentView(R.layout.activity_lost_find);
			
			//根据SP更新保护号码和锁
			tvSafePhone = (TextView) findViewById(R.id.tv_safe_phone);
			String phone = mPrefs.getString("safe_phone", null);
			tvSafePhone.setText(phone);
			
			ivProtect = (ImageView) findViewById(R.id.iv_protect);
			
			boolean protect = mPrefs.getBoolean("protect", false);
			
			if (protect) {
				ivProtect.setImageResource(R.drawable.lock);
			}else{
				ivProtect.setImageResource(R.drawable.unlock);
			}
			
		}else{
			//跳转设置向导页
			
			startActivity(new Intent(LostFindActivity.this,Setup1Activity.class));
			finish();
		}
		
		
	}
	
	public void reEnter(View view) {
		startActivity(new Intent(this, Setup1Activity.class));
		finish();
	}
}
