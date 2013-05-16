package com.variance.mimiprotect.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.util.MimiProtectGeneralManager;
import com.variance.mimiprotect.util.Settings;
import com.variance.mimiprotect.util.UserSetting;
import com.variance.mimiprotect.util.Utils;

public class UserSettingActivity extends MimiProtectActivity {

	private UserSetting userSetting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usercontact_settings);
		userSetting = MimiProtectGeneralManager.getUserSetting();
		showSettingsStatus();
	}

	private void updateTextView() {
		TextView txtImei = (TextView) findViewById(R.id.txtImei);
		TextView txtUsername = (TextView) findViewById(R.id.txtUsername);
		TextView txtLastCacheSyncDate = (TextView) findViewById(R.id.txtLastCacheSyncDate);
		TextView txtCacheAndSyncPeriod = (TextView) findViewById(R.id.txtCacheAndSyncPeriod);
		txtImei.setText(userSetting.getImei());
		txtUsername.setText(userSetting.getUsername());
		String lastCacheUpdate = Utils.toIsoString(userSetting
				.getLastCacheUpdate());
		txtLastCacheSyncDate.setText(lastCacheUpdate);
		txtCacheAndSyncPeriod.setText("" + userSetting.getCacheUpdatePeriod()
				+ " DAYS");
	}

	private void showSettingsStatus() {
		final CheckBox cbEnableLocalCacheAndSync = (CheckBox) findViewById(R.id.cbEnableLocalCacheAndSync);
		cbEnableLocalCacheAndSync.setChecked(userSetting.isAllowLocalCache());
		cbEnableLocalCacheAndSync
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						enableLocalCacheAndSync(isChecked);
					}
				});
		final CheckBox cbEnableAutomaticLogin = (CheckBox) findViewById(R.id.cbEnableAutomaticLogin);
		cbEnableAutomaticLogin.setChecked(userSetting.isEnableAutomaticLogin());
		cbEnableAutomaticLogin
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						enableAutomaticLogin(isChecked);
					}
				});
		final CheckBox cbEnableShowDashboard = (CheckBox) findViewById(R.id.cbEnableShowDashboard);
		cbEnableShowDashboard.setChecked(userSetting.isShowDashboard());
		cbEnableShowDashboard
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						enableShowDashboard(isChecked);
					}
				});
		final CheckBox cbEnableShowSplashScreen = (CheckBox) findViewById(R.id.cbEnableShowSplashScreen);
		cbEnableShowSplashScreen.setChecked(userSetting.isShowSplashScreen());
		cbEnableShowSplashScreen
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						enableShowSplashScreen(isChecked);
					}
				});
		final CheckBox cbLockMimiProtectToThisPhone = (CheckBox) findViewById(R.id.cbLockMimiProtectToThisPhone);
		cbLockMimiProtectToThisPhone.setChecked(userSetting
				.isLockMimiProtectToCurrentPhone());
		cbLockMimiProtectToThisPhone
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						lockMimiProtectToThisPhone(isChecked);
					}
				});
		final CheckBox cbSaveChatMessages = (CheckBox) findViewById(R.id.cbSaveChatMessages);
		Log.i("cbSaveChatMessages", userSetting.isSaveChatMessages() + "");
		cbSaveChatMessages.setChecked(userSetting.isSaveChatMessages());
		cbSaveChatMessages
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						saveChatMessages(isChecked);
					}
				});
		updateTextView();
	}

	public void handleShowProfile(View view) {
		finish();
		PhonebookActivity.startGeneralActivity(this, "My Profile",
				ProfileActivity.class, R.layout.usercontact_tabview, false);
	}

	public void enableShowDashboard(boolean state) {
		userSetting.setShowDashboard(state);
		setRelevantInformation();
		boolean updated = MimiProtectGeneralManager.updateUserSetting();
		final CheckBox cbEnableShowDashboard = (CheckBox) findViewById(R.id.cbEnableShowDashboard);
		cbEnableShowDashboard.setChecked((!updated && !state)
				|| (updated && state));
		updateTextView();
		Log.i("enableShowDashboard:" + state, userSetting.serialize());
	}

	public void enableShowSplashScreen(boolean state) {
		Log.i("enableShowSplashScreen:", "enableShowSplashScreen");
		userSetting.setShowSplashScreen(state);
		setRelevantInformation();
		boolean updated = MimiProtectGeneralManager.updateUserSetting();
		final CheckBox cbEnableShowSplashScreen = (CheckBox) findViewById(R.id.cbEnableShowSplashScreen);
		cbEnableShowSplashScreen.setChecked((!updated && !state)
				|| (updated && state));
		updateTextView();

	}

	public void enableAutomaticLogin(boolean state) {
		userSetting.setEnableAutomaticLogin(state);
		setRelevantInformation();
		boolean updated = MimiProtectGeneralManager.updateUserSetting();
		final CheckBox cbEnableAutomaticLogin = (CheckBox) findViewById(R.id.cbEnableAutomaticLogin);
		cbEnableAutomaticLogin.setChecked((!updated && !state)
				|| (updated && state));
		updateTextView();

	}

	public void saveChatMessages(boolean state) {
		userSetting.setSaveChatMessages(state);
		setRelevantInformation();
		MimiProtectGeneralManager.updateUserSetting();
		updateTextView();
	}

	public void lockMimiProtectToThisPhone(boolean state) {
		state = state || userSetting.isAllowLocalCache();
		userSetting.setLockMimiProtectToCurrentPhone(state);
		setRelevantInformation();
		boolean updated = MimiProtectGeneralManager.updateUserSetting();
		final CheckBox cbLockMimiProtectToThisPhone = (CheckBox) findViewById(R.id.cbLockMimiProtectToThisPhone);
		boolean lockPhone = (!updated && !state) || (updated && state);
		cbLockMimiProtectToThisPhone.setChecked(lockPhone);
		updateTextView();

	}

	public void setCacheAndSyncPeriod(View view) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Set Cache Sync Period:");
		builder.setMessage("Please specify the Cache Sync period for regular sync of contacts."
				+ "\n Values must be in days.");
		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		builder.setView(input);
		builder.setPositiveButton("Set Sync Period",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String period = input.getText().toString();
						if (period != null && !"".equals(period)) {
							try {
								int p_ = Integer.parseInt(period);
								userSetting.setCacheUpdatePeriod(p_);
								setRelevantInformation();
								MimiProtectGeneralManager.updateUserSetting();
								updateTextView();
								dialog.dismiss();
							} catch (NumberFormatException e) {
								Toast.makeText(UserSettingActivity.this,
										"Only numbers are allowed",
										Toast.LENGTH_LONG).show();
							} catch (ArithmeticException e) {
								Toast.makeText(UserSettingActivity.this,
										"Please enter only whole numbers",
										Toast.LENGTH_LONG).show();
							}
						}
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private ScrollView getView(ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ScrollView layout = (ScrollView) inflater.inflate(
				R.layout.usercontact_chatconfigurations, parent, false);
		return layout;
	}

	private void setChatConfigs(ScrollView layout) {
		((EditText) layout.findViewById(R.id.gtalkUsername))
				.setText(userSetting.getGtalkCredential().getUsername());
		((EditText) layout.findViewById(R.id.gtalkPassword))
				.setText(userSetting.getGtalkCredential().getPassword());
		((EditText) layout.findViewById(R.id.facebookUsername))
				.setText(userSetting.getFacebookCredential().getUsername());
		((EditText) layout.findViewById(R.id.facebookPassword))
				.setText(userSetting.getFacebookCredential().getPassword());
	}

	public void setChatSettings(View view) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Chat Configurations");
		// get the layout for the chat configuration.
		final ScrollView layout = getView(null);
		Log.e("confs", userSetting.serialize());
		setChatConfigs(layout);
		builder.setView(layout);
		builder.setPositiveButton("Save Configurations",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						userSetting.getGtalkCredential().setUsername(
								((EditText) layout
										.findViewById(R.id.gtalkUsername))
										.getText().toString());
						userSetting.getGtalkCredential().setPassword(
								((EditText) layout
										.findViewById(R.id.gtalkPassword))
										.getText().toString());
						userSetting.getFacebookCredential().setUsername(
								((EditText) layout
										.findViewById(R.id.facebookUsername))
										.getText().toString());
						userSetting.getFacebookCredential().setPassword(
								((EditText) layout
										.findViewById(R.id.facebookPassword))
										.getText().toString());
						MimiProtectGeneralManager.updateUserSetting();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public void enableLocalCacheAndSync(boolean state) {
		userSetting.setAllowLocalCache(state);
		userSetting.setLockMimiProtectToCurrentPhone(state
				|| userSetting.isLockMimiProtectToCurrentPhone());
		setRelevantInformation();
		boolean updated = MimiProtectGeneralManager.updateUserSetting();
		final CheckBox cbLockMimiProtectToThisPhone = (CheckBox) findViewById(R.id.cbLockMimiProtectToThisPhone);
		final CheckBox cbEnableLocalCacheAndSync = (CheckBox) findViewById(R.id.cbEnableLocalCacheAndSync);
		state = (!updated && !state) || (updated && state);
		cbEnableLocalCacheAndSync.setChecked(state);
		cbLockMimiProtectToThisPhone.setChecked(state
				|| userSetting.isLockMimiProtectToCurrentPhone());
		updateTextView();
	}

	private void setRelevantInformation() {
		if (MimiProtectGeneralManager.getCurrentUser() != null) {
			userSetting.setImei(Settings.getCurrentDeviceId(this));
			userSetting.setUsername(MimiProtectGeneralManager.getCurrentUser()
					.getUserName());
			userSetting.setPassword(MimiProtectGeneralManager.getCurrentUser()
					.getPassword());
		}
	}

	public void handleAboutMimiprotect(View view) {
		PhonebookActivity.startGeneralActivity(this, "About mimi protect",
				AboutMimiprotectActivity.class, R.layout.usercontact_tabview);
	}
}
