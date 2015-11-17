package com.zp.mobliesafe.activity;

import com.zp.mobliesafe.R;
import com.zp.mobliesafe.utils.ToastUtils;
import com.zp.mobliesafe.view.SettingItemView;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

public class Setup2Activity extends BaseSetupActivity {

	private SettingItemView sivSim;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_setup2);

		sivSim = (SettingItemView) findViewById(R.id.siv_sim);

		String sim = mPref.getString("sim", null);
		if (!TextUtils.isEmpty(sim)) {
			sivSim.setChecked(true);
		} else {
			sivSim.setChecked(false);
		}
		sivSim.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (sivSim.isChecked()) {
					sivSim.setChecked(false);
					// 删除已绑定的SIM卡
					mPref.edit().remove("sim").commit();
				} else {
					sivSim.setChecked(true);
					// 保存SIM卡的信息
					TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
					String simSerialNumber = tm.getSimSerialNumber();
					System.out.println("sim SerialNumber:" + simSerialNumber);

					// 将sim序列号保存唉SP中
					mPref.edit().putString("sim", simSerialNumber).commit();

				}
			}
		});
	}

	@Override
	public void showPreviousPage() {
		startActivity(new Intent(this, Setup1Activity.class));
		finish();
		overridePendingTransition(R.anim.tran_previous_in, R.anim.tran_previous_out);// 进入和退出

	}

	@Override
	public void showNextPage() {
		// 判断没有绑定SIM卡时，不能跳转到下一步
		String sim = mPref.getString("sim", null);
		if (TextUtils.isEmpty(sim)) {
			ToastUtils.ShowToast(this, "SIM必须要绑定");
			return;
		}
		startActivity(new Intent(this, Setup3Activity.class));
		finish();
		// 两个页面的转换动画
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);// 进入和退出
		// overridePendingTransition(enterAnim, exitAnim);

	}

}
