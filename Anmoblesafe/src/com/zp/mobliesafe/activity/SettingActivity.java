package com.zp.mobliesafe.activity;

import com.zp.mobliesafe.R;
import com.zp.mobliesafe.service.AddressService;
import com.zp.mobliesafe.utils.ServiceStatusUtils;
import com.zp.mobliesafe.view.SettingClickView;
import com.zp.mobliesafe.view.SettingItemView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class SettingActivity extends Activity {

	private SettingItemView sivUpdate;// 设置升级
	private SettingItemView sivAddress;// 设置归属地
	private SettingClickView scvAddressStyle;// 设置归属地
	private SettingClickView scvAddressLocation;// 设置归属地
	SharedPreferences mPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		mPref = getSharedPreferences("config", MODE_PRIVATE);

		sivUpdate = (SettingItemView) findViewById(R.id.siv_update);
		sivUpdate.setTitle("自动更新设置");

		initAddressView();
		initUpdateView();
		initAddressStyle();
		initAddressLocation();
	}
	
	/**
	 * 显示归属地位置
	 */
	public void initAddressLocation(){
		
		scvAddressLocation = (SettingClickView) findViewById(R.id.scv_address_location);
		scvAddressLocation.setTitle("归属地提示显示位置");
		scvAddressLocation.setDesc("设置归属地的提示位置");
		
		scvAddressLocation.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(SettingActivity.this,DragViewActivity.class));
			}
		});
	}
	
	
	/**
	 * 修改提示框
	 */
	private void initAddressStyle(){
		scvAddressStyle = (SettingClickView) findViewById(R.id.scv_address_style);
		
		scvAddressStyle.setTitle("归属地提示框风格");
		int style = mPref.getInt("address_style", 0);//读取保存的style
		scvAddressStyle.setDesc(items[style]);
		scvAddressStyle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showSingleChooseDailog();
			}
		});
	}
	
	final String[] items = new String[] { "半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿" };

	protected void showSingleChooseDailog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("归属地提示风格");
		
		int style = mPref.getInt("address_style", 0);//读取保存的style
		builder.setSingleChoiceItems(items, style, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mPref.edit().putInt("address_style", which).commit();
				scvAddressStyle.setDesc(items[which]);//更新组合控件的描述
				dialog.dismiss();//让dialog消失
			}
		});
		
		builder.setNegativeButton("取消", null);
		builder.show();
	}
	/**
	 * 初始化更新开关
	 */
	private void initUpdateView() {
		// sivUpdate.setDesc("自动更新已开启");
		boolean autoUpdate = mPref.getBoolean("auto_update", true);
		if (autoUpdate) {

			sivUpdate.setDesc("自动更新已开启");
			sivUpdate.setChecked(true);
		} else {
			sivUpdate.setDesc("自动更新已关闭");
			sivUpdate.setChecked(false);
		}
		sivUpdate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 判断勾选状态
				if (sivUpdate.isChecked()) {
					sivUpdate.setChecked(false);
					// sivUpdate.setDesc("自动更新已关闭");
					mPref.edit().putBoolean("auto_update", false).commit();

				} else {
					sivUpdate.setChecked(true);
					// sivUpdate.setDesc("自动更新已开启");
					mPref.edit().putBoolean("auto_update", true).commit();
				}
			}
		});
	}

	/**
	 * 初始化归属地开关
	 */
	private void initAddressView() {
		
		sivAddress = (SettingItemView) findViewById(R.id.siv_address);
		//根据归属地服务器是否运行更新选择框
		boolean serviceRunning = ServiceStatusUtils.isServiceRunning(this,"com.zp.mobliesafe.service.AddressService");
		
		if (serviceRunning) {
			sivAddress.setChecked(true);
		}else {
			sivAddress.setChecked(false);
		}
		sivAddress.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (sivAddress.isChecked()) {
					sivAddress.setChecked(false);
					// 停止归属地服务
					stopService(new Intent(SettingActivity.this, AddressService.class));
					//mPref.edit().putBoolean("auto_update", false).commit();
				} else {
					sivAddress.setChecked(true);
					// 开始归属地服务
					startService(new Intent(SettingActivity.this, AddressService.class));
					//mPref.edit().putBoolean("auto_update", true).commit();
				}

			}
		});

	}
}
