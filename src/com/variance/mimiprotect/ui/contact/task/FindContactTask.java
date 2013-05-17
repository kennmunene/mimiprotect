package com.variance.mimiprotect.ui.contact.task;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.variance.mimiprotect.contacts.Contact;
import com.variance.mimiprotect.request.HttpRequestManager;
import com.variance.mimiprotect.response.HttpResponseData;
import com.variance.mimiprotect.response.HttpResponseStatus;
import com.variance.mimiprotect.ui.MimiProtectActivity.OnRequestComplete;
import com.variance.mimiprotect.ui.PersonalPhonebookActivity;
import com.variance.mimiprotect.util.DataParser;
import com.variance.mimiprotect.util.Settings;

/**
 * Retrieves a contact from the server, give the contact id.
 * 
 * @author marembo
 * 
 */
public class FindContactTask extends AsyncTask<String, Void, HttpResponseData> {
	private Context context;
	private OnRequestComplete<Contact> onRequestComplete;
	private boolean toastResult;

	public FindContactTask(Context context,
			OnRequestComplete<Contact> onRequestComplete) {
		super();
		this.context = context;
		this.onRequestComplete = onRequestComplete;
	}

	public FindContactTask(Context context,
			OnRequestComplete<Contact> onRequestComplete, boolean toastResult) {
		super();
		this.context = context;
		this.onRequestComplete = onRequestComplete;
		this.toastResult = toastResult;
	}

	@Override
	protected void onPreExecute() {
		PersonalPhonebookActivity.showProgress(
				"Please wait, loading contact...", context, this);
	}

	@Override
	protected void onPostExecute(HttpResponseData result) {
		PersonalPhonebookActivity.endProgress();
		onRequestComplete.requestComplete(getContact(result));
	}

	@Override
	protected HttpResponseData doInBackground(String... params) {
		return HttpRequestManager.doRequestWithResponseData(
				Settings.getFindContactUrl(),
				Settings.makeFindContactParameter(params[0]), context);
	}

	private Contact getContact(HttpResponseData result) {
		if (result == null) {
			if (toastResult) {
				Toast.makeText(context,
						"Sorry! There is no internet Connection.",
						Toast.LENGTH_LONG).show();
			}
		} else if (result.getResponseStatus() == HttpResponseStatus.SUCCESS) {
			String contactStr = result.getMessage();
			return DataParser.getPersonalContactDetail(contactStr);
		} else if (toastResult) {
			Toast.makeText(context, "An error has occurred: " + result,
					Toast.LENGTH_LONG).show();
		}
		return null;
	}

	/**
	 * This method blocks until it return a contact or null;
	 * 
	 * @param context
	 * @return
	 */
	public static Contact findContact(Context context, String contactId) {
		FindContactTask task = new FindContactTask(context, null);
		return task.getContact(task.doInBackground(contactId));
	}

	/**
	 * This method blocks until it return a contact or null;
	 * 
	 * @param context
	 * @return
	 */
	public static Contact findContact(Context context, String contactId,
			boolean toastResult) {
		FindContactTask task = new FindContactTask(context, null, toastResult);
		return task.getContact(task.doInBackground(contactId));
	}

}
