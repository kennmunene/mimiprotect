package com.variance.mimiprotect.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.util.MimiProtectGeneralManager;

public class MimiProtectLandingPageActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mimi_connect_landingpage);
	}

	public void toSignin(View view) {
		PhonebookActivity.startGeneralActivity(this, "mimi",
				LoginActivity.class, R.layout.usercontact_whitetitled_tabview);
	}

	public void toSignup(View view) {
		PhonebookActivity.startGeneralActivity(this, "mimi",
				SignupActivity.class, R.layout.usercontact_whitetitled_tabview);
	}

	public void automaticallyLoginHandler(View view) {
		switch (view.getId()) {
		case R.id.automaticallyLogin:
			boolean isChecked = ((CheckBox) view).isChecked();
			if (MimiProtectGeneralManager.hasCurrentPhoneLock()) {
				MimiProtectGeneralManager.getUserSetting()
						.setEnableAutomaticLogin(isChecked);
				MimiProtectGeneralManager.updateUserSetting();
			}
			break;
		}
	}

	public void doCheckPassword(View view) {
		Intent webView = new Intent(Intent.ACTION_VIEW);
		webView.setData(Uri.parse("http://www.mimiprotect.com"));
		startActivity(webView);
	}
}
