package com.zp.mobliesafe.view;

import com.zp.mobliesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingItemView extends RelativeLayout {

	private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.zp.mobliesafe";
	private TextView tvTitle;
	private TextView tvDecs;
	private CheckBox cbstatus;
	private String mtitle;
	private String mDecsON;
	private String mDecsOff;

	public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView();
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mtitle = attrs.getAttributeValue(NAMESPACE, "title1");
		mDecsON = attrs.getAttributeValue(NAMESPACE, "desc_on");
		mDecsOff = attrs.getAttributeValue(NAMESPACE, "desc_off");
		initView();		
	}

	public SettingItemView(Context context) {
		super(context);
		initView();
	}

	private void initView() {
		// 将自定义好的布局文件给当前的SettingItemView
		View.inflate(getContext(), R.layout.view_setting_item, this);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvDecs = (TextView) findViewById(R.id.tv_desc);
		cbstatus = (CheckBox) findViewById(R.id.cb_status);
		 
		setTitle(mtitle);
		
	}

	public void setTitle(String title) {
		tvTitle.setText(title);
	}

	public void setDesc(String desc) {
		tvDecs.setText(desc);
	}

	public boolean isChecked() {
		return cbstatus.isChecked();
	}

	public void setChecked(boolean check) {
		cbstatus.setChecked(check);
		
		//根据选择的状态更新TextView
		if (check) {
			setDesc(mDecsON);  
			
		}else{
			setDesc(mDecsOff);
		}
	}

}
