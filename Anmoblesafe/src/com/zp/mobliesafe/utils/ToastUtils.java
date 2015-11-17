package com.zp.mobliesafe.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {

	
	public static void  ShowToast(Context context,String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
}
