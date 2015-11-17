package com.zp.chat.fragment;


import com.zp.chat.R;
import com.zp.chat.base.BaseFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DiscoverFrag extends BaseFragment {

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fra_discover, container, false);
		initView(view);
		return view;
	}
	
	private void initView(View view) {
		
	}

}
