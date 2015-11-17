package com.zp.mobliesafe.view;

import com.zp.mobliesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingClickView extends RelativeLayout {

	private TextView tvTitle;
	private TextView tvDecs;
	
	public SettingClickView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView();
	}

	public SettingClickView(Context context, AttributeSet attrs) {
		super(context, attrs);

		initView();		
	}

	public SettingClickView(Context context) {
		super(context);
		initView();
	}

	private void initView() {
		// 将自定义好的布局文件给当前的SettingItemView
		View.inflate(getContext(), R.layout.view_setting_click, this);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvDecs = (TextView) findViewById(R.id.tv_desc);
	}

	public void setTitle(String title) {
		tvTitle.setText(title);
	}

	public void setDesc(String desc) {
		tvDecs.setText(desc);
	}

}
