package com.zp.mobliesafe.activity;

import com.zp.mobliesafe.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Setup1Activity extends BaseSetupActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_setup1);
	}

	// button的点击事件，跳转到下一页
	public void next(View view) {
		
		showNextPage();
	}

	@Override
	public void showPreviousPage() {
		
	}

	@Override
	public void showNextPage() {
		startActivity(new Intent(this,Setup2Activity.class));
		finish();
		//两个页面的转换动画
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);//进入和退出
	}

	

}
