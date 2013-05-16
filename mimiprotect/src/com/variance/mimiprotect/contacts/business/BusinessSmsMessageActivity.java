package com.variance.mimiprotect.contacts.business;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.contacts.Contact;
import com.variance.mimiprotect.contacts.business.settings.BusinessContactConstants;
import com.variance.mimiprotect.contacts.business.task.BusinessSmsContactSearchTask;
import com.variance.mimiprotect.request.HttpRequestManager;
import com.variance.mimiprotect.response.HttpResponseData;
import com.variance.mimiprotect.response.HttpResponseStatus;
import com.variance.mimiprotect.ui.PersonalPhonebookActivity;
import com.variance.mimiprotect.ui.PhonebookActivity;
import com.variance.mimiprotect.ui.PhonebookType;
import com.variance.mimiprotect.util.SearchParameter;
import com.variance.mimiprotect.util.Settings;
import com.variance.mimiprotect.util.Utils;

public class BusinessSmsMessageActivity extends PhonebookActivity {
	private BusinessSmsContactSearchTask businessSmsContactSearchTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.businesscontact_sendmessage);
		initParameters();
	}

	public void handleSendSmsMessage(View view) {
		List<Contact> selectedContacts = businessSmsContactSearchTask != null ? businessSmsContactSearchTask
				.getSelectedContacts() : null;
		if (selectedContacts != null && !selectedContacts.isEmpty()) {
			// get the message.
			String message = ((EditText) findViewById(R.id.smsMessage))
					.getText().toString();
			if (!Utils.isNullStringOrEmpty(message)) {
				List<String> contactIds = new ArrayList<String>();
				for (Contact c : selectedContacts) {
					contactIds.add(c.getId());
				}
				final SmsMessage sms = new SmsMessage(contactIds, message);
				Log.i("sms-message", sms.toString());
				new AsyncTask<Void, Void, HttpResponseData>() {
					@Override
					protected void onPreExecute() {
						PersonalPhonebookActivity.showProgress("Sending...",
								BusinessSmsMessageActivity.this, this);
					}

					@Override
					protected void onPostExecute(HttpResponseData result) {
						PersonalPhonebookActivity.endProgress();
						String message = result == null ? "Sorry error occurred sending message!"
								: result.toString();
						Toast.makeText(BusinessSmsMessageActivity.this,
								message, Toast.LENGTH_LONG).show();
						if (result != null
								&& result.getResponseStatus() == HttpResponseStatus.SUCCESS) {
							((EditText) findViewById(R.id.smsMessage))
									.setText("");
						}
					}

					@Override
					protected HttpResponseData doInBackground(Void... params) {
						return HttpRequestManager.doRequestWithResponseData(
								Settings.getBusinessContactUrl(),
								Settings.makeSendBusinessBulkSms(sms));
					}

				}.execute();
			} else {
				Toast.makeText(this, "Please enter message to send!",
						Toast.LENGTH_LONG).show();
			}
		} else {
			Toast.makeText(this,
					"You must select contacts to send message to!",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected boolean doLoadFromCache() {
		// Unless there is no cache result, always load from cache.
		return true;
	}

	private void initParameters() {
		searchParameter = new SearchParameter(
				BusinessContactConstants.BUSINESS_DEFAULT_LOADING_SEARCH_TERM,
				0, -1);
		search();
	}

	@Override
	public void search() {
		super.doSearch();
		if (!isLoadedFromCache()) {
			this.runOnUiThread(new Runnable() {
				public void run() {
					businessSmsContactSearchTask = new BusinessSmsContactSearchTask(
							BusinessSmsMessageActivity.this);
					businessSmsContactSearchTask
							.execute(new SearchParameter[] { searchParameter });
				}
			});
		}
	}

	@Override
	protected void onContactLoadedFromCache(List<Contact> contacts) {
		businessSmsContactSearchTask = new BusinessSmsContactSearchTask(this);
		businessSmsContactSearchTask.loadViewFromCache(contacts);
	}

	@Override
	public PhonebookType getType() {
		return PhonebookType.OFFICE;
	}
}
