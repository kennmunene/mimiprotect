package com.variance.mimiprotect.contacts.task;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.request.HttpRequestManager;
import com.variance.mimiprotect.ui.GeneralTabActivity;
import com.variance.mimiprotect.ui.LiveLinkRequestsActivity;
import com.variance.mimiprotect.ui.LoginActivity;
import com.variance.mimiprotect.ui.PersonalPhonebookActivity;
import com.variance.mimiprotect.ui.PhonebookActivity;
import com.variance.mimiprotect.ui.dashboard.DashBoardActivity;
import com.variance.mimiprotect.util.MimiProtectGeneralManager;
import com.variance.mimiprotect.util.MimiProtectIntentConstants;
import com.variance.mimiprotect.util.Settings;

public class LoginTask extends AsyncTask<String, Void, String> {
	private String password;
	private Activity activity;
	private String userName;
	private boolean executeOnBackground;

	public LoginTask(String user, String password, Activity activity) {
		this.activity = activity;
		this.userName = user;
		this.password = password;
	}

	public LoginTask(String user, String password, Activity activity,
			boolean executeOnBackground) {
		this.activity = activity;
		this.userName = user;
		this.password = password;
		this.executeOnBackground = executeOnBackground;
	}

	@Override
	protected void onPreExecute() {
		Settings.setSessionInitializing();
		if (!executeOnBackground) {
			PersonalPhonebookActivity.showProgress("Signing in...", activity,
					this);
		}
	}

	@Override
	protected String doInBackground(String... url) {
		if (!Settings.isDebugging()) {
			try {
				return HttpRequestManager.doRequest(Settings.getSigninURL(),
						Settings.getLoginParameters(this.userName,
								this.password), true, activity);
			} catch (Exception e) {
				Log.e("log_tag", "Error converting result " + e.toString());
			}
		}
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		try {
			if (result == null) {
				return;
			}
			if (Settings.isDebugging()) {
				PhonebookActivity.startGeneralActivity(activity, "mimi",
						DashBoardActivity.class);
			} else if (result.equals("")
					|| result
							.equals(HttpRequestManager.NETWORK_CONNECTION_UNAVAILABLE)) {
				String codeValue = "Sorry! There is no internet Connection.";
				if (HttpRequestManager.getRequestCodeValue(result) != null) {
					codeValue = HttpRequestManager.getRequestCodeValue(result);
				}
				Toast.makeText(activity, codeValue, Toast.LENGTH_LONG).show();
			} else {
				if (result.startsWith("success")) {
					String sessionID = "";
					int start = result.indexOf("<id>") + "<id>".length();
					int end = result.indexOf("</id>");
					sessionID = result.substring(start, end);
					Settings.saveSessionID(activity, sessionID);
					startMimiProtect();
				} else {
					Toast.makeText(activity,
							"Access Denied. Invalid details. please try again",
							Toast.LENGTH_LONG).show();
				}
			}
		} finally {
			if (!executeOnBackground) {
				PersonalPhonebookActivity.endProgress();
			}
		}
	}

	private void startMimiProtect() {
		// initialize settings
		MimiProtectGeneralManager.initAll(activity);
		Intent intent = new Intent(activity, GeneralTabActivity.class);
		Intent callingintent = activity.getIntent();
		if (callingintent != null) {
			intent.putExtras(callingintent);
		}
		String className = callingintent
				.getStringExtra(MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_CLASS);
		String title = callingintent
				.getStringExtra(MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_TITLE);
		Log.i("MIMI_PROTECT_ACTIVITY_CLASS0", className + "");
		Log.i("MIMI_PROTECT_ACTIVITY_TITLE0", title + "");
		intent.putExtras(activity.getIntent());
		if (className == null
				|| className.equals(LoginActivity.class.getName())) {
			className = DashBoardActivity.class.getName();
			title = "Mimi Protect";
		}
		if (callingintent.getBooleanExtra(
				MimiProtectIntentConstants.ON_LIVELINK_NOTIFICATION_EXTRA,
				false)) {
			className = LiveLinkRequestsActivity.class.getName();
			title = "Livelink";
			intent.putExtra(
					MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_TABVIEW_LAYOUT,
					R.layout.usercontact_tabview);
		}
		if (title == null) {
			title = "Mimi Protect";
		}
		Log.i("MIMI_PROTECT_ACTIVITY_CLASS", className + "");
		Log.i("MIMI_PROTECT_ACTIVITY_TITLE", title);
		intent.putExtra(MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_CLASS,
				className);
		intent.putExtra(MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_TITLE,
				title);
		intent.putExtra(
				MimiProtectIntentConstants.MIMI_RPOTECT_SIGNED_UP_OPTION, true);
		activity.startActivity(intent);
		activity.finish(); // this is certainly start activity
	}
}
