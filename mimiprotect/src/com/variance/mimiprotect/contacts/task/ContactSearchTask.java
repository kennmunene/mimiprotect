package com.variance.mimiprotect.contacts.task;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.contacts.Contact;
import com.variance.mimiprotect.contacts.managers.ContactViewManager;
import com.variance.mimiprotect.request.HttpRequestManager;
import com.variance.mimiprotect.response.HttpResponseData;
import com.variance.mimiprotect.ui.PersonalPhonebookActivity;
import com.variance.mimiprotect.util.DataParser;
import com.variance.mimiprotect.util.SearchParameter;
import com.variance.mimiprotect.util.Settings;

public class ContactSearchTask extends
		AsyncTask<SearchParameter, String, String> {
	private Context context;
	private ContactViewManager contactViewManager;
	private boolean backgroundExecute;

	public ContactSearchTask(Context context) {
		super();
		this.context = context;
		this.backgroundExecute = false;
	}

	public ContactSearchTask(Context context, boolean backgroundExecute) {
		super();
		this.context = context;
		this.backgroundExecute = backgroundExecute;
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
			if (!backgroundExecute) {
				PersonalPhonebookActivity.endProgress();
			}
		}
	}

	@Override
	protected void onPreExecute() {
		if (!backgroundExecute) {
			PersonalPhonebookActivity.showProgress("Searching...", context,
					this);
		}
	}

	@Override
	protected String doInBackground(SearchParameter... arg0) {
		try {
			Log.e("ContactSearchTask:", "called on cache load");
			return HttpRequestManager.doRequest(Settings.getSearchContactUrl(),
					Settings.makeAESSearchParameter(arg0[0]), context);
		} catch (Exception e) {
			Log.e("log_tag", "Error converting result " + e.toString());
			return "";
		}
	}

	private void loadCustomView(String result) {
		Log.i("Search Result", result + "");
		if (result != null && !"".equals(result)) {
			ArrayList<Contact> businessContacts = DataParser
					.getCorporateContacts(result);
			ArrayList<Contact> personalContacts = DataParser
					.getPersonalContacts(result);
			contactViewManager = new ContactViewManager(businessContacts,
					personalContacts, (Activity) context);
			boolean demarcate = false;
			if (context instanceof PersonalPhonebookActivity) {
				demarcate = !((PersonalPhonebookActivity) context)
						.isDefaultSearch();
			}
			contactViewManager.initialize(R.id.personalContactView, true,
					false, demarcate);
		}
	}

	public void loadCachedView(List<Contact> contacts) {
		ArrayList<Contact> cachedContacts = new ArrayList<Contact>(contacts);
		contactViewManager = new ContactViewManager(cachedContacts,
				cachedContacts, (Activity) context);
		boolean demarcate = false;
		contactViewManager.initialize(R.id.personalContactView, true, false,
				demarcate);
	}

	public void responseReceived(HttpResponseData httpResponseData) {
		String result = httpResponseData.getMessage();
		if (result != null && !"".equals(result.trim())) {
			ArrayList<Contact> personalContacts = DataParser
					.getPersonalContacts(result);
			if (contactViewManager == null) {
				contactViewManager = new ContactViewManager(personalContacts,
						personalContacts, (Activity) context);
				boolean demarcate = false;
				if (context instanceof PersonalPhonebookActivity) {
					demarcate = !((PersonalPhonebookActivity) context)
							.isDefaultSearch();
				}
				contactViewManager.initialize(R.id.personalContactView, true,
						false, demarcate);
			} else {
				contactViewManager.onNextContactsReceived(personalContacts);
			}
		}
	}
}
