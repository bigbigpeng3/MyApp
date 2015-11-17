package com.zp.mobliesafe.receiver;

import com.zp.mobliesafe.db.dao.AddressDao;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * 去电的广播接受者
 * @author none1
 *
 */
public class OutCallReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		
		String number = getResultData();
		
		String address = AddressDao.getAddress(number);
		
		//Toast.makeText(context,address, Toast.LENGTH_LONG).show();
		
		
	}
	
	
	
}
