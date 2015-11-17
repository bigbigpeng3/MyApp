package com.zp.mobliesafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

/**
 * 服务状态工具类
 * 
 * @author none1
 *
 */
public class ServiceStatusUtils {

	/**
	 * 检查服务是否在运行
	 * 
	 * @return
	 */
	public static boolean isServiceRunning(Context context,String serviceName) {

		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		
		//获取所有的服务，最多返回100个
		List<RunningServiceInfo> runningServices = am.getRunningServices(100);

		for (RunningServiceInfo runningServiceInfo : runningServices) {
			
			String className = runningServiceInfo.service.getClassName();
			
			if (className.equals(serviceName)) {
				return true ;
			}
		}
		
		return false;
	}
}
