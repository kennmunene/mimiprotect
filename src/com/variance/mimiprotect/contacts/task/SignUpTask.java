package com.variance.mimiprotect.contacts.task;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.variance.mimiprotect.c2dm.pack.C2DMSettings;
import com.variance.mimiprotect.request.HttpRequestManager;
import com.variance.mimiprotect.ui.GeneralTabActivity;
import com.variance.mimiprotect.ui.LoginActivity;
import com.variance.mimiprotect.ui.PostSignupWizardActivity;
import com.variance.mimiprotect.ui.PersonalPhonebookActivity;
import com.variance.mimiprotect.util.MimiProtectGeneralManager;
import com.variance.mimiprotect.util.MimiProtectIntentConstants;
import com.variance.mimiprotect.util.Settings;

public class SignUpTask extends AsyncTask<String, Void, String> {
	private InputStream is = null;
	private String user;
	private String password;
	private String cpassword;
	private String email;
	private Activity activity;

	public SignUpTask(String name, String password, String cpassword,
			String email, Activity activity) {
		super();
		this.password = password;
		this.cpassword = cpassword;
		this.email = email;
		this.user = name;
		this.activity = activity;
	}

	public SignUpTask(String user, String password, String cpassword,
			Activity activity) {
		super();
		this.user = user;
		this.password = password;
		this.cpassword = cpassword;
		this.activity = activity;
	}

	@Override
	protected void onPreExecute() {
		PersonalPhonebookActivity.showProgress("Signing up...", activity, this);
	}

	@Override
	protected String doInBackground(String... url) {
		String result;
		try {
			HttpResponse response = HttpRequestManager.doRequestWithResponse(
					Settings.getSignupURL(), Settings.makeSignupParameters(
							user, email, password, cpassword));
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			result = sb.toString();
			Log.i("log_tag", "Result: " + result);
		} catch (Exception e) {
			result = "";
			Log.e("log_tag", "Error converting result " + e.toString());
		}
		return result;
	}

	@Override
	protected void onPostExecute(String result) {
		try {
			if (result.equals("")) {
				Toast.makeText(activity,
						"Sorry! There is no internet Connection.",
						Toast.LENGTH_LONG).show();
			} else {
				if (result.startsWith("success")) {
					// we are successfully signed up.
					activity.finish();
					Intent intent = new Intent(activity,
							GeneralTabActivity.class);
					intent.putExtra(
							MimiProtectIntentConstants.SIGININ_DO_NOT_START_MIMIPROTECT,
							true);
					intent.putExtra(
							MimiProtectIntentConstants.SIGININ_USERNAME, user);
					intent.putExtra(
							MimiProtectIntentConstants.SIGININ_PASSWORD,
							password);
					String className = PostSignupWizardActivity.class.getName();
					if (!MimiProtectGeneralManager.hasDefaultAccessibility()) {
						className = LoginActivity.class.getName();
					}
					intent.putExtra(
							MimiProtectIntentConstants.MIMI_PROTECT_ACTIVITY_CLASS,
							className);
					activity.startActivity(intent);
					if (MimiProtectGeneralManager.hasDefaultAccessibility()) {
						PersonalPhonebookActivity.setShortCut(activity,
								"mimi protect");
					}
				} else {
					Toast.makeText(activity,
							"You are unable to signup: " + result,
							Toast.LENGTH_LONG).show();
				}
			}
		} finally {
			doGCMRegistration();
			PersonalPhonebookActivity.endProgress();
		}
	}

	private void doGCMRegistration() {
		try {
			GCMRegistrar.checkDevice(activity);
			GCMRegistrar.checkManifest(activity);
			final String regId = GCMRegistrar.getRegistrationId(activity);
			if (regId.equals("")) {
				GCMRegistrar.register(activity, C2DMSettings.SENDER_ID);
			} else {
				Log.v("GCM Registration", "Already registered");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
