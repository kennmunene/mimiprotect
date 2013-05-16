package com.variance.mimiprotect.chat;

import java.util.HashMap;
import java.util.Map;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.util.MimiProtectIntentConstants;
import com.variance.mimiprotect.util.Utils;

/**
 * We separate this activity from the general chat activity cause we only need a
 * single instance to be running.
 * 
 * @author marembo
 * 
 */
public class ChatManagerTabActivity extends TabActivity {
	private TabHost tabHost;
	public static ChatManagerTabActivity CHAT_MANAGER;
	// chat id indicating the chats currently active
	public static Map<String, ChatManagerActivity> chats = new HashMap<String, ChatManagerActivity>();
	// The current tabs already added.
	public static Map<String, Integer> chatTabs = new HashMap<String, Integer>();
	private boolean active;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usercontact_tabcontent);
		CHAT_MANAGER = this;
		tabHost = getTabHost(); // The activity TabHost
		setTabChangeListener();
		setUI(getIntent());
		tabHost.setCurrentTab(0);
	}

	private void setTabChangeListener() {
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				int ix = tabId.lastIndexOf('_');
				if (ix != 0) {
					String chatId = tabId.substring(ix + 1);
					Log.i("onTabChanged:chatId", chatId);
					setCurrentActiveChat(chatId);
				}
			}
		});
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setUI(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		active = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		active = false;
	}

	@Override
	public void finish() {
		CHAT_MANAGER = null;
		chats.clear();
		chatTabs.clear();
		active = false;
		super.finish();
	}

	public boolean isActive() {
		return active;
	}

	public TextView getTitleView() {
		return (TextView) findViewById(R.id.txtTabViewTitle);
	}

	public void setCurrentActiveChat(String chatId) {
		Integer tab = chatTabs.get(chatId);
		Log.i("Current Tab:", "" + tab);
		if (tab != null) {
			tabHost.setCurrentTab(tab);
			for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
				if (i != tab) {
					tabHost.getTabWidget()
							.getChildAt(i)
							.setBackgroundResource(
									R.drawable.mimi_connect_top_button_no_stroke_background); // unselected
				}
				((TextView) tabHost.getTabWidget().getChildAt(i)
						.findViewById(R.id.txtTabViewTitle))
						.setCompoundDrawables(null, null, null, null);
			}
			tabHost.getTabWidget()
					.getChildAt(tabHost.getCurrentTab())
					.setBackgroundResource(
							R.drawable.mimi_connect_top_button_no_stroke_warped_background); // selected
		}
	}

	public void setChatIndicator(final String chatId, final String msg, String name) {
		if (!Utils.isNullStringOrEmpty(msg)) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Integer tab = chatTabs.get(chatId);
					int currentTab = tabHost.getCurrentTab();
					Log.i("Indicator Tab:", "" + tab);
					Log.i("Current Tab:", "" + currentTab);
					if (tab != null) {
						if (currentTab == tab) {
							return;
						}
						Toast.makeText(ChatManagerTabActivity.this,
								chatId + " says: \n" + msg, Toast.LENGTH_LONG)
								.show();
					}
				}
			});
		}
	}

	public void addCurrentActiveChat(ChatManagerActivity chatManagerActivity,
			String chatId) {
		chats.put(chatId, chatManagerActivity);
	}

	public void removeCurrentActiveChat(String chatId) {
		chats.remove(chatId);
		chatTabs.remove(chatId);
	}

	public ChatManagerActivity getCurrentActiveChat(String chatId) {
		return chats.get(chatId);
	}

	private void setUI(Intent i) {
		String title = i
				.getStringExtra(MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_TITLE);
		String chatId = i
				.getStringExtra(MimiProtectIntentConstants.MIMI_PROTECT_CHAT_ID);
		try {
			// check if we already have the tab
			if (chatTabs.containsKey(chatId)) {
				// then show current tab and go back/
				setCurrentActiveChat(chatId);
				return;
			}
			View tabView = LayoutInflater.from(this).inflate(
					R.layout.usercontact_chat_tabview, null);
			if (title != null && !"".equals(title.trim())) {
				TextView txtView = (TextView) tabView
						.findViewById(R.id.txtTabViewTitle);
				if (txtView != null) {
					txtView.setText(title);
				}
			}
			Intent intent = new Intent(this, ChatManagerActivity.class);
			intent.putExtras(i);
			TabHost.TabSpec spec = tabHost
					.newTabSpec("chat_activity_tab_" + chatId)
					.setIndicator(tabView).setContent(intent);
			tabHost.addTab(spec);
			// add the current chat tab.
			chatTabs.put(chatId, chatTabs.size());
			// set the current tab as active
			setCurrentActiveChat(chatId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
