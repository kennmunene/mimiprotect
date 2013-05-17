package com.variance.mimiprotect.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.contacts.task.LocationUpdateTask;
import com.variance.mimiprotect.contacts.task.LoginTask;
import com.variance.mimiprotect.util.MimiProtectGeneralManager;
import com.variance.mimiprotect.util.MimiProtectIntentConstants;
import com.variance.mimiprotect.util.Settings;

public class LoginActivity extends Activity {
	private EditText txtUsername;
	private EditText txtPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initLogin();
		initCurrentLocations();
		// initialize that we are set
		Settings.setPreference(this,
				MimiProtectIntentConstants.ON_MIMIPROTECT_FINISH_EXTRA,
				false + "");
		// set the fact that we are away from sign up and hence don't need the
		// landing page again.
		Settings.setSignedUp(this);
		Log.i("Starting Login Activity: ", "onCreate Login Activity");
	}

	private void initCurrentLocations() {
		LocationUpdateTask lut = new LocationUpdateTask(LoginActivity.this);
		lut.execute(new String[] {});
	}

	private void initLogin() {
		setContentView(R.layout.mimi_connect_loginscreen);
		txtUsername = (EditText) findViewById(R.id.txtUser);
		txtPassword = (EditText) findViewById(R.id.txtPassword);
		showAutomaticLogin();
	}

	public void myLoginhHandler(View view) {
		switch (view.getId()) {
		case R.id.btnSignIn:
			validateUSer();
			break;
		case R.id.btnGoToSignup:
			signUp();
			break;
		case R.id.fogotPassword:
			doCheckPassword();
			break;
		}
	}

	private void showAutomaticLogin() {
		if (MimiProtectGeneralManager.hasAccessibility()) {
			boolean autoLogin = MimiProtectGeneralManager
					.getUserSettingOverride().isEnableAutomaticLogin();
			((CheckBox) findViewById(R.id.automaticallyLogin))
					.setChecked(autoLogin);
		}
	}

	public void automaticallyLoginHandler(View view) {
		switch (view.getId()) {
		case R.id.automaticallyLogin:
			boolean isChecked = ((CheckBox) view).isChecked();
			if (MimiProtectGeneralManager.hasAccessibility()) {
				MimiProtectGeneralManager.getUserSettingOverride()
						.setEnableAutomaticLogin(isChecked);
				MimiProtectGeneralManager.updateUserSetting();
			}
			break;
		}
	}

	public void doCheckPassword() {
		Intent webView = new Intent(Intent.ACTION_VIEW);
		webView.setData(Uri.parse("http://www.mimiprotect.com"));
		startActivity(webView);
	}

	private void validateUSer() {
		String name = txtUsername.getText().toString().trim();
		String password = txtPassword.getText().toString().trim();
		validateUSer(name, password, this);
	}

	public void validateUSer(String name, String password, Activity context) {
		LoginTask task = new LoginTask(name, password, context);
		task.execute(new String[] { "" });
	}

	private void signUp() {
		PhonebookActivity.startGeneralActivity(this, "mimi",
				SignupActivity.class, R.layout.usercontact_whitetitled_tabview);

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return false;
	}

	@Override
	public void finish() {
		// we release the preferences that we are done
		Settings.setPreference(this,
				MimiProtectIntentConstants.ON_MIMIPROTECT_FINISH_EXTRA, "true");
		super.finish();
	}

}
