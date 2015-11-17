package com.zp.chat.activity;

import com.zp.chat.R;
import com.zp.chat.base.BaseActivity;
import com.zp.chat.fragment.ChatFrag;
import com.zp.chat.fragment.ContactFrag;
import com.zp.chat.fragment.DiscoverFrag;
import com.zp.chat.fragment.MeFrag;
import com.zp.chat.service.ConnectionService;
import com.zp.chat.widget.TabIndicator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

public class HomeActivity extends BaseActivity implements OnTabChangeListener {

	private FragmentTabHost mTabHost;

	private TabIndicator mChatIndicator;
	private TabIndicator mContactIndicator;
	private TabIndicator mDiscoverIndicator;
	private TabIndicator mMeIndicator;

	private static final String TAB_CHAT = "chat";
	private static final String TAB_CONTACT = "contact";
	private static final String TAB_DISCOVER = "discover";
	private static final String TAB_ME = "me";

	

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.act_home);

		initTabHost();
		initIndicator();
		initTab();
		
		Intent intent = new Intent(this, ConnectionService.class);
		startService(intent);

	}

	

	private void initTabHost() {
		mTabHost = (FragmentTabHost) findViewById(R.id.mytabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.homeTabContent);
		mTabHost.setOnTabChangedListener(this);
	}

	private void initIndicator() {
		mChatIndicator = new TabIndicator(this);
		mChatIndicator.setTabIcon(R.drawable.tab_icon_chat_normal,
				R.drawable.tab_icon_chat_focus);
		mChatIndicator.setTabHint(R.string.home_tab_chat);

		mContactIndicator = new TabIndicator(this);
		mContactIndicator.setTabIcon(R.drawable.tab_icon_contact_normal,
				R.drawable.tab_icon_contact_focus);
		mContactIndicator.setTabHint(R.string.home_tab_contact);

		mDiscoverIndicator = new TabIndicator(this);
		mDiscoverIndicator.setTabIcon(R.drawable.tab_icon_discover_normal,
				R.drawable.tab_icon_discover_focus);
		mDiscoverIndicator.setTabHint(R.string.home_tab_discover);

		mMeIndicator = new TabIndicator(this);
		mMeIndicator.setTabIcon(R.drawable.tab_icon_me_normal,
				R.drawable.tab_icon_me_focus);
		mMeIndicator.setTabHint(R.string.home_tab_me);
	}

	private void initTab() {
		TabSpec chatSpec = mTabHost.newTabSpec(TAB_CHAT);
		chatSpec.setIndicator(mChatIndicator);
		mTabHost.addTab(chatSpec, ChatFrag.class, null);

		TabSpec contactSpec = mTabHost.newTabSpec(TAB_CONTACT);
		contactSpec.setIndicator(mContactIndicator);
		mTabHost.addTab(contactSpec, ContactFrag.class, null);

		TabSpec discoverSpec = mTabHost.newTabSpec(TAB_DISCOVER);
		discoverSpec.setIndicator(mDiscoverIndicator);
		mTabHost.addTab(discoverSpec, DiscoverFrag.class, null);

		TabSpec meSpec = mTabHost.newTabSpec(TAB_ME);
		meSpec.setIndicator(mMeIndicator);
		mTabHost.addTab(meSpec, MeFrag.class, null);

		setCurrentTabByTag(TAB_CHAT);
	}

	private void setCurrentTabByTag(String tag) {
		mChatIndicator.setCurrentFocus(false);
		mContactIndicator.setCurrentFocus(false);
		mDiscoverIndicator.setCurrentFocus(false);
		mMeIndicator.setCurrentFocus(false);

		mTabHost.setCurrentTabByTag(tag);
		if (TAB_CHAT.equals(tag)) {
			mChatIndicator.setCurrentFocus(true);
		} else if (TAB_CONTACT.equals(tag)) {
			mContactIndicator.setCurrentFocus(true);
		} else if (TAB_DISCOVER.equals(tag)) {
			mDiscoverIndicator.setCurrentFocus(true);
		} else if (TAB_ME.equals(tag)) {
			mMeIndicator.setCurrentFocus(true);
		}
	}

	@Override
	public void onTabChanged(String tabId) {
		setCurrentTabByTag(tabId);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Intent intent = new Intent(this, ConnectionService.class);
		stopService(intent);
	}

}
