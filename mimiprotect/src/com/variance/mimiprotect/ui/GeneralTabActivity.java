package com.variance.mimiprotect.ui;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.SessionInitializationListener;
import com.variance.mimiprotect.contacts.task.LoginTask;
import com.variance.mimiprotect.ui.contact.NewContactActivity;
import com.variance.mimiprotect.util.MimiProtectGeneralManager;
import com.variance.mimiprotect.util.MimiProtectIntentConstants;
import com.variance.mimiprotect.util.Settings;
import com.variance.mimiprotect.util.UserSetting;

public class GeneralTabActivity extends TabActivity {
	private TabHost tabHost;
	public static GeneralTabActivity GENERAL_TAB_ACTIVITY;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usercontact_tabcontent);
		GENERAL_TAB_ACTIVITY = this;
		tabHost = getTabHost(); // The activity TabHost
		// Initialize a TabSpec for each tab and add it to the TabHost
		// add phone book tab
		setUI();
		tabHost.setCurrentTab(0);
	}

	public TextView getTitleView() {
		return (TextView) findViewById(R.id.txtTabViewTitle);
	}

	private void setUI() {
		Intent i = getIntent();
		if (i != null) {
			String iClass = i
					.getStringExtra(MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_CLASS);
			String title = i
					.getStringExtra(MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_TITLE);
			int tabviewLayout = i
					.getIntExtra(
							MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_TABVIEW_LAYOUT,
							R.layout.usercontact_whitetitled_tabview);
			boolean toLogin = false;
			boolean addingContactFromExternal = isOnFromExternalIntentFiredForAddContact();
			if (addingContactFromExternal) {
				if (!i.hasExtra(MimiProtectIntentConstants.MIMI_PROTECT_FROM_CREATE_CONTACT)) {
					if (!tryLogin(
							NewContactActivity.class.getName(),
							"My Phonebook",
							MimiProtectIntentConstants.MIMI_PROTECT_FROM_CREATE_CONTACT)) {
						toLogin = true;
					}
				}
				if (toLogin) {
					iClass = LoginActivity.class.getName();
					title = "mimi";
				} else {
					iClass = NewContactActivity.class.getName();
					title = "My Phonebook";
				}
			} else if (i
					.hasExtra(MimiProtectIntentConstants.ON_LIVELINK_NOTIFICATION_EXTRA)
					&& i.getBooleanExtra(
							MimiProtectIntentConstants.ON_LIVELINK_NOTIFICATION_EXTRA,
							false)) {
				// check if we are logged in first
				if (!tryLogin(
						iClass,
						"Livelink Requests",
						MimiProtectIntentConstants.ON_LIVELINK_NOTIFICATION_EXTRA)) {
					iClass = LoginActivity.class.getName();
					title = "My Phonebook";
					toLogin = true;
				}
			}
			if (iClass != null) {
				try {
					Class<?> iClazz = Class.forName(iClass);
					View tabView = LayoutInflater.from(this).inflate(
							tabviewLayout, null);
					if (title != null && !"".equals(title.trim())) {
						final TextView txtView = (TextView) tabView
								.findViewById(R.id.txtTabViewTitle);
						final ImageView imageView = (ImageView) tabView
								.findViewById(R.id.networkState);
						if (txtView != null) {
							txtView.setText(title);
							if (Settings.isLoggedIn()) {
								imageView
										.setImageResource(R.drawable.mimi_connect_network_online);
							} else {
								// we add a listener
								Settings.addSessionInitializationListener(new SessionInitializationListener() {

									@Override
									public void sessionInitialized() {
										runOnUiThread(new Runnable() {

											@Override
											public void run() {
												if (Settings.isLoggedIn()) {
													imageView
															.setImageResource(R.drawable.mimi_connect_network_online);
												}
											}
										});
									}

									@Override
									public void sessionDestroyed() {
									}
								});
							}
						}
					}
					Intent intent = new Intent(this, iClazz);
					intent.putExtras(i);
					if (toLogin || addingContactFromExternal) {
						intent.putExtra(
								MimiProtectIntentConstants.MIMI_PROTECT_FROM_CREATE_CONTACT,
								true);
						intent.putExtra(
								MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_CLASS,
								NewContactActivity.class.getName());
						intent.putExtra(
								MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_TITLE,
								"My Phonebook");
					}
					TabHost.TabSpec spec = tabHost
							.newTabSpec("general_activity_tab")
							.setIndicator(tabView).setContent(intent);
					tabHost.addTab(spec);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private boolean isOnFromExternalIntentFiredForAddContact() {
		Intent ii = getIntent();
		if ((ii != null && ii.getAction() != null && ii.getAction().equals(
				Intent.ACTION_INSERT))) {
			return true;
		}
		return false;
	}

	/**
	 * We try to login if we are going straight to create new contact if
	 * possible
	 */
	private boolean tryLogin(String iClassName, String title,
			String booleanExtraContstant) {
		Log.i("GeneralTabActivity::tryLogin()", "tryLogin()");
		if (Settings.getSessionID() != null) {
			Log.i("GeneralTabActivity::tryLogin():Already Logged in:",
					true + "");
			return true; // we are already logged in
		}
		// we init login
		MimiProtectGeneralManager.initUserSetting(this);
		UserSetting settings = MimiProtectGeneralManager
				.getUserSettingOverride();
		if (settings.isEnableAutomaticLogin()) {
			Intent intent = getIntent();
			intent.putExtra(
					MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_CLASS,
					iClassName);
			intent.putExtra(
					MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_TITLE,
					title);
			intent.putExtra(booleanExtraContstant, true);
			String user = settings.getUsername();
			String password = settings.getPassword();
			new LoginTask(user, password, this, true).execute(new String[] {});
			return true;
		}
		return false;
	}
}
