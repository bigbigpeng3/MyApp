package com.itheima.zhbj52.global;

/**
 * 定义全局参数
 * 
 * @author Kevin
 * 
 */
public class GlobalContants {

	
	//改成我的wifi地址 http://192.168.191.1:8080
	//public static final String SERVER_URL_1 = "http://10.0.2.2:8080/zhbj";
	public static final String SERVER_URL = "http://192.168.191.1:8080/zhbj";
	// public static final String SERVER_URL =
	// "http://zhihuibj.sinaapp.com/zhbj";
	public static final String CATEGORIES_URL = SERVER_URL + "/categories.json";// 获取分类信息的接口
	public static final String PHOTOS_URL = SERVER_URL
			+ "/photos/photos_1.json";// 获取组图信息的接口

}
