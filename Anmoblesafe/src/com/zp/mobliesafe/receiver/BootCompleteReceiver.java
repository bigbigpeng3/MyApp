package com.zp.mobliesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * 监听手机开机的启动广播
 * 
 * @author none1
 *
 */
public class BootCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		boolean on = sp.getBoolean("protect", false);
		System.out.println("BootCompleteReceiver : on " + on);
		if (on) {
			String sim = sp.getString("sim", null);// 获取绑定的SIM卡
			if (!TextUtils.isEmpty(sim)) {
				// 获取当前手机的SIM卡
				System.out.println("BootCompleteReceiver SIM :" + sim);
				TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
				String currentSim = tm.getSimSerialNumber() + "111";

				if (sim.equals(currentSim)) {
					System.out.println("手机安全");
				} else {
					System.out.println("手机危险");
					String phone = sp.getString("safe_phone", "");
					// 发送短信的逻辑
					SmsManager smsManager = SmsManager.getDefault();
					smsManager.sendTextMessage(phone, null, "SIM changed", null, null);

				}
			}
		}

	}

}
