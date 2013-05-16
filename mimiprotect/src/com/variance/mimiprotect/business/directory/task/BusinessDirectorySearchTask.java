package com.variance.mimiprotect.business.directory.task;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.contacts.Contact;
import com.variance.mimiprotect.contacts.managers.ContactViewManager;
import com.variance.mimiprotect.request.HttpRequestManager;
import com.variance.mimiprotect.ui.PersonalPhonebookActivity;
import com.variance.mimiprotect.util.DataParser;
import com.variance.mimiprotect.util.SearchParameter;
import com.variance.mimiprotect.util.Settings;

public class BusinessDirectorySearchTask extends
		AsyncTask<SearchParameter, String, String> {
	private Context context;

	public BusinessDirectorySearchTask(Context context) {
		super();
		this.context = context;
	}

	@Override
	protected void onPostExecute(String result) {
		try {
			if (result == null || result.equals("")) {
				Toast.makeText(context,
						"Sorry! There is no internet Connection.",
						Toast.LENGTH_LONG).show();
				return;
			}
			// We start a new activity here for loading the contacts view
			loadCustomView(result);
		} finally {
			PersonalPhonebookActivity.endProgress();
		}
	}

	@Override
	protected void onPreExecute() {
		PersonalPhonebookActivity.showProgress("Searching...", context, this);
	}

	@Override
	protected String doInBackground(SearchParameter... arg0) {
		try {
			return HttpRequestManager.doRequest(Settings.getSearchContactUrl(),
					Settings.makeBusinessDirectorySearchParameter(arg0[0]),
					context);
		} catch (Exception e) {
			Log.e("log_tag", "Error converting result " + e.toString());
			return "";
		}
	}

	private void loadCustomView(String result) {
		Log.i("Search Result", result);
		if (result != null && !"".equals(result)) {
			ArrayList<Contact> businessContacts = DataParser
					.getCorporateContacts(result);
			Log.i("Search Result", businessContacts.toString());
			ContactViewManager cva = new ContactViewManager(businessContacts,
					businessContacts, (Activity) context);
			cva.initialize(R.id.businessDirectoryView, false, true, false);
		}
	}

}
