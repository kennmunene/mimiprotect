package com.variance.mimiprotect.c2dm.pack;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.variance.mimiprotect.request.HttpRequestManager;
import com.variance.mimiprotect.util.Settings;

public class RegisterServerTask extends AsyncTask<Void, Void, Void> {
	private String url = Settings.getLiveLinkRegistrationURL();
	private Map<String, String> nameValue = null;
	private Context context;

	public RegisterServerTask(Context context) {
		this.context = context;
	}

	@Override
	protected Void doInBackground(Void... params) {
		String response = "";
		nameValue = new HashMap<String, String>();
		nameValue.put(C2DMSettings.C2DM_GCM_KEY,
				C2DMSettings.getSavedRegistrationID(context));
		nameValue.put("sessionID", Settings.getSessionID());
		try {
			response = HttpRequestManager.doRequest(url, nameValue);
		} catch (Exception e) {
			response = e.toString();
		}
		Log.d("gcm task", response);

		return null;
	}

}
