package com.zp.mobliesafe.activity;

import com.zp.mobliesafe.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Setup4Activity extends BaseSetupActivity {

//	private SharedPreferences mPref;

	private CheckBox cbProtect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_setup4);
		
		cbProtect = (CheckBox) findViewById(R.id.cb_protect);
		
		boolean protect = mPref.getBoolean("protect", false);
		
		//根据sp保存的状态保存CB
		if (protect) {
			cbProtect.setText("防盗保护已经开启");
			cbProtect.setChecked(true);
		}else {
			cbProtect.setText("防盗保护没有开启");
			cbProtect.setChecked(false);
		}
		
		cbProtect.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					cbProtect.setText("防盗保护已经开启");
					mPref.edit().putBoolean("protect", true).commit();
				}else {
					cbProtect.setText("防盗保护没有开启");
					mPref.edit().putBoolean("protect", false).commit();
				}
			}
		});
		
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
		startActivity(new Intent(this, Setup3Activity.class));
		finish();
		overridePendingTransition(R.anim.tran_previous_in, R.anim.tran_previous_out);//进入和退出
	}

	@Override
	public void showNextPage() {
		startActivity(new Intent(this, LostFindActivity.class));
		finish();
		mPref.edit().putBoolean("configed", true).commit();
		//两个页面的转换动画
				overridePendingTransition(R.anim.tran_in, R.anim.tran_out);//进入和退出
	}

}
