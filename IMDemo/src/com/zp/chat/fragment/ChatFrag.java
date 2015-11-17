package com.zp.chat.fragment;

import com.zp.chat.R;
import com.zp.chat.base.BaseFragment;

import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ChatFrag extends BaseFragment{

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fra_chat, container, false);
		initView(view);
		initEvent();

		return view;
	}

	private void initEvent() {
		
	}

	private void initView(View view) {
		
	}
}
