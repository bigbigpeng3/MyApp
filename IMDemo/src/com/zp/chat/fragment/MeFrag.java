package com.zp.chat.fragment;

import com.zp.chat.Connection;
import com.zp.chat.R;
import com.zp.chat.base.BaseFragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class MeFrag extends BaseFragment implements OnClickListener {

	// private TextView tvName;
	private TextView tvAccount;
	// private ImageView ivIcon;
	private View mAccountView;
	private View mSettingView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// dao = new AccountDao(getActivity());
		// account = dao.getCurrentAccount();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fra_me, container, false);
		initView(view);
		initEvent();
		return view;
	}

	private void initView(View view) {
		// mTopBar = (NormalTopBar) view.findViewById(R.id.me_top_bar);
		// tvName = (TextView) view.findViewById(R.id.me_tv_name);

		tvAccount = (TextView) view.findViewById(R.id.me_tv_account);
		if (!TextUtils.isEmpty(Connection.account)) {
			tvAccount.setText(Connection.account);
		}
		// ivIcon = (ImageView) view.findViewById(R.id.me_iv_icon);
		mAccountView = view.findViewById(R.id.me_item_account);
		mSettingView = view.findViewById(R.id.me_item_setting);
		// mTopBar.setBackVisibility(false);
		// mTopBar.setTitle("我");

	}

	private void initEvent() {
		mAccountView.setOnClickListener(this);
		mSettingView.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == mAccountView) {
			clickAccount();
		} else if (v == mSettingView) {
			clickSetting();
		}
	}

	private void clickSetting() {
		// Intent intent = new Intent(getActivity(), SettingActivity.class);
		// startActivity(intent);
	}

	private void clickAccount() {
		// Intent intent = new Intent(getActivity(),
		// PersonalInfoActivity.class);
		// intent.putExtra(PersonalInfoActivity.KEY_INTENT, account);
		// startActivityForResult(intent, REQUEST_CODE_PERSONAL);
	}

}
