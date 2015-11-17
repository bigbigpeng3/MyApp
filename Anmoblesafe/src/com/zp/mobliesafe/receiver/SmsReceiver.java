package com.zp.mobliesafe.receiver;


import com.zp.mobliesafe.R;
import com.zp.mobliesafe.service.LocatioService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		Object[] objects = (Object[]) intent.getExtras().get("pdus");
		//最多160个字符，70个汉字
		for(Object object : objects){
			
			SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
			//短信来源号码
			String originatingAddress = message.getOriginatingAddress();
			//短信内容
			String messageBody = message.getMessageBody();
			
			System.out.println(originatingAddress+":"+messageBody);
			
			if ("#*alarm*#".equals(messageBody)) {
				//播放报警音乐，即使手机调为静音，也能播放音乐，因为使用的是播放媒体声音的通道和铃声无关
				MediaPlayer player = MediaPlayer.create(context,R.raw.ylzs);
				player.setVolume(1f, 1f);
				player.setLooping(true);
				player.start();
				
				abortBroadcast();//中断短信的传递，系统的APP就接收不到了
			}else if ("#*location*#".equals(messageBody)) {
				//获得经纬度
				//开启定位服务
				context.startService(new Intent(context,LocatioService.class));
				
				SharedPreferences sp = context.getSharedPreferences("config",context.MODE_PRIVATE);
				
				String location = sp.getString("location", "getting location...");
				
				System.out.println("location:"+location);
				abortBroadcast();
			}else if ("#*wipedata*#".equals(messageBody)) {
				
				
			}else if ("#*lockscreen*#".equals(messageBody)) {
				
				
			}
		}
		
	}

}
