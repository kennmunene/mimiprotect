package com.variance.mimiprotect.ui.dashboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.variance.mimiprotect.R;
import com.variance.mimiprotect.SessionInitializationListener;
import com.variance.mimiprotect.business.directory.BusinessDirectoryActivity;
import com.variance.mimiprotect.business.meeting.BusinessMeetingActivity;
import com.variance.mimiprotect.c2dm.pack.C2DMSettings;
import com.variance.mimiprotect.chat.MimiProtectChatManager;
import com.variance.mimiprotect.contacts.business.BusinessContactActivity;
import com.variance.mimiprotect.contacts.business.NewBusinessActivity;
import com.variance.mimiprotect.contacts.task.HttpRequestTask;
import com.variance.mimiprotect.contacts.task.HttpRequestTaskListener;
import com.variance.mimiprotect.request.HttpRequestManager;
import com.variance.mimiprotect.response.HttpResponseConstants;
import com.variance.mimiprotect.response.HttpResponseData;
import com.variance.mimiprotect.response.HttpResponseStatus;
import com.variance.mimiprotect.ui.LiveLinkRequestsActivity;
import com.variance.mimiprotect.ui.LoginActivity;
import com.variance.mimiprotect.ui.MimiProtectActivity;
import com.variance.mimiprotect.ui.PersonalPhonebookActivity;
import com.variance.mimiprotect.ui.PhonebookActivity;
import com.variance.mimiprotect.ui.ProfileActivity;
import com.variance.mimiprotect.ui.contact.DiscoveryActivity;
import com.variance.mimiprotect.util.MimiProtectGeneralManager;
import com.variance.mimiprotect.util.Settings;
import com.variance.mimiprotect.util.UserSetting;
import com.variance.mimiprotect.util.Utils;

public class DashBoardActivity extends MimiProtectActivity {
	public static DashBoardActivity DASH_BOARD_ACTIVITY;
	private MimiProtectChatManager chatManager;
	private volatile boolean hasBusinessPhonebook;
	private volatile String businessName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mimi_connect_dashboard_layout);
		checkMimiWalletInstalled();
		addDashboardControls();
		doMimiProtectSettingSync();
	}

	@Override
	protected void onResume() {
		DASH_BOARD_ACTIVITY = this;
		super.onResume();
	}

	public MimiProtectChatManager getChatManager() {
		return chatManager;
	}

	public static Context getContext(){
		return DASH_BOARD_ACTIVITY;
	}
	
	public synchronized boolean isHasBusinessPhonebook() {
		Log.e("hasBusinessPhonebook", hasBusinessPhonebook + "");
		return hasBusinessPhonebook;
	}

	public synchronized void setHasBusinessPhonebook(
			boolean hasBusinessPhonebook) {
		this.hasBusinessPhonebook = hasBusinessPhonebook;
	}

	private String getLocalOfficePhonebookName() {
		if (MimiProtectGeneralManager.hasAccessibility()) {
			UserSetting userSetting = MimiProtectGeneralManager
					.getUserSettingOverride();
			if (userSetting.isBusinessPhonebookUser()) {
				String name = userSetting.getBusinessPhonebookName();
				if (!Utils.isNullStringOrEmpty(name)) {
					return name;
				}
			}
		}
		return "Office Phonebook";
	}

	public String getBusinessName() {
		if (Utils.isNullStringOrEmpty(businessName)) {
			synchronized (this) {
				if (Utils.isNullStringOrEmpty(businessName)) {
					businessName = getLocalOfficePhonebookName();
				}
			}
		}
		return businessName;
	}

	private void checkMimiWalletInstalled() {
		View view = findViewById(R.id.dashboard_header);
		if (view != null) {
			// view.setVisibility(View.GONE);
		}
	}

	private void loginToChatManager() {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				if (Settings.isLoggedIn()
						&& (chatManager == null || !chatManager.isConnected())) {
					chatManager = new MimiProtectChatManager();
					chatManager.login();
				}
				return null;
			}
		}.execute();
	}

	private void initActionListeners() {
		View view = findViewById(R.id.dashboard_header);
		if (view != null) {
			TextView txt = (TextView) view.findViewById(R.id.txtDashboardTitle);
			txt.setText((Settings.isLoggedIn() ? "online" : "offline"));
		}
		final Button btnPhonebook = (Button) findViewById(R.id.btn_phonebook);
		final Button btnCompanyphonebook = (Button) findViewById(R.id.btn_company);
		final Button btnDirectory = (Button) findViewById(R.id.btn_business_directory);
		final Button btnDiscover = (Button) findViewById(R.id.btn_discover);
		final Button btnProfile = (Button) findViewById(R.id.btn_profile);
		final Button btnLivelink = (Button) findViewById(R.id.btn_livelinks);
		if (!Settings.isSessionInitializing()) {
			btnPhonebook.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					if (Settings.isLoggedIn()
							|| MimiProtectGeneralManager.hasCurrentPhoneLock()) {
						startDashboardActivity("My Phonebook",
								PersonalPhonebookActivity.class);
					} else {
						Toast.makeText(
								DashBoardActivity.this,
								"You must be logged in to view your phonebook on this phone.",
								Toast.LENGTH_LONG).show();
					}
				}
			});
			btnCompanyphonebook.setText(getBusinessName());
			btnCompanyphonebook.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					showBusinessContact(getBusinessName());
				}
			});
			btnDirectory.setOnClickListener(new View.OnClickListener() {
				private final Activity activity = DashBoardActivity.this;

				public void onClick(View v) {
					showBusinessDirectory(activity);
				}
			});
			btnDiscover.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					if (Settings.isLoggedIn()) {
						startDashboardActivity("Discover",
								DiscoveryActivity.class);
					} else {
						Toast.makeText(
								DashBoardActivity.this,
								"Sorry! You must be logged in to perform discovery on your phone contacts.",
								Toast.LENGTH_LONG).show();
					}
				}
			});
			btnProfile.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					startDashboardActivity("My Profile", ProfileActivity.class);
				}
			});
			btnLivelink.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					if (Settings.isLoggedIn()) {
						startDashboardActivity("Livelink",
								LiveLinkRequestsActivity.class);
					} else {
						Toast.makeText(
								DashBoardActivity.this,
								"Sorry! You must be logged in to view or send livelink requests.",
								Toast.LENGTH_LONG).show();
					}
				}
			});
		}
	}

	private void initializeAllControlls() {
		// initialize controlls by logging in to
		loginToChatManager();
		new AsyncTask<Void, Void, HttpResponseData>() {
			final Button btnCompanyphonebook = (Button) findViewById(R.id.btn_company);

			@Override
			protected HttpResponseData doInBackground(Void... params) {
				if (HttpRequestManager.isOnline(DashBoardActivity.this)) {
					return getBusinessInformation();
				}
				return null;
			}

			@Override
			protected void onPostExecute(HttpResponseData response) {
				try {
					synchronized (DashBoardActivity.this) {
						if (response != null
								&& response.getResponseStatus() == HttpResponseStatus.SUCCESS) {
							Log.i("SessionInitializationListener",
									response.toString());
							hasBusinessPhonebook = Boolean
									.parseBoolean(response.getMessage());
							if (hasBusinessPhonebook) {
								businessName = response
										.getExtra(HttpResponseConstants.HTTP_RESPONSE_BUSINESS_NAME);
								Log.i("HTTP_RESPONSE_BUSINESS_NAME",
										businessName + "");
								if (Utils.isNullOrEmpty(businessName)) {
									businessName = "Office phonebook";
									hasBusinessPhonebook = false;
								} else {
									MimiProtectGeneralManager.getUserSetting()
											.setBusinessPhonebookName(
													businessName);
									MimiProtectGeneralManager.getUserSetting()
											.setBusinessPhonebookUser(
													hasBusinessPhonebook);
									MimiProtectGeneralManager
											.updateUserSetting();
								}
							}
						}
						if (!Utils.isNullOrEmpty(businessName)) {
							btnCompanyphonebook.setText(businessName);
						} else {
							businessName = getLocalOfficePhonebookName();
						}
					}
					Log.e("initializeAllControlls", businessName);
				} finally {
					initActionListeners();
				}
			}

		}.execute();
	}

	public void onOfficePhonebookCreated() {
		new AsyncTask<Void, Void, HttpResponseData>() {
			final Button btnCompanyphonebook = (Button) findViewById(R.id.btn_company);

			@Override
			protected HttpResponseData doInBackground(Void... params) {
				return getBusinessInformation();
			}

			@Override
			protected void onPostExecute(HttpResponseData response) {
				try {
					synchronized (DashBoardActivity.this) {
						if (response != null
								&& response.getResponseStatus() == HttpResponseStatus.SUCCESS) {
							hasBusinessPhonebook = Boolean
									.parseBoolean(response.getMessage());
							if (hasBusinessPhonebook) {
								businessName = response
										.getExtra(HttpResponseConstants.HTTP_RESPONSE_BUSINESS_NAME);
								Log.i("HTTP_RESPONSE_BUSINESS_NAME",
										businessName + "");
								if (Utils.isNullOrEmpty(businessName)) {
									businessName = "Office phonebook";
									hasBusinessPhonebook = false;
								} else if (MimiProtectGeneralManager
										.hasAccessibility()) {
									MimiProtectGeneralManager.getUserSetting()
											.setBusinessPhonebookName(
													businessName);
									MimiProtectGeneralManager.getUserSetting()
											.setBusinessPhonebookUser(
													hasBusinessPhonebook);
									MimiProtectGeneralManager
											.updateUserSetting();
								}
							}
						}
						if (!Utils.isNullOrEmpty(businessName)) {
							btnCompanyphonebook.setText(businessName);
						} else {
							businessName = getLocalOfficePhonebookName();
						}
					}
					Log.e("initializeAllControlls", businessName);
				} finally {
					initActionListeners();
				}
			}

		}.execute();
	}

	private void addSessionInitializationListenerIfNecessary() {
		boolean listenerAdded = false;
		if (Settings.isSessionInitializing()) {
			SessionInitializationListener listener = new SessionInitializationListener() {

				public void sessionInitialized() {
					Log.i("SessionInitializationListener",
							"SessionInitializationListener");
					initializeAllControlls();
				}

				public void sessionDestroyed() {
					synchronized (DashBoardActivity.this) {
						hasBusinessPhonebook = false;
					}
				}
			};
			if ((listenerAdded = Settings.isSessionInitializing())) {
				listenerAdded = Settings
						.addSessionInitializationListener(listener);
			}
		}
		if (!listenerAdded) {
			// session is already initialized
			// initialize here.
			initializeAllControlls();
		}
	}

	private void addDashboardControls() {
		addSessionInitializationListenerIfNecessary();
		initActionListeners();
	}

	private HttpResponseData getBusinessInformation() {
		return HttpRequestManager.doRequestWithResponseData(
				Settings.getBusinessContactUrl(),
				Settings.makeUserHasBusinessContactParameters());
	}

	private void showBusinessContact(String businessName) {
		if (hasBusinessPhonebook
				|| (MimiProtectGeneralManager.hasAccessibility() && MimiProtectGeneralManager
						.getUserSettingOverride().isBusinessPhonebookUser())) {
			startDashboardActivity(businessName, BusinessContactActivity.class);
		} else if (Settings.isLoggedIn()) {
			startDashboardActivity("Office Phonebook",
					NewBusinessActivity.class);
		} else {
			Toast.makeText(
					this,
					"You must be logged in to view/create office phonebook on this phone",
					Toast.LENGTH_LONG).show();
		}
	}

	public void downloadMimiMobile(View view) {
		String url = "https://www.mimimobile.com/mimiwallet.apk";
		Intent webView = new Intent(Intent.ACTION_VIEW);
		webView.setData(Uri.parse(url));
		startActivity(webView);
	}

	public void showActionOptions(View view) {
		View actionBar = findViewById(R.id.dashboardActionBar);
		if (actionBar != null) {
			if (actionBar.getVisibility() == View.GONE) {
				actionBar.setVisibility(View.VISIBLE);
			} else {
				actionBar.setVisibility(View.GONE);
			}
		}
	}

	public void handleBusinessMeeting(View view) {
		try {
			if (Settings.isLoggedIn()) {
				PhonebookActivity.startGeneralActivity(this,
						"Business Meeting", BusinessMeetingActivity.class,
						R.layout.usercontact_tabview);
			} else {
				Toast.makeText(
						this,
						"Sorry! You must be logged in to schedule or join a meeting.",
						Toast.LENGTH_LONG).show();
			}
		} finally {
			View actionBar = findViewById(R.id.dashboardActionBar);
			if (actionBar != null) {
				actionBar.setVisibility(View.GONE);
			}
		}
	}

	private void showBusinessDirectory(Activity activity) {
		if (Settings.isLoggedIn()) {
			startDashboardActivity("Business Listing",
					BusinessDirectoryActivity.class);
		} else {
			Toast.makeText(this,
					"Sorry! You must be logged in to view business listings",
					Toast.LENGTH_SHORT).show();
		}
	}

	public void startDashboardActivity(String title, Class<?> activityClass) {
		PhonebookActivity.startGeneralActivity(this, title, activityClass,
				R.layout.usercontact_tabview, false);
	}

	private void doMimiProtectSettingSync() {
		if (MimiProtectGeneralManager.hasAccessibility()) {
			Log.e("doMimiProtectSettingSync", "doMimiProtectSettingSync");
			doGCMRegistration();
			HttpRequestTaskListener<Void, HttpResponseData> httpRequestTaskHorse = new HttpRequestTaskListener<Void, HttpResponseData>() {

				public void onTaskStarted() {
				}

				public void onTaskCompleted(HttpResponseData data) {
					String statusSet = data != null ? data
							.getExtra(Settings.MIMIPROTECT_ANDROID_STATUS)
							: null;
					if (statusSet != null && statusSet.trim().equals("false")) {
						HttpRequestManager
								.doRequest(
										Settings.getSigninURL(),
										Settings.makeMimiProtectSettings(DashBoardActivity.this));
						PersonalPhonebookActivity
								.showMessage(
										"Mimi Protect SIM Settings",
										"Your SIM Settings seems to have changed, please update your contact information!"
												+ "\nSTORE, CONNECT, SHARE",
										DashBoardActivity.this);
					}
				}

				public HttpResponseData doTask(Void... params) {
					return HttpRequestManager
							.doRequestWithResponseData(
									Settings.getSigninURL(),
									Settings.makeMimiProtectSettingStatusRequest(DashBoardActivity.this));
				}
			};
			new HttpRequestTask<Void, Void, HttpResponseData>(
					httpRequestTaskHorse, "Checking Mimi Protect Settings",
					DashBoardActivity.this).executeInBackground();
		}
	}

	private void doGCMRegistration() {
		HttpRequestTaskListener<Void, HttpResponseData> httpRequestTaskHorse = new HttpRequestTaskListener<Void, HttpResponseData>() {

			public void onTaskStarted() {
			}

			public void onTaskCompleted(HttpResponseData data) {
				String statusSet = data != null ? data
						.getExtra(Settings.MIMIPROTECT_GCM_KEY_REGISTERED)
						: null;
				if (statusSet != null && statusSet.trim().equals("false")) {
					HttpRequestManager
							.doRequest(
									Settings.getSigninURL(),
									Settings.makeMimiProtectSettings(DashBoardActivity.this));
					try {
						GCMRegistrar.checkDevice(DashBoardActivity.this);
						GCMRegistrar.checkManifest(DashBoardActivity.this);
						GCMRegistrar.register(DashBoardActivity.this,
								C2DMSettings.SENDER_ID);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			public HttpResponseData doTask(Void... params) {
				return HttpRequestManager
						.doRequestWithResponseData(
								Settings.getLiveLinkRegistrationURL(),
								Settings.makeMimiProtectGCMStatusRequest(DashBoardActivity.this));
			}
		};
		new HttpRequestTask<Void, Void, HttpResponseData>(httpRequestTaskHorse,
				"Checking Mimi Protect Settings", DashBoardActivity.this)
				.executeInBackground();
	}

	@Override
	public void onBackPressed() {
		View actionBar = findViewById(R.id.dashboardActionBar);
		if (actionBar.getVisibility() == View.VISIBLE) {
			actionBar.setVisibility(View.GONE);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void finish() {
		setHasBusinessPhonebook(false);
		PersonalPhonebookActivity.logout(this, false,
				new OnRequestComplete<Boolean>() {

					@Override
					public void requestComplete(Boolean result) {
						PhonebookActivity.startGeneralActivity(
								DashBoardActivity.this, "mimi",
								LoginActivity.class);
						DashBoardActivity.super.finish();
					}

				});
	}

}
