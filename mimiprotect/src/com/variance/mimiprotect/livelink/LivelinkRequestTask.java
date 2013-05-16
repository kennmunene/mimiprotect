package com.variance.mimiprotect.livelink;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.contacts.Contact;
import com.variance.mimiprotect.contacts.business.task.BusinessContacSelectFromPersonalContacttAdapter;
import com.variance.mimiprotect.contacts.task.HttpRequestTask;
import com.variance.mimiprotect.contacts.task.HttpRequestTaskListener;
import com.variance.mimiprotect.request.HttpRequestManager;
import com.variance.mimiprotect.response.HttpResponseData;
import com.variance.mimiprotect.response.HttpResponseStatus;
import com.variance.mimiprotect.ui.LiveLinkRequestsActivity;
import com.variance.mimiprotect.ui.PersonalPhonebookActivity;
import com.variance.mimiprotect.util.DataParser;
import com.variance.mimiprotect.util.SearchParameter;
import com.variance.mimiprotect.util.Settings;

public class LivelinkRequestTask extends
		AsyncTask<Void, Void, HttpResponseData> {
	private LiveLinkRequestsActivity liveLinkRequestsActivity;
	private SearchParameter searchParameter;
	private List<Contact> selectedContacts;
	private ListView view;
	private BusinessContacSelectFromPersonalContacttAdapter adapter;

	public LivelinkRequestTask(
			LiveLinkRequestsActivity liveLinkRequestsActivity,
			SearchParameter searchParameter, List<Contact> selectedContacts) {
		super();
		this.liveLinkRequestsActivity = liveLinkRequestsActivity;
		this.searchParameter = searchParameter;
		this.selectedContacts = selectedContacts;
		view = (ListView) liveLinkRequestsActivity
				.findViewById(R.id.usercontact_livelinkoption_View);
	}

	public void onContactedLoadedFormCache(List<Contact> contacts) {
		CheckBox selectAllContactsCb = (CheckBox) liveLinkRequestsActivity
				.findViewById(R.id.usercontact_livelinkoption_selectAllContactCb);
		adapter = new BusinessContacSelectFromPersonalContacttAdapter(
				liveLinkRequestsActivity,
				R.layout.businesscontact_selectfrompersonalcontact_singlecontactview,
				contacts, selectedContacts, selectAllContactsCb);
		if (view != null) {
			view.setAdapter(adapter);
		}
	}

	@Override
	protected void onPostExecute(HttpResponseData result) {
		try {
			if (result != null
					&& result.getResponseStatus() == HttpResponseStatus.SUCCESS) {
				final ArrayList<Contact> contacts = DataParser
						.getPersonalContacts(result.getMessage());
				CheckBox selectAllContactsCb = (CheckBox) liveLinkRequestsActivity
						.findViewById(R.id.usercontact_livelinkoption_selectAllContactCb);
				adapter = new BusinessContacSelectFromPersonalContacttAdapter(
						liveLinkRequestsActivity,
						R.layout.businesscontact_selectfrompersonalcontact_singlecontactview,
						contacts, selectedContacts, selectAllContactsCb);
				view.setAdapter(adapter);
			}
		} finally {
			PersonalPhonebookActivity.endProgress();
		}
	}

	public void selectAllContacts() {
		adapter.selectAllContacts();
	}

	public void deselectAllContacts() {
		adapter.deselectAllContacts();
	}

	@Override
	protected void onPreExecute() {
		PersonalPhonebookActivity.showProgress("Please wait....",
				liveLinkRequestsActivity, this);
	}

	@Override
	protected HttpResponseData doInBackground(Void... arg0) {
		return HttpRequestManager.doRequestWithResponseData(
				Settings.getSearchContactUrl(),
				Settings.makeLoadAllPersonalContactsParameter(searchParameter));
	}

	public void doLivelink() {
		if (selectedContacts.isEmpty()) {
			Toast.makeText(liveLinkRequestsActivity,
					"You must select at least one contact for livelinking",
					Toast.LENGTH_LONG).show();
			return;
		}
		HttpRequestTaskListener<Void, HttpResponseData> listener = new HttpRequestTaskListener<Void, HttpResponseData>() {

			public void onTaskStarted() {

			}

			public void onTaskCompleted(HttpResponseData result) {
				if (result != null) {
					Toast.makeText(liveLinkRequestsActivity, result.toString(),
							Toast.LENGTH_LONG).show();
					Log.i("Livelink result: ", result.toString());
				} else {
					Toast.makeText(liveLinkRequestsActivity,
							"Did not receive result from server",
							Toast.LENGTH_LONG).show();
				}
			}

			public HttpResponseData doTask(Void... params) {
				return HttpRequestManager
						.doRequestWithResponseData(
								Settings.getLivelinkUrl(),
								Settings.makeLivelinkRequestParameter(selectedContacts));
			}
		};
		new HttpRequestTask<Void, Void, HttpResponseData>(listener,
				"Please wait. livelinking...", liveLinkRequestsActivity)
				.execute(new Void[] {});
	}
}
