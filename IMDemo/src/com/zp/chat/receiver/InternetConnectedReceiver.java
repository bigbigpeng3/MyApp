package com.zp.chat.receiver;

import com.zp.chat.utils.ToastUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class InternetConnectedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();

		if (activeNetworkInfo != null && activeNetworkInfo.isAvailable()) {
			ToastUtil.show(context, "网络已连接");
		} else {
			ToastUtil.show(context, "网络未连接,请检查网络设置");
		}
	}

}
